package it.unibo;

import com.google.gwt.user.server.rpc.jakarta.RemoteServiceServlet;

import java.util.ArrayList;
import java.util.List;

/**
 * Esegue sul server le operazioni relative agli annunci.
 */
public class AnnuncioServiceImpl extends RemoteServiceServlet implements AnnuncioService {

    private final AnnuncioRepository annuncioRepository = new AnnuncioRepository();

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
}
