package com.roberto.orbit.seguimiento;

import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

import com.roberto.orbit.util.OrekitConfig;

import org.hipparchus.geometry.euclidean.threed.Vector3D;


public class Ejemplo07SeguimientoDesdeMadrid {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
        // ----------------------------------------------------
        // 1. Inicialización de Orekit
        // ----------------------------------------------------

        OrekitConfig.inicializar();

        // ----------------------------------------------------
        // 2. Fecha
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

        double a = 7_000_000.0;
        double e = 0.001;
        double i = Math.toRadians(51.6);

        double argumentoPerigeo = Math.toRadians(0.0);
        double raan = Math.toRadians(0.0);
        double anomaliaVerdadera = Math.toRadians(0.0);

        double mu = Constants.WGS84_EARTH_MU;

        // ----------------------------------------------------
        // 5. Crear la órbita en EME2000
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

        OneAxisEllipsoid tierra =
        	    new OneAxisEllipsoid(
        	        Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
        	        Constants.WGS84_EARTH_FLATTENING,
        	        itrf);
        
        GeodeticPoint madrid = new GeodeticPoint(
        	    Math.toRadians(40.4168),   // latitud
        	    Math.toRadians(-3.7038),   // longitud
        	    667.0                      // altura en metros
        	);
        
        TopocentricFrame madridFrame =
        	    new TopocentricFrame(
        	        tierra,
        	        madrid,
        	        "Madrid"
        	    );
        
        Vector3D posicion = orbita.getPVCoordinates().getPosition();
        

        double azimut = madridFrame.getAzimuth(
        	    posicion,
        	    eme2000,
        	    fechaInicial
        	);

        	double elevacion = madridFrame.getElevation(
        	    posicion,
        	    eme2000,
        	    fechaInicial
        	);

        	double distancia = madridFrame.getRange(
        	    posicion,
        	    eme2000,
        	    fechaInicial
        	);
        	
        	//////////////////////////////////
        	System.out.println();
        	System.out.println("OBSERVACIÓN DESDE MADRID");
        	System.out.println("------------------------");

        	System.out.printf(
        	    "Azimut:    %.3f grados%n",
        	    Math.toDegrees(azimut)
        	);

        	System.out.printf(
        	    "Elevación: %.3f grados%n",
        	    Math.toDegrees(elevacion)
        	);

        	System.out.printf(
        	    "Distancia: %.3f km%n",
        	    distancia / 1000.0
        	);
        	
        
	}

}
