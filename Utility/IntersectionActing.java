package Utility;

import java.io.Serializable;

/**
 * Created by Catalin on 5/30/2017.
 */
public class IntersectionActing implements Serializable {
    boolean pairedLane;
    boolean intersectionState[];

    public IntersectionActing() {
        intersectionState = new boolean[4];
        for(int i =0; i<4; i++){
            intersectionState[i] = false;
        }
    }

    public void setLaneByTrafficLightID(int ID){
        pairedLane = false;
        if(ID == 0)
            this.setIntersectionState(true, false,false,false, false);

        if(ID == 1)
            this.setIntersectionState(false,true, false,false, false);

        if(ID == 2)
            this.setIntersectionState( false,false,true,false, false);

        if(ID == 3)
            this.setIntersectionState(false,false,false,true , false);

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

        pairedLane = true;
    }

    public void setIntersectionState(boolean Up, boolean Right, boolean Down, boolean Left, boolean pairedLane){
        this.pairedLane = pairedLane;

        intersectionState[0] = Up;
        intersectionState[1] = Right;
        intersectionState[2] = Down;
        intersectionState[3] = Left;
    }

    public boolean Equals(IntersectionActing act){

            if (    this.intersectionState[0] == act.intersectionState[0] &&
                    this.intersectionState[1] == act.intersectionState[1] &&
                    this.intersectionState[2] == act.intersectionState[2] &&
                    this.intersectionState[3] == act.intersectionState[3] &&
                    this.pairedLane == act. pairedLane)
                return true;

        return false;
    }

    public boolean[] getIntersectionState() {
        return intersectionState;
    }

    public boolean getIntersectionActingPairedState() { return  pairedLane;}
}
