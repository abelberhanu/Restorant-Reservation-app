package login;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import org.json.JSONException;
import org.json.JSONObject;

import db.management.DataFlow;
import polito.lab.anes.eatnow.R;
import polito.lab.anes.eatnow.activity.LoginActivity;

/**
 * Created by nowus on 5/4/2016.
 */
public class LoginManagement {
    private static SecureData sd = new SecureData();
    private static DataFlow df = new DataFlow();


    public LoginManagement() {
    }

    public void login(BaasUser user){
        // TODO : implement function which will manage to log in the account
        user.login(new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> result) {
                if (result.isSuccess()) {
                    Log.d("LOG", "The user is currently logged in: " + result.value());
                } else {
                    Log.e("LOG", "Show error", result.error());
                }
            }
        });
    }

    public void logout(Context context){
        // TODO : implement function which will manage to log out of the account
        final Context ctx = context;
        BaasUser.current().logout(new BaasHandler<Void>() {
            @Override
            public void handle(BaasResult<Void> result) {
                if (result.isSuccess()) {
                    Log.d("LOG", "Logged out: " + (BaasUser.current() == null));
                    saveLastUser("", ctx);
                    Intent i = new Intent(ctx, LoginActivity.class);
                    // set the new task and clear flags
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    ctx.getApplicationContext().startActivity(i);
                } else {
                    Log.e("LOG", "Show error", result.error());
                }
            }

            ;
        });
    }

    public void signup(){
        // TODO : implement function which will manage to register a new user
    }

    public void createProfileFromView(View v){
        // TODO : implement function which will manage to create the profile Locally and on the Web Server
        BaasUser bu = BaasUser.current();
        if(v != null){
            TextView accountTypeProfileTxV = (TextView) v.findViewById(R.id.accountTypeProfileTxV);
            EditText nameProfileTxV = (EditText) v.findViewById(R.id.nameProfileTxV);
            EditText fnameProfileTxV = (EditText) v.findViewById(R.id.fnameProfileTxV);
            EditText emailProfileTxV = (EditText) v.findViewById(R.id.emailProfileTxV);
            EditText phoneProfileTxV = (EditText) v.findViewById(R.id.phoneProfileTxV);
            EditText bdayProfileTxV = (EditText) v.findViewById(R.id.bdayProfileTxV);
            String accountType = "";
            String nameProfile = "";
            String fnameProfile = "";
            String emailProfile = "";
            String phoneProfile = "";
            String bdayProfile = "";
            if(accountTypeProfileTxV != null){
                accountType = accountTypeProfileTxV.getText().toString().replace("Account Type : ","");
            }
            if(nameProfileTxV != null){
                nameProfile = nameProfileTxV.getText().toString();
            }
            if(fnameProfileTxV != null){
                fnameProfile = fnameProfileTxV.getText().toString();
            }
            if(emailProfileTxV != null){
                emailProfile = emailProfileTxV.getText().toString();
            }
            if(phoneProfileTxV != null){
                phoneProfile = phoneProfileTxV.getText().toString();
            }
            if(bdayProfileTxV != null){
                bdayProfile = bdayProfileTxV.getText().toString();
            }

            bu.getScope(BaasUser.Scope.PRIVATE).put("accountType",accountType);
            bu.getScope(BaasUser.Scope.PRIVATE).put("name",nameProfile);
            bu.getScope(BaasUser.Scope.PRIVATE).put("fname", fnameProfile);
            bu.getScope(BaasUser.Scope.PRIVATE).put("email", emailProfile);
            bu.getScope(BaasUser.Scope.PRIVATE).put("phone", phoneProfile);
            bu.getScope(BaasUser.Scope.PRIVATE).put("bday", bdayProfile);
        }

    }

    public void deleteProfile(){
        // TODO : implement function which will manage to delete the profile Locally and on the Web Server
    }

    private void retrieveProfile(String username){
        // TODO : implement function which will manage to get the profile from the Web Server

    }

    public void uploadProfile(){
        // TODO : implement function which will manage to save the profile on the Web Server
        BaasUser.current().save(new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> res) {
                if(res.isSuccess()) {
                    Log.d("LOG", "User data has been saved");
                } else {
                    Log.e("LOG",res.error().toString());
                }
            }
        });
    }

    public void saveProfile(BaasUser bu,Context ctx){
        // TODO : implement function which will manage to save the profile Locally
        String toSave = bu.toString().replace("BaasUser{","{");
        String filename = "userProfileBaas.json";
        df.writeToFile(ctx,toSave,filename);
    }
    public void saveLastUser(String lastInfoSimplified, Context ctx){
        // TODO : implement function which will manage to save the last user profile Locally
        String filename = "userFile.txt";
        df.writeToFile(ctx,lastInfoSimplified,filename);
    }
    public String fetchLastUser(Context ctx) throws JSONException {
        String filename = "userFile.txt";
        String lastInfoEncrypt = df.readFromFile(ctx, filename);
//        lastInfoEncrypt = sd.decrypt(filename.getBytes(),"anes");
        if(lastInfoEncrypt != ""){
            JSONObject obj = new JSONObject(lastInfoEncrypt);
            return obj.getString("email") + ":::" + obj.getString("passcode") + ":::" + obj.getString("accountType");
        }else{
            return null;
        }
    }

    public String stringToSave(BaasUser bu){
        return "{" +
                "\"email\":\"" + bu.getName() + "\"," +
                "\"passcode\":\"" + bu.getPassword() + "\"," +
                "\"accountType\":\"" + bu.getScope(BaasUser.Scope.PRIVATE).getString("accountType") + "\"" +
                "}";
    }

    public void saveBaasUser(){
        BaasUser user = BaasUser.current();
        user.save(new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> res) {
                if(res.isSuccess()) {
                    Log.d("LOG", "User data has been saved");
                } else {
                    Log.e("LOG", String.valueOf(res.error()));
                }
            }
        });
    }
}
