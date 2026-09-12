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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class Ejemplo04OrbitaKepleriana {

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
        
       
        PrintWriter salida = null;

        try {

            salida = new PrintWriter(
                new FileWriter("orbita.csv")
            );

            salida.println("tiempo;x;y;z");
            for (double tiempo = 0.0;
            	     tiempo <= 3600.0;
            	     tiempo += 60.0) {

            	    KeplerianOrbit orbitaTiempo =
            	            orbita.shiftedBy(tiempo);

            	    PVCoordinates pvTiempo =
            	            orbitaTiempo.getPVCoordinates();

            	    Vector3D posicionTiempo =
            	            pvTiempo.getPosition();

            	    salida.printf(
            	    	    "%.0f;%.3f;%.3f;%.3f%n",
            	    	    tiempo,
            	    	    posicionTiempo.getX()/1000.0,
            	    	    posicionTiempo.getY()/1000.0,
            	    	    posicionTiempo.getZ()/1000.0
            	    	);
            	}
            salida.close();
            System.out.println(
            	    "Archivo orbita.csv creado correctamente."
            	);
            System.out.println(
            	    "Ruta del archivo: "
            	    + new java.io.File("orbita.csv").getAbsolutePath()
            	);
             }
  
     catch (IOException ex) {

        ex.printStackTrace();

    }
    
  }
}