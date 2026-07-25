package it.unibo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

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
}