package com.roberto.orbit.maniobras;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.ode.events.Action;
import org.orekit.forces.maneuvers.ImpulseManeuver;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.events.ApsideDetector;
import org.orekit.propagation.events.DateDetector;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.PVCoordinates;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo21ManiobraImpulsiva {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		OrekitConfig.inicializar();

		// ----------------------------------------------------
		// 1. Fecha inicial
		// ----------------------------------------------------

		TimeScale utc = TimeScalesFactory.getUTC();

		AbsoluteDate fechaInicial = new AbsoluteDate(
		        2024, 1, 1,
		        12, 0, 0.0,
		        utc
		);

		// ----------------------------------------------------
		// 2. Sistema de referencia
		// ----------------------------------------------------

		Frame eme2000 = FramesFactory.getEME2000();

		// ----------------------------------------------------
		// 3. Órbita inicial
		// ----------------------------------------------------

		double a = 10_000_000.0;   // m
		double e = 0.3;
		double i = Math.toRadians(51.6);

		double argumentoPerigeo = Math.toRadians(30.0);
		double raan = Math.toRadians(0.0);
		double TA = Math.toRadians(0.0);

		double mu = Constants.WGS84_EARTH_MU;

		KeplerianOrbit orbitaInicial =
		        new KeplerianOrbit(
		                a,
		                e,
		                i,
		                argumentoPerigeo,
		                raan,
		                TA,
		                PositionAngleType.TRUE,
		                eme2000,
		                fechaInicial,
		                mu);
	

	PVCoordinates pvInicial =
			orbitaInicial.getPVCoordinates();

	Vector3D velocidadInicial =
	        pvInicial.getVelocity();

	Vector3D direccionVelocidad =
	        velocidadInicial.normalize();

	double deltaV = 100.0;   // m/s

	Vector3D vectorDeltaV =
	        direccionVelocidad.scalarMultiply(deltaV);
	
	// ----------------------------------------------------
	// 4. Propagador kepleriano
	// ----------------------------------------------------

	KeplerianPropagator propagador =
	        new KeplerianPropagator(orbitaInicial);
	
	// ----------------------------------------------------
	// 5. Instante de la maniobra
	// ----------------------------------------------------

	AbsoluteDate fechaManiobra =
	        fechaInicial.shiftedBy(1.0); /* Para empezar un segundo después con el delta-v y no coger la maniobra inicial*/

	DateDetector detectorFecha =
	        new DateDetector(fechaManiobra);
	
	// ----------------------------------------------------
	// 6. Maniobra impulsiva
	// ----------------------------------------------------

	ImpulseManeuver maniobra =
	        new ImpulseManeuver(
	                detectorFecha,
	                vectorDeltaV,
	                300.0
	        );
	
	propagador.addEventDetector(maniobra);
	
	// ----------------------------------------------------
	// 7. Propagación después de la maniobra
	// ----------------------------------------------------

	AbsoluteDate fechaFinal =
	        fechaInicial.shiftedBy(3600.0);

	SpacecraftState estadoFinal =
	        propagador.propagate(fechaFinal);

	KeplerianOrbit orbitaFinal =
	        new KeplerianOrbit(estadoFinal.getOrbit());
	
	double perigeoInicial =
	        orbitaInicial.getA() * (1.0 - orbitaInicial.getE());

	double apogeoInicial =
	        orbitaInicial.getA() * (1.0 + orbitaInicial.getE());

	double perigeoFinal =
			orbitaFinal.getA() * (1.0 - orbitaFinal.getE());

	double apogeoFinal =
			orbitaFinal.getA() * (1.0 + orbitaFinal.getE());
	
	System.out.println();
	System.out.println("ORBITA INICIAL");
	System.out.println("a = " + orbitaInicial.getA() / 1000.0 + " km");
	System.out.println("e = " + orbitaInicial.getE());
	System.out.println("Perigeo = "
	        + perigeoInicial / 1000.0 + " km");

	System.out.println("Apogeo  = "
	        + apogeoInicial / 1000.0 + " km");
		

	System.out.println();
	System.out.println("ORBITA DESPUES DEL DELTA-V");
	System.out.println("a = " + orbitaFinal.getA() / 1000.0 + " km");
	System.out.println("e = " + orbitaFinal.getE());
	
	System.out.println("Perigeo final = "
	        + perigeoFinal / 1000.0 + " km");

	System.out.println("Apogeo final  = "
	        + apogeoFinal / 1000.0 + " km");
		
	
	// ----------------------------------------------------
	// 8. Detector del siguiente ápside
	// ----------------------------------------------------
	KeplerianPropagator propagadorApogeo =
	        new KeplerianPropagator(orbitaFinal);
	
	ApsideDetector detectorApsides =
	        new ApsideDetector(orbitaFinal)
	            .withHandler((state, detector, increasing) -> {

	                System.out.println(
	                        "APSIDE DETECTADO - "
	                        + state.getDate()
	                );

	                return Action.STOP;
	            });

	propagadorApogeo.addEventDetector(detectorApsides);
	
	double periodoFinal =
	        orbitaFinal.getKeplerianPeriod();
	
	AbsoluteDate fechaBusqueda =
	        orbitaFinal.getDate().shiftedBy(periodoFinal);

	SpacecraftState estadoApogeo =
	        propagadorApogeo.propagate(fechaBusqueda);;
    
    /****************************************/
    KeplerianOrbit orbitaApogeo =
            new KeplerianOrbit(estadoApogeo.getOrbit());

    System.out.println();
    System.out.println("ESTADO EN EL APOGEO");
    System.out.println("Fecha = " + estadoApogeo.getDate());
    System.out.println("TA = "
            + Math.toDegrees(orbitaApogeo.getTrueAnomaly())
            + " grados");	 
    /****************************************/
    PVCoordinates pvApogeo =
            estadoApogeo.getPVCoordinates();
    Vector3D velocidadApogeo =
            pvApogeo.getVelocity();
    double moduloVelocidadApogeo =
            velocidadApogeo.getNorm();
    
    System.out.println("Velocidad en apogeo = "
            + moduloVelocidadApogeo
            + " m/s");
    
    /****************************************************/
    /*Ahora inpulso en B*/
    
    Vector3D direccionVelocidadApogeo =
            velocidadApogeo.normalize();
    
    double deltaV2 = 972.77; //100.0;   // m/s
    
    Vector3D vectorDeltaV2 =
            direccionVelocidadApogeo.scalarMultiply(deltaV2);
    
    AbsoluteDate fechaManiobra2 =
            estadoApogeo.getDate().shiftedBy(1.0);
    
    DateDetector detectorFecha2 =
            new DateDetector(fechaManiobra2);
    
    ImpulseManeuver maniobra2 =
            new ImpulseManeuver(
                    detectorFecha2,
                    vectorDeltaV2,
                    300.0
            );
    
    KeplerianPropagator propagador2 =
            new KeplerianPropagator(orbitaApogeo);

    propagador2.addEventDetector(maniobra2);
    
    AbsoluteDate fechaFinal2 =
            estadoApogeo.getDate().shiftedBy(3600.0);

    SpacecraftState estadoFinal2 =
            propagador2.propagate(fechaFinal2);

    KeplerianOrbit orbitaFinal2 =
            new KeplerianOrbit(estadoFinal2.getOrbit());

	double perigeoFinal2 =
			orbitaFinal2.getA() * (1.0 - orbitaFinal2.getE());

	double apogeoFinal2 =
			orbitaFinal2.getA() * (1.0 + orbitaFinal2.getE());
	
	System.out.println();
	System.out.println("ORBITA DESPUES DEL SEGUNDO DELTA-V");
	System.out.println("a = " + orbitaFinal2.getA() / 1000.0 + " km");
	System.out.println("e = " + orbitaFinal2.getE());

	System.out.println("Perigeo final 2 = "
	        + perigeoFinal2 / 1000.0 + " km");

	System.out.println("Apogeo final 2 = "
	        + apogeoFinal2 / 1000.0 + " km");
    
	    }
	}
