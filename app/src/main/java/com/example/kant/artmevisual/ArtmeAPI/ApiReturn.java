package com.example.kant.artmevisual.ArtmeAPI;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Shaft on 01/03/2015.
 */
public class ApiReturn implements Serializable {
    @SerializedName("status")
    public String status;
    @SerializedName("messages")
    public String messages;
}
