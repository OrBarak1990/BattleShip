package gamePack;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class SubTracker {
    private final SubMarine.Category category;
    private final int length, amount;
    private IntegerProperty live;


    public SubTracker(SubMarine.Category theCategory, int thLength, int theAmount){
        category = theCategory;
        length = thLength;
        amount = theAmount;
        live = new SimpleIntegerProperty(amount);
    }



    public boolean equals(SubMarine.Category category, int length){
        return this.category == category && this.length == length;
    }

    void decrease(){
        live.setValue(live.getValue() - 1);
    }

    public SubMarine.Category getCategory() {
        return category;
    }

    public int getLength() {
        return length;
    }

    public int getLive() {
        return live.get();
    }

    public IntegerProperty liveProperty() {
        return live;
    }

    public int getAmount() {
        return amount;
    }
}
