package com.example.kant.artmevisual;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;

import java.util.ArrayList;
import java.util.List;

public class PhotosGridFragment extends Fragment implements AdapterView.OnItemClickListener {

    private List<String> photos;

    static PhotosGridFragment newInstance(List<String> photos) {
        PhotosGridFragment f = new PhotosGridFragment();

        Bundle args = new Bundle();
        args.putStringArrayList("photos", (ArrayList<String>) photos);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photos = getArguments() != null ? getArguments().getStringArrayList("photos") : new ArrayList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos_grid, container, false);

        Activity parentActivity = getActivity();
        final ObservableGridView gridView = (ObservableGridView) view.findViewById(R.id.scroll);
        final PhotosGridAdapter photosGridAdapter = new PhotosGridAdapter(getActivity(), photos);
        gridView.setAdapter(photosGridAdapter);
        gridView.setOnItemClickListener(this);
        gridView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.main_content));

        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            gridView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (photos.isEmpty() || photos.get(position).isEmpty())
            return;
        Intent intent = new Intent();
        intent.putExtra("picture_url", photos.get(position));
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
