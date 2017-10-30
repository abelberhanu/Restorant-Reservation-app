package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import db.management.DataFlow;
import polito.lab.anes.eatnow.R;
import polito.lab.anes.eatnow.activity.RestaurantProfileActivity;


public class RestaurantMenuFragment extends Fragment implements View.OnClickListener {
    //View Ref
    private LinearLayout myOwnFragView;
    public LayoutInflater myOwnCustomCardInflater = null;
    public LayoutInflater myOwnSimpleElementInflater = null;
    private ViewGroup myContainer= null;

    //Process Ref
    private DataFlow df;
    private JSONObject myMenu;
    private JSONArray menuList;
    private String restaurantBaasId;
    public int counterOfCardView = 0;
    private final  String addDishBTnTag = "addDishBTn";
    private final  String remDishBTnTag = "remDishBTn";
    private final int ADD_ITEM = 1;
    private final int REM_ITEM = 0;
    public HashMap<String,List<String>> mWishList = null;
    private String theStringWish;
    private String mQtyList = "";
    private HashMap<String,Integer> hWishList;
    private HashMap<String,Integer> hQtyList;
    private RestaurantProfileActivity parentRPA;


    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        df =  new DataFlow();
        restaurantBaasId = getArguments().getString("restaurantBaasId");
        getMenu();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myOwnFragView = (LinearLayout) inflater.inflate(R.layout.fragment_restaurant_menu, container, false);
        myContainer = container;
        parentRPA = (RestaurantProfileActivity) getActivity();
        mWishList = parentRPA.mWishList;
        return myOwnFragView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getTheView();
        fillQtyViews();
        ArrayList<View> alvAdd = getViewsByTag(myContainer,"addDishBTn");
        ArrayList<View> alvRem = getViewsByTag(myContainer,"remDishBTn");
        for(View addImgBtn:alvAdd){
            ((ImageButton) addImgBtn).setOnClickListener(this);
        }
        for(View remImgBtn:alvRem){
            ((ImageButton) remImgBtn).setOnClickListener(this);
        }


    }

    private void getMenu(){
        String restaurantIntel = df.readFromFile(getContext(),restaurantBaasId + ".txt");
        try {
            JSONObject jo = new JSONObject(restaurantIntel);
            myMenu = jo.getJSONObject("menu");
            menuList = jo.getJSONArray("listMenu");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getTheView()  {
        if(menuList != null){
            for(int i = 0 ; i < menuList.length() ; i++){
                CardView cv = getNewCardView();
                try {
                    String categoryTitle = menuList.getString(i);
                    setTitleCategorieCv(cv,categoryTitle);
                    getListOfDishes(cv,(ViewGroup)cv,categoryTitle);
                    myOwnFragView.addView(cv);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void getListOfDishes(CardView cd, ViewGroup vg, String categoryTitle) throws JSONException {

        JSONArray itemsOfCategory = getItemOfCategory(categoryTitle);
        LinearLayout llOfListDishes = (LinearLayout) cd.findViewWithTag("llForIncludeRestaurantDishesItem");
        if(itemsOfCategory != null){
            for(int i = 0 ; i < itemsOfCategory.length() ; i++){
                String value = itemsOfCategory.getString(i);
                LinearLayout ll =  getSimpleElementWithValues(vg,value);
                llOfListDishes.addView(ll);
            }
        }
    }

    public LinearLayout getSimpleElementWithValues(ViewGroup vg, String value) throws JSONException {
        LinearLayout simpleElementLl = getDishItemView(vg);
        fillSimpleElement(simpleElementLl,value);

        return simpleElementLl;
    }


    public JSONArray getItemOfCategory(String categoryTitle) throws JSONException {
        return myMenu.getJSONArray(categoryTitle);
    }
    public void fillSimpleElement(LinearLayout llToFill, String value) throws JSONException {

        TextView dishName  = (TextView) llToFill.findViewWithTag("dishName");
        TextView dishDescription  = (TextView) llToFill.findViewWithTag("dishDescription");
        TextView priceTxV  = (TextView) llToFill.findViewWithTag("priceTxV");
        TextView priceDetailsTxV  = (TextView) llToFill.findViewWithTag("priceDetailsTxV");

        String priceValue = "";
        String name = "";
        String description = "";
        String priceDetails = "";

        String[] values = value.split(":::");
        if(values.length == 2){
            name = values[0].trim();
            dishName.setText(name);
            priceValue = values[1].trim();
            priceTxV.setText(priceValue);
        }else if(values.length == 3){
            name = values[0].trim();
            dishName.setText(name);
            priceValue = values[1].trim();
            description = values[2].trim();
            if(description.contains("multiplePrice{")){
                description = description.replace("multiplePrice{","").replace("}","");
                String[] descriptionArray = description.split("---");
                if(priceValue.equals("0")) priceValue = "";
                for(int i = 0 ; i < descriptionArray.length ; i++){
                    String[] subDescription = descriptionArray[i].split("--");
                    priceDetails += subDescription[0];
                    priceValue += subDescription[1];
                    if(i + 1 < descriptionArray.length){
                        priceDetails += "-";
                        priceValue += "-";
                    }
                }
                priceTxV.setText(priceValue);
                priceDetailsTxV.setText(priceDetails);
            }else{
                priceTxV.setText(priceValue);
                dishDescription.setText(description);
            }
        }

    }

    private CardView getNewCardView(){
        myOwnCustomCardInflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        CardView cv = (CardView) myOwnCustomCardInflater.inflate(R.layout.restaurant_menu_item,null);
        cv.setId(counterOfCardView++);
        return cv;
    }
    public LinearLayout getDishItemView(ViewGroup vg){
        myOwnSimpleElementInflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        LinearLayout ll = (LinearLayout) myOwnSimpleElementInflater.inflate(R.layout.list_of_dishes_layout,vg,false);
        return ll;
    }

    private void setTitleCategorieCv(CardView cv,String title){
        TextView titleTxV = (TextView) cv.findViewWithTag("titleDishTxV");
        titleTxV.setText(capitalize(title.replaceAll("_"," ")));
    }
    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
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

    private void updateQty(View v,int action){
        TextView itemQty = null;
        int test = 0;
        while(itemQty == null && test < 5){
            itemQty = (TextView) v.findViewWithTag("itemQty");
            v = (View) v.getParent();
            test++;
        }
        if(itemQty != null){
            String t = (String) itemQty.getText();
            int actual_qty = 0;
            if(t != null && t !="null"){
                actual_qty = Integer.valueOf(t);
            }
            switch (action){
                case ADD_ITEM :
                    actual_qty++;
                    itemQty.setText(String.valueOf(actual_qty));
                    break;
                case REM_ITEM :
                    if(actual_qty != 0){
                        actual_qty--;
                        itemQty.setText(String.valueOf(actual_qty));
                    }
                    break;
            }
        }
    }
    private void fillQtyViews(){
        if(!mWishList.isEmpty()){
            ArrayList<View> alvDishName = getViewsByTag(myContainer,"dishName");
            if(!alvDishName.isEmpty()){
                for(View txv:alvDishName){
                    View txvIt = txv;
                    String dishName = ((TextView) txv).getText().toString().trim();
                    if(mWishList.containsKey(dishName)){
                        TextView itemQty = null;
                        while(itemQty == null){
                            itemQty = (TextView) txvIt.findViewWithTag("itemQty");
                            txvIt = (View) txvIt.getParent();
                        }
                        List<String> l = mWishList.get(dishName);
                        int lsize = l.size();
                        itemQty.setText(String.valueOf(lsize));
                    }
                }
            }
        }

    }
    private void addItem(String dishName,String strPrice){
        if(mWishList.containsKey(dishName)){
            List<String> l = mWishList.get(dishName);
            l.add(strPrice);
            mWishList.put(dishName,l);
        }else{
            List<String> l = new ArrayList<>();
            l.add(strPrice);
            mWishList.put(dishName,l);
        }
    }

    private void removeItem(String dishName){
        if(mWishList.containsKey(dishName)){
            List<String> l = mWishList.get(dishName);
            l.remove(l.size() - 1);
            if(l.isEmpty()){
                mWishList.remove(dishName);
            }else{
                mWishList.put(dishName,l);
            }
        }
    }

    @Override
    public void onClick(View v) {
        LinearLayout parent = (LinearLayout) v.getParent().getParent().getParent();
        TextView dishNameTxV = (TextView) parent.findViewWithTag("dishName");
        TextView priceTxV = (TextView) parent.findViewWithTag("priceTxV");
        String dishName = (String) dishNameTxV.getText();
        String strPrice = (String) priceTxV.getText();
        if(strPrice.contains("-")){
            strPrice = strPrice.split("-")[0];
        }
        switch ((String)v.getTag()) {
            case addDishBTnTag :
                addItem(dishName,strPrice);
                updateQty((View) v.getParent().getParent(),ADD_ITEM);
                Toast.makeText(getActivity(),"Added to selection", Toast.LENGTH_SHORT).show();
                break;
            case remDishBTnTag :
                if(mWishList.containsKey(dishName)){
                    removeItem(dishName);
                    updateQty((View) v.getParent().getParent(),REM_ITEM);
                    Toast.makeText(getActivity(),"Removed from selection", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"This item is not in your selection", Toast.LENGTH_SHORT).show();
                }

                // TODO : function to manage favorite (getlist, check unicity, add, save file)
                break;
        }
    }
}
