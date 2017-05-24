package Nucleus;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catalin on 5/14/2017.
 */
public class Nucleus extends Agent {

    public String localaddress = "";
    public List<String> online_cells = new ArrayList<>();
    private String locatie = "Hol"; // unnecessary?

    @Override
    public void setup() {

        ThreadedBehaviourFactory tbf = new ThreadedBehaviourFactory();

        Behaviour discovery = new Behaviour() {

            @Override
            public void action() {

                try {
                    localaddress = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                String base = localaddress.split("\\.")[0] + "." + localaddress.split("\\.")[1] + "." + localaddress.split("\\.")[2];

                int timeout = 50;
                for (int i = 2; i < 10; i++) {
                    String host = base + "." + i;
                    try {
                        if (InetAddress.getByName(host).isReachable(timeout)) {
                            System.out.println(host + " is reachable");
                            //if (i != Integer.parseInt(localaddress.split("\\.")[3]))
                            {
                                ACLMessage discovery = new ACLMessage(ACLMessage.REQUEST);
                                AID rec = new AID("nucleu@" + host + ":1099/JADE", AID.ISGUID);
                                rec.addAddresses("http://" + host + ":7778/acc");
                                discovery.setConversationId("ping");
                                discovery.addReceiver(rec);
                                discovery.setContent(myAgent.getAID().getName() + "~" + localaddress + "~" + locatie);
                                myAgent.send(discovery);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        };

        addBehaviour(tbf.wrap(discovery));

    }
}
