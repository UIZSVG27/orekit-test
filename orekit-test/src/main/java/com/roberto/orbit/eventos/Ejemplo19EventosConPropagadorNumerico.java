package com.roberto.orbit.eventos;

import org.hipparchus.ode.events.Action;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.OrbitType;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.NodeDetector;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo19EventosConPropagadorNumerico {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		OrekitConfig.inicializar();

		// ----------------------------------------------------
		// 1. Fecha inicial
		// ----------------------------------------------------

		TimeScale utc = TimeScalesFactory.getUTC();

		AbsoluteDate fechaInicial = new AbsoluteDate(
		        2024, 1, 1,
		        12, 0, 0.0,
		        utc
		);

		// ----------------------------------------------------
		// 2. Sistema de referencia
		// ----------------------------------------------------

		Frame eme2000 = FramesFactory.getEME2000();

		// ----------------------------------------------------
		// 3. Órbita inicial
		// ----------------------------------------------------

		double a = 10_000_000.0;   // m
		double e = 0.3;
		double i = Math.toRadians(51.6);

		double argumentoPerigeo = Math.toRadians(30.0);
		double raan = Math.toRadians(0.0);
		double TA = Math.toRadians(0.0);

		double mu = Constants.WGS84_EARTH_MU;

		KeplerianOrbit orbitaInicial =
		        new KeplerianOrbit(
		                a,
		                e,
		                i,
		                argumentoPerigeo,
		                raan,
		                TA,
		                PositionAngleType.TRUE,
		                eme2000,
		                fechaInicial,
		                mu
		        );
		// ----------------------------------------------------
		// 4. Integrador Dormand-Prince 853
		// ----------------------------------------------------

		double minStep = 0.1;
		double maxStep = 60.0;
		double positionTolerance = 0.1;

		double[][] tolerancias =
		        NumericalPropagator.tolerances(
		                positionTolerance,
		                orbitaInicial,
		                OrbitType.CARTESIAN
		        );

		DormandPrince853Integrator integrador =
		        new DormandPrince853Integrator(
		                minStep,
		                maxStep,
		                tolerancias[0],
		                tolerancias[1]
		        );
		
		NumericalPropagator propagador =
		        new NumericalPropagator(integrador);

		propagador.setOrbitType(OrbitType.CARTESIAN);

		propagador.setInitialState(
		        new SpacecraftState(orbitaInicial)
		);
		
		
		// ----------------------------------------------------
		// 5. Modelo gravitatorio terrestre con J2
		// ----------------------------------------------------

		NormalizedSphericalHarmonicsProvider gravityProvider =
		        GravityFieldFactory.getNormalizedProvider(2, 0);
		
		// ----------------------------------------------------
		// 6. Fuerza gravitatoria J2
		// ----------------------------------------------------

		HolmesFeatherstoneAttractionModel gravedadJ2 =
		        new HolmesFeatherstoneAttractionModel(
		                FramesFactory.getITRF(
		                        IERSConventions.IERS_2010,
		                        true
		                ),
		                gravityProvider
		        );

		propagador.addForceModel(gravedadJ2);
		
		NodeDetector detectorNodos =
		        new NodeDetector(
		                orbitaInicial,
		                eme2000
		        )
		        .withHandler((state, detector, increasing) -> {

		            if (increasing) {
		                System.out.println(
		                        "NODO ASCENDENTE  - "
		                        + state.getDate()
		                );
		            } else {
		                System.out.println(
		                        "NODO DESCENDENTE - "
		                        + state.getDate()
		                );
		            }

		            return Action.CONTINUE;
		        });

		propagador.addEventDetector(detectorNodos);
		
		// ----------------------------------------------------
		// 8. Propagación durante dos periodos
		// ----------------------------------------------------

		double periodo = orbitaInicial.getKeplerianPeriod();

		AbsoluteDate fechaFinal =
		        fechaInicial.shiftedBy(2.0 * periodo);

		propagador.propagate(fechaFinal);
		
	}

}
