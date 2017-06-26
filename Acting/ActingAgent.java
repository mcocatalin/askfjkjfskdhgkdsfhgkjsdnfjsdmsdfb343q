package Acting;

import GEngine.actingHandler;
import GEngine.graphicEngine;
import Nucleus.CoreAgent;
import Utility.IntersectionActing;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.Random;

import static GEngine.graphicEngine.ActiveIntersectionControllers;

/**
 * Created by Catalin on 5/29/2017.
 */
public class ActingAgent extends Agent implements IActing {

    IntersectionActing intersectionActing;
    boolean state = (new Random()).nextInt(1) != 1; // Alternate between South-Nord and East-West
    int index = (new Random()).nextInt(4);

    CyclicBehaviour receiverBehaviour = new CyclicBehaviour() {
        @Override
        public void action() {
            ACLMessage mesaj_receptionat = myAgent.receive();
                    if (mesaj_receptionat != null) {
                        if (CoreAgent.automaticMode) {
                            int senderID = Integer.parseInt(mesaj_receptionat.getSender().getLocalName().substring(mesaj_receptionat.getSender().getLocalName().length() - 1)); // Send Feedback to IntersectionController
                            int thisID = Integer.parseInt(myAgent.getAID().getLocalName().substring(myAgent.getAID().getLocalName().length() - 1));
                            if (mesaj_receptionat.getConversationId() == "ActingNormalCycle") {
                                try {
                                    intersectionActing = (IntersectionActing) mesaj_receptionat.getContentObject();
                                } catch (UnreadableException e) {
                                    e.printStackTrace();
                                }
                                actingHandler act = new actingHandler("Intersection", thisID, intersectionActing, true);

                                if (graphicEngine.request.size() != 0) {
                                    if(!ActiveIntersectionControllers[thisID])
                                    {
                                        for (int i = 0; i < graphicEngine.request.size(); i++){
                                            if(graphicEngine.request.get(i).getComponentID() == thisID)
                                                graphicEngine.request.remove(i);
                                        }
                                    }
                                    boolean equals = false;
                                    for (int i = 0; i < graphicEngine.request.size(); i++) {

                                        equals = equals || graphicEngine.request.get(i).Equals(act);
                                        if (equals)
                                            break;
                                    }
                                    if (!equals) {
                                        graphicEngine.request.add(act);
                                    }
                                } else {
                                    graphicEngine.request.add(act);
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

CyclicBehaviour centralizedControl =  new CyclicBehaviour() {
    @Override
    public void action() {
        ACLMessage mesaj_receptionat = myAgent.receive();
        if(mesaj_receptionat!=null) {
            if (CoreAgent.automaticMode) {
                int thisID = Integer.parseInt(mesaj_receptionat.getSender().getLocalName().substring(mesaj_receptionat.getSender().getLocalName().length() - 1));
                if (mesaj_receptionat.getConversationId() == "ActingCentralizedControl") {
                    try {
                        intersectionActing = (IntersectionActing) mesaj_receptionat.getContentObject();
                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }
                    actingHandler act = new actingHandler("Intersection", thisID, intersectionActing, true);

                    if (graphicEngine.request.size() != 0) {
                        boolean equals = false;
                        for (int i = 0; i < graphicEngine.request.size(); i++) {

                            equals = equals || graphicEngine.request.get(i).Equals(act);
                            if (equals)
                                break;
                        }
                        if (!equals)
                            graphicEngine.request.add( act);
                    } else
                        graphicEngine.request.add( act);
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

CyclicBehaviour manualCycle = new CyclicBehaviour() {
    @Override
    public void action() {

        try {
            Thread.sleep(40);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(!CoreAgent.automaticMode) {

            index = index % 4;

            int thisID = Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length() - 1)); // Send Feedback to IntersectionController

            intersectionActing = new IntersectionActing();
            if(!graphicEngine.controllerPairedState) {
                intersectionActing.setLaneByTrafficLightID(index);
            }else{
                intersectionActing.setLaneDirection(state, !state);
                state = !state;
            }

            actingHandler act = new actingHandler("Intersection", thisID, intersectionActing, true);
            if(graphicEngine.request != null) {
                if (graphicEngine.request.size() > 0) {
                    boolean equals = false;
                    for (int i = 0; i < graphicEngine.request.size(); i++) {

                        equals = equals || graphicEngine.request.get(i).Equals(act);
                        if (equals)
                            break;
                    }
                    if (!equals) {
                        graphicEngine.request.add(act);
                        index++;
                    }
                } else {
                    graphicEngine.request.add(act);
                    index++;
                }
            }
        }
    }
};

    @Override
    protected void setup() {

        addBehaviour(manualCycle);

        addBehaviour(receiverBehaviour);

        addBehaviour(centralizedControl);
    }
}
