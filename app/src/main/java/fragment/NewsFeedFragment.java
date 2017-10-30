package fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import db.management.DataFlow;
import polito.lab.anes.eatnow.R;
import polito.lab.anes.eatnow.activity.RestaurantProfileActivity;

public class NewsFeedFragment extends Fragment implements View.OnClickListener {

    // View ref
    private View myOwnFragmentView;
    private LinearLayout newsFragLL;
    private ViewGroup myOwnContainer;
    private Typeface myTitleTF;
    private Typeface myDescriptionTF;
    private Typeface myButtonTF;

    //Process Ref
    private HashMap<String,ImageView> newsFeedImageViewsList ;
    private HashMap<String,String> newsFeedUrlImageList ;
    private HashMap<String,Bitmap> newsFeedImageList ;
    private LayoutInflater myOwnCustomCardInflater = null;
    private int counterOfCardView = 1;
    private DataFlow df = new DataFlow();
    private FetchImages mFecthImages = null;
    private FectchLastNewsFeed mFetchLastNewsFeed = null;
    private JSONArray cvIntelArray;
    private FrameLayout waitNews;
    private Button refresh;
    private HashMap<Integer,String> equiId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsFeedImageViewsList = new HashMap<>();
        newsFeedUrlImageList = new HashMap<>();
        newsFeedImageList = new HashMap<>();
        equiId = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myOwnFragmentView = inflater.inflate(R.layout.fragment_news_feed, container, false);
        newsFragLL = (LinearLayout) myOwnFragmentView.findViewById(R.id.newsFragLL);
        waitNews = (FrameLayout) myOwnFragmentView.findViewById(R.id.waitNews);
        refresh = (Button) myOwnFragmentView.findViewById(R.id.refresh);
        refresh.setOnClickListener(this);
        myOwnContainer = container;
        myTitleTF = Typeface.createFromAsset(getContext().getAssets(),"fonts/Dolce_Vita_Heavy_Bold.ttf");;
        myDescriptionTF = Typeface.createFromAsset(getContext().getAssets(),"fonts/Dolce_Vita_Light.ttf");;
        myButtonTF = Typeface.createFromAsset(getContext().getAssets(),"fonts/Dolce_Vita.ttf");;

