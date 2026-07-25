package it.unibo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Versione asincrona del servizio utenti.
 */
public interface UtenteServiceAsync {

    /**
     * Invia al server la richiesta di registrazione.
     */
    void registra(
            String nomeUtente,
            String email,
            String password,
            AsyncCallback<Utente> callback
    );
}