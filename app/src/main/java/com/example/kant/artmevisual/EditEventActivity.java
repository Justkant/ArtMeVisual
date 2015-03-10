package com.example.kant.artmevisual;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.kant.artmevisual.ArtmeAPI.ArtmeAPI;
import com.example.kant.artmevisual.ArtmeAPI.Event;
import com.example.kant.artmevisual.ArtmeAPI.User;
import com.iainconnor.objectcache.CacheManager;
import com.iainconnor.objectcache.PutCallback;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class EditEventActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    private static final int RESULT_CODE = 100;
    private int event_id;
    private ImageView mEditImg;
    private MaterialEditText mEditName;
    private MaterialEditText mEditPlace;
    private MaterialEditText mEditDesc;
    private TextView mStartDate;
    private TextView mStartTime;
    private TextView mEndDate;
    private TextView mEndTime;
    private SimpleDateFormat dateFormat;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private boolean onStartDateTime = true;
    private EditEventActivity mActivity;
    private SimpleDateFormat timeFormat;
    private String picture_url;
    public Event event;
    private ArtmeAPI mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        mActivity = this;
        event_id = getIntent().getIntExtra("event_id", 0);
        if (event_id == 0) {
            finish();
        }

        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setTitle("Nouvel évènement");
            setSupportActionBar(toolbar);
        }

        mEditImg = (ImageView) findViewById(R.id.event_img);
        mEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBrowsePhotos();
            }
        });

        mEditName = (MaterialEditText) findViewById(R.id.edit_event_name);
        mEditPlace = (MaterialEditText) findViewById(R.id.edit_event_place);
        mEditDesc = (MaterialEditText) findViewById(R.id.edit_event_desc);

        mStartDate = (TextView) findViewById(R.id.start_date);
        mStartTime = (TextView) findViewById(R.id.start_time);
        mEndDate = (TextView) findViewById(R.id.end_date);
        mEndTime = (TextView) findViewById(R.id.end_time);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);
        timeFormat = new SimpleDateFormat("hh:mm:ss", getResources().getConfiguration().locale);

        final Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.HOUR, 1);
        startDate = dateFormat.format(calendar.getTime());
        startTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        mStartDate.setText(DateFormat.getDateInstance().format(calendar.getTime()));
        mStartTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
        calendar.add(Calendar.HOUR, 1);
        mEndDate.setText(DateFormat.getDateInstance().format(calendar.getTime()));
        mEndTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
        endDate = dateFormat.format(calendar.getTime());
        endTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());

        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartDateTime = true;
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setDateSetListener(mActivity);
                newFragment.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });

        mStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartDateTime = true;
                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.setTimeSetListener(mActivity);
                newFragment.show(getSupportFragmentManager(), TIMEPICKER_TAG);
            }
        });

        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartDateTime = false;
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setDateSetListener(mActivity);
                newFragment.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });

        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartDateTime = false;
                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.setTimeSetListener(mActivity);
                newFragment.show(getSupportFragmentManager(), TIMEPICKER_TAG);
            }
        });

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.base_url))
                .build();
        mApi = restAdapter.create(ArtmeAPI.class);

        mApi.getEventById(event_id,
                MySharedPreferences.readToPreferences(this, getString(R.string.token_string), ""),
                new Callback<Event>() {
                    @Override
                    public void success(Event ev, Response response) {
                        event = ev;
                        if (event.title != null)
                            mEditName.setHint(event.title);
                        if (event.adress != null)
                            mEditPlace.setHint(event.adress);
                        if (event.description != null)
                            mEditDesc.setHint(event.description);
                        try {
                            mStartDate.setText(DateFormat.getDateInstance().format(dateFormat.parse(event.start_date)));
                            mStartTime.setText(DateFormat.getTimeInstance().format(timeFormat.parse(event.start_date.substring(event.start_date.indexOf(" ") + 1))));
                            mEndDate.setText(DateFormat.getDateInstance().format(dateFormat.parse(event.end_date)));
                            mEndTime.setText(DateFormat.getTimeInstance().format(timeFormat.parse(event.end_date.substring(event.end_date.indexOf(" ") + 1))));
                            startDate = mStartDate.getText().toString();
                            startTime = mStartTime.getText().toString();
                            endDate = mEndDate.getText().toString();
                            endTime = mEndTime.getText().toString();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

    private void startBrowsePhotos() {
        Intent intent = new Intent(this, BrowsePhotosActivity.class);
        intent.putExtra("me", true);
        startActivityForResult(intent, RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CODE && resultCode == RESULT_OK && data != null) {
            picture_url = data.getStringExtra("picture_url");
            Picasso.with(mActivity)
                    .load(getString(R.string.base_url) + "/" + picture_url)
                    .centerCrop()
                    .fit()
                    .into(mEditImg);
        }
    }

    public void editEvent(View v) {
        if (!mEditName.getText().toString().isEmpty()) {
            event.title = mEditName.getText().toString();
        }
        if (!mEditPlace.getText().toString().isEmpty()) {
            event.adress = mEditPlace.getText().toString();
        }
        if (!mEditDesc.getText().toString().isEmpty()) {
            event.description = mEditDesc.getText().toString();
        }
        if (picture_url != null) {
            event.picture_url = picture_url;
        }
        event.start_date = startDate + " " + startTime;
        event.end_date = endDate + " " + endTime;
        String token = MySharedPreferences.readToPreferences(getBaseContext(), getString(R.string.token_string), "");
        mApi.putEvent(event.id, token, event, new Callback<Event>() {
            @Override
            public void success(Event event, Response response) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        final Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth);
        if (onStartDateTime) {
            startDate = dateFormat.format(cal.getTime());
            mStartDate.setText(DateFormat.getDateInstance().format(cal.getTime()));
        } else {
            endDate = dateFormat.format(cal.getTime());
            mEndDate.setText(DateFormat.getDateInstance().format(cal.getTime()));
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        if (onStartDateTime) {
            startTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(cal.getTime());
            mStartTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(cal.getTime()));
        } else {
            endTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(cal.getTime());
            mEndTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(cal.getTime()));
        }
    }
}
