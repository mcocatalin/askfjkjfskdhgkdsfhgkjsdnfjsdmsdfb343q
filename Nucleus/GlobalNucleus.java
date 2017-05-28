package Nucleus;

import GEngine.graphicEngine;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

/**
 * Created by Catalin on 5/22/2017.
 */
public class GlobalNucleus extends Agent {

    private boolean done = false;
    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
            if(graphicEngine.startApplication && !done) {

                // Get a hold on JADE runtime
                // Runtime rt = Runtime.instance();
                Runtime rt = Runtime.instance();

                // Exit the JVM when there are no more containers around
                rt.setCloseVM(true);
                System.out.print("runtime created\n");

                // Create a default profile
                Profile profile = new ProfileImpl(null, 1200, null);
                System.out.print("profile created\n");

                System.out.println("Launching a whole in-process platform..." + profile);
                jade.wrapper.AgentContainer mainContainer = rt.createMainContainer(profile);

                // now set the default Profile to start a container
                ProfileImpl pContainer = new ProfileImpl(null, 1234, null);
                System.out.println("Launching the agent container ..." + pContainer);

                jade.wrapper.AgentContainer cont = rt.createAgentContainer(pContainer);
                System.out.println("Launching the agent container after ..." + pContainer);

                System.out.println("containers created");
                System.out.println("Launching the rma agent on the main container ...");
                AgentController rma = null;
                try {
                    rma = mainContainer.createNewAgent("Controlling.VehicleController1",
                            "Controlling.VehicleController", new Object[0]);
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
                try {
                    rma.start();
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
                AgentController rma1 = null;
                try {
                    rma1 = mainContainer.createNewAgent("GEngine.Vehicle1",
                            "GEngine.Vehicle", new Object[0]);
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
                try {
                    rma1.start();
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
                done = false;
            }

            }
        });
    }
}
