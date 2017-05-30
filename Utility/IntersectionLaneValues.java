package Utility;

import java.io.Serializable;

/**
 * Created by Catalin on 5/22/2017.
 */
public class IntersectionLaneValues implements Serializable {
    int UpperDensity;
    int LowerDensity;
    int LeftDensity;
    int RightDensity;

    public boolean equals(IntersectionLaneValues obj) {
        return ((this.UpperDensity == obj.UpperDensity) && (this.LowerDensity == obj.LowerDensity) && (this.RightDensity == obj.RightDensity) && (this.LeftDensity == obj.LeftDensity));
    }

    @Override
    public String toString() {
        return "UpperDensity="+UpperDensity+"LowerDensity="+LowerDensity+"LeftDensity= "+LeftDensity+"RightDensity= "+RightDensity;
    }

    public IntersectionLaneValues(int upperDensity, int lowerDensity, int leftDensity, int rightDensity) {
        UpperDensity = upperDensity;
        LowerDensity = lowerDensity;
        LeftDensity = leftDensity;
        RightDensity = rightDensity;
    }

    public int getMaxDensity(){
        int maxValue = UpperDensity;
        if(maxValue <= LowerDensity)
            maxValue = LowerDensity;
        if(maxValue <= LeftDensity)
            maxValue = LeftDensity;
        if(maxValue <= RightDensity)
            maxValue = RightDensity;

        return maxValue;
    }

    public IntersectionLaneValues() {
    }
}
