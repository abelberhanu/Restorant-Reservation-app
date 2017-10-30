package user;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import java.io.Serializable;

import login.LoginManagement;
import login.SecureData;

/**
 * Created by nowus on 20/04/2016.
 */
public class UserClass extends AppCompatActivity implements Serializable {

    private String passcode;
    private String name;
    private String fname;
    private String email;
    private String phone;
    private String gender;
    private String address;
    private Uri    profilePic;
    private String userId;
    private String accountType;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Uri getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Uri profilePic) {
        this.profilePic = profilePic;
    }

    public  String getPasscode() {
        return passcode;
    }

    public void setPasscode( String passcode) {
        this.passcode = passcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UserClass(){
    }
    public UserClass( String passcode, String name, String fname, String email, String phone, String gender, String address, Uri profilePic) {
        this.passcode = passcode;
        this.name = name;
        this.fname = fname;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
        this.profilePic = profilePic;

    }

    public UserClass(String email,  String hashPasscode, String userId){
        this.email = email;
        this.passcode = hashPasscode;
        this.userId = userId;
    }



    @Override
    public String toString() {
        return "UserClass{" +
                "passcode='" + passcode + '\'' +
                ", name='" + name + '\'' +
                ", fname='" + fname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", accountType='" + accountType + '\'' +
                '}';
    }

    public void askWichTypeOfUser(){

    }
    public String simplifiedUserToJsonString(){
        return "{" +
                "\"email\":\"" + email + "\"," +
                "\"passcode\":\"" + passcode + "\"," +
                "\"accountType\":\"" + accountType + "\"" +
                "}";
    }

    public void changPassword(String newPassword) throws Exception {
        final BaasUser current = BaasUser.current();
        final SecureData sd = new SecureData();
        final String nwPwdHash = sd.getMySha256(newPassword);
        accountType = current.getScope(BaasUser.Scope.PRIVATE).getString("accountType");
        if(!accountType.equals("customer") && !accountType.equals("owner")) accountType = "guest" ;
        current.changePassword(nwPwdHash,new BaasHandler<Void>() {
            @Override
            public void handle(BaasResult<Void> baasResult) {
                LoginManagement lm = new LoginManagement();
                if(baasResult.isSuccess()) {
                    Log.d("LOG", "New password updated, you should relogin");
                    current.setPassword(nwPwdHash);
                    lm.login(current);
                } else {
                    Log.e("LOG","error",baasResult.error());
                }
                String toSave = "{" +
                        "\"email\":\"" + current.getName() + "\"," +
                        "\"passcode\":\"" + current.getPassword() + "\"," +
                        "\"accountType\":\"" + accountType + "\"" +
                        "}";
                lm.saveLastUser(toSave, getApplicationContext());
            }
        });
    }

}