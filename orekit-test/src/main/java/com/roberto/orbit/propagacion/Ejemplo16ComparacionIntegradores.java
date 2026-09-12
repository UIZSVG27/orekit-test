package com.roberto.orbit.propagacion;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.orekit.forces.gravity.NewtonianAttraction;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.OrbitType;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinates;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo16ComparacionIntegradores {

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
	     
	     double minStep = 0.1;
	     double maxStep = 10.0;
	     double positionTolerance = 0.1;

	     double[][] tolerancias =
	             NumericalPropagator.tolerances(
	                     positionTolerance,
	                     orbitaInicial,
	                     OrbitType.CARTESIAN
	             );
	     
	     DormandPrince853Integrator integradorDP =
	    	        new DormandPrince853Integrator(
	    	                minStep,
	    	                maxStep,
	    	                tolerancias[0],
	    	                tolerancias[1]
	    	        );
	     
	       NumericalPropagator propagadorDP =
	    	        new NumericalPropagator(integradorDP);

	    	propagadorDP.setOrbitType(OrbitType.CARTESIAN);

	    	SpacecraftState estadoInicialDP =
	    	        new SpacecraftState(orbitaInicial);

	    	propagadorDP.setInitialState(estadoInicialDP);

	    	NewtonianAttraction gravedadCentralDP =
	    	        new NewtonianAttraction(mu);

	    	propagadorDP.addForceModel(gravedadCentralDP);
	    	
	    	// ----------------------------------------------------
	    	// Propagación durante 7 días
	    	// ----------------------------------------------------

	    	double tiempoPropagacion = 7.0 * 86400.0;

	    	AbsoluteDate fechaFinal =
	    	        fechaInicial.shiftedBy(tiempoPropagacion);

	    	SpacecraftState estadoFinalDP =
	    	        propagadorDP.propagate(fechaFinal);

	    	KeplerianOrbit orbitaFinalDP =
	    	        new KeplerianOrbit(estadoFinalDP.getOrbit());
	   /*********************************************************************************/
	    	
	    	// ----------------------------------------------------
	    	// CASO 2: Runge-Kutta clásico de orden 4
	    	// ----------------------------------------------------

	    	double pasoRK4 = 10.0;   // segundos

	    	ClassicalRungeKuttaIntegrator integradorRK4 =
	    	        new ClassicalRungeKuttaIntegrator(pasoRK4); 
	    	
	    	NumericalPropagator propagadorRK4 =
	    	        new NumericalPropagator(integradorRK4);

	    	propagadorRK4.setOrbitType(OrbitType.CARTESIAN);

	    	SpacecraftState estadoInicialRK4 =
	    	        new SpacecraftState(orbitaInicial);

	    	propagadorRK4.setInitialState(estadoInicialRK4);
	    	
	    	NewtonianAttraction gravedadCentralDPRK4 =
	    	        new NewtonianAttraction(mu);

	    	propagadorRK4.addForceModel(gravedadCentralDPRK4);
	    	
	    	// ----------------------------------------------------
	    	// Propagación durante 7 días
	    	// ----------------------------------------------------

	    	double tiempoPropagacionRK4 = 7.0 * 86400.0;

	    	AbsoluteDate fechaFinalRK4 =
	    	        fechaInicial.shiftedBy(tiempoPropagacionRK4);

	    	SpacecraftState estadoFinalDPRK4 =
	    	        propagadorRK4.propagate(fechaFinalRK4);

	    	KeplerianOrbit orbitaFinalDPRK4 =
	    	        new KeplerianOrbit(estadoFinalDPRK4.getOrbit());
	    	
	    	/**************************************************************/
	    	/*Orbita Kepler*/
	        KeplerianOrbit orbitaKepler = new KeplerianOrbit(
	                a,
	                e,
	                i,
	                argumentoPerigeo,
	                raan,
	                anomalia,
	                PositionAngleType.TRUE,//PositionAngleType.TRUE,
	                eme2000,
	                fechaInicial,
	                mu
	            );
	            ///////////////////////////
	            double dt = 7.0 * 86400.0;

	            AbsoluteDate fechaFutura =
	            	    fechaInicial.shiftedBy(dt);
	            
	            KeplerianPropagator propagador =
	            	    new KeplerianPropagator(orbitaKepler);
	            SpacecraftState estadoFinalKepler =
	            	    propagador.propagate(fechaFutura);

	    	/**************************************************************/
	    	
	    	System.out.println();
	    	System.out.println("COMPARACION DE INTEGRADORES DESPUES DE 7 DIAS");
	    	System.out.println("---------------------------------------------");

	    	System.out.println();
	    	System.out.println("DORMAND-PRINCE 853");
	    	System.out.println("------------------");

	    	System.out.println(
	    	        "a    = " + orbitaFinalDP.getA() / 1000.0 + " km"
	    	);

	    	System.out.println(
	    	        "RAAN = "
	    	        + Math.toDegrees(
	    	                orbitaFinalDP.getRightAscensionOfAscendingNode()
	    	        )
	    	        + " grados"
	    	);

	    	System.out.println(
	    	        "w    = "
	    	        + Math.toDegrees(
	    	                orbitaFinalDP.getPerigeeArgument()
	    	        )
	    	        + " grados"
	    	);

	    	System.out.println();
	    	System.out.println("RUNGE-KUTTA 4 - paso = " + pasoRK4 + " s");
	    	System.out.println("----------------------------");

	    	System.out.println(
	    	        "a    = " + orbitaFinalDPRK4.getA() / 1000.0 + " km"
	    	);

	    	System.out.println(
	    	        "RAAN = "
	    	        + Math.toDegrees(
	    	                orbitaFinalDPRK4.getRightAscensionOfAscendingNode()
	    	        )
	    	        + " grados"
	    	);

	    	System.out.println(
	    	        "w    = "
	    	        + Math.toDegrees(
	    	                orbitaFinalDPRK4.getPerigeeArgument()
	    	        )
	    	        + " grados"
	    	);
	    	
	    	PVCoordinates pvDP =
	    	        estadoFinalDP.getPVCoordinates();

	    	PVCoordinates pvRK4 =
	    	        estadoFinalDPRK4.getPVCoordinates();
	    	
	    	PVCoordinates pvKepler =
	    			estadoFinalKepler.getPVCoordinates();

	    	Vector3D rDP =
	    	        pvDP.getPosition();

	    	Vector3D vDP =
	    	        pvDP.getVelocity();

	    	Vector3D rRK4 =
	    	        pvRK4.getPosition();

	    	Vector3D vRK4 =
	    	        pvRK4.getVelocity();
	    	
	    	Vector3D rKepler =
	    			pvKepler.getPosition();

	    	Vector3D vKepler =
	    			pvKepler.getVelocity();

	    	
	    	double errorPosicion =
	    	        Vector3D.distance(rDP, rRK4);

	    	double errorVelocidad =
	    	        Vector3D.distance(vDP, vRK4);
	    	
	    	// Error Dormand-Prince respecto a Kepler

	    	double errorPosicionDPKepler =
	    	        Vector3D.distance(rDP, rKepler);

	    	double errorVelocidadDPKepler =
	    	        Vector3D.distance(vDP, vKepler);


	    	// Error RK4 respecto a Kepler

	    	double errorPosicionRK4Kepler =
	    	        Vector3D.distance(rRK4, rKepler);

	    	double errorVelocidadRK4Kepler =
	    	        Vector3D.distance(vRK4, vKepler);
	    	
	    	System.out.println();
	    	System.out.println("DIFERENCIA ENTRE INTEGRADORES");
	    	System.out.println("-----------------------------");

	    	System.out.println(
	    	        "Error posicion  = "
	    	        + errorPosicion
	    	        + " m"
	    	);

	    	System.out.println(
	    	        "Error velocidad = "
	    	        + errorVelocidad
	    	        + " m/s"
	    	);
	    	
	    	System.out.println();
	    	System.out.println("ERROR RESPECTO A KEPLER");
	    	System.out.println("-----------------------");

	    	System.out.println();
	    	System.out.println("DORMAND-PRINCE 853");
	    	System.out.println(
	    	        "Error posicion  = "
	    	        + errorPosicionDPKepler
	    	        + " m"
	    	);
	    	System.out.println(
	    	        "Error velocidad = "
	    	        + errorVelocidadDPKepler
	    	        + " m/s"
	    	);

	    	System.out.println();
	    	System.out.println("RUNGE-KUTTA 4 - paso = " + pasoRK4 + " s");
	    	System.out.println(
	    	        "Error posicion  = "
	    	        + errorPosicionRK4Kepler
	    	        + " m"
	    	);
	    	System.out.println(
	    	        "Error velocidad = "
	    	        + errorVelocidadRK4Kepler
	    	        + " m/s"
	    	);

	}

}
