package objects;

/**
 * Created by nowus on 5/13/2016.
 */
public class OrderItem {
    private Double quantity;
    private String item;
    private Double price;
    public boolean fromStr;
    public OrderItem(Double quantity, String item, Double price) {
        this.quantity = quantity;
        this.item = item;
        this.price = price;
        this.fromStr = false;
    }
    public OrderItem(String str) {
        if(str.split(":::").length >= 3){
            this.quantity = Double.valueOf(str.split(":::")[0]);
            this.item = str.split(":::")[1];
            this.price = Double.valueOf(str.split(":::")[2]);
            this.fromStr = true;
        }else{
            this.fromStr = false;
        }
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


    @Override
    public String toString() {
        return this.getQuantity().toString() + ":::" +this.getItem() + ":::" + this.getPrice().toString();
    }
}
