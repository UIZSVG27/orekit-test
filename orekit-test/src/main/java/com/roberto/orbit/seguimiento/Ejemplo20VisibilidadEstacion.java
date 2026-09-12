package com.roberto.orbit.seguimiento;

import org.hipparchus.ode.events.Action;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.events.ElevationDetector;
import org.orekit.propagation.events.ElevationExtremumDetector;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo20VisibilidadEstacion {

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
		// 4. Propagador
		// ----------------------------------------------------

		KeplerianPropagator propagador =
		        new KeplerianPropagator(orbitaInicial);
		
		// ----------------------------------------------------
		// 5. Tierra
		// ----------------------------------------------------

		Frame itrf =
		        FramesFactory.getITRF(
		                IERSConventions.IERS_2010,
		                true
		        );

		OneAxisEllipsoid tierra =
		        new OneAxisEllipsoid(
		                Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
		                Constants.WGS84_EARTH_FLATTENING,
		                itrf
		        );
		
		// ----------------------------------------------------
		// 6. Estación terrestre: Madrid
		// ----------------------------------------------------

		GeodeticPoint madrid =
		        new GeodeticPoint(
		                Math.toRadians(40.4168),
		                Math.toRadians(-3.7038),
		                667.0
		        );

		TopocentricFrame estacionMadrid =
		        new TopocentricFrame(
		                tierra,
		                madrid,
		                "Madrid"
		        );
		
		// ----------------------------------------------------
		// 7. Detector de visibilidad
		// ----------------------------------------------------

		ElevationDetector detectorElevacion =
		        new ElevationDetector(estacionMadrid)
		            .withConstantElevation(0.0)
		            .withHandler((state, detector, increasing) -> {

		                if (increasing) {
		                    System.out.println(
		                            "APARECE SOBRE EL HORIZONTE - "
		                            + state.getDate()
		                    );
		                } else {
		                    System.out.println(
		                            "DESAPARECE BAJO EL HORIZONTE - "
		                            + state.getDate()
		                    );
		                }

		                return Action.CONTINUE;
		            });

		propagador.addEventDetector(detectorElevacion);		
		
		// ----------------------------------------------------
		// 8. Propagación durante 24 horas
		// ----------------------------------------------------
		ElevationExtremumDetector detectorMaxElevacion =
		        new ElevationExtremumDetector(estacionMadrid)
		            .withHandler((state, detector, increasing) -> {

		               if (!increasing) {

		                    double elevacion =
		                            estacionMadrid.getElevation(
		                                    state.getPosition(),
		                                    state.getFrame(),
		                                    state.getDate()
		                            );

		                    if (elevacion > 0.0) {

		                        System.out.println(
		                            "MAXIMA ELEVACION - "
		                            + state.getDate()
		                            + "  Elevacion = "
		                            + Math.toDegrees(elevacion)
		                            + " grados"
		                        );
		                }
		               }
		                return Action.CONTINUE;
		            });

		propagador.addEventDetector(detectorMaxElevacion);

		AbsoluteDate fechaFinal =
		        fechaInicial.shiftedBy(24.0 * 3600.0);

		propagador.propagate(fechaFinal);
		
	}

}
