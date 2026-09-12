package com.roberto.orbit.basico;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.orbits.CartesianOrbit;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.utils.PVCoordinates;

import com.roberto.orbit.util.OrekitConfig;


public class Ejemplo05ConversionRepresentacionOrbital {
	

	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

        // ----------------------------------------------------
        // 1. Inicialización de los datos de Orekit
        // ----------------------------------------------------

    	OrekitConfig.inicializar();
    	
        // ----------------------------------------------------
        // 2. Fecha y sistema de referencia
        // ----------------------------------------------------

        TimeScale utc = TimeScalesFactory.getUTC();

        AbsoluteDate fechaInicial = new AbsoluteDate(
            2024, 1, 1,
            12, 0, 0.0,
            utc
        );

        Frame eme2000 = FramesFactory.getEME2000();
        
    	double a = 7_000_000.0;
    	double e = 0.001;
    	double i = Math.toRadians(51.6);

    	double argumentoPerigeo = Math.toRadians(0.0);
    	double raan = Math.toRadians(0.0);
    	double anomaliaVerdadera = Math.toRadians(0.0);

    	// Parámetro gravitatorio terrestre, m³/s²
        double mu = Constants.WGS84_EARTH_MU;
        
        // ----------------------------------------------------
        // 4. Creación de la órbita
        // ----------------------------------------------------

        KeplerianOrbit orbita = new KeplerianOrbit(
            a,
            e,
            i,
            argumentoPerigeo,
            raan,
            anomaliaVerdadera,
            PositionAngleType.TRUE,
            eme2000,
            fechaInicial,
            mu
        );
                
        CartesianOrbit orbitaCartesiana =
        	    new CartesianOrbit(orbita);
    	

        System.out.println(
        	    "Frame kepleriano: " +
        	    orbita.getFrame().getName()
        	);

        	System.out.println(
        	    "Frame cartesiano: " +
        	    orbitaCartesiana.getFrame().getName()
        	);

        	    PVCoordinates pvKepleriana =
        		    orbita.getPVCoordinates();

        		PVCoordinates pvCartesiana =
        		    orbitaCartesiana.getPVCoordinates();

        		Vector3D posicionKepleriana =
        		    pvKepleriana.getPosition();

        		Vector3D posicionCartesiana =
        		    pvCartesiana.getPosition();

        		Vector3D velocidadKepleriana =
        		    pvKepleriana.getVelocity();

        		Vector3D velocidadCartesiana =
        		    pvCartesiana.getVelocity();
        		
        		System.out.println();
        		System.out.println("REPRESENTACIONES ORBITALES");
        		System.out.println("--------------------------");

        		System.out.println(
        		    "Clase kepleriana: "
        		    + orbita.getClass().getSimpleName()
        		);

        		System.out.println(
        		    "Clase cartesiana: "
        		    + orbitaCartesiana.getClass().getSimpleName()
        		);

        		System.out.println(
        		    "Frame kepleriano: "
        		    + orbita.getFrame().getName()
        		);

        		System.out.println(
        		    "Frame cartesiano: "
        		    + orbitaCartesiana.getFrame().getName()
        		);    
        		
        		System.out.println();
        		System.out.println("POSICIÓN");
        		System.out.println("--------");

        		System.out.printf(
        		    "Kepleriana: [%12.3f, %12.3f, %12.3f] km%n",
        		    posicionKepleriana.getX() / 1000.0,
        		    posicionKepleriana.getY() / 1000.0,
        		    posicionKepleriana.getZ() / 1000.0
        		);

        		System.out.printf(
        		    "Cartesiana: [%12.3f, %12.3f, %12.3f] km%n",
        		    posicionCartesiana.getX() / 1000.0,
        		    posicionCartesiana.getY() / 1000.0,
        		    posicionCartesiana.getZ() / 1000.0
        		);
        		
        		System.out.println();
        		System.out.println("VELOCIDAD");
        		System.out.println("---------");

        		System.out.printf(
        		    "Kepleriana: [%12.6f, %12.6f, %12.6f] km/s%n",
        		    velocidadKepleriana.getX() / 1000.0,
        		    velocidadKepleriana.getY() / 1000.0,
        		    velocidadKepleriana.getZ() / 1000.0
        		);

        		System.out.printf(
        		    "Cartesiana: [%12.6f, %12.6f, %12.6f] km/s%n",
        		    velocidadCartesiana.getX() / 1000.0,
        		    velocidadCartesiana.getY() / 1000.0,
        		    velocidadCartesiana.getZ() / 1000.0
        		);        		
        		
		
	}

}
