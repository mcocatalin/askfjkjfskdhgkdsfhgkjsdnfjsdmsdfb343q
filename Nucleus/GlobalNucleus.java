package Nucleus;

import GEngine.graphicEngine;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.behaviours.CyclicBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 * Created by Catalin on 5/22/2017.
 */
public class GlobalNucleus extends Agent {

    public static int GlobalNucleusSetPoint;
    public static boolean[] disableTrafficSystemIndex = new boolean[graphicEngine.numberOfIntersections];

//    public static void createAgents(){
//        jade.core.Runtime runtime = jade.core.Runtime.instance();
//        home = null;
//        Profile p = new ProfileImpl();
//        home = runtime.createMainContainer(p);
//        for(int i = 0; i < 2; i++) {
//
//            try {
//                Object[] args = new Object[1];
//                args[0]= (Integer) i;
//                AgentController a = home.createNewAgent(String.valueOf(i), Human.class.getName(), args);
//                a.start();  //acum va porni metoda setup() a agentului pe un thread separat
//            } catch (StaleProxyException e1) {
//                e1.printStackTrace();
//            }
//
//        }
//    }



    private boolean done = false;
    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
            if(graphicEngine.startApplication && !done) {

                Runtime runtime = jade.core.Runtime.instance();
                ContainerController home = null;
                Profile p = new ProfileImpl();
                home= runtime.createMainContainer(p);// acum ruleaza JADE pe calculatorul curent
//porneste interfata de la JADE (merge si fara asta)

                // Start number of vehicles # to continue!
                if(graphicEngine.numberOfCars >0) {
                    for (int i = 0; i < graphicEngine.numberOfCars; i++) {
                        // Sensing Agents
                        try {
                            AgentController rma = home.createNewAgent("VehicleSensing" + i,
                                    "Sensing.SensingAgent", new Object[0]);
                            rma.start();
                            // to print in console!!!
                        } catch (StaleProxyException e) {
                            e.printStackTrace();
                        }

                        // Controlling Agents
                        try {
                            AgentController rma = home.createNewAgent("VehicleController" + i,
                                    "Controlling.VehicleController", new Object[0]);
                            rma.start();
                            // to print in console!!!
                        } catch (StaleProxyException e) {
                            e.printStackTrace();
                        }

                        // Acting Agents
                        try {
                            AgentController rma = home.createNewAgent("VehicleActing" + i,
                                    "Acting.ActingAgent", new Object[0]);
                            rma.start();
                            // to print in console!!!
                        } catch (StaleProxyException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(graphicEngine.numberOfIntersections>0) {

                    for (int i = 0; i < graphicEngine.numberOfIntersections; i++) {
                        // Start number of Intersections # to continue!

                        // Sensing Agents
                        try {
                            AgentController rma = home.createNewAgent("IntersectionSensing" + i,
                                    "Sensing.SensingAgent", new Object[0]);
                            rma.start();
                            // to print in console!!!
                        } catch (StaleProxyException e) {
                            e.printStackTrace();
                        }

                        // Controlling Agents
                        try {
                            AgentController rma = home.createNewAgent("IntersectionController" + i,
                                    "Controlling.IntersectionController", new Object[0]);
                            rma.start();
                            // to print in console!!!
                        } catch (StaleProxyException e) {
                            e.printStackTrace();
                        }

                        // Acting Agents
                        try {
                            AgentController rma = home.createNewAgent("IntersectionActing" + i,
                                    "Acting.ActingAgent", new Object[0]);
                            rma.start();
                            // to print in console!!!
                        } catch (StaleProxyException e) {
                            e.printStackTrace();
                        }

                        // Nucleus Agents
                        try {
                            AgentController rma = home.createNewAgent("IntersectionNucleus" + i,
                                    "Nucleus.Nucleus", new Object[0]);
                            rma.start();
                            // to print in console!!!
                        } catch (StaleProxyException e) {
                            e.printStackTrace();
                        }
                    }
                }
                done = true;
            }

            }
        });

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // !!! Check Intersection State for every nucleus
            }
        });
    }
}
