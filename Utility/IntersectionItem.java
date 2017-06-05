package Utility;

import com.jme3.math.Vector3f;

import java.io.Serializable;

/**
 * Created by Catalin on 5/23/2017.
 */
public class IntersectionItem implements Serializable{

    Vector3f itemLocation[]; // Item locations in clockwise direction, starting from Upper.
    boolean itemState[]; // Item state in clockwise direction, starting from Upper.

    public Vector3f[] getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(Vector3f[] itemLocation) {
        this.itemLocation = itemLocation;
    }

    public boolean[] getItemState() {
        return itemState;
    }

    public void setItemState(boolean[] itemState) {
        this.itemState = itemState;
    }



//    public IntersectionItem(Vector3f upperLocation, Vector3f lowerLocation, Vector3f rightLocation, Vector3f leftLocation) {
//        UpperLocation = upperLocation;
//        LowerLocation = lowerLocation;
//        RightLocation = rightLocation;
//        LeftLocation = leftLocation;
//        UpperState = false;
//        LowerState = false;
//        RightState = false;
//        LeftState = false;
//    }

    public IntersectionItem() {
        itemLocation = new Vector3f[4];
        itemState = new boolean[4];
    }

    public IntersectionItem(Vector3f up, Vector3f right, Vector3f down, Vector3f left) {
        itemLocation = new Vector3f[4];
        itemState = new boolean[4];

        itemLocation[0] = up;
        itemLocation[1] = right;
        itemLocation[2] = down;
        itemLocation[3] = left;

    }

}
