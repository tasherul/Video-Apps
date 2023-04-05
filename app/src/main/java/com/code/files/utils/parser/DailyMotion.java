package com.code.files.utils.parser;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DailyMotion {
    private final String TAG = DailyMotion.class.getSimpleName();
    private String url;
    private Context context;
    private LinkParserCallback callback;

    public DailyMotion(String url, Context context, LinkParserCallback callback) {
        this.url = url;
        this.context = context;
        this.callback = callback;
    }

    public  void getStreamingLink(){
        String apiUrl = "https://www.dailymotion.com/player/metadata/video/" + getDailyMotionId(url);

        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        CustomRequest customRequest = new CustomRequest(Request.Method.GET, apiUrl, response -> {
            try {
                JSONObject obj = response.getJSONObject("qualities");
                JSONArray contacts = obj.getJSONArray("auto");
                List<Stream> streamList = new ArrayList();
                for(int i = 0; i < contacts.length(); ++i) {
                    String streamUrl = contacts.getJSONObject(i).getString("url");
                    streamList.add(new Stream("Default", "m3u8", streamUrl, url));
                }
                callback.onSuccess(streamList);
            } catch (Exception e) {
                callback.onError(e.getLocalizedMessage());
            }
        }, error -> callback.onError(error.getLocalizedMessage()));
        requestQueue.add(customRequest);
    }

    private  String getDailyMotionId(String url){
        String id = url.substring(url.lastIndexOf("/") + 1);
        Log.e(TAG, "getId: " + id );
        return id;
    }
}
