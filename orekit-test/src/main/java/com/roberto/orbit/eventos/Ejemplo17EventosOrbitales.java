package com.roberto.orbit.eventos;

import org.hipparchus.ode.events.Action;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.events.ApsideDetector;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo17EventosOrbitales {

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

		double argumentoPerigeo = Math.toRadians(0.0);
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
		// 4. Propagador kepleriano
		// ----------------------------------------------------

		KeplerianPropagator propagador =
		        new KeplerianPropagator(orbitaInicial);
		
		// ----------------------------------------------------
		// 5. Detector de ápsides
		// ----------------------------------------------------

		/*ApsideDetector detectorApsides =
		        new ApsideDetector(orbitaInicial)
		                .withHandler(new StopOnEvent());*/
		
		/*ApsideDetector detectorApsides =
		        new ApsideDetector(orbitaInicial)
		                .withHandler(new ContinueOnEvent());*/
		
		ApsideDetector detectorApsides =
		        new ApsideDetector(orbitaInicial)
		        .withHandler((state, detector, increasing) -> {

		            KeplerianOrbit orbitaEvento =
		                    new KeplerianOrbit(state.getOrbit());

		            double taEvento =
		                    Math.toDegrees(
		                            orbitaEvento.getTrueAnomaly()
		                    );

		            if (increasing) {
		                System.out.println(
		                        "PERIGEO  - "
		                        + state.getDate()
		                        + "   TA = "
		                        + taEvento
		                        + " grados"
		                );
		            } else {
		                System.out.println(
		                        "APOGEO   - "
		                        + state.getDate()
		                        + "   TA = "
		                        + taEvento
		                        + " grados"
		                );
		            }

		            return Action.CONTINUE;
		        });
		
		propagador.addEventDetector(detectorApsides);
		
		double periodo = orbitaInicial.getKeplerianPeriod();

		AbsoluteDate fechaFinal =
		        fechaInicial.shiftedBy(2.0 * periodo);

		/*SpacecraftState estadoEvento =
		        propagador.propagate(fechaFinal);*/
		
		SpacecraftState estadoFinal =
		        propagador.propagate(fechaFinal);
		
		KeplerianOrbit orbitaFinal =
		        new KeplerianOrbit(estadoFinal.getOrbit());

		System.out.println("Fecha del evento = " + estadoFinal.getDate());
		System.out.println(
		        "TA del evento = "
		        + Math.toDegrees(orbitaFinal.getTrueAnomaly())
		        + " grados"
		);
		
		
	}

}
