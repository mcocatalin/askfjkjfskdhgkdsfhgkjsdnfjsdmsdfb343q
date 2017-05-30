package Sensing;

import GEngine.graphicEngine;
import GEngine.actingHandler;
import Utility.Helper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Iterator;

import java.util.LinkedList;

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


    @Override
    public void setup(){
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
//                Iterator it = getAID().getAllAddresses();
//                String adresa = (String) it.next();
//                String platforma = getAID().getName().split("@")[1];
//
//                ACLMessage mesaj_ventilatie = new ACLMessage(ACLMessage.REQUEST);
//                AID r = new AID( getAIDName() + "@" + platforma, AID.ISGUID);
//                r.addAddresses(adresa);
//                mesaj_ventilatie.setConversationId("Sensing");
//                mesaj_ventilatie.addReceiver(r);
//                mesaj_ventilatie.setContent("accelerate");
//                myAgent.send(mesaj_ventilatie);
//                //System.out.println("Message sent to" + " " + adresa + " " + mesaj_ventilatie.getContent());
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

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
//                ACLMessage mesaj_receptionat = myAgent.receive();
//                if(mesaj_receptionat!=null)
//                {
//                    if(mesaj_receptionat.getContent()=="accelerate") {
//                    graphicEngine.request.add(0,new actingHandler("vehicleMovement","","","",0,0,0,0,0,0,0,0,0,400));
//                    }
//                }
            }
        });
    }

    @Override
    public void SetCommunication(String AIDName, boolean triggered) {
        setAIDName(AIDName);
        setTriggered(triggered);
    }
}
