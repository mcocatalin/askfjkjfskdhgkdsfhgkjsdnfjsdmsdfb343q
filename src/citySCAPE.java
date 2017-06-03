package src;

import GEngine.graphicEngine;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.behaviours.Behaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 * Created by Catalin on 12/31/2016.
 */
public class citySCAPE  extends Agent{
    static graphicEngine app;


    public static Runtime runtime;
    public static ContainerController home = null;
    public static Profile p;
    public static AgentController rma;


    Behaviour initBehaviour = new Behaviour() {
        @Override
        public void action() {
            runtime = jade.core.Runtime.instance();
            home = null;
            p = new ProfileImpl();
            home = runtime.createMainContainer(p);

            try {
                rma = home.createNewAgent("GlobalNucleus" ,
                        "Nucleus.GlobalNucleus", new Object[0]);
                rma.start();
                // to print in console!!!
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }


        }

        @Override
        public boolean done() {
            return true;
        }
    };

    @Override
    protected void setup() {
        app = new graphicEngine();

        app.start();

        //addBehaviour(initBehaviour);

    }
}
