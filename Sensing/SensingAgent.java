package Sensing;

import GEngine.sensingHandler;
import Utility.Helper;
import Utility.WorldDetector;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Iterator;

import java.io.IOException;
import java.util.LinkedList;

import static GEngine.graphicEngine.response;
import static GEngine.graphicEngine.worldDetectors;

/**
 * Created by Catalin on 5/1/2017.
 */
public class SensingAgent extends Agent implements Sensing.ISensing {

    //public Map<Integer,String> state = new HashMap<String,String>();

    LinkedList<String> AIDs;

    // Communication Members
        String AIDName;
        boolean triggered;

    public String getAIDName() {
        return AIDName;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setAIDName(String AIDName) {
        this.AIDName = AIDName;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    private boolean detectedWorld = false;

    Behaviour detectWorld = new CyclicBehaviour() {
        @Override
        public void action() {
            if (!detectedWorld) {
                if (worldDetectors.size() > 0) {
                    int thisID = Integer.parseInt(this.myAgent.getLocalName().substring(this.myAgent.getLocalName().length() - 1));
                    if (worldDetectors.get(thisID) != null) {
                        WorldDetector wd = worldDetectors.get(thisID);

                        Iterator it = getAID().getAllAddresses();
                        String adresa = (String) it.next();
                        String platforma = getAID().getName().split("@")[1];

                        ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                        AID r = new AID(Helper.IntersectionController + thisID + "@" + platforma, AID.ISGUID);
                        r.addAddresses(adresa);
                        //messageToSend.setContent("Sensing");
                        messageToSend.setConversationId("WorldDetector");
                        messageToSend.addReceiver(r);

                        try {
                            messageToSend.setContentObject(wd);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        myAgent.send(messageToSend);
                    }

                }
                detectedWorld = true;
            }
        }
    };

    @Override
    public void setup(){

        addBehaviour(detectWorld);

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action()
            { // Send Message to Controller
                synchronized (response) { // Thread access at the same time
                      if(response.size()>0) {

                          sensingHandler toHandle = response.remove(0);
                          if (toHandle != null) {
                              //if (this.myAgent.getAID().getLocalName().contains(toHandle.getType() + "Sensing" + toHandle.getComponentID())) {

                                  Iterator it = getAID().getAllAddresses();
                                  String adresa = (String) it.next();
                                  String platforma = getAID().getName().split("@")[1];

                                  ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                                  AID r = new AID(toHandle.getType() + "Controller" + toHandle.getComponentID() + "@" + platforma, AID.ISGUID);
                                  r.addAddresses(adresa);
                                  //messageToSend.setContent("Sensing");
                                  messageToSend.setConversationId("Sensing");
                                  messageToSend.addReceiver(r);

                                  try {
                                      messageToSend.setContentObject(toHandle.getObjToHandle());
                                  } catch (IOException e) {
                                      e.printStackTrace();
                                  }

                                  try {
                                      Thread.sleep(50);
                                  } catch (InterruptedException e) {
                                      e.printStackTrace();
                                  }
                                  myAgent.send(messageToSend);
                              }
                          //}
                      }
                }
            }
        });

        addBehaviour(new Behaviour() { // citeste retea de agenti
            @Override
            public void action() {
                AIDs = Helper.GetAIDs();
//                for (String AID:AIDs
//                     ) {
//                    if(!AID.contains("Controlling")) {
//                        AIDs.remove(AID);
//                    }
//                }
            }

            @Override
            public boolean done() {
                return true;
            }
        });

    }

    @Override
    public void SetCommunication(String AIDName, boolean triggered) {
        setAIDName(AIDName);
        setTriggered(triggered);
    }
}
