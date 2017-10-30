package views;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import user.CustomerUser;

/**
 * Created by nowus on 4/29/2016.
 */
public class FavoritesManagementClass {

    private LinearLayout elementLL;
    private TextView titleTxV;
    private TextView typeTxV;
    private ImageView separatorImV;
    private Typeface faceBold;
    private Typeface faceLight;
    private Typeface faceNormal;
    private String filename;
    private HashMap<String,String> userFavorites;



    private Context ctx;

    public FavoritesManagementClass(Context ctx) {
        this.setCtx(ctx);
        // TODO : get list of favorites, initialize Hasmap
    }

    public LinearLayout geteElementLL() {
        return elementLL;
    }
    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    private View createElementViewFavorite(String title, String type){
        faceBold = Typeface.createFromAsset(ctx.getAssets(),"fonts/Dolce_Vita_Heavy_Bold.ttf");
        faceLight = Typeface.createFromAsset(ctx.getAssets(),"fonts/Dolce_Vita_Light.ttf");
        faceNormal = Typeface.createFromAsset(ctx.getAssets(),"fonts/Dolce_Vita.ttf");

        elementLL = new LinearLayout(ctx);
        elementLL.setOrientation(LinearLayout.VERTICAL);

        titleTxV = new TextView(ctx);
        titleTxV.setText(title);
        titleTxV.setTypeface(faceBold);
        titleTxV.setGravity(Gravity.CENTER);

        typeTxV = new TextView(ctx);
        titleTxV.setText(type);
        titleTxV.setTypeface(faceLight);
        titleTxV.setGravity(Gravity.LEFT);

        separatorImV = new ImageView(ctx);

        elementLL.addView(separatorImV);
        elementLL.addView(titleTxV);
        elementLL.addView(typeTxV);

        return elementLL;
    }


    // TODO : modify algo to use hashmap instead
    private View createFavoritesContent(CustomerUser user){
        LinearLayout mainll = new LinearLayout(ctx);
        mainll.setOrientation(LinearLayout.VERTICAL);
        ArrayList<String> fav = user.getFavorites();
        if(fav == null){
            return null;
        }
        for(int i = 0 ; i < fav.size() ; i++){
            String tmp = fav.get(i);
            View tmpV = createElementViewFavorite(tmp.split(":::")[0],tmp.split(":::")[1]);
            mainll.addView(tmpV);
        }
        return mainll;
    }

    /**
     *
     * @param fav string represents the name of the favorite item
     * @return false is not in fav, true if already in
     */
    private boolean isFavorite(String fav){
        // TODO : getfavorite hashmap ,check for existence od fav ,answer
        return false;
    }

    private void saveFavorites(ArrayList<String> favorites){
        // TODO : save user
    }


    public void addFavorite(){
        // TODO :  check if exist, add in hashMap, (refresh view), save
    }

    public void removeFavorite(){
        // TODO : get hashmap, remove from hashmap (refresh view, save
    }


}
