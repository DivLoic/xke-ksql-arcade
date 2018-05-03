package fr.xebia.ldi.ksql.datagen

import fr.xebia.ldi.ksql.datagen.Selection._
import fr.xebia.ldi.ksql.datagen.CharactersGrid.Characters
import org.json4s.JObject
import org.json4s.JsonDSL._
import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by loicmdivad.
  */
case class Selection(timestamp: Long,
                     controller: Controller,
                     character: Characters,
                     player: Player,
                     game: Game,
                     machineId: Option[String]) {

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

object Selection {

  sealed trait Player {
    def getPlayerId: Short
  }

  final case class PlayerOne() extends Player { override def getPlayerId: Short = 1 }
  final case class PlayerTwo() extends Player { override def getPlayerId: Short = 2 }

  implicit val arbitraryPad: Arbitrary[Player] = Arbitrary(Gen.frequency((5, PlayerOne()), (4, PlayerTwo())))

  sealed trait Controller

  case object Human extends Controller
  case object Robot extends Controller

  sealed trait Game

  case object StreetFighter extends Game
  case object SoulCalibur extends Game
  case object Takken extends Game
  case object KingOfFight extends Game
  case object BudoKai extends Game

  implicit val arbitraryGame: Arbitrary[Game] = Arbitrary(
    Gen.frequency(
      (60, StreetFighter),
      (15, Takken),
      (10, SoulCalibur),
      (10, KingOfFight),
      (5, BudoKai)
    )
  )
}