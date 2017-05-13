import com.jme3.math.Vector3f;

/**
 * Created by Catalin on 5/9/2017.
 */
public class Helper {

    public static boolean IsInFront(Vector3f center){
        Vector3f toCheck = center;
        toCheck.setX(toCheck.getX() + 10);

        return true;
    }
}
