package com.roberto.orbit.propagacion;

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

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo15ComparacionPerturbaciones {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
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

	        Frame itrf = FramesFactory.getITRF(
	                IERSConventions.IERS_2010,
	                true
	        );

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
	  // 6. Caso 1: solamente gravedad central
	  // ----------------------------------------------------

	  double minStep = 0.1;
	  double maxStep = 300.0;
	  double positionTolerance = 10.0;

	  double[][] tolerancias =
	          NumericalPropagator.tolerances(
	                  positionTolerance,
	                  orbitaInicial,
	                  OrbitType.CARTESIAN
	          );

	  DormandPrince853Integrator integrador =
	          new DormandPrince853Integrator(
	                  minStep,
	                  maxStep,
	                  tolerancias[0],
	                  tolerancias[1]
	          );
	  NumericalPropagator propagadorCentral =
		        new NumericalPropagator(integrador);

		propagadorCentral.setOrbitType(OrbitType.CARTESIAN);

		SpacecraftState estadoInicial =
		        new SpacecraftState(orbitaInicial);

		propagadorCentral.setInitialState(estadoInicial);

		NewtonianAttraction gravedadCentral =
		        new NewtonianAttraction(mu);

		propagadorCentral.addForceModel(gravedadCentral);
		
		// ----------------------------------------------------
		// 6.2 Propagación del Caso 1
		// ----------------------------------------------------

		double tiempoPropagacion = 7.0 * 86400.0;   // 7 días

		AbsoluteDate fechaFinal =
		        fechaInicial.shiftedBy(tiempoPropagacion);

		SpacecraftState estadoFinalCentral =
		        propagadorCentral.propagate(fechaFinal);
		
	

		KeplerianOrbit orbitaFinalCentral =
		        new KeplerianOrbit(estadoFinalCentral.getOrbit());
		
		// ----------------------------------------------------
		// 7. Caso 2: gravedad central + J2
		// ----------------------------------------------------

		DormandPrince853Integrator integradorJ2 =
		        new DormandPrince853Integrator(
		                minStep,
		                maxStep,
		                tolerancias[0],
		                tolerancias[1]
		        );

		NumericalPropagator propagadorJ2 =
		        new NumericalPropagator(integradorJ2);

		propagadorJ2.setOrbitType(OrbitType.CARTESIAN);

		propagadorJ2.setInitialState(
		        new SpacecraftState(orbitaInicial)
		);
		
		// ----------------------------------------------------
		// 7.1 Modelo gravitatorio con J2
		// ----------------------------------------------------

		NormalizedSphericalHarmonicsProvider gravityProvider =
		        GravityFieldFactory.getNormalizedProvider(2, 0);

		HolmesFeatherstoneAttractionModel gravedadJ2 =
		        new HolmesFeatherstoneAttractionModel(
		                itrf,
		                gravityProvider
		        );

		propagadorJ2.addForceModel(gravedadJ2);
		
		SpacecraftState estadoFinalJ2 =
		        propagadorJ2.propagate(fechaFinal);

		KeplerianOrbit orbitaFinalJ2 =
		        new KeplerianOrbit(
		                estadoFinalJ2.getOrbit()
		        );
		
		// ----------------------------------------------------
		// 8. Caso 3: gravedad central + J2 + Sol
		// ----------------------------------------------------

		DormandPrince853Integrator integradorSol =
		        new DormandPrince853Integrator(
		                minStep,
		                maxStep,
		                tolerancias[0],
		                tolerancias[1]
		        );

		NumericalPropagator propagadorSol =
		        new NumericalPropagator(integradorSol);

		propagadorSol.setOrbitType(OrbitType.CARTESIAN);

		propagadorSol.setInitialState(
		        new SpacecraftState(orbitaInicial)
		);
		
		NormalizedSphericalHarmonicsProvider gravityProviderSol =
		        GravityFieldFactory.getNormalizedProvider(2, 0);

		HolmesFeatherstoneAttractionModel gravedadJ2Sol =
		        new HolmesFeatherstoneAttractionModel(
		                itrf,
		                gravityProviderSol
		        );

		propagadorSol.addForceModel(gravedadJ2Sol);
		
		ThirdBodyAttraction gravedadSol =
		        new ThirdBodyAttraction(
		                CelestialBodyFactory.getSun()
		        );

		propagadorSol.addForceModel(gravedadSol);
		
		SpacecraftState estadoFinalSol =
		        propagadorSol.propagate(fechaFinal);

		KeplerianOrbit orbitaFinalSol =
		        new KeplerianOrbit(
		                estadoFinalSol.getOrbit()
		        );
		// ----------------------------------------------------
		// 9. Caso 4: gravedad central + J2 + Sol + Luna
		// ----------------------------------------------------
		DormandPrince853Integrator integradorLuna =
		        new DormandPrince853Integrator(
		                minStep,
		                maxStep,
		                tolerancias[0],
		                tolerancias[1]
		        );

		NumericalPropagator propagadorLuna =
		        new NumericalPropagator(integradorLuna);

		propagadorLuna.setOrbitType(OrbitType.CARTESIAN);

		propagadorLuna.setInitialState(
		        new SpacecraftState(orbitaInicial)
		);
		
		NormalizedSphericalHarmonicsProvider gravityProviderLuna =
		        GravityFieldFactory.getNormalizedProvider(2, 0);

		HolmesFeatherstoneAttractionModel gravedadJ2Luna =
		        new HolmesFeatherstoneAttractionModel(
		                itrf,
		                gravityProviderLuna
		        );

		propagadorLuna.addForceModel(gravedadJ2Luna);


		ThirdBodyAttraction gravedadSol4 =
		        new ThirdBodyAttraction(
		                CelestialBodyFactory.getSun()
		        );

		propagadorLuna.addForceModel(gravedadSol4);


		ThirdBodyAttraction gravedadLuna =
		        new ThirdBodyAttraction(
		                CelestialBodyFactory.getMoon()
		        );

		propagadorLuna.addForceModel(gravedadLuna);
		
		SpacecraftState estadoFinalLuna =
		        propagadorLuna.propagate(fechaFinal);

		KeplerianOrbit orbitaFinalLuna =
		        new KeplerianOrbit(
		                estadoFinalLuna.getOrbit()
		        );
		
		  // ----------------------------------------------------
		  // 10. gravedad central + J2 + Sol + Luna + Presion de Radiación
		  // ----------------------------------------------------

		  double masa = 1000.0;     // kg
		  double area = 20.0;       // m²
		  double cr   = 1.2;        // coeficiente de reflexión
		  
		DormandPrince853Integrator integradorPRD =
		        new DormandPrince853Integrator(
		                minStep,
		                maxStep,
		                tolerancias[0],
		                tolerancias[1]
		        ); /* Integrador*/

		NumericalPropagator propagadorRDS =
		        new NumericalPropagator(integradorPRD); /*Propagador*/

		propagadorRDS.setOrbitType(OrbitType.CARTESIAN);

		propagadorRDS.setInitialState(
		        new SpacecraftState(
		                orbitaInicial,
		                masa
		        )
		); /*Establecer las CI*/
		
        /* Añadir J2*/
		NormalizedSphericalHarmonicsProvider gravityProviderRDS =
		        GravityFieldFactory.getNormalizedProvider(2, 0);

		HolmesFeatherstoneAttractionModel gravedadJ2LunaRDS =
		        new HolmesFeatherstoneAttractionModel(
		                itrf,
		                gravityProviderRDS
		        );
		
		propagadorRDS.addForceModel(gravedadJ2LunaRDS);
		/*Añadir J2*/
		
        /*Añadir Sol*/
		ThirdBodyAttraction gravedadSol5 =
		        new ThirdBodyAttraction(
		                CelestialBodyFactory.getSun()
		        );

		propagadorRDS.addForceModel(gravedadSol5);
        /*Añadir Sol*/

		/*Añadir Luna*/
		ThirdBodyAttraction gravedadLuna5 =
		        new ThirdBodyAttraction(
		                CelestialBodyFactory.getMoon()
		        );

		propagadorRDS.addForceModel(gravedadLuna5);
        /*Añadir Luna*/
		 
		  /*Añadir Presión radiación*/
		  IsotropicRadiationSingleCoefficient superficie =
			        new IsotropicRadiationSingleCoefficient(
			                area,
			                cr
			        );
		
		  
			OneAxisEllipsoid tierra =
			        new OneAxisEllipsoid(
			                Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
			                Constants.WGS84_EARTH_FLATTENING,
			                itrf
			        );
			
			SolarRadiationPressure radiacionSolar =
			        new SolarRadiationPressure(
			                CelestialBodyFactory.getSun(),
			                tierra,
			                superficie
			        );

			propagadorRDS.addForceModel(radiacionSolar);
			/*Añadir Presión radiación*/
			
			/*Propagar hasta la fecha final*/
			SpacecraftState estadoFinalRDS =
			        propagadorRDS.propagate(fechaFinal);

			KeplerianOrbit orbitaFinalRDS =
			        new KeplerianOrbit(
			                estadoFinalRDS.getOrbit()
			        );
			/*Propagar hasta la fecha final*/

			  // ----------------------------------------------------
			  // 11. gravedad central + J2 + Sol + Luna + Presion de Radiación + Drag
			  // ----------------------------------------------------

			  double cd = 2.2;          // coeficiente de drag		  
		     
		             DormandPrince853Integrator integratorR11 =
			                 new DormandPrince853Integrator(
			                        minStep,
			                        maxStep,
			                        tolerancias[0],
			                        tolerancias[1]
			               );
		  
				
				
				NumericalPropagator propagadorR11 =
				       new NumericalPropagator(integratorR11);
				
				propagadorR11.setOrbitType(OrbitType.CARTESIAN);
						
				
				SpacecraftState estadoInicialR11 =
				        new SpacecraftState(orbitaInicial, masa);
				
				propagadorR11.setInitialState(estadoInicialR11);
				
		        /* Añadir J2*/
				NormalizedSphericalHarmonicsProvider gravityProviderR11 =
				        GravityFieldFactory.getNormalizedProvider(2, 0);

				HolmesFeatherstoneAttractionModel gravedadJ2R11 =
				        new HolmesFeatherstoneAttractionModel(
				                itrf,
				                gravityProviderR11
				        );
				
				propagadorR11.addForceModel(gravedadJ2R11);
				/*Añadir J2*/
				
		        /*Añadir Sol*/
				ThirdBodyAttraction gravedadSol11 =
				        new ThirdBodyAttraction(
				                CelestialBodyFactory.getSun()
				        );

				propagadorR11.addForceModel(gravedadSol11);
		        /*Añadir Sol*/

				/*Añadir Luna*/
				ThirdBodyAttraction gravedadLuna11 =
				        new ThirdBodyAttraction(
				                CelestialBodyFactory.getMoon()
				        );

				propagadorR11.addForceModel(gravedadLuna11);
		        /*Añadir Luna*/
				 
				  /*Añadir Presión radiación*/
				  IsotropicRadiationSingleCoefficient superficieR11 =
					        new IsotropicRadiationSingleCoefficient(
					                area,
					                cr
					        );
				
				  
					OneAxisEllipsoid tierraR11 =
					        new OneAxisEllipsoid(
					                Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
					                Constants.WGS84_EARTH_FLATTENING,
					                itrf
					        );
					
					SolarRadiationPressure radiacionSolar11 =
					        new SolarRadiationPressure(
					                CelestialBodyFactory.getSun(),
					                tierraR11,
					                superficieR11
					        );

					propagadorR11.addForceModel(radiacionSolar11);
					/*Añadir Presión radiación*/

		
				
					Atmosphere atmosferaR11 =
					        new SimpleExponentialAtmosphere(
					                tierraR11,
					                4.0e-13,
					                700000.0,
					                88667.0
					        );
				
				  IsotropicDrag superficieDragR11 =
					        new IsotropicDrag(
					                area,
					                cd
					        );
				
				DragForce dragR11 =
				        new DragForce(
				                atmosferaR11,
				                superficieDragR11
				        );
				
				propagadorR11.addForceModel(dragR11);  /* Resistencia del satélite en la atmósfera */
				

					
					/*Propagar hasta la fecha final*/
					SpacecraftState estadoFinalR11 =
					        propagadorR11.propagate(fechaFinal);

					KeplerianOrbit orbitaFinalR11 =
					        new KeplerianOrbit(
					                estadoFinalR11.getOrbit()
					        );
					/*Propagar hasta la fecha final*/
		/***********************************************************************/
			
		
		
		
		System.out.println();
		System.out.println("COMPARACION DESPUES DE 7 DIAS");
		System.out.println("-----------------------------");

		System.out.println();
		System.out.println("CASO 1: GRAVEDAD CENTRAL");
		System.out.println("------------------------");

		System.out.println(
		        "a    = " + orbitaFinalCentral.getA() / 1000.0 + " km"
		);

		System.out.println(
		        "RAAN = "
		        + Math.toDegrees(
		                orbitaFinalCentral.getRightAscensionOfAscendingNode()
		        )
		        + " grados"
		);

		System.out.println(
		        "w    = "
		        + Math.toDegrees(
		                orbitaFinalCentral.getPerigeeArgument()
		        )
		        + " grados"
		);
		
      /*****************************************************/
		System.out.println();
		System.out.println("CASO 2: GRAVEDAD CENTRAL + J2");
		System.out.println("-----------------------------");

		System.out.println(
		        "a    = " + orbitaFinalJ2.getA() / 1000.0 + " km"
		);

		System.out.println(
		        "RAAN = "
		        + Math.toDegrees(
		                orbitaFinalJ2.getRightAscensionOfAscendingNode()
		        )
		        + " grados"
		);

		System.out.println(
		        "w    = "
		        + Math.toDegrees(
		                orbitaFinalJ2.getPerigeeArgument()
		        )
		        + " grados"
		);
		/***********************************/
		System.out.println();
		System.out.println("CASO 3: GRAVEDAD CENTRAL + J2 + SOL");
		System.out.println("----------------------------------");

		System.out.println(
		        "a    = " + orbitaFinalSol.getA() / 1000.0 + " km"
		);

		System.out.println(
		        "RAAN = "
		        + Math.toDegrees(
		                orbitaFinalSol.getRightAscensionOfAscendingNode()
		        )
		        + " grados"
		);

		System.out.println(
		        "w    = "
		        + Math.toDegrees(
		                orbitaFinalSol.getPerigeeArgument()
		        )
		        + " grados"
		);
		/*****************************************/
		System.out.println();
		System.out.println("CASO 4: GRAVEDAD CENTRAL + J2 + SOL + LUNA");
		System.out.println("-----------------------------------------");

		System.out.println(
		        "a    = " + orbitaFinalLuna.getA() / 1000.0 + " km"
		);

		System.out.println(
		        "RAAN = "
		        + Math.toDegrees(
		                orbitaFinalLuna.getRightAscensionOfAscendingNode()
		        )
		        + " grados"
		);

		System.out.println(
		        "w    = "
		        + Math.toDegrees(
		                orbitaFinalLuna.getPerigeeArgument()
		        )
		        + " grados"
		);
		/*******************************************************/
		System.out.println();
		System.out.println(
		        "CASO 5: GRAVEDAD CENTRAL + J2 + SOL + LUNA + RADIACION SOLAR"
		);
		System.out.println(
		        "----------------------------------------------------------"
		);

		System.out.println(
		        "a    = "
		        + orbitaFinalRDS.getA() / 1000.0
		        + " km"
		);

		System.out.println(
		        "RAAN = "
		        + Math.toDegrees(
		                orbitaFinalRDS.getRightAscensionOfAscendingNode()
		        )
		        + " grados"
		);

		System.out.println(
		        "w    = "
		        + Math.toDegrees(
		                orbitaFinalRDS.getPerigeeArgument()
		        )
		        + " grados"
				);
		/***************************************************/
		System.out.println();
		System.out.println(
		        "CASO 6: GRAVEDAD CENTRAL + J2 + SOL + LUNA + RADIACION SOLAR + DRAG"
		);
		System.out.println(
		        "-----------------------------------------------------------------"
		);

		System.out.println(
		        "a    = "
		        + orbitaFinalR11.getA() / 1000.0
		        + " km"
		);

		System.out.println(
		        "RAAN = "
		        + Math.toDegrees(
		                orbitaFinalR11.getRightAscensionOfAscendingNode()
		        )
		        + " grados"
		);

		System.out.println(
		        "w    = "
		        + Math.toDegrees(
		                orbitaFinalR11.getPerigeeArgument()
		        )
		        + " grados"
		);
	}
	
	
	

}
