package com.example.kant.artmevisual;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.kant.artmevisual.ArtmeAPI.ArtmeAPI;
import com.example.kant.artmevisual.ArtmeAPI.Event;
import com.example.kant.artmevisual.ArtmeAPI.Group;
import com.example.kant.artmevisual.ArtmeAPI.User;
import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.CacheManager;
import com.iainconnor.objectcache.GetCallback;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreateEventActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final int RESULT_CODE = 100;

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";

    private ArtmeAPI mApi;

    private CreateEventActivity mActivity;
    private Context mContext;
    private Spinner mAccountSpinner;
    private User user = null;

    private ImageView mEventImg;

    private MaterialEditText mEventName;
    private MaterialEditText mEventPlace;
    private MaterialEditText mEventDesc;

    private TextView mStartDate;
    private TextView mStartTime;
    private TextView mEndDate;
    private TextView mEndTime;

    private int postId;
    private boolean postAsUser = true;

    private boolean onStartDateTime = true;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;

    private SimpleDateFormat dateFormat;
    private boolean intoValidate = false;
    private String picture_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        mActivity = this;
        mContext = this;

        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setTitle("Nouvel évènement");
            setSupportActionBar(toolbar);
        }

        mAccountSpinner = (Spinner) findViewById(R.id.account_spinner);
        mAccountSpinner.setOnItemSelectedListener(this);

        mEventImg = (ImageView) findViewById(R.id.event_img);
        mEventImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBrowsePhotos();
            }
        });

        CacheManager mCacheManager = CacheManager.getInstance(((MyApplication) getApplicationContext()).getDiskCache());
        Type userType = new TypeToken<User>() {
        }.getType();
        mCacheManager.getAsync("me", User.class, userType, new GetCallback() {
            @Override
            public void onSuccess(Object o) {
                if (o != null) {
                    user = (User) o;
                    postId = user.id;
                    ArrayList<String> spinnerArray = new ArrayList<>();
                    spinnerArray.add(user.username);
                    for (Group group : user.groups) {
                        spinnerArray.add(group.title);
                    }
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(mContext,
                            android.R.layout.simple_spinner_item,
                            spinnerArray);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mAccountSpinner.setAdapter(spinnerArrayAdapter);
                }
            }

            @Override
            public void onFailure(Exception e) {
                //TODO: implement error handler
            }
        });

        mEventName = (MaterialEditText) findViewById(R.id.create_event_name);
        mEventPlace = (MaterialEditText) findViewById(R.id.create_event_place);
        mEventDesc = (MaterialEditText) findViewById(R.id.create_event_desc);

        mStartDate = (TextView) findViewById(R.id.start_date);
        mStartTime = (TextView) findViewById(R.id.start_time);
        mEndDate = (TextView) findViewById(R.id.end_date);
        mEndTime = (TextView) findViewById(R.id.end_time);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);

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
            Picasso.with(mContext)
                    .load(getString(R.string.base_url) + "/" + picture_url)
                    .centerCrop()
                    .fit()
                    .into(mEventImg);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_validate && !intoValidate) {
            intoValidate = true;
            if (mEventName.getText().toString().isEmpty()) {
                mEventName.setError("Le titre de l'évènement est obligatoire");
                intoValidate = false;
                return true;
            }
            Event event = new Event();
            if (picture_url != null && !picture_url.isEmpty())
                event.picture_url = picture_url;
            event.title = mEventName.getText().toString();
            String address = mEventPlace.getText().toString();
            if (!address.isEmpty())
                event.adress = address;
            event.start_date = startDate + " " + startTime;
            event.end_date = endDate + " " + endTime;
            String desc = mEventDesc.getText().toString();
            if (!desc.isEmpty()) {
                event.description = desc;
            }
            if (postAsUser) {
                postEventAsUser(event);
            } else {
                postEventAsGroup(event);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void postEventAsGroup(Event event) {
        mApi.groupPostEvent(postId, MySharedPreferences.readToPreferences(mContext, getString(R.string.token_string), ""), event, new Callback<Event>() {
            @Override
            public void success(Event event, Response response) {
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                intoValidate = false;
            }
        });
    }

    private void postEventAsUser(Event event) {
        mApi.userPostEvent(postId, MySharedPreferences.readToPreferences(mContext, getString(R.string.token_string), ""), event, new Callback<Event>() {
            @Override
            public void success(Event event, Response response) {
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                intoValidate = false;
            }
        });
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (user != null) {
            if (position == 0) {
                postId = user.id;
                postAsUser = true;
            } else {
                postId = user.groups.get(position - 1).id;
                postAsUser = false;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static class TimePickerFragment extends DialogFragment {

        private TimePickerDialog.OnTimeSetListener timeSetListener;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), timeSetListener, hour, minute, true);
        }

        public void setTimeSetListener(TimePickerDialog.OnTimeSetListener timeSetListener) {
            this.timeSetListener = timeSetListener;
        }
    }

    public static class DatePickerFragment extends DialogFragment {

        private DatePickerDialog.OnDateSetListener dateSetListener;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
        }

        public void setDateSetListener(DatePickerDialog.OnDateSetListener dateSetListener) {
            this.dateSetListener = dateSetListener;
        }
    }
}
