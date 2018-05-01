package fr.xebia.ldi.ksql.datagen

import fr.xebia.ldi.ksql.datagen.CharacterSelection.{Controller, Human, Player}
import org.json4s.JObject
import org.json4s.JsonDSL._

/**
  * Created by loicmdivad.
  */
case class CharacterSelection(character: Character, controller: Controller, timestamp: Long, player: Player) {

  def isHuman: Boolean = controller match {
    case Human => true
    case _ => false
  }

  def toJson: JObject =
      ( "human" -> isHuman ) ~
      ( "timestamp" -> timestamp ) ~
      ( "player" -> player.toString ) ~
      ( "character" -> character.toString )
}

object CharacterSelection {

  sealed trait Player

  case object PlayerOne extends Player
  case object PlayerTwo extends Player

  sealed trait Controller

  case object Human extends Controller
  case object Robot extends Controller
}