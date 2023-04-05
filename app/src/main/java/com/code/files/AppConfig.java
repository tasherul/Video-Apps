package com.code.files;

import com.google.common.io.BaseEncoding;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AppConfig {
    static {
        System.loadLibrary("api_config");
    }

    public static native String getApiServerUrl();
    public static native String getApiKey();
    public static native String getPurchaseCode();
    public static native String getOneSignalAppID();
    public static native String getYoutubeApiKey();
    public static native String getTermsUrl();
    private static final String K = "a8p2i3t0s8o5f7t";



    public static final String API_KEY = Xdecode(K,getApiKey());
    //copy your terms url from php admin dashboard & paste below
    public static final String TERMS_URL = Xdecode(K,getTermsUrl());
    public static final String ONE_SIGNAL_APP_ID = getOneSignalAppID();
    public static final String YOUTUBE_API_KEY = getYoutubeApiKey();

    //paypal payment status
    public static final boolean PAYPAL_ACCOUNT_LIVE = true;
    // download option for non subscribed user
    public static final boolean ENABLE_DOWNLOAD_TO_ALL = true;
    //enable RTL
    public static boolean ENABLE_RTL = true;
    //enable external player
    public static final boolean ENABLE_EXTERNAL_PLAYER = false;
    //default theme
    public static boolean DEFAULT_DARK_THEME_ENABLE = true;
    // First, you have to configure firebase to enable facebook, phone and google login
    // facebook authentication
    public static final boolean ENABLE_FACEBOOK_LOGIN = true;
    //Phone authentication
    public static final boolean ENABLE_PHONE_LOGIN = true;
    //Google authentication
    public static final boolean ENABLE_GOOGLE_LOGIN = true;

    public static final String API_SERVER_URL = Xdecode(K,getApiServerUrl());
    public static final String CIPHER_NAME = "AES/CBC/PKCS5PADDING";
    public static final int CIPHER_KEY_LEN = 16; //128 bits

    public static final String Xdecode(String key, String hexdata) {
        try {
            if (key.length() < CIPHER_KEY_LEN) {
                int numPad = CIPHER_KEY_LEN - key.length();

                for(int i = 0; i < numPad; i++){
                    key += "0"; //0 pad to len 16 bytes
                }

            } else if (key.length() > CIPHER_KEY_LEN) {
                key = key.substring(0, CIPHER_KEY_LEN); //truncate to 16 bytes
            }

            String data = hexToAscii(hexdata);

            String[] parts = data.split(":");

            IvParameterSpec iv = new IvParameterSpec(BaseEncoding.base64().decode(parts[1]));
            //Base64.getDecoder().decode(parts[1]));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance(CIPHER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] decodedEncryptedData = BaseEncoding.base64().decode(parts[0]);
            //Base64.getDecoder().decode(parts[0]);


            byte[] original = cipher.doFinal(decodedEncryptedData);

            return new String(original);
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        return "";
    }
    public static final String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }
}
