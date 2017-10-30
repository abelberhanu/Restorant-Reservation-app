package polito.lab.anes.eatnow.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.net.HttpRequest;

import org.json.JSONException;

import java.util.HashMap;

import fragment.CreateNewRestaurantStep1;
import fragment.CreateNewRestaurantStep2;
import fragment.CreateNewRestaurantStep3;
import objects.Restaurants;
import polito.lab.anes.eatnow.R;

public class CreateNewRestaurant extends AppCompatActivity implements View.OnClickListener {

    private android.support.v4.app.FragmentManager fm;
    public HashMap<Integer,Fragment> arf;
    private HashMap<Integer,View> mSectionHash;
    private final static int NEXT_STEP = 1;
    private final static int PREV_STEP = 0;
    public int actualFrag = 1;
    public void setActualFrag(int i){
        this.actualFrag = i;
    }
    public int  getActualFrag(){
        return this.actualFrag;
    }
    public Uri profilePicUri = null;
    public Uri getProfilePicUri(){return this.profilePicUri;}
    public void setProfilePicUri(Uri uri){this.profilePicUri = uri;}
    public Restaurants getRestaurantObject() {return restaurantObject;}
    public void setRestaurantObject(Restaurants restaurantObject) {this.restaurantObject = restaurantObject;}

    public Restaurants restaurantObject = new Restaurants();
    private final  String removeDish = "remButtonImB";
    private final  String addDish = "addButtonImB";
    private final  String deleteSection = "deleteSectionBtn";

    private Button nextBtn;
    private Button prevCancelBtn;
    private Fragment nextFrag;
    private LayoutInflater myOwnSectionInflater;
    public LinearLayout menuItemLL;

    public View getMyFrag2View() {
        return myFrag2View;
    }

    public void setMyFrag2View(View myFrag2View) {
        this.myFrag2View = myFrag2View;
    }

    public View myFrag2View;
    public Button newSectionBtn;
    public CreateNewRestaurantStep1 fragStep1 = null;
    public CreateNewRestaurantStep2 fragStep2 = null;
    public CreateNewRestaurantStep3 fragStep3 = null;



    public void setMenuItemLL(LinearLayout ll){
        this.menuItemLL = ll;
    }
    public LinearLayout getMenuItemLL(){
        return this.menuItemLL;
    }
    public void setNewSectionBtn(Button b){
        this.newSectionBtn = b;
        this.newSectionBtn.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_restaurant);
        mSectionHash = new HashMap<>();

