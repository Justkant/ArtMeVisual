package com.example.kant.artmevisual.ArtmeAPI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shaft on 16/02/2015.
 */
public class Event implements Serializable {
    public int id;
    public String title;
    public String description;
    public String start_date;
    public String end_date;
    public String adress;
    public String picture_url;
    public List<User> users = new ArrayList<>();
    public List<String> photos = new ArrayList<>();
    public boolean can_edit;
    public boolean is_sub;
    //TODO ADD HOUR + TAG (SOUS TYPE ?) + PRIX ? + lieu et non adress (zenith toussa)
}
