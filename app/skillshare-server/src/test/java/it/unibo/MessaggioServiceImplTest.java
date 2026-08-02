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
}