        arf = new HashMap<>();
        fragStep1 = new CreateNewRestaurantStep1();
        arf.put(actualFrag,fragStep1);


        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.frameLayoutForFragment,arf.get(actualFrag));
        ft.commit();

        nextBtn = (Button) findViewById(R.id.nextBtn);
        prevCancelBtn = (Button) findViewById(R.id.prevCancelBtn);
        nextBtn.setOnClickListener(this);
        prevCancelBtn.setOnClickListener(this);
        Button newSectionBtn = (Button)  findViewById(R.id.newSectionBtn);
        if(newSectionBtn != null) newSectionBtn.setOnClickListener(this);

    }



    public void displayFragment(int id) throws JSONException {
        switch (id){
            case NEXT_STEP :
                if(this.getActualFrag() == 1){
                    if(fragStep2 == null){
                        nextFrag = new CreateNewRestaurantStep2();
                    }else{
                        nextFrag = fragStep2;
                    }
                    arf.put(this.getActualFrag() + 1,nextFrag);
                    prevCancelBtn.setText("Previous");
                    switchContent(R.id.frameLayoutForFragment,arf.get(this.getActualFrag() + 1),NEXT_STEP);
                }else if(this.getActualFrag() == 2){
                    if(fragStep3 == null){
                        nextFrag = new CreateNewRestaurantStep3();
                    }else{
                        nextFrag = fragStep3;
                    }

                    nextBtn.setText("Validate");

                    arf.put(this.getActualFrag() + 1,nextFrag);
                    switchContent(R.id.frameLayoutForFragment,arf.get(this.getActualFrag() + 1),NEXT_STEP);
                }else if(this.getActualFrag() == 3){
                    String body = null;
                    restaurantObject.getRestaurantJson().put("profile_pic",restaurantObject.getProfilePic());
                    body = restaurantObject.getRestaurantJson().toString();
                    BaasBox box =BaasBox.getDefault();
                    if(body != null){
                        box.rest(HttpRequest.POST,
                                "plugin/user.createNewOwnerRestaurant",
                                new JsonObject().put("restaurantBody",body),
                                true,
                                new BaasHandler<JsonObject>() {
                                    @Override
                                    public void handle(BaasResult<JsonObject> res) {
                                        if(res.isSuccess()){
                                            if(res.value().getBoolean("data").equals(true)){
                                                Toast.makeText(CreateNewRestaurant.this,"Saved",Toast.LENGTH_LONG);
                                                finish();
                                            }else{
                                                Toast.makeText(CreateNewRestaurant.this,"Error, Restaurants not saved",Toast.LENGTH_LONG);
                                            }
                                        }else{
                                            Toast.makeText(CreateNewRestaurant.this,"Error, Restaurants not saved",Toast.LENGTH_LONG);
                                        }
                                    }
                                });
                    }


                }
                break;
            case PREV_STEP :
                if(this.getActualFrag() == 1){
                    finish();
                }else{
                    nextFrag = arf.get(this.getActualFrag() - 1);
                    if(this.getActualFrag() - 1 == 1){
                        prevCancelBtn.setText("Cancel");
                    }else if(this.getActualFrag() - 1 == 2){
                        nextBtn.setText("Next");
                    }
                    switchContent(R.id.frameLayoutForFragment,nextFrag,PREV_STEP);
                }
                break;
        }
    }

    public void switchContent(int newFragId, Fragment newFrag,int NEXT) {
        if(NEXT == NEXT_STEP){
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    .replace(newFragId, newFrag)
                    .addToBackStack(null)
                    .commit();
        }else if(NEXT == PREV_STEP){
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(newFragId, newFrag)
                    .addToBackStack(null)
                    .commit();
        }

    }
    private void setButtonListener(LinearLayout section){
        if(section != null){
            Button deleteSectionBtn = (Button) section.findViewWithTag("deleteSectionBtn");
            if(deleteSectionBtn != null) deleteSectionBtn.setOnClickListener(this);
            ImageButton remButtonImB = (ImageButton) section.findViewWithTag("remButtonImB");
            if(remButtonImB != null) remButtonImB.setOnClickListener(this);
            ImageButton addButtonImB = (ImageButton) section.findViewWithTag("addButtonImB");
            if(addButtonImB != null) addButtonImB.setOnClickListener(this);
        }
    }

    private LinearLayout createSection(){
        myOwnSectionInflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);
        return (LinearLayout) myOwnSectionInflater.inflate(R.layout.to_fill_menu_item,null);
    }

    private LinearLayout createNewDish(){
        myOwnSectionInflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);
        return (LinearLayout) myOwnSectionInflater.inflate(R.layout.to_fill_dish,null);
    }

    private void addNewSection(){
        menuItemLL = (LinearLayout) findViewById(R.id.menuItemFL).findViewWithTag("menuItemLL");
        LinearLayout section = createSection();
        setButtonListener(section);
        menuItemLL.addView(section);
    }
    private void addNewDish(LinearLayout section){
        LinearLayout dish = createNewDish();
        setButtonListener(dish);
        section.addView(dish);
    }

    private void removeSection(LinearLayout sectionToRemove){
        menuItemLL = (LinearLayout) findViewById(R.id.menuItemFL).findViewWithTag("menuItemLL");
        menuItemLL.removeView(sectionToRemove);
    }

    private void removeDish(LinearLayout sectionToRemoveFrom,LinearLayout dishToRemove){
        LinearLayout ll = (LinearLayout) sectionToRemoveFrom.findViewWithTag("dishItems");
        if(ll != null) ll.removeView(dishToRemove);
    }



    @Override
    public void onClick(View v) {
        LinearLayout ll = null;
        LinearLayout ll2 = null;
        switch (v.getId()){
            case R.id.nextBtn :
                try {
                    displayFragment(NEXT_STEP);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.prevCancelBtn :
                try {
                    displayFragment(PREV_STEP);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.newSectionBtn :
                addNewSection();
                break;
        }
        if((String) v.getTag() != null){
            switch ((String) v.getTag()){
                case removeDish :
                    while ( ll == null || ll2 == null){
                        if(ll == null )  ll = (LinearLayout) v.findViewWithTag("dishItems");
                        if(ll2 == null ) ll2 = (LinearLayout) v.findViewWithTag("dish");
                        v = (View) v.getParent();
                    }
                    removeDish(ll,ll2);
                    break;
                case addDish :
                    while ( ll == null){
                        ll = (LinearLayout) v.findViewWithTag("dishItems");
                        v = (View) v.getParent();
                    }
                    addNewDish(ll);
                    break;
                case deleteSection :
                    while ( ll == null){
                        ll = (LinearLayout) v.findViewWithTag("section");
                        v = (View) v.getParent();
                    }
                    removeSection(ll);
                    break;

            }
        }
    }
}
