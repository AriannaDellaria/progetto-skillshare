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
        validaAccessoChat(idRichiesta, idUtenteCorrente);
        return messaggioRepository.trovaPerRichiesta(idRichiesta);
    }

    /**
     * Invia un nuovo messaggio nella chat, dopo aver verificato
     * che l'utente sia autorizzato e che il testo non sia vuoto.
     */
    @Override
    public Messaggio invia(String idRichiesta, String idMittente, String testo) throws IllegalArgumentException {

        validaAccessoChat(idRichiesta, idMittente);

        if (testo == null || testo.trim().isEmpty()) {
            throw new IllegalArgumentException("Il testo del messaggio è obbligatorio.");
        }

        return messaggioRepository.crea(idRichiesta, idMittente, testo.trim());
    }

    /**
     * Condivide un link nella chat. Consentito solo al proprietario
     * dell'annuncio, e solo se il link è valido.
     */
    @Override
    public Messaggio condividiLink(String idRichiesta, String idUtenteCorrente, String url) throws IllegalArgumentException {

        RichiestaScambio richiesta = validaAccessoChat(idRichiesta, idUtenteCorrente);

        if (!idUtenteCorrente.equals(richiesta.getIdProprietario())) {
            throw new IllegalArgumentException("Solo chi ha pubblicato l'annuncio può condividere un link.");
        }

        if (!ValidatoreLink.linkValido(url)) {
            throw new IllegalArgumentException("Il link inserito non è valido.");
        }

        return messaggioRepository.crea(idRichiesta, idUtenteCorrente, url.trim(), true);
    }

    private RichiestaScambio validaAccessoChat(String idRichiesta, String idUtenteCorrente) {

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
        return richiesta;
    }
}
