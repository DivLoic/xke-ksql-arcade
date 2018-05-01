package fr.xebia.ldi.ksql.datagen

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by loicmdivad.
  */
case class Machine(id: Int, master: ActorRef) extends Actor {

  lazy val logger: Logger = LoggerFactory.getLogger(getClass)

  override def receive: Receive = {
    case _ => logger warn "Machine starting !"
  }

}


object Machine {

  def ref(id: Int, publisher: ActorRef)(implicit system: ActorSystem): ActorRef =
    system.actorOf(Props.apply(classOf[Machine], id, publisher))
}