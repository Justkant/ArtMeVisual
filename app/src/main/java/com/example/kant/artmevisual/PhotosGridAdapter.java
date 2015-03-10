package com.example.kant.artmevisual;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kant.artmevisual.ArtmeAPI.Event;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Quentin on 28/02/2015.
 * EpiAndroid Project.
 */
public class PhotosGridAdapter extends BaseAdapter {

    private final Context mContext;
    private final LayoutInflater inflater;

    private List<String> photos;

    public PhotosGridAdapter(Context context, List<String> photos) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.photos = photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String photo = (String) getItem(position);
        final View view;

        if (convertView == null) {
            view = inflater.inflate(R.layout.photo_grid_item, parent, false);
            view.setTag(R.id.item_image, view.findViewById(R.id.item_image));
        } else {
            view = convertView;
        }

        ImageView picture = (ImageView) view.getTag(R.id.item_image);

        Picasso.with(mContext).load(mContext.getString(R.string.base_url) + "/" + photo)
                .placeholder(R.drawable.placeholders)
                .error(R.drawable.placeholders)
                .fit()
                .centerCrop()
                .into(picture);

        return view;
    }

}
