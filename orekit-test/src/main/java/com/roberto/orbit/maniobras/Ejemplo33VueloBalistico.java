package com.roberto.orbit.maniobras;

import org.hipparchus.analysis.UnivariateFunction;
import org.hipparchus.analysis.solvers.BracketedUnivariateSolver;
import org.hipparchus.analysis.solvers.BracketingNthOrderBrentSolver;
import org.hipparchus.ode.ExpandableODE;
import org.hipparchus.ode.ODEState;
import org.hipparchus.ode.ODEStateAndDerivative;
import org.hipparchus.ode.OrdinaryDifferentialEquation;
import org.hipparchus.ode.events.Action;
import org.hipparchus.ode.events.AdaptableInterval;
import org.hipparchus.ode.events.ODEEventDetector;
import org.hipparchus.ode.events.ODEEventHandler;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;

public class Ejemplo33VueloBalistico {

	// Parámetros físicos del cohete
	private static final double MASA_INICIAL = 100_000.0; // kg
	private static final double MASA_SECA = 40_000.0; // kg
	private static final double EMPUJE = 1_500_000.0; // N
	private static final double VE = 3_000.0; // m/s

	// Parámetros terrestres
	private static final double RT = 6_378_137.0; // m
	private static final double MU = 3.986004418e14; // m³/s²

	public static void main(String[] args) {

		// Estado inicial del cohete
		double h0 = 0.0; // Altura inicial (m)
		double v0 = 0.0; // Velocidad inicial (m/s)
		double m0 = MASA_INICIAL; // Masa inicial (kg)
		double[] y0 = { h0, v0, m0 };

		// Estado inicial para Hipparchus
		ODEState estadoInicial = new ODEState(0.0, y0);

		// Integrador Dormand-Prince 853
		double pasoMinimo = 0.001;
		double pasoMaximo = 10.0;
		double toleranciaAbsoluta = 1.0e-8;
		double toleranciaRelativa = 1.0e-8;

		DormandPrince853Integrator integrador = new DormandPrince853Integrator(pasoMinimo, pasoMaximo,
				toleranciaAbsoluta, toleranciaRelativa);
		// Sistema de ecuaciones diferenciales
		EcuacionesCohete ecuaciones = new EcuacionesCohete();
		ExpandableODE sistema = new ExpandableODE(ecuaciones);

		// Tiempo final: agotamiento del combustible
		double tiempoFinal = (MASA_INICIAL - MASA_SECA) * VE / EMPUJE;

		// Integración numérica
		ODEStateAndDerivative estadoFinal = integrador.integrate(sistema, estadoInicial, tiempoFinal);

		// Recuperamos el estado final
		double[] yf = estadoFinal.getPrimaryState();

		System.out.println("Tiempo final: " + estadoFinal.getTime() + " s");
		System.out.println("Altura final: " + yf[0] / 1000.0 + " km");
		System.out.println("Velocidad final: " + yf[1] + " m/s");
		System.out.println("Masa final: " + yf[2] + " kg");

		// ----------- SEGUNDA FASE: VUELO BALÍSTICO -----------

		// Recuperamos el estado al apagar el motor
		double[] estadoApagado = estadoFinal.getPrimaryState();

		// Creamos el estado inicial de la segunda fase
		ODEState estadoInicialBalistico = new ODEState(estadoFinal.getTime(), estadoApagado);

		// Definimos las ecuaciones del vuelo sin motor
		EcuacionesVueloBalistico ecuacionesBalisticas = new EcuacionesVueloBalistico();
		

		

		ExpandableODE sistemaBalistico = new ExpandableODE(ecuacionesBalisticas);

		// Creamos otro integrador con las mismas tolerancias
		DormandPrince853Integrator integradorBalistico = new DormandPrince853Integrator(pasoMinimo, pasoMaximo,
				toleranciaAbsoluta, toleranciaRelativa);
		
		// Aquí registraremos el detector de altura máxima
		integradorBalistico.addEventDetector(
			    new DetectorAlturaMaxima()
			);
		
		
		// Integramos provisionalmente otros 150 segundos
		double tiempoFinalBalistico = tiempoFinal + 300.0;

		ODEStateAndDerivative estadoBalistico = integradorBalistico.integrate(sistemaBalistico, estadoInicialBalistico,
				tiempoFinalBalistico);

		// Mostramos los resultados
		double[] yb = estadoBalistico.getPrimaryState();

		System.out.println("\n--- VUELO BALÍSTICO ---");
		System.out.println("Tiempo: " + estadoBalistico.getTime() + " s");
		System.out.println("Altura: " + yb[0] / 1000.0 + " km");
		System.out.println("Velocidad: " + yb[1] + " m/s");
		System.out.println("Masa: " + yb[2] + " kg");

	}

// -----------CLASE DE LAS ECUACIONES DIFERENCIALES EN VUELO DE ASCENSO---
	private static class EcuacionesCohete implements OrdinaryDifferentialEquation {

		@Override
		public int getDimension() {
			return 3;
		}

		@Override
		public double[] computeDerivatives(double t, double[] y) {

			// Extraemos las variables de estado
			double h = y[0];
			double v = y[1];
			double m = y[2];

			// Calculamos las derivadas
			double dhdt = v; // V
			double dvdt = EMPUJE / m - MU / Math.pow(RT + h, 2); // F/m - mu/(Rt+ h)2
			double dmdt = -EMPUJE / VE; // -F/ve

			return new double[] { dhdt, dvdt, dmdt };
		}
	}

// -----------CLASE DE LAS ECUACIONES DIFERENCIALES EN VUELO BALISTICO---
	private static class EcuacionesVueloBalistico implements OrdinaryDifferentialEquation {

		@Override
		public int getDimension() {
			return 3;
		}

		@Override
		public double[] computeDerivatives(double t, double[] y) {

			double h = y[0];
			double v = y[1];

			double dhdt = v;
			double dvdt = -MU / Math.pow(RT + h, 2);
			double dmdt = 0.0;

			return new double[] { dhdt, dvdt, dmdt };
		}
	}
//	
	private static class DetectorAlturaMaxima
    implements ODEEventDetector {

	@Override
	public AdaptableInterval getMaxCheckInterval() {
		// TODO Auto-generated method stub
		return state -> 10.0;
	}

	@Override
	public int getMaxIterationCount() {
		// TODO Auto-generated method stub
		return 100;
	}

	@Override
	public BracketedUnivariateSolver<UnivariateFunction> getSolver() {
		// TODO Auto-generated method stub
		return new BracketingNthOrderBrentSolver(1.0e-6, 5);
	}

	@Override
	public ODEEventHandler getHandler() {
	    return (state, detector, increasing) -> Action.STOP;
	}
	
	@Override
	public double g(ODEStateAndDerivative state) {

	    // La velocidad vertical es la segunda variable de estado
	    double velocidad = state.getPrimaryState()[1];

	    return velocidad;
	}

}
	
}
