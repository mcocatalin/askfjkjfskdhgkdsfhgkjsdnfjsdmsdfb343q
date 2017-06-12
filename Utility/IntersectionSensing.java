package Utility;

import java.io.Serializable;

import static GEngine.graphicEngine.numberOfSensorperLane;

/**
 * Created by Catalin on 5/22/2017.
 */
public class IntersectionSensing implements Serializable { // Has information about intersection index lane -{ 0 - TO GO UP /// 1 - TO GO RIGHT /// 3 - TO GO DOWN /// 4 - TO GO LEFT } !!! And Sensor area number of cars.

    int laneDensity[][]; // Intersection lane density in clockwise direction starting with Upper

    public void setLaneDensity(int[][] laneDensity) {
        this.laneDensity = laneDensity;
    }

    public void setLaneDensityPerObj(int j, int k, int obj) {
        this.laneDensity[j][k] = obj;
    }

    public void DecrementNumberofCars(int lane, int decrement){
        for(int i=numberOfSensorperLane-1; i>= 0; i--){
            //if(this.laneDensity[lane][i] ) // metoda de update din graphic engine!!!
        }
    }

    public void initOrder(){
        if(this != null){
            int aux;

            int numberOfCarsperSensorArea = this.laneDensity[0][0];

            for(int i = 0; i < 4;i++){
                for(int j=0;j < numberOfSensorperLane-1;j++){
                    for(int k=j;k < numberOfSensorperLane;k++)
                    if(laneDensity[i][j]<laneDensity[i][k]){
                        aux = laneDensity[i][j];
                        laneDensity[i][j] = laneDensity[i][k];
                        laneDensity[i][k] = aux;

//
//
//                        if(numberOfCarsperSensorArea <= maxCarsPerSensingArea)
//                            result[j] = numberOfCarsperSensorArea;
//                        else
//                            result[j] =  maxCarsPerSensingArea;
//                        numberOfCarsperSensorArea = numberOfCarsperSensorArea - result[j];

                    }
                }
            }
        }
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
        laneDensity = new int[4][numberOfSensorperLane];

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
        laneDensity = new int[4][numberOfSensorperLane];
    }
}
