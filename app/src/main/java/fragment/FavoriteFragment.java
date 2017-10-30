package fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasResult;
import com.baasbox.android.RequestOptions;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.net.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import polito.lab.anes.eatnow.R;
import polito.lab.anes.eatnow.activity.RestaurantProfileActivity;

public class FavoriteFragment extends Fragment implements View.OnClickListener{
    private View myOwnFrag;
    public LayoutInflater myOwnFavItemLayoutInflater = null;
    Typeface face;
    private FrameLayout waitFL;
    private Button refreshBtn;
    private final static String remFav= "remFav";
    private final static String restaurantName= "restaurantName";

    private HashMap<String,String> mapFav;

    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myOwnFrag =  inflater.inflate(R.layout.fragment_favorite, container, false);
        mapFav = new HashMap<>();
        face =Typeface.createFromAsset(getContext().getAssets(),"fonts/Dolce_Vita.ttf");
        waitFL = (FrameLayout) myOwnFrag.findViewById(R.id.waitFL);
        refreshBtn = (Button) myOwnFrag.findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(this);
        return myOwnFrag;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshList();
    }

    public void createAndDisplayMyFavorites(BaasResult<JsonObject> res) throws JSONException {
        if(res.isSuccess()){
            waitFL.setVisibility(View.GONE);
            JsonObject job = res.value();
            JSONObject jo = new JSONObject(job.toString());
            JSONArray ja = jo.getJSONArray("data");
            for(int i = 0 ; i < ja.length() ; i++){
                JSONObject in = ja.getJSONObject(i).getJSONObject("in");
                String linkId = ja.getJSONObject(i).getString("id");
                if(!mapFav.containsKey(in.getString("name"))){
                    mapFav.put(in.getString("name"),in.getString("id") + ":::" + linkId);
                    LinearLayout favItem = getFavItem(in.getString("name"));
                    if(favItem != null) ((LinearLayout) myOwnFrag.findViewById(R.id.favListLL)).addView(favItem);

                }
            }



        }
    }

    public LinearLayout getFavItem(String name){
        if(getContext() != null){
            myOwnFavItemLayoutInflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            LinearLayout ll = (LinearLayout) myOwnFavItemLayoutInflater.inflate(R.layout.favorite_item,null);
            ((TextView) ll.findViewWithTag(restaurantName)).setText(name);
            ((TextView) ll.findViewWithTag(restaurantName)).setOnClickListener(this);
            ((TextView) ll.findViewWithTag(restaurantName)).setTypeface(face);
            ((ImageButton) ll.findViewWithTag(remFav)).setOnClickListener(this);
            return ll;
        }
        return null;

    }

    public void refreshList(){
        BaasBox box =BaasBox.getDefault();
        box.rest(HttpRequest.GET,
                "plugin/user.getFavorites",
                new JsonObject().put("restaurantId", ""),
                true,
                new BaasHandler<JsonObject>() {
                    @Override
                    public void handle(BaasResult<JsonObject> res) {
                        try {
                            createAndDisplayMyFavorites(res);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.refreshBtn :
                refreshList();
                break;
            default:
                switch ((String)v.getTag()){
                    case remFav :
                        View nameTxV = null;
                        View theItemTmp = null;
                        while(nameTxV == null || theItemTmp == null){
                            nameTxV = v.findViewWithTag(restaurantName);
                            theItemTmp = v.findViewWithTag("theItem");
                            v = (View) v.getParent();
                        }
                        final View theItem = theItemTmp;
                        final String name = String.valueOf(((TextView)nameTxV).getText());
                        String linkIdFull = mapFav.get(name);
                        String linkId = linkIdFull.split(":::")[1];
                        if(linkId != null){
                            BaasLink.withId(linkId).delete(RequestOptions.DEFAULT,new BaasHandler<Void>() {
                                @Override
                                public void handle(BaasResult<Void> ok) {
                                    if (ok.isSuccess()){
                                        ((LinearLayout) myOwnFrag.findViewById(R.id.favListLL)).removeView(theItem);
                                        mapFav.remove(name);
                                    }
                                }
                            });
                        }
                        break;
                    case restaurantName :
                        Intent myIntent = new Intent(getContext(), RestaurantProfileActivity.class);
                        String idFull = mapFav.get(((TextView)v).getText());
                        String id = idFull.split(":::")[0];
                        myIntent.putExtra("restaurantBaasId", id);
                        myIntent.putExtra("restaurantName", ((TextView)v).getText());
                        startActivity(myIntent);
                        break;
                }
                break;
        }


    }
}
