package it.unibo;

import com.google.gwt.user.server.rpc.jakarta.RemoteServiceServlet;

/**
 * Esegue sul server le operazioni relative alle valutazioni
 * assegnate al termine di uno scambio concluso.
 */
public class ValutazioneServiceImpl
        extends RemoteServiceServlet
        implements ValutazioneService {

    private final ValutazioneRepository valutazioneRepository = new ValutazioneRepository();
    private final RichiestaScambioRepository richiestaScambioRepository = new RichiestaScambioRepository();

    /**
     * Valida i dati e assegna una nuova valutazione.
     */
    @Override
    public Valutazione assegna(
            String idRichiesta,
            String idUtenteCorrente,
            int punteggio
    ) {
        // Validazione parametri di input
        if (idRichiesta == null || idRichiesta.trim().isEmpty()) {
            return null;
        }

        if (idUtenteCorrente == null || idUtenteCorrente.trim().isEmpty()) {
            return null;
        }

        if (punteggio < Valutazione.PUNTEGGIO_MINIMO || punteggio > Valutazione.PUNTEGGIO_MASSIMO) {
            return null;
        }

        //Controllo esistenza della richiesta
        RichiestaScambio richiesta = richiestaScambioRepository.trovaPerId(idRichiesta);
        if (richiesta == null) {
            return null;
        }

        if (richiesta.getStato() != StatoRichiesta.CONCLUSA) {
            return null;
        }

        DirezioneValutazione direzione = determinaDirezione(richiesta, idUtenteCorrente);
        if (direzione == null) {
            // L'utente non è né mittente né proprietario, dunque non è autorizzato.
            return null;
        }

        //Controllo che non sia già stato valutato
        if (valutazioneRepository.trovaPerRichiestaETipo(idRichiesta, direzione.tipo) != null) {
            return null;
        }

        // Se tutti i controlli passano, crea la valutazione
        return valutazioneRepository.crea(
                idRichiesta,
                idUtenteCorrente,
                direzione.idUtenteValutato,
                punteggio,
                direzione.tipo
        );
    }

    /**
     * Determina, in base al ruolo del chiamante nella richiesta,
     * chi è il destinatario della valutazione e quale ne è il tipo.
     * Restituisce null se il chiamante non è né mittente né
     * proprietario della richiesta.
     */
    private DirezioneValutazione determinaDirezione(RichiestaScambio richiesta, String idUtenteCorrente) {

        if (idUtenteCorrente.equals(richiesta.getIdMittente())) {
            return new DirezioneValutazione(
                    richiesta.getIdProprietario(),
                    TipoValutazione.PRESTAZIONE
            );
        }

        if (idUtenteCorrente.equals(richiesta.getIdProprietario())) {
            return new DirezioneValutazione(
                    richiesta.getIdMittente(),
                    TipoValutazione.CONTROPRESTAZIONE
            );
        }

        return null;
    }

    /**
     * Controlla se l'utente indicato ha già assegnato la propria
     * valutazione per la richiesta indicata in base al proprio ruolo.
     */
    @Override
    public boolean valutazioneGiaEsistente(String idRichiesta, String idUtenteCorrente) {

        if (idRichiesta == null || idRichiesta.trim().isEmpty()
                || idUtenteCorrente == null || idUtenteCorrente.trim().isEmpty()) {
            return false;
        }

        RichiestaScambio richiesta = richiestaScambioRepository.trovaPerId(idRichiesta);

        if (richiesta == null) {
            return false;
        }

        TipoValutazione tipo;

        if (idUtenteCorrente.equals(richiesta.getIdMittente())) {
            tipo = TipoValutazione.PRESTAZIONE;
        } else if (idUtenteCorrente.equals(richiesta.getIdProprietario())) {
            tipo = TipoValutazione.CONTROPRESTAZIONE;
        } else {
            return false;
        }

        return valutazioneRepository.trovaPerRichiestaETipo(idRichiesta, tipo) != null;
    }

    /**
     * Incapsula destinatario e tipo di valutazione dedotti dal ruolo del chiamante.
     */
    private static class DirezioneValutazione {
        private final String idUtenteValutato;
        private final TipoValutazione tipo;

        private DirezioneValutazione(String idUtenteValutato, TipoValutazione tipo) {
            this.idUtenteValutato = idUtenteValutato;
            this.tipo = tipo;
        }
    }
}