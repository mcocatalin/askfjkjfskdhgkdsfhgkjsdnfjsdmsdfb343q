package Controlling;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

/**
 * Created by Catalin on 5/27/2017.
 */
public class IntersectionController extends Agent {

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {

            }
        });
    }
}
