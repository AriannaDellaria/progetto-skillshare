package it.unibo;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

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

    /**
     * Richiede il controllo dell'email al server.
     */
    void emailGiaRegistrata(
            String email,
            AsyncCallback<Boolean> callback
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
    void aggiornaProfilo(
            String idUtente,
            String nuovaBio,
            String fotoProfilo,
            List<String> tagCompetenze,
            AsyncCallback<Utente> callback
    );

    void trovaPerId(String id, AsyncCallback<Utente> callback);
}