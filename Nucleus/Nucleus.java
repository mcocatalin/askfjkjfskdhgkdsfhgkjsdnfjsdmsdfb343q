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

import static GEngine.graphicEngine.disableTrafficSystemIndex;

/**
 * Created by Catalin on 5/14/2017.
 */
public class Nucleus extends Agent {

    public String localaddress = "";
//    public List<String> online_cells = new ArrayList<>();
//    private String locatie = "Hol"; // unnecessary?
    //IntersectionSensing intersectionSensing;
    private boolean receivedNewState = false;
    private int setPoint;
    private int[] maxDensity; // Info about lane direction density
    private boolean inRange; // Check setpoint
    private AID serviceController;

    Behaviour initBehaviour = new Behaviour() {
        @Override
        public void action() {
            serviceController = null;
            Runtime runtime = jade.core.Runtime.instance();
            ContainerController home = null;
            Profile p = new ProfileImpl();
            home = runtime.createMainContainer(p);
            for (int i = 0; i < graphicEngine.numberOfIntersections; i++) {
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
            }
        }

        @Override
        public boolean done() {
            return true;
        }
    };

    @Override
    public void setup() {

        addBehaviour(initBehaviour);

        addBehaviour(new CyclicBehaviour() { // Disabled intersection
            @Override
            public void action() { // Receive controller world status
                ACLMessage mesaj_receptionat = myAgent.receive();
                if(mesaj_receptionat!=null)
                {
                    if(mesaj_receptionat.getConversationId()=="StatusUpdate") {
                        try {
                            maxDensity = (int[]) mesaj_receptionat.getContentObject();
                        } catch (UnreadableException e) {
                            e.printStackTrace();
                        }

                        if((maxDensity[0] <= setPoint) && (maxDensity[1] <= setPoint)){
                            inRange = true;
                        }
                    }

                    if(mesaj_receptionat.getConversationId()=="UpdateSetPoint") {
                        try {
                            setPoint = (int) mesaj_receptionat.getContentObject();
                        } catch (UnreadableException e) {
                            e.printStackTrace();
                        }
                    }

                    if(mesaj_receptionat.getConversationId()=="DefectSolver") {
                        try {
                            serviceController = (AID) mesaj_receptionat.getContentObject();
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
                int thisID =Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length()-1));
                Iterator it = getAID().getAllAddresses();
                String adresa = (String) it.next();
                String platforma = getAID().getName().split("@")[1];

                ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                AID r = new AID("IntersectionController" + thisID + "@" + platforma, AID.ISGUID);
                r.addAddresses(adresa);
                if(maxDensity != null) {
                    if (!disableTrafficSystemIndex[thisID]) {
                        messageToSend.setConversationId("StatusUpdate");
                        try {
                            messageToSend.setContentObject(inRange);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        if(serviceController != null) {
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
                }
            }
        });

        addBehaviour(new CyclicBehaviour() { // Send data to GlobalNucleus to check setpoint
            @Override
            public void action() {
                int thisID = Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length()-1));
                Iterator it = getAID().getAllAddresses();
                String adresa = (String) it.next();
                String platforma = getAID().getName().split("@")[1];

                ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                AID r = new AID("GlobalNucleus"+ "@" + platforma, AID.ISGUID);
                r.addAddresses(adresa);
                //messageToSend.setContent("Sensing");
                messageToSend.setConversationId("Defect");
                messageToSend.addReceiver(r);

                try {
                    messageToSend.setContentObject(thisID); // send index ID of Defect Controller
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myAgent.send(messageToSend);
            }
        });

    }
}
