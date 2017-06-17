package Utility;

import GEngine.graphicEngine;

import java.io.Serializable;

import static GEngine.graphicEngine.maxCarsPerSensingArea;
import static GEngine.graphicEngine.numberOfSensorperLane;

/**
 * Created by Catalin on 5/22/2017.
 */
public class IntersectionSensing implements Serializable { // Has information about intersection index lane -{ 0 - TO GO UP /// 1 - TO GO RIGHT /// 3 - TO GO DOWN /// 4 - TO GO LEFT } !!! And Sensor area number of cars.

    int laneDensity[][]; // Intersection lane density in clockwise direction starting with Upper

    public void setLaneDensity(int[][] laneDensity) {
        this.laneDensity = laneDensity;
    }

    public boolean IntersectionLaneDensityISFULL(int index){
        int laneResult = 0;
        for(int i=0;i<numberOfSensorperLane;i++){
            laneResult = laneResult + this.laneDensity[index][i];
        }

        return laneResult >= maxCarsPerSensingArea*numberOfSensorperLane;
    }

    public void setLaneDensityPerObj(int j, int k, int obj) {
        this.laneDensity[j][k] = obj;
    }

    public void setLaneDensity(int index, int laneDensity){
        if(laneDensity >= 0) {
            if (laneDensity > graphicEngine.numberOfSensorperLane * maxCarsPerSensingArea)
                laneDensity = graphicEngine.numberOfSensorperLane * maxCarsPerSensingArea;

            int numberOfCarsperSensorArea = laneDensity;

            for (int k = 0; k < numberOfSensorperLane; k++) {

                if (numberOfCarsperSensorArea <= maxCarsPerSensingArea)
                    this.laneDensity[index][k] = numberOfCarsperSensorArea;
                else
                    this.laneDensity[index][k] = maxCarsPerSensingArea;
                numberOfCarsperSensorArea = numberOfCarsperSensorArea - this.laneDensity[index][k];

            }
        }
    }

    public void UpdateLanes(){

//        int result[] = this.getMaxDensity();
//
//        int numberOfCarsperSensorArea = result[1] + result[2];

        int numberOfCarsperSensorArea = 0;

        int result;

        for (int j = 0; j < 4; j++) {

            numberOfCarsperSensorArea = this.getDensity(j);
                for (int k = 0; k < numberOfSensorperLane; k++) {

                if(numberOfCarsperSensorArea <= maxCarsPerSensingArea)
                    result = numberOfCarsperSensorArea;
                else
                    result =  maxCarsPerSensingArea;
                numberOfCarsperSensorArea = numberOfCarsperSensorArea - result;

                this.laneDensity[j][k] = result;
            }
        }
    }

    public void DecrementNumberofCars(int lane, int decrement, float probability, int interval){
       // for(int i=numberOfSensorperLane-1; i>= 0; i--){
            //if(this.laneDensity[lane][i] ) // metoda de update din graphic engine!!!
        if(this.getDensity(lane) - decrement >= 0)
            setLaneDensity(lane, this.getDensity(lane) - decrement);

        //}
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

    public IntersectionSensing(int[] upperDensity,int[] rightDensity, int[] lowerDensity, int[] leftDensity ) {
        laneDensity = new int[4][numberOfSensorperLane];

        laneDensity[0] = upperDensity;
        laneDensity[1] = rightDensity;
        laneDensity[2] = lowerDensity;
        laneDensity[3] = leftDensity;
    }

    public int getDensity(int index){

        int dens = 0;

        for(int i=0; i< numberOfSensorperLane; i++){
            dens = dens + this.laneDensity[index][i];
        }

        return dens;
    }

    public int[] getMaxDensity(){

        int sectionDensity[] = new int[2];

        int maxDensityUpDown = getDensity(0)+ getDensity(2);
        int maxDensityRightLeft = getDensity(1) + getDensity(3) ;

        sectionDensity[0] = maxDensityUpDown;
        sectionDensity[1] = maxDensityRightLeft;

        return sectionDensity;
    }

    public IntersectionSensing() {
        laneDensity = new int[4][numberOfSensorperLane];
    }
}
