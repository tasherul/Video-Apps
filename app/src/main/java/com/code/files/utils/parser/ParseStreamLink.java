package com.code.files.utils.parser;

import android.content.Context;

import com.oxoo.spagreen.R;

import java.util.ArrayList;
import java.util.List;


public class ParseStreamLink {

    public static final String DAILY_MOTION = "dailymotion";
    public static final String DROP_BOX = "dropbox";
    public static final String VIMEO = "vimeo";
    public static final String TUBI_TV = "tubitv";
    private final String TAG = ParseStreamLink.class.getSimpleName();
    private Context context;
    private String link;
    private String type;
    private LinkParserCallback callback;

    public ParseStreamLink(Context context, String link, String type, LinkParserCallback callback) {
        this.context = context;
        this.link = link;
        this.type = type;
        this.callback = callback;
    }

    public void parseLink(){
        switch (type){
            case DAILY_MOTION:
                new DailyMotion(link, context, callback).getStreamingLink();
                break;
            case DROP_BOX:
                new DropBox(link, context, callback).getStreamingLink();
                break;
            case TUBI_TV:
                new TubiTV(link, context, callback).getStreamingLink();
                break;
            case VIMEO:
                new Vimeo(link, context, callback).getStreamingLink();
                break;
            default:
                //callback.onError(context.getString(R.string.something_went_text));
               List<Stream> list = new ArrayList<>();
               list.add(new Stream("", this.type, this.link, this.link));
               callback.onSuccess(list);
                break;
        }
    }
}
