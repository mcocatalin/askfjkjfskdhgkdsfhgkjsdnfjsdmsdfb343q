package src;

import GEngine.graphicEngine;
import jade.core.Agent;

/**
 * Created by Catalin on 12/31/2016.
 */
public class citySCAPE  extends Agent{
    static graphicEngine app;
    @Override
    protected void setup() {
        app = new graphicEngine();

        app.start();

    }
}
