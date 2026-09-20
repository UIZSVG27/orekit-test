package com.roberto.orbit.maniobras;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.forces.maneuvers.ImpulseManeuver;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.events.DateDetector;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

import com.roberto.orbit.util.OrekitConfig;

public class Ejemplo26EncuentroOrbital {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        OrekitConfig.inicializar();

        //--------- Radio común de las dos órbitas circulares------------------
        double radio = 7000.0e3;  // m

        // Parámetro gravitacional terrestre
        double mu = Constants.WGS84_EARTH_MU;

        System.out.println("Radio orbital = "
                + radio / 1000.0 + " km");
        
     //-------------- Fecha inicial de los dos satélites-----------------------
        TimeScale utc = TimeScalesFactory.getUTC();

        AbsoluteDate fechaInicial = new AbsoluteDate(
                2024, 1, 1,
                12, 0, 0.0,
                utc
        );

        // Sistema de referencia inercial
        Frame eme2000 = FramesFactory.getEME2000();
        
     //-------- Anomalías verdaderas iniciales--------------------------------
        double taA = Math.toRadians(0.0);
        double taB = Math.toRadians(30.0);

        // Órbita circular del satélite A
        KeplerianOrbit orbitaA = new KeplerianOrbit(
                radio, 0.0, 0.0,
                0.0, 0.0, taA,
                PositionAngleType.TRUE,
                eme2000, fechaInicial, mu
        );   
        
        // Órbita circular del satélite B
        KeplerianOrbit orbitaB = new KeplerianOrbit(
                radio, 0.0, 0.0,
                0.0, 0.0, taB,
                PositionAngleType.TRUE,
                eme2000, fechaInicial, mu
        );   
        
     // --------------------Posiciones iniciales de los satélites-----------S
        Vector3D posicionA =
                orbitaA.getPVCoordinates().getPosition();

        Vector3D posicionB =
                orbitaB.getPVCoordinates().getPosition();

        System.out.println("Posición inicial A = " + posicionA);
        System.out.println("Posición inicial B = " + posicionB);
        
     // -----Vector desde el satélite A hasta el satélite B EN LINEA RECTA!!!!-------
        Vector3D vectorAB = posicionB.subtract(posicionA);

        // Distancia entre los satélites
        double distanciaAB = vectorAB.getNorm();

        System.out.println("Vector de A hacia B = " + vectorAB);

        System.out.println("Distancia inicial A-B = "
                + distanciaAB / 1000.0 + " km");
        
     // -------Propagación de ambos satélites durante una hora (DEBERÍA SER LA MISMA)-------
        double dt = 3600.0;  // segundos

        KeplerianOrbit orbitaA1h =
                new KeplerianOrbit(orbitaA.shiftedBy(dt));

        KeplerianOrbit orbitaB1h =
                new KeplerianOrbit(orbitaB.shiftedBy(dt));

        // Posiciones después de una hora
        Vector3D posicionA1h =
                orbitaA1h.getPVCoordinates().getPosition();

        Vector3D posicionB1h =
                orbitaB1h.getPVCoordinates().getPosition();

        // Distancia entre ambos satélites
        double distanciaAB1h =
                posicionB1h.subtract(posicionA1h).getNorm();

        System.out.println("Distancia A-B después de una hora = "
                + distanciaAB1h / 1000.0 + " km");  
        
     // -----------------Período de la órbita circular inicial (Cuanto tarda A y B en un periodo)-------
        double periodoInicial = orbitaA.getKeplerianPeriod();

        System.out.println("Período orbital inicial = "
                + periodoInicial / 60.0 + " minutos");

     // -----Período necesario para la órbita de fase- RECORRE A MIENTRA B 360----------------
     // Número de vueltas de A en la órbita de fase
        int numeroVueltas = 2;

        double periodoFase =
                periodoInicial *
                (1.0 - 30.0 / (360.0 * numeroVueltas));

        System.out.println("Período de la órbita de fase = "
                + periodoFase / 60.0 + " minutos");   
        
     // Semieje mayor de la órbita de fase  a= (mu/ (T/2*pi)exp 2 )exp 3
        double aFase = Math.cbrt(
                mu * Math.pow(periodoFase / (2.0 * Math.PI), 2)
        );

        System.out.println("Semieje mayor de la órbita de fase = "
                + aFase / 1000.0 + " km");   
        
     // Perigeo de la órbita de fase NUEVA
        double radioPerigeo = 2.0 * aFase - radio;

        System.out.println("Radio del perigeo = "
                + radioPerigeo / 1000.0 + " km");

        System.out.println("Altura del perigeo = "
                + (radioPerigeo - Constants.WGS84_EARTH_EQUATORIAL_RADIUS)
                / 1000.0 + " km");
        
        
     // Velocidad circular inicial (FRENAR para en dos orbitas de Fase coincidir con B)
        double velocidadCircular = Math.sqrt(mu / radio);

