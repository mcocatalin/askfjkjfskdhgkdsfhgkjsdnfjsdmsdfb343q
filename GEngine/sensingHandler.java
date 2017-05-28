package GEngine;

/**
 * Created by Catalin on 5/22/2017.
 */
public class sensingHandler {

    private String type;
    private int componentID;
    private Object objToHandle;

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

    public Object getObjToHandle() {
        return objToHandle;
    }

    public void setObjToHandle(Object objToHandle) {
        this.objToHandle = objToHandle;
    }

    public sensingHandler() {

    }

    public sensingHandler(String type, int componentID, Object objToHandle) {

        this.type = type;
        this.componentID = componentID;
        this.objToHandle = objToHandle;
    }
}
