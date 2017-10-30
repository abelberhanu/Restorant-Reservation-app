package fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.net.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import polito.lab.anes.eatnow.R;
import polito.lab.anes.eatnow.activity.CreateNewRestaurant;
import polito.lab.anes.eatnow.activity.RestaurantOwnerActivity;

public class AllRestaurantOwnerFragment extends Fragment implements View.OnClickListener  {


    private FetchListOfRestaurants mFetchListOfRestaurants = null;

    private View myFragmentView;
    private LinearLayout ownerAllFragLL;
    private Button addNewRestaurantBtn;
    private Button refreshBtn;
    private HashMap<String,String> listRestaurant;
    private LayoutInflater myOwnResItemLayoutInflater;
    private FrameLayout waitRest;
    private Typeface face;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_all_restaurant_owner, container, false);
        //Todo : get restaurant list
        //Todo : display list

        ownerAllFragLL = (LinearLayout) myFragmentView.findViewById(R.id.ownerAllFragLL);
        addNewRestaurantBtn = (Button) myFragmentView.findViewById(R.id.addNewRestaurantBtn);
        refreshBtn = (Button) myFragmentView.findViewById(R.id.refreshBtn);
        waitRest = (FrameLayout) myFragmentView.findViewById(R.id.waitRest);
        listRestaurant = new HashMap<>();
        face = Typeface.createFromAsset(getContext().getAssets(),"fonts/Dolce_Vita.ttf");
        addNewRestaurantBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
        refreshAll();
        return myFragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addNewRestaurantBtn :
                Intent intent = new Intent();
                intent = new Intent(getActivity(), CreateNewRestaurant.class);
                startActivity(intent);
                break;
            case R.id.refreshBtn :
                refreshAll();
                break;
            default:
                switch ((String) v.getTag()){
                    case    "restaurantName":
                        TextView txV = (TextView) v.findViewWithTag("restaurantName");
                        String id = listRestaurant.get(txV.getText());
                        Intent intent1 = new Intent();
                        intent1 = new Intent(getActivity(), RestaurantOwnerActivity.class);
                        startActivity(intent1);
                        break;
                }
                break;
        }
    }

    private void showAnimation(Boolean b){
        if(b){
            waitRest.setVisibility(View.VISIBLE);
        }else{
            waitRest.setVisibility(View.GONE);
        }
    }

    public void refreshAll(){
        showAnimation(true);
        mFetchListOfRestaurants = new FetchListOfRestaurants();
        mFetchListOfRestaurants.execute(((Void) null));
    }
    public LinearLayout createRestaurantItem(JSONObject in) throws JSONException {
        if(getContext() != null){
            myOwnResItemLayoutInflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            LinearLayout ll = (LinearLayout) myOwnResItemLayoutInflater.inflate(R.layout.item_restaurant,null);
            ((TextView) ll.findViewWithTag("restaurantName")).setText(in.getString("name"));
            ((TextView) ll.findViewWithTag("restaurantName")).setOnClickListener(this);
            ((TextView) ll.findViewWithTag("restaurantName")).setTypeface(face);
            return ll;
        }
        return null;
    }



    public class FetchListOfRestaurants extends AsyncTask<Void, Void, Boolean> {

        public FetchListOfRestaurants() {
        }




        @Override
        protected Boolean doInBackground(Void... params) {
            BaasBox box =BaasBox.getDefault();
            box.rest(HttpRequest.GET,
                    "plugin/user.getOwnerRestaurants",
                    new JsonObject().put("", ""),
                    true,
                    new BaasHandler<JsonObject>() {
                        @Override
                        public void handle(BaasResult<JsonObject> res) {
                            Log.d("TAG","Ok: " + res.isSuccess());
                            JsonObject job = res.value();
                            try {
                                JSONArray jaData = (new JSONObject(job.toString())).getJSONArray("data");
                                if(jaData != null){
                                    for(int i = 0 ; i < jaData.length() ; i++){
                                        JSONObject in = jaData.getJSONObject(i).getJSONObject("in");
                                            String name = in.getString("name");
                                            if(!listRestaurant.containsKey(name)){
                                                listRestaurant.put(name,in.getString("id"));
                                                LinearLayout ll = createRestaurantItem(in);
                                                if(ll != null) ((LinearLayout) myFragmentView.findViewById(R.id.ownerAllFragLL)).addView(ll);
                                            }



                                        }
                                    showAnimation(false);
                                    }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
            return true;
        }
    }
}
