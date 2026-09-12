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

public class Ejemplo24TransferenciaConCambioPlano {

	public static void main(String[] args) {
       /*                                 */
		OrekitConfig.inicializar();
		
		// Datos del problema
		double r1 = 7000.0e3;      // m
		double r2 = 14000.0e3;     // m

		double i1 = Math.toRadians(0.0);
		double i2 = Math.toRadians(30.0);

		double deltaI = i2 - i1;
		double mu = Constants.WGS84_EARTH_MU;
		/**************************************************************/
		AbsoluteDate fecha =
		        new AbsoluteDate(
		                2024, 1, 1,
		                12, 0, 0.0,
		                TimeScalesFactory.getUTC()
		        );

		Frame frame =
		        FramesFactory.getEME2000();

		KeplerianOrbit orbitaInicial =
		        new KeplerianOrbit(
		                r1,                         // a
		                0.0,                        // e
		                i1,                         // inclinación
		                0.0,                        // omega
		                0.0,                        // RAAN
		                0.0,                        // TA
		                PositionAngleType.TRUE,
		                frame,
		                fecha,
		                mu
		        );
		/***************************************************************/
				
		System.out.println("Radio inicial = "
		        + r1 / 1000.0 + " km");

		System.out.println("Radio final = "
		        + r2 / 1000.0 + " km");

		System.out.println("Cambio de inclinación = "
		        + Math.toDegrees(deltaI) + " grados");
		
		/*                          */
		TransferenciaHohmann hohmann =
		        new TransferenciaHohmann(r1, r2, mu);

		hohmann.calcular();
		
		// Dirección de la velocidad inicial
		Vector3D direccionVelocidadInicial =
		        orbitaInicial.getPVCoordinates()
		                     .getVelocity()
		                     .normalize();

		// Vector Delta-V del primer impulso de Hohmann
		Vector3D deltaV1Vector =
		        direccionVelocidadInicial
		        .scalarMultiply(hohmann.getDeltaV1());
		
		AbsoluteDate fechaPrimerImpulso =
		        fecha.shiftedBy(1.0);
		
		// Detector para ejecutar el primer impulso
		DateDetector detectorPrimerImpulso =
		        new DateDetector(fechaPrimerImpulso);
		
		// Primera maniobra impulsiva
		ImpulseManeuver maniobraPrimerImpulso =
		        new ImpulseManeuver(
		                detectorPrimerImpulso,
		                deltaV1Vector,
		                300.0
		        );
		
		// Propagador de la órbita inicial
		KeplerianPropagator propagador =
		        new KeplerianPropagator(orbitaInicial);
		
		// Añadimos la maniobra al propagador
		propagador.addEventDetector(maniobraPrimerImpulso);
		
		// Fecha prevista de llegada al apogeo
		AbsoluteDate fechaApogeo =
		        fechaPrimerImpulso.shiftedBy(
		                hohmann.getTiempoTransferencia()
		        );
		// Propagamos hasta el apogeo
		SpacecraftState estadoApogeo =
		        propagador.propagate(fechaApogeo);
		double radioApogeoOrekit =
		        estadoApogeo.getPosition().getNorm();

		System.out.println("Radio en apogeo calculado por Orekit = "
		        + radioApogeoOrekit / 1000.0 + " km");
		
		// Velocidad en el apogeo obtenida por Orekit
		Vector3D velocidadApogeoOrekit =
		        estadoApogeo.getPVCoordinates().getVelocity();

		double moduloVelocidadApogeoOrekit =
		        velocidadApogeoOrekit.getNorm();

		System.out.println("Velocidad en apogeo calculada por Orekit = "
		        + moduloVelocidadApogeoOrekit + " m/s");
		
		System.out.println("Vector velocidad en apogeo = "
		        + velocidadApogeoOrekit);
		
		// Dirección unitaria de la velocidad en el apogeo
		Vector3D direccionTangencial =
		        velocidadApogeoOrekit.normalize();

		System.out.println("Dirección tangencial = "
		        + direccionTangencial);
		
		Vector3D direccionZ = Vector3D.PLUS_K;

		System.out.println("Dirección Z = "
		        + direccionZ);
		
		Vector3D direccionFinal =
		        direccionTangencial
		                .scalarMultiply(Math.cos(deltaI))
		        .add(
		                direccionZ
		                        .scalarMultiply(Math.sin(deltaI))
		        );

		System.out.println("Dirección final = "
		        + direccionFinal);

		System.out.println("Módulo dirección final = "
		        + direccionFinal.getNorm());
		
		double velocidadCircularFinal =
		        Math.sqrt(mu / r2);
		
		Vector3D velocidadFinal =
		        direccionFinal.scalarMultiply(velocidadCircularFinal);

		System.out.println("Vector velocidad final = "
		        + velocidadFinal);

		System.out.println("Módulo velocidad final = "
		        + velocidadFinal.getNorm() + " m/s");
		
		
		/********************************************************/

		System.out.println("Módulo Delta-V1 vector Orekit = "
		        + deltaV1Vector.getNorm() + " m/s");
		
		System.out.println("Delta-V1 Hohmann = "
		        + hohmann.getDeltaV1() + " m/s");

		System.out.println("Delta-V2 Hohmann = "
		        + hohmann.getDeltaV2() + " m/s");

		System.out.println("Tiempo transferencia = "
		        + hohmann.getTiempoTransferencia() / 60.0
		        + " min");
		double aTransferencia =
		        hohmann.getATransferencia();

		double velocidadApogeoTransferencia =
		        Math.sqrt(
		                mu * (2.0 / r2 - 1.0 / aTransferencia)
		        );

		System.out.println("Velocidad en apogeo de transferencia = "
		        + velocidadApogeoTransferencia + " m/s");
		


		System.out.println("Velocidad circular final = "
		        + velocidadCircularFinal + " m/s");
		
		double deltaVCombinado =
		        Math.sqrt(
		                velocidadApogeoTransferencia * velocidadApogeoTransferencia
		                + velocidadCircularFinal * velocidadCircularFinal
		                - 2.0 * velocidadApogeoTransferencia
		                      * velocidadCircularFinal
		                      * Math.cos(deltaI)
		        );  /* incremento V= sqrt (Vt*Vt + v2*v2 -2*Vt*V2*(cos(incremento i))*/

		System.out.println("Delta-V combinado en apogeo = "
		        + deltaVCombinado + " m/s");
		
		/*                          */
		double deltaVCambioPlanoFinal =
		        2.0 * velocidadCircularFinal
		        * Math.sin(deltaI / 2.0);

		System.out.println("Delta-V cambio de plano por separado = "
		        + deltaVCambioPlanoFinal + " m/s");
		/*                         */
		double deltaVSeparado =
		        hohmann.getDeltaV2()
		        + deltaVCambioPlanoFinal;

		System.out.println("Delta-V total por separado en apogeo = "
		        + deltaVSeparado + " m/s");
		double ahorroDeltaV =
		        deltaVSeparado - deltaVCombinado;

		System.out.println("Ahorro al combinar las maniobras = "
		        + ahorroDeltaV + " m/s");
		
		/*******************************************/
		double deltaVTotalSeparado =
		        hohmann.getDeltaV1()
		        + deltaVSeparado;

		double deltaVTotalCombinado =
		        hohmann.getDeltaV1()
		        + deltaVCombinado;

		System.out.println("Delta-V TOTAL misión por separado = "
		        + deltaVTotalSeparado + " m/s");

		System.out.println("Delta-V TOTAL misión combinada = "
		        + deltaVTotalCombinado + " m/s");
		
		Vector3D deltaV2Vector =
		        velocidadFinal.subtract(velocidadApogeoOrekit);

		System.out.println("Módulo Delta-V2 combinado vectorial = "
		        + deltaV2Vector.getNorm() + " m/s");
		
		/*******************************************/
		double velocidadCircularInicial =
		        Math.sqrt(mu / r1);

		double deltaVCambioPlanoAbajo =
		        2.0 * velocidadCircularInicial
		        * Math.sin(deltaI / 2.0);

		System.out.println("Delta-V cambio de plano a 7000 km = "
		        + deltaVCambioPlanoAbajo + " m/s");
		
		// Detector para el segundo impulso
		AbsoluteDate fechaSegundoImpulso =
		        fechaApogeo.shiftedBy(1.0);

		DateDetector detectorSegundoImpulso =
		        new DateDetector(fechaSegundoImpulso);

		// Segunda maniobra: circularización + cambio de plano
		ImpulseManeuver maniobraSegundoImpulso =
		        new ImpulseManeuver(
		                detectorSegundoImpulso,
		                deltaV2Vector,
		                300.0
		        );
		
		// Nuevo propagador desde el apogeo
		KeplerianPropagator propagadorFinal =
		        new KeplerianPropagator(estadoApogeo.getOrbit());

		// Añadimos la segunda maniobra
		propagadorFinal.addEventDetector(maniobraSegundoImpulso);
		
		// Propagamos unos segundos después del segundo impulso
		AbsoluteDate fechaFinal =
		        fechaSegundoImpulso.shiftedBy(10.0);

		SpacecraftState estadoFinal =
		        propagadorFinal.propagate(fechaFinal);

		// Convertimos la órbita final a elementos keplerianos
		KeplerianOrbit orbitaFinal =
		        new KeplerianOrbit(estadoFinal.getOrbit());

		System.out.println("Semieje mayor final = "
		        + orbitaFinal.getA() / 1000.0 + " km");

		System.out.println("Excentricidad final = "
		        + orbitaFinal.getE());

		System.out.println("Inclinación final = "
		        + Math.toDegrees(orbitaFinal.getI()) + " grados");
		
	}

}
