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
}
