package it.unibo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

/**
 * Definisce il servizio RPC per la registrazione.
 */
@RemoteServiceRelativePath("utente")
public interface UtenteService extends RemoteService {

    /**
     * Registra un nuovo utente.
     */
    Utente registra(
            String nomeUtente,
            String email,
            String password
    ) throws IllegalArgumentException;

    /**
     * Controlla se l'email è già associata a un account.
     */
    boolean emailGiaRegistrata(String email);

    /**
     * Autentica un utente tramite nome utente e password.
     */
    Utente login(
            String nomeUtente,
            String password
    );

    /**
     * Richiede l'aggiornamento del profilo utente.
     */
    Utente aggiornaProfilo(
            String id,
            String bio,
            String fotoProfilo,
            List<String> tagCompetenze
    );
    /**
     * Recupera un utente tramite id, per scopi di visualizzazione.
     */
    Utente trovaPerId(String id);
}

