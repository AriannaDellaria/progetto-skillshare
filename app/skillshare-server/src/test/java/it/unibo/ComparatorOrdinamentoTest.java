package it.unibo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica la mappatura tra criteri di ordinamento e Comparator.
 */
class ComparatorOrdinamentoTest {

    /**
     * Verifica che il criterio titolo crescente ordini
     * gli annunci in ordine alfabetico.
     */
    @Test
    void perCriterioTitoloCrescenteOrdinaAlfabeticamente() {

        List<Annuncio> annunci = creaAnnunci("Zebra", "Alfa", "Mela");

        Comparator<Annuncio> comparator =
                ComparatorOrdinamento.perCriterio(CriterioOrdinamento.TITOLO_CRESCENTE);

        annunci.sort(comparator);

        assertEquals("Alfa", annunci.get(0).getTitolo());
        assertEquals("Mela", annunci.get(1).getTitolo());
        assertEquals("Zebra", annunci.get(2).getTitolo());
    }

    /**
     * Verifica che il criterio titolo decrescente ordini
     * gli annunci in ordine alfabetico inverso.
     */
    @Test
    void perCriterioTitoloDecrescenteOrdinaAlContrario() {

        List<Annuncio> annunci = creaAnnunci("Zebra", "Alfa", "Mela");

        Comparator<Annuncio> comparator =
                ComparatorOrdinamento.perCriterio(CriterioOrdinamento.TITOLO_DECRESCENTE);

        annunci.sort(comparator);

        assertEquals("Zebra", annunci.get(0).getTitolo());
        assertEquals("Mela", annunci.get(1).getTitolo());
        assertEquals("Alfa", annunci.get(2).getTitolo());
    }

    /**
     * Verifica che l'ordinamento per titolo non sia sensibile
     * a maiuscole e minuscole.
     */
    @Test
    void perCriterioTitoloCrescenteNonDistingueMaiuscoleEMinuscole() {

        List<Annuncio> annunci = creaAnnunci("banana", "Arancia", "ciliegia");

        Comparator<Annuncio> comparator =
                ComparatorOrdinamento.perCriterio(CriterioOrdinamento.TITOLO_CRESCENTE);

        annunci.sort(comparator);

        assertEquals("Arancia", annunci.get(0).getTitolo());
        assertEquals("banana", annunci.get(1).getTitolo());
        assertEquals("ciliegia", annunci.get(2).getTitolo());
    }

    /**
     * Verifica che il criterio data crescente ordini
     * gli annunci dal meno recente al più recente.
     */
    @Test
    void perCriterioDataCrescenteOrdinaDalMenoRecenteAlPiuRecente() throws InterruptedException {

        Annuncio primo = new Annuncio(
                "1", "autore", "Primo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        Thread.sleep(5);

        Annuncio secondo = new Annuncio(
                "2", "autore", "Secondo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        List<Annuncio> annunci = new ArrayList<>();
        annunci.add(secondo);
        annunci.add(primo);

        Comparator<Annuncio> comparator =
                ComparatorOrdinamento.perCriterio(CriterioOrdinamento.DATA_CRESCENTE);

        annunci.sort(comparator);

        assertEquals("Primo", annunci.get(0).getTitolo());
        assertEquals("Secondo", annunci.get(1).getTitolo());
    }

    /**
     * Verifica che il criterio data decrescente ordini
     * gli annunci dal più recente al meno recente.
     */
    @Test
    void perCriterioDataDecrescenteOrdinaDalPiuRecenteAlMenoRecente() throws InterruptedException {

        Annuncio primo = new Annuncio(
                "1", "autore", "Primo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        Thread.sleep(5);

        Annuncio secondo = new Annuncio(
                "2", "autore", "Secondo", "Descrizione",
                "Categoria", "Sì", "Cambio"
        );

        List<Annuncio> annunci = new ArrayList<>();
        annunci.add(primo);
        annunci.add(secondo);

        Comparator<Annuncio> comparator =
                ComparatorOrdinamento.perCriterio(CriterioOrdinamento.DATA_DECRESCENTE);

        annunci.sort(comparator);

        assertEquals("Secondo", annunci.get(0).getTitolo());
        assertEquals("Primo", annunci.get(1).getTitolo());
    }

    /**
     * Verifica che un criterio null restituisca un Comparator null,
     * cioè nessun ordinamento da applicare.
     */
    @Test
    void perCriterioConCriterioNullRestituisceNull() {

        Comparator<Annuncio> comparator =
                ComparatorOrdinamento.perCriterio(null);

        assertNull(comparator);
    }

    // Crea una lista di annunci con i titoli indicati.
    private List<Annuncio> creaAnnunci(String... titoli) {
        List<Annuncio> annunci = new ArrayList<>();

        int contatore = 0;
        for (String titolo : titoli) {
            annunci.add(new Annuncio(
                    String.valueOf(contatore++),
                    "autore",
                    titolo,
                    "Descrizione",
                    "Categoria",
                    "Sì",
                    "Cambio"
            ));
        }

        return annunci;
    }
}