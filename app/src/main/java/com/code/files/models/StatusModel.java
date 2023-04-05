package com.code.files.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusModel {

    @SerializedName("login_status")
    @Expose
    private boolean login;
    @SerializedName("movie_status")
    @Expose
    private boolean movie;
    @SerializedName("series_status")
    @Expose
    private boolean series;

    public boolean getloginStatus() {
        return login;
    }

    public boolean getmovieStatus() {
        return movie;
    }

    public boolean getseriesStatus() {
        return series;
    }

}
