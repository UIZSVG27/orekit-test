package com.roberto.orbit.propagacion;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.forces.drag.DragForce;
import org.orekit.forces.drag.IsotropicDrag;
import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.orekit.forces.gravity.NewtonianAttraction;
import org.orekit.forces.gravity.ThirdBodyAttraction;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.orekit.forces.radiation.IsotropicRadiationSingleCoefficient;
import org.orekit.forces.radiation.SolarRadiationPressure;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.models.earth.atmosphere.Atmosphere;
import org.orekit.models.earth.atmosphere.SimpleExponentialAtmosphere;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.OrbitType;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinates;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo14NumericalPropagator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// TODO Auto-generated method stub

        // ----------------------------------------------------
        // 1. Inicialización de Orekit
        // ----------------------------------------------------

        OrekitConfig.inicializar();

        // ----------------------------------------------------
        // 2. Fecha inicial
        // ----------------------------------------------------

        TimeScale utc = TimeScalesFactory.getUTC();

        AbsoluteDate fechaInicial = new AbsoluteDate(
            2024, 1, 1,
            12, 0, 0.0,
            utc
        );

        // ----------------------------------------------------
        // 3. Sistemas de referencia
        // ----------------------------------------------------

        Frame eme2000 = FramesFactory.getEME2000();

 

     // ----------------------------------------------------
     // 4. Elementos orbitales
     // ----------------------------------------------------

     double a = 10_000_000.0;
     double e = 0.3;
     double i = Math.toRadians(51.6);

     double argumentoPerigeo = Math.toRadians(0.0);
     double raan = Math.toRadians(0.0);
     double anomalia = Math.toRadians(0.0);

     double mu = Constants.WGS84_EARTH_MU;
        


     // ----------------------------------------------------
     // 5. Órbita inicial
     // ----------------------------------------------------

     KeplerianOrbit orbitaInicial = new KeplerianOrbit(
             a,
             e,
             i,
             argumentoPerigeo,
             raan,
             anomalia,
             PositionAngleType.TRUE,
             eme2000,
             fechaInicial,
             mu
     );
     
  // ----------------------------------------------------
  // 5.1 Características físicas del satélite
  // ----------------------------------------------------

  double masa = 1000.0;     // kg
  double area = 20.0;       // m²
  double cr   = 1.2;        // coeficiente de reflexión
  double cd = 2.2;          // coeficiente de drag
  
  IsotropicRadiationSingleCoefficient superficie =
	        new IsotropicRadiationSingleCoefficient(
	                area,
	                cr
	        );
  IsotropicDrag superficieDrag =
	        new IsotropicDrag(
	                area,
	                cd
	        );
  
     
  // ----------------------------------------------------
  // 6. Configuración del integrador numérico
  // ----------------------------------------------------

  double minStep = 0.001;
  double maxStep = 300.0;
  double positionTolerance = 10;

  double[][] tolerances =
          NumericalPropagator.tolerances(
                  positionTolerance,
                  orbitaInicial,
                  OrbitType.CARTESIAN
          );
     
  DormandPrince853Integrator integrator =
	        new DormandPrince853Integrator(
	                minStep,
	                maxStep,
	                tolerances[0],
	                tolerances[1]
	        );
  
		//----------------------------------------------------
		//7. Crear el propagador numérico
		//----------------------------------------------------
		
		NumericalPropagator propagador =
		       new NumericalPropagator(integrator);
		
		propagador.setOrbitType(OrbitType.CARTESIAN);
		
		/*SpacecraftState estadoInicial =
		       new SpacecraftState(orbitaInicial);*/
		
		SpacecraftState estadoInicial =
		        new SpacecraftState(orbitaInicial, masa);
		
		propagador.setInitialState(estadoInicial);
		
		NewtonianAttraction gravedadCentral =
		        new NewtonianAttraction(mu);
		

		propagador.addForceModel(gravedadCentral);
		
		AbsoluteDate fechaFinal =
		        fechaInicial.shiftedBy(3600.0);
		

		Frame itrf = FramesFactory.getITRF(
		        IERSConventions.IERS_2010,
		        true
		);
		NormalizedSphericalHarmonicsProvider gravityProvider =
		        GravityFieldFactory.getNormalizedProvider(2, 0);
		
		HolmesFeatherstoneAttractionModel gravedadJ2 =
		        new HolmesFeatherstoneAttractionModel(
		                itrf,
		                gravityProvider
		        );
		propagador.addForceModel(gravedadJ2); /*FORMA TIERRA*/
		
		ThirdBodyAttraction gravedadSol =
		        new ThirdBodyAttraction(
		                CelestialBodyFactory.getSun()
		        );

		propagador.addForceModel(gravedadSol);/*SOL*/
		
		ThirdBodyAttraction gravedadLuna =
		        new ThirdBodyAttraction(
		                CelestialBodyFactory.getMoon()
		        );

		propagador.addForceModel(gravedadLuna); /*LUNA*/
		
		OneAxisEllipsoid tierra =
		        new OneAxisEllipsoid(
		                Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
		                Constants.WGS84_EARTH_FLATTENING,
		                itrf
		        );
		
		Atmosphere atmosfera =
		        new SimpleExponentialAtmosphere(
		                tierra,
		                4.0e-13,
		                700000.0,
		                88667.0
		        );
		DragForce drag =
		        new DragForce(
		                atmosfera,
		                superficieDrag
		        );
		
		propagador.addForceModel(drag);  /* Resistencia del satélite en la atmósfera */
		
		SolarRadiationPressure radiacionSolar =
		        new SolarRadiationPressure(
		                CelestialBodyFactory.getSun(),
		                tierra,
		                superficie
		        );
		
		propagador.addForceModel(radiacionSolar); /*+ RADIACION SOLAR*/
		
		SpacecraftState estadoFinal =
		        propagador.propagate(fechaFinal);
		
		
		/*
		 * Vamos a comparar con una orbita Kepleriano básica y luego movernos 3600 sg
		 */
		PVCoordinates pvNumerico =
		        estadoFinal.getPVCoordinates();

		Vector3D rNumerico = pvNumerico.getPosition();
		Vector3D vNumerico = pvNumerico.getVelocity();

		KeplerianOrbit orbitaKepler =
		        (KeplerianOrbit) orbitaInicial.shiftedBy(3600.0);

		PVCoordinates pvKepler =
		        orbitaKepler.getPVCoordinates();

		Vector3D rKepler = pvKepler.getPosition();
		Vector3D vKepler = pvKepler.getVelocity();
		
		System.out.println();
		System.out.println("COMPARACION NUMERICO vs KEPLER");
		System.out.println("------------------------------");

		System.out.println("r numerico = " + rNumerico);
		System.out.println("r Kepler   = " + rKepler);

		System.out.println();

		System.out.println("v numerico = " + vNumerico);
		System.out.println("v Kepler   = " + vKepler);
		
		double errorPosicion =
		        Vector3D.distance(rNumerico, rKepler);

		double errorVelocidad =
		        Vector3D.distance(vNumerico, vKepler);

		System.out.println();
		System.out.println("Error posicion = " + errorPosicion + " m");
		System.out.println("Error velocidad = " + errorVelocidad + " m/s");
		
		KeplerianOrbit orbitaFinal =
		        new KeplerianOrbit(estadoFinal.getOrbit());

		System.out.println();
		System.out.println("PROPAGACION NUMERICA CON J2 + SOL + LUNA + RADIACION SOLAR + RESISTENCIA ATMÓSFERA");
		System.out.println("---------------------------");

		System.out.println(
		        "RAAN inicial = "
		        + Math.toDegrees(
		                orbitaInicial.getRightAscensionOfAscendingNode()
		        )
		        + " grados"
		);

		System.out.println(
		        "RAAN final = "
		        + Math.toDegrees(
		                orbitaFinal.getRightAscensionOfAscendingNode()
		        )
		        + " grados"
		);

		System.out.println(
		        "w inicial = "
		        + Math.toDegrees(
		                orbitaInicial.getPerigeeArgument()
		        )
		        + " grados"
		);

		System.out.println(
		        "w final = "
		        + Math.toDegrees(
		                orbitaFinal.getPerigeeArgument()
		        )
		        + " grados"
		);
		
		/* Variación a con el t*/
		System.out.println();
		System.out.println("EVOLUCION DEL SEMIEJE MAYOR");
		System.out.println("---------------------------");
		System.out.println("Dia        a (km)");

		for (int dia = 0; dia <= 7; dia++) {

		    double tiempo = dia * 86400.0;

		    AbsoluteDate fechaDia =
		            fechaInicial.shiftedBy(tiempo);

		    SpacecraftState estadoDia =
		            propagador.propagate(fechaDia);

		    KeplerianOrbit orbitaDia =
		            new KeplerianOrbit(
		                    estadoDia.getOrbit()
		            );

		    System.out.printf(
		            "%2d      %12.6f%n",
		            dia,
		            orbitaDia.getA() / 1000.0
		    );
		}
	}

}
