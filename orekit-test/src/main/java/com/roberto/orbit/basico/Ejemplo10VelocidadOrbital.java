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
import org.orekit.utils.PVCoordinates;

import com.roberto.orbit.util.OrekitConfig;


public class Ejemplo10VelocidadOrbital {

    public static void main(String[] args) {

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

        double a = 10_000_000.0;//7_000_000.0;
        double e = 0.3; //0.001;
        double i = Math.toRadians(51.6);

        double argumentoPerigeo = Math.toRadians(0.0);
        double raan = Math.toRadians(0.0);
        double anomalia = Math.toRadians(0.0);

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
            anomalia,
            PositionAngleType.TRUE,//PositionAngleType.TRUE,
            eme2000,
            fechaInicial,
            mu
        );
        
        double velocidadPerigeo = orbita.getPVCoordinates().getVelocity().getNorm();
        System.out.println(
        	    "Velocidad Perigeo Orekit = "
        	    + velocidadPerigeo / 1000.0
        	    + " km/s"
        	);
       
        double anomaliaA = Math.toRadians(180.0);
        KeplerianOrbit orbitaA = new KeplerianOrbit(
                a,
                e,
                i,
                argumentoPerigeo,
                raan,
                anomaliaA,
                PositionAngleType.TRUE,//PositionAngleType.TRUE,
                eme2000,
                fechaInicial,
                mu
            );
        double velocidadApogeo = orbitaA.getPVCoordinates().getVelocity().getNorm();
        System.out.println(
        	    "Velocidad Apogeo Orekit = "
        	    + velocidadApogeo / 1000.0
        	    + " km/s"
        	);    
        
        double anomalia90 = Math.toRadians(90.0);
        KeplerianOrbit orbita90= new KeplerianOrbit(
                a,
                e,
                i,
                argumentoPerigeo,
                raan,
                anomalia90,
                PositionAngleType.TRUE,//PositionAngleType.TRUE,
                eme2000,
                fechaInicial,
                mu
            );
        double velocidad90 = orbita90.getPVCoordinates().getVelocity().getNorm();
        System.out.println(
        	    "Velocidad 90 TA Orekit = "
        	    + velocidad90 / 1000.0
        	    + " km/s"
        	);        

        double anomalia270 = Math.toRadians(270.0);
        KeplerianOrbit orbita270= new KeplerianOrbit(
                a,
                e,
                i,
                argumentoPerigeo,
                raan,
                anomalia270,
                PositionAngleType.TRUE,//PositionAngleType.TRUE,
                eme2000,
                fechaInicial,
                mu
            );
        
        
     // ----------------------------------------------------
     // 6. Distancias de perigeo y apogeo
     // ----------------------------------------------------

     double rp = a * (1.0 - e);
     double ra = a * (1.0 + e);

     System.out.println("Perigeo = " + rp / 1000.0 + " km");
     System.out.println("Apogeo  = " + ra / 1000.0 + " km");
     

  // ----------------------------------------------------
  // 7. Velocidades mediante la ecuación vis-viva
  // ----------------------------------------------------

  double vp = Math.sqrt(
          mu * (2.0 / rp - 1.0 / a)
  );

  double va = Math.sqrt(
          mu * (2.0 / ra - 1.0 / a)
  );

  System.out.println();
  System.out.println("VELOCIDADES POR VIS-VIVA");
  System.out.println("------------------------");
  System.out.println("Velocidad perigeo = " + vp / 1000.0 + " km/s");
  System.out.println("Velocidad apogeo  = " + va / 1000.0 + " km/s");        
     
  double r90 = a * (1.0 - e * e);

  double v90VisViva = Math.sqrt(
          mu * (2.0 / r90 - 1.0 / a)
  );

  System.out.println(
          "Velocidad 90 TA Vis-Viva = "
          + v90VisViva / 1000.0
          + " km/s"
  );
  
  double ma90 = orbita90.getMeanAnomaly();

  System.out.println(
      "MA cuando TA = 90 = "
      + Math.toDegrees(ma90)
      + " grados"
  );
  
  double n = Math.sqrt(mu / Math.pow(a, 3));
  
  double tiempoHasta90 = ma90 / n;

  System.out.println(
      "Tiempo hasta TA = 90 = "
      + tiempoHasta90
      + " segundos"
  );
  
  KeplerianOrbit orbita90Propagada =
	        (KeplerianOrbit) orbita.shiftedBy(tiempoHasta90);
  
  double taComprobacion =
	        Math.toDegrees(orbita90Propagada.getTrueAnomaly());

	System.out.println(
	    "TA después de propagar = "
	    + taComprobacion
	    + " grados"
	);
	
	PVCoordinates pv90 = orbita90.getPVCoordinates();

	Vector3D r = pv90.getPosition();
	Vector3D v = pv90.getVelocity();
	Vector3D rUnitario = r.normalize();

	double vr = Vector3D.dotProduct(v, rUnitario);

	System.out.println(
	    "Velocidad radial = "
	    + vr / 1000.0
	    + " km/s"
	);
	
	double velocidadTotal = v.getNorm();

	double vt = Math.sqrt(
	    velocidadTotal * velocidadTotal
	    - vr * vr
	);

	System.out.println(
	    "Velocidad transversal = "
	    + vt / 1000.0
	    + " km/s"
	);
	double h = r.getNorm() * vt;
	System.out.println(
		    "h = r * vt       = "
		    + h
		    + " m2/s"
		);

		System.out.println(
		    "h = |r x v|      = "
		    + Vector3D.crossProduct(r, v).getNorm()
		    + " m2/s"
		);
		
		double gamma = Math.atan2(vr, vt);

		System.out.println(
		        "Angulo de trayectoria gamma = "
		        + Math.toDegrees(gamma)
		        + " grados"
		);		
	
		double TA90 = Math.toRadians(90.0);

		double gammaCurtis90 = Math.atan2(
		        e * Math.sin(TA90),
		        1.0 + e * Math.cos(TA90)
		);

		System.out.println(
		        "Gamma por formula de Curtis(90) = "
		        + Math.toDegrees(gammaCurtis90)
		        + " grados"
		);
		/////////////////////////////
		double TA180 = Math.toRadians(180.0);

		double gammaCurtis180 = Math.atan2(
		        e * Math.sin(TA180),
		        1.0 + e * Math.cos(TA180)
		);

		System.out.println(
		        "Gamma por formula de Curtis(180) = "
		        + Math.toDegrees(gammaCurtis180)
		        + " grados"
		);
		/////////////////////////////
		double TA270 = Math.toRadians(270.0);
		
		double gammaCurtis270 = Math.atan2(
		e * Math.sin(TA270),
		1.0 + e * Math.cos(TA270)
		);
		
		System.out.println(
		"Gamma por formula de Curtis(270) = "
		+ Math.toDegrees(gammaCurtis270)
		+ " grados"
		);
		/////////////////////////////
		double TA0 = Math.toRadians(0.0);
		
		double gammaCurtis0 = Math.atan2(
		e * Math.sin(TA0),
		1.0 + e * Math.cos(TA0)
		);
		
		System.out.println(
		"Gamma por formula de Curtis(0) = "
		+ Math.toDegrees(gammaCurtis0)
		+ " grados"
		);

		
    }
}