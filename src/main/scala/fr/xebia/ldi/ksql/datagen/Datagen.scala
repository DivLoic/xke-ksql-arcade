package fr.xebia.ldi.ksql.datagen

import java.util.Properties

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, DelayOverflowStrategy, OverflowStrategy}
import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.config.ConfigFactory
import fr.xebia.ldi.ksql.datagen.Character.{Akuma, Ken, Ryu, `Chun-Li`}
import fr.xebia.ldi.ksql.datagen.CharacterSelection.{Human, Machine, PlayerOne, PlayerTwo}
import org.apache.kafka.clients.admin.{AdminClient, CreateTopicsResult, NewTopic}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.connect.json.JsonSerializer
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConverters._

/**
  * Created by loicmdivad.
  */
case class Datagen(props: Properties) {

  val topics = Vector(
    new NewTopic("SELECT-SCREEN", 6, 1)
  )

  def topicCreation: Try[CreateTopicsResult] = {
    val client: AdminClient = AdminClient.create(props)
    Try(client.createTopics(topics.asJava))
  }
}

object Datagen extends App {

  lazy val logger = LoggerFactory.getLogger(getClass)

  implicit val formats = DefaultFormats

  implicit val system: ActorSystem = ActorSystem()

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val conf = ConfigFactory.load()

  val producerSettings: ProducerSettings[String, JsonNode] =
    ProducerSettings(system, new StringSerializer(), new JsonSerializer())

  val datagen = Datagen(producerSettings.properties)

  datagen.topicCreation match {
    case Failure(t) =>
      logger error "Fail to create the required topics!"

    case Success(result) => logger info "Starting the Data Generator"

      Source.cycle(() => {
        Vector(
          CharacterSelection(Ryu, Human, 0L, PlayerOne),
          CharacterSelection(Ken, Human, 0L, PlayerTwo),
          CharacterSelection(`Chun-Li`, Machine, 0L, PlayerOne),
          CharacterSelection(Akuma, Human, 0L, PlayerOne)
        ).toIterator
      })
        .delay(1 second, DelayOverflowStrategy.backpressure)
        .buffer(4, OverflowStrategy.backpressure)
        .map(selection => asJsonNode(selection.toJson))
        .map(node => new ProducerRecord("SELECTION", "0", node))
        .runWith(Producer.plainSink(producerSettings))

  }

  def apply(props: Map[String, String]): Datagen = new Datagen(
    props.foldLeft(new Properties())((properties, map) => {
      properties.put(map._1, map._2)
      properties
    })
  )

}
