package it.unibo;

import com.google.gwt.user.server.rpc.jakarta.RemoteServiceServlet;
import java.util.ArrayList;
import java.util.List;

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
        try {
            return gestoreRegistrazione.registra(
                    nomeUtente,
                    email,
                    password
            );
        } catch (IllegalArgumentException e) {
            return null;
        }
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

    /**
     * Aggiorna il profilo dell'utente sul server.
     */
    @Override
    public Utente aggiornaProfilo(
            String id,
            String bio,
            String fotoProfilo,
            List<String> tagCompetenze
    ) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        if (bio == null) {
            bio = "";
        }
        if (fotoProfilo == null) {
            fotoProfilo = "";
        }
        if (tagCompetenze == null) {
            tagCompetenze = new ArrayList<>();
        }

        return utenteRepository.aggiornaProfilo(
                id,
                bio.trim(),
                fotoProfilo.trim(),
                tagCompetenze
        );
    }

    @Override
    public Utente trovaPerId(String id) {
        return utenteRepository.trovaPerId(id);
    }

    /**
     * Controlla se l'email è già associata a un account.
     */
    @Override
    public boolean emailGiaRegistrata(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return utenteRepository.emailGiaRegistrata(
                email.trim()
        );
    }
}