package polito.lab.anes.eatnow.activity;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.baasbox.android.BaasUser;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import db.management.DataFlow;
import fragment.AllFragment;
import fragment.FavoriteFragment;
import fragment.ReservationFragment;
import fragment.NewsFeedFragment;
import fragment.NotLoggedProfileFragment;
import fragment.ProfileFragment;
import polito.lab.anes.eatnow.R;

public class MainActivity extends AppCompatActivity {
    private BottomBar mBottomBar;
    private android.support.v4.app.FragmentManager fm;
    private Fragment newsFragment;
    private Fragment profileFragment;
    private Fragment favoriteFragment;
    private Fragment reservationFragment;
    private Fragment allFragment;
    private int lastId;
    private String accountType;
    private FrameLayout myScrollingContent;
    private FrameLayout mainActivityFrameLayoutNoScroll;
    private static final String STATE_ID = "lastId";
    private Typeface faceLight;
    private BaasUser bu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            lastId = savedInstanceState.getInt(STATE_ID);
            bu = (BaasUser) savedInstanceState.get("baasUser");

        }
        DataFlow df = new DataFlow();
        accountType = df.readFromFile(getApplicationContext(),"lastUserAccountType");
        faceLight = Typeface.createFromAsset(getAssets(),"fonts/Dolce_Vita_Light.ttf");

        setContentView(R.layout.activity_main);


        myScrollingContent = (FrameLayout) findViewById(R.id.myScrollingContent);
        mainActivityFrameLayoutNoScroll = (FrameLayout) findViewById(R.id.mainActivityFrameLayoutNoScroll);
        mBottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.myCoordinator), findViewById(R.id.myScrollingContent),savedInstanceState);
        mBottomBar.setItemsFromMenu(R.menu.main, new MenuClass());
        if(faceLight != null) mBottomBar.setTypeFace("Dolce_Vita_Light.ttf");
        setColors(mBottomBar);

    }

    public void setColors(BottomBar mBottomBar){
        // Setting colors for different tabs when there's more than three of them.
        // You can set colors for tabs in three different ways as shown below.
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.all_color_accent));
        mBottomBar.mapColorForTab(1, ContextCompat.getColor(this, R.color.all_color_accent));
        mBottomBar.mapColorForTab(2, ContextCompat.getColor(this, R.color.all_color_accent));
        mBottomBar.mapColorForTab(3, ContextCompat.getColor(this, R.color.all_color_accent));
        mBottomBar.mapColorForTab(4, ContextCompat.getColor(this, R.color.profile_color_accent));
    }

    public void displayFragment(int id){
        FragmentTransaction ft = fm.beginTransaction();
        switch (id){
            case R.id.bottomBarItemNews :
                if(newsFragment == null){
                    newsFragment = new NewsFeedFragment();
                }
                if(id != lastId){
                    switchContent(R.id.mainActivityFrameLayout,newsFragment);
                }else{
                    ft.add(R.id.mainActivityFrameLayout, newsFragment);
                }
                if(lastId == R.id.bottomBarItemAll){
                    myScrollingContent.setVisibility(FrameLayout.VISIBLE);
                    Fragment f = getSupportFragmentManager().findFragmentByTag("first");
                    if( f!= null) ft.remove(f);
                    allFragment = null;
                    mainActivityFrameLayoutNoScroll.setVisibility(FrameLayout.GONE);
                }
                break;
            case R.id.bottomBarItemFavorite :
                if(favoriteFragment == null){
                    favoriteFragment = new FavoriteFragment();
                }
                if(id != lastId){
                    switchContent(R.id.mainActivityFrameLayout,favoriteFragment);
                }
                if(lastId == R.id.bottomBarItemAll){
                    myScrollingContent.setVisibility(FrameLayout.VISIBLE);
                    Fragment f = getSupportFragmentManager().findFragmentByTag("first");
                    if( f!= null) ft.remove(f);
                    allFragment = null;
                    mainActivityFrameLayoutNoScroll.setVisibility(FrameLayout.GONE);
                }
                break;
            case R.id.bottomBarItemAll :
                if(lastId == id){
                    Fragment f = getSupportFragmentManager().findFragmentByTag("first");
                    if(f != null) {
                        ft.remove(f);
                    }
                }
                if(allFragment == null){
                    allFragment = new AllFragment();
                    ft.add(R.id.mainActivityFrameLayoutNoScroll, allFragment,"first");
                }
                myScrollingContent.setVisibility(FrameLayout.INVISIBLE);
                mainActivityFrameLayoutNoScroll.setVisibility(FrameLayout.VISIBLE);

//                else{
//                    ft.add(R.id.mainActivityFrameLayoutNoScroll, allFragment);
//                }
                break;
            case R.id.bottomBarItemNearby :
                if(reservationFragment == null){
                    reservationFragment = new ReservationFragment();
                }
                if(id != lastId){
                        switchContent(R.id.mainActivityFrameLayout,reservationFragment);
                }else{
                    ft.add(R.id.mainActivityFrameLayout, reservationFragment);
                }
                if(lastId == R.id.bottomBarItemAll){
                    myScrollingContent.setVisibility(FrameLayout.VISIBLE);
                    Fragment f = getSupportFragmentManager().findFragmentByTag("first");
                    if( f!= null) ft.remove(f);
                    allFragment = null;
                    mainActivityFrameLayoutNoScroll.setVisibility(FrameLayout.GONE);
                }
                break;
            case R.id.bottomBarItemProfile :
                if(profileFragment == null){
                    if(accountType.equals("guest")){
                        profileFragment = new NotLoggedProfileFragment();
                    }else{
                        profileFragment = new ProfileFragment();
                    }

                }
                if(id != lastId){
                    switchContent(R.id.mainActivityFrameLayout,profileFragment);
                }else{
                    ft.add(R.id.mainActivityFrameLayout, profileFragment);
                }
                if(lastId == R.id.bottomBarItemAll){
                    myScrollingContent.setVisibility(FrameLayout.VISIBLE);
                    Fragment f = getSupportFragmentManager().findFragmentByTag("first");
                    if( f!= null) ft.remove(f);
                    allFragment = null;
                    mainActivityFrameLayoutNoScroll.setVisibility(FrameLayout.GONE);
                }
                break;
        }
        lastId = id;
        ft.commit();
    }

    public void switchContent(int newFragId, Fragment newFrag) {
        getSupportFragmentManager().beginTransaction()
                .replace(newFragId, newFrag)
                .commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);
        // TODO
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_ID,lastId);
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);
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
