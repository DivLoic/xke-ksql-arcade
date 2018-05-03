package fr.xebia.ldi.ksql.datagen

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import fr.xebia.ldi.ksql.datagen.Arena.{SimpleMessage, TurnOnMachine}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by loicmdivad.
  */
case class Arena(machines: Map[Int, ActorRef]) extends Actor {

  lazy val logger: Logger = LoggerFactory.getLogger(getClass)

  override def receive: Receive = {
    case mess: TurnOnMachine => machines.get(mess.id).foreach(_ ! mess)
    case mess: SimpleMessage => logger warn s"Receive the following message: ${mess.content}"
    case _ => logger error "Not valid type of message"
  }
}

object Arena {

  sealed trait EventMessage

  final case class SimpleMessage(content: String) extends EventMessage
  final case class TurnOnMachine(id: Int, content: String = "Ready? Fight!") extends EventMessage
  final case class SelectScreenClick() extends EventMessage

  def `with-n-machines`(n: Int, publisher: ActorRef)(implicit system: ActorSystem): ActorRef = {

    val machines: Map[Int, ActorRef] = (1 to n).map(i => i -> Machine.ref(i, publisher)).toMap

    system.actorOf(Props.apply(classOf[Arena], machines))
  }
}
