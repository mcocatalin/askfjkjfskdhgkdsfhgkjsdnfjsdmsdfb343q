package GEngine;

import Utility.IntersectionActing;

/**
 * Created by Catalin on 3/19/2017.
 */
public class actingHandler {
    public String type = "None";
    public int componentID;
    public IntersectionActing objToHandle;

    public boolean isWaitCycle() {
        return waitCycle;
    }

    public void setWaitCycle(boolean waitCycle) {
        this.waitCycle = waitCycle;
    }

    public boolean waitCycle;
    //Properties

    public actingHandler() {
    }

    public String getType() {

        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        actingHandler that = (actingHandler) o;

        if (getComponentID() != that.getComponentID()) return false;
        if (getType() != null ? !getType().equals(that.getType()) : that.getType() != null) return false;
        return getObjToHandle() != null ? getObjToHandle().equals(that.getObjToHandle()) : that.getObjToHandle() == null;
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + getComponentID();
        result = 31 * result + (getObjToHandle() != null ? getObjToHandle().hashCode() : 0);
        return result;
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

    public IntersectionActing getObjToHandle() {
        return objToHandle;
    }

    public void setObjToHandle(IntersectionActing objToHandle) {
        this.objToHandle = objToHandle;
    }

    public actingHandler(String type, int componentID, IntersectionActing objToHandle, boolean waitCycle) {
        this.type = type;
        this.componentID = componentID;
        this.objToHandle = objToHandle;
        this.waitCycle = waitCycle;
    }

    public actingHandler(String type, int componentID, IntersectionActing objToHandle) {

        this.type = type;
        this.componentID = componentID;
        this.objToHandle = objToHandle;
    }

    public boolean Equals(actingHandler act){

        if(this.getType() == act.getType() && this.getComponentID() == act.getComponentID() && this.getObjToHandle().Equals(act.getObjToHandle()) && this.waitCycle == act.waitCycle)
        {
            return true;
        }

        return false;
    }
}
