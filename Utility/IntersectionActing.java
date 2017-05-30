package Utility;

import java.io.Serializable;

/**
 * Created by Catalin on 5/30/2017.
 */
public class IntersectionActing implements Serializable {
    private boolean UpperState;
    private boolean LowerState;
    private boolean RightState;
    private boolean LeftState;

    public boolean isUpperState() {
        return UpperState;
    }

    public void setUpperState(boolean upperState) {
        UpperState = upperState;
    }

    public boolean isLowerState() {
        return LowerState;
    }

    public void setLowerState(boolean lowerState) {
        LowerState = lowerState;
    }

    public boolean isRightState() {
        return RightState;
    }

    public void setRightState(boolean rightState) {
        RightState = rightState;
    }

    public boolean isLeftState() {
        return LeftState;
    }

    public void setLeftState(boolean leftState) {
        LeftState = leftState;
    }

    public IntersectionActing() {

    }

    public IntersectionActing(boolean upperState, boolean lowerState, boolean rightState, boolean leftState) {

        UpperState = upperState;
        LowerState = lowerState;
        RightState = rightState;
        LeftState = leftState;
    }
}
