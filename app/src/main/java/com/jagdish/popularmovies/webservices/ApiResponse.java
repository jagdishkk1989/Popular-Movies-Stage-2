package com.jagdish.popularmovies.webservices;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ApiResponse<T> {
    @SerializedName("results")
    public ArrayList<T> results;
}