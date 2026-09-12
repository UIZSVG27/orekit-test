package com.roberto.orbit.basico;

import java.io.File;

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

public class Ejemplo02OrbitaKepleriana {

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
    }

    /**
     * Localiza el directorio orekit-data.
     *
     * Orden de búsqueda:
     * 1. Primer argumento del programa.
     * 2. Variable de entorno OREKIT_DATA.
     * 3. Directorio relativo ./orekit-data.
     */
    private static File localizarOrekitData(String[] args) {

        // 1. Argumento del programa
        if (args.length > 0 && !args[0].isBlank()) {
            return validarDirectorio(
                new File(args[0]),
                "argumento del programa"
            );
        }

        // 2. Variable de entorno
        String rutaEntorno = System.getenv("OREKIT_DATA");

        if (rutaEntorno != null && !rutaEntorno.isBlank()) {
            return validarDirectorio(
                new File(rutaEntorno),
                "variable de entorno OREKIT_DATA"
            );
        }

        // 3. Ruta relativa al directorio de ejecución
        return validarDirectorio(
            new File("orekit-data"),
            "directorio relativo"
        );
    }

    /**
     * Verifica que la ruta exista y sea un directorio.
     */
    private static File validarDirectorio(
        File directorio,
        String origen
    ) {

        if (!directorio.isDirectory()) {
            throw new IllegalStateException(
                "No existe el directorio de datos de Orekit."
                + System.lineSeparator()
                + "Origen comprobado: " + origen
                + System.lineSeparator()
                + "Ruta: " + directorio.getAbsolutePath()
                + System.lineSeparator()
                + System.lineSeparator()
                + "Puedes indicar la ruta mediante:"
                + System.lineSeparator()
                + "  1. El primer argumento del programa."
                + System.lineSeparator()
                + "  2. La variable de entorno OREKIT_DATA."
                + System.lineSeparator()
                + "  3. Una carpeta ./orekit-data."
            );
        }

        return directorio;
    }
}