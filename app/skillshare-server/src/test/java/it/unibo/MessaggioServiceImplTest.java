package it.unibo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MessaggioServiceImplTest {

    private MessaggioServiceImpl service;
    private RichiestaScambioServiceImpl richiestaScambioService;
    private AnnuncioServiceImpl annuncioService;

    @BeforeEach
    void inizializzaTest() {
        service = new MessaggioServiceImpl();
        richiestaScambioService = new RichiestaScambioServiceImpl();
        annuncioService = new AnnuncioServiceImpl();

        DatabaseCore.getDB().hashMap("annunci").createOrOpen().clear();
        DatabaseCore.getDB().hashMap("richiesteScambio").createOrOpen().clear();
        DatabaseCore.getDB().hashMap("messaggi").createOrOpen().clear();
        DatabaseCore.commit();
    }

    /**
     * Verifica che il mittente possa aprire la chat di una
     * richiesta accettata.
     */
    @Test
    void apriChatConMittenteERichiestaAccettataRestituisceCronologia() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        richiestaScambioService.accetta(richiesta.getId(), "autore1");

        List<Messaggio> cronologia = service.apriChat(richiesta.getId(), "mittente1");

        assertNotNull(cronologia);
    }

    /**
     * Verifica che il proprietario dell'annuncio possa aprire
     * la chat di una richiesta accettata.
     */
    @Test
    void apriChatConProprietarioERichiestaAccettataRestituisceCronologia() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        richiestaScambioService.accetta(richiesta.getId(), "autore1");

        List<Messaggio> cronologia = service.apriChat(richiesta.getId(), "autore1");

        assertNotNull(cronologia);
    }

    /**
     * Verifica che un utente estraneo non possa aprire la chat.
     */
    @Test
    void apriChatConUtenteNonPartecipanteRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        richiestaScambioService.accetta(richiesta.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.apriChat(richiesta.getId(), "estraneo1")
        );
    }

    /**
     * Verifica che non si possa accedere alla chat di una richiesta
     * ancora in attesa.
     */
    @Test
    void apriChatConRichiestaInAttesaRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.apriChat(richiesta.getId(), "mittente1")
        );
    }

    /**
     * Verifica che non si possa accedere alla chat di una richiesta
     * rifiutata.
     */
    @Test
    void apriChatConRichiestaRifiutataRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        richiestaScambioService.rifiuta(richiesta.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.apriChat(richiesta.getId(), "mittente1")
        );
    }

    /**
     * Verifica che non si possa aprire la chat di una richiesta
     * inesistente.
     */
    @Test
    void apriChatConRichiestaInesistenteRestituisceErrore() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.apriChat("id-inesistente", "mittente1")
        );
    }

    /**
     * Verifica che un partecipante possa inviare un messaggio valido
     * in una chat di una richiesta accettata.
     */
    @Test
    void inviaConDatiValidiRestituisceMessaggio() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio richiesta"
        );

        richiestaScambioService.accetta(richiesta.getId(), "autore1");

        Messaggio messaggio = service.invia(richiesta.getId(), "mittente1", "Ciao, a che ora ci vediamo?");

        assertNotNull(messaggio);
        assertEquals(richiesta.getId(), messaggio.getIdRichiesta());
        assertEquals("mittente1", messaggio.getIdMittente());
        assertEquals("Ciao, a che ora ci vediamo?", messaggio.getTesto());
    }

    /**
     * Verifica che un messaggio con testo vuoto venga rifiutato.
     */
    @Test
    void inviaConTestoVuotoRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio richiesta"
        );

        richiestaScambioService.accetta(richiesta.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.invia(richiesta.getId(), "mittente1", "   ")
        );
    }

    /**
     * Verifica che un utente non partecipante non possa inviare messaggi.
     */
    @Test
    void inviaConUtenteNonPartecipanteRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio richiesta"
        );

        richiestaScambioService.accetta(richiesta.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.invia(richiesta.getId(), "estraneo1", "Ciao")
        );
    }

    /**
     * Verifica che non si possa inviare un messaggio in una chat
     * di una richiesta non ancora accettata.
     */
    @Test
    void inviaConRichiestaInAttesaRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio richiesta"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.invia(richiesta.getId(), "mittente1", "Ciao")
        );
    }

    /**
     * Verifica che i messaggi inviati compaiano poi nella cronologia
     * restituita da apriChat.
     */
    @Test
    void messaggioInviatoCompareNellaCronologia() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio richiesta"
        );

        richiestaScambioService.accetta(richiesta.getId(), "autore1");

        service.invia(richiesta.getId(), "mittente1", "Primo messaggio");
        service.invia(richiesta.getId(), "autore1", "Risposta");

        List<Messaggio> cronologia = service.apriChat(richiesta.getId(), "mittente1");

        assertEquals(2, cronologia.size());
    }

    /**
     * Verifica che il proprietario possa condividere un link valido.
     */
    @Test
    void condividiLinkConProprietarioELinkValidoRestituisceMessaggio() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        richiestaScambioService.accetta(richiesta.getId(), "autore1");

        Messaggio messaggio = service.condividiLink(
                richiesta.getId(), "autore1", "https://meet.esempio.it/incontro"
        );

        assertNotNull(messaggio);
        assertTrue(messaggio.isLink());
        assertEquals("https://meet.esempio.it/incontro", messaggio.getTesto());
    }

    /**
     * Verifica che il mittente non possa condividere link.
     */
    @Test
    void condividiLinkConMittenteRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        richiestaScambioService.accetta(richiesta.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.condividiLink(richiesta.getId(), "mittente1", "https://esempio.it")
        );
    }

    /**
     * Verifica che un link non valido venga rifiutato.
     */
    @Test
    void condividiLinkConLinkNonValidoRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        richiestaScambioService.accetta(richiesta.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.condividiLink(richiesta.getId(), "autore1", "non-e-un-link")
        );
    }

    /**
     * Verifica che non si possa condividere un link in una chat
     * di una richiesta non ancora accettata.
     */
    @Test
    void condividiLinkConRichiestaInAttesaRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.condividiLink(richiesta.getId(), "autore1", "https://esempio.it")
        );
    }

    /**
     * Verifica che non si possa più accedere alla chat (apriChat)
     * di uno scambio dopo che è stato terminato.
     */
    @Test
    void apriChatConRichiestaConclusaRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        richiestaScambioService.accetta(richiesta.getId(), "autore1");
        richiestaScambioService.termina(richiesta.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.apriChat(richiesta.getId(), "mittente1")
        );
    }

    /**
     * Verifica che non si possano più inviare messaggi in una chat
     * dopo che lo scambio è stato terminato.
     */
    @Test
    void inviaConRichiestaConclusaRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        richiestaScambioService.accetta(richiesta.getId(), "autore1");
        richiestaScambioService.termina(richiesta.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.invia(richiesta.getId(), "mittente1", "Ciao")
        );
    }

    /**
     * Verifica che non si possa più condividere un link
     * dopo che lo scambio è stato terminato.
     */
    @Test
    void condividiLinkConRichiestaConclusaRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        richiestaScambioService.accetta(richiesta.getId(), "autore1");
        richiestaScambioService.termina(richiesta.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.condividiLink(richiesta.getId(), "autore1", "https://esempio.it")
        );
    }

    /**
     * Verifica che il blocco valga anche per il proprietario,
     * non solo per il mittente, dopo la conclusione dello scambio.
     */
    @Test
    void apriChatConProprietarioERichiestaConclusaRestituisceErrore() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        richiestaScambioService.accetta(richiesta.getId(), "autore1");
        richiestaScambioService.termina(richiesta.getId(), "autore1");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.apriChat(richiesta.getId(), "autore1")
        );
    }
}