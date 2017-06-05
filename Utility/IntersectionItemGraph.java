package Utility;

/**
 * Created by Catalin on 6/4/2017.
 */
public class IntersectionItemGraph {
    int ComponentID;

    IntersectionItemGraph isLeftNeighbourNucleusID;
    IntersectionItemGraph isRightNeighbourNucleusID;
    IntersectionItemGraph isUpNeighbourNucleusID;
    IntersectionItemGraph isDownNeighbouNucleusIDr;

    public IntersectionItemGraph(int componentID) {
        ComponentID = componentID;

//        isLeftNeighbourNucleusID = new IntersectionItemGraph(-1);
//        isRightNeighbourNucleusID =  new IntersectionItemGraph(-1);
//        isUpNeighbourNucleusID =  new IntersectionItemGraph(-1);
//        isDownNeighbouNucleusIDr =  new IntersectionItemGraph(-1);
    }

    public IntersectionItemGraph(int componentID, IntersectionItemGraph isLeftNeighbour, IntersectionItemGraph isRightNeighbour, IntersectionItemGraph isUpNeighbour, IntersectionItemGraph isDownNeighbour) {
        ComponentID = componentID;

        this.isLeftNeighbourNucleusID = isLeftNeighbour;
        this.isRightNeighbourNucleusID = isRightNeighbour;
        this.isUpNeighbourNucleusID = isUpNeighbour;
        this.isDownNeighbouNucleusIDr = isDownNeighbour;
    }

    public IntersectionItemGraph() {

        ComponentID = -1;

        isLeftNeighbourNucleusID = new IntersectionItemGraph(-1);
        isRightNeighbourNucleusID = new IntersectionItemGraph(-1);
        isUpNeighbourNucleusID = new IntersectionItemGraph(-1);
        isDownNeighbouNucleusIDr = new IntersectionItemGraph(-1);
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
        return isDownNeighbouNucleusIDr;
    }

    public void setDownNeighbour(IntersectionItemGraph downNeighbour) {
        isDownNeighbouNucleusIDr = downNeighbour;
    }
}
