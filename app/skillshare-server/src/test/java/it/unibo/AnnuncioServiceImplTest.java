package it.unibo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica il servizio dedicato alla gestione degli annunci.
 */
public class AnnuncioServiceImplTest {

    private AnnuncioServiceImpl service;

    /**
     * Prepara l'ambiente prima di ogni test.
     * Inizializza il servizio e pulisce gli annunci salvati.
     */
    @BeforeEach
    void inizializzaTest() {

        service = new AnnuncioServiceImpl();

        DatabaseCore.getDB()
                .hashMap("annunci")
                .createOrOpen()
                .clear();

        DatabaseCore.commit();
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

    /**
     * Verifica che un utente possa recuperare i propri annunci.
     */
    @Test
    void mieiAnnunciRestituisceGliAnnunciDellUtente() {

        String idUtente = "utente1";

        service.crea(
                idUtente,
                "Java",
                "Lezioni Java",
                "Informatica",
                "Sì",
                "Inglese"
        );

        List<Annuncio> annunci = service.mieiAnnunci(idUtente);

        assertEquals(
                1,
                annunci.size()
        );

        assertEquals(
                "Java",
                annunci.get(0).getTitolo()
        );
    }

    /**
     * Verifica che un utente senza annunci pubblicati
     * riceva una lista vuota.
     */
    @Test
    void mieiAnnunciSenzaAnnunciRestituisceListaVuota() {
        List<Annuncio> annunci = service.mieiAnnunci("utente_senza_annunci");
        assertNotNull(annunci);
        assertTrue(annunci.isEmpty());
    }
}
