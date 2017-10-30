package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import polito.lab.anes.eatnow.R;

/**
 * Created by nowus on 20/04/2016.
 */
public class CustomListAdapter extends BaseAdapter implements SectionIndexer,Filterable {

    private Context ctx ;
    private HashMap<String,Integer> alphaIndexer;
    private String[] sections;
    private String[] items;
    private String[] originalValues;

    public CustomListAdapter(Context ctx,String[] items){
        this.ctx = ctx;
        this.items = new String[items.length];
        this.originalValues = new String[items.length];
        this.items = items;
        this.originalValues = items;
        alphaIndexer = new HashMap<String,Integer>();
        int size = items.length;
        for (int x = 0 ; x < size; x++){
            String s = items[x];
            String ch = s.substring(0,1);
            ch = ch.toUpperCase();
            if(!alphaIndexer.containsKey(ch)){
                alphaIndexer.put(ch,x);
            }
        }

        Set<String > sectionLetters = alphaIndexer.keySet();
        ArrayList<String > sectionList = new ArrayList<String >(sectionLetters);
        Collections.sort(sectionList);
        sections = new String[sectionList.size()];
        sectionList.toArray(sections);
    }


    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Typeface face=Typeface.createFromAsset(ctx.getAssets(),"fonts/Dolce_Vita_Light.ttf");

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.restaurant_item,parent,false);
        }
        TextView restaurantNameTxVAllList = (TextView) convertView.findViewById(R.id.restaurantNameTxVAllList);
        restaurantNameTxVAllList.setText(items[position]);
        restaurantNameTxVAllList.setTypeface(face);
        return  convertView;
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return alphaIndexer.get(sections[sectionIndex]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence)
            {
                FilterResults results = new FilterResults();

                //If there's nothing to filter on, return the original data for your list
                if(charSequence == null || charSequence.length() == 0)
                {
                    results.values =  new ArrayList<String>(Arrays.asList(originalValues));
                    results.count = originalValues.length;
                }
                else
                {
                    ArrayList<String> filterResultsData = new ArrayList<String>();

                    for(String data : originalValues)
                    {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
                        if(data.toLowerCase().contains(charSequence.toString().toLowerCase()))
                        {
                            filterResultsData.add(data);
                        }
                    }

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults)
            {
                ArrayList<String> tmp = (ArrayList<String>) filterResults.values;
                items = tmp.toArray(new String[tmp.size()]);
                notifyDataSetChanged();
            }
        };
    }
}