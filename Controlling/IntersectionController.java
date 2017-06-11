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

import static GEngine.graphicEngine.ActiveIntersectionControllers;
import static GEngine.graphicEngine.EventLogEntries;

/**
 * Created by Catalin on 5/27/2017.
 */
public class IntersectionController extends Agent implements IController {

    IntersectionSensing intersectionSensing;
    IntersectionActing intersectionActing;

    boolean normalState;
    boolean defectState;
    AID serviceControllerAID;
    Timer timer;

    WorldDetector wd;
    boolean detectedWorld;

    // Alternate state of traffic lights
    boolean Updown;
    boolean RightLeft;

    // Graphic Engine variables
    public static int cicleInterval = 3000; // to be set from UI

    private long wakeupTime;

    int setPoint;

    // to be deleted!!
    boolean finished;

    Behaviour initBehaviour = new Behaviour() {
        @Override
        public void action() {
            intersectionActing = new IntersectionActing();
            normalState = true;
            defectState = false;
            serviceControllerAID = null; // None service controller
            timer = new Timer(); // For switching state of lights

            detectedWorld = true;  // TO BE LET TRUE !!!


            Updown = true;
            RightLeft = false;
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

                if (!normalState) { // Centralized Behaviour
                    int thisID = Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length() - 1));
                    Iterator it = getAID().getAllAddresses();
                    String adresa = (String) it.next();
                    String platforma = getAID().getName().split("@")[1];

                    ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                    AID r = new AID("IntersectionNucleus" + thisID + "@" + platforma, AID.ISGUID);
                    r.addAddresses(adresa);
                    if(detectedWorld) { // not relevant yet
                        if(myAgent.getCurQueueSize()>0) {
                            for (int i = 0; i < myAgent.getCurQueueSize(); i++) {
                                 // TO SEND HERE MAX DENSITY VECTOR AND CHECK SOMEWHERE IN CORE AGENT PERSPECTIVE OF INTERSECTION WOTH NEIGHBOURS ... ICKY!
                            }
                        }
                    }

                    messageToSend.addReceiver(r);
                    try {
                        Thread.sleep(50*(thisID+1));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    myAgent.send(messageToSend);

                }
            }
        }
    };

    CyclicBehaviour normalCicle = new CyclicBehaviour() {
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
                    if (serviceControllerAID != null) {
                        // r = serviceControllerAID;
                        int controllerID = Integer.parseInt(serviceControllerAID.getLocalName().substring(serviceControllerAID.getLocalName().length() - 1));
                        r = new AID("IntersectionActing" + controllerID + "@" + platforma, AID.ISGUID);
                    }
                    else
                        r = new AID("IntersectionActing" + thisID + "@" + platforma, AID.ISGUID);
                    r.addAddresses(adresa);
                    //if (intersectionActing != null) {
//                        try {
//                            Thread.sleep(cicleInterval);

                    if (ActiveIntersectionControllers[thisID] || serviceControllerAID != null) {
                        messageToSend.setConversationId("ActingNormalCycle");
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
                    //Thread.sleep(50*(thisID+1));
                    myAgent.send(messageToSend);

//                        } catch (InterruptedException e) {
//
//                        }
                    // }
                }
            }
        }
    };

    CyclicBehaviour receiver = new CyclicBehaviour() { // Disabled intersection
        @Override
        public void action() { // Receive world feedback
            if(myAgent.getCurQueueSize()>0) {
                for (int i = 0; i < myAgent.getCurQueueSize(); i++) {
                    ACLMessage mesaj_receptionat = myAgent.receive();
                    if (mesaj_receptionat != null)
                    {
                        myAgent.getCurQueueSize();
                        if (detectedWorld) {
                            if (mesaj_receptionat.getConversationId() == "Sensing") { // Data from Sensors
                                try {
                                    intersectionSensing = (IntersectionSensing) mesaj_receptionat.getContentObject();
                                } catch (UnreadableException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (mesaj_receptionat.getConversationId() == "UpdateSetPoint") { // Data from Nucleus
                                try {
                                    setPoint = (int) mesaj_receptionat.getContentObject();
                                    if(intersectionSensing.getMaxDensity()[0] <= setPoint || intersectionSensing.getMaxDensity()[1] <= setPoint){
                                        normalState = true;
                                    }
                                    else {
                                        normalState = false;

                                    }

                                } catch (UnreadableException e) {
                                    e.printStackTrace();
                                }
                            }

//                            if (mesaj_receptionat.getConversationId() == "StatusUpdate") { // Data from Nucleus
//                                try {
//                                    normalState = (boolean) mesaj_receptionat.getContentObject();
//                                } catch (UnreadableException e) {
//                                    e.printStackTrace();
//                                }
//                            }

                            if (mesaj_receptionat.getConversationId() == "DefectSolver") { // Data from Nucleus
                                try {
                                    serviceControllerAID = (AID) mesaj_receptionat.getContentObject();
                                } catch (UnreadableException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                        if (mesaj_receptionat.getConversationId() == "WorldDetector") { // Data from Nucleus
                            try {
                                wd = (WorldDetector) mesaj_receptionat.getContentObject();
                                EventLogEntries.add(this.myAgent.getLocalName() + " a primit worldDetect");
                            } catch (UnreadableException e) {
                                e.printStackTrace();
                            }
                        }

                        //System.out.println("Controllerul " + this.myAgent.getLocalName() + " a primit mesaj: " + mesaj_receptionat.getConversationId());
                    }
                    else {
                        block();
                        break;
                    }
                }
            }
//            ACLMessage mesaj_receptionat = myAgent.receive();
//            if (mesaj_receptionat != null)
//             {
//                 myAgent.getCurQueueSize();
//                 if (detectedWorld) {
//                    if (mesaj_receptionat.getConversationId() == "Sensing") { // Data from Sensors
//                        try {
//                            intersectionSensing = (IntersectionSensing) mesaj_receptionat.getContentObject();
//                        } catch (UnreadableException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                     if (mesaj_receptionat.getConversationId() == "UpdateSetPoint") { // Data from Sensors
//                         try {
//                             setPoint = (int) mesaj_receptionat.getContentObject();
//                         } catch (UnreadableException e) {
//                             e.printStackTrace();
//                         }
//                     }
//
//                    if (mesaj_receptionat.getConversationId() == "StatusUpdate") { // Data from Nucleus
//                        try {
//                            normalState = (boolean) mesaj_receptionat.getContentObject();
//                        } catch (UnreadableException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    if (mesaj_receptionat.getConversationId() == "DefectSolver") { // Data from Nucleus
//                        try {
//                            serviceControllerAID = (AID) mesaj_receptionat.getContentObject();
//                        } catch (UnreadableException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                }
//                     if (mesaj_receptionat.getConversationId() == "WorldDetector") { // Data from Nucleus
//                     try {
//                         wd = (WorldDetector) mesaj_receptionat.getContentObject();
//                         EventLogEntries.add(this.myAgent.getLocalName() + " a primit worldDetect");
//                     } catch (UnreadableException e) {
//                         e.printStackTrace();
//                     }
//                 }
//            }

        }
    };

    CyclicBehaviour sendNucleusData = new CyclicBehaviour() {
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
                if(myAgent.getCurQueueSize()>0) {
                    for (int i = 0; i < myAgent.getCurQueueSize(); i++) {
                        if (intersectionSensing != null) {
                            if (ActiveIntersectionControllers[thisID]) {
//                                                        messageToSend.setConversationId("StatusUpdate"); // No status update needed in nucleus!!!
//
//                                                        try {
//                                                            messageToSend.setContentObject(intersectionSensing.getMaxDensity());
//                                                        } catch (IOException e) {
//                                                            e.printStackTrace();
//                                                        }
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
                    EventLogEntries.add(this.myAgent.getLocalName() + " a trimis worldDetect");
                }
            }


            messageToSend.addReceiver(r);
            try {
                Thread.sleep(50*(thisID+1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            myAgent.send(messageToSend);
        }
    };



        @Override
        protected void setup() {

            addBehaviour(initBehaviour);

            addBehaviour(normalCicle);

            addBehaviour(receiver);

            addBehaviour(sendNucleusData);

            addBehaviour(centralizedControl);

        }
    }

