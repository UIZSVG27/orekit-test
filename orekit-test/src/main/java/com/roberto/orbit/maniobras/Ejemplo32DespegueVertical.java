package com.roberto.orbit.maniobras;

import org.hipparchus.ode.OrdinaryDifferentialEquation;
import org.hipparchus.ode.ODEState;
import org.hipparchus.ode.ODEStateAndDerivative;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.hipparchus.ode.ExpandableODE;

public class Ejemplo32DespegueVertical {

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
//		
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



	}

// ------------------------------------------------------------------------------
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
			double dhdt = v;
			double dvdt = EMPUJE / m - MU / Math.pow(RT + h, 2);
			double dmdt = -EMPUJE / VE;

			return new double[] { dhdt, dvdt, dmdt };
		}
	}
}
