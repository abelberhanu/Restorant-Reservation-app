package fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baasbox.android.BaasFile;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.Grant;

import java.io.FileNotFoundException;
import java.io.InputStream;

import polito.lab.anes.eatnow.R;
import polito.lab.anes.eatnow.activity.CreateNewRestaurant;

public class CreateNewRestaurantStep1 extends Fragment implements View.OnClickListener {
    private CreateNewRestaurant parentCNR;
    private int actualFrag;
    private final int SELECT_PHOTO = 1 ;
    private final int RESULT_OK = -1 ;

    private View myFragView;
    private ImageView img;

    public CreateNewRestaurantStep1() {
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
        parentCNR = (CreateNewRestaurant) getActivity();
        parentCNR.setActualFrag(1);

        myFragView =  inflater.inflate(R.layout.fragment_create_new_restaurant_step1, container, false);

        img = (ImageView) myFragView.findViewById(R.id.coverPictureRestaurant);
        img.setOnClickListener(this);
        if(parentCNR.getProfilePicUri() != null){
            img.setImageURI(parentCNR.getProfilePicUri());
        }
        parentCNR.setMyFrag2View(myFragView);
        return myFragView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(parentCNR.getProfilePicUri() != null){
            outState.putString("coverPictureRestaurant", parentCNR.getProfilePicUri().toString());
        }
        outState.putString("restaurantName", String.valueOf(((TextView) myFragView.findViewById(R.id.restaurantName)).getText()));
        outState.putString("addressRestaurant", String.valueOf(((TextView) myFragView.findViewById(R.id.addressRestaurant)).getText()));
        outState.putString("phoneRestaurant", String.valueOf(((TextView) myFragView.findViewById(R.id.phoneRestaurant)).getText()));
        outState.putString("websiteRestaurant", String.valueOf(((TextView) myFragView.findViewById(R.id.websiteRestaurant)).getText()));
        outState.putString("descriptionRestaurant", String.valueOf(((TextView) myFragView.findViewById(R.id.descriptionRestaurant)).getText()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            if(savedInstanceState.getString("coverPictureRestaurant") != null) parentCNR.setProfilePicUri(Uri.parse(savedInstanceState.getString("coverPictureRestaurant")));
            String restaurantName = savedInstanceState.getString("restaurantName");
            String addressRestaurant = savedInstanceState.getString("addressRestaurant");
            String phoneRestaurant = savedInstanceState.getString("phoneRestaurant");
            String websiteRestaurant = savedInstanceState.getString("websiteRestaurant");
            String descriptionRestaurant = savedInstanceState.getString("descriptionRestaurant");

            if(parentCNR.getProfilePicUri() != null ){
                ((ImageView) myFragView.findViewById(R.id.coverPictureRestaurant)).setImageURI(parentCNR.getProfilePicUri());
            }
            if(restaurantName != null ){
                ((EditText) myFragView.findViewById(R.id.restaurantName)).setText(restaurantName);
            }
            if(addressRestaurant != null ){
                ((EditText) myFragView.findViewById(R.id.addressRestaurant)).setText(addressRestaurant);
            }
            if(phoneRestaurant != null ){
                ((EditText) myFragView.findViewById(R.id.phoneRestaurant)).setText(phoneRestaurant);
            }
            if(websiteRestaurant != null ){
                ((EditText) myFragView.findViewById(R.id.websiteRestaurant)).setText(websiteRestaurant);
            }
            if(descriptionRestaurant != null ){
                ((EditText) myFragView.findViewById(R.id.descriptionRestaurant)).setText(descriptionRestaurant);
            }
        }
        parentCNR.setMyFrag2View(myFragView);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == SELECT_PHOTO){
            if(resultCode == RESULT_OK){
                Boolean process = true;
                if((parentCNR.getProfilePicUri() != null)){
                    if((data.getData().equals(parentCNR.getProfilePicUri().toString()))){
                        process = false;
                    }
                }
                if(process){
                    parentCNR.setProfilePicUri(data.getData());
                    img.setImageURI(parentCNR.getProfilePicUri());
                    try {
                        InputStream stream =getContext().getContentResolver().openInputStream(parentCNR.getProfilePicUri());
                        BaasFile file = new BaasFile();
                        if(parentCNR.getRestaurantObject().getProfilePic() != null){
                            file.delete(parentCNR.getRestaurantObject().getProfilePic(),new BaasHandler<Void>() {
                                @Override
                                public void handle(BaasResult<Void> res) {
                                    if(res.isSuccess()) {
                                        Log.d("LOG", "User pic has been deleted");
                                    } else {
                                        Log.e("LOG", String.valueOf(res.error()));
                                    }
                                }
                            });
                        }
                        file.upload(stream,new BaasHandler<BaasFile>() {
                            @Override
                            public void handle(BaasResult<BaasFile> res) {
                                if(res.isSuccess()) {
                                    Log.d("LOG", "User data has been saved");
                                    String profilPicString = res.value().getId().toString();
                                    parentCNR.getRestaurantObject().setProfilePic(profilPicString);
                                } else {
                                    Log.e("LOG", String.valueOf(res.error()));
                                }
                            }
                        });
                        file.grantAll(Grant.READ,"registered",new BaasHandler<Void>(){
                            @Override
                            public void handle(BaasResult<Void> res){
                                if (res.isSuccess()) {
                                    Log.d("LOG","andrea can read the file");
                                } else {
                                    Log.e("LOG","deal with error",res.error());
                                }
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
    switch (v.getId()){
        case R.id.coverPictureRestaurant :
            Intent intentNewPic = new Intent(Intent.ACTION_PICK);
            intentNewPic.setType("image/*");
            startActivityForResult(intentNewPic,SELECT_PHOTO);
            break;
    }
    }
}
