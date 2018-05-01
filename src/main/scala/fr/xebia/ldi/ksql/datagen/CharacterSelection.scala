package fr.xebia.ldi.ksql.datagen

import fr.xebia.ldi.ksql.datagen.CharacterSelection._
import org.json4s.JObject
import org.json4s.JsonDSL._

/**
  * Created by loicmdivad.
  */
case class CharacterSelection(character: Character,
                               game: Game, controller: Controller,
                               timestamp: Long,
                               player: Player) {

  def isHuman: Boolean = controller match {
    case Human => true
    case _ => false
  }

  def toJson: JObject =
    ( "ts" -> timestamp ) ~
    ( "human" -> isHuman ) ~
    ( "game" -> game.toString ) ~
    ( "player" -> player.getPlayerId ) ~
    ( "character" -> character.toString )
}

object CharacterSelection {

  sealed trait Player {
    def getPlayerId: Short
  }

  final case class PlayerOne() extends Player { override def getPlayerId: Short = 1 }
  final case class PlayerTwo() extends Player { override def getPlayerId: Short = 2 }

  sealed trait Controller

  case object Human extends Controller
  case object Robot extends Controller

  sealed trait Game

  case object StreetFighter extends Game
  case object SoulCalibur extends Game
  case object Takken extends Game
  case object KingOfFight extends Game
  case object BudoKai extends Game


}