package Controlling;

import GEngine.graphicEngine;
import Utility.Helper;
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
import static Utility.Helper.tStart;

/**
 * Created by Catalin on 5/27/2017.
 */
public class IntersectionController extends Agent implements IController {

    IntersectionSensing intersectionSensing;
    IntersectionActing intersectionActing;

    boolean normalState; // Check if the Global refence is in range
    boolean defectState;
    AID serviceControllerAID;
    Timer timer;

    WorldDetector wd;
    boolean detectedWorld;

    // Alternate state of traffic lights
    boolean Updown;
    boolean RightLeft;

    // Graphic Engine variables
    public static int cicleInterval = 8000; // to be set from UI

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


    CyclicBehaviour centralizedCycle = new CyclicBehaviour() {
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

    CyclicBehaviour senderBehaviour = new CyclicBehaviour() {
        @Override
        public void action() {
            if (detectedWorld) {
                if (normalState) {
                    int thisID = Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length() - 1));
                    Iterator it = getAID().getAllAddresses();
                    String adresa = (String) it.next();
                    String platforma = getAID().getName().split("@")[1];

                    ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                    AID r;
                    if (serviceControllerAID != null) { // IF DEFECT GO IN CASCADE SOLVER !!!
                        int controllerID = Integer.parseInt(serviceControllerAID.getLocalName().substring(serviceControllerAID.getLocalName().length() - 1));
                        r = new AID(Helper.IntersectionController + controllerID + "@" + platforma, AID.ISGUID);
                        messageToSend.setConversationId("DefectRequest");

                        try {
                            messageToSend.setContentObject(intersectionSensing);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        r = new AID("IntersectionActing" + thisID + "@" + platforma, AID.ISGUID);
                        r.addAddresses(adresa);
                        if (intersectionSensing != null) {
                            if (ActiveIntersectionControllers[thisID] || serviceControllerAID != null) {
                                messageToSend.setConversationId("ActingNormalCycle");

                                if (graphicEngine.controllerPairedState) {
                                    if (intersectionSensing.getMaxDensity()[0] > intersectionSensing.getMaxDensity()[1]) {
                                        intersectionActing.setLaneDirection(true, false);
                                        // EventLogEntries.add("Controller-ul cu ID-ul " + thisID + " a schimbat directia de mers pe Nord-Sud");
                                    } else {
                                        intersectionActing.setLaneDirection(false, true);
                                        // EventLogEntries.add("Controller-ul cu ID-ul " + thisID + " a schimbat directia de mers pe Vest-Est");
                                    }
                                } else { // SINGLE LANE !!
                                    int maxLaneDensityID = 0;
                                    for (int i = 1; i < 4; i++) {
                                        if (intersectionSensing.getDensity(maxLaneDensityID) <= intersectionSensing.getDensity(i)) {
                                            maxLaneDensityID = i;
                                        }
                                    }
                                    intersectionActing.setLaneByTrafficLightID(maxLaneDensityID);
                                }

                                try {
                                    messageToSend.setContentObject(intersectionActing);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }

                            else if(!ActiveIntersectionControllers[thisID] && serviceControllerAID == null){
                                messageToSend.setConversationId("ActingNormalCycle");

//                                if (graphicEngine.controllerPairedState) {
//                                    intersectionActing.setLaneByTrafficLightID(-1);
//                                } else { // SINGLE LANE !!
//                                    intersectionActing.setLaneByTrafficLightID(-1);
//                                }

                                // Leave same state for intersection

                                try {
                                    messageToSend.setContentObject(intersectionActing);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                    }


                    messageToSend.addReceiver(r);
                    myAgent.send(messageToSend);
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    boolean sendedMessage = false;

    CyclicBehaviour receiver = new CyclicBehaviour() { // Disabled intersection
        @Override
        public void action() { // Receive world feedback
            if (myAgent.getCurQueueSize() > 0) {
                int thisID = Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length() - 1));
                for (int i = 0; i < myAgent.getCurQueueSize(); i++) {
                    ACLMessage mesaj_receptionat = myAgent.receive();
                    if (mesaj_receptionat != null) {
                        myAgent.getCurQueueSize();
                        if (detectedWorld) {
                            if (mesaj_receptionat.getConversationId() == "Sensing") { // Data from Sensors
                                try {
                                    intersectionSensing = (IntersectionSensing) mesaj_receptionat.getContentObject();
                                } catch (UnreadableException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (mesaj_receptionat.getConversationId() == "DefectRequest") { // Data from Defected Controllers
                                int defectedControllerID = Integer.parseInt(mesaj_receptionat.getSender().getLocalName().substring(mesaj_receptionat.getSender().getLocalName().length() - 1));
                                try {
                                    IntersectionSensing IS = (IntersectionSensing) mesaj_receptionat.getContentObject();



                                    Iterator it = getAID().getAllAddresses();
                                    String adresa = (String) it.next();
                                    String platforma = getAID().getName().split("@")[1];

                                    ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                                    AID r = new AID("IntersectionActing" + defectedControllerID + "@" + platforma, AID.ISGUID);
                                    r.addAddresses(adresa);

                                    messageToSend.setConversationId("ActingNormalCycle");

                                    if (graphicEngine.controllerPairedState) {
                                        if (IS.getMaxDensity()[0] > IS.getMaxDensity()[1]) {
                                            intersectionActing.setLaneDirection(true, false);
                                            // EventLogEntries.add("Controller-ul cu ID-ul " + thisID + " a schimbat directia de mers pe Nord-Sud");
                                        } else {
                                            intersectionActing.setLaneDirection(false, true);
                                            // EventLogEntries.add("Controller-ul cu ID-ul " + thisID + " a schimbat directia de mers pe Vest-Est");
                                        }
                                    } else { // SINGLE LANE !!
                                        int maxLaneDensityID = 0;
                                        for (int k = 1; k < 4; k++) {
                                            if (IS.getDensity(maxLaneDensityID) <= IS.getDensity(k)) {
                                                maxLaneDensityID = k;
                                            }
                                        }
                                        intersectionActing.setLaneByTrafficLightID(maxLaneDensityID);
                                    }

                                    try {
                                        messageToSend.setContentObject(intersectionActing);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                messageToSend.addReceiver(r);
                                myAgent.send(messageToSend);




                                } catch (UnreadableException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                if(!sendedMessage) {
                                    System.out.println("Controllerul " + thisID + " a trimis date catre actuatorul " + defectedControllerID + "\n");
                                    sendedMessage = true;
                                }

                            }



                            if (mesaj_receptionat.getConversationId() == "UpdateSetPoint") { // Data from Nucleus
                                try {
                                    setPoint = (int) mesaj_receptionat.getContentObject();
//                                    if(intersectionSensing != null) {
//                                        if (intersectionSensing.getMaxDensity()[0] <= 2 * setPoint && intersectionSensing.getMaxDensity()[1] <= 2 * setPoint) {
//                                            normalState = true;
//                                        } else {
////                                            normalState = false;
//                                        }
//                                    }

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
                                    int serviceControllerID = Integer.parseInt(serviceControllerAID.getLocalName().substring(serviceControllerAID.getLocalName().length() - 1));
                                    if(!receivedControllerID) {

                                        if(tStart > -1){
                                            EventLogEntries.add("TS: " + String.valueOf((float)((System.currentTimeMillis() - tStart)/1000)) + " Controlerul intersectiei " + Helper.getIntersectionCoordinate(thisID) +" i s-au asociat sarcinile \ncontrolerului intersectiei " + Helper.getIntersectionCoordinate(serviceControllerID));
                                            System.out.println("TS: " + String.valueOf((float)((System.currentTimeMillis() - tStart)/1000)) + " Controlerul intersectiei " + Helper.getIntersectionCoordinate(thisID) +" i s-au asociat sarcinile controlerului intersectiei " + Helper.getIntersectionCoordinate(serviceControllerID));
                                        }
                                        receivedControllerID = true;
                                    }
                                    ActiveIntersectionControllers[thisID] = true;
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

    boolean receivedControllerID = false;

    CyclicBehaviour sendNucleusData = new CyclicBehaviour() {
        @Override
        public void action() {

            int thisID = Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length() - 1));
            Iterator it = getAID().getAllAddresses();
            String adresa = (String) it.next();
            String platforma = getAID().getName().split("@")[1];

            ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
            AID r = new AID("IntersectionNucleus" + thisID + "@" + platforma, AID.ISGUID);
            r.addAddresses(adresa);
            if(detectedWorld) { // World intersection graph
                if(myAgent.getCurQueueSize()>0) {
                    for (int i = 0; i < myAgent.getCurQueueSize(); i++) {
                        if (intersectionSensing != null) {
                            if (ActiveIntersectionControllers[thisID]) {

                                messageToSend.setConversationId("UpdatePriority"); // No status update needed in nucleus!!!
                                try {
                                messageToSend.setContentObject(intersectionSensing.getMaxDensity());
                                } catch (IOException e) {
                                e.printStackTrace();
                                }
                            } else {
                                messageToSend.setConversationId("Defect");
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
            myAgent.send(messageToSend);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };



        @Override
        protected void setup() {

            addBehaviour(initBehaviour);

            addBehaviour(receiver);

            addBehaviour(sendNucleusData);

            addBehaviour(senderBehaviour);

            addBehaviour(centralizedCycle);

        }
    }

