package objects;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nowus on 5/13/2016.
 */
public class WishList implements Parcelable {

    public ArrayList<OrderItem> getListOfOrder() {
        return listOfOrder;
    }

    public void setListOfOrder(ArrayList<OrderItem> listOfOrder) {
        this.listOfOrder = listOfOrder;
    }

    private ArrayList<OrderItem> listOfOrder ;

    public WishList() {
        this.listOfOrder = new ArrayList<OrderItem>();
    }


    @Override
    public String toString() {
        String ret = "";
        if(listOfOrder != null){
            for(int i = 0 ; i< listOfOrder.size() ; i++){
                ret += listOfOrder.get(i).toString() + "--";
            }
        }

        return ret;
    }

    public boolean addOrder(OrderItem order){
        return this.listOfOrder.add(order);
    }
    public boolean removeOrder(OrderItem order){
        return this.listOfOrder.remove(order);
    }
    public OrderItem removeAt(int position) throws IndexOutOfBoundsException{
        return this.listOfOrder.remove(position);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject res = null;
        JSONArray orders = null;
        if(listOfOrder != null){
            res = new JSONObject();
            orders = new JSONArray();
            for(int i = 0 ; i< listOfOrder.size() ; i++){
                OrderItem oi = listOfOrder.get(i);

                JSONObject orderI = new JSONObject();
                JSONObject details = new JSONObject();

                details.put("item",oi.getItem());
                details.put("price",oi.getPrice());
                details.put("quantity",oi.getQuantity());

                orderI.put("order" + String.valueOf(i),details);
                orders.put(orderI);

                }
            res.put("orders",orders);
            }
        return res;
    }

    public double getTotal(){
        Double total = 0.0;
        if(this.listOfOrder != null){
            for(OrderItem ord:this.listOfOrder){
                total += ord.getPrice() * ord.getQuantity();
            }
        }
        return total;
    }


    protected WishList(Parcel in) {
        if (in.readByte() == 0x01) {
            listOfOrder = new ArrayList<OrderItem>();
            in.readList(listOfOrder, OrderItem.class.getClassLoader());
        } else {
            listOfOrder = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (listOfOrder == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(listOfOrder);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<WishList> CREATOR = new Parcelable.Creator<WishList>() {
        @Override
        public WishList createFromParcel(Parcel in) {
            return new WishList(in);
        }

        @Override
        public WishList[] newArray(int size) {
            return new WishList[size];
        }
    };
}