package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Mostra gli annunci attivi degli altri utenti.
 */
public class AnnunciGui {

    private final AnnuncioServiceAsync annuncioService = GWT.create(AnnuncioService.class);
    private final UtenteServiceAsync utenteService = GWT.create(UtenteService.class);

    private Utente utente;
    private VerticalPanel risultatiPanel;
    private Label messaggio;
    private TextBox campoRicerca;
    private FiltroAnnunci filtroAttuale;
    private ListBox ordinamentoBox;
    private List<Annuncio> annunciCorrenti = new ArrayList<>();
    private String messaggioListaVuotaCorrente = "";

    /**
     * Carica gli annunci del marketplace.
     */
    public void mostra() {
        filtroAttuale = null;
        inizializza();
        caricaAnnunciAttivi();
    }

    /**
     * Carica gli annunci del marketplace applicando i filtri indicati.
     */
    public void mostraConFiltri(FiltroAnnunci filtro) {
        filtroAttuale = filtro;
        inizializza();

        if (filtro == null || filtro.isVuoto()) {
            caricaAnnunciAttivi();
        } else {
            applicaFiltri(filtro);
        }
    }

    /**
     * Costruisce la struttura comune della pagina (barra di ricerca,
     * bottoni, pannello risultati), condivisa tra mostra() e mostraConFiltri().
     */
    private void inizializza() {
        RootPanel.get().clear();

        utente = SessioneUtente.getUtenteCorrente();

        // Impedisce l'accesso senza login.
        if (utente == null) {
            new LoginGui().mostra();
            return;
        }

        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(12);

        HTML titolo = new HTML("<h2>Skill Marketplace</h2>");

        panel.add(titolo);
        aggiungiBarraRicerca(panel);
        aggiungiMenuOrdinamento(panel);

        Button filtriButton = new Button("Filtri");
        filtriButton.addClickHandler(event -> new FiltriGui(filtroAttuale).mostra());
        panel.add(filtriButton);

        messaggio = new Label("Caricamento annunci...");
        panel.add(messaggio);

        risultatiPanel = new VerticalPanel();
        risultatiPanel.setSpacing(12);
        panel.add(risultatiPanel);

        panel.add(
                NavigationHelper.creaBottoneIndietroHomeUtente()
        );

        RootPanel.get().add(panel);
    }

