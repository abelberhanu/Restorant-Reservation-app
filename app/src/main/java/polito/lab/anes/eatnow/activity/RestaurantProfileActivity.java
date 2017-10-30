package polito.lab.anes.eatnow.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.util.HashMap;
import java.util.List;

import db.management.DataFlow;
import fragment.RestaurantInfoFragment;
import fragment.RestaurantMenuFragment;
import fragment.WishListFragment;
import polito.lab.anes.eatnow.R;

public class RestaurantProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private BottomBar mBottomBar;
    private ImageButton backButton;
    private FrameLayout myScrollingContentRestaurant;
    private TextView titleTxV;
    private android.support.v4.app.FragmentManager fm;
    private Fragment restaurantInfoFragment;
    private Fragment restaurantMenuFragment;
    private Fragment restaurantWhishListFragment;
    private int lastId;
    private String restaurantBaasId;
    private String restaurantName;
    private Typeface faceBold ;
    private Typeface faceLight;
    public HashMap<String,List<String>> mWishList;
    final private String actualRestaurantFilename = "actualRestaurantView.txt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile);
        faceBold = Typeface.createFromAsset(getAssets(),"fonts/Dolce_Vita_Heavy_Bold.ttf");
        faceLight = Typeface.createFromAsset(getAssets(),"fonts/Dolce_Vita_Light.ttf");
        mWishList = new HashMap<>();
        initialize(savedInstanceState);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtnRestaurantActivity:
                DataFlow df = new DataFlow();
                df.writeToFile(getApplicationContext(),"","wishList" + restaurantBaasId + ".txt");
                df.writeToFile(getApplicationContext(),"","mQtyList" + restaurantBaasId + ".txt");
                finish();
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            DataFlow df = new DataFlow();
            df.writeToFile(getApplicationContext(),"","wishList" + restaurantBaasId + ".txt");
            df.writeToFile(getApplicationContext(),"","mQtyList" + restaurantBaasId + ".txt");
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void initialize(Bundle savedInstanceState){
        restaurantBaasId = getIntent().getStringExtra("restaurantBaasId");
        DataFlow df = new DataFlow();
        df.writeToFile(getApplicationContext(),"","wishList" + restaurantBaasId + ".txt");
        restaurantName = getIntent().getStringExtra("restaurantName");
        backButton = (ImageButton) findViewById(R.id.backBtnRestaurantActivity);
        backButton.setOnClickListener(this);
        myScrollingContentRestaurant = (FrameLayout) findViewById(R.id.myScrollingContentRestaurant);
        mBottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.myCoordinatorRestaurant), findViewById(R.id.myScrollingContentRestaurant), savedInstanceState);
        mBottomBar.setItemsFromMenu(R.menu.menu_activity_restaurant, new MenuClass());
        setColors(mBottomBar);
        titleTxV = (TextView) findViewById(R.id.restaurantNameTxV);
        titleTxV.setTypeface(faceBold);
        titleTxV.setText(restaurantName);

    }
    public void setColors(BottomBar mBottomBar){
        // Setting colors for different tabs when there's more than three of them.
        // You can set colors for tabs in three different ways as shown below.
        mBottomBar.setActiveTabColor("#d60e1523");
        mBottomBar.setTypeFace("Dolce_Vita_Light.ttf");
    }
    private void displayFragment(int menuItemId){
        FragmentTransaction ft = fm.beginTransaction();
        Bundle args = new Bundle();
        switch (menuItemId){
            case R.id.bottomBarItemInfo :
                if(restaurantInfoFragment == null){
                    restaurantInfoFragment = new RestaurantInfoFragment();
                    args.putString("restaurantBaasId", restaurantBaasId);
                    restaurantInfoFragment.setArguments(args);
                }
                if(menuItemId != lastId){
                    switchContent(R.id.restaurantActivityFrameLayout,restaurantInfoFragment);
                    args.putString("restaurantBaasId", restaurantBaasId);
                    restaurantInfoFragment.setArguments(args);
                }else{
                    ft.add(R.id.restaurantActivityFrameLayout, restaurantInfoFragment);
                    args.putString("restaurantBaasId", restaurantBaasId);
                    restaurantInfoFragment.setArguments(args);
                }
                break;
            case R.id.bottomBarItemMenu :
                if(restaurantMenuFragment == null){
                    restaurantMenuFragment = new RestaurantMenuFragment();
                }
                if(menuItemId != lastId){
                    switchContent(R.id.restaurantActivityFrameLayout,restaurantMenuFragment);
                    args.putString("restaurantBaasId", restaurantBaasId);
                    restaurantMenuFragment.setArguments(args);
                }else{
                    ft.add(R.id.restaurantActivityFrameLayout, restaurantMenuFragment);
                    args.putString("restaurantBaasId", restaurantBaasId);
                    restaurantMenuFragment.setArguments(args);
                }
                break;
            case R.id.bottomBarItemWhishList :
                if(restaurantWhishListFragment == null){
                    restaurantWhishListFragment = new WishListFragment();
                }
                if(menuItemId != lastId){
                    switchContent(R.id.restaurantActivityFrameLayout,restaurantWhishListFragment);
                    args.putString("restaurantBaasId", restaurantBaasId);
                    restaurantWhishListFragment.setArguments(args);
                }else{
                    ft.add(R.id.restaurantActivityFrameLayout, restaurantWhishListFragment);
                    args.putString("restaurantBaasId", restaurantBaasId);
                    restaurantWhishListFragment.setArguments(args);
                }
                break;
        }
        lastId = menuItemId;
        ft.commit();
    }

    public void switchContent(int newFragId, Fragment newFrag) {
        getSupportFragmentManager().beginTransaction()
                .replace(newFragId, newFrag)
                .commit();
    }

    private class MenuClass implements OnMenuTabClickListener {
        @Override
        public void onMenuTabSelected(@IdRes int menuItemId) {
            if(fm == null) fm = getSupportFragmentManager();
            displayFragment(menuItemId);
        }

        @Override
        public void onMenuTabReSelected(@IdRes int menuItemId) {
            if (menuItemId == R.id.bottomBarItemFavorite) {
                // The user reselected item number one, scroll your content to top.
            }
        }
    }


}
