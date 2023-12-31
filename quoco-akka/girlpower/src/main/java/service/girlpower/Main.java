package service.girlpower;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import service.actor.Quoter;
import service.message.Init;

public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create(); //creates actor system
        ActorRef ref = system.actorOf(Props.create(Quoter.class), "girlpower"); //actor reference
        ref.tell(new Init(new GPQService()), null);
        ActorSelection selection =
                system.actorSelection("akka.tcp://default@127.0.0.1:2551/user/broker"); //registration
        selection.tell("register", ref);
    }
}
