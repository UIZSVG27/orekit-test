package com.roberto.orbit.pruebas;

import java.io.File;

import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;

public class KeplerExample {

    public static void main(String[] args) {

        // 1. Cargar los datos de Orekit ANTES de utilizar UTC
        File orekitData = new File(
            "C:/ACTUALIZACIONES/2026/20260718_TUTORIALS/orekit-data"
        );

        if (!orekitData.isDirectory()) {
            throw new IllegalStateException(
                "No existe el directorio: " +
                orekitData.getAbsolutePath()
            );
        }

        DataProvidersManager manager =
            DataContext.getDefault().getDataProvidersManager();

        manager.clearProviders();
        manager.addProvider(new DirectoryCrawler(orekitData));

        // 2. Ahora ya se puede pedir la escala UTC
        TimeScale utc = TimeScalesFactory.getUTC();

        // 3. Fecha inicial
        AbsoluteDate fechaInicial = new AbsoluteDate(
            2024, 1, 1,
            12, 0, 0.0,
            utc
        );

        // 4. Marco de referencia
        Frame eme2000 = FramesFactory.getEME2000();

        System.out.println("Orekit cargado correctamente");
        System.out.println("Fecha inicial: " + fechaInicial);
        System.out.println("Marco: " + eme2000.getName());

        System.out.println("Datos utilizados:");
        for (String nombre : manager.getLoadedDataNames()) {
            System.out.println("  " + nombre);
        }

        // A partir de aquí, parámetros de la órbita...
        double a = 7000e3;
        double e = 0.001;
        double i = Math.toRadians(51.6);
        double raan = Math.toRadians(0.0);
        double argumentoPerigeo = Math.toRadians(0.0);
        double anomalia = Math.toRadians(0.0);

        System.out.println("Semieje mayor: " + a + " m");
        System.out.println("Excentricidad: " + e);
        System.out.println("Inclinación: " + Math.toDegrees(i) + " grados");
    }
}