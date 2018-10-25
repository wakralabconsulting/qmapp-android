package com.qatarmuseums.qatarmuseumsapp.culturepass;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.profile.ProfileActivity;
import com.qatarmuseums.qatarmuseumsapp.profile.ProfileDetails;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CulturePassActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private View backArrow;
    private Animation iconZoomOutAnimation, zoomOutAnimation;
    private View becomeMember, loginButton, dialogLoginButton;
    Util util;
    private Dialog loginDialog;
    private LayoutInflater layoutInflater;
    private ImageView closeBtn;
    private EditText mUsernameView, mPasswordView;
    private ProgressBar mProgressView;
    private FrameLayout mLoginFormView;
    private UserLoginTask mAuthTask = null;
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "haithembahri:saliha", "foo:bar"
    };
    private TextInputLayout mPasswordViewLayout, mUsernameViewLayout;
    private TextView forgotPassword;
    private int REQUEST_CODE = 123;
    private String language;
    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_culture_pass);
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        becomeMember = findViewById(R.id.become_a_member_btn);
        loginButton = findViewById(R.id.login_btn);
        setSupportActionBar(toolbar);
        util = new Util();
        backArrow = findViewById(R.id.toolbar_back);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        iconZoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        backArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        backArrow.startAnimation(iconZoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        becomeMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                util.showComingSoonDialog(CulturePassActivity.this, R.string.coming_soon_content);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });
        becomeMember.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        becomeMember.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        loginButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        loginButton.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });

    }

    public void performLogin(int appLanguage) {
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService = APIClient.getClient().create(APIInterface.class);
        Call<ProfileDetails> call = apiService.generateToken(language, new LoginData(
                username, password));
        call.enqueue(new Callback<ProfileDetails>() {

            @Override
            public void onResponse(Call<ProfileDetails> call, Response<ProfileDetails> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("TOKEN : ", response.body().getToken());
                        Toast.makeText(CulturePassActivity.this, "TOKEN : " +
                                response.body().getToken(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileDetails> call, Throwable t) {
                Toast.makeText(CulturePassActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }

        });
    }

    protected void showLoginDialog() {
        loginDialog = new Dialog(this, R.style.DialogNoAnimation);
        loginDialog.setCancelable(true);
        loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.login_popup, null);
        loginDialog.setContentView(view);
        closeBtn = view.findViewById(R.id.close_dialog);
        dialogLoginButton = view.findViewById(R.id.dialog_login_button);
        dialogLoginButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dialogLoginButton.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        dialogLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
                hideSoftKeyboard(view);
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginDialog.dismiss();

            }
        });
        mUsernameView = view.findViewById(R.id.username);
        mPasswordView = view.findViewById(R.id.password);
        mPasswordViewLayout = view.findViewById(R.id.password_text_input_layout);
        mUsernameViewLayout = view.findViewById(R.id.username_text_input_layout);
        forgotPassword = view.findViewById(R.id.forgot_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    hideSoftKeyboard(textView);
                    return true;
                }
                return false;
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                util.showComingSoonDialog(CulturePassActivity.this, R.string.coming_soon_content);
            }
        });
        mUsernameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (text.length() > 0) {
                    mUsernameViewLayout.setError(null);
                } else {

                    mUsernameViewLayout.setError(getString(R.string.error_username_required));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (text.length() > 0) {
                    mPasswordViewLayout.setError(null);
                } else {
                    mPasswordViewLayout.setError(getString(R.string.error_password_required));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mLoginFormView = view.findViewById(R.id.login_form);
        mProgressView = view.findViewById(R.id.login_progress);
        loginDialog.show();
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameViewLayout.setError(null);
        mPasswordViewLayout.setError(null);

        // Store values at the time of the login attempt.
        username = mUsernameView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            mUsernameViewLayout.setError(getString(R.string.error_username_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordViewLayout.setError(getString(R.string.error_password_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            // Commented for API
//            showProgress(true);
//            mAuthTask = new UserLoginTask(email, password);
//            mAuthTask.execute((Void) null);
            navigateToProfile();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                navigateToProfile();
            } else {
                mPasswordViewLayout.setError(getString(R.string.error_incorrect_credentials));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }

    public void navigateToProfile() {
        loginDialog.dismiss();
        Intent navigationIntent = new Intent(CulturePassActivity.this, ProfileActivity.class);
        startActivityForResult(navigationIntent, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123) {
            if (resultCode == RESULT_OK) {
                finish();
            } else {
                util.showNormalDialog(this, R.string.logout_success_message);
            }
        }
    }
}
