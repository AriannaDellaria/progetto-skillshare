package it.unibo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValutazioneServiceImplTest {

    private ValutazioneServiceImpl service;
    private RichiestaScambioServiceImpl richiestaScambioService;
    private AnnuncioServiceImpl annuncioService;

    @BeforeEach
    void inizializzaTest() {
        service = new ValutazioneServiceImpl();
        richiestaScambioService = new RichiestaScambioServiceImpl();
        annuncioService = new AnnuncioServiceImpl();

        DatabaseCore.getDB().hashMap("annunci").createOrOpen().clear();
        DatabaseCore.getDB().hashMap("richiesteScambio").createOrOpen().clear();
        DatabaseCore.getDB().hashMap("valutazioni").createOrOpen().clear();
        DatabaseCore.commit();
    }

    // Crea uno scambio completo, dallo stato IN_ATTESA fino a CONCLUSA,
    // restituendo la richiesta pronta per essere valutata.
    private RichiestaScambio creaScambioConcluso() {
        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        richiestaScambioService.accetta(richiesta.getId(), "autore1");
        richiestaScambioService.termina(richiesta.getId(), "autore1");

        return richiesta;
    }

    /**
     * Verifica che il mittente possa assegnare correttamente una
     * valutazione valida, di tipo PRESTAZIONE, al proprietario.
     */
    @Test
    void assegnaConMittenteRestituisceValutazioneDiPrestazione() {

        RichiestaScambio richiesta = creaScambioConcluso();

        Valutazione valutazione = service.assegna(
                richiesta.getId(), "mittente1", 8
        );

        assertNotNull(valutazione);
        assertEquals(richiesta.getId(), valutazione.getIdRichiesta());
        assertEquals("mittente1", valutazione.getIdMittente());
        assertEquals("autore1", valutazione.getIdUtenteValutato());
        assertEquals(8, valutazione.getPunteggio());
        assertEquals(TipoValutazione.PRESTAZIONE, valutazione.getTipo());
    }

    /**
     * Verifica che il proprietario possa assegnare correttamente una
     * valutazione valida, di tipo CONTROPRESTAZIONE, al mittente.
     */
    @Test
    void assegnaConProprietarioRestituisceValutazioneDiControprestazione() {

        RichiestaScambio richiesta = creaScambioConcluso();

        Valutazione valutazione = service.assegna(
                richiesta.getId(), "autore1", 6
        );

        assertNotNull(valutazione);
        assertEquals(richiesta.getId(), valutazione.getIdRichiesta());
        assertEquals("autore1", valutazione.getIdMittente());
        assertEquals("mittente1", valutazione.getIdUtenteValutato());
        assertEquals(6, valutazione.getPunteggio());
        assertEquals(TipoValutazione.CONTROPRESTAZIONE, valutazione.getTipo());
    }

    /**
     * Verifica che entrambi i partecipanti possano valutare lo
     * stesso scambio, senza che l'una impedisca l'altra.
     */
    @Test
    void assegnaConEntrambiIPartecipantiRestituisceDueValutazioniDistinte() {

        RichiestaScambio richiesta = creaScambioConcluso();

        Valutazione daMittente = service.assegna(richiesta.getId(), "mittente1", 8);
        Valutazione daProprietario = service.assegna(richiesta.getId(), "autore1", 5);

        assertNotNull(daMittente);
        assertNotNull(daProprietario);
        assertEquals(TipoValutazione.PRESTAZIONE, daMittente.getTipo());
        assertEquals(TipoValutazione.CONTROPRESTAZIONE, daProprietario.getTipo());
    }

    /**
     * Verifica che gli estremi dell'intervallo consentito
     * (1 e 10) siano accettati.
     */
    @Test
    void assegnaConPunteggioAgliEstremiDellIntervalloRestituisceValutazione() {

        RichiestaScambio richiesta = creaScambioConcluso();

        Valutazione valutazioneMin = service.assegna(richiesta.getId(), "mittente1", 1);
        assertNotNull(valutazioneMin);
        assertEquals(1, valutazioneMin.getPunteggio());

        Valutazione valutazioneMax = service.assegna(richiesta.getId(), "autore1", 10);
        assertNotNull(valutazioneMax);
        assertEquals(10, valutazioneMax.getPunteggio());
    }

    /**
     * Verifica che un punteggio inferiore al minimo consentito
     * venga rifiutato, restituendo null.
     */
    @Test
    void assegnaConPunteggioSottoIlMinimoRestituisceNull() {

        RichiestaScambio richiesta = creaScambioConcluso();

        Valutazione valutazione = service.assegna(richiesta.getId(), "mittente1", 0);

        assertNull(valutazione);
    }

    /**
     * Verifica che un punteggio superiore al massimo consentito
     * venga rifiutato, restituendo null.
     */
    @Test
    void assegnaConPunteggioSopraIlMassimoRestituisceNull() {

        RichiestaScambio richiesta = creaScambioConcluso();

        Valutazione valutazione = service.assegna(richiesta.getId(), "mittente1", 11);

        assertNull(valutazione);
    }

    /**
     * Verifica che non si possa valutare uno scambio ancora
     * in attesa, mai accettato né concluso.
     */
    @Test
    void assegnaConRichiestaInAttesaRestituisceNull() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        Valutazione valutazione = service.assegna(richiesta.getId(), "mittente1", 5);

        assertNull(valutazione);
    }

    /**
     * Verifica che non si possa valutare uno scambio accettato
     * ma non ancora concluso.
     */
    @Test
    void assegnaConRichiestaAccettataMaNonConclusaRestituisceNull() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        richiestaScambioService.accetta(richiesta.getId(), "autore1");

        Valutazione valutazione = service.assegna(richiesta.getId(), "mittente1", 5);

        assertNull(valutazione);
    }

    /**
     * Verifica che non si possa valutare uno scambio rifiutato.
     */
    @Test
    void assegnaConRichiestaRifiutataRestituisceNull() {

        Annuncio annuncio = annuncioService.crea(
                "autore1", "Titolo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        RichiestaScambio richiesta = richiestaScambioService.invia(
                annuncio.getId(), "mittente1", "Messaggio"
        );

        richiestaScambioService.rifiuta(richiesta.getId(), "autore1");

        Valutazione valutazione = service.assegna(richiesta.getId(), "mittente1", 5);

        assertNull(valutazione);
    }

    /**
     * Verifica che un utente estraneo, non partecipante allo
     * scambio, non possa assegnare una valutazione.
     */
    @Test
    void assegnaConUtenteEstraneoRestituisceNull() {

        RichiestaScambio richiesta = creaScambioConcluso();

        Valutazione valutazione = service.assegna(richiesta.getId(), "estraneo1", 5);

        assertNull(valutazione);
    }

    /**
     * Verifica che il mittente non possa inserire una seconda
     * valutazione per lo stesso scambio già valutato da lui.
     */
    @Test
    void assegnaConMittenteCheHaGiaValutatoRestituisceNull() {

        RichiestaScambio richiesta = creaScambioConcluso();

        service.assegna(richiesta.getId(), "mittente1", 7);

        Valutazione secondoTentativo = service.assegna(richiesta.getId(), "mittente1", 9);

        assertNull(secondoTentativo);
    }

    /**
     * Verifica che il proprietario non possa inserire una seconda
     * valutazione per lo stesso scambio già valutato da lui, anche
     * se il mittente ha già valutato.
     */
    @Test
    void assegnaConProprietarioCheHaGiaValutatoRestituisceNull() {

        RichiestaScambio richiesta = creaScambioConcluso();

        service.assegna(richiesta.getId(), "mittente1", 7);
        service.assegna(richiesta.getId(), "autore1", 4);

        Valutazione secondoTentativoProprietario = service.assegna(richiesta.getId(), "autore1", 9);

        assertNull(secondoTentativoProprietario);
    }

    /**
     * Verifica che non si possa valutare una richiesta inesistente.
     */
    @Test
    void assegnaConRichiestaInesistenteRestituisceNull() {

        Valutazione valutazione = service.assegna("id-inesistente", "mittente1", 5);

        assertNull(valutazione);
    }

    /**
     * Verifica che l'id della richiesta sia obbligatorio.
     */
    @Test
    void assegnaSenzaIdRichiestaRestituisceNull() {

        Valutazione valutazione = service.assegna(null, "mittente1", 5);

        assertNull(valutazione);
    }

    /**
     * Verifica che l'id dell'utente corrente sia obbligatorio.
     */
    @Test
    void assegnaSenzaIdUtenteCorrenteRestituisceNull() {

        RichiestaScambio richiesta = creaScambioConcluso();

        Valutazione valutazione = service.assegna(richiesta.getId(), null, 5);

        assertNull(valutazione);
    }


    /**
     * Verifica che valutazioneGiaEsistente restituisca false prima
     * che sia stata inserita alcuna valutazione.
     */
    @Test
    void valutazioneGiaEsistenteSenzaValutazioniRestituisceFalse() {

        RichiestaScambio richiesta = creaScambioConcluso();

        assertFalse(service.valutazioneGiaEsistente(richiesta.getId(), "mittente1"));
    }

    /**
     * Verifica che valutazioneGiaEsistente restituisca true per il
     * mittente dopo che ha assegnato la propria valutazione.
     */
    @Test
    void valutazioneGiaEsistenteConMittenteCheHaValutatoRestituisceTrue() {

        RichiestaScambio richiesta = creaScambioConcluso();

        service.assegna(richiesta.getId(), "mittente1", 7);

        assertTrue(service.valutazioneGiaEsistente(richiesta.getId(), "mittente1"));
    }

    /**
     * Verifica che la valutazione del mittente non influenzi il
     * controllo per il proprietario.
     */
    @Test
    void valutazioneGiaEsistenteConMittenteCheHaValutatoNonInfluenzaProprietario() {

        RichiestaScambio richiesta = creaScambioConcluso();

        service.assegna(richiesta.getId(), "mittente1", 7);

        assertFalse(service.valutazioneGiaEsistente(richiesta.getId(), "autore1"));
    }

    /**
     * Verifica che valutazioneGiaEsistente restituisca false per
     * un utente non partecipante allo scambio.
     */
    @Test
    void valutazioneGiaEsistenteConUtenteEstraneoRestituisceFalse() {

        RichiestaScambio richiesta = creaScambioConcluso();

        assertFalse(service.valutazioneGiaEsistente(richiesta.getId(), "estraneo1"));
    }

    /**
     * Verifica che valutazioneGiaEsistente restituisca false quando
     * i parametri passati sono null o vuoti.
     */
    @Test
    void valutazioneGiaEsistenteConParametriNonValidiRestituisceFalse() {

        assertFalse(service.valutazioneGiaEsistente(null, "mittente1"));
        assertFalse(service.valutazioneGiaEsistente("richiesta1", null));
        assertFalse(service.valutazioneGiaEsistente("", "mittente1"));
    }
}