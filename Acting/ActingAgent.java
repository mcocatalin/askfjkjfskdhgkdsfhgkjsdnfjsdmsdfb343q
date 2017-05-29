package Acting;

import GEngine.actingHandler;
import GEngine.graphicEngine;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Created by Catalin on 5/29/2017.
 */
public class ActingAgent extends Agent implements IActing {

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage mesaj_receptionat = myAgent.receive();
                if(mesaj_receptionat!=null)
                {
                    if(mesaj_receptionat.getContent()=="accelerate") {
                    }
                    graphicEngine.request.add(0,new actingHandler("Vehicle",0,null));

                }
            }
        });
    }
}
