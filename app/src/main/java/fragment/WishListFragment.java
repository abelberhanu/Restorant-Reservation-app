package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.net.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import objects.OrderItem;
import objects.WishList;
import polito.lab.anes.eatnow.R;
import polito.lab.anes.eatnow.activity.RestaurantProfileActivity;


public class WishListFragment extends Fragment implements View.OnClickListener {
    private View myOwnFragView = null;
    private FrameLayout wait;
    private TextView errorTxv;


    private HashMap<String,List<String>> mWishList = null;
    private HashMap<String,Integer> hWishList;
    private String restaurantBaasId = null;
    private RestaurantProfileActivity parentRPA;
    private ArrayList<OrderItem> aloi;
    private WishList whishList;

    public WishListFragment(){}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restaurantBaasId = getArguments().getString("restaurantBaasId");
        aloi = new ArrayList<>();
        whishList = new WishList();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myOwnFragView = inflater.inflate(R.layout.fragment_whish_list, container, false);
        parentRPA = (RestaurantProfileActivity) getActivity();
        wait = (FrameLayout) myOwnFragView.findViewById(R.id.wait);
        errorTxv = (TextView) myOwnFragView.findViewById(R.id.errorTxv);
        wait.setVisibility(View.INVISIBLE);
        mWishList = parentRPA.mWishList;
        Button orderBtn = (Button) myOwnFragView.findViewById(R.id.orderBtn);
        orderBtn.setOnClickListener(this);
        return  myOwnFragView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String theStringWish = "";
        TextView tx =(TextView) view.findViewById(R.id.txtVi);
        TextView txtTot =(TextView) view.findViewById(R.id.txtTot);
        tx.setText("");
        if(!mWishList.isEmpty()){
            Set<String> set  = mWishList.keySet();
            for(String key:set){
                List<String> l =  mWishList.get(key);
                int listSize = l.size();
                if(listSize != 0){
                    theStringWish += key + ", qty : "+ listSize + ", " + l.get(0) +  "€\n"; // name of the dish + qty + price
                    whishList.addOrder(new OrderItem(Double.valueOf(listSize),key,Double.valueOf(l.get(0))));
                }else {
                    mWishList.remove(key);
                }

            }
        }
        tx.setText(theStringWish);
        txtTot.setText("Total : "  + String.valueOf(whishList.getTotal()) + " €");


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.orderBtn :
                if(whishList != null){
                    try {
                        JSONObject orders = whishList.toJSON();
                        if(orders != null){
                            String strOrd = orders.toString();
                            JsonObject query = new JsonObject();
                            query.put("orders",strOrd);
                            query.put("restaurantId",restaurantBaasId);
                            query.put("total",whishList.getTotal());
                            wait.setVisibility(View.VISIBLE);
                            BaasBox box =BaasBox.getDefault();
                            box.rest(HttpRequest.POST,
                                    "plugin/user.createReservation",
                                    query,
                                    true,
                                    new BaasHandler<JsonObject>() {
                                        @Override
                                        public void handle(BaasResult<JsonObject> res) {
                                            if(res.isSuccess()){
                                                if(res.value().getBoolean("data").equals(true)){
                                                    errorTxv.setText("Order Done !");
                                                    wait.setVisibility(View.GONE);
                                                }else{
                                                    errorTxv.setText("Error Ocurred, Order was not done");
                                                    wait.setVisibility(View.GONE);
                                                }
                                            }else{
                                                errorTxv.setText("Error Ocurred, Order was not done");
                                                wait.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
