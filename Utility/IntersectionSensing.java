package Utility;

import java.io.Serializable;

/**
 * Created by Catalin on 5/22/2017.
 */
public class IntersectionSensing implements Serializable {
    int laneDensity[]; // Intersection lane density in clockwise direction starting with Upper

    public boolean equals(IntersectionSensing obj) {
            for(int i=0; i<laneDensity.length; i++){
                if(! (obj.laneDensity[i] == laneDensity[i]))
                    return false;
            }
        return true;
    }

    public IntersectionSensing(int upperDensity, int lowerDensity, int leftDensity, int rightDensity) {
        laneDensity = new int[4];

        laneDensity[0] = upperDensity;
        laneDensity[1] = rightDensity;
        laneDensity[2] = lowerDensity;
        laneDensity[3] = leftDensity;
    }

    public int[] getMaxDensity(){

        int sectionDensity[] = new int[2];

        sectionDensity[0] = laneDensity[0] + laneDensity[2];
        sectionDensity[1] = laneDensity[1] + laneDensity[3];

        return sectionDensity;
    }

    public IntersectionSensing() {
        laneDensity = new int[4];
    }
}
