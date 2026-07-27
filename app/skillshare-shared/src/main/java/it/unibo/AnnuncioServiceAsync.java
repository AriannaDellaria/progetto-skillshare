package it.unibo;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.List;

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

    /**
     * Recupera gli annunci dell'utente.
     */
    void mieiAnnunci(
            String idAutore,
            AsyncCallback<List<Annuncio>> callback
    );
}
