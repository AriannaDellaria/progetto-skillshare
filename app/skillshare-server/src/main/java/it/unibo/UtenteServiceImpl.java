package it.unibo;

import com.google.gwt.user.server.rpc.jakarta.RemoteServiceServlet;

/**
 * Espone tramite RPC la registrazione degli utenti.
 */
public class UtenteServiceImpl extends RemoteServiceServlet implements UtenteService {

    private final UtenteRepository utenteRepository;
    private final GestoreRegistrazione gestoreRegistrazione;

    /**
     * Crea il servizio con il repository reale.
     */
    public UtenteServiceImpl() {
        this(new UtenteRepository());
    }

    /**
     * Permette di fornire un repository controllato nei test.
     */
    UtenteServiceImpl( UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
        this.gestoreRegistrazione = new GestoreRegistrazione(utenteRepository);
    }

    /**
     * Delega la registrazione alla logica applicativa.
     */
    @Override
    public Utente registra(
            String nomeUtente,
            String email,
            String password
    ) {
        return gestoreRegistrazione.registra(
                nomeUtente,
                email,
                password
        );
    }

    // Esegue il login controllando le credenziali nel repository.
    @Override
    public Utente login(String nomeUtente, String password) {
        if (nomeUtente == null
                || nomeUtente.trim().isEmpty()
                || password == null
                || password.isEmpty()) {
            return null;
        }

        return utenteRepository.login(
                nomeUtente.trim(),
                password
        );
    }
}