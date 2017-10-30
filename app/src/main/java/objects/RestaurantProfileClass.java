package objects;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.net.URI;

import polito.lab.anes.eatnow.R;

/**
 * Created by nowus on 24/04/2016.
 */
public class RestaurantProfileClass extends AppCompatActivity {
    private String restaurantName;
    private Double rating;
    private String address;
    private String description;
    private String phone;
    private URI coverPicture;
    private Places place;
    private Double lon;
    private Double lat;
    private String type;
    private String website;

    public RestaurantProfileClass(Places place) {
        this.place = place;
        fillValues();
    }

    private void fillValues(){
        setAddress(this.place.address);
        setDescription(this.place.description);
        setRestaurantName(this.place.name);
        setPhone(this.place.phoneNumber);
        setRating(this.place.rating);
        setLon(this.place.lon);
        setLat(this.place.lat);
        setType(this.place.type);
        setWebsite(this.place.website);
//        setCoverPicture(this.place.coverPicture);
    }

    public void fillView(View v,Context ctx){
        TextView restaurantAddressTxV = (TextView) v.findViewById(R.id.restaurantProAddressTxV);
        TextView restaurantPhoneTxV = (TextView) v.findViewById(R.id.restaurantProPhoneTxV);
        TextView restaurantDescriptionTxV = (TextView) v.findViewById(R.id.restaurantProDescriptionTxV);

        Typeface face=Typeface.createFromAsset(ctx.getAssets(),"fonts/Dolce_Vita_Light.ttf");

        restaurantAddressTxV.setText(getAddress());
        restaurantAddressTxV.setTypeface(face);

        restaurantPhoneTxV.setText(getPhone());

        restaurantDescriptionTxV.setText(getDescription());
    }


    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public URI getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(URI coverPicture) {
        this.coverPicture = coverPicture;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
