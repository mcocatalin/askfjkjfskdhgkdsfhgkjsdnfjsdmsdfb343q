package GEngine;

import Utility.IntersectionLaneValues;

/**
 * Created by Catalin on 5/22/2017.
 */
public class sensingHandler {

    private String type;
    private int componentID;
    private IntersectionLaneValues objToHandle;


    public sensingHandler() {

    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public boolean equals(sensingHandler obj) {
        return ((this.type == obj.type) && (this.componentID == obj.componentID) && (this.objToHandle.equals(obj.objToHandle)));
    }

    public sensingHandler(String type, int componentID, IntersectionLaneValues objToHandle) {

        this.type = type;
        this.componentID = componentID;
        this.objToHandle = objToHandle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getComponentID() {
        return componentID;
    }

    public void setComponentID(int componentID) {
        this.componentID = componentID;
    }

    public IntersectionLaneValues getObjToHandle() {
        return objToHandle;
    }

    public void setObjToHandle(IntersectionLaneValues objToHandle) {
        this.objToHandle = objToHandle;
    }

}
