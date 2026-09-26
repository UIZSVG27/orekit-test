package com.roberto.orbit.basico;

import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo27OrbitaEliptica {

	public static void main(String[] args) {
		
		OrekitConfig.inicializar();
		
		/*rp y ra*/
		// Perigeo y apogeo de la órbita elíptica
		double rp = 7000.0e3;   // m
		double ra = 20000.0e3;  // m
		/*Cálculos*/
		double a = (rp + ra) / 2.0;
		double e = (ra - rp) / (ra + rp);
		System.out.println("Semieje mayor = "
		        + a / 1000.0 + " km");

		System.out.println("Excentricidad = "
		        + e);
		/****************vis-viva*****************************/
		double mu = Constants.WGS84_EARTH_MU;
		double vp = Math.sqrt(
		        mu * (2.0 / rp - 1.0 / a)
		);

		double va = Math.sqrt(
		        mu * (2.0 / ra - 1.0 / a)
		);
		System.out.println("Velocidad en perigeo = "
		        + vp / 1000.0 + " km/s");

		System.out.println("Velocidad en apogeo = "
		        + va / 1000.0 + " km/s");
		double periodo = 2.0 * Math.PI
		        * Math.sqrt(Math.pow(a, 3) / mu);

		System.out.println("Periodo orbital = "
		        + periodo + " s");

		System.out.println("Periodo orbital = "
		        + periodo / 60.0 + " min");
		
		/********Construcción de la Órbita(Kepleriana)****************************/
		// Fecha inicial
		TimeScale utc = TimeScalesFactory.getUTC();

		AbsoluteDate fechaInicial = new AbsoluteDate(
		        2024, 1, 1,
		        12, 0, 0.0,
		        utc
		);

		// Sistema de referencia inercial
		Frame eme2000 = FramesFactory.getEME2000();
		
		KeplerianOrbit orbitaInicial =
		        new KeplerianOrbit(
		                a,
		                e,
		                0.0,   // inclinación
		                0.0,   // argumento del perigeo
		                0.0,   // RAAN
		                0.0,   // TA = 0 -> estamos en el perigeo
		                PositionAngleType.TRUE,
		                eme2000,
		                fechaInicial,
		                mu
		        );
		/*Comprobación del perigeo es correcto*/
		double radioInicial =
		        orbitaInicial.getPosition().getNorm();

		System.out.println("Radio inicial según Orekit = "
		        + radioInicial / 1000.0 + " km");
		/*Propagamos 1 hora*/
		double dt = 3600.0;   // s

		KeplerianOrbit orbita1h =
		        orbitaInicial.shiftedBy(dt);
		double ta1h = orbita1h.getTrueAnomaly();
		double e1h  = orbita1h.getEccentricAnomaly();
		double me1h = orbita1h.getMeanAnomaly();
		System.out.println("TA después de 1 h = "
		        + Math.toDegrees(ta1h) + " grados");

		System.out.println("E después de 1 h = "
		        + Math.toDegrees(e1h) + " grados");

		System.out.println("M_e después de 1 h = "
		        + Math.toDegrees(me1h) + " grados");
		/***********************Alguna comprobación***************************/
		double n = Math.sqrt(mu / Math.pow(a, 3));

		double meCalculada = n * dt;

		System.out.println("Movimiento medio n = "
		        + n + " rad/s");

		System.out.println("M_e calculada = "
		        + Math.toDegrees(meCalculada)
		        + " grados");
		/*Comprobación*/
		double meDesdeE = e1h - e * Math.sin(e1h);

		System.out.println("M_e desde ecuación de Kepler = " + Math.toDegrees(meDesdeE) + " grados");
		double factor = Math.sqrt((1.0 + e) / (1.0 - e));
		double taDesdeE =2.0 * Math.atan( factor * Math.tan(e1h / 2.0));		
		System.out.println("TA desde E = "+ Math.toDegrees(taDesdeE) +  " grados");
		
		/* 1 hora después con TA aprox 134 grados*/
		double radioDesdeE = a * (1.0 - e * Math.cos(e1h));
		double radioOrekit = orbita1h.getPosition().getNorm();
        System.out.println("Radio desde E = "+ radioDesdeE / 1000.0+ " km");
		System.out.println("Radio según Orekit = "+ radioOrekit / 1000.0+ " km");		
		
		/*************Comprobación vis-viva y con Orbita Orekit********************/
		double velocidad1h =
		        Math.sqrt(
		                mu * (2.0 / radioOrekit - 1.0 / a)
		        );

		System.out.println("Velocidad después de 1 h = "
		        + velocidad1h / 1000.0
		        + " km/s");
		double velocidadOrekit =
		        orbita1h.getPVCoordinates()
		                .getVelocity()
		                .getNorm();

		System.out.println("Velocidad según Orekit = "
		        + velocidadOrekit / 1000.0
		        + " km/s");
		/************************Alguna comprobación más****************************/
		double h = Math.sqrt(
		        mu * a * (1.0 - e * e)
		);
		
		double vr = (mu / h)
		        * e * Math.sin(ta1h);

		double vt = (mu / h)
		        * (1.0 + e * Math.cos(ta1h));
		System.out.println("Velocidad radial = "
		        + vr / 1000.0 + " km/s");

		System.out.println("Velocidad transversal = "
		        + vt / 1000.0 + " km/s");

		double velocidadComponentes =
		        Math.sqrt(vr * vr + vt * vt);

		System.out.println("Velocidad desde vr y vt = "
		        + velocidadComponentes / 1000.0
		        + " km/s");
		double gamma = Math.atan2(vr, vt);

		System.out.println("Ángulo de trayectoria = "
		        + Math.toDegrees(gamma) + " grados");
		

	}

}
