package it.unibo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ValutazioneServiceAsync {

    /**
     * Richiede l'assegnazione di una valutazione al termine di uno
     * scambio concluso.
     */
    void assegna(
            String idRichiesta,
            String idUtenteCorrente,
            int punteggio,
            String commento,
            AsyncCallback<Valutazione> callback
    );

    /**
     * Richiede al server se una valutazione esiste già per la
     * richiesta indicata.
     */
    void valutazioneGiaEsistente(
            String idRichiesta,
            String idUtenteCorrente,
            AsyncCallback<Boolean> callback
    );

    /**
     * Richiede il rating calcolato per l'utente indicato.
     */
    void calcolaRating(
            String idUtente,
            AsyncCallback<RatingUtente> callback
    );
}