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
