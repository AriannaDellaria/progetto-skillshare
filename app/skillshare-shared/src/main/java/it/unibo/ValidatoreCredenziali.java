package it.unibo;

/**
 * Controlla la validità delle credenziali inserite dall'utente.
 */
public final class ValidatoreCredenziali {

    private static final int LUNGHEZZA_MINIMA_PASSWORD = 7;

    /**
     * Impedisce la creazione di istanze della classe.
     */
    private ValidatoreCredenziali() {
    }

    /**
     * Controlla che la password rispetti i requisiti minimi.
     */
    public static boolean passwordValida(String password) {
        if (password == null
                || password.length() < LUNGHEZZA_MINIMA_PASSWORD) {
            return false;
        }

        // Cerca almeno un carattere speciale.
        for (int i = 0; i < password.length(); i++) {
            char carattere = password.charAt(i);

            if (!Character.isLetterOrDigit(carattere)
                    && !Character.isWhitespace(carattere)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Controlla che l'indirizzo email abbia un formato valido.
     */
    public static boolean emailValida(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return email.trim().matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
        );
    }

    /**
     * Restituisce i requisiti richiesti per la password.
     */
    public static String requisitoPassword() {
        return "La password deve contenere almeno 7 caratteri "
                + "e almeno un carattere speciale.";
    }
}