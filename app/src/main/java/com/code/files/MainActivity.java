package com.code.files;

import android.Manifest;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.code.files.adapters.NavigationAdapter;
import com.code.files.database.DatabaseHelper;
import com.code.files.database.config.ConfigViewModel;
import com.code.files.fragments.LiveTvFragment;
import com.code.files.fragments.MoviesFragment;
import com.code.files.fragments.TvSeriesFragment;
import com.code.files.models.GetCommentsModel;
import com.code.files.models.HomeContent;
import com.code.files.models.NavigationModel;
import com.code.files.models.PostCommentModel;
import com.code.files.models.StatusModel;
import com.code.files.nav_fragments.CountryFragment;
import com.code.files.nav_fragments.FavoriteFragment;
import com.code.files.nav_fragments.GenreFragment;
import com.code.files.nav_fragments.MainHomeFragment;
import com.code.files.network.RetrofitClient;
import com.code.files.network.apis.CommentApi;
import com.code.files.network.model.config.Configuration;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.onesignal.OneSignal;
import com.oxoo.spagreen.R;
import com.code.files.utils.Constants;
import com.code.files.utils.HelperUtils;
import com.code.files.utils.PreferenceUtils;
import com.code.files.utils.RtlUtils;
import com.code.files.utils.SpacingItemDecoration;
import com.code.files.utils.Tools;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Serializable {
    private static final String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private LinearLayout navHeaderLayout;
    private boolean st = false;
    private   StatusModel barstatus = new StatusModel();

    public   MutableLiveData<StatusModel> bs = new MutableLiveData<>();
    public boolean movie =true;
    public boolean series = true;
    public boolean login =true;
    private RecyclerView recyclerView;
    private NavigationAdapter mAdapter;
    private List<NavigationModel> list = new ArrayList<>();
    private NavigationView navigationView;
    private String[] navItemImage;

    private String[] navItemName2;
    private String[] navItemImage2;
    private boolean status = false;
    private int[] Nav_position = new int[3];

    private FirebaseAnalytics mFirebaseAnalytics;
    public boolean isDark;
    private String navMenuStyle;

    private Switch themeSwitch;
    private final int PERMISSION_REQUEST_CODE = 100;
    private String searchType;
    private boolean[] selectedtype = new boolean[3]; // 0 for movie, 1 for series, 2 for live tv....
    private DatabaseHelper db;
    private boolean vpnStatus;
    private HelperUtils helperUtils;
    ConsentForm form;
    private ConfigViewModel configViewModel;
    private Configuration configuration;


    private boolean isNotTV() {
        UiModeManager uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        return uiModeManager.getCurrentModeType() != android.content.res.Configuration.UI_MODE_TYPE_TELEVISION;
    }

    private boolean isCastApiAvailable() {
        return isNotTV() && GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RtlUtils.setScreenDirection(this);
        db = new DatabaseHelper(MainActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);
        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //onesignal permission
        OneSignal.promptForPushNotifications();

        MobileAds.initialize(this, initializationStatus -> {
            Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
            for (String adapterClass : statusMap.keySet()) {
                AdapterStatus status = statusMap.get(adapterClass);
                Log.d("MyApp", String.format(
                        "Adapter name: %s, Description: %s, Latency: %d",
                        adapterClass, status.getDescription(), status.getLatency()));
            }
        });
        //check vpn connection
        helperUtils = new HelperUtils(MainActivity.this);
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(MainActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
            return;
        }
        // To resolve cast button visibility problem. Check Cast State when app is open.
        if (isCastApiAvailable()) {
            try {
                CastContext castContext = CastContext.getSharedInstance(this);
                castContext.getCastState();
            } catch (Exception e) {
                Log.e(TAG, "onCreate: cast is not available: " + e.getLocalizedMessage());
            }
        }


        configViewModel = new ViewModelProvider(MainActivity.this).get(ConfigViewModel.class);
        configuration = configViewModel.getConfigData();

        navMenuStyle = configuration.getAppConfig().getMenu();

        //---analytics-----------
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "main_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        if (sharedPreferences.getBoolean("firstTime", true)) {
            showTermServicesDialog();
        }

        //init gdpr
        initGDPR();


        //update subscription
        if (PreferenceUtils.isLoggedIn(MainActivity.this)) {
            PreferenceUtils.updateSubscriptionStatus(MainActivity.this);
        }

        // checking storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkStoragePermission()) {
                createDownloadDir();
            } else {
                requestPermission();
            }
        } else {
            createDownloadDir();
        }

        //----init---------------------------
        navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navHeaderLayout = findViewById(R.id.nav_head_layout);
        themeSwitch = findViewById(R.id.theme_switch);
        themeSwitch.setChecked(isDark);


        //----navDrawer------------------------
        //toolbar = findViewById(R.id.toolbar);
        if (!isDark) {
            //toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            navHeaderLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            navHeaderLayout.setBackgroundColor(getResources().getColor(R.color.nav_head_bg));
            navigationView.setBackgroundColor(getResources().getColor(R.color.black_window));
        }

        navigationView.setNavigationItemSelectedListener(this);

        //------fetch api status ---------



        //----fetch array------------
        String[] navItemName = getResources().getStringArray(R.array.nav_item_name);

        navItemImage = getResources().getStringArray(R.array.nav_item_image);
        navItemImage2 = getResources().getStringArray(R.array.nav_item_image_2);
        navItemName2 = getResources().getStringArray(R.array.nav_item_name_2);


        //----navigation view items---------------------
        recyclerView = findViewById(R.id.recyclerView);
        if (navMenuStyle == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

        } else if (navMenuStyle.equals("grid")) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 15), true));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        }
        recyclerView.setHasFixedSize(true);

        status = PreferenceUtils.isLoggedIn(this);
        //Nav_position

        Navbar_status();

        if (status) {
            PreferenceUtils.updateSubscriptionStatus(MainActivity.this);
            for (int i = 0; i < navItemName.length; i++) {
               //Navbar_status(i,true);
                //if(!Nav_position_check2(i,true)) {
                    NavigationModel models = new NavigationModel(navItemImage[i], navItemName[i]);
                    list.add(models);
               // }
            }
        } else {
            for (int i = 0; i < navItemName2.length; i++) {
               // Navbar_status(i,false);
               // if(!Nav_position_check2(i,false)) {
                    NavigationModel models = new NavigationModel(navItemImage2[i], navItemName2[i]);
                    list.add(models);
               // }
            }
        }

        //set data and list adapter
        mAdapter = new NavigationAdapter(this, list, navMenuStyle);
        recyclerView.setAdapter(mAdapter);

        final NavigationAdapter.OriginalViewHolder[] viewHolder = {null};

        mAdapter.setOnItemClickListener(new NavigationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, NavigationModel obj, int position, NavigationAdapter.OriginalViewHolder holder) {

                //----------------------action for click items nav---------------------

                if (position == 0) {
                    loadFragment(new MainHomeFragment());
                } else if (position == 1) {
                    loadFragment(new MoviesFragment());
                } else if (position == 2) {
                    loadFragment(new TvSeriesFragment());
                } else if (position == 3) {
                    loadFragment(new LiveTvFragment());
                } else if (position == 4) {
                    loadFragment(new GenreFragment());
                } else if (position == 5) {
                    loadFragment(new CountryFragment());
                } else {
                    if (status) {

                        if (position == 6) {
                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        } else if (position == 7) {
                            loadFragment(new FavoriteFragment());
                        } else if (position == 8) {
                            Intent intent = new Intent(MainActivity.this, SubscriptionActivity.class);
                            startActivity(intent);
                        } else if (position == 9) {
                            Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                            startActivity(intent);
                        } else if (position == 10) {
                            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(intent);
                        } else if (position == 11) {

                            new MaterialAlertDialogBuilder(MainActivity.this)
                                    .setMessage("Are you sure to logout ?")
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            if (user != null) {
                                                FirebaseAuth.getInstance().signOut();
                                            }

                                            SharedPreferences.Editor editor = getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
                                            editor.putBoolean(Constants.USER_LOGIN_STATUS, false);
                                            editor.apply();
                                            editor.commit();

                                            DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                                            databaseHelper.deleteUserData();

                                            PreferenceUtils.clearSubscriptionSavedData(MainActivity.this);

                                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).create().show();
                        }

                    } else {
                        if (position == 6) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else if (position == 7) {
                            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(intent);
                        }

                    }

                }


                //----behaviour of bg nav items-----------------
                if (!obj.getTitle().equals("Settings") && !obj.getTitle().equals("Login") && !obj.getTitle().equals("Sign Out")) {

                    if (isDark) {
                        mAdapter.chanColor(viewHolder[0], position, R.color.nav_bg);
                    } else {
                        mAdapter.chanColor(viewHolder[0], position, R.color.white);
                    }


                    if (navMenuStyle.equals("grid")) {
                        holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        holder.name.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        holder.selectedLayout.setBackground(getResources().getDrawable(R.drawable.round_grey_transparent));
                        holder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }

                    viewHolder[0] = holder;
                }


                mDrawerLayout.closeDrawers();
            }
        });

        //----external method call--------------
        loadFragment(new MainHomeFragment());

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
                    editor.putBoolean("dark", true);
                    editor.apply();

                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
                    editor.putBoolean("dark", false);
                    editor.apply();
                }

                mDrawerLayout.closeDrawers();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private  boolean Nav_position_check(int position,boolean nav) {
        //    String[] navItemName = getResources().getStringArray(R.array.nav_item_name);
        //    navItemImage = getResources().getStringArray(R.array.nav_item_image);
        //    navItemImage2 = getResources().getStringArray(R.array.nav_item_image_2);
        //   navItemName2 = getResources().getStringArray(R.array.nav_item_name_2);
        if(position==1){
            if(barstatus.getmovieStatus()){
                return true;
            }else{
            return false;}
        }
        else if(position==2){
            if(barstatus.getseriesStatus()){
                return true;
            }
            else{
                return false;}
        }
        else if(position==6 && nav == false){
            if(barstatus.getloginStatus()){
                return true;
            }
            else{
                return false;}
        }
        else{
            return  true;
        }
    }
    public   boolean Nav_position_check2(int position,boolean nav) {
        for (int j = 0; j < Nav_position.length; j++) {
            if (Nav_position[j] == position) {
                return  true;
            }
        }
        return  false;
    }
    public void set_nav(StatusModel status){
        barstatus = status;
        if(status.getmovieStatus() && status.getloginStatus() && status.getseriesStatus()){
            Nav_position= new int[]{1, 2, 6};
        }else if(status.getseriesStatus() && status.getmovieStatus()){
            Nav_position = new int[]{1,2};
        } else if(status.getloginStatus() && status.getmovieStatus()){
            Nav_position=new int[] {6,1};
        } else if (status.getloginStatus() && status.getseriesStatus()) {
            Nav_position = new int[]{6,2};
        }
        else if(status.getseriesStatus()){
            Nav_position = new int[]{2};
        } else if( status.getmovieStatus()){
            Nav_position=new int[] {1};
        } else if (status.getloginStatus()) {
            Nav_position = new int[]{6};
        }
    }

    public  void Navbar_status(){

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        CommentApi api = retrofit.create(CommentApi.class);
        Call<StatusModel> call = api.getAllStatus(AppConfig.API_KEY);
        MutableLiveData<StatusModel> ret = new MutableLiveData<>();
        call.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, retrofit2.Response<StatusModel> response) {
                if (response.code() == 200) {
                    //barstatus = response.body();
                    StatusModel n = response.body();
//                    barstatus = n;
                    //bs.clear();
                    ret.postValue(n);
                movie = n.getmovieStatus();
                series = n.getseriesStatus();
                login = n.getloginStatus();
                    //bs.add(n);
                    //set_nav(n);

                }
            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable t) {

            }
        });

