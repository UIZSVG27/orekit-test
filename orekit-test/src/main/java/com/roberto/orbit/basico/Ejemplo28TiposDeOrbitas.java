package com.roberto.orbit.basico;

import java.io.FileWriter;
import java.io.IOException;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo28TiposDeOrbitas {

	public static void main(String[] args) {
		
		OrekitConfig.inicializar();
		
		double mu = Constants.WGS84_EARTH_MU;

		// Radio donde vamos a comparar las velocidades
		double r = 7000.0e3;   // m

		// Velocidad circular
		double vCircular = Math.sqrt(mu / r);

		// Velocidad de escape
		double vEscape = Math.sqrt(2.0 * mu / r);
		
		System.out.println("Velocidad circular = "
		        + vCircular / 1000.0 + " km/s");

		System.out.println("Velocidad de escape = "
		        + vEscape / 1000.0 + " km/s");
		
		/*****************************************/
		double vEliptica = 0.9 * vEscape;
		double vParabolica = vEscape;
		double vHiperbolica = 1.1 * vEscape;
		double energiaEliptica =
		        vEliptica * vEliptica / 2.0 - mu / r;

		double energiaParabolica =
		        vParabolica * vParabolica / 2.0 - mu / r;

		double energiaHiperbolica =
		        vHiperbolica * vHiperbolica / 2.0 - mu / r;
		System.out.println("Energia eliptica = "
		        + energiaEliptica + " J/kg");

		System.out.println("Energia parabolica = "
		        + energiaParabolica + " J/kg");

		System.out.println("Energia hiperbolica = "
		        + energiaHiperbolica + " J/kg");
		
		/****************************************/
		double hEliptica = r * vEliptica;
		double hParabolica = r * vParabolica;
		double hHiperbolica = r * vHiperbolica;
		
		double eEliptica = Math.sqrt(
		        1.0 + 2.0 * energiaEliptica
		        * hEliptica * hEliptica
		        / (mu * mu)
		);

		double eParabolica = Math.sqrt(
		        1.0 + 2.0 * energiaParabolica
		        * hParabolica * hParabolica
		        / (mu * mu)
		);

		double eHiperbolica = Math.sqrt(
		        1.0 + 2.0 * energiaHiperbolica
		        * hHiperbolica * hHiperbolica
		        / (mu * mu)
		);
		
		System.out.println("e eliptica = " + eEliptica);
		System.out.println("e parabolica = " + eParabolica);
		System.out.println("e hiperbolica = " + eHiperbolica);
		/************************************************************/
		double aEliptica =
		        -mu / (2.0 * energiaEliptica);

		double aHiperbolica =
		        -mu / (2.0 * energiaHiperbolica);
		
		System.out.println("a eliptica = "
		        + aEliptica / 1000.0 + " km");

		System.out.println("a hiperbolica = "
		        + aHiperbolica / 1000.0 + " km");
		/***********Velocidad hiperbólica en el infinito***********************/
		double vInfinito =
		        Math.sqrt(2.0 * energiaHiperbolica);

		System.out.println("Velocidad en el infinito = "
		        + vInfinito / 1000.0
		        + " km/s");
		 
		/***********************************************************************/
		TimeScale utc = TimeScalesFactory.getUTC();

		AbsoluteDate fechaInicial = new AbsoluteDate(
		        2024, 1, 1,
		        12, 0, 0.0,
		        utc
		);

		Frame eme2000 = FramesFactory.getEME2000();
		
		KeplerianOrbit orbitaHiperbolica =
		        new KeplerianOrbit(
		                aHiperbolica,
		                eHiperbolica,
		                0.0,   // inclinación
		                0.0,   // argumento del periapsis
		                0.0,   // RAAN
		                0.0,   // TA = 0 -> periapsis
		                PositionAngleType.TRUE,
		                eme2000,
		                fechaInicial,
		                mu
		        );
		
		double radioPeriapsis =
		        orbitaHiperbolica.getPosition().getNorm();

		System.out.println("Radio en periapsis según Orekit = "
		        + radioPeriapsis / 1000.0
		        + " km");
		/***************************************************************/
		
		double dtHiperbola = 3600.0;   // 1 hora

		KeplerianOrbit orbitaHiperbola1h =
		        orbitaHiperbolica.shiftedBy(dtHiperbola);

		double radio1h =
		        orbitaHiperbola1h.getPosition().getNorm();

		double velocidad1hHiperbola =
		        orbitaHiperbola1h.getPVCoordinates()
		                         .getVelocity()
		                         .getNorm();

		System.out.println("Radio hipérbola después de 1 h = "
		        + radio1h / 1000.0 + " km");

		System.out.println("Velocidad hipérbola después de 1 h = "
		        + velocidad1hHiperbola / 1000.0 + " km/s");
		/**************************************************************/
		
		try (FileWriter writer = new FileWriter("hiperbola.csv")) {

		    writer.write("t,x,y,z,r,v\n");

		    double tiempoInicial = -3.0 * 3600.0;
		    double tiempoFinal   =  3.0 * 3600.0;
		    double paso = 300.0;

		    for (double t = tiempoInicial; t <= tiempoFinal; t += paso) {
		    

		        KeplerianOrbit orbitaT =
		                orbitaHiperbolica.shiftedBy(t);

		        Vector3D posicion =
		                orbitaT.getPosition();

		        Vector3D velocidad =
		                orbitaT.getPVCoordinates()
		                        .getVelocity();

		        double radio =
		                posicion.getNorm();

		        double moduloVelocidad =
		                velocidad.getNorm();

		        writer.write(
		                t + ","
		                + posicion.getX() + ","
		                + posicion.getY() + ","
		                + posicion.getZ() + ","
		                + radio + ","
		                + moduloVelocidad + "\n"
		        );
		    }

		    System.out.println("Archivo hiperbola.csv creado.");

		} catch (IOException ex) {

		    ex.printStackTrace();
		}

		
	}

}
