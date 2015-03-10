package com.example.kant.artmevisual;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kant.artmevisual.ArtmeAPI.ArtmeAPI;
import com.example.kant.artmevisual.ArtmeAPI.User;
import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.CacheManager;
import com.iainconnor.objectcache.GetCallback;
import com.iainconnor.objectcache.PutCallback;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class EditProfileActivity extends BaseActivity implements View.OnClickListener{

    private static final int RESULT_CODE = 100;
    private Context mContext;
    private MaterialEditText mEditFirstname;
    private MaterialEditText mEditLastname;
    private MaterialEditText mEditPassword;
    private ImageView user_pic;

    private ArtmeAPI mApi;
    private Toolbar mToolbar;
    private User user;
    private CacheManager mCacheManager;
    private String picture_url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mContext = this;

        mToolbar = getActionBarToolbar();

        mCacheManager = CacheManager.getInstance(((MyApplication) getApplicationContext()).getDiskCache());
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.base_url))
                .build();
        mApi = restAdapter.create(ArtmeAPI.class);

        mEditFirstname = (MaterialEditText) findViewById(R.id.firstnameText);
        mEditLastname = (MaterialEditText) findViewById(R.id.lastnameText);
        mEditPassword = (MaterialEditText) findViewById(R.id.edit_passwordText);
        user_pic = (ImageView) findViewById(R.id.profil_img);
        user_pic.setOnClickListener(this);

        Type userType = new TypeToken<User>() {
        }.getType();
        mCacheManager.getAsync("me", User.class, userType, new GetCallback() {
            @Override
            public void onSuccess(Object o) {
                if (o != null) {
                    user = (User) o;
                    ImageView profilPic = (ImageView) findViewById(R.id.profil_img);
                    Picasso.with(mContext)
                            .load(getString(R.string.base_url) + "/" + user.picture_url)
                            .centerCrop()
                            .fit()
                            .into(profilPic);
                    mToolbar.setTitle(user.username);
                    setSupportActionBar(mToolbar);
                    mEditFirstname.setHint(user.first_name);
                    mEditLastname.setHint(user.last_name);

                }
            }

            @Override
            public void onFailure(Exception e) {
                //TODO: implement error handler
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
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

    public void editUser(View v) {
        if (!mEditPassword.getText().toString().isEmpty()) {
            user.password = mEditPassword.getText().toString();
        }
        if (!mEditFirstname.getText().toString().isEmpty()) {
            user.first_name = mEditFirstname.getText().toString();
        }
        if (!mEditLastname.getText().toString().isEmpty()) {
            user.last_name = mEditLastname.getText().toString();
        }
        if (!picture_url.isEmpty()) {
            user.picture_url = picture_url;
        }
        mCacheManager.putAsync("me", user, new PutCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
        String token = MySharedPreferences.readToPreferences(getBaseContext(), getString(R.string.token_string), "");
        mApi.putUser(token, user.id, user, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                //mCreatePassword.setText("");
                //mCreatePassword.setError(getString(R.string.create_error));
            }
        });
    }

    @Override
    public void onClick(View v) {
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
                    .into(user_pic);
        }
    }
}
