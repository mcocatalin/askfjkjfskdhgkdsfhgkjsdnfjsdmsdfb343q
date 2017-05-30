package Controlling;

import GEngine.graphicEngine;
import GEngine.actingHandler;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Created by Catalin on 3/21/2017.
 */
public class VehicleController extends Agent{

@Override
   public void setup(){
       addBehaviour(new CyclicBehaviour() {
           @Override
           public void action() {
               ACLMessage mesaj_receptionat = this.getAgent().receive();
               if(mesaj_receptionat!=null)
               {
                   if(mesaj_receptionat.getContent()=="accelerate") {
                   }
                   // graphicEngine.request.add(0,new actingHandler("vehicleMovement","","","",0,0,0,0,0,0,0,0,0,400));

                   }
           }
       });
   }
}
