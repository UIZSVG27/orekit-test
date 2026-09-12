package com.roberto.orbit.maniobras;

import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo22TransferenciaHohmann {

	public static void main(String[] args) {
		OrekitConfig.inicializar();
		
		double r1 = 7000.0e3;     // m
		double r2 = 14000.0e3;//20000.0e3;//14000.0e3;    // m
		double mu = Constants.WGS84_EARTH_MU; // m3/s2
		Frame frame = FramesFactory.getEME2000();
		
		TransferenciaHohmann hohmann =
		        new TransferenciaHohmann(r1, r2, mu);
		hohmann.calcular();
		/*                  */
		AbsoluteDate fecha =
		        new AbsoluteDate(
		                2024, 1, 1,
		                12, 0, 0.0,
		                TimeScalesFactory.getUTC()
		        );
		/*                  */
		double aTransferencia = hohmann.getATransferencia();
		double eTransferencia = hohmann.getETransferencia();
		
		/*  Óbita Kepleriana                */
		KeplerianOrbit orbitaTransferencia =
		        new KeplerianOrbit(
		                aTransferencia,
		                eTransferencia,
		                0.0,                    // i
		                0.0,                    // omega
		                0.0,                    // RAAN
		                0.0,                    // TA
		                PositionAngleType.TRUE,
		                frame,
		                fecha,
		                mu
		        );
		double radioInicial =
		        orbitaTransferencia.getPosition().getNorm();

		System.out.println("Radio inicial según Orekit = "
		        + radioInicial / 1000.0 + " km");
		
		double velocidadInicial =
		        orbitaTransferencia.getPVCoordinates()
		                           .getVelocity()
		                           .getNorm();
		
		System.out.println("Velocidad inicial según Orekit = "
		        + velocidadInicial + " m/s");
		
		double tiempoTransferencia = hohmann.getTiempoTransferencia();
		
		KeplerianOrbit orbitaEnApogeo =
		        orbitaTransferencia.shiftedBy(tiempoTransferencia);
		
		double radioApogeo =
		        orbitaEnApogeo.getPosition().getNorm();

		System.out.println("Radio en apogeo según Orekit = "
		        + radioApogeo / 1000.0 + " km");
		
		double velocidadApogeo =
		        orbitaEnApogeo.getPVCoordinates()
		                      .getVelocity()
		                      .getNorm();

		System.out.println("Velocidad en apogeo según Orekit = "
		        + velocidadApogeo + " m/s");
		
		/*  Órbita Kepleriana                */
		
		double deltaV1 = hohmann.getDeltaV1();
		System.out.println("Delta-V1 = " + deltaV1 + " m/s");
		double deltaV2 = hohmann.getDeltaV2();
		System.out.println("Delta-V2 = " + deltaV2 + " m/s");
		double deltaVTotal = hohmann.getDeltaVTotal();
		System.out.println("Delta-V total = " + deltaVTotal + " m/s");
		

		System.out.println("Tiempo transferencia = "
		        + tiempoTransferencia + " s");
		
		double tiempoTransferenciaMin =
		        tiempoTransferencia / 60.0;
		System.out.println("Tiempo transferencia = "
		        + tiempoTransferenciaMin + " min");
		

		System.out.println("Semieje mayor transferencia = "
		        + aTransferencia / 1000.0 + " km");
		

		System.out.println("Excentricidad transferencia = "
		        + eTransferencia);
		
	}

}
