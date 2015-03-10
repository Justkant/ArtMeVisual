package com.example.kant.artmevisual;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.kant.artmevisual.ArtmeAPI.ArtmeAPI;
import com.example.kant.artmevisual.ArtmeAPI.User;
import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.CacheManager;
import com.iainconnor.objectcache.GetCallback;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import java.lang.reflect.Type;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Quentin on 01/03/2015.
 * EpiAndroid Project.
 */
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = getActionBarToolbar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SettingsFragment())
                    .commit();
        }

    }

    @Override
    public void onRefresh() {
    }

    public class SettingsFragment extends PreferenceFragment {
        private MaterialDialog.Builder mDecoDialog;
        private MaterialDialog.Builder mDeleteDialog;
        private ArtmeAPI mApi;
        private CacheManager mCacheManager;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            mCacheManager = CacheManager.getInstance(((MyApplication) getApplicationContext()).getDiskCache());

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(getString(R.string.base_url))
                    .build();
            mApi = restAdapter.create(ArtmeAPI.class);

            mDecoDialog = new MaterialDialog.Builder(getActivity())
                    .title("Effacer les données Utilisateur ?")
                    .content("Vous devrez vous reconnecter à votre compte ArtMe.")
                    .positiveText("OK")
                    .negativeText("ANNULER")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            MySharedPreferences.clearPreferences(getActivity());
                            getActivity().finish();
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                        }
                    });

            mDeleteDialog = new MaterialDialog.Builder(getActivity())
                    .title("Supprimer le compte Utilisateur ?")
                    .content("Vous devrez créer un nouveau compte afin de vous reconnecter.")
                    .positiveText("OK")
                    .negativeText("ANNULER")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            Type userType = new TypeToken<User>() {
                            }.getType();
                            mCacheManager.getAsync("me", User.class, userType, new GetCallback() {
                                @Override
                                public void onSuccess(Object o) {
                                    if (o != null) {
                                        User user = (User) o;
                                        mApi.deleteUser(user.id, MySharedPreferences.readToPreferences(getActivity(), getString(R.string.token_string), ""), new Callback<String>() {
                                            @Override
                                            public void success(String s, Response response) {
                                                MySharedPreferences.clearPreferences(getActivity());
                                                getActivity().finish();
                                            }

                                            @Override
                                            public void failure(RetrofitError error) {
                                                SnackbarManager.show(
                                                        Snackbar.with(getActivity())
                                                                .type(SnackbarType.MULTI_LINE)
                                                                .text("Un problème est survenu")
                                                );
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    //TODO: implement error handler
                                }
                            });
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                        }
                    });

            Preference mdeco = findPreference("deco_button");
            Preference mDeleteUser = findPreference("delete_button");

            mdeco.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    mDecoDialog.show();
                    return true;
                }
            });

            mDeleteUser.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    mDeleteDialog.show();
                    return true;
                }
            });
        }

    }
}