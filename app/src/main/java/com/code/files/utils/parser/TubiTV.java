package com.code.files.utils.parser;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TubiTV {
    private final String TAG = TubiTV.class.getSimpleName();
    private String url;
    private Context context;
    private LinkParserCallback callback;

    public TubiTV(String url, Context context, LinkParserCallback callback) {
        this.url = url;
        this.context = context;
        this.callback = callback;
    }

    public void getStreamingLink(){
        List<Stream> streams = new ArrayList<>();
        String id = getId();
        String api = "https://tubitv.com/oz/videos/" + id + "/content";
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        CustomRequest customRequest = new CustomRequest(Request.Method.GET, api, response -> {
            try {
                JSONArray videoResources    = response.getJSONArray("video_resources");
                JSONObject rawManifest      = (JSONObject) videoResources.get(0) ;
                JSONObject manifest         = rawManifest.getJSONObject("manifest");
                String link                 = manifest.getString("url");
                streams.add(new Stream("720", "m3u8", link, ""));
                callback.onSuccess(streams);

            } catch (JSONException e) {
                e.printStackTrace();
                callback.onError(e.getLocalizedMessage());
            }
        }, error -> {
            callback.onError(error.getLocalizedMessage());
        });

        requestQueue.add(customRequest);
    }

    private String getId(){
        String[] parts = url.split("/");
        return parts[4];
    }
}
