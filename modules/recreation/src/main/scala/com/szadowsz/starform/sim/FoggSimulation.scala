package com.szadowsz.starform.sim

import com.szadowsz.starform.model.{StarformProfile, StarformSimulation}
import com.szadowsz.starform.model.accrete.AccreteStats
import com.szadowsz.starform.model.star.constants.FoggBaseStarConst
import com.szadowsz.starform.system.StarformSystem
import com.szadowsz.starform.system.bodies.base.Planetismal
import com.szadowsz.starform.system.bodies.planetoid.FoggPlanet
import com.szadowsz.starform.system.bodies.star.FoggStar
import com.szadowsz.starform.unit.UnitConverter

case class FoggSimulation[C <: FoggBaseStarConst](profile : StarformProfile[FoggStar,C])
  extends StarformSimulation[FoggStar, FoggPlanet, C,AccreteStats, StarformSystem[FoggStar, FoggPlanet]](profile) {

  /**
    * Function to initialise a new instance at the beginning of each run.
    *
    * @return a new [[AccreteStats]] instance.
    */
  override protected def initStats(): AccreteStats = AccreteStats()

  /**
    * Function to initialise a new solar system instance at the end of each run.
    *
    * @return a new [[StarformSystem]] instance.
    */
  override protected def createSystem(seed: Long, stats: AccreteStats, planets: List[FoggPlanet]): StarformSystem[FoggStar, FoggPlanet] = {
    StarformSystem(seed, stats, star, planets)
  }

  override protected def buildEcosphere(proto: Planetismal): FoggPlanet  = {
    val orbitZone: Int = eCalc.orbitalZone(star.luminosity, proto.axis)

    val (equatorialRadius, density) = eCalc.radiusAndDensity(proto.mass, proto.axis, star.meanHabitableRadius, proto.isGasGiant, orbitZone)

    val angularVelocity = eCalc.totalAngularVelocity(star.mass, star.age, proto.mass, equatorialRadius, proto.axis, density, proto.isGasGiant)
    val lengthOfOrbit = eCalc.orbitLength(proto.axis, proto.mass, star.mass)
    val (synch, resonant, lengthOfDay) = eCalc.dayLength(angularVelocity, lengthOfOrbit, proto.ecc)

    val gravity = eCalc.surfaceGravity(proto.mass, equatorialRadius)
    val escapeVel = eCalc.escapeVelocity(UnitConverter.mToKm(gravity), equatorialRadius)
    val rmsSpeed = eCalc.speedRMS(eCalc.MOLECULAR_NITROGEN, equatorialRadius)

    val suffersFromGE = eCalc.suffersGreenhouseEffect(proto.axis, star.innerHabitableRadius)
    val retainsGas = eCalc.hasGasRetention(escapeVel, rmsSpeed)
    val volatileGasInventory = eCalc.vGasInventory(rand, proto.mass, orbitZone, retainsGas, suffersFromGE, star.mass)

    val surfPressure = eCalc.surfacePressure(volatileGasInventory, equatorialRadius, gravity)
    val atmos = eCalc.atmosphere(orbitZone, surfPressure, suffersFromGE, proto.isGasGiant)
    val mw = eCalc.molecule_limit(proto.mass,escapeVel,equatorialRadius)
    val (water, clouds, ice, albedo, surfTemp) = eCalc.calcClimate(star.meanHabitableRadius,proto.axis, equatorialRadius, surfPressure, volatileGasInventory, mw, atmos)

    new FoggPlanet(proto, equatorialRadius, density, lengthOfOrbit, lengthOfDay, gravity, surfPressure, water, clouds, ice, albedo, surfTemp)
  }
}