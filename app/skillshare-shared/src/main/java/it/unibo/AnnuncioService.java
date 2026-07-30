package it.unibo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.List;

/**
 * Definisce le operazioni sugli annunci.
 */
@RemoteServiceRelativePath("annuncio")
public interface AnnuncioService extends RemoteService {

    /**
     * Crea un nuovo annuncio.
     */
    Annuncio crea(
            String idAutore,
            String titolo,
            String descrizione,
            String categoria,
            String disponibilita,
            String controprestazione
    );

    /**
     * Recupera gli annunci dell'utente autenticato.
     */
    List<Annuncio> mieiAnnunci(String idAutore);

    /**
     * Recupera gli annunci attivi disponibili.
     */
    List<Annuncio> trovaAttivi();

    /**
     * Cerca gli annunci con una determinata parola chiave.
     */
    List<Annuncio> cerca(String keyword);

    /**
     * Restituisce gli annunci attivi che soddisfano i criteri
     * di filtro indicati.
     */
    List<Annuncio> filtra(FiltroAnnunci filtro);

    /**
     * Ordina la lista di annunci indicata secondo il criterio scelto.
     */
    List<Annuncio> ordina(List<Annuncio> annunci, CriterioOrdinamento criterio);

    /**
     * Modifica un annuncio esistente dell'utente.
     */
    Annuncio modificaAnnuncio(
            String idAnnuncio,
            String idAutore,
            String titolo,
            String categoria,
            String descrizione,
            String disponibilita,
            String controprestazione
    );

    /**
     * Disattiva un annuncio dell'autore.
     */
    boolean rimuovi(
            String idAnnuncio,
            String idAutore
    );
}
