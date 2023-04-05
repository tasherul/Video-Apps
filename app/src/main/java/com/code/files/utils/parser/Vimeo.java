package com.code.files.utils.parser;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Vimeo {
    private final String TAG = Vimeo.class.getSimpleName();
    private String url;
    private Context context;
    private LinkParserCallback callback;

    public Vimeo(String url, Context context, LinkParserCallback callback) {
        this.url = url;
        this.context = context;
        this.callback = callback;
    }

    public void getStreamingLink(){
        String videoID = getID(url);
        String api = "https://player.vimeo.com/video/" + videoID + "/config";
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        CustomRequest customRequest = new CustomRequest(Request.Method.GET, api, response -> {
            try {
                JSONObject requestArray = response.getJSONObject("request");
                JSONObject files = requestArray.getJSONObject("files");
                JSONArray progressiveArray = files.getJSONArray("progressive");
                List<Stream> streamList = new ArrayList();

                if (!progressiveArray.isNull(0)) {
                    Log.e(TAG, "getStreamingLink: progressive" );
                    for (int i = 0; i < progressiveArray.length(); ++i) {
                        JSONObject object = progressiveArray.getJSONObject(i);
                        streamList.add(new Stream(object.getString("quality"), "mp4", object.getString("url"), url));
                    }
                }else {
                    Log.e(TAG, "getStreamingLink: hls" );
                    JSONObject hls = files.getJSONObject("hls");
                    JSONObject cdns = hls.getJSONObject("cdns");
                    JSONObject akfire = cdns.getJSONObject("akfire_interconnect_quic");
                    String link = akfire.getString("url");
                    streamList.add(new Stream("720p", "m3u8", link, url));
                }
                callback.onSuccess(streamList);
            } catch (JSONException error) {
                callback.onError(error.getLocalizedMessage());
            }
        }, error -> {
            callback.onError(error.getLocalizedMessage());
        });

        requestQueue.add(customRequest);
    }

    private String getID(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
