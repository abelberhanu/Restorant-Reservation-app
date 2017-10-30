package fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import adapter.CustomListAdapter;
import db.management.DataFlow;
import polito.lab.anes.eatnow.R;
import polito.lab.anes.eatnow.activity.RestaurantProfileActivity;


public class AllFragment extends Fragment {
    static final String logTag = "ActivitySwipeDetector";


    private View view;
    private ListView restaurantListView;
    private EditText inputSeatchEdTx;
    private CustomListAdapter adapter;

    private String[] items;
    private HashMap<String,String> nameIdHash;
    private DataFlow df;
    private FetchListOfRestaurants mFetchListOfRestaurants = null;

    public AllFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nameIdHash = new HashMap<>();
        df = new DataFlow();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_all, container, false);
        restaurantListView = (ListView)  view.findViewById(R.id.restaurantLV);
        inputSeatchEdTx = (EditText) view.findViewById(R.id.searchInputEdTx);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFetchListOfRestaurants = null;
        restaurantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent myIntent = new Intent(getContext(), RestaurantProfileActivity.class);
                String restaurantBaasId = nameIdHash.get(items[position]);
                String restaurantName = items[position];
                myIntent.putExtra("restaurantBaasId", restaurantBaasId);
                myIntent.putExtra("restaurantName", restaurantName);
                startActivity(myIntent);

            }
        });
        inputSeatchEdTx.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        mFetchListOfRestaurants = new FetchListOfRestaurants();
        mFetchListOfRestaurants.execute(((Void) null));


    }

    private boolean createListForAdapter(JSONArray ja){
        if(ja != null){
            this.items = new String[ja.length()];
            List<String> nameList = new ArrayList<String>();
            for(int i =0 ; i < ja.length() ; i++){

                try {
                    JSONObject jo = ja.getJSONObject(i);
                    nameIdHash.put(jo.getString("name"),jo.getString("id"));
                    nameList.add(jo.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            nameList.toArray(this.items);
            Arrays.sort(this.items);
            adapter = new CustomListAdapter(getContext(),this.items);
            restaurantListView.setAdapter(adapter);
            return true;
        }else{
            return false;
        }
    }

    private JSONArray getListFromLocal(){
        String localFeed = df.readFromFile(getContext(),"localListRestaurant.txt");
        if(localFeed == null || localFeed == "") return null;
        try {
            JSONObject jo = new JSONObject(localFeed);
            return jo.getJSONArray("listRestaurants");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveListOfPlaces(){
        Iterator it = nameIdHash.entrySet().iterator();
        String begin = "{listRestaurants : [" ;
        String restaurantIntel = "";
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            restaurantIntel += "{ \"name\" : \"" +
                    pair.getKey().toString() + "\"," +
                    "\"id\" : \"" + pair.getValue().toString() + "\"}";
            if(it.hasNext()){
                restaurantIntel += ",";
            }
        }
        String finale = begin + restaurantIntel + "]}";
        df.writeToFile(getContext(),begin + restaurantIntel + "]}","localListRestaurant.txt");
    }

    public class FetchListOfRestaurants extends AsyncTask<Void, Void, Boolean> {

        private List<BaasDocument> lbd;

        public FetchListOfRestaurants() {
        }

        private JSONArray createJsonArrayFromBaasDocument(){
            JSONArray ja = new JSONArray();
            for(BaasDocument doc:lbd){
                try {
                    JSONObject jo = new JSONObject(doc.toJson().toString());
                    ja.put(jo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return ja;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            BaasQuery.Criteria filter = BaasQuery.builder()
                    .projection("id","name")
                    .criteria();

            BaasDocument.fetchAll("Restaurants",filter,
                    new BaasHandler<List<BaasDocument>>() {
                        @Override
                        public void handle(BaasResult<List<BaasDocument>> res) {
                            if (res.isSuccess()) {
                                lbd = res.value();
                                createListForAdapter(createJsonArrayFromBaasDocument());
                                saveListOfPlaces();
                                mFetchListOfRestaurants = null;
                            } else {
                                //no online data
                                boolean b =  createListForAdapter(getListFromLocal());
                                if(!b){
                                    //no local data
                                }
                                Log.e("LOG","Error",res.error());
                            }
                        }
                    });
            return true;
        }
    }

}


