package objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nowus on 20/04/2016.
 */
public class Places {
    public String name;
    public String address;
    public Double lon;
    public Double lat;
    public String type;
    public String description;
    public String website;
    public String phoneNumber;
    public Double rating;
    public URI coverPicture;
    public ArrayList<Object> menu;
    public JSONArray starters;
    public JSONObject main_course;
    public JSONArray deserts;
    public JSONObject drinks;
    private JSONArray listMenu;
    private JSONObject simpleMenu;


    public Places() {

    }

    public Places(JSONObject jo) {
            try{
                this.name = jo.getString("name");
            }catch(JSONException e){

            }
            try{
                this.address = jo.getString("address");
            }catch(JSONException e){

            }
            try{
                this.lon = jo.getDouble("lon");
            }catch(JSONException e){

            }
            try{
                this.lat = jo.getDouble("lat");
            }catch(JSONException e){

            }
            try{
                this.type = jo.getString("type");
            }catch(JSONException e){

            }
            try{
                this.description = jo.getString("description");
            }catch(JSONException e){

            }
            try{
                this.website = jo.getString("website");
            }catch(JSONException e){

            }
            try{
                this.phoneNumber = jo.getString("phone_number");
            }catch(JSONException e){

            }
            try{
                this.rating = jo.getDouble("rating");
            }catch(JSONException e){

            }
            try{
                this.listMenu = jo.getJSONArray("listMenu");
            }catch(JSONException e){

            }
            try{
                this.simpleMenu = jo.getJSONObject("menu");
            }catch(JSONException e){

            }


    }

    public Places(String name, String address, Double lon, Double lat, String type, String description, String website, String phoneNumber, Double rating, JSONArray starters, JSONObject main_course, JSONArray deserts, JSONObject drinks) {
        this.name = name;
        this.address = address;
        this.lon = lon;
        this.lat = lat;
        this.type = type;
        this.description = description;
        this.website = website;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
        this.starters = starters;
        this.main_course = main_course;
        this.deserts = deserts;
        this.drinks = drinks;
        this.menu = new ArrayList<Object>();
        this.menu.add(starters);
        this.menu.add(main_course);
        this.menu.add(deserts);
        this.menu.add(drinks);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<Object> getMenu() {
        return menu;
    }

    public void setMenu(ArrayList<Object> menu) {
        this.menu = menu;
    }

    public JSONArray getStarters() {
        return starters;
    }

    public void setStarters(JSONArray starters) {
        this.starters = starters;
    }

    public JSONObject getMain_course() {
        return main_course;
    }

    public void setMain_course(JSONObject main_course) {
        this.main_course = main_course;
    }

    public JSONArray getDeserts() {
        return deserts;
    }

    public void setDeserts(JSONArray deserts) {
        this.deserts = deserts;
    }

    public JSONObject getDrinks() {
        return drinks;
    }

    public void setDrinks(JSONObject drinks) {
        this.drinks = drinks;
    }

}
