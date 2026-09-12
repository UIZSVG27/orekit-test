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

public class Ejemplo03OrbitaKepleriana {

    public static void main(String[] args) {

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

        // ----------------------------------------------------
        // 3. Elementos keplerianos
        // ----------------------------------------------------

        double a = 7_000_000.0;                  // semieje mayor, m
        double e = 0.001;                        // excentricidad
        double i = Math.toRadians(51.6);          // inclinación
        double argumentoPerigeo = Math.toRadians(0.0);
        double raan = Math.toRadians(0.0);
        double anomaliaVerdadera = Math.toRadians(0.0);
        
        double dt = 3600.0;

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
        
        KeplerianOrbit orbitaDespues = orbita.shiftedBy(dt); //Orbita 3600sg después
        PVCoordinates pvDespues = orbitaDespues.getPVCoordinates();
        Vector3D posicionDespues =pvDespues.getPosition();
        Vector3D velocidadDespues =pvDespues.getVelocity();

        // ----------------------------------------------------
        // 5. Recuperar elementos orbitales
        // ----------------------------------------------------

        System.out.println();
        System.out.println("ELEMENTOS ORBITALES");
        System.out.println("-------------------");

        System.out.printf(
            "Semieje mayor:         %.3f km%n",
            orbita.getA() / 1000.0
        );

        System.out.printf(
            "Excentricidad:         %.8f%n",
            orbita.getE()
        );

        System.out.printf(
            "Inclinación:           %.6f grados%n",
            Math.toDegrees(orbita.getI())
        );

        System.out.printf(
            "Argumento del perigeo: %.6f grados%n",
            Math.toDegrees(orbita.getPerigeeArgument())
        );

        System.out.printf(
            "RAAN:                  %.6f grados%n",
            Math.toDegrees(
                orbita.getRightAscensionOfAscendingNode()
            )
        );

        System.out.printf(
            "Anomalía verdadera:    %.6f grados%n",
            Math.toDegrees(orbita.getTrueAnomaly())
        );

        // ----------------------------------------------------
        // 6. Magnitudes derivadas
        // ----------------------------------------------------

        double periodo = orbita.getKeplerianPeriod();
        double movimientoMedio =
            orbita.getKeplerianMeanMotion();

        System.out.println();
        System.out.println("MAGNITUDES DERIVADAS");
        System.out.println("--------------------");

        System.out.printf(
            "Periodo orbital:       %.3f s%n",
            periodo
        );

        System.out.printf(
            "Periodo orbital:       %.3f min%n",
            periodo / 60.0
        );

        System.out.printf(
            "Movimiento medio:      %.10f rad/s%n",
            movimientoMedio
        );

        // ----------------------------------------------------
        // 7. Posición y velocidad
        // ----------------------------------------------------

        PVCoordinates pv = orbita.getPVCoordinates();

        Vector3D posicion = pv.getPosition();
        Vector3D velocidad = pv.getVelocity();

        System.out.println();
        System.out.println("VECTOR DE ESTADO EN EME2000");
        System.out.println("---------------------------");

        System.out.printf(
            "r = [%12.3f, %12.3f, %12.3f] km%n",
            posicion.getX() / 1000.0,
            posicion.getY() / 1000.0,
            posicion.getZ() / 1000.0
        );

        System.out.printf(
            "v = [%12.6f, %12.6f, %12.6f] km/s%n",
            velocidad.getX() / 1000.0,
            velocidad.getY() / 1000.0,
            velocidad.getZ() / 1000.0
        );

        System.out.printf(
            "|r| = %.3f km%n",
            posicion.getNorm() / 1000.0
        );

        System.out.printf(
            "|v| = %.6f km/s%n",
            velocidad.getNorm() / 1000.0
        );
        
        //Datos de la órbita 3600 sg después
        System.out.println();
        System.out.println("----------------------------");
        System.out.println("ESTADO 3600 SEGUNDOS DESPUÉS");
        System.out.println("----------------------------");

        System.out.println(
            "Fecha: " + orbitaDespues.getDate()
        );

        System.out.printf(
            "Anomalía verdadera: %.6f grados%n",
            Math.toDegrees(
                orbitaDespues.getTrueAnomaly()
            )
        );

        System.out.printf(
            "r = [%12.3f, %12.3f, %12.3f] km%n",
            posicionDespues.getX() / 1000.0,
            posicionDespues.getY() / 1000.0,
            posicionDespues.getZ() / 1000.0
        );

        System.out.printf(
            "v = [%12.6f, %12.6f, %12.6f] km/s%n",
            velocidadDespues.getX() / 1000.0,
            velocidadDespues.getY() / 1000.0,
            velocidadDespues.getZ() / 1000.0
        );

        System.out.printf(
            "|r| = %.3f km%n",
            posicionDespues.getNorm() / 1000.0
        );

        System.out.printf(
            "|v| = %.6f km/s%n",
            velocidadDespues.getNorm() / 1000.0
        );
        
      //Comparación
        System.out.println();
        System.out.println("COMPARACIÓN DE ELEMENTOS");
        System.out.println("------------------------");

        System.out.printf(
            "a inicial / final: %.3f / %.3f km%n",
            orbita.getA() / 1000.0,
            orbitaDespues.getA() / 1000.0
        );

        System.out.printf(
            "e inicial / final: %.8f / %.8f%n",
            orbita.getE(),
            orbitaDespues.getE()
        );

        System.out.printf(
            "i inicial / final: %.6f / %.6f grados%n",
            Math.toDegrees(orbita.getI()),
            Math.toDegrees(orbitaDespues.getI())
        );

        System.out.printf(
            "ν inicial / final: %.6f / %.6f grados%n",
            Math.toDegrees(orbita.getTrueAnomaly()),
            Math.toDegrees(orbitaDespues.getTrueAnomaly())
        );
        
    }

  

    
    
}