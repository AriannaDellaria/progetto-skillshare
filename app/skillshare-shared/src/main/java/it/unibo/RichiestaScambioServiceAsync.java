package it.unibo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Versione asincrona del servizio richieste di scambio.
 */
public interface RichiestaScambioServiceAsync {

    /**
     * Invia al server la richiesta di scambio.
     */
    void invia(
            String idAnnuncio,
            String idMittente,
            String messaggio,
            AsyncCallback<RichiestaScambio> callback
    );
}