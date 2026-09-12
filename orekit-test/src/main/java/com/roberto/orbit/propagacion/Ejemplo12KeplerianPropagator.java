package com.roberto.orbit.propagacion;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.PVCoordinates;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo12KeplerianPropagator {

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
        double e = 0.3; //0.001;
        double i = Math.toRadians(51.6);

        double argumentoPerigeo = Math.toRadians(0.0);
        double raan = Math.toRadians(0.0);
        double anomalia = Math.toRadians(0.0);

        double mu = Constants.WGS84_EARTH_MU;

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
        ///////////////////////////
        double dt = 3600.0; 
        AbsoluteDate fechaFutura =
        	    fechaInicial.shiftedBy(dt);
        
        KeplerianPropagator propagador =
        	    new KeplerianPropagator(orbita);
        SpacecraftState estado =
        	    propagador.propagate(fechaFutura);
        
        System.out.println("Fecha inicial = " + fechaInicial);
        System.out.println("Fecha propagada = " + estado.getDate());
        
        PVCoordinates pvEstado =
                estado.getPVCoordinates();

        Vector3D rEstado = pvEstado.getPosition();
        Vector3D vEstado = pvEstado.getVelocity();
        
        System.out.println(
                "r = " + rEstado
        );

        System.out.println(
                "v = " + vEstado
        );
        //////////////////////////////////////////////////////////
        KeplerianOrbit orbitaShifted =
                (KeplerianOrbit) orbita.shiftedBy(dt);

        PVCoordinates pvShifted =
                orbitaShifted.getPVCoordinates();

        Vector3D rShifted = pvShifted.getPosition();
        Vector3D vShifted = pvShifted.getVelocity();

        System.out.println();
        System.out.println("COMPARACION CON shiftedBy");
        System.out.println("-------------------------");

        System.out.println("r shiftedBy = " + rShifted);
        System.out.println("v shiftedBy = " + vShifted);
        /////////////////////////////////////////////////////
        KeplerianOrbit orbitaPropagada =
                (KeplerianOrbit) estado.getOrbit();
        System.out.println();
        System.out.println("ELEMENTOS ORBITALES DESPUES DE 3600 s");
        System.out.println("------------------------------------");

        System.out.println(
                "a = " + orbitaPropagada.getA() / 1000.0 + " km"
        );

        System.out.println(
                "e = " + orbitaPropagada.getE()
        );

        System.out.println(
                "i = " + Math.toDegrees(orbitaPropagada.getI()) + " grados"
        );

        System.out.println(
                "RAAN = "
                + Math.toDegrees(orbitaPropagada.getRightAscensionOfAscendingNode())
                + " grados"
        );

        System.out.println(
                "Argumento perigeo = "
                + Math.toDegrees(orbitaPropagada.getPerigeeArgument())
                + " grados"
        );

        System.out.println(
                "TA = "
                + Math.toDegrees(orbitaPropagada.getTrueAnomaly())
                + " grados"
        );
        ///////////////////////////////////////////////////////
        System.out.println();
        System.out.println("EVOLUCION KEPLERIANA");
        System.out.println("--------------------");
        System.out.println("Hora    a(km)      e        i(deg)    RAAN(deg)   w(deg)     TA(deg)");

        for (int hora = 0; hora <= 6; hora++) {

            AbsoluteDate fecha =
                    fechaInicial.shiftedBy(hora * 3600.0);

            SpacecraftState estadoHora =
                    propagador.propagate(fecha);

            KeplerianOrbit orbitaHora =
                    (KeplerianOrbit) estadoHora.getOrbit();

            System.out.printf(
                "%3d   %9.3f   %.6f   %8.3f   %9.3f   %8.3f   %9.3f%n",
                hora,
                orbitaHora.getA() / 1000.0,
                orbitaHora.getE(),
                Math.toDegrees(orbitaHora.getI()),
                Math.toDegrees(orbitaHora.getRightAscensionOfAscendingNode()),
                Math.toDegrees(orbitaHora.getPerigeeArgument()),
                Math.toDegrees(orbitaHora.getTrueAnomaly())
            );
        }
        
	}

}
