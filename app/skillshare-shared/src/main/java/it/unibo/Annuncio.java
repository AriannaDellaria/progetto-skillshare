package it.unibo;

import java.io.Serializable;

/**
 * Rappresenta un annuncio pubblicato sulla piattaforma.
 */
public class Annuncio implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String idAutore;
    private String titolo;
    private String descrizione;
    private String categoria;
    private String disponibilita;
    private String controprestazione;
    private boolean attivo;
    private long dataPubblicazione;

    /**
     * Costruttore richiesto da GWT RPC.
     */
    public Annuncio() {
    }

    /**
     * Crea un nuovo annuncio.
     */
    public Annuncio(
            String id,
            String idAutore,
            String titolo,
            String descrizione,
            String categoria,
            String disponibilita,
            String controprestazione
    ) {
        this.id = id;
        this.idAutore = idAutore;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.categoria = categoria;
        this.disponibilita = disponibilita;
        this.controprestazione = controprestazione;
        this.attivo = true;
        this.dataPubblicazione = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }
    public String getIdAutore() {
        return idAutore;
    }
    public String getTitolo() {
        return titolo;
    }
    public String getDescrizione() {
        return descrizione;
    }
    public String getCategoria() {
        return categoria;
    }
    public String getDisponibilita() {
        return disponibilita;
    }
    public String getControprestazione() {
        return controprestazione;
    }
    public boolean isAttivo() {
        return attivo;
    }
    public long getDataPubblicazione() {
        return dataPubblicazione;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setIdAutore(String idAutore) {
        this.idAutore = idAutore;
    }
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public void setDisponibilita(String disponibilita) {
        this.disponibilita = disponibilita;
    }
    public void setControprestazione(String controprestazione) {
        this.controprestazione = controprestazione;
    }
    public void setAttivo(boolean attivo) {
        this.attivo = attivo;
    }
}
