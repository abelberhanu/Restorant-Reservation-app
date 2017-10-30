package fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasFile;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.DataStreamHandler;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.net.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import db.management.DataFlow;
import objects.Places;
import objects.RestaurantProfileClass;
import polito.lab.anes.eatnow.R;
import polito.lab.anes.eatnow.activity.RestaurantProfileActivity;


public class RestaurantInfoFragment extends Fragment implements View.OnClickListener{
    private String restaurantBaasId;
    private View view;
    private ImageButton likeBtn;
    private TextView restaurantProPhoneTxV;
    private ImageView coverPictureImageView;
    private Places place;
    private FetchDocument mFetchDocument = null;
    private boolean allReadyCall = false;
    private RestaurantProfileActivity parentRPA;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restaurantBaasId = getArguments().getString("restaurantBaasId");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_restaurant_info, container, false);
        likeBtn = (ImageButton) view.findViewById(R.id.btnLike);
        coverPictureImageView = (ImageView) view.findViewById(R.id.coverPictureImageView);
        restaurantProPhoneTxV = (TextView) view.findViewById(R.id.restaurantProPhoneTxV);
        TextView restaurantAddressTxV = (TextView) view.findViewById(R.id.restaurantProAddressTxV);
        TextView restaurantPhoneTxV = (TextView) view.findViewById(R.id.restaurantProPhoneTxV);
        TextView restaurantDescriptionTxV = (TextView) view.findViewById(R.id.restaurantProDescriptionTxV);

        Typeface face=Typeface.createFromAsset(getContext().getAssets(),"fonts/Dolce_Vita_Light.ttf");
        restaurantAddressTxV.setTypeface(face);
        likeBtn.setOnClickListener(this);
        restaurantProPhoneTxV.setOnClickListener(this);

        parentRPA = (RestaurantProfileActivity) getActivity();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!allReadyCall){
            mFetchDocument = new FetchDocument();
            mFetchDocument.execute(((Void) null));
            allReadyCall = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLike:
                Toast.makeText(getActivity(),"Added to Favorite", Toast.LENGTH_SHORT).show();
                // TODO : function to manage favorite (getlist, check unicity, add, save file)
                BaasBox box =BaasBox.getDefault();
                box.rest(HttpRequest.POST,
                        "plugin/user.addToFavorites",
                        new JsonObject().put("restaurantId", restaurantBaasId),
                        true,
                        new BaasHandler<JsonObject>() {
                            @Override
                            public void handle(BaasResult<JsonObject> res) {
                                Log.d("TAG","Ok: " + res.isSuccess());
                            }
                        });
                break;
            case R.id.restaurantProPhoneTxV :
                createDialog();
                break;
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save the values you need from your textview into "outState"-object
        super.onSaveInstanceState(outState);
    }

    private void createDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Do you want to call ?");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        alertDialogBuilder
                .setView(inflater.inflate(R.layout.dialog_trigger_call, null))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + place.getPhoneNumber()));
                        startActivity(callIntent);
                        dialog.dismiss();
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private boolean fillView(){
        if(restaurantBaasId != null){
            DataFlow df =  new DataFlow();
            String restaurantIntel =restaurantIntel = df.readFromFile(getContext(),restaurantBaasId + ".txt");
            if(restaurantIntel != null && restaurantIntel != ""){
                try {
                    JSONObject jo = new JSONObject(restaurantIntel);
                    place = new Places(jo);
                    getPic(jo);
                    RestaurantProfileClass rpc = new RestaurantProfileClass(place);
                    rpc.fillView(view,getContext());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }else{
                return false;
            }

        }
        return false;
    }
    public void getPic(JSONObject jo) throws JSONException {
        BaasFile file = new BaasFile();
        String id =  jo.getString("profile_pic");
        file.stream(id, new DataStreamHandler<BaasFile>() {
            @Override
            public void startData(String s, long l, String s1) throws Exception {
            }

            @Override
            public void onData(byte[] bytes, int i) throws Exception {
            }

            @Override
            public BaasFile endData(String s, long l, String s1) throws Exception {
                return null;
            }

            @Override
            public void finishStream(String s) {
            }
        },new BaasHandler<BaasFile>() {
            @Override
            public void handle(BaasResult<BaasFile> res) {
                if ( res.isSuccess() ) {
                    byte[] data = res.value().getData();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);
                    coverPictureImageView.setImageBitmap(bitmap);
                    Log.d("LOG","File received");
                } else {
                    Log.e("LOG","Error while streaming",res.error());
                }
            }
        });
    }

    public class FetchDocument extends AsyncTask<Void,Void,Boolean> {
        public FetchDocument() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            BaasDocument.fetch("Restaurants",
                    restaurantBaasId,
                    new BaasHandler<BaasDocument>() {
                        @Override
                        public void handle(BaasResult<BaasDocument> baasResult) {
                            if(baasResult.isSuccess()) {
                                BaasDocument doc = baasResult.value();
                                DataFlow df =  new DataFlow();
                                df.writeToFile(getContext(),doc.toJson().toString(),restaurantBaasId + ".txt");
                                fillView();
                                mFetchDocument = null;
                            } else {
                                Log.e("LOG","error",baasResult.error());
                            }
                        }
                    });
            return null;
        }

    }


}
