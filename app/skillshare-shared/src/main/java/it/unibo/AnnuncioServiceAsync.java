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

    /**
     * Modifica un annuncio esistente dell'utente.
     */
    void modificaAnnuncio(
            String idAnnuncio,
            String idAutore,
            String titolo,
            String categoria,
            String descrizione,
            String disponibilita,
            String controprestazione,
            AsyncCallback<Annuncio> callback
    );

    /**
     * Richiede la rimozione di un annuncio.
     */
    void rimuovi(
            String idAnnuncio,
            String idAutore,
            AsyncCallback<Boolean> callback
    );
}