        // Velocidad en el apogeo de la órbita de fase
        double velocidadFase = Math.sqrt(
                mu * (2.0 / radio - 1.0 / aFase)
        );

        // Primer impulso: reducción de velocidad
        double deltaV1 = velocidadFase - velocidadCircular;

        System.out.println("Velocidad circular = "
                + velocidadCircular + " m/s");

        System.out.println("Velocidad inicial de fase = "
                + velocidadFase + " m/s");

        System.out.println("Delta-V1 = "
                + deltaV1 + " m/s");   
        
     //--------- Dirección de la velocidad inicial de A Dirección del primer Impulso
        Vector3D direccionVelocidadA =
                orbitaA.getPVCoordinates()
                      .getVelocity()
                      .normalize();

        // Vector del primer impulso
        Vector3D deltaV1Vector =
                direccionVelocidadA.scalarMultiply(deltaV1);

        System.out.println("Vector Delta-V1 = "
                + deltaV1Vector);

        System.out.println("Módulo Delta-V1 = "
                + deltaV1Vector.getNorm() + " m/s");
        
     // -------------Primer impulso en la fecha inicial para coger la velocidad necesaria (en el mismo t
     // que le corresponde. Ni 1 seg después   
        AbsoluteDate fechaPrimerImpulso =
                fechaInicial.shiftedBy(1.0);

        DateDetector detectorPrimerImpulso =
                new DateDetector(fechaPrimerImpulso);
        
        ImpulseManeuver maniobraPrimerImpulso =
                new ImpulseManeuver(
                        detectorPrimerImpulso,
                        deltaV1Vector,
                        300.0
                );

        // Propagador del satélite A
        KeplerianPropagator propagadorA =
                new KeplerianPropagator(orbitaA);

        // Incorporamos la maniobra al propagador
        propagadorA.addEventDetector(maniobraPrimerImpulso);   
        
     // ------Propagamos A durante un minuto COMPROBAR HA ENTRADO EN LA NUEVA ORBITA
        AbsoluteDate fechaComprobacion =
                fechaInicial.shiftedBy(60.0);

        SpacecraftState estadoA =
                propagadorA.propagate(fechaComprobacion);

        // Órbita obtenida después del primer impulso
        KeplerianOrbit orbitaFaseOrekit =
                new KeplerianOrbit(estadoA.getOrbit());

        System.out.println("Semieje mayor teórico = "
                + aFase / 1000.0 + " km");

        System.out.println("Semieje mayor Orekit = "
                + orbitaFaseOrekit.getA() / 1000.0 + " km");

        System.out.println("Período de fase Orekit = "
                + orbitaFaseOrekit.getKeplerianPeriod() / 60.0
                + " minutos");   
        
     // ------------------Tiempo de encuentro: dos vueltas de A en la órbita de fase
        double tiempoEncuentro =
                numeroVueltas * periodoFase;

        // Fecha prevista del encuentro
        AbsoluteDate fechaEncuentro =
                fechaPrimerImpulso.shiftedBy(tiempoEncuentro);

        System.out.println("Tiempo hasta el encuentro = "
                + tiempoEncuentro / 60.0 + " minutos");  
        
     // -----------Estado de A en la fecha prevista del encuentro (CON PROPAGADOR)
        SpacecraftState estadoEncuentroA =
                propagadorA.propagate(fechaEncuentro);

        // Posición de A
        Vector3D posicionEncuentroA =
                estadoEncuentroA.getPVCoordinates().getPosition();

        // Posición de B en la misma fecha
        Vector3D posicionEncuentroB =
                orbitaB.shiftedBy(
                        fechaEncuentro.durationFrom(fechaInicial)
                ).getPVCoordinates().getPosition();

        // Distancia entre ambos satélites
        double distanciaEncuentro =
                posicionEncuentroB.subtract(posicionEncuentroA)
                                 .getNorm();

        System.out.println("Distancia A-B en el encuentro = "
                + distanciaEncuentro / 1000.0 + " km");
     // VELOCIDAD PARA EL ENCUENTRO
        
     // Velocidad de A en el encuentro
        Vector3D velocidadEncuentroA =
                estadoEncuentroA.getPVCoordinates()
                               .getVelocity();

        // Velocidad de B en la misma fecha
        Vector3D velocidadEncuentroB =
                orbitaB.shiftedBy(
                        fechaEncuentro.durationFrom(fechaInicial)
                ).getPVCoordinates().getVelocity();

        // Diferencia de velocidades: B menos A
        Vector3D diferenciaVelocidades =
                velocidadEncuentroB.subtract(velocidadEncuentroA);

        System.out.println("Velocidad de A = "
                + velocidadEncuentroA.getNorm() + " m/s");

