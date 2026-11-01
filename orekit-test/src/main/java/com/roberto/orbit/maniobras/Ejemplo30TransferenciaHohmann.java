package com.roberto.orbit.maniobras;

import org.orekit.utils.Constants;
import org.orekit.propagation.SpacecraftState;
import org.orekit.forces.gravity.NewtonianAttraction;

import com.roberto.orbit.util.OrekitConfig;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.orekit.orbits.OrbitType;
import org.orekit.propagation.numerical.NumericalPropagator;


public class Ejemplo30TransferenciaHohmann {

	public static void main(String[] args) {
		
		OrekitConfig.inicializar();

		double r1 = 7000.0e3;
		double r2 = 14000.0e3;
		double mu = Constants.WGS84_EARTH_MU;
		TransferenciaHohmann hohmann = new TransferenciaHohmann(r1, r2, mu);
		hohmann.calcular();
		System.out.println("Semieje mayor = " + hohmann.getATransferencia() / 1000.0 + " km");
		System.out.println("Excentricidad = " + hohmann.getETransferencia());
		System.out.println("Tiempo transferencia = " + hohmann.getTiempoTransferencia() / 60.0 + " min");
        //		
		// Órbita kepleriana de transferencia
		Frame frame = FramesFactory.getEME2000();

		AbsoluteDate fechaInicial = new AbsoluteDate(2026, 10, 25, 12, 0, 0.0, TimeScalesFactory.getUTC());

		KeplerianOrbit orbitaTransferencia = new KeplerianOrbit(hohmann.getATransferencia(),
				hohmann.getETransferencia(), 0.0, // inclinación
				0.0, // argumento del perigeo
				0.0, // RAAN
				0.0, // TA inicial: perigeo
				PositionAngleType.TRUE, frame, fechaInicial, mu);
		System.out.println("Radio inicial = " + orbitaTransferencia.getPosition().getNorm() / 1000.0 + " km");

		System.out.println(
				"Velocidad inicial = " + orbitaTransferencia.getPVCoordinates().getVelocity().getNorm() + " m/s");
		
        //Ahora con DP853		
		// Tolerancias para la integración numérica
		double[][] tolerancias = NumericalPropagator.tolerances(1.0, orbitaTransferencia, OrbitType.CARTESIAN);

		// Integrador Dormand-Prince 8(5,3)
		DormandPrince853Integrator integrador = new DormandPrince853Integrator(0.001, 300.0, tolerancias[0],
				tolerancias[1]);

		// Propagador numérico
		NumericalPropagator propagador = new NumericalPropagator(integrador);
		propagador.setOrbitType(OrbitType.CARTESIAN);

		// Estado inicial del satélite
		SpacecraftState estadoInicial = new SpacecraftState(orbitaTransferencia);

		propagador.setInitialState(estadoInicial);

		// Gravedad central kepleriana
		NewtonianAttraction gravedadCentral = new NewtonianAttraction(mu);

		propagador.addForceModel(gravedadCentral);
		// Tiempo necesario para llegar al apogeo
		double tiempoTransferencia = hohmann.getTiempoTransferencia();

		AbsoluteDate fechaFinal = fechaInicial.shiftedBy(tiempoTransferencia);

		// Propagación numérica con DP853
		SpacecraftState estadoFinal = propagador.propagate(fechaFinal);

		// Radio alcanzado
		double radioFinal = estadoFinal.getPosition().getNorm();

		System.out.println("Radio final DP853 = " + radioFinal / 1000.0 + " km");
		// Solución kepleriana analítica
		KeplerianOrbit orbitaAnalitica = orbitaTransferencia.shiftedBy(tiempoTransferencia);

		// Separación entre las posiciones finales
		double separacion = estadoFinal.getPosition().subtract(orbitaAnalitica.getPosition()).getNorm();

		System.out.println("Separación DP853 - Kepler = " + separacion + " m");
	}

}
