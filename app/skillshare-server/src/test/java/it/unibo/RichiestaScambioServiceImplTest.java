package it.unibo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica il servizio dedicato alle richieste di scambio.
 */
public class RichiestaScambioServiceImplTest {

    private RichiestaScambioServiceImpl service;
    private AnnuncioServiceImpl annuncioService;

    @BeforeEach
    void inizializzaTest() {
        service = new RichiestaScambioServiceImpl();
        annuncioService = new AnnuncioServiceImpl();

        DatabaseCore.getDB().hashMap("annunci").createOrOpen().clear();
        DatabaseCore.getDB().hashMap("richiesteScambio").createOrOpen().clear();
        DatabaseCore.commit();
    }

    /**
     * Verifica che una richiesta valida venga creata correttamente.
     */
    @Test
    void inviaConDatiValidiRestituisceRichiesta() {

        Annuncio annuncio = annuncioService.crea(
                "autore1",
                "Lezioni Java",
                "Descrizione",
                "Informatica",
                "Sì",
                "Inglese"
        );

        RichiestaScambio richiesta = service.invia(
                annuncio.getId(), "mittente1", "Mi interessa lo scambio"
        );

        assertNotNull(richiesta);
        assertEquals(annuncio.getId(), richiesta.getIdAnnuncio());
        assertEquals("mittente1", richiesta.getIdMittente());
        assertEquals("autore1", richiesta.getIdProprietario());
        assertEquals("Mi interessa lo scambio", richiesta.getMessaggio());
        assertEquals("Lezioni Java", richiesta.getTitoloAnnuncio());
        assertNotNull(richiesta.getNomeMittente());
    }

    /**
     * Verifica che l'id dell'annuncio sia obbligatorio.
     */
    @Test
    void inviaSenzaIdAnnuncioRestituisceErrore() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.invia(null, "mittente1", "Messaggio")
        );
    }

    /**
     * Verifica che l'id del mittente sia obbligatorio.
     */
    @Test
    void inviaSenzaIdMittenteRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1",
                "Titolo",
                "Descrizione",
                "Categoria",
                "Sì",
                "Cambio"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.invia(annuncio.getId(), null, "Messaggio")
        );
    }

    /**
     * Verifica che il messaggio sia obbligatorio.
     */
    @Test
    void inviaSenzaMessaggioRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1",
                "Titolo",
                "Descrizione",
                "Categoria",
                "Sì",
                "Cambio"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.invia(annuncio.getId(), "mittente1", "   ")
        );
    }

    /**
     * Verifica che non si possa inviare una richiesta
     * per un annuncio inesistente.
     */
    @Test
    void inviaPerAnnuncioInesistenteRestituisceErrore() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.invia("id-inesistente", "mittente1", "Messaggio")
        );
    }

    /**
     * Verifica che non si possa inviare una richiesta
     * per un proprio annuncio.
     */
    @Test
    void inviaPerPropriAnnuncioRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1",
                "Titolo",
                "Descrizione",
                "Categoria",
                "Sì",
                "Cambio"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.invia(annuncio.getId(), "autore1", "Messaggio")
        );
    }

    /**
     * Verifica che non si possa inviare una richiesta
     * per un annuncio non più attivo.
     */
    @Test
    void inviaPerAnnuncioNonAttivoRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1",
                "Titolo",
                "Descrizione",
                "Categoria",
                "Sì",
                "Cambio"
        );

        annuncioService.rimuovi(annuncio.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.invia(annuncio.getId(), "mittente1", "Messaggio")
        );
    }

    /**
     * Verifica che non si possa inviare una richiesta
     * per un annuncio con disponibilità impostata a "No".
     */
    @Test
    void inviaPerAnnuncioNonDisponibileRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1",
                "Titolo",
                "Descrizione",
                "Categoria",
                "No",
                "Cambio"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.invia(annuncio.getId(), "mittente1", "Messaggio")
        );
    }

    /**
     * Verifica che richiesteRicevute restituisca tutte le richieste
     * inviate per gli annunci del proprietario indicato.
     */
    @Test
    void richiesteRicevuteRestituisceLeRichiesteDelProprietario() {

        Annuncio annuncio = annuncioService.crea(
                "autore1",
                "Lezioni Java",
                "Descrizione",
                "Informatica",
                "Sì",
                "Inglese"
        );

        service.invia(annuncio.getId(), "mittente1", "Primo messaggio");
        service.invia(annuncio.getId(), "mittente2", "Secondo messaggio");

        List<RichiestaScambio> richieste = service.richiesteRicevute("autore1");

        assertEquals(2, richieste.size());
    }

    /**
     * Verifica che richiesteRicevute restituisca una lista vuota
     * se l'utente non ha ricevuto richieste.
     */
    @Test
    void richiesteRicevuteSenzaRichiesteRestituisceListaVuota() {

        List<RichiestaScambio> richieste = service.richiesteRicevute("autore-senza-richieste");

        assertTrue(richieste.isEmpty());
    }

    /**
     * Verifica che richiesteRicevute restituisca una lista vuota
     * quando l'id proprietario è null o vuoto.
     */
    @Test
    void richiesteRicevuteConIdProprietarioNonValidoRestituisceListaVuota() {

        assertTrue(service.richiesteRicevute(null).isEmpty());
        assertTrue(service.richiesteRicevute("").isEmpty());
    }
}