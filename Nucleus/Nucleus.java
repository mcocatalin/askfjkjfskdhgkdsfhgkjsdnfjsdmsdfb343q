package Nucleus;

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

import java.io.IOException;

import static GEngine.graphicEngine.*;
import static Nucleus.CoreAgent.LocationGraph;

/**
 * Created by Catalin on 5/14/2017.
 */
public class Nucleus extends Agent {

    //IntersectionSensing intersectionSensing;
    private boolean receivedNewState = false;
    private int setPoint;
    private int[] maxDensity; // Info about lane direction density
    private boolean inRange; // Check setpoint
    private AID serviceController;
    private boolean defectRequest;
    private int priority;
    private boolean mostSemnificativePriority;

    WorldDetector wd;
    boolean detectedWorld;

    AMSAgentDescription[] agents = null;


    ContainerController home;
    private AgentController rmaNucleus;


    Behaviour discoverAgents = new Behaviour() {
        @Override
        public void action() { // To descover all agents in current ContainerController

            try {
                SearchConstraints c = new SearchConstraints();
                c.setMaxResults ( new Long(-1) );
                agents = AMSService.search( myAgent, new AMSAgentDescription (), c );
            }
            catch (Exception e) {  }
        }

        @Override
        public boolean done() {
            return true;
        }
    };

