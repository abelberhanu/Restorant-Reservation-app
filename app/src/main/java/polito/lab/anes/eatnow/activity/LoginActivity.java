package polito.lab.anes.eatnow.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import java.util.ArrayList;
import java.util.List;

import db.management.DataFlow;
import de.hdodenhof.circleimageview.CircleImageView;
import login.LoginManagement;
import login.SecureData;
import polito.lab.anes.eatnow.R;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int BAD_PASSWORD = 1;
    private static final int ERR_SERV = 2;
    private static final int USERNAME_ALREADY_EXISTING = 3;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private UserRegisterTask mAuthTaskRegister = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private TextView errorLogTxV;
    private TextView guestTxVLogin;
    private CircleImageView civ;
    private LinearLayout llGuest;
    private View mProgressView;
    private View mLoginFormView;

    //Net ref

    private String baasboxErrorMessage;
    private String accountType ="guest";
    private LoginManagement lm = new LoginManagement();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        errorLogTxV = (TextView) findViewById(R.id.errorLogTxV);
        guestTxVLogin = (TextView) findViewById(R.id.guestTxVLogin);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Dolce_Vita.ttf");
        guestTxVLogin.setTypeface(tf);
        mPasswordView = (EditText) findViewById(R.id.password);
        llGuest = (LinearLayout) findViewById(R.id.guestLlLoginActivity);
        llGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectivity()){
                    accountType = "guest";
                    callActivity();
                }else{
                    mEmailView.requestFocus();
                    errorLogTxV.setText(R.string.error_connection);
                    errorLogTxV.setTextColor(Color.RED);
                }
            }
        });
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    try {
                        attemptLogin(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        Button mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attemptLogin(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attemptLogin(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(Boolean register) throws Exception {
        if (mAuthTask != null) {
            return;
        }
        if (mAuthTaskRegister != null && register) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            if(!register){
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);
            }else{
                mAuthTaskRegister = new UserRegisterTask(email, password);
                createDialog();
            }

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private boolean isConnected = false;
        private final SecureData sc = new SecureData();

        UserLoginTask(String email, String password) throws Exception {
            mEmail = email;
            mPassword = sc.getMySha256(password);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.



            BaasUser user = BaasUser.withUserName(mEmail)
                    .setPassword(mPassword);
            BaasResult<BaasUser> res = user.loginSync();
            if(res.isSuccess()){
                isConnected = true;
                Log.d("LOG", res.value().toString());
                accountType = res.value().getScope(BaasUser.Scope.PRIVATE).get("accountType");
                return true;
            }else if(!checkConnectivity()){
                // No connection
                return false;
            }else{
                // good connection BUT pad password err serv
                isConnected = true;
                baasboxErrorMessage = res.error().getMessage();
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                BaasUser bu = BaasUser.current();
                lm.saveLastUser(lm.stringToSave(bu),getApplicationContext());
                callActivity();
            } else {
                if(!isConnected){
                    //No connection
                    errorLogTxV.setText(R.string.error_connection);
                    errorLogTxV.setTextColor(Color.RED);
                }else{
                    switch(getError(baasboxErrorMessage)){
                        case BAD_PASSWORD:
                            mPasswordView.setError(getString(R.string.error_incorrect_password));
                            mPasswordView.requestFocus();
                            break;
                        case ERR_SERV:
                            errorLogTxV.setError(baasboxErrorMessage);
                            mPasswordView.requestFocus();
                            break;
                    }
                }

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }
    public int getError(String message){
        if(message.toLowerCase().contains("unauthorized")){
            return BAD_PASSWORD;
        }else if(message.toLowerCase().contains("error signing up")){
            return USERNAME_ALREADY_EXISTING;
        }else{
            return ERR_SERV;
        }
    }

    public void callActivity(){
        Intent intent = new Intent();
        String actualAccType;
        if(!accountType.equals("guest")){
            BaasUser bu = BaasUser.current();
            actualAccType = bu.getScope(BaasUser.Scope.PRIVATE).getString("accountType");
            intent.putExtra("actualUser",bu);
        }
        actualAccType = accountType;
        DataFlow df = new DataFlow();
        df.writeToFile(getApplicationContext(),actualAccType,"lastUserAccountType");

        switch(actualAccType){
            case "customer" :
                intent = new Intent(LoginActivity.this, MainActivity.class);
                break;
            case "owner"    :
                intent = new Intent(LoginActivity.this, OwnerMainActivity.class);
                break;
            case "guest"    :
                intent = new Intent(LoginActivity.this, MainActivity.class);
                break;
            default:
                intent = new Intent(LoginActivity.this, MainActivity.class);
                break;
        }
        startActivity(intent);

    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private boolean isConnected = false;
        private final SecureData sc = new SecureData();

        UserRegisterTask(String email, String password) throws Exception {
            mEmail = email;
            mPassword = sc.getMySha256(password);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.



            BaasUser user = BaasUser.withUserName(mEmail)
                    .setPassword(mPassword);
            user.getScope(BaasUser.Scope.PRIVATE).put("accountType",accountType);
            BaasResult<BaasUser> res = user.signupSync();
            if(res.isSuccess()){
                isConnected = true;
                res.value();
                return true;
            }else if(!checkConnectivity()){
                // No connection
                return false;
            }else{
                // good connection BUT pad password err serv
                isConnected = true;
                baasboxErrorMessage = res.error().getMessage();
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTaskRegister = null;
            showProgress(false);

            if (success) {
                BaasUser bu = BaasUser.current();
                lm.saveLastUser(lm.stringToSave(bu),getApplicationContext());
                callActivity();
            } else {
                if(!isConnected){
                    //No connection
                    errorLogTxV.setText(R.string.error_connection);
                    errorLogTxV.setTextColor(Color.RED);
                }else{
                    switch(getError(baasboxErrorMessage)){
                        case USERNAME_ALREADY_EXISTING:
                            mEmailView.setError(getString(R.string.error_email_already_existing));
                            mEmailView.requestFocus();
                            break;
                        case ERR_SERV:
                            errorLogTxV.setError(baasboxErrorMessage);
                            mPasswordView.requestFocus();
                            break;
                    }
                }

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTaskRegister = null;
            showProgress(false);
        }
    }


    private Boolean checkConnectivity(){
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnected();
    }


    private void createDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        alertDialogBuilder.setMessage("Who are you ? :)");
        LayoutInflater inflater = LoginActivity.this.getLayoutInflater();
        alertDialogBuilder
                .setView(inflater.inflate(R.layout.dialog_account_type, null))
                .setPositiveButton("Owner", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        accountType = "owner";
                        mAuthTaskRegister.execute((Void) null);
                        dialog.dismiss();
                    }
                });

        alertDialogBuilder.setNegativeButton("Customer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                accountType = "customer";
                mAuthTaskRegister.execute((Void) null);
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}

