package it.unibo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /**
     * Verifica che un utente proprietario
     * possa modificare correttamente un annuncio.
     */
    @Test
    void modificaAnnuncioAggiornaIValori() {

        String idUtente = "utente1";

        Annuncio annuncio = service.crea(
                idUtente,
                "Lezioni Java",
                "Offro lezioni base",
                "Informatica",
                "Sì",
                "Lezioni inglese"
        );

        Annuncio aggiornato = service.modificaAnnuncio(
                annuncio.getId(),
                idUtente,
                "Lezioni Java avanzate",
                "Programmazione",
                "Offro lezioni avanzate",
                "No",
                "Ripetizioni matematica"
        );


        assertNotNull(aggiornato);

        assertEquals(
                "Lezioni Java avanzate",
                aggiornato.getTitolo()
        );

        assertEquals(
                "Programmazione",
                aggiornato.getCategoria()
        );

        assertEquals(
                "No",
                aggiornato.getDisponibilita()
        );

        assertEquals(
                "Ripetizioni matematica",
                aggiornato.getControprestazione()
        );
    }


    /**
     * Verifica che la modifica di un annuncio inesistente
     * venga rifiutata.
     */
    @Test
    void modificaAnnuncioInesistenteRestituisceErrore() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.modificaAnnuncio(
                        "id inesistente",
                        "utente1",
                        "Titolo",
                        "Informatica",
                        "Descrizione",
                        "Sì",
                        "Inglese"
                )
        );
    }


    /**
     * Verifica che un campo obbligatorio vuoto
     * impedisca la modifica dell'annuncio.
     */
    @Test
    void modificaAnnuncioConTitoloVuotoRestituisceErrore() {

        Annuncio annuncio = service.crea(
                "utente1",
                "Titolo originale",
                "Descrizione valida",
                "Informatica",
                "Sì",
                "Inglese"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.modificaAnnuncio(
                        annuncio.getId(),
                        "utente1",
                        "",
                        "Informatica",
                        "Descrizione valida",
                        "Sì",
                        "Inglese"
                )
        );
    }


    /**
     * Verifica che i nuovi valori sostituiscano
     * quelli precedentemente salvati.
     */
    @Test
    void modificaAnnuncioSostituisceIValoriPrecedenti() {

        Annuncio annuncio = service.crea(
                "utente1",
                "Titolo vecchio",
                "Descrizione vecchia",
                "Informatica",
                "Sì",
                "Inglese"
        );

        Annuncio aggiornato = service.modificaAnnuncio(
                annuncio.getId(),
                "utente1",
                "Titolo nuovo",
                "Database",
                "Nuova descrizione",
                "No",
                "Matematica"
        );

        assertEquals(
                "Titolo nuovo",
                aggiornato.getTitolo()
        );

        assertNotEquals(
                "Titolo vecchio",
                aggiornato.getTitolo()
        );
    }


    /**
     * Verifica che un utente diverso dal proprietario
     * non possa modificare un annuncio.
     */
    @Test
    void modificaAnnuncioConUtenteDiversoRestituisceErrore() {

        Annuncio annuncio = service.crea(
                "utente1",
                "Titolo",
                "Descrizione",
                "Informatica",
                "Sì",
                "Inglese"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.modificaAnnuncio(
                        annuncio.getId(),
                        "utente2",
                        "Nuovo titolo",
                        "Informatica",
                        "Nuova descrizione",
                        "No",
                        "Matematica"
                )
        );
    }

    /**
     * Verifica che il proprietario possa eliminare
     * correttamente un proprio annuncio.
     */
    @Test
    void eliminaAnnuncioDelProprietarioRestituisceTrue() {

        Annuncio annuncio = service.crea(
                "utente1",
                "Lezioni Java",
                "Descrizione",
                "Informatica",
                "Sì",
                "Inglese"
        );

        boolean risultato = service.rimuovi(
                annuncio.getId(),
                "utente1"
        );

        assertTrue(risultato);
    }

    /**
     * Verifica che un annuncio eliminato
     * non venga più mostrato tra gli annunci dell'utente.
     */
    @Test
    void annuncioEliminatoNonCompareNellaLista() {

        String idUtente = "utente1";


        Annuncio annuncio = service.crea(
                idUtente,
                "Lezioni Java",
                "Descrizione",
                "Informatica",
                "Sì",
                "Inglese"
        );

        service.rimuovi(
                annuncio.getId(),
                idUtente
        );

        List<Annuncio> annunci = service.mieiAnnunci(idUtente);

        assertTrue(annunci.isEmpty());
    }

    /**
     * Verifica che un utente diverso dal proprietario
     * non possa eliminare un annuncio.
     */
    @Test
    void eliminaAnnuncioDiAltroUtenteRestituisceFalse() {

        Annuncio annuncio = service.crea(
                "utente1",
                "Lezioni Java",
                "Descrizione",
                "Informatica",
                "Sì",
                "Inglese"
        );

        boolean risultato = service.rimuovi(
                annuncio.getId(),
                "utente2"
        );

        assertFalse(risultato);
    }


    /**
     * Verifica che non sia possibile eliminare
     * un annuncio inesistente.
     */
    @Test
    void eliminaAnnuncioInesistenteRestituisceFalse() {

        boolean risultato = service.rimuovi(
                "id inesistente",
                "utente1"
        );

        assertFalse(risultato);
    }

    /**
     * Verifica che vengano restituiti
     * gli annunci attivi pubblicati dagli utenti.
     */
    @Test
    void trovaAttiviRestituisceAnnunciDisponibili() {

        service.crea(
                "utente1",
                "Lezioni Java",
                "Programmazione Java",
                "Informatica",
                "Sì",
                "Inglese"
        );

        List<Annuncio> annunci = service.trovaAttivi();

        assertEquals(
                1,
                annunci.size()
        );

        assertEquals(
                "Lezioni Java",
                annunci.get(0).getTitolo()
        );
    }


    /**
     * Verifica che senza annunci disponibili
     * venga restituita una lista vuota.
     */
    @Test
    void trovaAttiviSenzaAnnunciRestituisceListaVuota() {
        List<Annuncio> annunci = service.trovaAttivi();
        assertNotNull(annunci);
        assertTrue(annunci.isEmpty());
    }

    /**
     * Verifica che la ricerca trovi un annuncio
     * il cui titolo contiene la parola chiave.
     */
    @Test
    void cercaConKeywordNelTitoloTrovaAnnuncio() {

        String idUtente = "utente_" + System.nanoTime();

        String keyword = "chitarra" + System.nanoTime();

        Annuncio annuncio = service.crea(
                idUtente,
                "Lezioni di " + keyword,
                "Imparo le basi",
                "Musica",
                "Sì",
                "Cambio"
        );

        List<Annuncio> risultati = service.cerca(keyword);

        boolean presente = risultati.stream().anyMatch(a -> a.getId().equals(annuncio.getId()));

        assertTrue(presente);
    }


    /**
     * Verifica che la ricerca trovi un annuncio
     * la cui categoria contiene la parola chiave.
     */
    @Test
    void cercaConKeywordNellaCategoriaTrovaAnnuncio() {

        String idUtente = "utente_" + System.nanoTime();

        String keyword = "categoriaunica" + System.nanoTime();

        Annuncio annuncio = service.crea(
                idUtente,
                "Titolo qualsiasi",
                "Descrizione",
                keyword,
                "Sì",
                "Cambio"
        );

        List<Annuncio> risultati = service.cerca(keyword);

        boolean presente = risultati.stream().anyMatch(a -> a.getId().equals(annuncio.getId()));

        assertTrue(presente);
    }


    /**
     * Verifica che la ricerca non sia sensibile a maiuscole e minuscole.
     */
    @Test
    void cercaConKeywordMaiuscolaTrovaAnnuncioConTitoloMinuscolo() {

        String idUtente =
                "utente_" + System.nanoTime();

        String keyword =
                "javascript" + System.nanoTime();

        Annuncio annuncio = service.crea(
                idUtente,
                "Corso di " + keyword,
                "Descrizione",
                "Informatica",
                "Sì",
                "Cambio"
        );

        List<Annuncio> risultati = service.cerca(keyword.toUpperCase());

        boolean presente = risultati.stream()
                .anyMatch(a -> a.getId().equals(annuncio.getId()));

        assertTrue(presente);
    }


    /**
     * Verifica che la ricerca non restituisca
     * annunci che non corrispondono alla parola chiave.
     */
    @Test
    void cercaConKeywordSenzaCorrispondenzeNonRestituisceAnnunci() {

        String keyword = "paroleinesistenti" + System.nanoTime();

        List<Annuncio> risultati = service.cerca(keyword);

        assertTrue(risultati.isEmpty());
    }


    /**
     * Verifica che la ricerca richieda una parola chiave.
     */
    @Test
    void cercaSenzaKeywordRestituisceErrore() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.cerca(null)
        );
    }


    /**
     * Verifica che la ricerca non accetti
     * una parola chiave vuota.
     */
    @Test
    void cercaConKeywordVuotaRestituisceErrore() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.cerca("   ")
        );
    }

    /**
     * Verifica che il filtro per categoria trovi
     * un annuncio con quella categoria.
     */
    @Test
    void filtraConCategoriaSelezionataTrovaAnnuncio() {

        service.crea(
                "utente1",
                "Lezioni di chitarra",
                "Imparo le basi",
                "Musica",
                "Sì",
                "Cambio"
        );

        Set<String> categorie = new HashSet<>();
        categorie.add("Musica");

        FiltroAnnunci filtro = new FiltroAnnunci();
        filtro.setCategorieSelezionate(categorie);

        List<Annuncio> risultati = service.filtra(filtro);

        assertEquals(
                1,
                risultati.size()
        );

        assertEquals(
                "Lezioni di chitarra",
                risultati.get(0).getTitolo()
        );
    }

    /**
     * Verifica che il filtro per categoria non restituisca
     * annunci con categoria diversa da quella selezionata.
     */
    @Test
    void filtraConCategoriaDiversaNonTrovaAnnuncio() {

        service.crea(
                "utente1",
                "Lezioni di chitarra",
                "Imparo le basi",
                "Musica",
                "Sì",
                "Cambio"
        );

        Set<String> categorie = new HashSet<>();
        categorie.add("Informatica");

        FiltroAnnunci filtro = new FiltroAnnunci();
        filtro.setCategorieSelezionate(categorie);

        List<Annuncio> risultati = service.filtra(filtro);

        assertTrue(risultati.isEmpty());
    }

    /**
     * Verifica che il campo "Altro" trovi un annuncio
     * la cui categoria corrisponde al testo inserito.
     */
    @Test
    void filtraConAltroCategoriaTrovaAnnuncio() {

        service.crea(
                "utente1",
                "Corso di fotografia",
                "Descrizione",
                "Fotografia",
                "Sì",
                "Cambio"
        );

        FiltroAnnunci filtro = new FiltroAnnunci();
        filtro.setAltroCategoria("fotografia");

        List<Annuncio> risultati = service.filtra(filtro);

        assertEquals(
                1,
                risultati.size()
        );
    }

    /**
     * Verifica che il filtro per disponibilità trovi
     * solo gli annunci con la disponibilità richiesta.
     */
    @Test
    void filtraConDisponibilitaTrovaSoloAnnunciCorrispondenti() {

        service.crea(
                "utente1",
                "Lezioni Java",
                "Descrizione",
                "Informatica",
                "Sì",
                "Inglese"
        );

        service.crea(
                "utente1",
                "Lezioni SQL",
                "Descrizione",
                "Informatica",
                "No",
                "Inglese"
        );

        FiltroAnnunci filtro = new FiltroAnnunci();
        filtro.setDisponibilitaSelezionata("Sì");

        List<Annuncio> risultati = service.filtra(filtro);

        assertEquals(
                1,
                risultati.size()
        );

        assertEquals(
                "Lezioni Java",
                risultati.get(0).getTitolo()
        );
    }

    /**
     * Verifica che categoria e disponibilità combinate
     * restituiscano solo gli annunci che soddisfano entrambi i criteri.
     */
    @Test
    void filtraConCategoriaEDisponibilitaCombinateRestituisceSoloCorrispondenze() {

        service.crea(
                "utente1",
                "Lezioni Java",
                "Descrizione",
                "Informatica",
                "Sì",
                "Inglese"
        );

        service.crea(
                "utente1",
                "Lezioni SQL",
                "Descrizione",
                "Informatica",
                "No",
                "Inglese"
        );

        service.crea(
                "utente1",
                "Lezioni di chitarra",
                "Descrizione",
                "Musica",
                "Sì",
                "Cambio"
        );

        Set<String> categorie = new HashSet<>();
        categorie.add("Informatica");

        FiltroAnnunci filtro = new FiltroAnnunci();
        filtro.setCategorieSelezionate(categorie);
        filtro.setDisponibilitaSelezionata("Sì");

        List<Annuncio> risultati = service.filtra(filtro);

        assertEquals(
                1,
                risultati.size()
        );

        assertEquals(
                "Lezioni Java",
                risultati.get(0).getTitolo()
        );
    }

    /**
     * Verifica che senza alcun criterio impostato
     * il filtro restituisca tutti gli annunci attivi.
     */
    @Test
    void filtraConFiltroVuotoRestituisceTuttiGliAnnunciAttivi() {

        service.crea(
                "utente1",
                "Lezioni Java",
                "Descrizione",
                "Informatica",
                "Sì",
                "Inglese"
        );

        service.crea(
                "utente1",
                "Lezioni di chitarra",
                "Descrizione",
                "Musica",
                "No",
                "Cambio"
        );

        FiltroAnnunci filtro = new FiltroAnnunci();

        List<Annuncio> risultati = service.filtra(filtro);

        assertEquals(
                2,
                risultati.size()
        );
    }

    /**
     * Verifica che un annuncio disattivato non venga
     * restituito nemmeno se soddisfa i criteri di filtro.
     */
    @Test
    void filtraNonRestituisceAnnunciEliminati() {

        Annuncio annuncio = service.crea(
                "utente1",
                "Lezioni Java",
                "Descrizione",
                "Informatica",
                "Sì",
                "Inglese"
        );

        service.rimuovi(annuncio.getId(), "utente1");

        Set<String> categorie = new HashSet<>();
        categorie.add("Informatica");

        FiltroAnnunci filtro = new FiltroAnnunci();
        filtro.setCategorieSelezionate(categorie);

        List<Annuncio> risultati = service.filtra(filtro);

        assertTrue(risultati.isEmpty());
    }

    /**
     * Verifica che il filtro richieda un oggetto FiltroAnnunci valido.
     */
    @Test
    void filtraConFiltroNullRestituisceErrore() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.filtra(null)
        );
    }

    /**
     * Verifica che il metodo ordina applichi correttamente
     * il criterio titolo crescente.
     */
    @Test
    void ordinaConTitoloCrescenteOrdinaAlfabeticamente() {

        List<Annuncio> annunci = new java.util.ArrayList<>();
        annunci.add(service.crea(
                "utente1", "Zebra", "Descrizione",
                "Categoria", "Sì", "Cambio"
        ));
        annunci.add(service.crea(
                "utente1", "Alfa", "Descrizione",
                "Categoria", "Sì", "Cambio"
        ));

        List<Annuncio> ordinati = service.ordina(
                annunci,
                CriterioOrdinamento.TITOLO_CRESCENTE
        );

        assertEquals("Alfa", ordinati.get(0).getTitolo());
        assertEquals("Zebra", ordinati.get(1).getTitolo());
    }

    /**
     * Verifica che il metodo ordina applichi correttamente
     * il criterio titolo decrescente.
     */
    @Test
    void ordinaConTitoloDecrescenteOrdinaAlContrario() {

        List<Annuncio> annunci = new java.util.ArrayList<>();
        annunci.add(service.crea(
                "utente1", "Alfa", "Descrizione",
                "Categoria", "Sì", "Cambio"
        ));
        annunci.add(service.crea(
                "utente1", "Zebra", "Descrizione",
                "Categoria", "Sì", "Cambio"
        ));

        List<Annuncio> ordinati = service.ordina(
                annunci,
                CriterioOrdinamento.TITOLO_DECRESCENTE
        );

        assertEquals("Zebra", ordinati.get(0).getTitolo());
        assertEquals("Alfa", ordinati.get(1).getTitolo());
    }

    /**
     * Verifica che senza un criterio la lista venga restituita invariata.
     */
    @Test
    void ordinaConCriterioNullRestituisceListaInvariata() {

        List<Annuncio> annunci = new java.util.ArrayList<>();
        annunci.add(service.crea(
                "utente1", "Zebra", "Descrizione",
                "Categoria", "Sì", "Cambio"
        ));
        annunci.add(service.crea(
                "utente1", "Alfa", "Descrizione",
                "Categoria", "Sì", "Cambio"
        ));

        List<Annuncio> ordinati = service.ordina(annunci, null);

        assertEquals("Zebra", ordinati.get(0).getTitolo());
        assertEquals("Alfa", ordinati.get(1).getTitolo());
    }

    /**
     * Verifica che ordinare una lista nulla restituisca
     * una lista vuota, senza sollevare eccezioni.
     */
    @Test
    void ordinaConListaNullRestituisceListaVuota() {

        List<Annuncio> ordinati = service.ordina(
                null,
                CriterioOrdinamento.TITOLO_CRESCENTE
        );

        assertNotNull(ordinati);
        assertTrue(ordinati.isEmpty());
    }

    /**
     * Verifica che ordina restituisca una nuova lista ordinata
     * senza modificare la lista originale passata come parametro.
     */
    @Test
    void ordinaNonModificaListaOriginale() {

        Annuncio zebra = service.crea(
                "utente1", "Zebra", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );
        Annuncio alfa = service.crea(
                "utente1", "Alfa", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        List<Annuncio> originale = new java.util.ArrayList<>();
        originale.add(zebra);
        originale.add(alfa);

        service.ordina(
                originale,
                CriterioOrdinamento.TITOLO_CRESCENTE
        );

        assertEquals("Zebra", originale.get(0).getTitolo());
        assertEquals("Alfa", originale.get(1).getTitolo());
    }
}
