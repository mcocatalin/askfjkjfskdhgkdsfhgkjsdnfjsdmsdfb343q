package Utility;

import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import jade.core.AID;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static GEngine.graphicEngine.expiredCycleTime;
import static GEngine.graphicEngine.numberOfIntersections;
import static Nucleus.CoreAgent.availableNucleus;

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

    public static boolean DoneDecrementDelay[] = new boolean[4];

    public static long tStart = -1;


    public static void LogDebugUseData(int ID, long time, int UpLaneIntersectionValue, int RightIntersectionValue, int DownLaneIntersectionValue, int LeftLaneIntersectionValue, boolean state1, int state2){
//
//        try {
//            if (!log.exists()) {
//                System.out.println("We had to make a new file.");
//                log.createNewFile();
//            }
//
//            FileWriter fileWriter = new FileWriter(log, true);
//
//            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                System.out.println( time + " " + "ID: " + ID+1 + " Cycle interval: " +state1 + " " + " intersection state: " + state2 + " "+ UpLaneIntersectionValue + " " + RightIntersectionValue + " " + DownLaneIntersectionValue + " " + LeftLaneIntersectionValue);
//                bufferedWriter.newLine();
//
//                //bufferedWriter.write("Eroare_temperatura = " + (referinta_temperatura - temperatura) + " eroare umiditate = " + (referinta_umiditate - umiditate) + "\n" + "Comenzi: racire = " + racire + "; incalzire = " + incalzire + "; umidificator = " + umidificator + "; ventilator = " + (ventilator + surplus_comanda_ventilator) + "\n\n");
//                bufferedWriter.close();
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public static void EventsLog(int ID,  double time,  int UpLaneIntersectionValue, int RightIntersectionValue, int DownLaneIntersectionValue, int LeftLaneIntersectionValue, String event){
        File log = new File("Event" +  ".txt");

        try {
            if (!log.exists()) {
                System.out.println("We had to make a new file.");
                log.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(log, true);

            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                if(event != "None"){
                    bufferedWriter.write(event);
                }
                bufferedWriter.write( time + " " + UpLaneIntersectionValue + " " + RightIntersectionValue + " " + DownLaneIntersectionValue + " " + LeftLaneIntersectionValue);
                bufferedWriter.newLine();

                //bufferedWriter.write("Eroare_temperatura = " + (referinta_temperatura - temperatura) + " eroare umiditate = " + (referinta_umiditate - umiditate) + "\n" + "Comenzi: racire = " + racire + "; incalzire = " + incalzire + "; umidificator = " + umidificator + "; ventilator = " + (ventilator + surplus_comanda_ventilator) + "\n\n");
                bufferedWriter.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void LogFileData(int ID, long time, int UpLaneIntersectionValue, int RightIntersectionValue, int DownLaneIntersectionValue, int LeftLaneIntersectionValue){
        File log = new File("Controller" + ID + "LogFile" + ".txt");

        try {
            if (!log.exists()) {
                System.out.println("We had to make a new file.");
                log.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(log, true);

            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                bufferedWriter.write( time + " " + UpLaneIntersectionValue + " " + RightIntersectionValue + " " + DownLaneIntersectionValue + " " + LeftLaneIntersectionValue);
                bufferedWriter.newLine();

                //bufferedWriter.write("Eroare_temperatura = " + (referinta_temperatura - temperatura) + " eroare umiditate = " + (referinta_umiditate - umiditate) + "\n" + "Comenzi: racire = " + racire + "; incalzire = " + incalzire + "; umidificator = " + umidificator + "; ventilator = " + (ventilator + surplus_comanda_ventilator) + "\n\n");
                bufferedWriter.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

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

    public static String getIntersectionCoordinate(int id) {
        String intersectionCoordonateName = new String();

        switch (id) {
            case 0:
                intersectionCoordonateName = "centrale";
                break;
            case 1:
                intersectionCoordonateName = "din nord";
                break;
            case 2:
                intersectionCoordonateName = "din nord";
                break;
            case 3:
                intersectionCoordonateName = "din nord";
                break;
            case 4:
                intersectionCoordonateName = "din nord";
                break;

        }

        return intersectionCoordonateName;
    }


    public static TimerTask tmtsk = new TimerTask() {
        @Override
        public void run() {
            expiredCycleTime[0] = true;
        }
    };
    public static TimerTask tmtskNormal[] = new TimerTask[numberOfIntersections];

    public static boolean normalPhaseTick = false;

    public static TimerTask tmtskNormalPhase = new TimerTask() {
        @Override
        public void run() {
            normalPhaseTick = true;
        }
    };

    public static TimerTask decrementTask[] = new TimerTask[4];

    public static boolean go = false;

    public static TimerTask globalSimulator = new TimerTask() {
        @Override
        public void run() {
            go = true;
        }
    };

//    decrementTask[0] = new TimerTask(){
//
//        @Override
//        public void run() {
//            //System.out.println("\nStarted cycle cronometer task");
////            try {
////                Thread.sleep(Controlling.IntersectionController.cicleInterval);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//
//            DoneDecrementDelay[0] = true;
//            DoneDecrementDelay[1] = true;
//            DoneDecrementDelay[2] = true;
//            DoneDecrementDelay[3] = true;
//
//            // System.out.println("\nEnded cycle cronometer task" );
//        }
//    };


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
