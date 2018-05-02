package fr.xebia.ldi.ksql.datagen

import java.time.{LocalDateTime, ZoneOffset}

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import fr.xebia.ldi.ksql.datagen.Arena.{SelectScreenClick, TurnOnMachine}
import fr.xebia.ldi.ksql.datagen.CharactersGrid.{Characters, `?`}
import fr.xebia.ldi.ksql.datagen.CharacterSelection._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalacheck.rng.Seed
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by loicmdivad.
  */
case class Machine(id: Int, actorProducer: ActorRef) extends Actor {

  lazy val logger: Logger = LoggerFactory.getLogger(getClass)
  lazy val default: Gen.Parameters = Gen.Parameters.default.withSize(3000)

  override def receive: Receive = {
    case TurnOnMachine(`id`, _) =>
      logger info s"Machine n° $id is starting ..."
      context.system.scheduler.scheduleOnce(1 seconds, self, selection())

    case SelectScreenClick() =>
      logger debug s"The machine $id received a new click from the select screen"
      context.system.scheduler.scheduleOnce(0.2 seconds, self, selection())

    case message => logger warn s"Unknown message received by the machine $id: $message"
  }

  def generate: Gen[CharacterSelection] = for {

    instantGen <- Gen.const(LocalDateTime.now.toInstant(ZoneOffset.UTC).toEpochMilli)

    humanPlayerGen <- Gen.frequency((5, Human), (2, Robot))

    instantGen <- Gen.choose(instantGen - 15000L, instantGen)

    playerGen <- arbitrary[Player]

    gameGen <- arbitrary[Game]

    characterGen <- gameGen match {
      case StreetFighter => arbitrary[Characters]
      case _ => Gen.const(`?`)
    }

  } yield CharacterSelection(characterGen, gameGen, humanPlayerGen, instantGen, playerGen)

  def selection(): SelectScreenClick = {
    generate.apply(default, Seed.random()).foreach(actorProducer ! _)
    SelectScreenClick()
  }
}


object Machine {

  def ref(id: Int, actorProducer: ActorRef)(implicit system: ActorSystem): ActorRef =
    system.actorOf(Props.apply(classOf[Machine], id, actorProducer))
}