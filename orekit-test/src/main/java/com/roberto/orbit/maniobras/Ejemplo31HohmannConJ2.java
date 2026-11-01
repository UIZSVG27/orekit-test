package com.roberto.orbit.maniobras;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.ode.events.Action;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.orekit.forces.gravity.NewtonianAttraction;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.OrbitType;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.ApsideDetector;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo31HohmannConJ2 {

	public static void main(String[] args) {

		// 1. Inicialización de Orekit
		OrekitConfig.inicializar();

		// 2. Parámetro gravitatorio terrestre
		double mu = Constants.WGS84_EARTH_MU;
		
		// 3. Radios de las órbitas circulares
		double r1 = 7000.0e3;
		double r2 = 14000.0e3;

		// 4. Transferencia de Hohmann
		TransferenciaHohmann hohmann = new TransferenciaHohmann(r1, r2, mu);
		hohmann.calcular();

		// 5. Elementos de la órbita de transferencia
		double a = hohmann.getATransferencia();
		double e = hohmann.getETransferencia();
		double tiempoTransferencia = hohmann.getTiempoTransferencia();
		
		System.out.println("r1 = " + r1);
		System.out.println("r2 = " + r2);
		System.out.println("a = " + a);
		System.out.println("e = " + e);
		System.out.println("Tiempo = " + tiempoTransferencia);
		Frame frame = FramesFactory.getEME2000();

		// 7. Fecha inicial
		TimeScale utc = TimeScalesFactory.getUTC();

		AbsoluteDate fechaInicial = new AbsoluteDate(2026, 10, 25, 12, 0, 0.0, utc);

		// 8. Órbita inicial de transferencia
		KeplerianOrbit orbitaTransferencia = new KeplerianOrbit(a, e, 0.0, // Inclinación
				0.0, // Argumento del perigeo
				0.0, // RAAN
				0.0, // TA inicial
				PositionAngleType.TRUE, frame, fechaInicial, mu);
//		--------------------------------------------------------------------------------------
		// 9. Tolerancias del integrador
		double[][] tolerancias = NumericalPropagator.tolerances(1.0, orbitaTransferencia, OrbitType.CARTESIAN);

		// 10. Integrador Dormand-Prince 853
		DormandPrince853Integrator integradorKepler = new DormandPrince853Integrator(0.001, 300.0, tolerancias[0],
				tolerancias[1]);

		// 11. Propagador numérico kepleriano
		NumericalPropagator propagadorKepler = new NumericalPropagator(integradorKepler);

		propagadorKepler.setOrbitType(OrbitType.CARTESIAN);

		// 12. Estado inicial
		SpacecraftState estadoInicial = new SpacecraftState(orbitaTransferencia);
		ApsideDetector detectorApogeo =
		        new ApsideDetector(orbitaTransferencia)
		            .withHandler((state, detector, increasing) -> {
		                if (!increasing) {
		                    return Action.STOP;      // Apogeo
		                }
		                return Action.CONTINUE;      // Perigeo
		            });

		propagadorKepler.setInitialState(estadoInicial);

		// 13. Modelo de gravedad central
		NewtonianAttraction gravedadCentral = new NewtonianAttraction(mu);

		propagadorKepler.addForceModel(gravedadCentral);
//		--------------------------------------------------------------------------------------------------
		
		// 14. Integrador independiente para el modelo con J2
		DormandPrince853Integrator integradorJ2 = new DormandPrince853Integrator(0.001, 300.0, tolerancias[0],
				tolerancias[1]);

		// 15. Segundo propagador numérico
		NumericalPropagator propagadorJ2 = new NumericalPropagator(integradorJ2);

		propagadorJ2.setOrbitType(OrbitType.CARTESIAN);

		// Ambos parten exactamente del mismo estado inicial
		propagadorJ2.setInitialState(estadoInicial);
		// 16. Gravedad central para el segundo propagador
		propagadorJ2.addForceModel(new NewtonianAttraction(mu));

		// 17. Sistema de referencia ligado a la Tierra
		Frame itrf = FramesFactory.getITRF(IERSConventions.IERS_2010, true);

		// 18. Modelo gravitatorio con J2
		NormalizedSphericalHarmonicsProvider gravedadJ2 = GravityFieldFactory.getNormalizedProvider(2, 0);

		HolmesFeatherstoneAttractionModel modeloJ2 = new HolmesFeatherstoneAttractionModel(itrf, gravedadJ2);
//		--------------------------------------------------------------------------------------------------
		// Aceleración kepleriana inicial
		double aceleracionKepler = mu / (r1 * r1);

		// Aceleración perturbadora inicial
		double aceleracionJ2 = modeloJ2.acceleration(estadoInicial, new double[] { gravedadJ2.getMu() }).getNorm();

		// Relación entre ambas aceleraciones
		double relacion = aceleracionJ2 / aceleracionKepler;

		System.out.println("Aceleracion Kepler = " + aceleracionKepler + " m/s2");

		System.out.println("Aceleracion J2 = " + aceleracionJ2 + " m/s2");

		System.out.println("Relacion J2 / Kepler = " + relacion);
//		----------------------------------------------------------------------------------------------------

		// 19. Añadir la perturbación
		propagadorJ2.addForceModel(modeloJ2);
//		--------------------------------------PROPAGAMOS AMBOS MODELOS
		// 20. Instante final de la transferencia kepleriana
		AbsoluteDate fechaFinal = fechaInicial.shiftedBy(tiempoTransferencia);

		// 21. Propagación con gravedad central
		SpacecraftState estadoFinalKepler = propagadorKepler.propagate(fechaFinal);

		// 22. Propagación con gravedad central + J2
		SpacecraftState estadoFinalJ2 = propagadorJ2.propagate(fechaFinal);

		// 23. Radios orbitales finales
		double radioKepler = estadoFinalKepler.getPosition().getNorm();

		double radioJ2 = estadoFinalJ2.getPosition().getNorm();

		// 24. Separación entre las posiciones
		double separacion = estadoFinalJ2.getPosition().subtract(estadoFinalKepler.getPosition()).getNorm();

		// 25. Resultados
		System.out.println("Tiempo de transferencia: " + tiempoTransferencia / 60.0 + " min");

		System.out.println("Radio final Kepler: " + radioKepler / 1000.0 + " km");

		System.out.println("Radio final J2: " + radioJ2 / 1000.0 + " km");

		System.out.println("Diferencia de radios: " + (radioJ2 - radioKepler) + " m");

		System.out.println("Separacion entre posiciones: " + separacion + " m");

		double velocidadKepler = estadoFinalKepler.getPVCoordinates().getVelocity().getNorm();

		double velocidadJ2 = estadoFinalJ2.getPVCoordinates().getVelocity().getNorm();

		System.out.println("Velocidad final Kepler = " + velocidadKepler + " m/s");

		System.out.println("Velocidad final J2 = " + velocidadJ2 + " m/s");
		
		double productoEscalarJ2 = estadoFinalJ2.getPVCoordinates().getPosition()
				.dotProduct(estadoFinalJ2.getPVCoordinates().getVelocity());

		System.out.println("Producto escalar r·v con J2 = "
		        + productoEscalarJ2 + " m²/s");
		
		// Reiniciar la propagación desde el estado inicial
		propagadorJ2.resetInitialState(estadoInicial);

		// Registrar el detector
		propagadorJ2.addEventDetector(detectorApogeo);

		// Buscar el apogeo en un intervalo suficientemente largo
		AbsoluteDate fechaLimite = fechaInicial.shiftedBy(6000.0);

		SpacecraftState estadoApogeoJ2 = propagadorJ2.propagate(fechaLimite);

		// Tiempo transcurrido hasta el apogeo
		double tiempoApogeoJ2 = estadoApogeoJ2.getDate().durationFrom(fechaInicial);

		System.out.println("Tiempo apogeo J2 = " + tiempoApogeoJ2 / 60.0 + " min");

		System.out.println("Diferencia respecto a Hohmann = " + (tiempoApogeoJ2 - tiempoTransferencia) + " s");

		double radioApogeoJ2 = estadoApogeoJ2.getPosition().getNorm();

		System.out.println("Radio del apogeo con J2 = " + radioApogeoJ2 / 1000.0 + " km");

		System.out.println("Diferencia respecto a 14000 km = " + (radioApogeoJ2 - r2) + " m");
		
		// Energía kepleriana específica inicial
		double energiaInicial = estadoInicial.getPVCoordinates().getVelocity().getNormSq() / 2.0
				- mu / estadoInicial.getPosition().getNorm();

		// Energía kepleriana específica en el apogeo con J2
		double energiaApogeo = estadoApogeoJ2.getPVCoordinates().getVelocity().getNormSq() / 2.0
				- mu / estadoApogeoJ2.getPosition().getNorm();

		System.out.println("Energia inicial = " + energiaInicial + " J/kg");

		System.out.println("Energia en apogeo J2 = " + energiaApogeo + " J/kg");

		System.out.println("Variacion energia kepleriana = " + (energiaApogeo - energiaInicial) + " J/kg");
//		--------------------------------------------------------------------------------------------------
		// Constantes del modelo J2
		double J2 = 1.08263e-3;
		double radioTierra = Constants.WGS84_EARTH_EQUATORIAL_RADIUS;

		// Potencial perturbador J2 inicial
		double potencialJ2Inicial = -mu * J2 * radioTierra * radioTierra / (2.0 * Math.pow(r1, 3));

		// Potencial perturbador J2 en el apogeo
		double potencialJ2Apogeo = -mu * J2 * radioTierra * radioTierra / (2.0 * Math.pow(radioApogeoJ2, 3));

		// Energías totales
		double energiaTotalInicial = energiaInicial + potencialJ2Inicial;

		double energiaTotalApogeo = energiaApogeo + potencialJ2Apogeo;

		System.out.println("Energia total inicial = " + energiaTotalInicial + " J/kg");

		System.out.println("Energia total apogeo = " + energiaTotalApogeo + " J/kg");

		System.out.println("Variacion energia total = " + (energiaTotalApogeo - energiaTotalInicial) + " J/kg");
//   -----------------------------------------------------------------
		// 26. Momento angular específico inicial
		Vector3D hInicial = estadoInicial.getPVCoordinates().getMomentum();

		// 27. Momento angular específico en el apogeo con J2
		Vector3D hApogeo = estadoApogeoJ2.getPVCoordinates().getMomentum();

		// 28. Comparación
		System.out.println("Momento angular inicial = " + hInicial.getNorm() + " m2/s");

		System.out.println("Momento angular apogeo J2 = " + hApogeo.getNorm() + " m2/s");

		System.out.println("Variacion relativa del momento angular = "
				+ (hApogeo.getNorm() - hInicial.getNorm()) / hInicial.getNorm());
		
	}

}
