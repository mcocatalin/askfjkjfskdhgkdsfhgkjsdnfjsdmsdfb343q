package src;

import GEngine.graphicEngine;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.Runtime;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Iterator;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.io.IOException;

import static GEngine.graphicEngine.EventLogEntries;

/**
 * Created by Catalin on 12/31/2016.
 */
public class CitySCAPE extends Agent{
    static graphicEngine app;


    public static Runtime runtime;
    public static ContainerController home = null;
    public static Profile p;
    public static AgentController rma;


    Behaviour initBehaviour = new Behaviour() {
        @Override
        public void action() {

            EventLogEntries.add("Environement loaded.");

        }

        @Override
        public boolean done() {
            return true;
        }
    };

    Behaviour sendWorldNet = new CyclicBehaviour() {
        @Override
        public void action() {
            if (app.doneCreatingWorldNet) {

                System.out.println("In send world net!");
                Iterator it = getAID().getAllAddresses();
                String adresa = (String) it.next();
                String platforma = getAID().getName().split("@")[1];

                ACLMessage messageToSend = new ACLMessage(ACLMessage.INFORM);
                AID r = new AID("CoreAgent" + "@" + platforma, AID.ISGUID);

                messageToSend.setConversationId("WorldDetect");

                try {
                    AID defectedAID = new AID("CoreAgent" + "@" + platforma, AID.ISGUID);
                    messageToSend.setContentObject(graphicEngine.worldDetectors); // send AID of Defect Controller
                } catch (IOException e) {
                    e.printStackTrace();
                }
                messageToSend.addReceiver(r);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myAgent.send(messageToSend);

                app.doneCreatingWorldNet = false;

            }
        }
    };

    @Override
    protected void setup() {
        app = new graphicEngine();

        app.start();

        addBehaviour(initBehaviour);

        addBehaviour(sendWorldNet);

    }
}
