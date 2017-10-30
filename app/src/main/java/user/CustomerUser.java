package user;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by nowus on 20/04/2016.
 */
public class CustomerUser extends UserClass {
    private Double rating;
    private ArrayList<String> favorites;
    private String accountType = "customer";

    public CustomerUser(){
        super();
    }

    public CustomerUser( String passcode, String name, String fname, String email, String phone, String gender, String address, Double rating, Uri profilePic,ArrayList<String> favorites) {
        super(passcode, name, fname, email, phone, gender, address, profilePic);
        setFavorites(favorites);
        setRating(rating);
    }

    public CustomerUser(String email,  String hashPasscode, String userId) {
        super(email, hashPasscode, userId);
    }

    public ArrayList<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayList<String> favorites) {
        this.favorites = favorites;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
