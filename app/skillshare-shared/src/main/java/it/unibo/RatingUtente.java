package it.unibo;

import java.io.Serializable;

/**
 * Rappresenta il rating calcolato di un utente, distinto per
 * prestazione e controprestazione.
 */
public class RatingUtente implements Serializable {

    private static final long serialVersionUID = 1L;

    private double mediaPrestazione;
    private int numeroRecensioniPrestazione;
    private double mediaControprestazione;
    private int numeroRecensioniControprestazione;

    /**
     * Costruttore richiesto da GWT RPC.
     */
    public RatingUtente() {
    }

    public RatingUtente(
            double mediaPrestazione,
            int numeroRecensioniPrestazione,
            double mediaControprestazione,
            int numeroRecensioniControprestazione
    ) {
        this.mediaPrestazione = mediaPrestazione;
        this.numeroRecensioniPrestazione = numeroRecensioniPrestazione;
        this.mediaControprestazione = mediaControprestazione;
        this.numeroRecensioniControprestazione = numeroRecensioniControprestazione;
    }

    public double getMediaPrestazione() {
        return mediaPrestazione;
    }

    public int getNumeroRecensioniPrestazione() {
        return numeroRecensioniPrestazione;
    }

    public double getMediaControprestazione() {
        return mediaControprestazione;
    }

    public int getNumeroRecensioniControprestazione() {
        return numeroRecensioniControprestazione;
    }

    /**
     * Indica se l'utente ha ricevuto almeno una recensione di
     * prestazione.
     */
    public boolean haRecensioniPrestazione() {
        return numeroRecensioniPrestazione > 0;
    }

    /**
     * Indica se l'utente ha ricevuto almeno una recensione di
     * controprestazione.
     */
    public boolean haRecensioniControprestazione() {
        return numeroRecensioniControprestazione > 0;
    }

    public void setMediaPrestazione(double mediaPrestazione) {
        this.mediaPrestazione = mediaPrestazione;
    }

    public void setNumeroRecensioniPrestazione(int numeroRecensioniPrestazione) {
        this.numeroRecensioniPrestazione = numeroRecensioniPrestazione;
    }

    public void setMediaControprestazione(double mediaControprestazione) {
        this.mediaControprestazione = mediaControprestazione;
    }

    public void setNumeroRecensioniControprestazione(int numeroRecensioniControprestazione) {
        this.numeroRecensioniControprestazione = numeroRecensioniControprestazione;
    }
}