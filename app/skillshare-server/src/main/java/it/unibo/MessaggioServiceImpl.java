package it.unibo;

import com.google.gwt.user.server.rpc.jakarta.RemoteServiceServlet;

import java.util.List;

/**
 * Esegue sul server le operazioni relative alla chat di uno scambio.
 */
public class MessaggioServiceImpl extends RemoteServiceServlet implements MessaggioService {

    private final MessaggioRepository messaggioRepository = new MessaggioRepository();
    private final RichiestaScambioRepository richiestaScambioRepository = new RichiestaScambioRepository();

    /**
     * Verifica che l'utente sia partecipante a uno scambio accettato
     * e restituisce la cronologia dei messaggi.
     */
    @Override
    public List<Messaggio> apriChat(String idRichiesta, String idUtenteCorrente) throws IllegalArgumentException {

        if (idRichiesta == null || idRichiesta.trim().isEmpty()) {
            throw new IllegalArgumentException("Richiesta non valida.");
        }

        if (idUtenteCorrente == null || idUtenteCorrente.trim().isEmpty()) {
            throw new IllegalArgumentException("Devi effettuare l'accesso per aprire la chat.");
        }

        RichiestaScambio richiesta = richiestaScambioRepository.trovaPerId(idRichiesta);

        if (richiesta == null) {
            throw new IllegalArgumentException("La richiesta selezionata non esiste più.");
        }

        boolean partecipa = idUtenteCorrente.equals(richiesta.getIdMittente())
                || idUtenteCorrente.equals(richiesta.getIdProprietario());

        if (!partecipa) {
            throw new IllegalArgumentException("Non sei autorizzato ad accedere a questa chat.");
        }

        if (richiesta.getStato() != StatoRichiesta.ACCETTATA) {
            throw new IllegalArgumentException("La chat è disponibile solo per scambi accettati.");
        }

        return messaggioRepository.trovaPerRichiesta(idRichiesta);
    }
}
