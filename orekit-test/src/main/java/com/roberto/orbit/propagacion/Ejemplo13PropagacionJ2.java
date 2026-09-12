package com.roberto.orbit.propagacion;

import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.UnnormalizedSphericalHarmonicsProvider;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.EcksteinHechlerPropagator;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo13PropagacionJ2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

        // ----------------------------------------------------
        // 1. Inicialización de Orekit
        // ----------------------------------------------------

        OrekitConfig.inicializar();

        // ----------------------------------------------------
        // 2. Fecha inicial
        // ----------------------------------------------------

        TimeScale utc = TimeScalesFactory.getUTC();

        AbsoluteDate fechaInicial = new AbsoluteDate(
            2024, 1, 1,
            12, 0, 0.0,
            utc
        );

        // ----------------------------------------------------
        // 3. Sistemas de referencia
        // ----------------------------------------------------

        Frame eme2000 = FramesFactory.getEME2000();

 

        // ----------------------------------------------------
        // 4. Elementos orbitales
        // ----------------------------------------------------

        double a = 10_000_000.0;//7_000_000.0;
        double e = 0.001; //0.001;
        double i = Math.toRadians(51.6);

        double argumentoPerigeo = Math.toRadians(0.0);
        double raan = Math.toRadians(0.0);
        double anomalia = Math.toRadians(0.0);

        double mu = Constants.WGS84_EARTH_MU;
        UnnormalizedSphericalHarmonicsProvider gravityProvider =
                GravityFieldFactory.getUnnormalizedProvider(6, 0);
        

        // ----------------------------------------------------
        // 5. Crear la órbita en EME2000
        // ----------------------------------------------------

        KeplerianOrbit orbita = new KeplerianOrbit(
            a,
            e,
            i,
            argumentoPerigeo,
            raan,
            anomalia,
            PositionAngleType.TRUE,//PositionAngleType.TRUE,
            eme2000,
            fechaInicial,
            mu
        );
        /*
         Propagador
         */
        double dt = 3600.0; 
        AbsoluteDate fechaFutura =
        	    fechaInicial.shiftedBy(dt);
        
        
     // Kepler puro
        KeplerianPropagator propagador =
                new KeplerianPropagator(orbita);

        SpacecraftState estado =
                propagador.propagate(fechaFutura);


        // Tierra no esférica
        EcksteinHechlerPropagator propagadorJ2 =
                new EcksteinHechlerPropagator(
                        orbita,
                        gravityProvider
                );

        SpacecraftState estadoJ2 =
                propagadorJ2.propagate(fechaFutura);


        // Recuperar las órbitas propagadas
        KeplerianOrbit orbitaKepler =
                (KeplerianOrbit) estado.getOrbit();

        KeplerianOrbit orbitaJ2 =
                new KeplerianOrbit(estadoJ2.getOrbit());
        
        System.out.println();
        System.out.println("COMPARACION DESPUES DE 3600 s");
        System.out.println("-----------------------------");

        System.out.println(
                "RAAN Kepler = "
                + Math.toDegrees(
                        orbitaKepler.getRightAscensionOfAscendingNode()
                )
                + " grados"
        );

        System.out.println(
                "RAAN J2 = "
                + Math.toDegrees(
                        orbitaJ2.getRightAscensionOfAscendingNode()
                )
                + " grados"
        );

        System.out.println(
                "w Kepler = "
                + Math.toDegrees(
                        orbitaKepler.getPerigeeArgument()
                )
                + " grados"
        );

        System.out.println(
                "w J2 = "
                + Math.toDegrees(
                        orbitaJ2.getPerigeeArgument()
                )
                + " grados"
        );
        /*
         * Precesión Nodal
         */
        System.out.println();
        System.out.println("EVOLUCION DEL RAAN");
        System.out.println("------------------");
        System.out.println("Dia       RAAN Kepler       RAAN J2");

        for (int dia = 0; dia <= 7; dia++) {

            double tiempo = dia * 86400.0;

            AbsoluteDate fecha =
                    fechaInicial.shiftedBy(tiempo);

            SpacecraftState estadoKeplerDia =
                    propagador.propagate(fecha);

            SpacecraftState estadoJ2Dia =
                    propagadorJ2.propagate(fecha);

            KeplerianOrbit orbitaKeplerDia =
                    new KeplerianOrbit(estadoKeplerDia.getOrbit());

            KeplerianOrbit orbitaJ2Dia =
                    new KeplerianOrbit(estadoJ2Dia.getOrbit());

            double raanKeplerDia =
                    Math.toDegrees(
                            orbitaKeplerDia.getRightAscensionOfAscendingNode()
                    );

            double raanJ2Dia =
                    Math.toDegrees(
                            orbitaJ2Dia.getRightAscensionOfAscendingNode()
                    );

            System.out.printf(
                    "%2d        %10.6f       %10.6f%n",
                    dia,
                    raanKeplerDia,
                    raanJ2Dia
            );
        }
        /*
         * Comprobación teórica de la precesión nodal debida a J2
         */

        double J2 = 1.08263e-3;

        double radioTierra =
                Constants.WGS84_EARTH_EQUATORIAL_RADIUS;

        // Movimiento medio
        double nMovimiento =
                Math.sqrt(mu / (a * a * a));

        // Parámetro p
        double p =
                a * (1.0 - e * e);

        // Variación del RAAN en rad/s
        double raanPunto =
                -1.5
                * J2
                * nMovimiento
                * Math.pow(radioTierra / p, 2)
                * Math.cos(i);

        // Convertimos de rad/s a grados/día
        double raanGradosDia =
                Math.toDegrees(raanPunto)
                * 86400.0;

        System.out.println();
        System.out.println("PREDICCION TEORICA J2");
        System.out.println("---------------------");

        System.out.println(
                "dRAAN/dt = "
                + raanGradosDia
                + " grados/dia"
        );
        
	}

}
