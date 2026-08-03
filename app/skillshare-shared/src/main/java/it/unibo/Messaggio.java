package it.unibo;

import java.io.Serializable;

/**
 * Rappresenta un messaggio scambiato nella chat di una richiesta
 * di scambio accettata.
 */
public class Messaggio implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String idRichiesta;
    private String idMittente;
    private String testo;
    private boolean isLink;
    private long dataInvio;

    /**
     * Costruttore richiesto da GWT RPC.
     */
    public Messaggio() {
    }

    /**
     * Crea un nuovo messaggio.
     */
    public Messaggio(
            String id,
            String idRichiesta,
            String idMittente,
            String testo
    ) {
        this.id = id;
        this.idRichiesta = idRichiesta;
        this.idMittente = idMittente;
        this.testo = testo;
        this.dataInvio = System.currentTimeMillis();
    }

    public Messaggio(
            String id,
            String idRichiesta,
            String idMittente,
            String testo,
            boolean isLink
    ) {
        this.id = id;
        this.idRichiesta = idRichiesta;
        this.idMittente = idMittente;
        this.testo = testo;
        this.isLink = isLink;
        this.dataInvio = System.currentTimeMillis();
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

    public String getTesto() {
        return testo;
    }

    public long getDataInvio() {
        return dataInvio;
    }

    public boolean isLink() {
        return isLink;
    }

    public void setLink(boolean link) {
        isLink = link;
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

    public void setTesto(String testo) {
        this.testo = testo;
    }
}