        return myOwnFragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFecthImages = null;
        mFetchLastNewsFeed = null;
        if(cvIntelArray == null || cvIntelArray.length() == 0){
            refreshAll();
        }else{
            processData(cvIntelArray);
        }


    }

    public void refreshAll(){
        showAnimation(true);
        mFetchLastNewsFeed = new FectchLastNewsFeed();
        mFetchLastNewsFeed.execute(((Void) null));
    }

    private void showAnimation(Boolean b){
        if(b){
            waitNews.setVisibility(View.VISIBLE);
        }else{
            waitNews.setVisibility(View.GONE);
        }
    }
    private void processData(JSONArray cvIntelArray){
        for(int i = 0 ; i< cvIntelArray.length(); i++){
            try {
                JSONObject obj = cvIntelArray.getJSONObject(i);
                CardView cv = getCardView(obj);
                setView(obj,cv);
                newsFragLL.addView(cv);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(newsFeedUrlImageList.size() > 0){
            mFecthImages = new FetchImages();
            mFecthImages.execute(((Void) null));
        }
    }

    private JSONArray getLocalNewsFeed()  {
        //TODO : read db to get local news && contact server to get last news from the last we had
        String localNewsFeed = df.readFromFile(getContext(),"localNewsFeed.txt");
//        String localNewsFeed = "{listLocal : [{\n" +
//                "  \"title\": \"New Pizza ! Piano B\",\n" +
//                "  \"description\": \"Come try our new pizza 'La New Yorkaise' \\n 7.00 EUR\",\n" +
//                "  \"meta\": \"\",\n" +
//                "  \"image\": \"http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg\",\n" +
//                "  \"restaurantId\": \"109b6e8b-3a5c-4120-861c-a6db686cb241\",\n" +
//                "  \"limit\": \"\"\n" +
//                "},{\n" +
//                "  \"title\": \"New Pizza ! Piano B\",\n" +
//                "  \"description\": \"Come try our new pizza 'La New Yorkaise' \\n 7.00 EUR\",\n" +
//                "  \"meta\": \"\",\n" +
//                "  \"image\": \"drawable:::jupiter\",\n" +
//                "  \"restaurantId\": \"109b6e8b-3a5c-4120-861c-a6db686cb241\",\n" +
//                "  \"limit\": \"\"\n" +
//                "}]}";
        if (localNewsFeed == null || localNewsFeed == "") return null;
        JSONObject json = null;
        try {
            json = new JSONObject(localNewsFeed);
            return json.getJSONArray("listLocal");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setView(JSONObject jsonObject,CardView cv){
        TextView titleTxV = (TextView) cv.findViewWithTag("titleTxV");
        TextView descriptionContentOfCD = (TextView) cv.findViewWithTag("descriptionContentOfCD");
        ImageView coverPic = (ImageView) cv.findViewWithTag("coverPic");
        Button buttonCheck = (Button) cv.findViewWithTag("buttonCheck");

        try {
            titleTxV.setText(jsonObject.getString("title"));
            titleTxV.setTypeface(myTitleTF);
            descriptionContentOfCD.setText(jsonObject.getString("description"));
            descriptionContentOfCD.setTypeface(myDescriptionTF);
            buttonCheck.setTypeface(myButtonTF);
            if(jsonObject.getString("image").contains("drawable:::")){
                //"yourpackagename:drawable/" + StringGenerated
//                int idResImage = getResources().getIdentifier(jsonObject.getString("image"), null, null);
                int idResImage = getContext().getResources().getIdentifier(jsonObject.getString("image").replace("drawable:::",""), "drawable", getContext().getPackageName());
                coverPic.setImageResource(idResImage);
            }else{
                newsFeedUrlImageList.put(String.valueOf(cv.getId()),jsonObject.getString("image"));
                newsFeedImageViewsList.put(String.valueOf(cv.getId()),coverPic);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private void displayCoverImages(){
        if(newsFeedImageList != null){
            Iterator it = newsFeedImageList.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                Bitmap image = (Bitmap) pair.getValue();
                String ivId = (String) pair.getKey();
                ImageView iv = (ImageView) newsFragLL.findViewById(Integer.valueOf(ivId)).findViewWithTag("coverPic");
                iv.setImageBitmap(image);
                it.remove();
            }
        }
    }
    private CardView getCardView(JSONObject  obj) throws JSONException {
        myOwnCustomCardInflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        CardView cv = (CardView) myOwnCustomCardInflater.inflate(R.layout.all_frag_elem_item,myOwnContainer,false);
        Button btn = (Button) cv.findViewWithTag("buttonCheck");
        btn.setTag("buttonCheck" + ":::" + String.valueOf(counterOfCardView)  + ":::" + obj.getString("name"));
        equiId.put(counterOfCardView,obj.getString("id"));
        btn.setOnClickListener(this);
        cv.setId(counterOfCardView++);
        return cv;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.refresh){
            refreshAll();
        }else{
            if (((String) v.getTag()).contains("buttonCheck")){
                Intent myIntent = new Intent(getContext(), RestaurantProfileActivity.class);
                int index = Integer.valueOf(((String) v.getTag()).split(":::")[1]);
                String restaurantName = ((String) v.getTag()).split(":::")[2];
                String id = equiId.get(index);
                myIntent.putExtra("restaurantBaasId", id);
                myIntent.putExtra("restaurantName", restaurantName);
                startActivity(myIntent);
            }
        }

    }

    public class FectchLastNewsFeed extends AsyncTask<Void, Void, Boolean>{

        private List<BaasDocument> lbd;

        public FectchLastNewsFeed() {
        }

        private  void saveLocalNews(){
            String toSave = "{listLocal : ";
            String cvIntel = cvIntelArray.toString();
            df.writeToFile(getContext(),toSave + cvIntel + "}","localNewsFeed.txt");
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
            BaasQuery.Criteria filter = BaasQuery.builder().pagination(0,20)
                    .orderBy("_creation_date desc")
                    .pagination(0,10)
                    .criteria();

            BaasDocument.fetchAll("News",filter,
                    new BaasHandler<List<BaasDocument>>() {
                        @Override
                        public void handle(BaasResult<List<BaasDocument>> res) {
                            if (res.isSuccess()) {
                                lbd = res.value();
                                cvIntelArray = createJsonArrayFromBaasDocument();
                                if(cvIntelArray != null){
                                    processData(cvIntelArray);
                                    saveLocalNews();
                                    showAnimation(false);
                                }
                                mFetchLastNewsFeed = null;
                            } else {
                                Log.e("LOG","Error",res.error());
                            }
                        }
                    });
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
    public class FetchImages extends AsyncTask<Void, Void, Boolean> {

        public FetchImages() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(newsFeedUrlImageList != null && newsFeedUrlImageList.size() > 0){
                Iterator it = newsFeedUrlImageList.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    String url = pair.getValue().toString();
                    Bitmap mIcon11 = null;
                    try {
                        InputStream in = new java.net.URL(url).openStream();
                        mIcon11 = BitmapFactory.decodeStream(in);
                        newsFeedImageList.put(pair.getKey().toString(),mIcon11);
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                        e.printStackTrace();
                    }
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }

            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            mFecthImages = null;
            displayCoverImages();

        }

        @Override
        protected void onCancelled() {
            mFecthImages = null;
        }
    }
}
