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

    // Genera una stringa della lunghezza indicata, utile per i test
    // sui limiti del commento.
    private String stringaDiLunghezza(int lunghezza) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lunghezza; i++) {
            builder.append("a");
        }
        return builder.toString();
    }

    /**
     * Verifica che il mittente possa assegnare correttamente una
     * valutazione con un commento valido.
     */
    @Test
    void assegnaConCommentoValidoRestituisceValutazioneConCommento() {

        RichiestaScambio richiesta = creaScambioConcluso();

        Valutazione valutazione = service.assegna(
                richiesta.getId(), "mittente1", 8, "Esperienza molto positiva."
        );

        assertNotNull(valutazione);
        assertEquals("Esperienza molto positiva.", valutazione.getCommento());
    }

    /**
     * Verifica che la valutazione possa essere assegnata anche
     * senza commento, dato che è facoltativo.
     */
    @Test
    void assegnaSenzaCommentoRestituisceValutazione() {

        RichiestaScambio richiesta = creaScambioConcluso();

        Valutazione valutazione = service.assegna(
                richiesta.getId(), "mittente1", 8, ""
        );

        assertNotNull(valutazione);
        assertEquals("", valutazione.getCommento());
    }

    /**
     * Verifica che la valutazione possa essere assegnata anche
     * con commento null.
     */
    @Test
    void assegnaConCommentoNullRestituisceValutazione() {

        RichiestaScambio richiesta = creaScambioConcluso();

        Valutazione valutazione = service.assegna(
                richiesta.getId(), "mittente1", 8, null
        );

        assertNotNull(valutazione);
    }

    /**
     * Verifica che un commento esattamente alla lunghezza massima
     * consentita venga accettato.
     */
    @Test
    void assegnaConCommentoAllaLunghezzaMassimaRestituisceValutazione() {

        RichiestaScambio richiesta = creaScambioConcluso();

        String commento = stringaDiLunghezza(Valutazione.COMMENTO_LUNGHEZZA_MASSIMA);

        Valutazione valutazione = service.assegna(
                richiesta.getId(), "mittente1", 8, commento
        );

        assertNotNull(valutazione);
        assertEquals(Valutazione.COMMENTO_LUNGHEZZA_MASSIMA, valutazione.getCommento().length());
    }

    /**
     * Verifica che un commento che supera la lunghezza massima
     * consentita venga rifiutato, restituendo null.
     */
    @Test
    void assegnaConCommentoTroppoLungoRestituisceNull() {

        RichiestaScambio richiesta = creaScambioConcluso();

        String commentoTroppoLungo = stringaDiLunghezza(Valutazione.COMMENTO_LUNGHEZZA_MASSIMA + 1);

        Valutazione valutazione = service.assegna(
                richiesta.getId(), "mittente1", 8, commentoTroppoLungo
        );

        assertNull(valutazione);
    }

    /**
     * Verifica che un commento troppo lungo non venga comunque
     * salvato nel repository, nemmeno parzialmente.
     */
    @Test
    void assegnaConCommentoTroppoLungoNonSalvaNulla() {

        RichiestaScambio richiesta = creaScambioConcluso();

        String commentoTroppoLungo = stringaDiLunghezza(Valutazione.COMMENTO_LUNGHEZZA_MASSIMA + 50);

        service.assegna(richiesta.getId(), "mittente1", 8, commentoTroppoLungo);

        assertFalse(service.valutazioneGiaEsistente(richiesta.getId(), "mittente1"));
    }

    /**
     * Verifica che il proprietario possa assegnare correttamente una
     * valutazione con commento, di tipo CONTROPRESTAZIONE.
     */
    @Test
    void assegnaConProprietarioECommentoRestituisceValutazioneDiControprestazione() {

        RichiestaScambio richiesta = creaScambioConcluso();

        Valutazione valutazione = service.assegna(
                richiesta.getId(), "autore1", 6, "Controprestazione adeguata."
        );

        assertNotNull(valutazione);
        assertEquals(TipoValutazione.CONTROPRESTAZIONE, valutazione.getTipo());
        assertEquals("Controprestazione adeguata.", valutazione.getCommento());
    }

    /**
     * Verifica che gli estremi dell'intervallo di punteggio
     * consentito siano accettati anche insieme a un commento.
     */
    @Test
    void assegnaConPunteggioAgliEstremiECommentoRestituisceValutazione() {

        RichiestaScambio richiesta = creaScambioConcluso();

        Valutazione valutazioneMin = service.assegna(richiesta.getId(), "mittente1", 1, "Minimo");
        assertNotNull(valutazioneMin);
        assertEquals(1, valutazioneMin.getPunteggio());

        Valutazione valutazioneMax = service.assegna(richiesta.getId(), "autore1", 10, "Massimo");
        assertNotNull(valutazioneMax);
        assertEquals(10, valutazioneMax.getPunteggio());
    }

    /**
     * Verifica che un punteggio fuori range venga rifiutato anche
     * se il commento è valido.
     */
    @Test
    void assegnaConPunteggioNonValidoECommentoValidoRestituisceNull() {

        RichiestaScambio richiesta = creaScambioConcluso();

        Valutazione valutazione = service.assegna(
                richiesta.getId(), "mittente1", 11, "Commento valido"
        );

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

        Valutazione valutazione = service.assegna(richiesta.getId(), "mittente1", 5, "Commento");

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

        Valutazione valutazione = service.assegna(richiesta.getId(), "mittente1", 5, "Commento");

        assertNull(valutazione);
    }

    /**
     * Verifica che un utente estraneo, non partecipante allo
     * scambio, non possa assegnare una valutazione.
     */
    @Test
    void assegnaConUtenteEstraneoRestituisceNull() {

        RichiestaScambio richiesta = creaScambioConcluso();

        Valutazione valutazione = service.assegna(richiesta.getId(), "estraneo1", 5, "Commento");

        assertNull(valutazione);
    }

    /**
     * Verifica che non si possa inserire una seconda valutazione
     * per lo stesso scambio già valutato nella stessa direzione.
     */
    @Test
    void assegnaConValutazioneGiaEsistenteRestituisceNull() {

        RichiestaScambio richiesta = creaScambioConcluso();

        service.assegna(richiesta.getId(), "mittente1", 7, "Primo commento");

        Valutazione secondoTentativo = service.assegna(
                richiesta.getId(), "mittente1", 9, "Secondo commento"
        );

        assertNull(secondoTentativo);
    }

    /**
     * Verifica che il commento del primo tentativo resti quello
     * salvato, anche dopo un secondo tentativo fallito.
     */
    @Test
    void assegnaConValutazioneGiaEsistenteNonSovrascriveIlCommentoOriginale() {

        RichiestaScambio richiesta = creaScambioConcluso();

        service.assegna(richiesta.getId(), "mittente1", 7, "Commento originale");
        service.assegna(richiesta.getId(), "mittente1", 3, "Tentativo di sovrascrittura");

        boolean giaEsistente = service.valutazioneGiaEsistente(richiesta.getId(), "mittente1");

        assertTrue(giaEsistente);
    }

    /**
     * Verifica che non si possa valutare una richiesta inesistente.
     */
    @Test
    void assegnaConRichiestaInesistenteRestituisceNull() {

        Valutazione valutazione = service.assegna("id-inesistente", "mittente1", 5, "Commento");

        assertNull(valutazione);
    }

    /**
     * Verifica che l'id della richiesta sia obbligatorio.
     */
    @Test
    void assegnaSenzaIdRichiestaRestituisceNull() {

        Valutazione valutazione = service.assegna(null, "mittente1", 5, "Commento");

        assertNull(valutazione);
    }

    /**
     * Verifica che l'id dell'utente corrente sia obbligatorio.
     */
    @Test
    void assegnaSenzaIdUtenteCorrenteRestituisceNull() {

        RichiestaScambio richiesta = creaScambioConcluso();

        Valutazione valutazione = service.assegna(richiesta.getId(), null, 5, "Commento");

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

        service.assegna(richiesta.getId(), "mittente1", 7, "Commento");

        assertTrue(service.valutazioneGiaEsistente(richiesta.getId(), "mittente1"));
    }

    /**
     * Verifica che la valutazione del mittente non influenzi il
     * controllo per il proprietario: le due direzioni sono
     * indipendenti.
     */
    @Test
    void valutazioneGiaEsistenteConMittenteCheHaValutatoNonInfluenzaProprietario() {

        RichiestaScambio richiesta = creaScambioConcluso();

        service.assegna(richiesta.getId(), "mittente1", 7, "Commento");

        assertFalse(service.valutazioneGiaEsistente(richiesta.getId(), "autore1"));
    }

    /**
     * Verifica che calcolaRating calcoli correttamente la media
     * delle valutazioni di prestazione ricevute da un utente.
     */
    @Test
    void calcolaRatingConValutazioniDiPrestazioneCalcolaLaMediaCorretta() {

        Annuncio annuncio1 = annuncioService.crea(
                "autore1", "Titolo1", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );
        RichiestaScambio richiesta1 = richiestaScambioService.invia(
                annuncio1.getId(), "mittente1", "Messaggio"
        );
        richiestaScambioService.accetta(richiesta1.getId(), "autore1");
        richiestaScambioService.termina(richiesta1.getId(), "autore1");
        service.assegna(richiesta1.getId(), "mittente1", 8, "Buono");

        Annuncio annuncio2 = annuncioService.crea(
                "autore1", "Titolo2", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );
        RichiestaScambio richiesta2 = richiestaScambioService.invia(
                annuncio2.getId(), "mittente2", "Messaggio"
        );
        richiestaScambioService.accetta(richiesta2.getId(), "autore1");
        richiestaScambioService.termina(richiesta2.getId(), "autore1");
        service.assegna(richiesta2.getId(), "mittente2", 6, "Nella media");

        RatingUtente rating = service.calcolaRating("autore1");

        assertEquals(7.0, rating.getMediaPrestazione());
        assertEquals(2, rating.getNumeroRecensioniPrestazione());
    }

    /**
     * Verifica che calcolaRating calcoli correttamente la
     * media delle valutazioni di controprestazione.
     */
    @Test
    void calcolaRatingConValutazioniDiControprestazioneCalcolaLaMediaCorretta() {

        RichiestaScambio richiesta = creaScambioConcluso();

        service.assegna(richiesta.getId(), "autore1", 4, "Controprestazione scarsa");

        RatingUtente rating = service.calcolaRating("mittente1");

        assertEquals(4.0, rating.getMediaControprestazione());
        assertEquals(1, rating.getNumeroRecensioniControprestazione());
    }

    /**
     * Verifica che le due direzioni (prestazione e controprestazione)
     * siano calcolate in modo indipendente per lo stesso utente,
     * quando ricopre entrambi i ruoli in scambi diversi.
     */
    @Test
    void calcolaRatingConEntrambeLeDirezioniLeTieneSeparate() {

        // autore1 riceve una valutazione di prestazione da mittente1
        RichiestaScambio richiesta1 = creaScambioConcluso();
        service.assegna(richiesta1.getId(), "mittente1", 9, "Ottimo");

        // autore1 riceve anche una valutazione di controprestazione,
        // partecipando come mittente di un altro scambio
        Annuncio annuncio2 = annuncioService.crea(
                "autore2", "Titolo2", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );
        RichiestaScambio richiesta2 = richiestaScambioService.invia(
                annuncio2.getId(), "autore1", "Messaggio"
        );
        richiestaScambioService.accetta(richiesta2.getId(), "autore2");
        richiestaScambioService.termina(richiesta2.getId(), "autore2");
        service.assegna(richiesta2.getId(), "autore2", 5, "Nella media");

        RatingUtente rating = service.calcolaRating("autore1");

        assertEquals(9.0, rating.getMediaPrestazione());
        assertEquals(1, rating.getNumeroRecensioniPrestazione());
        assertEquals(5.0, rating.getMediaControprestazione());
        assertEquals(1, rating.getNumeroRecensioniControprestazione());
    }

    /**
     * Verifica che un utente senza alcuna recensione abbia
     * numeroRecensioni pari a zero per entrambe le direzioni, e che
     * questo sia distinguibile da una media effettivamente pari a zero.
     */
    @Test
    void calcolaRatingSenzaRecensioniRestituisceContatoriAZero() {

        RatingUtente rating = service.calcolaRating("utente-senza-recensioni");

        assertEquals(0, rating.getNumeroRecensioniPrestazione());
        assertEquals(0, rating.getNumeroRecensioniControprestazione());
        assertFalse(rating.haRecensioniPrestazione());
        assertFalse(rating.haRecensioniControprestazione());
    }

    /**
     * Verifica che haRecensioniPrestazione/Controprestazione
     * distinguano correttamente la presenza di almeno una recensione.
     */
    @Test
    void calcolaRatingConUnaRecensioneRestituisceHaRecensioniTrue() {

        RichiestaScambio richiesta = creaScambioConcluso();
        service.assegna(richiesta.getId(), "mittente1", 5, "Commento");

        RatingUtente rating = service.calcolaRating("autore1");

        assertTrue(rating.haRecensioniPrestazione());
        assertFalse(rating.haRecensioniControprestazione());
    }

    /**
     * Verifica che calcolaRating restituisca contatori a zero
     * quando l'id utente passato è null o vuoto.
     */
    @Test
    void calcolaRatingConIdUtenteNonValidoRestituisceContatoriAZero() {

        RatingUtente ratingNull = service.calcolaRating(null);
        assertEquals(0, ratingNull.getNumeroRecensioniPrestazione());
        assertEquals(0, ratingNull.getNumeroRecensioniControprestazione());

        RatingUtente ratingVuoto = service.calcolaRating("");
        assertEquals(0, ratingVuoto.getNumeroRecensioniPrestazione());
        assertEquals(0, ratingVuoto.getNumeroRecensioniControprestazione());
    }

    /**
     * Verifica che la media sia calcolata correttamente anche con
     * punteggi che producono un risultato non intero.
     */
    @Test
    void calcolaRatingConMediaNonInteraCalcolaCorrettamente() {

        Annuncio annuncio1 = annuncioService.crea(
                "autore3", "Titolo1", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );
        RichiestaScambio richiesta1 = richiestaScambioService.invia(
                annuncio1.getId(), "mittente1", "Messaggio"
        );
        richiestaScambioService.accetta(richiesta1.getId(), "autore3");
        richiestaScambioService.termina(richiesta1.getId(), "autore3");
        service.assegna(richiesta1.getId(), "mittente1", 7, "Commento");

        Annuncio annuncio2 = annuncioService.crea(
                "autore3", "Titolo2", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );
        RichiestaScambio richiesta2 = richiestaScambioService.invia(
                annuncio2.getId(), "mittente2", "Messaggio"
        );
        richiestaScambioService.accetta(richiesta2.getId(), "autore3");
        richiestaScambioService.termina(richiesta2.getId(), "autore3");
        service.assegna(richiesta2.getId(), "mittente2", 8, "Commento");

        RatingUtente rating = service.calcolaRating("autore3");

        assertEquals(7.5, rating.getMediaPrestazione());
    }
}