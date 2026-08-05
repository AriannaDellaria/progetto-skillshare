package it.unibo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta lo storico pubblico degli scambi conclusi di un
 * utente, distinto per prestazione e controprestazione, ciascuno
 * ordinato cronologicamente.
 */
public class StoricoUtente implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<VoceStorico> storicoPrestazione;
    private List<VoceStorico> storicoControprestazione;

    /**
     * Costruttore richiesto da GWT RPC.
     */
    public StoricoUtente() {
        this.storicoPrestazione = new ArrayList<>();
        this.storicoControprestazione = new ArrayList<>();
    }

    public StoricoUtente(
            List<VoceStorico> storicoPrestazione,
            List<VoceStorico> storicoControprestazione
    ) {
        this.storicoPrestazione = storicoPrestazione;
        this.storicoControprestazione = storicoControprestazione;
    }

    public List<VoceStorico> getStoricoPrestazione() {
        return storicoPrestazione;
    }

    public List<VoceStorico> getStoricoControprestazione() {
        return storicoControprestazione;
    }

    public void setStoricoPrestazione(List<VoceStorico> storicoPrestazione) {
        this.storicoPrestazione = storicoPrestazione;
    }

    public void setStoricoControprestazione(List<VoceStorico> storicoControprestazione) {
        this.storicoControprestazione = storicoControprestazione;
    }
}