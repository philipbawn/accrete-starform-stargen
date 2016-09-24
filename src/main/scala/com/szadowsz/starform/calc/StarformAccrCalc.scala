package com.szadowsz.starform.calc


import com.szadowsz.accrete.calc.{AccreteCalc, PlanetesimalCalc}
import com.szadowsz.starform.constants.StarformConstants
import com.szadowsz.starform.util.StarUtil

import scala.util.Random

/**
  * @author Zakski : 21/07/2015.
  */
trait StarformAccrCalc extends AccreteCalc {
  this: StarformPlanCalc with StarUtil with StarformConstants =>

  /**
    * Method to calculate dust cloud density at a given radius. Original Formula taken from "III. Experimental Simulation section a) Initial Conditions in the
    * Cloud" in "Formation of Planetary Systems by Aggregation: A Computer Simulation". Density is now altered by the mass of the Central Star.
    *
    * @see p. 14, Formation of Planetary Systems by Aggregation: A Computer Simulation - Stephen H. Dole
    * @see eq. 8, p. 503, Extra-solar Planetary Systems: A Microcomputer Simulation - Martyn J. Fogg
    * @see method DustDensity, line 96 in DoleParams.java - Ian Burrell (accrete)
    * @see method distribute_planetary_masses, line 417 in accrete.c - Mat Burdick (accrete)
    * @see method Density, line 122 in Dole.c - Andrew Folkins (accretion)
    * @see method Density, line 132 in dole.c - Keris (accretion v1)
    * @see method Density, line 217 in dole.cc - Keris (accretion v2)
    * @see method dist_planetary_masses, line 449 in accrete.c - Keris (starform)
    * @see method dist_planetary_masses, line 419 in accrete.c - Mat Burdick (starform)
    * @see method dist_planetary_masses, line 154 in  Star.java - Carl Burke (starform)
    *
    * @param radius - the current distance from the stellar mass in AU
    * @return Dust density at that radius of the cloud
    */
  override def dustDensity(radius: Double): Double = {
   (DUST_DENSITY_COEFF * Math.sqrt(getStarMass)) * Math.exp(-ALPHA * Math.pow(radius, 1.0 / N)) // TODO check if changes to A are correct
  }
}