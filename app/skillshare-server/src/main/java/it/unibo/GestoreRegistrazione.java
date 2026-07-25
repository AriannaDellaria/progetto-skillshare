package it.unibo;

/**
 * Gestisce le regole applicative della registrazione.
 */
public class GestoreRegistrazione {

    private final UtenteRepository utenteRepository;

    /**
     * Crea il gestore con il repository degli utenti.
     */
    public GestoreRegistrazione(
            UtenteRepository utenteRepository
    ) {
        this.utenteRepository = utenteRepository;
    }

    /**
     * Registra un utente dopo aver validato i dati.
     */
    public Utente registra(
            String nomeUtente,
            String email,
            String password
    ) {
        if (nomeUtente == null
                || nomeUtente.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Il nome utente è obbligatorio."
            );
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "L'email è obbligatoria."
            );
        }

        if (!ValidatoreCredenziali.emailValida(email)) {
            throw new IllegalArgumentException(
                    "Formato email non valido."
            );
        }

        if (!ValidatoreCredenziali.passwordValida(password)) {
            throw new IllegalArgumentException(
                    ValidatoreCredenziali.requisitoPassword()
            );
        }

        return utenteRepository.registra(
                nomeUtente.trim(),
                email.trim(),
                password
        );
    }
}