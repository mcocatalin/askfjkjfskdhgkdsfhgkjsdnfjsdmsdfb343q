package Utility;

import com.jme3.math.Vector3f;

/**
 * Created by Catalin on 5/23/2017.
 */
public class IntersectionItem {
    Vector3f UpperLocation;
    Vector3f LowerLocation;
    Vector3f RightLocation;
    Vector3f LeftLocation;


    public IntersectionItem(Vector3f upperLocation, Vector3f lowerLocation, Vector3f rightLocation, Vector3f leftLocation) {
        UpperLocation = upperLocation;
        LowerLocation = lowerLocation;
        RightLocation = rightLocation;
        LeftLocation = leftLocation;
    }

    public IntersectionItem() {
    }


    public Vector3f getUpperLocation() {
        return UpperLocation;
    }

    public void setUpperLocation(Vector3f upperLocation) {
        UpperLocation = upperLocation;
    }

    public Vector3f getLowerLocation() {
        return LowerLocation;
    }

    public void setLowerLocation(Vector3f lowerLocation) {
        LowerLocation = lowerLocation;
    }

    public Vector3f getRightLocation() {
        return RightLocation;
    }

    public void setRightLocation(Vector3f rightLocation) {
        RightLocation = rightLocation;
    }

    public Vector3f getLeftLocation() {
        return LeftLocation;
    }

    public void setLeftLocation(Vector3f leftLocation) {
        LeftLocation = leftLocation;
    }
}
