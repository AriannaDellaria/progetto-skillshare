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

    /**
     * Verifica che il proprietario possa accettare correttamente
     * una richiesta in attesa.
     */
    @Test
    void accettaConProprietarioCorrettoAggiornaLoStato() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = service.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        RichiestaScambio aggiornata = service.accetta(richiesta.getId(), "autore1");

        assertEquals(StatoRichiesta.ACCETTATA, aggiornata.getStato());
    }

    /**
     * Verifica che il proprietario possa correttamente rifiutare
     * una richiesta in attesa.
     */
    @Test
    void rifiutaConProprietarioCorrettoAggiornaLoStato() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = service.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        RichiestaScambio aggiornata = service.rifiuta(richiesta.getId(), "autore1");

        assertEquals(StatoRichiesta.RIFIUTATA, aggiornata.getStato());
    }

    /**
     * Verifica che un utente diverso dal proprietario dell'annuncio
     * non possa accettare la richiesta.
     */
    @Test
    void accettaConUtenteNonProprietarioRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = service.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.accetta(richiesta.getId(), "utente-non-autorizzato")
        );
    }

    /**
     * Verifica che non si possa accettare una richiesta già accettata.
     */
    @Test
    void accettaConRichiestaGiaAccettataRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = service.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        service.accetta(richiesta.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.accetta(richiesta.getId(), "autore1")
        );
    }

    /**
     * Verifica che non si possa accettare una richiesta già rifiutata.
     */
    @Test
    void accettaConRichiestaGiaRifiutataRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = service.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        service.rifiuta(richiesta.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.accetta(richiesta.getId(), "autore1")
        );
    }

    /**
     * Verifica che non si possa rifiutare una richiesta inesistente.
     */
    @Test
    void rifiutaConRichiestaInesistenteRestituisceErrore() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.rifiuta("id-inesistente", "autore1")
        );
    }

    /**
     * Verifica che richiesteInviate restituisca tutte le richieste
     * inviate dall'utente indicato.
     */
    @Test
    void richiesteInviateRestituisceLeRichiesteDelMittente() {

        Annuncio annuncio1 = annuncioService.crea(
                "autore1", "Lezioni Java", "Descrizione",
                "Informatica", "Sì", "Inglese"
        );

        Annuncio annuncio2 = annuncioService.crea(
                "autore2", "Lezioni Chitarra", "Descrizione",
                "Musica", "Sì", "Spagnolo"
        );

        service.invia(annuncio1.getId(), "mittente1", "Primo messaggio");
        service.invia(annuncio2.getId(), "mittente1", "Secondo messaggio");

        List<RichiestaScambio> richieste = service.richiesteInviate("mittente1");

        assertEquals(2, richieste.size());
    }

    /**
     * Verifica che richiesteInviate restituisca una lista vuota
     * se l'utente non ha inviato richieste.
     */
    @Test
    void richiesteInviateSenzaRichiesteRestituisceListaVuota() {
        List<RichiestaScambio> richieste = service.richiesteInviate("mittente-senza-richieste");

        assertTrue(richieste.isEmpty());
    }

    /**
     * Verifica che richiesteInviate restituisca una lista vuota
     * quando l'id mittente è null o vuoto.
     */
    @Test
    void richiesteInviateConIdMittenteNonValidoRestituisceListaVuota() {
        assertTrue(service.richiesteInviate(null).isEmpty());
        assertTrue(service.richiesteInviate("").isEmpty());
    }

    /**
     * Verifica che il mittente possa recuperare la propria richiesta.
     */
    @Test
    void trovaPerIdConMittenteRestituisceLaRichiesta() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = service.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        RichiestaScambio trovata = service.trovaPerId(richiesta.getId(), "mittente1");

        assertNotNull(trovata);
        assertEquals(richiesta.getId(), trovata.getId());
    }

    /**
     * Verifica che il proprietario possa recuperare la richiesta.
     */
    @Test
    void trovaPerIdConProprietarioRestituisceLaRichiesta() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = service.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        RichiestaScambio trovata = service.trovaPerId(richiesta.getId(), "autore1");

        assertNotNull(trovata);
    }

    /**
     * Verifica che un utente non partecipante non possa
     * recuperare la richiesta.
     */
    @Test
    void trovaPerIdConUtenteNonPartecipanteRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = service.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.trovaPerId(richiesta.getId(), "estraneo1")
        );
    }

    /**
     * Verifica che non si possa recuperare una richiesta inesistente.
     */
    @Test
    void trovaPerIdConRichiestaInesistenteRestituisceErrore() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.trovaPerId("id-inesistente", "mittente1")
        );
    }

    /**
     * Verifica che il proprietario possa terminare correttamente
     * uno scambio accettato.
     */
    @Test
    void terminaConProprietarioERichiestaAccettataAggiornaLoStato() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = service.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        service.accetta(richiesta.getId(), "autore1");

        RichiestaScambio conclusa = service.termina(richiesta.getId(), "autore1");

        assertEquals(StatoRichiesta.CONCLUSA, conclusa.getStato());
    }

    /**
     * Verifica che il mittente non possa terminare lo scambio,
     * anche se la richiesta è stata accettata.
     */
    @Test
    void terminaConMittenteRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = service.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        service.accetta(richiesta.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.termina(richiesta.getId(), "mittente1")
        );
    }

    /**
     * Verifica che un utente estraneo non possa terminare lo scambio.
     */
    @Test
    void terminaConUtenteEstraneoRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = service.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        service.accetta(richiesta.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.termina(richiesta.getId(), "estraneo1")
        );
    }

    /**
     * Verifica che non si possa terminare una richiesta ancora
     * in attesa, mai accettata.
     */
    @Test
    void terminaConRichiestaInAttesaRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = service.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.termina(richiesta.getId(), "autore1")
        );
    }

    /**
     * Verifica che non si possa terminare una richiesta
     * già rifiutata.
     */
    @Test
    void terminaConRichiestaRifiutataRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = service.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        service.rifiuta(richiesta.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.termina(richiesta.getId(), "autore1")
        );
    }

    /**
     * Verifica che non si possa terminare due volte lo stesso
     * scambio già concluso.
     */
    @Test
    void terminaConRichiestaGiaConclusaRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = service.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        service.accetta(richiesta.getId(), "autore1");
        service.termina(richiesta.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.termina(richiesta.getId(), "autore1")
        );
    }

    /**
     * Verifica che non si possa terminare una richiesta inesistente.
     */
    @Test
    void terminaConRichiestaInesistenteRestituisceErrore() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.termina("id-inesistente", "autore1")
        );
    }
}