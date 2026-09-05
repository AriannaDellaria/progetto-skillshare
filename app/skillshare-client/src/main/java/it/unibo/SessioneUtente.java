package it.unibo;

/**
 * Mantiene l'utente autenticato durante l'utilizzo dell'app.
 */
public final class SessioneUtente {

    private static Utente utenteCorrente;

    private SessioneUtente() {
    }

    // Salva l'utente autenticato.
    public static void accedi(Utente utente) {
        utenteCorrente = utente;
    }

    // Restituisce l'utente autenticato.
    public static Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    // Chiude la sessione.
    public static void esci() {
        utenteCorrente = null;
    }
}