        System.out.println("Velocidad de B = "
                + velocidadEncuentroB.getNorm() + " m/s");

        System.out.println("Velocidad relativa A-B = "
                + diferenciaVelocidades.getNorm() + " m/s"); 
        
      //PREPARAR EL SEGUNDO IMPULSO
     // Segundo impulso: igualar la velocidad de A con la de B
        Vector3D deltaV2Vector = diferenciaVelocidades;

        System.out.println("Vector Delta-V2 = "
                + deltaV2Vector);

        System.out.println("Módulo Delta-V2 = "
                + deltaV2Vector.getNorm() + " m/s");  
     
     // Segundo impulso: un segundo después del encuentro
     //   AbsoluteDate fechaSegundoImpulso =
     //           fechaEncuentro.shiftedBy(1.0);
        
     // Segundo impulso: exactamente en la fecha prevista del encuentro
        AbsoluteDate fechaSegundoImpulso = fechaEncuentro;
        

        // Detector de la fecha del segundo impulso
        DateDetector detectorSegundoImpulso =
                new DateDetector(fechaSegundoImpulso);

        // Maniobra para recuperar la velocidad circular
        ImpulseManeuver maniobraSegundoImpulso =
                new ImpulseManeuver(
                        detectorSegundoImpulso,
                        deltaV2Vector,
                        300.0
                );

        // Nuevo propagador desde el estado de A en el encuentro
        //KeplerianPropagator propagadorFinal =
        //       new KeplerianPropagator(
        //               estadoEncuentroA.getOrbit()
        //      );
        
     // Nuevo propagador desde la órbita inicial de A.
     // Comenzará antes de los dos impulsos.
     KeplerianPropagator propagadorFinal =
             new KeplerianPropagator(orbitaA);

     // Reproducimos el primer impulso para entrar en la órbita de fase.
     propagadorFinal.addEventDetector(maniobraPrimerImpulso);

        // Incorporamos el segundo impulso
        propagadorFinal.addEventDetector(maniobraSegundoImpulso);
        
        //--------------------------------
     // Propagamos hasta 10 segundos después del segundo impulso
        AbsoluteDate fechaFinal =
                fechaSegundoImpulso.shiftedBy(10.0);

        SpacecraftState estadoFinal =
                propagadorFinal.propagate(fechaFinal);

        // Obtenemos los elementos de la órbita final
        KeplerianOrbit orbitaFinal =
                new KeplerianOrbit(estadoFinal.getOrbit());

        System.out.println("Semieje mayor final = "
                + orbitaFinal.getA() / 1000.0 + " km");

        System.out.println("Excentricidad final = "
                + orbitaFinal.getE());

        System.out.println("Período orbital final = "
                + orbitaFinal.getKeplerianPeriod() / 60.0
                + " minutos");
        //La posición y la velocidad de ambos satélites en una misma fecha, después del segundo impulso
        
     // Estado de B en la misma fecha final
        KeplerianOrbit orbitaBFinal =
                new KeplerianOrbit(
                        orbitaB.shiftedBy(
                                fechaFinal.durationFrom(fechaInicial)
                        )
                );

        // Distancia entre A y B
        double distanciaFinal =
                estadoFinal.getPVCoordinates().getPosition()
                .subtract(orbitaBFinal.getPVCoordinates().getPosition())
                .getNorm();

        // Velocidad relativa entre A y B
        double velocidadRelativaFinal =
                estadoFinal.getPVCoordinates().getVelocity()
                .subtract(orbitaBFinal.getPVCoordinates().getVelocity())
                .getNorm();

        System.out.println("Distancia final A-B = "
                + distanciaFinal + " m");

        System.out.println("Velocidad relativa final A-B = "
                + velocidadRelativaFinal + " m/s"); 

           //////////////////////////////////////////////////////////////
     // ------------------------------------------------------------
     // RESUMEN: COMPARACIÓN ENTRE LOS EJEMPLOS 25 Y 26
     // ------------------------------------------------------------

     // Resultado que obtuvimos en el Ejemplo 25 (segundo impulso
     // aplicado un segundo después de la fecha del encuentro).
     double distanciaEjemplo25 = 111.14154036038978; // m

     System.out.println();
     System.out.println("========== RESUMEN DEL ENCUENTRO ==========");

     System.out.println("Distancia final en el Ejemplo 25 = "
             + distanciaEjemplo25 + " m");

     System.out.println("Distancia final en el Ejemplo 26 = "
             + distanciaFinal + " m");

     System.out.println("Reducción de la distancia final = "
             + (distanciaEjemplo25 - distanciaFinal) + " m");

     System.out.println("Velocidad relativa final en el Ejemplo 26 = "
             + velocidadRelativaFinal + " m/s");

     System.out.println("=========================================="); 
           
	}

}
