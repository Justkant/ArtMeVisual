package com.example.kant.artmevisual.ArtmeAPI;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shaft on 16/02/2015.
 */
public class User implements Serializable {
    public int id;
    public String last_name;
    public String first_name;
    @SerializedName("username")
    public String username;
    public String picture_url;
    public String email;
    @SerializedName("password")
    public String password;
    public String description;
    public String creation_date;

    public List<Group> groups = new ArrayList<>();

    public List<Event> sub_events = new ArrayList<>();
    public List<Event> past_events = new ArrayList<>();
    public List<Event> next_events = new ArrayList<>();

    public List<String> photos = new ArrayList<>();
}
