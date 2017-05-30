package Controlling;

import Utility.IntersectionSensing;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.leap.Iterator;

import java.io.IOException;

/**
 * Created by Catalin on 5/27/2017.
 */
public class IntersectionController extends Agent implements IController{

    IntersectionSensing intersectionSensing;

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() { // Disabled intersection
            @Override
            public void action() { // Receive world feedback
                ACLMessage mesaj_receptionat = myAgent.receive();
                if(mesaj_receptionat!=null)
                {
                    if(mesaj_receptionat.getConversationId()=="Sensing") {
                        try {
                            intersectionSensing = (IntersectionSensing) mesaj_receptionat.getContentObject();
                        } catch (UnreadableException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        addBehaviour(new CyclicBehaviour() { // Send data to Nucleus to decide behaviour
            @Override
            public void action() {
                Iterator it = getAID().getAllAddresses();
                String adresa = (String) it.next();
                String platforma = getAID().getName().split("@")[1];

                ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                AID r = new AID("IntersectionNucleus" + this.myAgent.getAID().getLocalName().substring(this.myAgent.getAID().getLocalName().length()-1)+ "@" + platforma, AID.ISGUID);
                r.addAddresses(adresa);
                //messageToSend.setContent("Sensing");
                messageToSend.setConversationId("StatusUpdate");
                messageToSend.addReceiver(r);

                try {
                    messageToSend.setContentObject(intersectionSensing);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myAgent.send(messageToSend);
            }
        });

    }
}
