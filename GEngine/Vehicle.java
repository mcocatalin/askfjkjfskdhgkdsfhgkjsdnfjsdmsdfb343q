package GEngine;

import Utility.Helper;
import com.sun.deploy.util.StringUtils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Iterator;
import jme3tools.navigation.StringUtil;

import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Catalin on 3/18/2017.
 */
public class Vehicle extends Agent {

    public LinkedList<String> AIDs = new LinkedList<String>();

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage mesaj_receptionat = myAgent.receive();
                if(mesaj_receptionat!=null) {
//                    myAgent.receive();
//                    if (mesaj_receptionat.getConversationId() == "stropitori") {
//                        stropitori = Boolean.parseBoolean(mesaj_receptionat.getContent());
//                        environment.sprinkler=stropitori;
//                    }

                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {


                Iterator it = getAID().getAllAddresses();
                String adresa = (String) it.next();
                String platforma = getAID().getName().split("@")[1];

                ACLMessage mesaj_ventilatie = new ACLMessage(ACLMessage.REQUEST);
                AID r = new AID("Controlling.VehicleController@" + platforma, AID.ISGUID);
                r.addAddresses(adresa);
                mesaj_ventilatie.setConversationId("Controlling.VehicleController");
                mesaj_ventilatie.addReceiver(r);
                mesaj_ventilatie.setContent("accelerate");
                // StringUtils.substringBetween(mesaj_ventilatie.getContent(), "(", ")");

            }


        });

        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                AIDs = Helper.GetAIDs();
            }

            @Override
            public boolean done() {
                return true;
            }
        });
    }
}
