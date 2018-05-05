package fr.xebia.ldi.ksql.datagen

import java.time.{LocalDateTime, ZoneId, ZoneOffset}

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import fr.xebia.ldi.ksql.datagen.Arena.{SelectScreenClick, TurnOnMachine}
import fr.xebia.ldi.ksql.datagen.CharactersGrid.{Akuma, Characters, `?`}
import fr.xebia.ldi.ksql.datagen.Selection._
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
  val zoneId: ZoneId = ZoneId.of("Europe/Paris")

  override def receive: Receive = {
    case TurnOnMachine(`id`, _) =>
      logger info s"Machine n° $id is starting ..."
      context.system.scheduler.scheduleOnce(1 seconds, self, selection())

    case SelectScreenClick() =>
      logger debug s"The machine $id received a new click from the select screen"
      val delay: Double = Gen.choose(0.5, 2.0).sample.getOrElse(2.5)
      context.system.scheduler.scheduleOnce(delay seconds, self, selection())

    case message => logger warn s"Unknown message received by the machine $id: $message"
  }

  def generate: Gen[Selection] = for {

    instantGen <- Gen.const(LocalDateTime.now(zoneId).toInstant(ZoneOffset.UTC).toEpochMilli)

    humanPlayerGen <- Gen.frequency((5, Human), (1, Robot))

    playerGen <- arbitrary[Player]

    gameGen <- arbitrary[Game]

    machineid <- Gen.const(Some(s"TERM-$id"))

    characterGen <- gameGen match {
      case StreetFighter => arbitrary[Characters]
      case _ => Gen.const(`?`)
    }

    timestampGen <- characterGen match {
      case Akuma => Gen.choose(instantGen - 15000L, instantGen)
      case _ => Gen.const(instantGen)
    }

  } yield Selection(timestampGen, humanPlayerGen, characterGen, playerGen, gameGen, machineid)

  def selection(): SelectScreenClick = {
    generate.apply(default, Seed.random()).foreach(actorProducer ! _)
    SelectScreenClick()
  }
}


object Machine {

  def ref(id: Int, actorProducer: ActorRef)(implicit system: ActorSystem): ActorRef =
    system.actorOf(Props.apply(classOf[Machine], id, actorProducer))
}