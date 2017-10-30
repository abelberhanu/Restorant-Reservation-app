package polito.lab.anes.eatnow.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import fragment.MenuOwnerFragment;
import fragment.MenudishesFragment;
import fragment.ReservationFragment;
import polito.lab.anes.eatnow.R;

public class RestaurantOwnerActivity extends AppCompatActivity {
    private BottomBar mBottomBar;
    private Typeface faceLight;
    private FrameLayout myScrollingContent;
    private android.support.v4.app.FragmentManager fm;
    private MenudishesFragment newdishesFragment;
    private MenuOwnerFragment menuFragmentView;
    private ReservationFragment reservationFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_owner);
        faceLight = Typeface.createFromAsset(getAssets(),"fonts/Dolce_Vita_Light.ttf");
        myScrollingContent = (FrameLayout) findViewById(R.id.restaurantOwnerScrollingContent);
        mBottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.restaurantOwnerCoordinator), findViewById(R.id.restaurantOwnerScrollingContent),savedInstanceState);
        mBottomBar.setItemsFromMenu(R.menu.owner_restaurant_view_menu, new MenuClass());
        if(faceLight != null) mBottomBar.setTypeFace("Dolce_Vita_Light.ttf");
        setColors(mBottomBar);

    }

    public void setColors(BottomBar mBottomBar){
        // Setting colors for different tabs when there's more than three of them.
        // You can set colors for tabs in three different ways as shown below.
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.all_color_accent));
        mBottomBar.mapColorForTab(1, ContextCompat.getColor(this, R.color.all_color_accent));
        mBottomBar.mapColorForTab(2, ContextCompat.getColor(this, R.color.all_color_accent));
    }

    public void displayFragment(int id){
        FragmentTransaction ft = fm.beginTransaction();
        switch (id){
            case R.id.ownerMenu :
                if(menuFragmentView == null){
                    menuFragmentView = new MenuOwnerFragment();
                    ft.add(R.id.restaurantOwnerFrameLayout, menuFragmentView);
                    ft.commit();
                }else{
                    switchContent(R.id.restaurantOwnerFrameLayout,menuFragmentView);
                    ft.commit();
                }
                break;
            case R.id.reservationMenu :
                if(reservationFragment == null){
                    reservationFragment = new ReservationFragment();
                    ft.add(R.id.restaurantOwnerFrameLayout, reservationFragment);
                    ft.commit();
                    switchContent(R.id.restaurantOwnerFrameLayout,reservationFragment);
                }else{
                    switchContent(R.id.restaurantOwnerFrameLayout,reservationFragment);
                    ft.commit();
                }
                break;
            case R.id.newDishes :
                if(newdishesFragment == null){
                    newdishesFragment = new MenudishesFragment();
                    ft.add(R.id.restaurantOwnerFrameLayout, newdishesFragment);
                    ft.commit();
                    switchContent(R.id.restaurantOwnerFrameLayout,newdishesFragment);
                }else{
                    switchContent(R.id.restaurantOwnerFrameLayout,newdishesFragment);
                    ft.commit();
                }
                break;
        }
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
