package Nucleus;

import GEngine.graphicEngine;
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

import static GEngine.graphicEngine.disableTrafficSystemIndex;

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

    WorldDetector wd;
    boolean detectedWorld;


    ContainerController home;
    private AgentController rmaNucleus;

    Behaviour discoverAgents = new Behaviour() {
        @Override
        public void action() { // To descover all agents in current ContainerController
            AMSAgentDescription[] agents = null;

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

    Behaviour sendWorldDetector = new CyclicBehaviour() {
        @Override
        public void action() {
            if (!detectedWorld && wd!=null) {

                Iterator it = getAID().getAllAddresses();
                String adresa = (String) it.next();
                String platforma = getAID().getName().split("@")[1];

                ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                AID r = new AID("GlobalNucleus" + "@" + platforma, AID.ISGUID);

                messageToSend.setConversationId("WorldDetector");

                try {
                    messageToSend.setContentObject(wd); // send state of intersection
                } catch (IOException e) {
                    e.printStackTrace();
                }
                messageToSend.addReceiver(r);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myAgent.send(messageToSend);
                detectedWorld = true;
            }
        }
    };

    Behaviour initBehaviour = new Behaviour() {
        @Override
        public void action() {
            serviceController = null;
            defectRequest = false;
            detectedWorld = false;

            home = this.myAgent.getContainerController();

            for (int i = 0; i < graphicEngine.numberOfIntersections; i++) {
                // Sensing Agents
                try {
                   // home.getAgent("IntersectionSensing" + i);
                    rmaNucleus = home.createNewAgent("IntersectionSensing" + i,
                            "Sensing.SensingAgent", new Object[0]);
                    rmaNucleus.start();
                    // to print in console!!!
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }

                // Controlling Agents
                try {
                    rmaNucleus = home.createNewAgent("IntersectionController" + i,
                            "Controlling.IntersectionController", new Object[0]);
                    rmaNucleus.start();
                    // to print in console!!!
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }

                // Acting Agents
                try {
                    rmaNucleus = home.createNewAgent("IntersectionActing" + i,
                            "Acting.ActingAgent", new Object[0]);
                    rmaNucleus.start();
                    // to print in console!!!
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean done() {
            return true;
        }
    };

    Behaviour synchronizeIntersections = new CyclicBehaviour() {
        @Override
        public void action() {
            if(detectedWorld) {

                if (!inRange && maxDensity != null) {
                    Iterator it = getAID().getAllAddresses();
                    String adresa = (String) it.next();
                    String platforma = getAID().getName().split("@")[1];

                    ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                    AID r = new AID("GlobalNucleus" + "@" + platforma, AID.ISGUID);

                    messageToSend.setConversationId("CentralizedControl");

                    try {
                        messageToSend.setContentObject(maxDensity); // send state of intersection
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    messageToSend.addReceiver(r);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    myAgent.send(messageToSend);
                }
            }
        }
    };

    @Override
    public void setup() {

        addBehaviour(discoverAgents); // Debug use only

        addBehaviour(initBehaviour);

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() { // Receive controller world status
                ACLMessage mesaj_receptionat = myAgent.receive();
                if(mesaj_receptionat!=null)
                {
                    if(detectedWorld) {
                        if (mesaj_receptionat.getConversationId() == "StatusUpdate") {
                            try {
                                maxDensity = (int[]) mesaj_receptionat.getContentObject();
                            } catch (UnreadableException e) {
                                e.printStackTrace();
                            }

                            if ((maxDensity[0] <= setPoint) || (maxDensity[1] <= setPoint)) {
                                inRange = true;
                            } else
                                inRange = false;
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
                    }
                    else
                        if(mesaj_receptionat.getConversationId()=="WorldDetector") {
                            try {
                                wd = (WorldDetector) mesaj_receptionat.getContentObject();
                            } catch (UnreadableException e) {
                                e.printStackTrace();
                            }
                        }
                }
            }
        });

        addBehaviour(new CyclicBehaviour() {
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
                    if (!disableTrafficSystemIndex[thisID]) {
                        messageToSend.setConversationId("StatusUpdate");
                        try {
                            messageToSend.setContentObject(inRange);
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
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    myAgent.send(messageToSend);
                    //}
                }
            }
        });

        addBehaviour(new CyclicBehaviour() { // Send data to GlobalNucleus to check setpoint
            @Override
            public void action() {
                if (detectedWorld) {
                    if (defectRequest) {
                        int thisID = Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length() - 1));
                        Iterator it = getAID().getAllAddresses();
                        String adresa = (String) it.next();
                        String platforma = getAID().getName().split("@")[1];

                        ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                        AID r = new AID("GlobalNucleus" + "@" + platforma, AID.ISGUID);

                        messageToSend.setConversationId("Defect");

                        try {
                            AID defectedAID = new AID("IntersectionController" + thisID + "@" + platforma, AID.ISGUID);
                            messageToSend.setContentObject(defectedAID); // send AID of Defect Controller
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        messageToSend.addReceiver(r);
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        myAgent.send(messageToSend);
                    }
                }
            }
        });

        addBehaviour(synchronizeIntersections);

        addBehaviour(sendWorldDetector);

    }
}
