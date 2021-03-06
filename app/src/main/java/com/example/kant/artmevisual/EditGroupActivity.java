package com.example.kant.artmevisual;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.kant.artmevisual.ArtmeAPI.ApiReturn;
import com.example.kant.artmevisual.ArtmeAPI.ArtmeAPI;
import com.example.kant.artmevisual.ArtmeAPI.Group;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class EditGroupActivity extends BaseActivity implements View.OnClickListener {

    private static final int RESULT_CODE = 100;
    private int group_id;
    private Toolbar mToolbar;
    private MaterialEditText mEditName;
    private MaterialEditText mEditDescription;
    private MaterialEditText mEditAdress;
    private ImageView user_pic;
    private Group group;
    private ArtmeAPI mApi;
    private String picture_url;
    private EditGroupActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        mActivity = this;
        group_id = getIntent().getIntExtra("group_id", 0);
        if (group_id == 0) {
            finish();
        }

        mToolbar = getActionBarToolbar();

        mEditName = (MaterialEditText) findViewById(R.id.groupName);
        mEditDescription = (MaterialEditText) findViewById(R.id.groupDescription);
        mEditAdress = (MaterialEditText) findViewById(R.id.group_adress);
        user_pic = (ImageView) findViewById(R.id.group_img);
        user_pic.setOnClickListener(this);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.base_url))
                .build();
        mApi = restAdapter.create(ArtmeAPI.class);

        mApi.getGroupById(group_id,
                MySharedPreferences.readToPreferences(this, getString(R.string.token_string), ""),
                new Callback<Group>() {
                    @Override
                    public void success(Group gp, Response response) {
                        group = gp;
                        mEditName.setText(gp.title);
                        mEditDescription.setText(gp.description);
                        mEditAdress.setText(gp.address);
                        mToolbar.setTitle(group.title);
                        setSupportActionBar(mToolbar);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
    }

    public void editGroup(View v) {
        if (!mEditName.getText().toString().isEmpty()) {
            group.title = mEditName.getText().toString();
        }
        if (!mEditDescription.getText().toString().isEmpty()) {
            group.description = mEditDescription.getText().toString();
        }
        if (!mEditAdress.getText().toString().isEmpty()) {
            group.address = mEditAdress.getText().toString();
        }
        if (picture_url != null) {
            group.picture_url = picture_url;
        }
        String token = MySharedPreferences.readToPreferences(getBaseContext(), getString(R.string.token_string), "");
        mApi.putGroup(token, group.id, group, new Callback<Group>() {
            @Override
            public void success(Group group, Response response) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CODE && resultCode == RESULT_OK && data != null) {
            picture_url = data.getStringExtra("picture_url");
            Picasso.with(getApplicationContext())
                    .load(getString(R.string.base_url) + "/" + picture_url)
                    .centerCrop()
                    .fit()
                    .into(user_pic);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            mApi.deleteGroup(group.id, MySharedPreferences.readToPreferences(mActivity, getString(R.string.token_string), ""), new Callback<ApiReturn>() {
                @Override
                public void success(ApiReturn s, Response response) {
                    Intent intent = getIntent();
                    intent.putExtra("delete", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void failure(RetrofitError error) {
                    SnackbarManager.show(
                            Snackbar.with(mActivity)
                                    .type(SnackbarType.MULTI_LINE)
                                    .text("Un problème est survenu")
                    );
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, BrowsePhotosActivity.class);
        intent.putExtra("me", true);
        startActivityForResult(intent, RESULT_CODE);
    }
}
