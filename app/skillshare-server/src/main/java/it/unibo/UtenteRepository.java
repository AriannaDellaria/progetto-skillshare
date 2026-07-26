package it.unibo;

import org.mapdb.DB;
import org.mapdb.Serializer;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * Gestisce la persistenza degli utenti.
 */
public class UtenteRepository {

    private static final DB db = DatabaseCore.getDB();

    private static final ConcurrentMap<String, Utente> utenti =
            db.hashMap(
                    "utenti",
                    Serializer.STRING,
                    Serializer.JAVA
            ).createOrOpen();

    /**
     * Registra un nuovo utente.
     */
    public Utente registra(
            String nomeUtente,
            String email,
            String password
    ) {
        String nomeUtentePulito = nomeUtente.trim();
        String emailPulita = email.trim();

        if (emailGiaRegistrata(emailPulita)) {
            throw new IllegalArgumentException(
                    "Email già registrata."
            );
        }

        if (nomeUtenteGiaRegistrato(nomeUtentePulito)) {
            throw new IllegalArgumentException(
                    "Nome utente già utilizzato."
            );
        }

        String id = UUID.randomUUID().toString();

        Utente utente = new Utente(
                id,
                nomeUtentePulito,
                emailPulita,
                password
        );

        // Salva il nuovo utente nel database.
        utenti.put(id, utente);
        DatabaseCore.commit();

        return utente;
    }

    /**
     * Cerca un utente tramite email.
     */
    public Utente trovaPerEmail(String email) {
        if (email == null) {
            return null;
        }

        for (Utente utente : utenti.values()) {
            if (utente.getEmail() != null
                    && utente.getEmail().equalsIgnoreCase(
                    email.trim()
            )) {
                return utente;
            }
        }

        return null;
    }

    /**
     * Cerca un utente tramite nome utente.
     */
    public Utente trovaPerNomeUtente(String nomeUtente) {
        if (nomeUtente == null) {
            return null;
        }

        for (Utente utente : utenti.values()) {
            if (utente.getNomeUtente() != null
                    && utente.getNomeUtente()
                    .equalsIgnoreCase(nomeUtente.trim())) {
                return utente;
            }
        }

        return null;
    }

    /**
     * Controlla se l'email è già registrata.
     */
    public boolean emailGiaRegistrata(String email) {
        return trovaPerEmail(email) != null;
    }

    /**
     * Controlla se il nome utente è già registrato.
     */
    public boolean nomeUtenteGiaRegistrato(
            String nomeUtente
    ) {
        return trovaPerNomeUtente(nomeUtente) != null;
    }

    /**
     *  Controlla nome utente e password per il login.
     */
    public Utente login(String nomeUtente, String password) {
        Utente utente = trovaPerNomeUtente(nomeUtente);

        if (utente == null || password == null) {
            return null;
        }

        if (!utente.getPassword().equals(password)) {
            return null;
        }

        return utente;
    }
}