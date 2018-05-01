package fr.xebia.ldi.ksql.datagen

import java.time.{LocalDateTime, ZoneOffset}

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import fr.xebia.ldi.ksql.datagen.Arena.{SelectScreenClick, TurnOnMachine}
import fr.xebia.ldi.ksql.datagen.Character.Ryu
import fr.xebia.ldi.ksql.datagen.CharacterSelection.{Human, PlayerOne}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by loicmdivad.
  */
case class Machine(id: Int, actorProducer: ActorRef) extends Actor {

  lazy val logger: Logger = LoggerFactory.getLogger(getClass)

  override def receive: Receive = {
    case _@TurnOnMachine(`id`, _) =>
      logger info s"Machine n° $id is starting ..."
      context.system.scheduler.scheduleOnce(1 seconds, self, selection())

    case SelectScreenClick() =>
      logger debug s"The machine $id received a new click from the select screen"
      context.system.scheduler.scheduleOnce(1 seconds, self, selection())

    case message => logger warn s"Unknown message received by the machine $id: $message"
  }

  def selection(): SelectScreenClick = {
    actorProducer ! CharacterSelection(Ryu, Human, LocalDateTime.now.toInstant(ZoneOffset.UTC).toEpochMilli, PlayerOne)
    SelectScreenClick()
  }
}


object Machine {

  def ref(id: Int, actorProducer: ActorRef)(implicit system: ActorSystem): ActorRef =
    system.actorOf(Props.apply(classOf[Machine], id, actorProducer))
}