package Sensing;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.HashMap;
import jade.util.leap.Iterator;
import jade.util.leap.Map;

import java.util.Dictionary;

/**
 * Created by Catalin on 5/1/2017.
 */
public class SensingAgent extends Agent implements Sensing.ISensing {

    //public Map<Integer,String> state = new HashMap<String,String>();

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
                Iterator it = getAID().getAllAddresses();
                String adresa = (String) it.next();
                String platforma = getAID().getName().split("@")[1];

                ACLMessage mesaj_ventilatie = new ACLMessage(ACLMessage.REQUEST);
                AID r = new AID( getAIDName() + "@" + platforma, AID.ISGUID);
                r.addAddresses(adresa);
                mesaj_ventilatie.setConversationId("Sensing");
                mesaj_ventilatie.addReceiver(r);
                mesaj_ventilatie.setContent("accelerate");
                myAgent.send(mesaj_ventilatie);
                System.out.println("Message sent to" + " " + adresa + " " + mesaj_ventilatie.getContent());
            }
        });
    }

    @Override
    public void SetCommunication(String AIDName, boolean triggered) {
        setAIDName(AIDName);
        setTriggered(triggered);
    }
}
