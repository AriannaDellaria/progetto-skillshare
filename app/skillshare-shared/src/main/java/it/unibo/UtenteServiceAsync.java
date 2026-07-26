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

    // Invia al server la richiesta di accesso.
    void login(
            String nomeUtente,
            String password,
            AsyncCallback<Utente> callback
    );

    /**
     * Invia al server i nuovi dati del profilo.
     */
    void aggiornaBio(
            String idUtente,
            String nuovaBio,
            AsyncCallback<Utente> callback
    );
}