package com.roberto.orbit.propagacion;

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
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo29PropagacionRealista {

	public static void main(String[] args) {

		OrekitConfig.inicializar();

		double mu = Constants.WGS84_EARTH_MU;

		double a = 7000.0e3;
		double e = 0.001;
		double i = Math.toRadians(51.6);
		double omega = 0.0;
		double raan = 0.0;
		double ta = 0.0;

		TimeScale utc = TimeScalesFactory.getUTC();

		AbsoluteDate fechaInicial = new AbsoluteDate(2026, 10, 4, 12, 0, 0.0, utc);

		Frame eme2000 = FramesFactory.getEME2000();

		KeplerianOrbit orbitaInicial = new KeplerianOrbit(a, e, i, omega, raan, ta, PositionAngleType.TRUE, eme2000,
				fechaInicial, mu);

		System.out.println("Aceleración kepleriana inicial = " + mu / (a * a) + " m/s²");

//		Tolerancias para el Integrador
		double[][] tolerancias = NumericalPropagator.tolerances(1.0, // 1.0 Posición de tolerancia en metros
				orbitaInicial, OrbitType.KEPLERIAN);

//		INTEGRADOR 0.001 Paso mínimo  300 Paso máximo Tolerancias Absolutas  Tolerancias relativas
		DormandPrince853Integrator integrador = new DormandPrince853Integrator(0.001, 300.0, tolerancias[0],
				tolerancias[1]);

//		PROPAGADOR --> Va a gestionar la evolución del estado orbital
		NumericalPropagator propagador = new NumericalPropagator(integrador); // Definición del Propagador

		// estadoInicial será la órbita del satélite (dado por orbitaInicial)
		SpacecraftState estadoInicial = new SpacecraftState(orbitaInicial);

		propagador.setInitialState(estadoInicial); // Le pasamos el estadoInicial como dato de la órbita Inicial

		// |Orbit|-->
		// |SpacecraftState(OrbitaInicial)|-->|NumericalPropagator(DormandPrince853Integrator)|
		// EstadoInicial + Integrador ---> NumericalPropagator --> Estado futuro
		// + + Modelos de Fuerzas ----\

//		MODELO 1 (.) Atracción Newtoniana
		NewtonianAttraction gravedadCentral = new NewtonianAttraction(mu);
		propagador.addForceModel(gravedadCentral); // EstadoInicial + Integrador + ForceModel --> NumericalPropagator

		// MODELO 2: campo gravitatorio hasta grado 2
		Frame itrf = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
		NormalizedSphericalHarmonicsProvider gravedadJ2 = GravityFieldFactory.getNormalizedProvider(2, 0);
		HolmesFeatherstoneAttractionModel modeloJ2 = new HolmesFeatherstoneAttractionModel(itrf, gravedadJ2);
		propagador.addForceModel(modeloJ2);

/////////////////////////
/// // Aceleración perturbadora J2 en el instante inicial
		double aceleracionJ2 = modeloJ2.acceleration(estadoInicial, new double[] { gravedadJ2.getMu() }).getNorm();

		System.out.println("Aceleración J2 inicial = " + aceleracionJ2 + " m/s²");

		System.out.println("Relación J2 / Kepler = " + aceleracionJ2 / (mu / (a * a)));

		// Precesión secular del RAAN debida a J2
		double J2 = 1.08263e-3;
		double radioTierra = Constants.WGS84_EARTH_EQUATORIAL_RADIUS;

		double p = a * (1.0 - e * e);
		double n = Math.sqrt(mu / (a * a * a));

		double omegaPunto = -1.5 * n * J2 * Math.pow(radioTierra / p, 2) * Math.cos(i);

		double variacionRAAN = Math.toDegrees(omegaPunto * 3600.0);

		System.out.println("Variación teórica RAAN en 1 h = " + variacionRAAN + " grados");

/////////////////////////		
		// AHORA propagamos
		AbsoluteDate fechaFinal = fechaInicial.shiftedBy(3600.0); // Propagamos

		SpacecraftState estadoFinal = propagador.propagate(fechaFinal); // PROPAGANDO PROPAGANDO PROPAGANDO

//		IMPRESION DEL RESULTADO FINAL
		/*
		 * Órbita inicial → SpacecraftState → NumericalPropagator + Dormand-Prince +
		 * NewtonianAttraction → propagate(fechaFinal) → estadoFinal
		 */

		double radioFinal = estadoFinal.getPosition().getNorm();

		double velocidadFinal = estadoFinal.getPVCoordinates().getVelocity().getNorm();

		System.out.println("Radio final = " + radioFinal / 1000.0 + " km");
		System.out.println("Velocidad final = " + velocidadFinal / 1000.0 + " km/s");

//		COMPARACIÓN CON KEPLERIANA
		KeplerianOrbit orbitaKepler1h = orbitaInicial.shiftedBy(600.0);
//		IMPRESION DE RESULTADO DE FORMA KEPLERIANA
		double radioKepler1h = orbitaKepler1h.getPosition().getNorm();

		double velocidadKepler1h = orbitaKepler1h.getPVCoordinates().getVelocity().getNorm();

		System.out.println("Radio Kepler 1 h = " + radioKepler1h / 1000.0 + " km");
		System.out.println("Velocidad Kepler 1 h = " + velocidadKepler1h / 1000.0 + " km/s");

//     Diferencias
		System.out.println("Diferencia de radio = " + (radioFinal - radioKepler1h) + " m");
		System.out.println("Diferencia de velocidad = " + (velocidadFinal - velocidadKepler1h) + " m/s");
//		

		double separacion = estadoFinal.getPosition().subtract(orbitaKepler1h.getPosition()).getNorm();

		System.out.println("Separación entre posiciones = " + separacion + " m");

		// COMPARACiÓN
		KeplerianOrbit orbitaFinalJ2 = (KeplerianOrbit) OrbitType.KEPLERIAN.convertType(estadoFinal.getOrbit());
		System.out.println();
		System.out.println("---- ELEMENTOS ORBITALES TRAS 1 h CON J2 ----");

		System.out.println("a    = " + orbitaFinalJ2.getA() / 1000.0 + " km");
		System.out.println("e    = " + orbitaFinalJ2.getE());
		System.out.println("i    = " + Math.toDegrees(orbitaFinalJ2.getI()) + " grados");
		System.out.println("RAAN = " + Math.toDegrees(orbitaFinalJ2.getRightAscensionOfAscendingNode()) + " grados");
		System.out.println("omega= " + Math.toDegrees(orbitaFinalJ2.getPerigeeArgument()) + " grados");
		System.out.println("TA   = " + Math.toDegrees(orbitaFinalJ2.getTrueAnomaly()) + " grados");

//      TRAYECTORIA PERTURBADA = UNA FAMILIA CONTINUA DE ORBITAS KEPLERIANAS OSCULATORIAS
	}
}
