package Utility;

import GEngine.graphicEngine;

import java.io.Serializable;

/**
 * Created by Catalin on 5/22/2017.
 */
public class IntersectionSensing implements Serializable {

    int laneDensity[][]; // Intersection lane density in clockwise direction starting with Upper

    public void setLaneDensity(int[][] laneDensity) {
        this.laneDensity = laneDensity;
    }

    public void setLaneDensityPerObj(int j, int k, int obj) {
        this.laneDensity[j][k] = obj;

        //                    for (int count = 0; count < result.length / 2; ++count) {
//                        aux = result[count];
//                        result[count] = result[result.length - count - 1];
//                        result[result.length - count - 1] = aux;
//                    }

    }

    public void setLaneDensity(int index, int[] density){
        if(this.laneDensity[index] == null) {
            this.laneDensity[index] = new int[4];
            //if (this.laneDensity[index] == null)

        }
        this.laneDensity[index] = density;
    }

    public boolean equals(IntersectionSensing obj) {
            for(int i=0; i<laneDensity.length; i++){
                if(! (obj.laneDensity[i] == laneDensity[i]))
                    return false;
            }
        return true;
    }

    public IntersectionSensing(int[] upperDensity, int[] lowerDensity, int[] leftDensity, int[] rightDensity) {
        laneDensity = new int[4][graphicEngine.numberOfSensorperLane];

        laneDensity[0] = upperDensity;
        laneDensity[1] = rightDensity;
        laneDensity[2] = lowerDensity;
        laneDensity[3] = leftDensity;
    }

    public int[] getMaxDensity(){

        int sectionDensity[] = new int[2];

        int maxDensityUpDown=laneDensity[0][0] + laneDensity[0][1] + laneDensity[0][2] + laneDensity[2][0] + laneDensity[2][1] + laneDensity[2][2] ;
        int maxDensityRightLeft=laneDensity[1][0] + laneDensity[1][1] + laneDensity[1][2] + laneDensity[3][0] + laneDensity[3][1] + laneDensity[3][2] ;

        sectionDensity[0] = maxDensityUpDown;
        sectionDensity[1] = maxDensityRightLeft;

        return sectionDensity;
    }

    public IntersectionSensing() {
        laneDensity = new int[4][graphicEngine.numberOfSensorperLane];
    }
}
