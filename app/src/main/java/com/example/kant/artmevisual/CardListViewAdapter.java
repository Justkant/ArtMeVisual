package com.example.kant.artmevisual;

import android.content.Context;
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
public class CardListViewAdapter extends BaseAdapter {

    private final Context mContext;
    private final LayoutInflater inflater;

    private List<Event> events;

    public CardListViewAdapter(Context context, List<Event> events) {
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
            view = inflater.inflate(R.layout.card_list_item, parent, false);
            view.setTag(R.id.event_img, view.findViewById(R.id.event_img));
            view.setTag(R.id.event_title, view.findViewById(R.id.event_title));
            view.setTag(R.id.event_desc, view.findViewById(R.id.event_desc));
        } else {
            view = convertView;
        }

        TextView title = (TextView) view.getTag(R.id.event_title);
        TextView desc = (TextView) view.getTag(R.id.event_desc);
        ImageView picture = (ImageView) view.getTag(R.id.event_img);

        Picasso.with(mContext).load(mContext.getString(R.string.base_url) + "/" + event.picture_url)
                .placeholder(R.drawable.placeholders)
                .error(R.drawable.placeholders)
                .fit()
                .centerCrop()
                .into(picture);
        title.setText(event.title);
        desc.setText(event.description);

        return view;
    }

}
