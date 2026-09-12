package com.roberto.orbit.seguimiento;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo08SeguimientoContinuoDesdeMadrid {

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

        // ----------------------------------------------------
        // 6. Modelo de la Tierra
        // ----------------------------------------------------

        OneAxisEllipsoid tierra = new OneAxisEllipsoid(
            Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
            Constants.WGS84_EARTH_FLATTENING,
            itrf
        );

        // ----------------------------------------------------
        // 7. Punto geodésico de Madrid
        // ----------------------------------------------------

        GeodeticPoint madrid = new GeodeticPoint(
            Math.toRadians(40.4168),   // Latitud
            Math.toRadians(-3.7038),   // Longitud
            667.0                      // Altura en metros
        );

        // ----------------------------------------------------
        // 8. Sistema topocéntrico de Madrid
        // ----------------------------------------------------

        TopocentricFrame madridFrame = new TopocentricFrame(
            tierra,
            madrid,
            "Madrid"
        );

        // ----------------------------------------------------
        // 9. Cabecera de resultados
        // ----------------------------------------------------

        System.out.println();
        System.out.println("SEGUIMIENTO DEL SATÉLITE DESDE MADRID");
        System.out.println("------------------------------------");
        System.out.println();

        System.out.printf(
            "%5s %10s %12s %15s%n",
            "Min",
            "Az (°)",
            "Elev (°)",
            "Dist (km)"
        );

        System.out.println(
            "----------------------------------------------------"
        );

        // ----------------------------------------------------
        // 10. Seguimiento durante 120 minutos
        // ----------------------------------------------------

        for (int minuto = 0; minuto <= 120; minuto++) {

            // Tiempo transcurrido en segundos
            double tiempo = minuto * 60.0;

            // Propagar la órbita hasta el instante actual
            Orbit orbitaActual = orbita.shiftedBy(tiempo);

            // Posición del satélite en EME2000
            Vector3D posicionActual =
                orbitaActual
                    .getPVCoordinates()
                    .getPosition();

            // Azimut visto desde Madrid
            double azimutActual = madridFrame.getAzimuth(
                posicionActual,
                eme2000,
                orbitaActual.getDate()
            );

            // Elevación vista desde Madrid
            double elevacionActual = madridFrame.getElevation(
                posicionActual,
                eme2000,
                orbitaActual.getDate()
            );

            // Distancia Madrid-satélite
            double distanciaActual = madridFrame.getRange(
                posicionActual,
                eme2000,
                orbitaActual.getDate()
            );

            // Mostrar resultados
            System.out.printf(
                "%5d %10.2f %12.2f %15.2f%n",
                minuto,
                Math.toDegrees(azimutActual),
                Math.toDegrees(elevacionActual),
                distanciaActual / 1000.0
            );
        }
    }
}