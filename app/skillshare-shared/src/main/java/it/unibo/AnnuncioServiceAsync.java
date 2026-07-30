package it.unibo;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.List;

/**
 * Versione asincrona del servizio annunci.
 */
public interface AnnuncioServiceAsync {
    /**
     * Invia al server la richiesta di creazione.
     */
    void crea(
            String idAutore,
            String titolo,
            String descrizione,
            String categoria,
            String disponibilita,
            String controprestazione,
            AsyncCallback<Annuncio> callback
    );

    /**
     * Recupera gli annunci dell'utente.
     */
    void mieiAnnunci(
            String idAutore,
            AsyncCallback<List<Annuncio>> callback
    );

    /**
     * Richiede la ricerca degli annunci per parola chiave.
     */
    void cerca(
            String keyword,
            AsyncCallback<List<Annuncio>> callback
    );

    /**
     * Richiede gli annunci che soddisfano i criteri di filtro.
     */
    void filtra(
            FiltroAnnunci filtro,
            AsyncCallback<List<Annuncio>> callback
    );

    /**
     * Richiede l'ordinamento della lista di annunci secondo il criterio scelto.
     */
    void ordina(
            List<Annuncio> annunci,
            CriterioOrdinamento criterio,
            AsyncCallback<List<Annuncio>> callback
    );

    /**
     * Modifica un annuncio esistente dell'utente.
     */
    void modificaAnnuncio(
            String idAnnuncio,
            String idAutore,
            String titolo,
            String categoria,
            String descrizione,
            String disponibilita,
            String controprestazione,
            AsyncCallback<Annuncio> callback
    );

    /**
     * Richiede la rimozione di un annuncio.
     */
    void rimuovi(
            String idAnnuncio,
            String idAutore,
            AsyncCallback<Boolean> callback
    );

    /**
     * Recupera gli annunci attivi.
     */
    void trovaAttivi(
            AsyncCallback<List<Annuncio>> callback
    );
}