//        new AlertDialog.Builder(MainActivity.this).setMessage("Do you want to exit ? M:"+bs.getValue().getmovieStatus())
//                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        dialog.dismiss();
//                        finish();
//                        System.exit(0);
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                }).create().show();
    }
    private void initGDPR() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
        String[] publisherIds = {getResources().getString(R.string.admob_publisher_id)};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d(TAG, "onConsentInfoUpdated");
                switch (consentStatus) {
                    case PERSONALIZED:
                        Log.d(TAG, "onConsentInfoUpdated: PERSONALIZED");
                        ConsentInformation.getInstance(MainActivity.this).setConsentStatus(ConsentStatus.PERSONALIZED);
                        break;
                    case NON_PERSONALIZED:
                        Log.d(TAG, "NON_PERSONALIZED");
                        ConsentInformation.getInstance(MainActivity.this).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                        break;
                    case UNKNOWN:
                        Log.d(TAG, "UNKNOWN");
                        if
                        (ConsentInformation.getInstance(MainActivity.this).isRequestLocationInEeaOrUnknown()) {
                            URL privacyUrl = null;
                            try {
                                // TODO: Replace with your app's privacy policy URL.
                                privacyUrl = new URL(AppConfig.TERMS_URL);

                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                // Handle error.
                                Log.d(TAG, "onConsentInfoUpdated: " + e.getLocalizedMessage());
                            }
                            form = new ConsentForm.Builder(MainActivity.this,
                                    privacyUrl)
                                    .withListener(new ConsentFormListener() {
                                        @Override
                                        public void onConsentFormLoaded() {
                                            Log.d(TAG, "onConsentFormLoaded");
                                            showForm();
                                        }

                                        @Override
                                        public void onConsentFormOpened() {
                                            Log.d(TAG, "onConsentFormOpened");
                                        }

                                        @Override
                                        public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                                            Log.d(TAG, "onConsentFormClosed");
                                        }

                                        @Override
                                        public void onConsentFormError(String errorDescription) {
                                            Log.d(TAG, "onConsentFormError");
                                            Log.d(TAG, errorDescription);
                                        }
                                    })
                                    .withPersonalizedAdsOption()
                                    .withNonPersonalizedAdsOption()
                                    .build();
                            form.load();
                        } else {
                            Log.d(TAG, "PERSONALIZED else");
                            ConsentInformation.getInstance(MainActivity.this).setConsentStatus(ConsentStatus.PERSONALIZED);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String reason) {
                Log.d(TAG, "onFailedToUpdateConsentInfo: " + reason);
            }
        });
    }

    private void showForm() {
        if (form != null) {
            Log.d(TAG, "show ok");
            form.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        return true;
    }


    private boolean loadFragment(Fragment fragment) {

        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_search:

                final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {

                        Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
                        intent.putExtra("q", s);
                        startActivity(intent);

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                });

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            new AlertDialog.Builder(MainActivity.this).setMessage("Do you want to exit ?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            finish();
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();

        }
    }

    //----nav menu item click---------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // set item as selected to persist highlight
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
        return true;
    }

    private void showTermServicesDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_term_of_services);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        WebView webView = dialog.findViewById(R.id.webView);
        Button declineBt = dialog.findViewById(R.id.bt_decline);
        Button acceptBt = dialog.findViewById(R.id.bt_accept);
        //populate webView with data
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        webView.loadUrl(AppConfig.TERMS_URL);

        if (isDark) {
            declineBt.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey_outline));
            acceptBt.setBackground(getResources().getDrawable(R.drawable.btn_rounded_dark));
        }

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        acceptBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
                editor.putBoolean("firstTime", false);
                editor.apply();
                dialog.dismiss();
            }
        });

        declineBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    // ------------------ checking storage permission ------------
    private boolean checkStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");

                    // creating the download directory named oxoo
                    createDownloadDir();

                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    // creating download folder
    public void createDownloadDir() {
        File file = new File(Constants.getDownloadDir(MainActivity.this), getResources().getString(R.string.app_name));
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void goToSearchActivity() {
        startActivity(new Intent(MainActivity.this, SearchActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check vpn connection
        helperUtils = new HelperUtils(MainActivity.this);
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(MainActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
        }
    }
}
