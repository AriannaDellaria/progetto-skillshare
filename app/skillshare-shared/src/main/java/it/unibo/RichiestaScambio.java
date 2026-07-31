package it.unibo;

import java.io.Serializable;

/**
 * Rappresenta una richiesta di scambio inviata da un utente
 * al proprietario di un annuncio.
 */
public class RichiestaScambio implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String idAnnuncio;
    private String idMittente;
    private String idProprietario;
    private String messaggio;
    private String titoloAnnuncio;
    private String nomeMittente;
    private long dataInvio;

    /**
     * Costruttore richiesto da GWT RPC.
     */
    public RichiestaScambio() {
    }

    /**
     * Crea una nuova richiesta di scambio.
     */
    public RichiestaScambio(
            String id,
            String idAnnuncio,
            String idMittente,
            String idProprietario,
            String messaggio,
            String titoloAnnuncio,
            String nomeMittente
    ) {
        this.id = id;
        this.idAnnuncio = idAnnuncio;
        this.idMittente = idMittente;
        this.idProprietario = idProprietario;
        this.messaggio = messaggio;
        this.titoloAnnuncio = titoloAnnuncio;
        this.nomeMittente = nomeMittente;
        this.dataInvio = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public String getIdAnnuncio() {
        return idAnnuncio;
    }

    public String getIdMittente() {
        return idMittente;
    }

    public String getIdProprietario() {
        return idProprietario;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public long getDataInvio() {
        return dataInvio;
    }

    public String getTitoloAnnuncio() {
        return titoloAnnuncio;
    }

    public String getNomeMittente() {
        return nomeMittente;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIdAnnuncio(String idAnnuncio) {
        this.idAnnuncio = idAnnuncio;
    }

    public void setIdMittente(String idMittente) {
        this.idMittente = idMittente;
    }

    public void setIdProprietario(String idProprietario) {
        this.idProprietario = idProprietario;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public void setTitoloAnnuncio(String titoloAnnuncio) {
        this.titoloAnnuncio = titoloAnnuncio;
    }

    public void setNomeMittente(String nomeMittente) {
        this.nomeMittente = nomeMittente;
    }

}