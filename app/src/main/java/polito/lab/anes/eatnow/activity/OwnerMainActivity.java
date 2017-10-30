package polito.lab.anes.eatnow.activity;

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

import fragment.AllRestaurantOwnerFragment;
import fragment.ProfileFragment;
import polito.lab.anes.eatnow.R;

public class OwnerMainActivity extends AppCompatActivity {
    private BottomBar mBottomBar;
    private FrameLayout myScrollingContent;
    private Fragment allFragment;
    private Fragment homeOwnerFragment;


    private android.support.v4.app.FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_main);

        myScrollingContent = (FrameLayout) findViewById(R.id.myScrollingContentOwner);
        mBottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.myCoordinatorOwner), findViewById(R.id.myScrollingContent),savedInstanceState);
        mBottomBar.setItemsFromMenu(R.menu.owner_main_menu, new MenuClass());
    }

    public void setColors(BottomBar mBottomBar){
        // Setting colors for different tabs when there's more than three of them.
        // You can set colors for tabs in three different ways as shown below.
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.all_color_accent));
        mBottomBar.mapColorForTab(1, ContextCompat.getColor(this, R.color.all_color_accent));
    }

    public void displayFragment(int id){
        FragmentTransaction ft = fm.beginTransaction();
        switch (id){
            case R.id.ownerMainBottomBarItemNews :
                if(homeOwnerFragment == null){
                    homeOwnerFragment = new ProfileFragment();
                    ft.add(R.id.ownerMainActivityFrameLayout, homeOwnerFragment);
                    ft.commit();
                }else{
                    switchContent(R.id.ownerMainActivityFrameLayout,homeOwnerFragment);
                    ft.commit();
                }
                break;
            case R.id.ownerMainBottomBarItemAll :
                if(allFragment == null){
                    allFragment = new AllRestaurantOwnerFragment();
                    ft.add(R.id.ownerMainActivityFrameLayout, allFragment);
                    ft.commit();
                    switchContent(R.id.ownerMainActivityFrameLayout,allFragment);
                }else{
                    switchContent(R.id.ownerMainActivityFrameLayout,allFragment);
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
            if (menuItemId == R.id.ownerMainBottomBarItemNews) {
                // The user reselected item number one, scroll your content to top.
            }
        }
    }
}
