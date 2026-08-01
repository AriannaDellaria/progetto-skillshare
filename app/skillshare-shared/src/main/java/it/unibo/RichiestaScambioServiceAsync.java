package it.unibo;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

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

    void richiesteRicevute(
            String idProprietario,
            AsyncCallback<List<RichiestaScambio>> callback
    );

    void richiesteInviate(
            String idMittente,
            AsyncCallback<List<RichiestaScambio>> callback
    );

    void accetta(
            String idRichiesta,
            String idUtenteCorrente,
            AsyncCallback<RichiestaScambio> callback
    );

    void rifiuta(
            String idRichiesta,
            String idUtenteCorrente,
            AsyncCallback<RichiestaScambio> callback
    );
}