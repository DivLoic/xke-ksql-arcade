package fr.xebia.ldi.ksql.datagen

import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by loicmdivad.
  */
object CharactersGrid {

  sealed trait Characters

  case object Birdie extends Characters
  case object Cammy extends Characters
  case object `Chun-Li` extends Characters
  case object Dhalsim extends Characters
  case object `F.A.N.G` extends Characters
  case object Karin extends Characters
  case object Ken extends Characters
  case object Laura extends Characters
  case object MrBison extends Characters
  case object Nash extends Characters
  case object Necalli extends Characters
  case object Mika extends Characters
  case object Rashid extends Characters
  case object Ryu extends Characters
  case object Vega extends Characters
  case object Zangief extends Characters
  case object Alex extends Characters
  case object Balrog extends Characters
  case object Guile extends Characters
  case object Ibuki extends Characters
  case object Juri extends Characters
  case object Urien extends Characters
  case object Abigail extends Characters
  case object Akuma extends Characters
  case object Ed extends Characters
  case object Kolin extends Characters
  case object Menat extends Characters
  case object Zeku extends Characters
  case object Blanka extends Characters
  case object Cody extends Characters
  case object Falke extends Characters
  case object G extends Characters
  case object Sagat extends Characters
  case object Sakura extends Characters

  case object `?` extends Characters

  val unknownCharacter: Arbitrary[Characters] = Arbitrary(Gen.const(`?`))

  implicit val arbitraryCharacter: Arbitrary[Characters] = Arbitrary(
    Gen.frequency(
      (12, Ryu),
      (8, `Chun-Li`),
      (8, Akuma),
      (7, Sakura),
      (7, Ken),
      (7, Guile),
      (7, MrBison),
      (6, Dhalsim),
      (6, Cammy),
      (5, Birdie),
      (4 , Zangief)
    )
  )

}
