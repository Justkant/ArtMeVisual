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
public class GridViewAdapter extends BaseAdapter {

    private final Context mContext;
    private final LayoutInflater inflater;

    private List<Event> events;

    public GridViewAdapter(Context context, List<Event> events) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.events = events;
    }

    public void setEventList(final List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Event event = (Event) getItem(position);
        final View view;

        if (convertView == null) {
            view = inflater.inflate(R.layout.grid_item, parent, false);
            view.setTag(R.id.item_image, view.findViewById(R.id.item_image));
            view.setTag(R.id.title_text, view.findViewById(R.id.title_text));
        } else {
            view = convertView;
        }

        TextView title = (TextView) view.getTag(R.id.title_text);
        ImageView picture = (ImageView) view.getTag(R.id.item_image);

        Picasso.with(mContext).load(mContext.getString(R.string.base_url) + "/" + event.picture_url)
                .placeholder(R.drawable.placeholders)
                .error(R.drawable.placeholders)
                .fit()
                .centerCrop()
                .into(picture);
        //TODO: replace by category based colors
        picture.setColorFilter(Color.argb(150, 255, 150, 0));
        title.setText(event.title);

        return view;
    }

}
