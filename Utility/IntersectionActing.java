package Utility;

import java.io.Serializable;

/**
 * Created by Catalin on 5/30/2017.
 */
public class IntersectionActing implements Serializable {
    boolean intersectionState[];

    public IntersectionActing() {
        intersectionState = new boolean[4];
        for(int i =0; i<4; i++){
            intersectionState[i] = false;
        }
    }

    public void setLaneDirection(boolean UpDown, boolean RightLeft){
        if(UpDown){
            intersectionState[0] = true;
            intersectionState[1] = false;
            intersectionState[2] = true;
            intersectionState[3] = false;
        }

        if(RightLeft){
            intersectionState[0] = false;
            intersectionState[1] = true;
            intersectionState[2] = false;
            intersectionState[3] = true;
        }
    }

    public void setIntersectionState(boolean Up, boolean Right, boolean Down, boolean Left){
        intersectionState[0] = Up;
        intersectionState[1] = Right;
        intersectionState[2] = Down;
        intersectionState[3] = Left;
    }
}