package it.unibo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Versione asincrona del servizio annunci.
 */
public interface AnnuncioServiceAsync {
    /**
     * Invia al server la richiesta di creazione.
     */
    void crea(
            String idAutore,
            String titolo,
            String descrizione,
            String categoria,
            String disponibilita,
            String controprestazione,
            AsyncCallback<Annuncio> callback
    );
}
