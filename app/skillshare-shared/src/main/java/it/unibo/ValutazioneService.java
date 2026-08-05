package it.unibo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Definisce le operazioni relative alle valutazioni di uno scambio.
 */
@RemoteServiceRelativePath("valutazione")
public interface ValutazioneService extends RemoteService {

    /**
     * Assegna una valutazione numerica, con commento facoltativo, al
     * termine di uno scambio concluso. Il destinatario della valutazione è il
     * proprietario dell'annuncio associato alla richiesta.
     */
    Valutazione assegna(
            String idRichiesta,
            String idUtenteCorrente,
            int punteggio,
            String commento
    );

    /**
     * Controlla se una valutazione è già stata assegnata per la
     * richiesta indicata.
     */
    boolean valutazioneGiaEsistente(String idRichiesta, String idUtenteCorrente);

    /**
     * Calcola il rating dell'utente indicato, come media delle
     * valutazioni numeriche ricevute, distinto per prestazione e
     * controprestazione.
     */
    RatingUtente calcolaRating(String idUtente);
}