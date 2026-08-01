package it.unibo;

import com.google.gwt.user.server.rpc.jakarta.RemoteServiceServlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Esegue sul server le operazioni relative agli annunci.
 */
public class AnnuncioServiceImpl extends RemoteServiceServlet implements AnnuncioService {

    private final AnnuncioRepository annuncioRepository = new AnnuncioRepository();
    private final RichiestaScambioRepository richiestaScambioRepository = new RichiestaScambioRepository();

    /**
     * Valida i dati e crea un nuovo annuncio.
     */
    @Override
    public Annuncio crea(
            String idAutore,
            String titolo,
            String descrizione,
            String categoria,
            String disponibilita,
            String controprestazione
    ) {
        if (idAutore == null || idAutore.trim().isEmpty()) {
            throw new IllegalArgumentException("Devi effettuare l'accesso per pubblicare un annuncio.");
        }
        if (titolo == null || titolo.trim().isEmpty()) {
            throw new IllegalArgumentException("Il titolo è obbligatorio.");
        }
        if (descrizione == null || descrizione.trim().isEmpty()) {
            throw new IllegalArgumentException("La descrizione è obbligatoria.");
        }
        if (categoria == null) {
            categoria = "";
        }
        if (disponibilita == null) {
            throw new IllegalArgumentException("Seleziona la disponibilità.");
        }
        if (controprestazione == null || controprestazione.trim().isEmpty()) {
            throw new IllegalArgumentException("La controprestazione è obbligatoria.");
        }
        return annuncioRepository.crea(
                idAutore,
                titolo.trim(),
                descrizione.trim(),
                categoria.trim(),
                disponibilita.trim(),
                controprestazione.trim()
        );
    }

    /**
     * Recupera tutti gli annunci creati dall'utente.
     */
    @Override
    public List<Annuncio> mieiAnnunci(String idAutore) {

        if (idAutore == null || idAutore.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return annuncioRepository.trovaPerAutore(idAutore);
    }

    /**
     * Restituisce gli annunci attivi disponibili.
     */
    @Override
    public List<Annuncio> trovaAttivi() {

        List<Annuncio> annunci = annuncioRepository.trovaAttivi();

        if (annunci == null) {
            return Collections.emptyList();
        }

        return annunci;
    }

    /**
     * Valida la parola chiave e restituisce gli annunci attivi corrispondenti.
     */
    @Override
    public List<Annuncio> cerca(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Inserisci una parola chiave per la ricerca."
            );
        }

        AnnuncioMatcher matcher = new KeywordAnnuncioMatcher(keyword);

        return annuncioRepository.cerca(matcher);
    }

    /**
     * Restituisce gli annunci attivi che soddisfano i criteri di
     * filtro indicati, delegando il criterio a FiltroAnnuncioMatcher.
     */
    @Override
    public List<Annuncio> filtra(FiltroAnnunci filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException(
                    "Filtro non valido."
            );
        }

        AnnuncioMatcher matcher = new FiltroAnnuncioMatcher(filtro);

        return annuncioRepository.cerca(matcher);
    }

    /**
     * Ordina la lista di annunci indicata secondo il criterio scelto,
     * delegando il confronto a ComparatorOrdinamento. Se il criterio
     * è nullo o la lista è vuota, restituisce la lista invariata.
     */
    @Override
    public List<Annuncio> ordina(List<Annuncio> annunci, CriterioOrdinamento criterio) {
        if (annunci == null) {
            return Collections.emptyList();
        }

        Comparator<Annuncio> comparator = ComparatorOrdinamento.perCriterio(criterio);

        List<Annuncio> risultato = new ArrayList<>(annunci);

        if (comparator != null) {
            risultato.sort(comparator);
        }

        return risultato;
    }

    /**
     * Modifica un annuncio dopo aver controllato i campi.
     */
    @Override
    public Annuncio modificaAnnuncio(
            String idAnnuncio,
            String idAutore,
            String titolo,
            String categoria,
            String descrizione,
            String disponibilita,
            String controprestazione
    ) {
        if (idAnnuncio == null || idAnnuncio.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Annuncio non valido."
            );
        }

        if (idAutore == null || idAutore.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Utente non valido."
            );
        }

        if (titolo == null || titolo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Il titolo è obbligatorio."
            );
        }

        if (disponibilita == null
                || disponibilita.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La disponibilità è obbligatoria."
            );
        }

        if (controprestazione == null
                || controprestazione.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La controprestazione è obbligatoria."
            );
        }

        Annuncio annuncio = annuncioRepository.modificaAnnuncio(
                idAnnuncio,
                idAutore,
                titolo.trim(),
                categoria == null ? "" : categoria.trim(),
                descrizione == null ? "" : descrizione.trim(),
                disponibilita.trim(),
                controprestazione.trim()
        );

        if (annuncio == null) {
            throw new IllegalArgumentException(
                    "Non puoi modificare questo annuncio."
            );
        }

        return annuncio;
    }

    /**
     * Disattiva un annuncio solo se appartiene all'autore.
     */
    @Override
    public boolean rimuovi(String idAnnuncio, String idAutore) {
        if (idAnnuncio == null || idAutore == null) {
            return false;
        }
        boolean rimosso = annuncioRepository.rimuovi(idAnnuncio, idAutore);
        if (rimosso) {
            List<RichiestaScambio> richieste = richiestaScambioRepository.trovaPerAnnuncio(idAnnuncio);

            for (RichiestaScambio richiesta : richieste) {
                if (richiesta.getStato() == StatoRichiesta.IN_ATTESA) {
                    richiestaScambioRepository.aggiornaStato(richiesta.getId(), StatoRichiesta.RIFIUTATA);
                }
            }
        }
        return rimosso;
    }
}
