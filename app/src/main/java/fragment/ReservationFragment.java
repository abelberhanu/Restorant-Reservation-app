package fragment;

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

public class ReservationFragment extends Fragment implements View.OnClickListener{
    private View myOwnFrag;
    private HashMap<String,String> mapRes;
    public LayoutInflater myOwnReservationLI = null;

    private Typeface face;
    private FrameLayout waitFL;
    private Button refreshBtn;
    private final static String remRes= "remRes";
    private final static String restaurantName= "restaurantName";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myOwnFrag = inflater.inflate(R.layout.fragment_reservation, container, false);
        mapRes = new HashMap<>();
        face = Typeface.createFromAsset(getContext().getAssets(),"fonts/Dolce_Vita.ttf");
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

    public void refreshList(){
        BaasBox box =BaasBox.getDefault();
        box.rest(HttpRequest.GET,
                "plugin/user.getReservations",
                new JsonObject().put("", ""),
                true,
                new BaasHandler<JsonObject>() {
                    @Override
                    public void handle(BaasResult<JsonObject> res) {
                        try {
                            createAndDisplayMyReservationss(res);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public LinearLayout getFavItem(String name,Double total){
        myOwnReservationLI = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        LinearLayout ll = (LinearLayout) myOwnReservationLI.inflate(R.layout.reservation_items,null);
        ((TextView) ll.findViewWithTag(restaurantName)).setText(name);
        ((TextView) ll.findViewWithTag("totalTag")).setText(String.valueOf(total));
        ((TextView) ll.findViewWithTag(restaurantName)).setOnClickListener(this);
        ((TextView) ll.findViewWithTag(restaurantName)).setTypeface(face);
        ((ImageButton) ll.findViewWithTag(remRes)).setOnClickListener(this);
        return ll;
    }


    public void createAndDisplayMyReservationss(BaasResult<JsonObject> res) throws JSONException {
        if(res.isSuccess()){
            waitFL.setVisibility(View.GONE);
            JsonObject job = res.value();
            JSONObject jo = new JSONObject(job.toString());
            JSONArray ja = jo.getJSONArray("data");
            for(int i = 0 ; i < ja.length() ; i++){
                JSONObject in = ja.getJSONObject(i).getJSONObject("in");
                String linkId = ja.getJSONObject(i).getString("id");
                if(!mapRes.containsKey(in.getString("name"))){
                    mapRes.put(in.getString("name"),in.getString("id") + ":::" + linkId);
                    LinearLayout favItem = getFavItem(in.getString("name"),in.getDouble("total"));
                    ((LinearLayout) myOwnFrag.findViewById(R.id.reservationLL)).addView(favItem);
                }
            }



        }
    }
    @Override
    public void onClick(View v) {
        View nameTxV = null;
        View theResTmp = null;
        switch (v.getId()){
            case R.id.refreshBtn :
                refreshList();
                break;
            default:
                switch ((String)v.getTag()){
                    case remRes :
                        while(nameTxV == null || theResTmp == null){
                            nameTxV = v.findViewWithTag(restaurantName);
                            theResTmp = v.findViewWithTag("theRes");
                            v = (View) v.getParent();
                        }
                        final View theRes = theResTmp;
                        final String name = String.valueOf(((TextView)nameTxV).getText());
                        String linkIdFull = mapRes.get(name);
                        String linkId = linkIdFull.split(":::")[1];
                        if(linkId != null){
                            BaasLink.withId(linkId).delete(RequestOptions.DEFAULT,new BaasHandler<Void>() {
                                @Override
                                public void handle(BaasResult<Void> ok) {
                                    if (ok.isSuccess()){
                                        ((LinearLayout) myOwnFrag.findViewById(R.id.reservationLL)).removeView(theRes);
                                        mapRes.remove(name);
                                    }
                                }
                            });
                        }
                        break;
                    case restaurantName :

                        while(nameTxV == null || theResTmp == null){
                            nameTxV = v.findViewWithTag(restaurantName);
                            theResTmp = v.findViewWithTag("theRes");
                            v = (View) v.getParent();
                        }
                        final View theRes2 = theResTmp;
                        final String name2 = String.valueOf(((TextView)nameTxV).getText());
                        String IdFull2 = mapRes.get(name2);
                        String Id2 = IdFull2.split(":::")[0];
//                        Intent myIntent = new Intent(getContext(), RestaurantProfileActivity.class);
//                        String idFull = mapRes.get(((TextView)v).getText());
//                        String id = idFull.split(":::")[0];
//                        myIntent.putExtra("restaurantBaasId", id);
//                        myIntent.putExtra("restaurantName", ((TextView)v).getText());
//                        startActivity(myIntent);
                        break;
                }
                break;
        }
    }
}
