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

import static GEngine.graphicEngine.EventLogEntries;

/**
 * Created by Catalin on 5/22/2017.
 */
public class CoreAgent extends Agent {

    public static int GlobalNucleusSetPoint;
    public static LinkedList<AID> availableNucleus;
    public static LinkedList<AID> disabledControllers;
    private int NucleusIndex;
    private boolean DoneCreatingAgents;
    private boolean doneInitBehaviour;
    private boolean requestedServiceController;
    ContainerController home = null;

    LinkedList<IntersectionItemGraph> LocationGraph;


    LinkedList<WorldDetector> wd;
    int detectedWorldItems;
    boolean doneDetecting;
    boolean doneProcessingNucleusesLocation;


    //State of intersections density of vehicles !!!
    private int density[][];

    private boolean centralControl;

    public static AgentController rma; // Same as CoreAgent seted in iniBehaviour

    Behaviour discoverAgents = new Behaviour() {
        @Override
        public void action() { // To descover all agents in current container from Agent Management System AMS
            AMSAgentDescription[] agents = null;

            try {
                SearchConstraints c = new SearchConstraints();
                c.setMaxResults ( new Long(-1) );
                agents = AMSService.search( myAgent, new AMSAgentDescription (), c );
                //AMSService.
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
                    } else
                        if(!doneProcessingNucleusesLocation)
                        {
                        for (int i = 0; i < wd.size() - 1; i++) {
                            for (int j = i + 1; j < wd.size(); j++) {
                                int side = Helper.checkGraphPosition(wd.get(i).getIntersectionItem(), wd.get(j).getIntersectionItem());
                                switch (side) {
                                    case 0:
                                        LocationGraph.get(i).setUpNeighbour(LocationGraph.get(j));
                                        LocationGraph.get(j).setDownNeighbour(LocationGraph.get(i));
                                        break;
                                    case 1:
                                        LocationGraph.get(i).setDownNeighbour(LocationGraph.get(j));
                                        LocationGraph.get(j).setUpNeighbour(LocationGraph.get(i));
                                        break;
                                    case 2:
                                        LocationGraph.get(i).setRightNeighbour(LocationGraph.get(j));
                                        LocationGraph.get(j).setLeftNeighbour(LocationGraph.get(i));
                                        break;
                                    case 3:
                                        LocationGraph.get(i).setLeftNeighbour(LocationGraph.get(j));
                                        LocationGraph.get(j).setRightNeighbour(LocationGraph.get(i));
                                        break;

                                }

                            }

                        }
                            doneProcessingNucleusesLocation = true;
                    }
                }
            }
        }
    };

    Behaviour initBehaviour = new Behaviour() {
        @Override
        public void action() {
            availableNucleus = new LinkedList<AID>();
            NucleusIndex = 0;
            DoneCreatingAgents = false;
            disabledControllers = new LinkedList<AID>();
            requestedServiceController = false;

            home = this.myAgent.getContainerController();

            density = new int[graphicEngine.numberOfIntersections][2];

            centralControl = false;

            detectedWorldItems = 0;
            wd = new LinkedList<WorldDetector>();
            LocationGraph = new LinkedList<IntersectionItemGraph>();

            doneDetecting = false;
            doneProcessingNucleusesLocation = false;

            //doneInitBehaviour = true;

            //AgentCreator ac = new AgentCreator();

            EventLogEntries.add("Core Agent initialized!");
        }

        @Override
        public boolean done() {
            return true;
        }
    };

    Behaviour updateStates = new CyclicBehaviour() { // Send data to Nucleus to update setpoint
        @Override
        public void action() {
            if (DoneCreatingAgents && doneDetecting) {
                NucleusIndex = NucleusIndex % graphicEngine.numberOfIntersections;
                Iterator it = getAID().getAllAddresses();
                String adresa = (String) it.next();
                String platforma = getAID().getName().split("@")[1];

                ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                AID r = new AID("IntersectionNucleus" + NucleusIndex + "@" + platforma, AID.ISGUID);
                r.addAddresses(adresa);
                        if(centralControl){ // TO IMPLEMENT!!!
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
                NucleusIndex++;

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

            if(graphicEngine.startApplication && !DoneCreatingAgents) {

                Iterator it = getAID().getAllAddresses();
                String adresa = (String) it.next();
                String platforma = getAID().getName().split("@")[1];


                // Start number of vehicles # to continue!
                if(graphicEngine.numberOfCars >0) {
                    for (int i = 0; i < graphicEngine.numberOfCars; i++) {
                        // Sensing Agents
                        //if(AMSService.)
                        try {
                            rma = home.createNewAgent("VehicleSensing" + i,
                                    "Sensing.SensingAgent", new Object[0]);
                            rma.start();
                            //graphicEngine.EventLogEntries.add("Created " )
                            // to print in console!!!
                        } catch (StaleProxyException e) {
                            e.printStackTrace();
                        }

                        // Controlling Agents
                        try {
                            rma = home.createNewAgent("VehicleController" + i,
                                    "Controlling.VehicleController", new Object[0]);
                            rma.start();
                            // to print in console!!!
                        } catch (StaleProxyException e) {
                            e.printStackTrace();
                        }

                        // Acting Agents
                        try {
                            rma = home.createNewAgent("VehicleActing" + i,
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
                            availableNucleus.add(new AID("IntersectionNucleus" + i + "@" + platforma, AID.ISGUID));
                            graphicEngine.EventLogEntries.add("Started Intersection Nucleus " + i + " agent.");
                        } catch (StaleProxyException e) {
                            e.printStackTrace();
                        }
                    }
                }
                DoneCreatingAgents = true;
            }
//            else
//                graphicEngine.createEventLogEntry("Initiated Global Nucleus!");

        }
    };


    CyclicBehaviour getDefectedControllers = new CyclicBehaviour() {
        @Override
        public void action() {

            Iterator it = getAID().getAllAddresses();
            String adresa = (String) it.next();
            String platforma = getAID().getName().split("@")[1];

            ACLMessage mesaj_receptionat = myAgent.receive();
            if (mesaj_receptionat != null) {
                if(doneDetecting) {
                    // !!! Assign another controller to defected ones.


                        if (mesaj_receptionat.getConversationId() == "Defect" && !requestedServiceController) {
                            try {
                                AID defectedAID = (AID) mesaj_receptionat.getContentObject();
                                disabledControllers.add(defectedAID);
                                //availableNucleus.remove(defectedAID);
                            } catch (UnreadableException e) {
                                e.printStackTrace();
                            }

                            requestedServiceController = true;
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


        }
    };

    Behaviour setServiceController = new CyclicBehaviour() {
        @Override
        public void action() { // To defected ones.
            if (disabledControllers.size() > 0 && availableNucleus.size() > 0 && requestedServiceController && doneDetecting) {
                for (int i=0; i<disabledControllers.size(); i++) {

                    Iterator it = getAID().getAllAddresses();
                    String adresa = (String) it.next();
                    String platforma = getAID().getName().split("@")[1];

                    ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                    AID r = new AID(Helper.IntersectionNucleus + disabledControllers.get(i).getLocalName().substring(disabledControllers.get(i).getLocalName().length()-1) + "@" + platforma, AID.ISGUID);
                    disabledControllers.get(i).addAddresses(adresa);
                    messageToSend.setConversationId("DefectSolver");
                    messageToSend.addReceiver(r);

                    try {
                        messageToSend.setContentObject( new AID(Helper.IntersectionController + Helper.getAvailableControllerAID(disabledControllers.get(i)) + "@" + platforma, AID.ISGUID)); // send first AID available - !!! to create a new fesable method for this
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myAgent.send(messageToSend);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //requestedServiceController = false;
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
