package user;

import android.net.Uri;

/**
 * Created by nowus on 20/04/2016.
 */
public class OwnerUser extends UserClass{

    private String ownPlaceNameFile;
    private String accountType = "owner";

    public OwnerUser(){
        super();
    }

    public OwnerUser( String passcode, String name, String fname, String email, String phone, String gender, String address, String ownPlaceNameFile, Uri profilePic) {
        super(passcode, name, fname, email, phone, gender, address, profilePic);
        this.ownPlaceNameFile = ownPlaceNameFile;
    }

    public OwnerUser(String email,  String hashPasscode, String userId) {
        super(email, hashPasscode,userId);
    }

    @Override
    public String toString() {
        return "OwnerUser{" +
                "ownPlaceNameFile='" + ownPlaceNameFile + '\'' +
                '}';
    }
}