    // Aggiunge il campo di ricerca e il pulsante al pannello principale.
    private void aggiungiBarraRicerca(VerticalPanel panel) {
        campoRicerca = new TextBox();

        campoRicerca.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    eseguiRicerca();
                }
            }
        });

        Button bottoneCerca = new Button("Cerca");
        bottoneCerca.addClickHandler(event -> eseguiRicerca());

        panel.add(campoRicerca);
        panel.add(bottoneCerca);
    }

    // Aggiunge il menu a tendina per selezionare il criterio di ordinamento.
    private void aggiungiMenuOrdinamento(VerticalPanel panel) {
        ordinamentoBox = new ListBox();
        ordinamentoBox.addItem("Ordina per...");

        for (CriterioOrdinamento criterio : CriterioOrdinamento.values()) {
            ordinamentoBox.addItem(criterio.getEtichetta());
        }

        ordinamentoBox.addChangeHandler(event -> riordinaERimostra());

        panel.add(ordinamentoBox);
    }

    // Richiede al server di ordinare la lista di annunci già ricevuta
    // secondo il criterio selezionato, e mostra il risultato.
    private void riordinaERimostra() {
        String etichettaSelezionata = ordinamentoBox.getSelectedValue();
        CriterioOrdinamento criterio = CriterioOrdinamento.daEtichetta(etichettaSelezionata);

        if (criterio == null) {
            renderizzaAnnunci(annunciCorrenti, messaggioListaVuotaCorrente);
            return;
        }

        annuncioService.ordina(
                annunciCorrenti,
                criterio,
                new AsyncCallback<List<Annuncio>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        messaggio.setText(
                                "Errore durante l'ordinamento degli annunci."
                        );
                    }

                    @Override
                    public void onSuccess(List<Annuncio> annunciOrdinati) {
                        renderizzaAnnunci(annunciOrdinati, messaggioListaVuotaCorrente);
                    }
                }
        );
    }

    // Avvia la ricerca in base al testo inserito, oppure ricarica tutti gli annunci se vuoto.
    private void eseguiRicerca() {
        String keyword = campoRicerca.getText().trim();

        if (keyword.isEmpty()) {
            caricaAnnunciAttivi();
            return;
        }

        messaggio.setText("Ricerca in corso...");
        risultatiPanel.clear();

        annuncioService.cerca(
                keyword,
                new AsyncCallback<List<Annuncio>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        messaggio.setText(
                                "Errore durante la ricerca degli annunci."
                        );
                    }

                    @Override
                    public void onSuccess(List<Annuncio> annunci) {
                        mostraRisultati(annunci, "Nessun annuncio trovato per questa ricerca.");
                    }
                }
        );
    }

    /**
     * Carica gli annunci del marketplace applicando i criteri di filtro.
     */
    private void applicaFiltri(FiltroAnnunci filtro) {
        messaggio.setText("Applicazione filtri in corso...");
        risultatiPanel.clear();

        annuncioService.filtra(
                filtro,
                new AsyncCallback<List<Annuncio>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        messaggio.setText(
                                "Errore durante l'applicazione dei filtri."
                        );
                    }

                    @Override
                    public void onSuccess(List<Annuncio> annunci) {
                        mostraRisultati(annunci, "Nessun annuncio corrisponde ai filtri selezionati.");
                    }
                }
        );
    }

    // Carica tutti gli annunci attivi.
    private void caricaAnnunciAttivi() {
        messaggio.setText("Caricamento annunci...");
        risultatiPanel.clear();

        annuncioService.trovaAttivi(
                new AsyncCallback<List<Annuncio>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        messaggio.setText(
                                "Errore durante il caricamento degli annunci."
                        );
                    }

                    @Override
                    public void onSuccess(List<Annuncio> annunci) {
                        mostraRisultati(annunci, "Non ci sono annunci disponibili.");
                    }
                }
        );
    }

    // Mostra l'elenco degli annunci ricevuti dal server, salvando lo
    // stato corrente per permettere un successivo riordino.
    private void mostraRisultati(List<Annuncio> annunci, String messaggioListaVuota) {
        annunciCorrenti = annunci == null ? new ArrayList<>() : annunci;
        messaggioListaVuotaCorrente = messaggioListaVuota;

        String etichettaSelezionata = ordinamentoBox != null
                ? ordinamentoBox.getSelectedValue() : null;
        CriterioOrdinamento criterio = CriterioOrdinamento.daEtichetta(etichettaSelezionata);

        if (criterio != null) {
            riordinaERimostra();
        } else {
            renderizzaAnnunci(annunciCorrenti, messaggioListaVuota);
        }
    }

    // Disegna a schermo la lista di annunci già ordinata, escludendo
    // quelli dell'utente corrente.
    private void renderizzaAnnunci(List<Annuncio> annunci, String messaggioListaVuota) {
        messaggio.setText("");
        risultatiPanel.clear();

        if (annunci == null || annunci.isEmpty()) {
            messaggio.setText(messaggioListaVuota);
            return;
        }

        boolean trovato = false;

        for (Annuncio annuncio : annunci) {
            if (!utente.getId().equals(annuncio.getIdAutore())) {
                risultatiPanel.add(creaSchedaAnnuncio(annuncio));
                trovato = true;
            }
        }

        if (!trovato) {
            messaggio.setText(messaggioListaVuota);
        }
    }

    // Crea la scheda di un annuncio.
    private VerticalPanel creaSchedaAnnuncio(
            Annuncio annuncio
    ) {
        VerticalPanel scheda = new VerticalPanel();
        scheda.setSpacing(5);
        scheda.setWidth("400px");

        Label titolo = new Label(annuncio.getTitolo());

        Label descrizione = new Label(annuncio.getDescrizione());

        Anchor autore = new Anchor("Pubblicato da: ...");
        autore.addClickHandler(event ->
                new ProfiloPubblicoGui(annuncio.getIdAutore(), () -> mostra()).mostra()
        );

        utenteService.trovaPerId(annuncio.getIdAutore(), new AsyncCallback<Utente>() {
            @Override
            public void onFailure(Throwable caught) {
                autore.setText("Pubblicato da: utente sconosciuto");
            }

            @Override
            public void onSuccess(Utente risultato) {
                autore.setText("Pubblicato da: " + (risultato != null ? risultato.getNomeUtente() : "utente sconosciuto"));
            }
        });

        Label categoria = new Label("Categoria: " + annuncio.getCategoria());

        Label disponibilita = new Label("Disponibilità: " + annuncio.getDisponibilita());

        Label controprestazione = new Label("Controprestazione: " + annuncio.getControprestazione());

        Label dataPubblicazione = new Label("Pubblicato il: " + formattaData(annuncio.getDataPubblicazione()));

        scheda.add(titolo);
        scheda.add(descrizione);
        scheda.add(categoria);
        scheda.add(disponibilita);
        scheda.add(controprestazione);
        scheda.add(dataPubblicazione);

        Button richiediScambioButton = new Button("Invia richiesta di scambio");
        richiediScambioButton.addClickHandler(
                event -> new RichiediScambioGui(annuncio).mostra()
        );
        scheda.add(richiediScambioButton);

        return scheda;
    }

    // Converte il timestamp di pubblicazione in una data leggibile (gg/mm/aaaa).
    private String formattaData(long timestamp) {
        DateTimeFormat formato = DateTimeFormat.getFormat("dd/MM/yyyy");
        return formato.format(new Date(timestamp));
    }
}