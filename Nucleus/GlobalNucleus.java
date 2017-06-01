package Nucleus;

import GEngine.graphicEngine;
import jade.core.*;
import jade.core.Runtime;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.leap.Iterator;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by Catalin on 5/22/2017.
 */
public class GlobalNucleus extends Agent {

    public static int GlobalNucleusSetPoint;
    public static LinkedList<AID> availableControllers;
    public static LinkedList<AID> disabledControllers;
    private int NucleusIndex;
    private boolean DoneCreatingAgents;

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

    Behaviour initBehaviour = new Behaviour() {
        @Override
        public void action() {
            availableControllers = new LinkedList<AID>();
            NucleusIndex = 0;
            DoneCreatingAgents = false;
            disabledControllers = new LinkedList<AID>();

        }

        @Override
        public boolean done() {
            return true;
        }
    };

    Behaviour updateSetPoint = new CyclicBehaviour() { // Send data to Nucleus to update setpoint
        @Override
        public void action() {
            if (DoneCreatingAgents) {
                NucleusIndex = NucleusIndex % graphicEngine.numberOfIntersections;
                Iterator it = getAID().getAllAddresses();
                String adresa = (String) it.next();
                String platforma = getAID().getName().split("@")[1];

                ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                AID r = new AID("IntersectionNucleus" + NucleusIndex + "@" + platforma, AID.ISGUID);
                r.addAddresses(adresa);
                messageToSend.setConversationId("UpdateSetPoint");
                messageToSend.addReceiver(r);

                try {
                    messageToSend.setContentObject(GlobalNucleusSetPoint);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myAgent.send(messageToSend);
                NucleusIndex++;
            }
        }
    };

    Behaviour createAgents = new CyclicBehaviour() {
        @Override
        public void action() {
            if(graphicEngine.startApplication && !DoneCreatingAgents) {

                Iterator it = getAID().getAllAddresses();
                String adresa = (String) it.next();
                String platforma = getAID().getName().split("@")[1];

                Runtime runtime = jade.core.Runtime.instance();
                ContainerController home = null;
                Profile p = new ProfileImpl();
                home= runtime.createMainContainer(p);

                // Maximum 9 vehicles, method to parse ID is checking only the last character of the Agent Local Name.

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
                        // Nucleus Agents
                        try {
                            AgentController rma = home.createNewAgent("IntersectionNucleus" + i,
                                    "Nucleus.Nucleus", new Object[0]);
                            rma.start();
                            availableControllers.add(new AID("IntersectionNucleus" + i + "@" + platforma, AID.ISGUID));
                        } catch (StaleProxyException e) {
                            e.printStackTrace();
                        }
                    }
                }
                DoneCreatingAgents = true;
            }

        }
    };


    Behaviour getDefectedControllers = new CyclicBehaviour() {
        @Override
        public void action() {
            // !!! Assign another controller to defected ones.

            Iterator it = getAID().getAllAddresses();
            String adresa = (String) it.next();
            String platforma = getAID().getName().split("@")[1];

            ACLMessage mesaj_receptionat = myAgent.receive();
            if(mesaj_receptionat!=null) {
                if (mesaj_receptionat.getConversationId() == "Defect") {
                    try {
                        int ID = (int) mesaj_receptionat.getContentObject();
                        disabledControllers.add(new AID("IntersectionNucleus" + ID + "@" + platforma, AID.ISGUID));
                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    Behaviour setServiceController = new CyclicBehaviour() {
        @Override
        public void action() { // To defected ones.
            if (disabledControllers.size()> 0 && availableControllers.size() > 0) {
                for (int i=0; i<disabledControllers.size(); i++) {

                    Iterator it = getAID().getAllAddresses();
                    String adresa = (String) it.next();
                    String platforma = getAID().getName().split("@")[1];

                    ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                    //AID r = new AID("IntersectionNucleus" + NucleusIndex + "@" + platforma, AID.ISGUID);
                    disabledControllers.get(i).addAddresses(adresa);
                    messageToSend.setConversationId("DefectSolver");
                    messageToSend.addReceiver( disabledControllers.get(i));

                    try {
                        messageToSend.setContentObject(availableControllers.get(i)); // send first AID available - to create a new fesable method for this
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myAgent.send(messageToSend);
                }
            }
        }
    };

    @Override
    protected void setup() {

        addBehaviour(initBehaviour);

        addBehaviour(updateSetPoint);

        addBehaviour(createAgents);

        addBehaviour(getDefectedControllers);

        addBehaviour(setServiceController);
    }
}
