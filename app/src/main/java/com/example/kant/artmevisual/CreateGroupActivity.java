package com.example.kant.artmevisual;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kant.artmevisual.ArtmeAPI.ApiReturn;
import com.example.kant.artmevisual.ArtmeAPI.ArtmeAPI;
import com.example.kant.artmevisual.ArtmeAPI.Group;
import com.example.kant.artmevisual.ArtmeAPI.User;
import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.CacheManager;
import com.iainconnor.objectcache.GetCallback;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.lang.reflect.Type;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class CreateGroupActivity extends BaseActivity {

    private Context mContext;

    private ArtmeAPI mApi;

    private MaterialEditText mGroupName;
    private MaterialEditText mGroupPlace;
    private MaterialEditText mGroupDesc;

    private int user_id;

    private boolean intoValidate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        mContext = this;

        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setTitle("Nouveau groupe");
            setSupportActionBar(toolbar);
        }

        CacheManager mCacheManager = CacheManager.getInstance(((MyApplication) getApplicationContext()).getDiskCache());
        Type userType = new TypeToken<User>() {
        }.getType();
        mCacheManager.getAsync("me", User.class, userType, new GetCallback() {
            @Override
            public void onSuccess(Object o) {
                if (o != null) {
                    User user = (User) o;
                    user_id = user.id;
                }
            }

            @Override
            public void onFailure(Exception e) {
                //TODO: implement error handler
            }
        });

        mGroupName = (MaterialEditText) findViewById(R.id.create_group_name);
        mGroupPlace = (MaterialEditText) findViewById(R.id.create_group_place);
        mGroupDesc = (MaterialEditText) findViewById(R.id.create_group_desc);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.base_url))
                .build();
        mApi = restAdapter.create(ArtmeAPI.class);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_group, menu);
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
            if (mGroupName.getText().toString().isEmpty()) {
                mGroupName.setError("Le nom de groupe est obligatoire");
                intoValidate = false;
                return true;
            }
            Group group = new Group();
            group.title = mGroupName.getText().toString();
            String address = mGroupPlace.getText().toString();
            if (!address.isEmpty())
                group.adress = address;
            String desc = mGroupDesc.getText().toString();
            if (!desc.isEmpty())
                group.description = desc;
            mApi.crtGroup(MySharedPreferences.readToPreferences(mContext, getString(R.string.token_string), ""), group, new Callback<ApiReturn>() {
                @Override
                public void success(ApiReturn apiReturn, Response response) {
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void failure(RetrofitError error) {
                    intoValidate = false;
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
    }
}
