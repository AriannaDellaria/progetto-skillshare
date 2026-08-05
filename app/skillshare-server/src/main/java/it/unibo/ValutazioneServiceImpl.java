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
            int punteggio,
            String commento
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

        // Il commento è facoltativo, ma se presente non deve
        // superare la lunghezza massima consentita.
        String commentoPulito = commento == null ? "" : commento.trim();
        if (commentoPulito.length() > Valutazione.COMMENTO_LUNGHEZZA_MASSIMA) {
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
                commentoPulito,
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

    /**
     * Calcola il rating dell'utente indicato come media aritmetica
     * delle valutazioni numeriche ricevute, distinto per prestazione
     * e controprestazione.
     */
    @Override
    public RatingUtente calcolaRating(String idUtente) {

        if (idUtente == null || idUtente.trim().isEmpty()) {
            return new RatingUtente(0, 0, 0, 0);
        }

        java.util.List<Valutazione> prestazioni =
                valutazioneRepository.trovaPerUtenteValutatoETipo(idUtente, TipoValutazione.PRESTAZIONE);

        java.util.List<Valutazione> controprestazioni =
                valutazioneRepository.trovaPerUtenteValutatoETipo(idUtente, TipoValutazione.CONTROPRESTAZIONE);

        double mediaPrestazione = calcolaMedia(prestazioni);
        double mediaControprestazione = calcolaMedia(controprestazioni);

        return new RatingUtente(
                mediaPrestazione,
                prestazioni.size(),
                mediaControprestazione,
                controprestazioni.size()
        );
    }

    // Calcola la media aritmetica dei punteggi di una lista di
    // valutazioni. Restituisce 0 se la lista è vuota.
    private double calcolaMedia(java.util.List<Valutazione> valutazioni) {

        if (valutazioni.isEmpty()) {
            return 0;
        }

        int somma = 0;
        for (Valutazione valutazione : valutazioni) {
            somma += valutazione.getPunteggio();
        }

        return (double) somma / valutazioni.size();
    }

    /**
     * Recupera lo storico pubblico degli scambi conclusi dell'utente indicato.
     */
    @Override
    public StoricoUtente recuperaStorico(String idUtente) {

        if (idUtente == null || idUtente.trim().isEmpty()) {
            return new StoricoUtente();
        }

        java.util.List<Valutazione> valutazioniPrestazione =
                valutazioneRepository.trovaPerUtenteValutatoETipo(idUtente, TipoValutazione.PRESTAZIONE);

        java.util.List<Valutazione> valutazioniControprestazione =
                valutazioneRepository.trovaPerUtenteValutatoETipo(idUtente, TipoValutazione.CONTROPRESTAZIONE);

        java.util.List<VoceStorico> storicoPrestazione =
                costruisciVociStorico(valutazioniPrestazione, true);

        java.util.List<VoceStorico> storicoControprestazione =
                costruisciVociStorico(valutazioniControprestazione, false);

        return new StoricoUtente(storicoPrestazione, storicoControprestazione);
    }

    // Costruisce le voci di storico a partire dalle valutazioni,
    // recuperando per ciascuna il testo identificativo dalla
    // richiesta di scambio associata: il titolo dell'annuncio per la
    // prestazione, il messaggio della proposta per la
    // controprestazione.
    private java.util.List<VoceStorico> costruisciVociStorico(
            java.util.List<Valutazione> valutazioni,
            boolean usaTitoloAnnuncio
    ) {
        java.util.List<VoceStorico> voci = new java.util.ArrayList<>();

        for (Valutazione valutazione : valutazioni) {

            RichiestaScambio richiesta =
                    richiestaScambioRepository.trovaPerId(valutazione.getIdRichiesta());

            if (richiesta == null) {
                continue;
            }

            String testo = usaTitoloAnnuncio
                    ? richiesta.getTitoloAnnuncio()
                    : richiesta.getMessaggio();

            voci.add(new VoceStorico(
                    testo,
                    valutazione.getPunteggio(),
                    valutazione.getCommento(),
                    valutazione.getDataValutazione()
            ));
        }

        voci.sort((a, b) -> Long.compare(b.getData(), a.getData()));

        return voci;
    }
}