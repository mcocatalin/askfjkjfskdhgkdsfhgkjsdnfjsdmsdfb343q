package Nucleus;

import GEngine.graphicEngine;
import Utility.Helper;
import Utility.IntersectionItemGraph;
import Utility.WorldDetector;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.leap.Iterator;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.IOException;
import java.util.LinkedList;

import static GEngine.graphicEngine.*;

/**
 * Created by Catalin on 5/22/2017.
 */
public class CoreAgent extends Agent {

    public static int GlobalNucleusSetPoint;
    public static boolean automaticMode;

    // intern state for automatic/manual switch
    boolean doneCreatingManualActuators;

    public static LinkedList<AID> availableNucleus;
    public static LinkedList<AID> disabledControllers;
    private int NucleusIndex;
    private boolean doneCreatingInfrastructureAgents;
    private boolean doneInitBehaviour;
    private static int requestedServiceController;
    ContainerController home = null;

    public static LinkedList<IntersectionItemGraph> LocationGraph;

    LinkedList<WorldDetector> wd;
    int detectedWorldItems;
    boolean doneDetecting;
    public static boolean doneProcessingNucleusesLocation;


    //State of intersections density of vehicles !!!
    private int density[][];

    private boolean centralControl;

    public static AgentController rma; // Same as CoreAgent setted in iniBehaviour

    Behaviour discoverAgents = new Behaviour() {
        @Override
        public void action() { // To descover all agents in current container from Agent Management System AMS
            AMSAgentDescription[] agents = null;

            try {
                SearchConstraints c = new SearchConstraints();
                c.setMaxResults ( new Long(-1) );
                agents = AMSService.search( myAgent, new AMSAgentDescription (), c );
            }
            catch (Exception e) {

            }
        }

        @Override
        public boolean done() {
            return true;
        }
    };

