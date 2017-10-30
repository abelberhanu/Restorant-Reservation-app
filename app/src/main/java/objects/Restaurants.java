package objects;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import polito.lab.anes.eatnow.R;

/**
 * Created by nowus on 6/9/2016.
 */
public class Restaurants {
    private String address;
    private String phone;
    private String website;
    private String description;
    private String menuStr;
    private String name;
    private JSONObject restaurantJson;

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    private String profilePic;


    public String getMenuStr() {
        return menuStr;
    }

    public void setMenuStr(String menuStr) {
        this.menuStr = menuStr;
    }



    public Restaurants(){
    }

    public Restaurants(JSONObject jo){


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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JSONObject getRestaurantJson() {
        return restaurantJson;
    }

    public void setRestaurantJson(JSONObject restaurantJson) {
        this.restaurantJson = restaurantJson;
    }

    public void createMenuStr() throws JSONException {
        String strtmp = "";
        JSONObject menujson = restaurantJson.getJSONObject("menu");
        if(menujson != null){
            Iterator<String> it = menujson.keys();
            while(it.hasNext()){
                String section = it.next();
                strtmp += section.replace("_"," ") + " :\n";
                JSONArray sectionJA = menujson.getJSONArray(section);
                if(sectionJA != null){
                    for (int i = 0 ; i < sectionJA.length() ; i++){
                        String dishToProcess = sectionJA.getString(i);
                        String dishname = "";
                        if(dishToProcess.split(":::")[0] != null) dishname = dishToProcess.split(":::")[0].trim();
                        String dishprice = "";
                        if(dishToProcess.split(":::")[1] != null) dishprice = dishToProcess.split(":::")[1].trim() + " €";
                        String dishdescription = "";
                        if(dishToProcess.split(":::")[2] != null) dishdescription = dishToProcess.split(":::")[2].trim();
                        strtmp += dishname + " \t" + dishprice + "\n" + dishdescription + "\n";


                    }
                }
            }
        }
        this.setMenuStr(strtmp);


    }


    public void createRestaurantsFromView(View vCivil, View vMenu) throws JSONException {
        ArrayList<View> sectionAL;
        ArrayList<View> dishAL;
        ViewGroup dishItemsView;
        EditText sectionTitlesExV;
        JSONObject restaurantJO = new JSONObject();
        JSONObject menuJo = new JSONObject();
        JSONArray listMenu = new JSONArray();
        if( vMenu != null){
                sectionAL = getViewsByTag((ViewGroup) vMenu,"section");
                if(sectionAL.size() > 0){
                    for (View s:sectionAL){
                        sectionTitlesExV = (EditText) s.findViewWithTag("sectionTitleExV");
                        if(sectionTitlesExV != null ){
                            String sectionTitle = String.valueOf(sectionTitlesExV.getText()).replace(" ","_").trim(); //get name of section
                            if(sectionTitle != ""){
                                listMenu.put(sectionTitle); // put the name of the section in listMenu object
                                JSONArray sectionTitleJA = new JSONArray();
                                dishAL = getViewsByTag((ViewGroup)s, "dish");
                                if(dishAL != null){
                                    for (View dish:dishAL){
                                        String dishName = String.valueOf(((EditText)dish.findViewWithTag("dishNameExV")).getText()).trim();
                                        String dishPrice = String.valueOf(((EditText)dish.findViewWithTag("dishPriceExV")).getText()).trim();
                                        String dishDescription = String.valueOf(((EditText)dish.findViewWithTag("dishDescriptionExV")).getText()).trim();
                                        sectionTitleJA.put(dishName + " ::: " + dishPrice + " ::: " + dishDescription); // put the dish in section object
                                    }
                                }
                                menuJo.put(sectionTitle,sectionTitleJA); // put the section in menu object

                            }

                        }
                    }
                }
                restaurantJO.put("menu",menuJo);
                restaurantJO.put("listMenu",listMenu);
        }
        if(vCivil!= null){
            LinearLayout civilLL = null;
            while(civilLL == null){
                civilLL = (LinearLayout) vCivil.findViewById(R.id.fragmentStep1);
                vCivil = (View) vCivil.getParent();
            }

            this.setAddress(String.valueOf(((EditText) civilLL.findViewById(R.id.addressRestaurant)).getText()).trim());
            this.setDescription(String.valueOf(((EditText) civilLL.findViewById(R.id.descriptionRestaurant)).getText()).trim());
            this.setPhone(String.valueOf(((EditText) civilLL.findViewById(R.id.phoneRestaurant)).getText()).trim());
            this.setWebsite(String.valueOf(((EditText) civilLL.findViewById(R.id.websiteRestaurant)).getText()).trim());
            this.setName(String.valueOf(((EditText) civilLL.findViewById(R.id.restaurantName)).getText()).trim());

            restaurantJO.put("name",getName());
            restaurantJO.put("description",getDescription());
            restaurantJO.put("address",getAddress());
            restaurantJO.put("phone_number",getPhone());
            restaurantJO.put("website",getWebsite());

            this.setRestaurantJson(restaurantJO);

            createMenuStr();



        }
    }



    private static ArrayList<View> getViewsByTag(ViewGroup root, String tag){
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }
}
