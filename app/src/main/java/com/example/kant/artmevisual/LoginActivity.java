package com.example.kant.artmevisual;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.kant.artmevisual.ArtmeAPI.ArtmeAPI;
import com.example.kant.artmevisual.ArtmeAPI.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoginActivity extends ActionBarActivity {

    private Toolbar toolbar;

    private ArtmeAPI mApi;

    private RelativeLayout mLoginLayout;
    private RelativeLayout mCreateAccountLayout;

    private Button mBtnLogin;
    private MaterialEditText mLogin;
    private MaterialEditText mPassword;
    private boolean isLoginValide = false;
    private boolean isPasswordValide = false;

    private Button mBtnCreateUser;
    private MaterialEditText mCreateUsername;
    private MaterialEditText mCreateEmail;
    private MaterialEditText mCreateFirstname;
    private MaterialEditText mCreateLastname;
    private MaterialEditText mCreatePassword;
    private boolean isCreateUsernameValide = false;
    private boolean isCreateEmailValide = false;
    private boolean isCreatePasswordValide = false;

    private void resetCache() {
        MySharedPreferences.clearPreferences(this);
        try {
            ((MyApplication) getApplicationContext()).getDiskCache().clearCache();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolbar.setTitle("Connexion");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLoginLayout = (RelativeLayout) findViewById(R.id.loginRelativeLayout);
        mCreateAccountLayout = (RelativeLayout) findViewById(R.id.createAccountRelativeLayout);

        mLogin = (MaterialEditText) findViewById(R.id.loginText);
        mPassword = (MaterialEditText) findViewById(R.id.passwordText);
        mBtnLogin = (Button) findViewById(R.id.loginButton);

        mBtnCreateUser = (Button) findViewById(R.id.create_useraccount);
        mCreateUsername = (MaterialEditText) findViewById(R.id.usernameText);
        mCreateEmail = (MaterialEditText) findViewById(R.id.emailText);
        mCreateFirstname = (MaterialEditText) findViewById(R.id.firstnameText);
        mCreateLastname = (MaterialEditText) findViewById(R.id.lastnameText);
        mCreatePassword = (MaterialEditText) findViewById(R.id.create_passwordText);

        setTextWatcherForLogin();
        setTextWatcherForCreateUser();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.base_url))
                .build();
        mApi = restAdapter.create(ArtmeAPI.class);

        if (savedInstanceState != null) {
            if (!savedInstanceState.getBoolean("loginState")) {
                mLoginLayout.setVisibility(View.GONE);
                mCreateAccountLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mLoginLayout.getVisibility() == View.VISIBLE)
            outState.putBoolean("loginState", true);
        else
            outState.putBoolean("loginState", false);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        resetCache();
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

    private void setTextWatcherForLogin() {
        mLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isLoginValide = !s.toString().isEmpty();
                updateLoginBtnState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isPasswordValide = !s.toString().isEmpty();
                updateLoginBtnState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setTextWatcherForCreateUser() {
        mCreateUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isCreateUsernameValide = !s.toString().isEmpty();
                updateCreateUserBtnState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCreateEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isCreateEmailValide = !s.toString().isEmpty();
                updateCreateUserBtnState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCreatePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isCreatePasswordValide = !s.toString().isEmpty();
                updateCreateUserBtnState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateLoginBtnState() {
        if (isLoginValide && isPasswordValide) {
            mBtnLogin.setEnabled(true);
        } else {
            mBtnLogin.setEnabled(false);
        }
    }

    private void updateCreateUserBtnState() {
        if (isCreateUsernameValide && isCreatePasswordValide && isCreateEmailValide) {
            mBtnCreateUser.setEnabled(true);
        } else {
            mBtnCreateUser.setEnabled(false);
        }
    }

    public void switchLayout(View view) {
        if (mLoginLayout.getVisibility() == View.VISIBLE) {
            mLoginLayout.animate().alpha(0.0f).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginLayout.setVisibility(View.GONE);
                    toolbar.setTitle("Création de compte");
                    setSupportActionBar(toolbar);
                    mCreateAccountLayout.setAlpha(0.0f);
                    mCreateAccountLayout.setVisibility(View.VISIBLE);
                    mCreateAccountLayout.animate().setListener(null).alpha(1.0f);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {
            mCreateAccountLayout.animate().alpha(0.0f).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mCreateAccountLayout.setVisibility(View.GONE);
                    toolbar.setTitle("Connexion");
                    setSupportActionBar(toolbar);
                    mLoginLayout.setAlpha(0.0f);
                    mLoginLayout.setVisibility(View.VISIBLE);
                    mLoginLayout.animate().setListener(null).alpha(1.0f);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    public void connectUser(View view) {
        User user = new User();
        user.username = mLogin.getText().toString();
        user.password = mPassword.getText().toString();
        mApi.login(user, new Callback<String>() {
            @Override
            public void success(String token, Response response) {
                MySharedPreferences.saveToPreferences(getBaseContext(), getString(R.string.token_string), token);
                startActivity(new Intent(getBaseContext(), BrowseEventsActivity.class));
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                mPassword.setText("");
                mPassword.setError(getString(R.string.login_error));
            }
        });
    }

    public void createUser(View view) {
        User user = new User();
        user.username = mCreateUsername.getText().toString();
        user.email = mCreateEmail.getText().toString();
        user.password = mCreatePassword.getText().toString();
        if (!mCreateFirstname.getText().toString().isEmpty()) {
            user.first_name = mCreateFirstname.getText().toString();
        }
        if (!mCreateLastname.getText().toString().isEmpty()) {
            user.last_name = mCreateLastname.getText().toString();
        }
        mApi.postUser(user, new Callback<String>() {
            @Override
            public void success(String token, Response response) {
                MySharedPreferences.saveToPreferences(getBaseContext(), getString(R.string.token_string), token);
                startActivity(new Intent(getBaseContext(), BrowseEventsActivity.class));
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                mCreatePassword.setText("");
                mCreatePassword.setError(getString(R.string.create_error));
            }
        });
    }
}
