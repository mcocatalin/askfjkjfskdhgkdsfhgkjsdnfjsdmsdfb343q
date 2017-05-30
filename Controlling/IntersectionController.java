package Controlling;

import Utility.IntersectionLaneValues;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/**
 * Created by Catalin on 5/27/2017.
 */
public class IntersectionController extends Agent implements IController{

    IntersectionLaneValues intersectionLaneValues;

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() { // Disabled intersection
            @Override
            public void action() {
                ACLMessage mesaj_receptionat = myAgent.receive();
                if(mesaj_receptionat!=null)
                {
                    if(mesaj_receptionat.getConversationId()=="Sensing") {
                        try {
                            intersectionLaneValues = (IntersectionLaneValues) mesaj_receptionat.getContentObject();
                        } catch (UnreadableException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });
    }
}
