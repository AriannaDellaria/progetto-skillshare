package it.unibo;

import com.google.gwt.user.server.rpc.jakarta.RemoteServiceServlet;

import java.util.ArrayList;
import java.util.List;

/**
 * Esegue sul server le operazioni relative alle richieste di scambio.
 */
public class RichiestaScambioServiceImpl extends RemoteServiceServlet implements RichiestaScambioService {

    private final RichiestaScambioRepository richiestaScambioRepository = new RichiestaScambioRepository();
    private final AnnuncioRepository annuncioRepository = new AnnuncioRepository();
    private final UtenteRepository utenteRepository = new UtenteRepository();

    /**
     * Valida i dati e crea una nuova richiesta di scambio.
     */
    @Override
    public RichiestaScambio invia(
            String idAnnuncio,
            String idMittente,
            String messaggio
    ) {
        if (idAnnuncio == null || idAnnuncio.trim().isEmpty()) {
            throw new IllegalArgumentException("Annuncio non valido.");
        }

        if (idMittente == null || idMittente.trim().isEmpty()) {
            throw new IllegalArgumentException("Devi effettuare l'accesso per inviare una richiesta.");
        }

        if (messaggio == null || messaggio.trim().isEmpty()) {
            throw new IllegalArgumentException("Inserisci un messaggio per la richiesta.");
        }

        Annuncio annuncio = annuncioRepository.trovaPerId(idAnnuncio);

        if (annuncio == null) {
            throw new IllegalArgumentException("L'annuncio selezionato non esiste più.");
        }

        if (idMittente.equals(annuncio.getIdAutore())) {
            throw new IllegalArgumentException("Non puoi inviare una richiesta per un tuo annuncio.");
        }

        if (!annuncio.isAttivo()) {
            throw new IllegalArgumentException("Questo annuncio non è più disponibile.");
        }

        if ("No".equalsIgnoreCase(annuncio.getDisponibilita())) {
            throw new IllegalArgumentException("Questo annuncio non è attualmente disponibile.");
        }

        Utente mittente = utenteRepository.trovaPerId(idMittente);
        String nomeMittente = mittente != null ? mittente.getNomeUtente() : idMittente;

        return richiestaScambioRepository.crea(
                idAnnuncio,
                idMittente,
                annuncio.getIdAutore(),
                messaggio.trim(),
                annuncio.getTitolo(),
                nomeMittente
        );
    }

    @Override
    public List<RichiestaScambio> richiesteRicevute(String idProprietario) {

        if (idProprietario == null || idProprietario.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return richiestaScambioRepository.trovaPerProprietario(idProprietario);
    }

    @Override
    public List<RichiestaScambio> richiesteInviate(String idMittente) {

        if (idMittente == null || idMittente.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return richiestaScambioRepository.trovaPerMittente(idMittente);
    }

    /**
     * Accetta una richiesta di scambio, se consentito.
     */
    @Override
    public RichiestaScambio accetta(String idRichiesta, String idUtenteCorrente) throws IllegalArgumentException {
        RichiestaScambio richiesta = validaOperazioneSuRichiesta(idRichiesta, idUtenteCorrente);
        return richiestaScambioRepository.aggiornaStato(richiesta.getId(), StatoRichiesta.ACCETTATA);
    }

    /**
     * Rifiuta una richiesta di scambio, se consentito.
     */
    @Override
    public RichiestaScambio rifiuta(String idRichiesta, String idUtenteCorrente) throws IllegalArgumentException {
        RichiestaScambio richiesta = validaOperazioneSuRichiesta(idRichiesta, idUtenteCorrente);
        return richiestaScambioRepository.aggiornaStato(richiesta.getId(), StatoRichiesta.RIFIUTATA);
    }

    /**
     * Valida che l'utente possa accettare/rifiutare la richiesta indicata:
     * la richiesta deve esistere, l'utente deve esserne il destinatario,
     * e la richiesta deve essere ancora in attesa.
     */
    private RichiestaScambio validaOperazioneSuRichiesta(String idRichiesta, String idUtenteCorrente) {

        if (idRichiesta == null || idRichiesta.trim().isEmpty()) {
            throw new IllegalArgumentException("Richiesta non valida.");
        }

        if (idUtenteCorrente == null || idUtenteCorrente.trim().isEmpty()) {
            throw new IllegalArgumentException("Devi effettuare l'accesso per gestire una richiesta.");
        }

        RichiestaScambio richiesta = richiestaScambioRepository.trovaPerId(idRichiesta);

        if (richiesta == null) {
            throw new IllegalArgumentException("La richiesta selezionata non esiste più.");
        }

        if (!idUtenteCorrente.equals(richiesta.getIdProprietario())) {
            throw new IllegalArgumentException("Non sei autorizzato a gestire questa richiesta.");
        }

        if (richiesta.getStato() != StatoRichiesta.IN_ATTESA) {
            throw new IllegalArgumentException("Questa richiesta è già stata gestita.");
        }

        return richiesta;
    }

    /**
     * Recupera una richiesta di scambio, verificando che l'utente
     * indicato ne sia partecipante (mittente o proprietario).
     */
    @Override
    public RichiestaScambio trovaPerId(String idRichiesta, String idUtenteCorrente) throws IllegalArgumentException {

        if (idRichiesta == null || idRichiesta.trim().isEmpty()) {
            throw new IllegalArgumentException("Richiesta non valida.");
        }

        if (idUtenteCorrente == null || idUtenteCorrente.trim().isEmpty()) {
            throw new IllegalArgumentException("Devi effettuare l'accesso.");
        }

        RichiestaScambio richiesta = richiestaScambioRepository.trovaPerId(idRichiesta);

        if (richiesta == null) {
            throw new IllegalArgumentException("La richiesta selezionata non esiste più.");
        }

        boolean partecipa = idUtenteCorrente.equals(richiesta.getIdMittente())
                || idUtenteCorrente.equals(richiesta.getIdProprietario());

        if (!partecipa) {
            throw new IllegalArgumentException("Non sei autorizzato a visualizzare questa richiesta.");
        }

        return richiesta;
    }
}
