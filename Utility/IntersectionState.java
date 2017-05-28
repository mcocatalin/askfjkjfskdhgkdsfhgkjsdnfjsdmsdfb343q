package Utility;

/**
 * Created by Catalin on 5/22/2017.
 */
public class IntersectionState {
    int UpperDensity;
    int LowerDensity;
    int LeftDensity;
    int RightDensity;

    public IntersectionState(int upperDensity, int lowerDensity, int leftDensity, int rightDensity) {
        UpperDensity = upperDensity;
        LowerDensity = lowerDensity;
        LeftDensity = leftDensity;
        RightDensity = rightDensity;
    }

    public IntersectionState() {
    }
}
