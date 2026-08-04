package it.unibo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ValutazioneRepositoryTest {

    private ValutazioneRepository valutazioneRepository;

    @BeforeEach
    void inizializzaTest() {
        valutazioneRepository = new ValutazioneRepository();

        DatabaseCore.getDB().hashMap("valutazioni").createOrOpen().clear();
        DatabaseCore.commit();
    }

    /**
     * Verifica che una valutazione venga creata e salvata
     * correttamente con tutti i campi, incluso il tipo.
     */
    @Test
    void creaSalvaTuttiICampi() {

        Valutazione valutazione = valutazioneRepository.crea(
                "richiesta1", "mittente1", "proprietario1", 8,
                TipoValutazione.PRESTAZIONE
        );

        assertNotNull(valutazione);
        assertNotNull(valutazione.getId());
        assertEquals("richiesta1", valutazione.getIdRichiesta());
        assertEquals("mittente1", valutazione.getIdMittente());
        assertEquals("proprietario1", valutazione.getIdUtenteValutato());
        assertEquals(8, valutazione.getPunteggio());
        assertEquals(TipoValutazione.PRESTAZIONE, valutazione.getTipo());
    }

    /**
     * Verifica che due valutazioni create in sequenza
     * ricevano id diversi.
     */
    @Test
    void creaAssegnaIdDiversiAValutazioniDistinte() {

        Valutazione prima = valutazioneRepository.crea(
                "richiesta1", "mittente1", "proprietario1", 7,
                TipoValutazione.PRESTAZIONE
        );

        Valutazione seconda = valutazioneRepository.crea(
                "richiesta2", "mittente2", "proprietario2", 9,
                TipoValutazione.CONTROPRESTAZIONE
        );

        assertNotEquals(prima.getId(), seconda.getId());
    }

    /**
     * Verifica che trovaPerRichiestaETipo trovi correttamente la
     * valutazione associata a una specifica richiesta e tipo.
     */
    @Test
    void trovaPerRichiestaETipoConValutazioneEsistenteRestituisceLaValutazione() {

        valutazioneRepository.crea(
                "richiesta1", "mittente1", "proprietario1", 6,
                TipoValutazione.PRESTAZIONE
        );

        Valutazione trovata = valutazioneRepository.trovaPerRichiestaETipo(
                "richiesta1", TipoValutazione.PRESTAZIONE
        );

        assertNotNull(trovata);
        assertEquals("richiesta1", trovata.getIdRichiesta());
        assertEquals(6, trovata.getPunteggio());
    }

    /**
     * Verifica che le due valutazione per la stessa
     * richiesta siano tenute distinte: cercando l'altro tipo non si
     * trova la valutazione appena creata.
     */
    @Test
    void trovaPerRichiestaETipoConTipoDiversoNonTrovaLaValutazione() {

        valutazioneRepository.crea(
                "richiesta1", "mittente1", "proprietario1", 6,
                TipoValutazione.PRESTAZIONE
        );

        Valutazione trovata = valutazioneRepository.trovaPerRichiestaETipo(
                "richiesta1", TipoValutazione.CONTROPRESTAZIONE
        );

        assertNull(trovata);
    }

    /**
     * Verifica che una richiesta possa avere entrambe le
     * valutazioni contemporaneamente.
     */
    @Test
    void trovaPerRichiestaETipoConEntrambeLeDirezioniRestituisceQuellaCorretta() {

        valutazioneRepository.crea(
                "richiesta1", "mittente1", "proprietario1", 8,
                TipoValutazione.PRESTAZIONE
        );

        valutazioneRepository.crea(
                "richiesta1", "proprietario1", "mittente1", 5,
                TipoValutazione.CONTROPRESTAZIONE
        );

        Valutazione prestazione = valutazioneRepository.trovaPerRichiestaETipo(
                "richiesta1", TipoValutazione.PRESTAZIONE
        );
        Valutazione controprestazione = valutazioneRepository.trovaPerRichiestaETipo(
                "richiesta1", TipoValutazione.CONTROPRESTAZIONE
        );

        assertEquals(8, prestazione.getPunteggio());
        assertEquals(5, controprestazione.getPunteggio());
    }

    /**
     * Verifica che trovaPerRichiestaETipo restituisca null se
     * nessuna valutazione è associata a quella richiesta.
     */
    @Test
    void trovaPerRichiestaETipoSenzaValutazioneRestituisceNull() {

        Valutazione trovata = valutazioneRepository.trovaPerRichiestaETipo(
                "richiesta-inesistente", TipoValutazione.PRESTAZIONE
        );

        assertNull(trovata);
    }

    /**
     * Verifica che trovaPerRichiestaETipo restituisca null quando
     * l'id richiesta o il tipo passati sono null.
     */
    @Test
    void trovaPerRichiestaETipoConParametriNullRestituisceNull() {

        assertNull(valutazioneRepository.trovaPerRichiestaETipo(null, TipoValutazione.PRESTAZIONE));
        assertNull(valutazioneRepository.trovaPerRichiestaETipo("richiesta1", null));
    }

    /**
     * Verifica che trovaPerRichiesta restituisca entrambe le
     * valutazioni associate alla stessa richiesta.
     */
    @Test
    void trovaPerRichiestaRestituisceTutteLeValutazioniDellaRichiesta() {

        valutazioneRepository.crea(
                "richiesta1", "mittente1", "proprietario1", 8,
                TipoValutazione.PRESTAZIONE
        );

        valutazioneRepository.crea(
                "richiesta1", "proprietario1", "mittente1", 5,
                TipoValutazione.CONTROPRESTAZIONE
        );

        List<Valutazione> risultati = valutazioneRepository.trovaPerRichiesta("richiesta1");

        assertEquals(2, risultati.size());
    }

    /**
     * Verifica che trovaPerRichiesta restituisca una lista vuota
     * se la richiesta non ha valutazioni.
     */
    @Test
    void trovaPerRichiestaSenzaValutazioniRestituisceListaVuota() {

        List<Valutazione> risultati = valutazioneRepository.trovaPerRichiesta("richiesta-inesistente");

        assertTrue(risultati.isEmpty());
    }

    /**
     * Verifica che trovaPerUtenteValutatoETipo restituisca solo le
     * valutazioni ricevute dall'utente indicato, nel tipo indicato.
     */
    @Test
    void trovaPerUtenteValutatoETipoRestituisceSoloLeValutazioniDelTipoCorretto() {

        valutazioneRepository.crea(
                "richiesta1", "mittente1", "proprietario1", 8,
                TipoValutazione.PRESTAZIONE
        );

        valutazioneRepository.crea(
                "richiesta2", "mittente2", "proprietario1", 6,
                TipoValutazione.PRESTAZIONE
        );

        valutazioneRepository.crea(
                "richiesta3", "proprietario1", "mittente3", 9,
                TipoValutazione.CONTROPRESTAZIONE
        );

        List<Valutazione> prestazioni = valutazioneRepository.trovaPerUtenteValutatoETipo(
                "proprietario1", TipoValutazione.PRESTAZIONE
        );

        assertEquals(2, prestazioni.size());
    }

    /**
     * Verifica che trovaPerUtenteValutatoETipo restituisca una
     * lista vuota se l'utente non ha ricevuto valutazioni di quel tipo.
     */
    @Test
    void trovaPerUtenteValutatoETipoSenzaValutazioniRestituisceListaVuota() {

        List<Valutazione> risultati = valutazioneRepository.trovaPerUtenteValutatoETipo(
                "utente-senza-valutazioni", TipoValutazione.PRESTAZIONE
        );

        assertTrue(risultati.isEmpty());
    }

    /**
     * Verifica che trovaPerUtenteValutatoETipo restituisca una
     * lista vuota quando i parametri passati sono null.
     */
    @Test
    void trovaPerUtenteValutatoETipoConParametriNullRestituisceListaVuota() {

        assertTrue(valutazioneRepository.trovaPerUtenteValutatoETipo(null, TipoValutazione.PRESTAZIONE).isEmpty());
        assertTrue(valutazioneRepository.trovaPerUtenteValutatoETipo("utente1", null).isEmpty());
    }
}