    CyclicBehaviour sendWorldDetector = new CyclicBehaviour() {
        @Override
        public void action() {
            if (!detectedWorld && wd!=null) {

                Iterator it = getAID().getAllAddresses();
                String adresa = (String) it.next();
                String platforma = getAID().getName().split("@")[1];
                int thisID = Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length() - 1));
                ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                AID r = new AID("CoreAgent" + "@" + platforma, AID.ISGUID);
                r.addAddresses(adresa);

                messageToSend.setConversationId("WorldDetector");

                try {
                    messageToSend.setContentObject(wd); // send state of intersection
                    EventLogEntries.add(this.myAgent.getLocalName() + " a trimis worldDetect");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                messageToSend.addReceiver(r);
                myAgent.send(messageToSend);
                detectedWorld = true;



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
            serviceController = null;
            defectRequest = false;

            detectedWorld = true; // TO BE LET TRUE !!!

            home = this.myAgent.getContainerController();

            priority = 0;

            mostSemnificativePriority = false;

//            for (int i = 0; i < graphicEngine.numberOfIntersections; i++) {
//
//                // Controlling Agents
//                    try {
//                        rmaNucleus = home.createNewAgent("IntersectionController" + i,
//                                "Controlling.IntersectionController", new Object[0]);
//                    rmaNucleus.start();
//                    // to print in console!!!
//                    graphicEngine.EventLogEntries.add("Started Intersection Controller " + i + " agent.");
//                    } catch (StaleProxyException e) {
//                        e.printStackTrace();
//                    }
//
//
//                // Acting Agents
//                try {
//                    rmaNucleus = home.createNewAgent("IntersectionActing" + i,
//                            "Acting.ActingAgent", new Object[0]);
//                    rmaNucleus.start();
//                    // to print in console!!!
//                    graphicEngine.EventLogEntries.add("Started Intersection Acting " + i + " agent.");
//                } catch (StaleProxyException e) {
//                    e.printStackTrace();
//                }
//
//                // Sensing Agents
//                try {
//                    // home.getAgent("IntersectionSensing" + i);
//                    rmaNucleus = home.createNewAgent("IntersectionSensing" + i,
//                            "Sensing.SensingAgent", new Object[0]);
//                    rmaNucleus.start();
//                    // to print in console!!!
//                    graphicEngine.EventLogEntries.add("Started Intersection Sensing " + i + " agent.");
//                } catch (StaleProxyException e) {
//                    e.printStackTrace();
//                }
//            }

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

    CyclicBehaviour centralizedControl = new CyclicBehaviour() {
        @Override
        public void action() {
            if(detectedWorld) {

                if (!inRange && maxDensity != null) {

                    Iterator it = getAID().getAllAddresses();
                    String adresa = (String) it.next();
                    String platforma = getAID().getName().split("@")[1];

                    int thisID = Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length() - 1));
                    ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                    AID r = new AID("CoreAgent" + "@" + platforma, AID.ISGUID);

                    messageToSend.setConversationId("CentralizedControl");

                    try {
                        messageToSend.setContentObject(maxDensity); // send state of intersection
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    messageToSend.addReceiver(r);
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

    CyclicBehaviour negociateBehaviour = new CyclicBehaviour() { // Send data to CoreAgent to check setpoint
        @Override
        public void action() {
            if (detectedWorld) {
                if (defectRequest) {

                    int thisID = Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length() - 1));

                    if (!uncontrolledNegociation) {

                        Iterator it = getAID().getAllAddresses();
                        String adresa = (String) it.next();
                        String platforma = getAID().getName().split("@")[1];

                        ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                        AID r = new AID("CoreAgent" + "@" + platforma, AID.ISGUID);
                        r.addAddresses(adresa);

                        messageToSend.setConversationId("Defect");

                        try {
                            AID defectedAID = new AID("IntersectionController" + thisID + "@" + platforma, AID.ISGUID);
                            messageToSend.setContentObject(defectedAID); // send AID of Defect Controller
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        messageToSend.addReceiver(r);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        myAgent.send(messageToSend);

                        defectRequest = false; // sent Defect Request, waiting for answer
                    }
                    else{

                        Iterator it = getAID().getAllAddresses();
                        String platforma = getAID().getName().split("@")[1];
                        int serviceControllerPriority = 0;
                        int serviceControllerID=-1;

                        if (LocationGraph.get(thisID).isUpNeighbour() != null) {
                            serviceController = new AID("IntersectionController" + LocationGraph.get(thisID).isUpNeighbour().getComponentID() + "@" + platforma, AID.ISGUID);
                            serviceControllerPriority = LocationGraph.get(thisID).isUpNeighbour().getIntersectionSensing().getMaxDensity()[0] + LocationGraph.get(thisID).isUpNeighbour().getIntersectionSensing().getMaxDensity()[1];
                            serviceControllerID = LocationGraph.get(thisID).isUpNeighbour().getComponentID();
                        }
                        else if (LocationGraph.get(thisID).isLeftNeighbour() != null) {
                            serviceController = new AID("IntersectionController" + LocationGraph.get(thisID).isLeftNeighbour().getComponentID() + "@" + platforma, AID.ISGUID);
                            serviceControllerPriority = LocationGraph.get(thisID).isUpNeighbour().getIntersectionSensing().getMaxDensity()[0] + LocationGraph.get(thisID).isUpNeighbour().getIntersectionSensing().getMaxDensity()[1];
                            serviceControllerID = LocationGraph.get(thisID).isUpNeighbour().getComponentID();
                        }
                        else if (LocationGraph.get(thisID).isRightNeighbour() != null) {
                            serviceController = new AID("IntersectionController" + LocationGraph.get(thisID).isRightNeighbour().getComponentID() + "@" + platforma, AID.ISGUID);
                            serviceControllerPriority = LocationGraph.get(thisID).isUpNeighbour().getIntersectionSensing().getMaxDensity()[0] + LocationGraph.get(thisID).isUpNeighbour().getIntersectionSensing().getMaxDensity()[1];
                            serviceControllerID = LocationGraph.get(thisID).isUpNeighbour().getComponentID();
                        }
                        else if (LocationGraph.get(thisID).isDownNeighbour() != null) {
                            serviceController = new AID("IntersectionController" + LocationGraph.get(thisID).isDownNeighbour().getComponentID() + "@" + platforma, AID.ISGUID);
                            serviceControllerPriority = LocationGraph.get(thisID).isUpNeighbour().getIntersectionSensing().getMaxDensity()[0] + LocationGraph.get(thisID).isUpNeighbour().getIntersectionSensing().getMaxDensity()[1];
                            serviceControllerID = LocationGraph.get(thisID).isUpNeighbour().getComponentID();
                        }

                        int defectedControllerPriority = LocationGraph.get(thisID).getIntersectionSensing().getMaxDensity()[0] + LocationGraph.get(thisID).getIntersectionSensing().getMaxDensity()[1];

                            try {
                                Thread.sleep((serviceControllerPriority + defectedControllerPriority)*1000 );
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        System.out.println("Timpul de procesare al nucleului de service "  +serviceControllerID+" este de " + serviceControllerPriority + defectedControllerPriority);
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

    CyclicBehaviour receiverBehaviour = new CyclicBehaviour() {
        @Override
        public void action() { // Receive controller world status

            if (myAgent.getCurQueueSize() > 0) {
                for (int i = 0; i < myAgent.getCurQueueSize(); i++) {
                    ACLMessage mesaj_receptionat = myAgent.receive();
                    if (mesaj_receptionat != null) {
                        if (detectedWorld) {

                            if (mesaj_receptionat.getConversationId() == "UpdatePriority") {
                                priority = 0;
                                try {
                                    maxDensity = (int[]) mesaj_receptionat.getContentObject();
                                    for(int k=0; i<maxDensity.length;i++)
                                    {
                                        priority = maxDensity[k] + priority;
                                    }



                                } catch (UnreadableException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (mesaj_receptionat.getConversationId() == "UpdateSetPoint") {
                                try {
                                    setPoint = (int) mesaj_receptionat.getContentObject();
                                } catch (UnreadableException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (mesaj_receptionat.getConversationId() == "Defect") {
                                //                        try {
                                //                            defectRequest = (boolean) mesaj_receptionat.getContentObject();
                                //                        } catch (UnreadableException e) {
                                //                            e.printStackTrace();
                                //                        }
                                defectRequest = true;
                            }

                            if (mesaj_receptionat.getConversationId() == "DefectSolver") {
                                try {
                                    serviceController = (AID) mesaj_receptionat.getContentObject();
                                } catch (UnreadableException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (mesaj_receptionat.getConversationId() == "WorldDetector") { // Deactivated!!!
                            try {
                                wd = (WorldDetector) mesaj_receptionat.getContentObject();
                                EventLogEntries.add(this.myAgent.getLocalName() + " a primit worldDetect");
                            } catch (UnreadableException e) {
                                e.printStackTrace();
                            }
                        }

                        //System.out.println("Nucleul " + this.myAgent.getLocalName() + " a primit mesaj: " + mesaj_receptionat.getConversationId());
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

    CyclicBehaviour sendControllerData = new CyclicBehaviour() {
        @Override
        public void action() { // Send data to Intersection Controller to start a behaviour using this feedback
            int thisID = Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length() - 1));
            Iterator it = getAID().getAllAddresses();
            String adresa = (String) it.next();
            String platforma = getAID().getName().split("@")[1];

            ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
            AID r = new AID("IntersectionController" + thisID + "@" + platforma, AID.ISGUID);
            r.addAddresses(adresa);
            // if(maxDensity != null) {
            if (detectedWorld) {
                if (ActiveIntersectionControllers[thisID]) {
                    messageToSend.setConversationId("UpdateSetPoint"); // send SetPoint setted in UI at CoreAgent to controller
                    try {
                        messageToSend.setContentObject(setPoint);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (defectRequest && serviceController != null) {
                        messageToSend.setConversationId("DefectSolver");
                        try {
                            messageToSend.setContentObject(serviceController);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                messageToSend.addReceiver(r);
                myAgent.send(messageToSend);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //System.out.println("Nucleul " + this.myAgent.getLocalName() + " a trimis  catre controller mesaj: " + messageToSend.getConversationId());
                //}
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void setup() {

        //addBehaviour(discoverAgents); // Debug use only

        addBehaviour(initBehaviour);

        addBehaviour(receiverBehaviour);

        addBehaviour(sendControllerData);

        addBehaviour(negociateBehaviour);

        addBehaviour(centralizedControl);

        addBehaviour(sendWorldDetector); // Deactivated !!!

    }
}
