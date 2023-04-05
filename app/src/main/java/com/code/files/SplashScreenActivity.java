package com.code.files;

import static com.facebook.ads.internal.api.AdViewConstructorParams.CONTEXT;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.code.files.database.DatabaseHelper;
import com.code.files.database.config.ConfigViewModel;
import com.code.files.network.apis.ConfigurationApi;
import com.code.files.network.model.config.ApkUpdateInfo;
import com.code.files.network.model.config.Configuration;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.oxoo.spagreen.BuildConfig;
import com.oxoo.spagreen.R;
import com.code.files.network.RetrofitClient;
import com.code.files.utils.HelperUtils;
import com.code.files.utils.PreferenceUtils;
import com.code.files.utils.ApiResources;
import com.code.files.utils.Constants;
import com.code.files.utils.ToastMsg;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "SplashScreen";
    private final int PERMISSION_REQUEST_CODE = 100;
    private int SPLASH_TIME = 1500;
    private Thread timer;
    private DatabaseHelper db;
    private boolean isRestricted = false;
    private boolean isUpdate = false;
    private boolean vpnStatus = false;
    private HelperUtils helperUtils;
    private ConfigViewModel configViewModel;

    @Override
    protected void onStart() {
        super.onStart();
        //check any restricted app is available or not
       /* ApplicationInfo restrictedApp = helperUtils.getRestrictApp();
        if (restrictedApp != null){
            boolean isOpenInBackground = helperUtils.isForeground(restrictedApp.packageName);
            if (isOpenInBackground){
                Log.e(TAG, restrictedApp.loadLabel(this.getPackageManager()).toString() + ", is open in background.");
            }else {
                Log.e(TAG, "No restricted app is running in background.");
            }
        }else {
            Log.e(TAG, "No restricted app installed!!");
        }*/


        //check VPN connection is set or not
        vpnStatus = new HelperUtils(SplashScreenActivity.this).isVpnConnectionAvailable();

        requestPermission();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);
        configViewModel = new ViewModelProvider(SplashScreenActivity.this).get(ConfigViewModel.class);

        db = new DatabaseHelper(SplashScreenActivity.this);
        helperUtils = new HelperUtils(SplashScreenActivity.this);
        vpnStatus = new HelperUtils(SplashScreenActivity.this).isVpnConnectionAvailable();

        //print keyHash for facebook login
        // createKeyHash(SplashScreenActivity.this, BuildConfig.APPLICATION_ID);

        timer = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_TIME);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    if (PreferenceUtils.isLoggedIn(SplashScreenActivity.this)) {
                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();
                    } else {

                        if (isLoginMandatory()) {
                            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                        }
                    }

                }
            }
        };
    }

    private void requestPermission() {
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // You can use the API that requires the permission.
                getConfigurationData();
            }else {
                //no need to grant permission
                getConfigurationData();
            }
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            getConfigurationData();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }


    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your
            // app.
            getConfigurationData();
        } else {
            // Explain to the user that the feature is unavailable because the
            // feature requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
            new ToastMsg(SplashScreenActivity.this).toastIconError("Unable to open app without permission");
            //close app
            //System.exit(0);
        }
    });

    public boolean isLoginMandatory() {
        return PreferenceUtils.isMandatoryLogin(configViewModel);
    }

    public void getConfigurationData() {
        if (!vpnStatus) {
            Retrofit retrofit = RetrofitClient.getRetrofitInstance();
            ConfigurationApi api = retrofit.create(ConfigurationApi.class);
            Call<Configuration> call = api.getConfigurationData(AppConfig.API_KEY);
            call.enqueue(new Callback<Configuration>() {
                @Override

                public void onResponse(Call<Configuration> call, Response<Configuration> response) {
                    Log.e(TAG, "onResponse: " + response.code());
                    if (response.code() == 200) {
                        Configuration configuration = response.body();
                        if (configuration != null) {
                            configuration.setId(1);

                            ApiResources.CURRENCY = configuration.getPaymentConfig().getCurrency();
                            ApiResources.PAYPAL_CLIENT_ID = configuration.getPaymentConfig().getPaypalClientId();
                            ApiResources.EXCHSNGE_RATE = configuration.getPaymentConfig().getExchangeRate();
                            ApiResources.RAZORPAY_EXCHANGE_RATE = configuration.getPaymentConfig().getRazorpayExchangeRate();
                            //save genre, country and tv category list to constants
                            Constants.genreList = configuration.getGenre();
                            Constants.countryList = configuration.getCountry();
                            Constants.tvCategoryList = configuration.getTvCategory();

                            db.deleteAllDownloadData();
                            configViewModel.insert(configuration);

                            //apk update check
                            if (isNeedUpdate(configuration.getApkUpdateInfo().getVersionCode())) {
                                showAppUpdateDialog(configuration.getApkUpdateInfo());
                                return;
                            }

                            //Configuration data = configViewModel.getConfigData(SplashScreenActivity.this, configViewModel);
                            timer.start();

                        } else {
                            showErrorDialog(getString(R.string.error_toast), getString(R.string.failed_to_communicate));
                        }
                    } else {
                        showErrorDialog(getString(R.string.error_toast), getString(R.string.failed_to_communicate));
                    }
                }

                @Override
                public void onFailure(Call<Configuration> call, Throwable t) {
                    Log.e(TAG, t.getLocalizedMessage());
                    showErrorDialog(getString(R.string.error_toast), getString(R.string.failed_to_communicate));
                }
            });
        } else {
            helperUtils.showWarningDialog(SplashScreenActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
        }
    }

    private void showAppUpdateDialog(final ApkUpdateInfo info) {
        new MaterialAlertDialogBuilder(this).setTitle("New version: " + info.getVersionName()).setMessage(info.getWhatsNew()).setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //update clicked
                dialog.dismiss();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.getApkUrl()));
                startActivity(browserIntent);
                finish();
            }
        }).setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //exit clicked
                if (info.isSkipable()) {
                    if (configViewModel.getConfigData() != null) {
                        timer.start();
                    } else {
                        new ToastMsg(SplashScreenActivity.this).toastIconError(getString(R.string.error_toast));
                        finish();
                    }
                } else {
                    System.exit(0);
                }
                dialog.dismiss();
            }
        }).setCancelable(false).show();
    }

    private void showErrorDialog(String title, String message) {
        new MaterialAlertDialogBuilder(this).setTitle(title).setCancelable(false).setMessage(message).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
                finish();
            }
        }).show();
    }

    private boolean isNeedUpdate(String versionCode) {
        return Integer.parseInt(versionCode) > BuildConfig.VERSION_CODE;
    }

    // ------------------ checking storage permission ------------
    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            }
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                Log.v(TAG, "Permission is granted");
                return true;

            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //resume tasks needing this permission
            getConfigurationData();
        }else {
            new ToastMsg(this).toastIconError("Accept storage permission to continue");
        }
    }

    public static void createKeyHash(Activity activity, String yourPackage) {
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(yourPackage, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(SplashScreenActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
        }
    }
}
