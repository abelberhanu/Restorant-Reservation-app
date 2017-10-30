package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import polito.lab.anes.eatnow.R;
import polito.lab.anes.eatnow.activity.CreateNewRestaurant;


public class CreateNewRestaurantStep2 extends Fragment {
    private View myFragmentView;
    private CreateNewRestaurant parentCNR;
    private List<View> lv = null;
    private int actualFrag;

    public CreateNewRestaurantStep2() {
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
        myFragmentView = inflater.inflate(R.layout.fragment_create_new_restaurant_step2, container, false);
        parentCNR = (CreateNewRestaurant) getActivity();

        LinearLayout menuLL = (LinearLayout) myFragmentView.findViewWithTag("menuItemLL");
        if(menuLL != null){
            if( parentCNR.getMenuItemLL() != null){
                if(((LinearLayout) menuLL).getChildCount() > 0)
                    ((LinearLayout) menuLL).removeAllViews();
                lv = new ArrayList<>();
                for(int i = 0 ; i < parentCNR.getMenuItemLL().getChildCount() ; i++){
                    lv.add(parentCNR.getMenuItemLL().getChildAt(i));
                }
                ((FrameLayout) menuLL.getParent()).removeView(menuLL);
                menuLL = new LinearLayout(getContext());
                menuLL.setOrientation(LinearLayout.VERTICAL);
                menuLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                menuLL.setTag("menuItemLL");
                ((FrameLayout) myFragmentView.findViewById(R.id.menuItemFL)).addView(menuLL);
                for(View v : lv){
                    LinearLayout parent = (LinearLayout) v.getParent();
                    parent.removeView(v);
                    menuLL.addView(v);
                }
                parentCNR.setMenuItemLL(menuLL);
            }
        }

        Button newSectionBtn = (Button)  myFragmentView.findViewById(R.id.newSectionBtn);
        if(newSectionBtn != null) parentCNR.setNewSectionBtn(newSectionBtn);


        parentCNR.setActualFrag(2);
        actualFrag = parentCNR.getActualFrag();


        return myFragmentView;
    }

}


