package db.management;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import objects.Places;

/**
 * Created by nowus on 20/04/2016.
 */
public class DataFlow {
    public DataFlow() {
    }

    public String loadJSONFromAsset(Context context,String filename) {
        String json = null;
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    public ArrayList<HashMap<String, String>> getListOfPlaces(Context context, String filename) throws JSONException {
        ArrayList<HashMap<String, String>> placeList = new ArrayList<HashMap<String, String>>();
        String jo_inside;
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(context,filename));
            if(obj == null){
                return null;
            }
            JSONArray restaurantsJsonArray = obj.getJSONArray("restaurants");
            JSONArray fastFoodJsonArray = obj.getJSONArray("fast_food");
            JSONArray bakeriesJsonArray = obj.getJSONArray("bakeries");
            for (int i = 0; i < restaurantsJsonArray.length(); i++) {
                HashMap<String, String> restaurants = new HashMap<String, String>();
                jo_inside = restaurantsJsonArray.getString(i);
                restaurants.put(jo_inside,"restaurant");
                placeList.add(restaurants);
            }
            for (int i = 0; i < fastFoodJsonArray.length(); i++) {
                HashMap<String, String> fast_food = new HashMap<String, String>();
                jo_inside = fastFoodJsonArray.getString(i);
                fast_food.put(jo_inside,"fast_food");
                placeList.add(fast_food);
            }
            for (int i = 0; i < bakeriesJsonArray.length(); i++) {
                HashMap<String, String> bakeries = new HashMap<String, String>();
                jo_inside = bakeriesJsonArray.getString(i);
                bakeries.put(jo_inside, "bakeries");
                placeList.add(bakeries);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return placeList;
    }
    public Places getSpecificPlaces(Context context, String filename) throws JSONException {
        Places requiredPlace = new Places();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(context,filename));
            JSONObject menu = obj.getJSONObject("menu");
            JSONArray starters = menu.getJSONArray("starters");
            JSONObject mainCourses = menu.getJSONObject("main_course");
            JSONArray deserts = menu.getJSONArray("deserts");
            JSONObject drinks = menu.getJSONObject("drinks");
            String name = obj.getString("name");
            String address = obj.getString("address");
            Double lon = obj.getDouble("lon");
            Double lat = obj.getDouble("lat");
            String type  = obj.getString("type");
            String description = obj.getString("description");
            String website = obj.getString("website");
            String phone = obj.getString("phone_number");
            Double rating = obj.getDouble("rating");
            requiredPlace = new Places(name,address,lon,lat,type,description,website,phone,rating,starters,mainCourses,deserts,drinks);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requiredPlace;
    }
    public boolean writeToFile(Context context, String data, String filename) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write("");
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (Exception e) {
            Log.e("Exception", "File write failed: " + e.toString());
            return false;
        }
        return true;
    }
    public String readFromFile(Context context,String filename) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            return "";
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
            return "";
        }

        return ret;
    }


    public String saveImageToInternalStorage(Bitmap bitmapImage,String filename, Context ctx) throws IOException {
        ContextWrapper cw = new ContextWrapper(ctx);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("mImages", Context.MODE_PRIVATE);
        // Create mImages
        File myPath=new File(directory,filename + ".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        return directory.getAbsolutePath() + "/" + filename + ".png";
    }

    public Bitmap loadImageFromStorage(String filename)
    {
        Bitmap b = null ;
        try {
            File f=new File(filename);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            Log.e("loadImageFromStorage ::", "Can not read file: " + e.toString());
        }
        return b;
    }



}