    Behaviour createWorldNet = new CyclicBehaviour() {
        @Override
        public void action() {
            if(wd != null){
                if(wd.size() == graphicEngine.numberOfIntersections ) {
                    if (!doneDetecting) {
                        for (int i = 0; i < wd.size(); i++)
                            LocationGraph.add(new IntersectionItemGraph(wd.get(i).getComponentID()));
                        doneDetecting = true;

                        LocationGraph.get(0).setUpNeighbour(LocationGraph.get(1));
                        LocationGraph.get(0).setLeftNeighbour(LocationGraph.get(2));
                        LocationGraph.get(0).setDownNeighbour(LocationGraph.get(3));
                        LocationGraph.get(0).setRightNeighbour(LocationGraph.get(4));

                        LocationGraph.get(1).setDownNeighbour(LocationGraph.get(0));

                        LocationGraph.get(2).setRightNeighbour(LocationGraph.get(0));

                        LocationGraph.get(3).setUpNeighbour(LocationGraph.get(0));

                        LocationGraph.get(4).setLeftNeighbour(LocationGraph.get(0));
//                    }
//                    else // TO REVISE
//                        if(!doneProcessingNucleusesLocation)
//                        {
//                        for (int i = 0; i < wd.size() - 1; i++) {
//                            for (int j = i + 1; j < wd.size(); j++) {
//                                int side = Helper.checkGraphPosition(wd.get(i).getIntersectionItem(), wd.get(j).getIntersectionItem());
//                                switch (side) {
//                                    case 0:
//                                        LocationGraph.get(i).setUpNeighbour(LocationGraph.get(j));
//                                        LocationGraph.get(j).setDownNeighbour(LocationGraph.get(i));
//                                        break;
//                                    case 1:
//                                        LocationGraph.get(i).setDownNeighbour(LocationGraph.get(j));
//                                        LocationGraph.get(j).setUpNeighbour(LocationGraph.get(i));
//                                        break;
//                                    case 2:
//                                        LocationGraph.get(i).setRightNeighbour(LocationGraph.get(j));
//                                        LocationGraph.get(j).setLeftNeighbour(LocationGraph.get(i));
//                                        break;
//                                    case 3:
//                                        LocationGraph.get(i).setLeftNeighbour(LocationGraph.get(j));
//                                        LocationGraph.get(j).setRightNeighbour(LocationGraph.get(i));
//                                        break;
//
//                                }
//
//                            }
//
//                        }
                            doneProcessingNucleusesLocation = true;

                        EventLogEntries.add("Agentul Core a detectat reteaua de intersectii.");
                    }
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //block();
        }


    };

    Behaviour initBehaviour = new Behaviour() {
        @Override
        public void action() {
            availableNucleus = new LinkedList<AID>();
            NucleusIndex = 0;
            doneCreatingInfrastructureAgents = false;
            disabledControllers = new LinkedList<AID>();
            requestedServiceController = 0;
            automaticMode= false;

            doneCreatingManualActuators = false;

            home = this.myAgent.getContainerController();

            density = new int[graphicEngine.numberOfIntersections][2];

            centralControl = false;

            detectedWorldItems = 0;
            wd = new LinkedList<WorldDetector>();
            LocationGraph = new LinkedList<IntersectionItemGraph>();

            doneDetecting = false;
            doneProcessingNucleusesLocation = false;

            EventLogEntries.add("Agentul Core principal este lansat in executie!");

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        @Override
        public boolean done() {
            return true;
        }


    };

    Behaviour updateStates = new CyclicBehaviour() { // Send data to Nucleus to update setpoint
        @Override
        public void action() {
            if (doneCreatingInfrastructureAgents && doneDetecting && automaticMode ) {
                for(NucleusIndex = 0; NucleusIndex< numberOfIntersections; NucleusIndex++) {

                    Iterator it = getAID().getAllAddresses();
                    String adresa = (String) it.next();
                    String platforma = getAID().getName().split("@")[1];

                    ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                    AID r = new AID("IntersectionNucleus" + NucleusIndex + "@" + platforma, AID.ISGUID);
                    r.addAddresses(adresa);
                    if (centralControl) { // TO IMPLEMENT!!!
                        messageToSend.setConversationId("CentralizedControl");
                        //messageToSend.setContentObject(new );
                    }
                    messageToSend.setConversationId("UpdateSetPoint");
                    messageToSend.addReceiver(r);

                    try {
                        messageToSend.setContentObject(GlobalNucleusSetPoint);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myAgent.send(messageToSend);
                }
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    Behaviour createEnvironment = new Behaviour() {
        @Override
        public void action() {

            try {
                rma = home.createNewAgent("Environment" ,
                        "src.CitySCAPE", new Object[0]);
                rma.start();
                //graphicEngine.EventLogEntries.add("Created " )
                // to print in console!!!
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean done() {
            return true;
        }
    };

    CyclicBehaviour createAgents = new CyclicBehaviour() {
        @Override
        public void action() {

            if (graphicEngine.startApplication && !automaticMode && !doneCreatingManualActuators) {

                // Start number of vehicles # to continue!
//                if(graphicEngine.numberOfCars >0) {
//                    for (int i = 0; i < graphicEngine.numberOfCars; i++) {
//                        // Sensing Agents
//                        //if(AMSService.)
//                        try {
//                            rma = home.createNewAgent("VehicleSensing" + i,
//                                    "Sensing.SensingAgent", new Object[0]);
//                            rma.start();
//                            //graphicEngine.EventLogEntries.add("Created " )
//                            // to print in console!!!
//                        } catch (StaleProxyException e) {
//                            e.printStackTrace();
//                        }
//
//                        // Controlling Agents
//                        try {
//                            rma = home.createNewAgent("VehicleController" + i,
//                                    "Controlling.VehicleController", new Object[0]);
//                            rma.start();
//                            // to print in console!!!
//                        } catch (StaleProxyException e) {
//                            e.printStackTrace();
//                        }
//
//                        // Acting Agents
//                        try {
//                            rma = home.createNewAgent("VehicleActing" + i,
//                                    "Acting.ActingAgent", new Object[0]);
//                            rma.start();
//                            // to print in console!!!
//                        } catch (StaleProxyException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }

                if (graphicEngine.numberOfIntersections > 0) {

                    for (int i = 0; i < graphicEngine.numberOfIntersections; i++) {

                        // Start number of Intersections # to continue!
                        // Acting Agents
                        if (!doneCreatingManualActuators) {
                            try {
                                rma = home.createNewAgent("IntersectionActing" + i,
                                        "Acting.ActingAgent", new Object[0]);
                                rma.start();
                                // to print in console!!!
                                graphicEngine.EventLogEntries.add("Agent element de actionare cu ID-ul " + i + " este lansat\n  in executie.");
                            } catch (StaleProxyException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    doneCreatingManualActuators = true;
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (startApplication && automaticMode && !doneCreatingInfrastructureAgents) {


                String platforma = getAID().getName().split("@")[1];
                if (graphicEngine.numberOfIntersections > 0) {


                    for (int i = 0; i < graphicEngine.numberOfIntersections; i++) {

                            // Start number of Intersections # to continue!
                            // Acting Agents
                            if (!doneCreatingManualActuators) {
                                try {
                                    rma = home.createNewAgent("IntersectionActing" + i,
                                            "Acting.ActingAgent", new Object[0]);
                                    rma.start();
                                    // to print in console!!!
                                    graphicEngine.EventLogEntries.add("Agent element de actionare cu ID-ul " + i + " este lansat\n  in executie.");
                                } catch (StaleProxyException e) {
                                    e.printStackTrace();
                                }
                            }


                        // Nucleus Agents
                        try {
                            AgentController rma = home.createNewAgent("IntersectionNucleus" + i,
                                    "Nucleus.Nucleus", new Object[0]);
                            rma.start();
                            availableNucleus.add(new AID("IntersectionNucleus" + i + "@" + platforma, AID.ISGUID));
                            graphicEngine.EventLogEntries.add("Agent nucleu celula cu ID-ul " + i + " este lansat in executie.");
                        } catch (StaleProxyException e) {
                            e.printStackTrace();
                        }
                        // Controlling Agents
                        try {
                            rma = home.createNewAgent("IntersectionController" + i,
                                    "Controlling.IntersectionController", new Object[0]);
                            rma.start();
                            // to print in console!!!
                            graphicEngine.EventLogEntries.add("Agent controller cu ID-ul " + i + " este lansat in executie.");
                        } catch (StaleProxyException e) {
                            e.printStackTrace();
                        }
                        // Sensing Agents
                        try {
                            // home.getAgent("IntersectionSensing" + i);
                            rma = home.createNewAgent("IntersectionSensing" + i,
                                    "Sensing.SensingAgent", new Object[0]);
                            rma.start();
                            // to print in console!!!
                            graphicEngine.EventLogEntries.add("Agent senzor cu ID-ul " + i + " este lansat in executie.");
                        } catch (StaleProxyException e) {
                            e.printStackTrace();
                        }
                    }
                    if(!doneCreatingManualActuators){
                        doneCreatingManualActuators = true;
                    }
                    doneCreatingInfrastructureAgents = true;
                }

            }
        }
    };


    CyclicBehaviour getDefectedControllers = new CyclicBehaviour() {
        @Override
        public void action() {

            ACLMessage mesaj_receptionat = myAgent.receive();
            if (mesaj_receptionat != null) {
                if(doneDetecting) {
                    // !!! Assign another controller to defected ones.

                        if (mesaj_receptionat.getConversationId() == "Defect") {
                            try {
                                AID defectedAID = (AID) mesaj_receptionat.getContentObject();
                                disabledControllers.add(defectedAID);
                                //availableNucleus.remove(defectedAID);
                            } catch (UnreadableException e) {
                                e.printStackTrace();
                            }
                            requestedServiceController++;
                        }

                        if (mesaj_receptionat.getConversationId() == "CentralizedControl") {

                            try {
                                int NucleusID = Integer.parseInt(mesaj_receptionat.getSender().getLocalName().substring(mesaj_receptionat.getSender().getLocalName().length()-1)); // Send Feedback to IntersectionController

                                density[NucleusID] = (int[]) mesaj_receptionat.getContentObject();
                            } catch (UnreadableException e) {
                                e.printStackTrace();
                            }

                            if( density != null && density.length == graphicEngine.numberOfIntersections)
                                centralControl = true;
                        }
                    }
                else{
                    if (mesaj_receptionat.getConversationId() == "WorldDetect") {
                        try {
                            wd = (LinkedList<WorldDetector>) mesaj_receptionat.getContentObject();
                        } catch (UnreadableException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    Behaviour setServiceController = new CyclicBehaviour() {
        @Override
        public void action() { // To defected ones.
            if (disabledControllers.size() > 0 && availableNucleus.size() > 0 && requestedServiceController>0 && doneDetecting) {
                for (int i=0; i<disabledControllers.size(); i++) {

                    int defectID = Integer.parseInt(disabledControllers.get(i).getLocalName().substring(disabledControllers.get(i).getLocalName().length()-1));
                    int defectSolverID = 0;

                    Iterator it = getAID().getAllAddresses();
                    String adresa = (String) it.next();
                    String platforma = getAID().getName().split("@")[1];

                    ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                    AID r = new AID(Helper.IntersectionNucleus + defectID + "@" + platforma, AID.ISGUID);
                    disabledControllers.get(i).addAddresses(adresa);
                    messageToSend.setConversationId("DefectSolver");
                    messageToSend.addReceiver(r);

                    for(int count = 0; count < LocationGraph.size(); count ++) {
                        if (LocationGraph.get(count).getComponentID() == defectID){
                            if(LocationGraph.get(count).isUpNeighbour()!= null)
                                defectSolverID = LocationGraph.get(count).isUpNeighbour().getComponentID();
                            else
                            if(LocationGraph.get(count).isLeftNeighbour()!= null)
                                defectSolverID = LocationGraph.get(count).isLeftNeighbour().getComponentID();
                            else
                            if(LocationGraph.get(count).isRightNeighbour()!= null)
                                defectSolverID = LocationGraph.get(count).isRightNeighbour().getComponentID();
                            else
                            if(LocationGraph.get(count).isDownNeighbour()!= null)
                                defectSolverID = LocationGraph.get(count).isDownNeighbour().getComponentID();
                        }
                    }


                    try {
                        messageToSend.setContentObject( new AID(Helper.IntersectionController + defectSolverID + "@" + platforma, AID.ISGUID));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myAgent.send(messageToSend);

                }
                requestedServiceController--;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void setup() {

        addBehaviour(createWorldNet);

        addBehaviour(initBehaviour);

        addBehaviour(createEnvironment);

        addBehaviour(updateStates);

        addBehaviour(createAgents);

        addBehaviour(discoverAgents);

        addBehaviour(getDefectedControllers);

        addBehaviour(setServiceController);
    }
}
