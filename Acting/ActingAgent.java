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

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage mesaj_receptionat = myAgent.receive();
                if(mesaj_receptionat!=null)
                {
                    int thisID =Integer.parseInt(this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length()-1)); // Send Feedback to IntersectionController
                    if(mesaj_receptionat.getContent()=="Acting") {
                        try {
                            intersectionActing = (IntersectionActing) mesaj_receptionat.getContentObject();
                        } catch (UnreadableException e) {
                            e.printStackTrace();
                        }
                        //Object objHandle = intersectionActing;
                        graphicEngine.request.add(new actingHandler("Intersection", thisID, intersectionActing));
                    }
                }
            }
        });
    }
}
