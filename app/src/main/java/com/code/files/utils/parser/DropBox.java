package com.code.files.utils.parser;

import android.content.Context;

import com.oxoo.spagreen.R;

import java.util.ArrayList;
import java.util.List;


public class DropBox {
    private final String TAG = DropBox.class.getSimpleName();
   private String url;
   private Context context;
   private LinkParserCallback callback;

   public DropBox(String url, Context context, LinkParserCallback callback) {
      this.url = url;
      this.context = context;
      this.callback = callback;
   }

   public void getStreamingLink(){
      String tempUrl = null;
      List<Stream> streams = new ArrayList<>();
      if (url.toLowerCase().contains("dl=0")){
         tempUrl = url.replace("dl=0", "dl=1");
      }else {
         tempUrl = url + "?dl=1";
      }

      if (tempUrl.contains("dl=1")){
         streams.add(new Stream("default", "mp4", tempUrl, url));
         callback.onSuccess(streams);
      }else {
         callback.onError(context.getString(R.string.something_went_text));
      }
   }
}
