package com.roberto.orbit.maniobras;

public class TransferenciaHohmann {

	private double r1; //radio de la órbita inicial
    private double r2; //radio de la órbita final
    private double mu; //Parámtreo gravitacional

    private double deltaV1;
    private double deltaV2;
    private double deltaVTotal;
    private double tiempoTransferencia;
    
    private double aTransferencia;
    private double eTransferencia;
    
    public TransferenciaHohmann(double r1, double r2, double mu) {

        this.r1 = r1;
        this.r2 = r2;
        this.mu = mu;
    }
    /****************************/
    public void calcular() {

         aTransferencia =
                (r1 + r2) / 2.0; // at = (r1 + r2) /2
        
        
        double v1 =
                Math.sqrt(mu / r1); // v1 = (mu/r1)1/2
        
        double vt1 =
                Math.sqrt(mu * (2.0 / r1 - 1.0 / aTransferencia));  //vt1 = (mu* (2/r1)-(1/at)1/2
        
        deltaV1 = vt1 - v1;
        //
        double vt2 =
                Math.sqrt(mu * (2.0 / r2 - 1.0 / aTransferencia));
        
        double v2 =
                Math.sqrt(mu / r2); // v2 = (mu/r2)1/2
        
        deltaV2 = v2 - vt2;
        
        //deltaVTotal = deltaV1 + deltaV2;
        deltaVTotal =
                Math.abs(deltaV1) + Math.abs(deltaV2);
        
        tiempoTransferencia =
                Math.PI * Math.sqrt(
                        Math.pow(aTransferencia, 3) / mu
                );
        eTransferencia =
                Math.abs(r2 - r1) / (r1 + r2);
        
    /****************************/   
        
    }
    // Getters 
    public double getDeltaV1() {
        return deltaV1;
    }  
    public double getDeltaV2() {
        return deltaV2;
    }
    public double getDeltaVTotal() {
        return deltaVTotal;
    }
    public double getTiempoTransferencia() {
        return tiempoTransferencia;
    }
    public double getATransferencia() {
        return aTransferencia;
    
    }
    public double getETransferencia() {
        return eTransferencia;
    }
}
