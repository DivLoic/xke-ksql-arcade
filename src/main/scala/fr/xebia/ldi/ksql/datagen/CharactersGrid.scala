package fr.xebia.ldi.ksql.datagen

import org.json4s.JObject
import org.json4s.JsonDSL._
import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by loicmdivad.
  */
object CharactersGrid {

  sealed class Characters(val id: String) {

    def toJson: JObject = ("id" -> id ) ~ ( "name" -> toString )
  }

  case object Birdie extends Characters("SF1")
  case object Cammy extends Characters("SF2")
  case object `Chun-Li` extends Characters("SF3")
  case object Dhalsim extends Characters("SF4")
  case object `F.A.N.G` extends Characters("SF5")
  case object Karin extends Characters("SF6")
  case object Ken extends Characters("SF7")
  case object Laura extends Characters("SF8")
  case object MrBison extends Characters("SF9")
  case object Nash extends Characters("SF10")
  case object Necalli extends Characters("SF11")
  case object Mika extends Characters("SF12")
  case object Rashid extends Characters("SF13")
  case object Ryu extends Characters("SF14")
  case object Vega extends Characters("SF15")
  case object Zangief extends Characters("SF16")
  case object Alex extends Characters("SF17")
  case object Balrog extends Characters("SF18")
  case object Guile extends Characters("SF19")
  case object Ibuki extends Characters("SF20")
  case object Juri extends Characters("SF21")
  case object Urien extends Characters("SF22")
  case object Abigail extends Characters("SF23")
  case object Akuma extends Characters("SF24")
  case object Ed extends Characters("SF25")
  case object Kolin extends Characters("SF26")
  case object Menat extends Characters("SF27")
  case object Zeku extends Characters("SF28")
  case object Blanka extends Characters("SF29")
  case object Cody extends Characters("SF30")
  case object Falke extends Characters("SF31")
  case object G extends Characters("SF32")
  case object Sagat extends Characters("SF33")
  case object Sakura extends Characters("SF34")

  case object `?` extends Characters("")

  implicit val arbitraryCharacter: Arbitrary[Characters] = Arbitrary(
    Gen.frequency(
      (20, Ryu),
      (18, `Chun-Li`),
      (16, Akuma),
      (12, Sakura),
      (12, Ken),
      (10, Guile),
      (7, MrBison),
      (6, Dhalsim),
      (6, Cammy),
      (5, Birdie),
      (4, Zangief)
    )
  )

  val allCharacters = Vector(
    Birdie,
    Cammy,
    `Chun-Li`,
    Dhalsim,
    `F.A.N.G`,
    Karin,
    Ken,
    Laura,
    MrBison,
    Nash,
    Necalli,
    Mika,
    Rashid,
    Ryu,
    Vega,
    Zangief,
    Alex,
    Balrog,
    Guile,
    Ibuki,
    Juri,
    Urien,
    Abigail,
    Akuma,
    Ed,
    Kolin,
    Menat,
    Zeku,
    Blanka,
    Cody,
    Falke,
    G,
    Sagat,
    Sakura
  )


}
