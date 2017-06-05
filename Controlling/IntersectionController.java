package Controlling;

import Utility.IntersectionActing;
import Utility.IntersectionSensing;
import Utility.WorldDetector;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.leap.Iterator;

import java.io.IOException;
import java.util.Timer;

import static GEngine.graphicEngine.disableTrafficSystemIndex;

/**
 * Created by Catalin on 5/27/2017.
 */
public class IntersectionController extends Agent implements IController {

    IntersectionSensing intersectionSensing;
    IntersectionActing intersectionActing;
    boolean changeState;
    boolean normalState;
    boolean defectState;
    AID serviceControllerID;
    Timer timer;

    WorldDetector wd;
    boolean detectedWorld;

    // Alternate state of traffic lights
    boolean Updown;
    boolean RightLeft;

    // Graphic Engine variables
    public static int cicleInterval = 3000; // to be set from UI

    private long wakeupTime;

    // to be deleted!!
    boolean finished;

    Behaviour initBehaviour = new Behaviour() {
        @Override
        public void action() {
            intersectionActing = new IntersectionActing();
            normalState = true;
            defectState = false;
            serviceControllerID = null; // None service controller
            timer = new Timer(); // For switching state of lights
            detectedWorld = false;

            Updown = true;
            RightLeft = false;
        }

        @Override
        public boolean done() {
            return true;
        }
    };


    Behaviour normalCicleTrafficLights = new CyclicBehaviour() {
        @Override
        public void action() {
            if(detectedWorld) {

                if (normalState) {
                    int thisID = Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length() - 1));
                    Iterator it = getAID().getAllAddresses();
                    String adresa = (String) it.next();
                    String platforma = getAID().getName().split("@")[1];

                    ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                    AID r;
                    if (serviceControllerID != null)
                        r = serviceControllerID;
                    else
                        r = new AID("IntersectionActing" + thisID + "@" + platforma, AID.ISGUID);
                    r.addAddresses(adresa);
                    if (intersectionActing != null) {
                        try {
                            Thread.sleep(cicleInterval);

                            if (!disableTrafficSystemIndex[thisID] || serviceControllerID != null) {
                                messageToSend.setConversationId("Acting");
                                try {
                                    intersectionActing.setLaneDirection(Updown, RightLeft);
                                    messageToSend.setContentObject(intersectionActing);
                                    Updown = !Updown;
                                    RightLeft = !RightLeft;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } // to add any condition?
                            messageToSend.addReceiver(r);
                            Thread.sleep(50);
                            myAgent.send(messageToSend);
                        } catch (InterruptedException e) {

                        }
                    }
                }
            }
        }
    };

    Behaviour receiver = new CyclicBehaviour() { // Disabled intersection
        @Override
        public void action() { // Receive world feedback
            ACLMessage mesaj_receptionat = myAgent.receive();
            if (mesaj_receptionat != null)
             {
                 if (detectedWorld) {
                    if (mesaj_receptionat.getConversationId() == "Sensing") { // Data from Sensors
                        try {
                            intersectionSensing = (IntersectionSensing) mesaj_receptionat.getContentObject();
                        } catch (UnreadableException e) {
                            e.printStackTrace();
                        }
                    }

                    if (mesaj_receptionat.getConversationId() == "StatusUpdate") { // Data from Nucleus
                        try {
                            normalState = (boolean) mesaj_receptionat.getContentObject();
                        } catch (UnreadableException e) {
                            e.printStackTrace();
                        }
                    }

                    if (mesaj_receptionat.getConversationId() == "DefectSolver") { // Data from Nucleus
                        try {
                            serviceControllerID = (AID) mesaj_receptionat.getContentObject();
                        } catch (UnreadableException e) {
                            e.printStackTrace();
                        }
                    }

                }
                 if (mesaj_receptionat.getConversationId() == "WorldDetector") { // Data from Nucleus
                     try {
                         wd = (WorldDetector) mesaj_receptionat.getContentObject();
                     } catch (UnreadableException e) {
                         e.printStackTrace();
                     }
                 }
            }

        }
    };



        @Override
        protected void setup() {

            addBehaviour(initBehaviour);

            addBehaviour(normalCicleTrafficLights);

            addBehaviour(receiver);

            addBehaviour(new CyclicBehaviour() {
                @Override
                public void action() { // Send data to Nucleus to decide behaviour

                        int thisID = Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length() - 1));
                        Iterator it = getAID().getAllAddresses();
                        String adresa = (String) it.next();
                        String platforma = getAID().getName().split("@")[1];

                        ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                        AID r = new AID("IntersectionNucleus" + thisID + "@" + platforma, AID.ISGUID);
                        r.addAddresses(adresa);
                        if(detectedWorld) {
                            if (intersectionSensing != null) {
                                if (!disableTrafficSystemIndex[thisID]) {
                                    messageToSend.setConversationId("StatusUpdate");

                                    try {
                                        messageToSend.setContentObject(intersectionSensing.getMaxDensity());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    messageToSend.setConversationId("Defect");
    //                            try {
    //                                messageToSend.setContentObject(true);
    //                            } catch (IOException e) {
    //                                e.printStackTrace();
    //                            }
                                }
                            }
                        }
                        else {
                            if (wd != null) {
                                messageToSend.setConversationId("WorldDetector");

                                try {
                                    messageToSend.setContentObject(wd);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                detectedWorld = true;
                            }
                        }


                    messageToSend.addReceiver(r);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    myAgent.send(messageToSend);
                }
            });

//
//            addBehaviour(new CyclicBehaviour() { // Set IntersectionActing object to be handled by Acting agent.
//                @Override
//                public void action() {
//
//                }
//            });

        }
    }

