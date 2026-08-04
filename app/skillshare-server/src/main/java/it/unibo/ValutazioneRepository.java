package it.unibo;

import org.mapdb.DB;
import org.mapdb.Serializer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * Gestisce il salvataggio delle valutazioni assegnate al termine
 * di uno scambio concluso.
 */
public class ValutazioneRepository {

    private static final DB db = DatabaseCore.getDB();

    private static final ConcurrentMap<String, Valutazione> valutazioni =
            db.hashMap("valutazioni", Serializer.STRING, Serializer.JAVA)
                    .createOrOpen();

    /**
     * Crea e salva una nuova valutazione.
     */
    public Valutazione crea(
            String idRichiesta,
            String idMittente,
            String idUtenteValutato,
            int punteggio,
            String commento,
            TipoValutazione tipo
    ) {
        String id = UUID.randomUUID().toString();

        Valutazione valutazione = new Valutazione(
                id,
                idRichiesta,
                idMittente,
                idUtenteValutato,
                punteggio,
                commento,
                tipo
        );

        valutazioni.put(id, valutazione);
        DatabaseCore.commit();

        return valutazione;
    }

    /**
     * Cerca la valutazione già assegnata per una specifica richiesta
     * di scambio e per uno specifico tipo. Ogni scambio concluso
     * può avere al più una valutazione per ciascun tipo.
     */
    public Valutazione trovaPerRichiestaETipo(String idRichiesta, TipoValutazione tipo) {

        if (idRichiesta == null || tipo == null) {
            return null;
        }

        for (Valutazione valutazione : valutazioni.values()) {
            if (idRichiesta.equals(valutazione.getIdRichiesta())
                    && tipo == valutazione.getTipo()) {
                return valutazione;
            }
        }

        return null;
    }

    /**
     * Cerca tutte le valutazioni associate a una richiesta,
     * indipendentemente dal tipo.
     */
    public List<Valutazione> trovaPerRichiesta(String idRichiesta) {

        List<Valutazione> risultati = new ArrayList<>();

        if (idRichiesta == null) {
            return risultati;
        }

        for (Valutazione valutazione : valutazioni.values()) {
            if (idRichiesta.equals(valutazione.getIdRichiesta())) {
                risultati.add(valutazione);
            }
        }

        return risultati;
    }

    /**
     * Cerca tutte le valutazioni ricevute da un utente, di un
     * determinato tipo.
     */
    public List<Valutazione> trovaPerUtenteValutatoETipo(
            String idUtenteValutato,
            TipoValutazione tipo
    ) {
        List<Valutazione> risultati = new ArrayList<>();

        if (idUtenteValutato == null || tipo == null) {
            return risultati;
        }

        for (Valutazione valutazione : valutazioni.values()) {
            if (idUtenteValutato.equals(valutazione.getIdUtenteValutato())
                    && tipo == valutazione.getTipo()) {
                risultati.add(valutazione);
            }
        }

        return risultati;
    }
}