package it.unibo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica il servizio dedicato alla gestione degli annunci.
 */
public class AnnuncioServiceImplTest {

    private AnnuncioServiceImpl service;

    /**
     * Crea il servizio reale prima di ogni test.
     */
    @BeforeEach
    public void setUp() {
        service = new AnnuncioServiceImpl();
    }


    /**
     * Verifica che un utente autenticato
     * possa creare un annuncio valido.
     */
    @Test
    void creaAnnuncioConDatiValidiRestituisceAnnuncio() {

        String idUtente =
                "utente_" + System.nanoTime();


        Annuncio annuncio = service.crea(
                idUtente,
                "Lezioni di Java",
                "Offro lezioni di programmazione",
                "Informatica",
                "Sì",
                "Lezioni di inglese"
        );

        assertNotNull(annuncio);

        assertEquals(
                "Lezioni di Java",
                annuncio.getTitolo()
        );

        assertEquals(
                idUtente,
                annuncio.getIdAutore()
        );

        assertEquals(
                "Sì",
                annuncio.getDisponibilita()
        );
    }

    /**
     * Verifica che il titolo sia obbligatorio.
     */
    @Test
    void creaAnnuncioSenzaTitoloRestituisceErrore() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.crea(
                        "utente1",
                        "",
                        "Descrizione valida",
                        "Informatica",
                        "Sì",
                        "Inglese"
                )
        );
    }

    /**
     * Verifica che la descrizione sia obbligatoria.
     */
    @Test
    void creaAnnuncioSenzaDescrizioneRestituisceErrore() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.crea(
                        "utente1",
                        "Titolo valido",
                        "",
                        "Informatica",
                        "Sì",
                        "Inglese"
                )
        );
    }

    /**
     * Verifica che la controprestazione sia obbligatoria.
     */
    @Test
    void creaAnnuncioSenzaControprestazioneRestituisceErrore() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.crea(
                        "utente1",
                        "Titolo valido",
                        "Descrizione valida",
                        "Informatica",
                        "Sì",
                        ""
                )
        );
    }

    /**
     * Verifica che un utente non autenticato
     * non possa creare annunci.
     */
    @Test
    void creaAnnuncioSenzaUtenteRestituisceErrore() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.crea(
                        null,
                        "Titolo valido",
                        "Descrizione valida",
                        "Informatica",
                        "Sì",
                        "Inglese"
                )
        );
    }

    /**
     * Verifica che tutti i dati dell'annuncio
     * vengano salvati correttamente.
     */
    @Test
    void creaAnnuncioSalvaTuttiICampi() {

        Annuncio annuncio = service.crea(
                "utente1",
                "Ripetizioni SQL",
                "Aiuto con database",
                "Database",
                "No",
                "Aiuto matematica"
        );

        assertEquals(
                "Ripetizioni SQL",
                annuncio.getTitolo()
        );

        assertEquals(
                "Aiuto con database",
                annuncio.getDescrizione()
        );

        assertEquals(
                "Database",
                annuncio.getCategoria()
        );

        assertEquals(
                "No",
                annuncio.getDisponibilita()
        );

        assertEquals(
                "Aiuto matematica",
                annuncio.getControprestazione()
        );
    }
}
