package com.example.kant.artmevisual.ArtmeAPI;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shaft on 16/02/2015.
 */
public class Group implements Serializable {
    public int id;
    @SerializedName("title")
    public String title;
    public String description;
    public String picture_url;
    public String creation_date;
    public String address;
    public List<User> users = new ArrayList<>();
    public List<Event> next_events = new ArrayList<>();
    public List<Event> past_events = new ArrayList<>();
    public List<String> photos = new ArrayList<>();
    public boolean can_edit;
}
