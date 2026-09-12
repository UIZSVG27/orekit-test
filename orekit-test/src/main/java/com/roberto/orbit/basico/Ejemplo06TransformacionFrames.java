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
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinates;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo06TransformacionFrames {

    public static void main(String[] args) {

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

        // ----------------------------------------------------
        // 6. Mismo estado expresado en EME2000 e ITRF
        // ----------------------------------------------------

        PVCoordinates pvEme2000 =
            orbita.getPVCoordinates(eme2000);

        PVCoordinates pvItrf =
            orbita.getPVCoordinates(itrf);

        Vector3D posicionEme2000 =
            pvEme2000.getPosition();

        Vector3D velocidadEme2000 =
            pvEme2000.getVelocity();

        Vector3D posicionItrf =
            pvItrf.getPosition();

        Vector3D velocidadItrf =
            pvItrf.getVelocity();

        // ----------------------------------------------------
        // 7. Mostrar resultados
        // ----------------------------------------------------

        System.out.println();
        System.out.println("MISMO ESTADO EN DOS FRAMES");
        System.out.println("--------------------------");

        System.out.println("Fecha: " + fechaInicial);
        System.out.println("Frame original: "
            + orbita.getFrame().getName());

        System.out.println();
        System.out.println("COORDENADAS EN EME2000");
        System.out.println("----------------------");

        imprimirEstado(
            posicionEme2000,
            velocidadEme2000
        );

        System.out.println();
        System.out.println("COORDENADAS EN ITRF");
        System.out.println("-------------------");

        imprimirEstado(
            posicionItrf,
            velocidadItrf
        );

        // ----------------------------------------------------
        // 8. Comprobar que las distancias son iguales
        // ----------------------------------------------------

        System.out.println();
        System.out.println("COMPROBACIÓN");
        System.out.println("------------");

        System.out.printf(
            "|r| en EME2000: %.6f km%n",
            posicionEme2000.getNorm() / 1000.0
        );

        System.out.printf(
            "|r| en ITRF:    %.6f km%n",
            posicionItrf.getNorm() / 1000.0
        );
    }

    private static void imprimirEstado(
        Vector3D posicion,
        Vector3D velocidad
    ) {

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
    }
}