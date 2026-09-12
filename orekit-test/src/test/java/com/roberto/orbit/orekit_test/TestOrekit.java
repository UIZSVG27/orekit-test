package com.roberto.orbit.orekit_test;

import org.junit.jupiter.api.Test;

import org.orekit.orbits.PositionAngleType;

import static org.junit.jupiter.api.Assertions.*;

import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.ZipJarCrawler;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.OrbitType;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.hipparchus.geometry.euclidean.threed.Vector3D;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class TestOrekit {

    @Test
    void testOrekitCargaYOrbita() throws Exception {

        // 1) Cargar datos de Orekit (necesitas el ZIP de datos)
        File orekitData = new File("src/main/resources/orekit-data.zip");
        assertTrue(orekitData.exists(), "El archivo orekit-data.zip no existe");

        DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
        manager.addProvider(new ZipJarCrawler(orekitData));

        // 2) Crear fecha
        AbsoluteDate fecha = new AbsoluteDate(2024, 1, 1, 12, 0, 0, TimeScalesFactory.getUTC());

        // 3) Crear una órbita kepleriana sencilla
        KeplerianOrbit orbita = new KeplerianOrbit(
                7000e3,      // a: semieje mayor
                0.01,        // excentricidad
                0.1,         // inclinación
                0.0,         // argumento del perigeo
                0.0,         // ascensión recta nodo ascendente
                0.0,         // anomalía verdadera
                PositionAngleType.TRUE,
                FramesFactory.getEME2000(),
                fecha,
                Constants.EIGEN5C_EARTH_MU
        );

        // 4) Comprobaciones
        assertEquals(7000e3, orbita.getA(), 1e-6);
        assertEquals(0.01, orbita.getE(), 1e-6);
        assertEquals(0.1, orbita.getI(), 1e-6);

        // 5) Vector posición
        Vector3D posicion = orbita.getPVCoordinates().getPosition();
        assertNotNull(posicion);

        System.out.println("Orekit funciona correctamente. Posición: " + posicion);
    }
}
