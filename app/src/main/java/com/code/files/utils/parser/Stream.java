package com.code.files.utils.parser;

public class Stream {
    private final String TAG = Stream.class.getSimpleName();

    private String quality;
    private String extension;
    private String url;
    private String refer;

    public Stream(String quality, String extension, String url, String refer) {
        this.quality = quality;
        this.extension = extension;
        this.url = url;
        this.refer = refer;
    }

    public String getTAG() {
        return TAG;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }
}
