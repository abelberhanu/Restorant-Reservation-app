package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

import polito.lab.anes.eatnow.R;
import polito.lab.anes.eatnow.activity.CreateNewRestaurant;


public class CreateNewRestaurantStep3 extends Fragment {
    private CreateNewRestaurant parentCNR;
    private int actualFrag;
    private View myFragView;

    public CreateNewRestaurantStep3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragView = inflater.inflate(R.layout.fragment_create_new_restaurant_step3, container, false);
        parentCNR = (CreateNewRestaurant) getActivity();
        parentCNR.setActualFrag(3);
        actualFrag = parentCNR.getActualFrag();
        try {
            View vCivil = parentCNR.getMyFrag2View();
            View vMenu = parentCNR.getMenuItemLL();
            parentCNR.getRestaurantObject().createRestaurantsFromView(vCivil,vMenu);
            ((TextView)myFragView.findViewById(R.id.menuTxV)).setText(parentCNR.getRestaurantObject().getMenuStr());
            ((TextView)myFragView.findViewById(R.id.civilName)).setText(parentCNR.getRestaurantObject().getName());
            ((TextView)myFragView.findViewById(R.id.civilAddress)).setText(parentCNR.getRestaurantObject().getAddress());
            ((TextView)myFragView.findViewById(R.id.civilPhone)).setText(parentCNR.getRestaurantObject().getPhone());
            ((TextView)myFragView.findViewById(R.id.civilwebsite)).setText(parentCNR.getRestaurantObject().getWebsite());
            ((TextView)myFragView.findViewById(R.id.civilDescription)).setText(parentCNR.getRestaurantObject().getDescription());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return myFragView ;
    }
}
