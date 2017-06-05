package Utility;

import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import jade.core.AID;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Nucleus.GlobalNucleus.availableNucleus;

/**
 * Created by Catalin on 5/9/2017.
 */
public class Helper {

    public static final String IntersectionNucleus = "IntersectionNucleus";
    public static final String IntersectionController = "IntersectionController";

    public static HashMap< String, Integer > IntersectionPoint = new HashMap< String, Integer >(){{
        put("UpperPoint",1);
        put("LowerPoint",2);
        put("RightPoint",3);
        put("LeftPoint",4);
    }};

    public static int checkGraphPosition(IntersectionItem intersectionItem1, IntersectionItem intersectionItem2){
        Plane plane = new Plane();

        for(int i = 0; i < intersectionItem1.getItemLocation().length/2; i++){
            plane.setOriginNormal(intersectionItem1.getItemLocation()[i], intersectionItem2.getItemLocation()[i+2]);
            //for(int j= i; j< intersectionItem2.getItemLocation().length;j++) {
                   if ( plane.whichSide(intersectionItem1.getItemLocation()[i+1]) == Plane.Side.Negative &&
                        plane.whichSide(intersectionItem2.getItemLocation()[i]) == Plane.Side.Negative &&
                        plane.whichSide(intersectionItem2.getItemLocation()[i+1]) == Plane.Side.Negative &&
                           i == 0
                        )
                       return 0;  // isUpNeighbourNucleusID

                    if ( plane.whichSide(intersectionItem1.getItemLocation()[i+1]) == Plane.Side.Positive &&
                            plane.whichSide(intersectionItem2.getItemLocation()[i]) == Plane.Side.Positive &&
                            plane.whichSide(intersectionItem2.getItemLocation()[i+1]) == Plane.Side.Positive &&
                            i == 1
                            )
                        return 1; // isDownNeighbouNucleusID

                    if ( plane.whichSide(intersectionItem1.getItemLocation()[i+1]) == Plane.Side.Positive &&
                            plane.whichSide(intersectionItem2.getItemLocation()[i]) == Plane.Side.Positive &&
                            plane.whichSide(intersectionItem2.getItemLocation()[i+1]) == Plane.Side.Positive &&
                            i == 0
                            )
                        return 2; // isRightNeighbourNucleusID

                    if ( plane.whichSide(intersectionItem1.getItemLocation()[i+1]) == Plane.Side.Negative &&
                            plane.whichSide(intersectionItem2.getItemLocation()[i]) == Plane.Side.Negative &&
                            plane.whichSide(intersectionItem2.getItemLocation()[i+1]) == Plane.Side.Negative &&
                            i == 1
                            )
                        return 3; // isLeftNeighbourNucleusID


        }
        return -1;
    }

    public static int getAvailableControllerAID(AID disabledControllerAID){

        CharSequence ComponentID = disabledControllerAID.getLocalName().substring(disabledControllerAID.getLocalName().length()-1);
        for (int i = 0; i< availableNucleus.size(); i++) {
            if(!availableNucleus.get(i).getLocalName().contains(ComponentID)){
                return i;
            }
        }
        return -1;
    }

//    public boolean ElapsedTime(int timePeriod){
//        TimerTask tmtsk = new TimerTask() {
//
//            @Override
//            public void run() {
//                Calendar dateTime = Calendar.getInstance();
//                System.out.println("\nPrint task");
//                try {
//                    Thread.sleep(1001);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Calendar dateTimeFinal = Calendar.getInstance();
//                long time = dateTimeFinal.getTimeInMillis()
//                        - dateTime.getTimeInMillis();
//                System.out.println("\nExecution time: " + time);
//            }
//        };
//        Timer tm = new Timer();
//        tm.scheduleAtFixedRate(tmtsk, 0, 1000);
//    }


    public static boolean IsInFront(Vector3f center){
        Vector3f toCheck = center;
        toCheck.setX(toCheck.getX() + 10);

        return true;
    }

    public static String ReadConfigurationFile() {
        String result = null;

        try(BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Catalin\\IdeaProjects\\CitySCAPE\\Utility\\Configuration.txt"))) {
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
