package Utility;

import com.jme3.math.Vector3f;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Catalin on 5/9/2017.
 */
public class Helper {

    public static HashMap< String, Integer > IntersectionPoint = new HashMap< String, Integer >(){{
        put("UpperPoint",1);
        put("LowerPoint",2);
        put("RightPoint",3);
        put("LeftPoint",4);
    }};



    public static boolean IsInFront(Vector3f center){
        Vector3f toCheck = center;
        toCheck.setX(toCheck.getX() + 10);

        return true;
    }

    public static String ReadConfigurationFile() {
        String result = null;

        try(BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Catalin\\IdeaProjects\\citySCAPE\\Utility\\Configuration.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();

            }

            result = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static LinkedList<String> GetAIDs(){
        LinkedList<String> result = new LinkedList<String>();
        String pattern1 = ";";
        String pattern2 = ":";
        String auxiliary = ReadConfigurationFile();

        Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
        Matcher m = p.matcher(auxiliary);
        while (m.find()) {
            result.add(m.group(1));
        }
    return result;
    }
}
