package Controlling;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

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
//                               intersectionSensing = (IntersectionSensing) mesaj_receptionat.getContentObject();
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
