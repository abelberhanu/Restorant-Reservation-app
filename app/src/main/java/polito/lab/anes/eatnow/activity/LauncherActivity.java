package polito.lab.anes.eatnow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import org.json.JSONException;
import org.json.JSONObject;

import db.management.DataFlow;
import login.LoginManagement;
import polito.lab.anes.eatnow.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LauncherActivity extends AppCompatActivity {
    private LoginManagement lm = new LoginManagement();
    private BaasBox client;
    private JSONObject jsonObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launcher);


        //**************************************************** BAASBOX
        BaasBox.Builder b =
                new BaasBox.Builder(this);
        client = b.setApiDomain(getResources().getString(R.string.endpointBaasbox))
                .setPort(9000)
                .setAppCode(getResources().getString(R.string.appcodeBaasbox))
                .init();

        //**************************************************
        checkLastUser();

    }

    public void checkLastUser(){
        final String lastUserEmail;
        final String lastUserPass;
        final String lastUserType;

        // TODO :  get the last user login info from file in the phone, try to connect :
            //TODO : if connect ok : open main Activity OWNER or USER with info
            //TODO : if connect not ok : open Login Activity
        try {
            String lastUserInfoSimple = lm.fetchLastUser(getApplicationContext());

                 if(lastUserInfoSimple != null && lastUserInfoSimple != ""){
                     lastUserEmail= lastUserInfoSimple.split(":::")[0];
                     lastUserPass = lastUserInfoSimple.split(":::")[1];
                     lastUserType = lastUserInfoSimple.split(":::")[2];
                     BaasUser user = BaasUser.withUserName(lastUserEmail)
                             .setPassword(lastUserPass);
                     user.login(new BaasHandler<BaasUser>() {
                         @Override
                         public void handle(BaasResult<BaasUser> result) {
                             Intent launcherIntent = new Intent();
                             if (result.isSuccess()) {
                                 String tmp = BaasUser.current().toString();
                                 try {
                                     tmp = tmp.replace("BaasUser{","{");
                                     jsonObj = new JSONObject(tmp);
                                     int i = 0;
                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }
                                     switch (lastUserType) {
                                     case "customer" :
                                         launcherIntent = new Intent(LauncherActivity.this, MainActivity.class);
                                         break;
                                     case "owner"    :
                                         launcherIntent = new Intent(LauncherActivity.this, OwnerMainActivity.class);
                                         break;
                                     case "guest"    :
                                         launcherIntent = new Intent(LauncherActivity.this, MainActivity.class);
                                         break;
                                     default:
                                         launcherIntent = new Intent(LauncherActivity.this, MainActivity.class);
                                         break;
                                 }
                                 Log.d("LOG", "The user is currently logged in: " + result.value());
                             } else {
                                 launcherIntent = new Intent(LauncherActivity.this, LoginActivity.class);
                                 Log.e("LOG", "Show error", result.error());
                             }
                             launcherIntent.putExtra("type", lastUserType);
                             String toSave = "{" +
                                     "\"email\":\"" + lastUserEmail + "\"," +
                                     "\"passcode\":\"" + lastUserPass + "\"," +
                                     "\"accountType\":\"" + lastUserType + "\"" +
                                     "}";
                             launcherIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                             lm.saveLastUser(toSave,getApplicationContext());
                             DataFlow df = new DataFlow();
                             df.writeToFile(getApplicationContext(),lastUserType,"lastUserAccountType");
                             startActivity(launcherIntent);
                         }
                     });
                 }else{
                     Intent launcherIntent = new Intent();
                     launcherIntent = new Intent(LauncherActivity.this, LoginActivity.class);
                     startActivity(launcherIntent);
                 }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
