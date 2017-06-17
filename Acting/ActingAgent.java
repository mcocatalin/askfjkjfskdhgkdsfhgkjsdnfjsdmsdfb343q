package Acting;

import GEngine.actingHandler;
import GEngine.graphicEngine;
import Nucleus.CoreAgent;
import Utility.IntersectionActing;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/**
 * Created by Catalin on 5/29/2017.
 */
public class ActingAgent extends Agent implements IActing {

    IntersectionActing intersectionActing;
    boolean state = false; // Alternate between South-Nord and East-West

    CyclicBehaviour normalCycle = new CyclicBehaviour() {
        @Override
        public void action() {
            ACLMessage mesaj_receptionat = myAgent.receive();
                    if (mesaj_receptionat != null) {
                        if (CoreAgent.automaticMode) {
                            int thisID = Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length() - 1)); // Send Feedback to IntersectionController
                            if (mesaj_receptionat.getConversationId() == "ActingNormalCycle") {
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
                            graphicEngine.request.add(0, act);
                    } else
                        graphicEngine.request.add(0, act);
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
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(!CoreAgent.automaticMode) {

            int thisID = Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length() - 1)); // Send Feedback to IntersectionController

            intersectionActing = new IntersectionActing();
            intersectionActing.setLaneDirection(state,!state);
            state = !state;

            actingHandler act = new actingHandler("Intersection", thisID, intersectionActing, true);

            if (graphicEngine.request.size() != 0) {
                boolean equals = false;
                for (int i = 0; i < graphicEngine.request.size(); i++) {

                    equals = equals || graphicEngine.request.get(i).Equals(act);
                    if (equals)
                        break;
                }
                if (!equals)
                    graphicEngine.request.add(act);
            } else
                graphicEngine.request.add(act);
        }
    }
};

    @Override
    protected void setup() {

        addBehaviour(manualCycle);

        addBehaviour(normalCycle);

        addBehaviour(centralizedControl);
    }
}
