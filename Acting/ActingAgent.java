package Acting;

import GEngine.actingHandler;
import GEngine.graphicEngine;
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

    CyclicBehaviour normalCycle = new CyclicBehaviour() {
        @Override
        public void action() {
            ACLMessage mesaj_receptionat = myAgent.receive();
//            if(myAgent.getCurQueueSize()>0) {
//                for (int j = 0; j < myAgent.getCurQueueSize(); j++) {
                    if (mesaj_receptionat != null) {
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
//                            try {
//                                Thread.sleep(IntersectionController.cicleInterval);
                                        graphicEngine.request.add(act);
//                                        if (normalCycleTimer[thisID] || CoreAgent.LocationGraph.get(thisID).getIntersectionActing() == null) {
//                                            CoreAgent.LocationGraph.get(thisID).setIntersectionActing(act.getObjToHandle());
//                                            normalCycleTimer[thisID] = false;
//                                        }
                                }
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            } else {
                                graphicEngine.request.add(act);
//
//                                if (normalCycleTimer[thisID] || CoreAgent.LocationGraph.get(thisID).getIntersectionActing() == null) {
//                                    CoreAgent.LocationGraph.get(thisID).setIntersectionActing(act.getObjToHandle());
//                                    normalCycleTimer[thisID] = false;
//                                }
                            }

//                            if(CoreAgent.LocationGraph.get(thisID).getIntersectionActing() != null) {
//                                boolean equals = false;
//
//                                    equals = intersectionActing.Equals(CoreAgent.LocationGraph.get(thisID).getIntersectionActing());
//
//                                if(!equals){
//                                    CoreAgent.LocationGraph.get(thisID).setIntersectionActing(intersectionActing);
//                                }
//                            }
//                            else
//                                CoreAgent.LocationGraph.get(thisID).setIntersectionActing(intersectionActing);
                        }



                    }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//                }
//            }

        }
    };

CyclicBehaviour centralizedControl =  new CyclicBehaviour() {
    @Override
    public void action() {
        ACLMessage mesaj_receptionat = myAgent.receive();
        if(mesaj_receptionat!=null)
        {
            int thisID = Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length()-1)); // Send Feedback to IntersectionController
            if(mesaj_receptionat.getConversationId()=="ActingCentralizedControl") {
                try {
                    intersectionActing = (IntersectionActing) mesaj_receptionat.getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                actingHandler act = new actingHandler("Intersection", thisID, intersectionActing,true);

                if(graphicEngine.request.size() != 0) {
                    boolean equals = false;
                    for (int i = 0; i < graphicEngine.request.size(); i++) {

                        equals = equals  || graphicEngine.request.get(i).Equals(act);
                        if(equals)
                            break;
                    }
                    if(!equals)
                        graphicEngine.request.add(0,act);
                }
                else
                    graphicEngine.request.add(0,act);
            }
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
};

    @Override
    protected void setup() {

        addBehaviour(normalCycle);

        addBehaviour(centralizedControl);
    }
}
