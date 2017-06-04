package Utility;

import java.io.Serializable;

/**
 * Created by Catalin on 6/4/2017.
 */
public class WorldDetector implements Serializable {
    private String type;
    private int componentID;
    IntersectionItem intersectionItem;

    public WorldDetector() {
    }

    public WorldDetector(String type, int componentID, IntersectionItem intersectionItem) {
        this.type = type;
        this.componentID = componentID;
        this.intersectionItem = intersectionItem;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getComponentID() {
        return componentID;
    }

    public void setComponentID(int componentID) {
        this.componentID = componentID;
    }

    public IntersectionItem getIntersectionItem() {
        return intersectionItem;
    }

    public void setIntersectionItem(IntersectionItem intersectionItem) {
        this.intersectionItem = intersectionItem;
    }
}
