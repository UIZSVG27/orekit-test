package com.roberto.orbit.maniobras;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.forces.maneuvers.ImpulseManeuver;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.events.DateDetector;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo23CambioPlanoOrbital {

	public static void main(String[] args) {
	
		double r = 7000.0e3;   // m
		double mu = Constants.WGS84_EARTH_MU;

		double velocidadCircular =
		        Math.sqrt(mu / r);

		System.out.println("Velocidad circular = "
		        + velocidadCircular + " m/s");
		
		double i1 = Math.toRadians(0.0); // Funciones trabajan en radianes
		double i2 = Math.toRadians(30.0); // Funciones trabajan en radianes

		double deltaI = i2 - i1;

		System.out.println("Cambio de inclinación = "
		        + Math.toDegrees(deltaI) + " grados");
		
		double deltaV =
		        2.0 * velocidadCircular
		        * Math.sin(deltaI / 2.0); //Coste de la maniobra 2vsin(incremento(v)72)

		System.out.println("Delta-V cambio de plano = "
		        + deltaV + " m/s");
		/*                       */
		double velocidadApogeo = 4356.7158983628515;

		double deltaVApogeo =
		        2.0 * velocidadApogeo
		        * Math.sin(deltaI / 2.0);

		System.out.println("Delta-V cambio de plano en apogeo = "
		        + deltaVApogeo + " m/s");
		/*                        */
		Vector3D v1 =
		        new Vector3D(
		                0.0,
		                velocidadCircular,
		                0.0
		        );

		Vector3D v2 =
		        new Vector3D(
		                0.0,
		                velocidadCircular * Math.cos(deltaI),
		                velocidadCircular * Math.sin(deltaI)
		        );
		
		Vector3D deltaVVector = v2.subtract(v1);
		System.out.println("Módulo Delta-V calculado con vectores = "
		        + deltaVVector.getNorm() + " m/s");
		
		/*Orbita*/
		System.out.println("--Órbita--");
		OrekitConfig.inicializar();

		Frame frame = FramesFactory.getEME2000();

		AbsoluteDate fecha =
		        new AbsoluteDate(
		                2024, 1, 1,
		                12, 0, 0.0,
		                TimeScalesFactory.getUTC()
		        );
		KeplerianOrbit orbitaInicial =
		        new KeplerianOrbit(
		                r,                      // a
		                0.0,                    // e
		                i1,                     // inclinación
		                0.0,                    // omega
		                0.0,                    // RAAN
		                0.0,                    // TA
		                PositionAngleType.TRUE,
		                frame,
		                fecha,
		                mu
		        );
		System.out.println("Inclinación inicial según Orekit = "
		        + Math.toDegrees(orbitaInicial.getI())
		        + " grados");
		
		KeplerianPropagator propagador =
		        new KeplerianPropagator(orbitaInicial);

		AbsoluteDate fechaManiobra =
		        fecha.shiftedBy(1.0);

		DateDetector detector =
		        new DateDetector(fechaManiobra);

		// Órbita justo en el instante de la maniobra
		KeplerianOrbit orbitaEnManiobra =
		        orbitaInicial.shiftedBy(1.0);

		// Velocidad real en ese instante
		Vector3D velocidadEnManiobra =
		        orbitaEnManiobra.getPVCoordinates()
		                        .getVelocity();

		System.out.println("Velocidad en instante de maniobra = "
		        + velocidadEnManiobra.getNorm()
		        + " m/s");

		// Posición real en ese instante
		Vector3D posicionEnManiobra =
		        orbitaEnManiobra.getPVCoordinates()
		                        .getPosition();

		// Dirección radial
		Vector3D direccionRadial =
		        posicionEnManiobra.normalize();

		// Velocidad objetivo: giramos la velocidad deltaI
		Vector3D velocidadObjetivo =
		        velocidadEnManiobra
		                .scalarMultiply(Math.cos(deltaI))
		        .add(
		                Vector3D.crossProduct(
		                        direccionRadial,
		                        velocidadEnManiobra
		                ).scalarMultiply(Math.sin(deltaI))
		        );

		System.out.println("Módulo velocidad objetivo = "
		        + velocidadObjetivo.getNorm()
		        + " m/s");

		// Delta-V correcto en el instante real de la maniobra
		Vector3D deltaVVectorCorregido =
		        velocidadObjetivo.subtract(velocidadEnManiobra);

		System.out.println("Módulo Delta-V corregido = "
		        + deltaVVectorCorregido.getNorm()
		        + " m/s");

		// Maniobra
		ImpulseManeuver maniobraCambioPlano =
		        new ImpulseManeuver(
		                detector,
		                deltaVVectorCorregido,
		                300.0
		        );

		propagador.addEventDetector(
		        maniobraCambioPlano
		);

		// Propagamos hasta después de la maniobra
		SpacecraftState estadoFinal =
		        propagador.propagate(
		                fechaManiobra.shiftedBy(10.0)
		        );
		
		KeplerianOrbit orbitaFinal =
		        new KeplerianOrbit(
		                estadoFinal.getOrbit()
		        );

		System.out.println("Inclinación final según Orekit = "
		        + Math.toDegrees(orbitaFinal.getI())
		        + " grados");
		
		System.out.println("Semieje mayor inicial = "
		        + orbitaInicial.getA() / 1000.0 + " km");

		System.out.println("Semieje mayor final = "
		        + orbitaFinal.getA() / 1000.0 + " km");

		System.out.println("Excentricidad inicial = "
		        + orbitaInicial.getE());

		System.out.println("Excentricidad final = "
		        + orbitaFinal.getE());
		/***********************************************************/
		
		System.out.println("RAAN inicial = "
		        + Math.toDegrees(orbitaInicial.getRightAscensionOfAscendingNode())
		        + " grados");

		System.out.println("RAAN final = "
		        + Math.toDegrees(orbitaFinal.getRightAscensionOfAscendingNode())
		        + " grados");
		
		double n =
		        Math.sqrt(mu / Math.pow(r, 3));

		double anguloRecorrido =
		        n * 1.0;

		System.out.println("Movimiento angular en 1 segundo = "
		        + Math.toDegrees(anguloRecorrido)
		        + " grados");
		

	}

}
