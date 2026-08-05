package it.unibo;

import java.io.Serializable;

/**
 * Rappresenta una singola voce dello storico pubblico degli scambi
 * di un utente.
 */
public class VoceStorico implements Serializable {

    private static final long serialVersionUID = 1L;

    private String testo;
    private int punteggio;
    private String commento;
    private long data;

    /**
     * Costruttore richiesto da GWT RPC.
     */
    public VoceStorico() {
    }

    public VoceStorico(String testo, int punteggio, String commento, long data) {
        this.testo = testo;
        this.punteggio = punteggio;
        this.commento = commento;
        this.data = data;
    }

    public String getTesto() {
        return testo;
    }

    public int getPunteggio() {
        return punteggio;
    }

    public String getCommento() {
        return commento;
    }

    public long getData() {
        return data;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }

    public void setData(long data) {
        this.data = data;
    }
}