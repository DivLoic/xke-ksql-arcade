package fr.xebia.ldi.ksql.datagen

import java.util.Properties

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.kafka.scaladsl.Producer
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.config.ConfigFactory
import fr.xebia.ldi.ksql.datagen.Arena.TurnOnMachine
import fr.xebia.ldi.ksql.datagen.CharactersGrid.allCharacters
import org.apache.kafka.clients.admin.{AdminClient, CreateTopicsResult, NewTopic}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.connect.json.JsonSerializer
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

/**
  * Created by loicmdivad.
  */
case class Datagen(props: Properties) {

  val topics = Vector(
    new NewTopic("CLICKS", 6, 1),
    new NewTopic("FIGHTERS", 1, 1)
  )

  def topicCreation: Try[CreateTopicsResult] = {
    val client: AdminClient = AdminClient.create(props)
    Try(client.createTopics(topics.asJava))
  }

  def `start-n-first`(n: Int, arena: ActorRef): Unit =
    (1 to n).foreach(arena ! TurnOnMachine(_))
}

object Datagen extends App {

  lazy val logger = LoggerFactory.getLogger(getClass)

  implicit val formats: DefaultFormats.type = DefaultFormats

  implicit val system: ActorSystem = ActorSystem()

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val conf = ConfigFactory.load()

  val producerSettings: ProducerSettings[String, JsonNode] =
    ProducerSettings(system, new StringSerializer(), new JsonSerializer())

  val datagen: Datagen = Datagen(producerSettings.properties)

  datagen.topicCreation match {
    case Failure(t) =>
      logger error("Fail to create the required topics : ", t)
      materializer.shutdown()
      system.terminate()

    case Success(result) =>

      logger info "Starting the Data Generator"

      logger info "Loading fighter reference"

      Source.fromIterator(() => allCharacters.toIterator).map { fighter =>
        new ProducerRecord("FIGHTERS", fighter.id.toString, asJsonNode(fighter.toJson))
      }
        .runWith(Producer.plainSink(producerSettings))

      logger info "Loading the selection events"

      val actorProducer: ActorRef = Source.actorRef[Selection](10, OverflowStrategy.dropBuffer)
          .map(node => new ProducerRecord("CLICKS", node.machineId.get, asJsonNode(node.toJson)))
          .map(ProducerMessage.Message(_, NotUsed))
          .via(Producer.flow(producerSettings))
          .to(Sink.ignore)
          .run()

      val arena: ActorRef = Arena.`with-n-machines`(20, actorProducer)

      datagen.`start-n-first`(15, arena)
  }

  def apply(props: Map[String, String]): Datagen = new Datagen(
    props.foldLeft(new Properties())((properties, map) => {
      properties.put(map._1, map._2)
      properties
    })
  )

}
