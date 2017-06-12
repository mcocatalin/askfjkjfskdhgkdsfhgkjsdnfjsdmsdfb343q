package Utility;

/**
 * Created by Catalin on 6/4/2017.
 */
public class IntersectionItemGraph {
    int ComponentID;

    IntersectionSensing intersectionSensing;

    IntersectionActing intersectionActing;

    IntersectionItemGraph isLeftNeighbourNucleusID;
    IntersectionItemGraph isRightNeighbourNucleusID;
    IntersectionItemGraph isUpNeighbourNucleusID;
    IntersectionItemGraph isDownNeighbouNucleusID;


    public IntersectionActing getIntersectionActing() {
        return intersectionActing;
    }

    public void setIntersectionActing(IntersectionActing intersectionActing) {
        this.intersectionActing = intersectionActing;
    }


    public IntersectionSensing getIntersectionSensing() {
        return intersectionSensing;
    }

    public void setIntersectionSensing(IntersectionSensing intersectionSensing) {
        this.intersectionSensing = intersectionSensing;
    }

    public IntersectionItemGraph(int componentID) {
        ComponentID = componentID;

    }

    public IntersectionItemGraph(int componentID, IntersectionItemGraph isLeftNeighbour, IntersectionItemGraph isRightNeighbour, IntersectionItemGraph isUpNeighbour, IntersectionItemGraph isDownNeighbour) {
        ComponentID = componentID;

        this.isLeftNeighbourNucleusID = isLeftNeighbour;
        this.isRightNeighbourNucleusID = isRightNeighbour;
        this.isUpNeighbourNucleusID = isUpNeighbour;
        this.isDownNeighbouNucleusID = isDownNeighbour;
    }

    public IntersectionItemGraph() {

        ComponentID = -1;

        isLeftNeighbourNucleusID = new IntersectionItemGraph(-1);
        isRightNeighbourNucleusID = new IntersectionItemGraph(-1);
        isUpNeighbourNucleusID = new IntersectionItemGraph(-1);
        isDownNeighbouNucleusID = new IntersectionItemGraph(-1);
    }

    public int getComponentID() {
        return ComponentID;
    }

    public void setComponentID(int componentID) {
        ComponentID = componentID;
    }

    public IntersectionItemGraph isLeftNeighbour() {
        return isLeftNeighbourNucleusID;
    }

    public void setLeftNeighbour(IntersectionItemGraph leftNeighbour) {
        isLeftNeighbourNucleusID = leftNeighbour;
    }

    public IntersectionItemGraph isRightNeighbour() {
        return isRightNeighbourNucleusID;
    }

    public void setRightNeighbour(IntersectionItemGraph rightNeighbour) {
        isRightNeighbourNucleusID = rightNeighbour;
    }

    public IntersectionItemGraph isUpNeighbour() {
        return isUpNeighbourNucleusID;
    }

    public void setUpNeighbour(IntersectionItemGraph upNeighbour) {
        isUpNeighbourNucleusID = upNeighbour;
    }

    public IntersectionItemGraph isDownNeighbour() {
        return isDownNeighbouNucleusID;
    }

    public void setDownNeighbour(IntersectionItemGraph downNeighbour) {
        isDownNeighbouNucleusID = downNeighbour;
    }
}
