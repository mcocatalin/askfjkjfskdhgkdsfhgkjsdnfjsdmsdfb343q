package Controlling;

import GEngine.graphicEngine;
import GEngine.actingHandler;
import Utility.IntersectionLaneValues;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/**
 * Created by Catalin on 3/21/2017.
 */
public class VehicleController extends Agent implements IController{

@Override
   public void setup(){
       addBehaviour(new CyclicBehaviour() {
           @Override
           public void action() {
               ACLMessage mesaj_receptionat = myAgent.receive();
//                   if(mesaj_receptionat!=null)
//                   {
//                       if(mesaj_receptionat.getContent()=="Sensing") {
//                           try {
//                               intersectionLaneValues = (IntersectionLaneValues) mesaj_receptionat.getContentObject();
//                           } catch (UnreadableException e) {
//                               e.printStackTrace();
//                           }
//                       }
//
//                   }
           }
       });
   }
}
