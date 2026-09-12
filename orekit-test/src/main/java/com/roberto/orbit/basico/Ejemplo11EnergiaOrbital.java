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

public class Ejemplo11EnergiaOrbital {

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
        
        PVCoordinates pv = orbita.getPVCoordinates();

        Vector3D r = pv.getPosition();
        Vector3D v = pv.getVelocity();

        double moduloR = r.getNorm();
        double moduloV = v.getNorm();
        
        double energia =
                moduloV * moduloV / 2.0
                - mu / moduloR;
 
        System.out.println("ENERGIA ORBITAL EN EL PERIGEO");
        System.out.println("----------------------------");

        System.out.println("r = " + moduloR / 1000.0 + " km");
        System.out.println("v = " + moduloV / 1000.0 + " km/s");

        System.out.println(
                "Energia especifica = "
                + energia
                + " J/kg"
        );
        /////////////////////////////////////
         double energiaTeorica = -mu / (2.0 * a);

		System.out.println(
		        "Energia teorica = "
		        + energiaTeorica
		        + " J/kg"
		);
       ////////////////////////
        double anomaliaA = Math.toRadians(180.0);

        

        // ----------------------------------------------------
        // 6. Crear la órbita en EME2000
        // ----------------------------------------------------

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
        
        PVCoordinates pvA = orbitaA.getPVCoordinates();

        Vector3D rA = pvA.getPosition();
        Vector3D vA = pvA.getVelocity();

        double moduloRA = rA.getNorm();
        double moduloVA = vA.getNorm();
        
        double energiaA =
                moduloVA * moduloVA / 2.0
                - mu / moduloRA;
        
        System.out.println("----------------------------");
        System.out.println("ENERGIA ORBITAL EN EL APOGEO");
        System.out.println("----------------------------");

        System.out.println("rA = " + moduloRA / 1000.0 + " km");
        System.out.println("vA = " + moduloVA / 1000.0 + " km/s");

        System.out.println(
                "Energia especifica = "
                + energiaA
                + " J/kg"
        ); 
        //////////////////////////////////////////////////
        double aCalculado = -mu / (2.0 * energia);

        System.out.println();
        System.out.println("SEMIEJE MAYOR A PARTIR DE LA ENERGIA");
        System.out.println("-----------------------------------");

        System.out.println(
                "a calculado = "
                + aCalculado / 1000.0
                + " km"
        );
		////////////////////////////////////////////////////
        Vector3D hVector = Vector3D.crossProduct(r, v);

        double h = hVector.getNorm();

        double eCalculada = Math.sqrt(
                1.0
                + (2.0 * energia * h * h)
                / (mu * mu)
        );

        System.out.println();
        System.out.println("EXCENTRICIDAD A PARTIR DE r Y v");
        System.out.println("------------------------------");

        System.out.println(
                "h = "
                + h
                + " m2/s"
        );

        System.out.println(
                "e calculada = "
                + eCalculada
        );
        
        double hz = hVector.getZ();

        double iCalculada = Math.acos(hz / h);

        System.out.println();
        System.out.println("INCLINACION A PARTIR DE h");
        System.out.println("-------------------------");

        System.out.println(
            "i calculada = "
            + Math.toDegrees(iCalculada)
            + " grados"
        );
        
        Vector3D k = new Vector3D(0.0, 0.0, 1.0);
        Vector3D nVector = Vector3D.crossProduct(k, hVector);
        System.out.println();
        System.out.println("VECTOR NODO");
        System.out.println("-----------");

        System.out.println(
            "N = "
            + nVector
        );
        double n = nVector.getNorm();

        System.out.println(
            "|N| = "
            + n
        );
        
        double raanCalculado = Math.atan2(
        	    nVector.getY(),
        	    nVector.getX()
        	);
        if (raanCalculado < 0.0) {
            raanCalculado += 2.0 * Math.PI;
        }
        
        System.out.println();
        System.out.println("RAAN A PARTIR DEL VECTOR NODO");
        System.out.println("-----------------------------");

        System.out.println(
            "RAAN calculado = "
            + Math.toDegrees(raanCalculado)
            + " grados"
        );
        
        Vector3D eVector =
        	    Vector3D.crossProduct(v, hVector)
        	        .scalarMultiply(1.0 / mu)
        	        .subtract(
        	            r.scalarMultiply(1.0 / moduloR)
        	        );

        double eVectorModulo = eVector.getNorm();

        System.out.println();
        System.out.println("VECTOR EXCENTRICIDAD");
        System.out.println("--------------------");

        System.out.println("eVector = " + eVector);
        System.out.println("|eVector| = " + eVectorModulo);

        double omegaCalculado = Math.atan2(
        	    Vector3D.dotProduct(
        	        Vector3D.crossProduct(nVector, eVector),
        	        hVector
        	    ) / h,
        	    Vector3D.dotProduct(nVector, eVector)
        	);
        
        if (omegaCalculado < 0.0) {
            omegaCalculado += 2.0 * Math.PI;
        }

        System.out.println(
        	    "Argumento de perigeo calculado = "
        	    + Math.toDegrees(omegaCalculado)
        	    + " grados"
        	);
        
        double taCalculado = Math.atan2(
        	    Vector3D.dotProduct(
        	        Vector3D.crossProduct(eVector, r),
        	        hVector
        	    ) / h,
        	    Vector3D.dotProduct(eVector, r)
        	);

        	if (taCalculado < 0.0) {
        	    taCalculado += 2.0 * Math.PI;
        	}

        	System.out.println();
        	System.out.println("TRUE ANOMALY A PARTIR DE r Y e");
        	System.out.println("----------------------------");

        	System.out.println(
        	    "TA calculado = "
        	    + Math.toDegrees(taCalculado)
        	    + " grados"
        	);
        
	}

}
