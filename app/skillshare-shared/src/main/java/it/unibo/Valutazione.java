package it.unibo;

import java.io.Serializable;

/**
 * Rappresenta la valutazione numerica assegnata da un utente
 * al creatore dell'annuncio al termine di uno scambio concluso.
 */
public class Valutazione implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Intervallo consentito per il punteggio.
     */
    public static final int PUNTEGGIO_MINIMO = 1;
    public static final int PUNTEGGIO_MASSIMO = 10;

    private String id;
    private String idRichiesta;
    private String idMittente;
    private String idUtenteValutato;
    private int punteggio;
    private TipoValutazione tipo;
    private long dataValutazione;

    /**
     * Costruttore richiesto da GWT RPC.
     */
    public Valutazione() {
    }

    /**
     * Crea una nuova valutazione.
     */
    public Valutazione(
            String id,
            String idRichiesta,
            String idMittente,
            String idUtenteValutato,
            int punteggio,
            TipoValutazione tipo
    ) {
        this.id = id;
        this.idRichiesta = idRichiesta;
        this.idMittente = idMittente;
        this.idUtenteValutato = idUtenteValutato;
        this.punteggio = punteggio;
        this.tipo = tipo;
        this.dataValutazione = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public String getIdRichiesta() {
        return idRichiesta;
    }

    public String getIdMittente() {
        return idMittente;
    }

    public String getIdUtenteValutato() {
        return idUtenteValutato;
    }

    public int getPunteggio() {
        return punteggio;
    }

    public TipoValutazione getTipo() {
        return tipo;
    }

    public long getDataValutazione() {
        return dataValutazione;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIdRichiesta(String idRichiesta) {
        this.idRichiesta = idRichiesta;
    }

    public void setIdMittente(String idMittente) {
        this.idMittente = idMittente;
    }

    public void setIdUtenteValutato(String idUtenteValutato) {
        this.idUtenteValutato = idUtenteValutato;
    }

    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }

    public void setTipo(TipoValutazione tipo) {
        this.tipo = tipo;
    }
}