package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import java.util.Date;
import java.util.List;

// Mostra il profilo pubblico di un utente, con il rating calcolato
// sulla base delle valutazioni ricevute.
public class ProfiloPubblicoGui {

    private final UtenteServiceAsync utenteService = GWT.create(UtenteService.class);
    private final ValutazioneServiceAsync valutazioneService = GWT.create(ValutazioneService.class);

    private final String idUtente;
    private final Runnable azioneIndietro;

    public ProfiloPubblicoGui(String idUtente, Runnable azioneIndietro) {
        this.idUtente = idUtente;
        this.azioneIndietro = azioneIndietro;
    }

    public void mostra() {

        RootPanel.get().clear();

        Utente utenteCorrente = SessioneUtente.getUtenteCorrente();

        if (utenteCorrente == null) {
            new LoginGui().mostra();
            return;
        }

        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(10);

        HTML titolo = new HTML("<h2>Profilo utente</h2>");
        Label messaggioStato = new Label("Caricamento profilo...");

        panel.add(titolo);
        panel.add(messaggioStato);

        VerticalPanel contenutoPanel = new VerticalPanel();
        contenutoPanel.setSpacing(8);
        panel.add(contenutoPanel);

        Button indietroButton = new Button("Indietro");
        indietroButton.addClickHandler(event -> azioneIndietro.run());
        panel.add(indietroButton);

        RootPanel.get().add(panel);

        caricaProfilo(messaggioStato, contenutoPanel);
    }

    // Recupera nome utente e rating, poi mostra entrambe le sezioni.
    private void caricaProfilo(Label messaggioStato, VerticalPanel contenutoPanel) {

        utenteService.trovaPerId(idUtente, new AsyncCallback<Utente>() {

            @Override
            public void onFailure(Throwable caught) {
                messaggioStato.setText("Errore durante il caricamento del profilo.");
            }

            @Override
            public void onSuccess(Utente utente) {

                if (utente == null) {
                    messaggioStato.setText("Utente non trovato.");
                    return;
                }

                contenutoPanel.add(new Label("Nome utente: " + utente.getNomeUtente()));

                caricaRating(messaggioStato, contenutoPanel);
            }
        });
    }

    private void caricaRating(Label messaggioStato, VerticalPanel contenutoPanel) {

        valutazioneService.calcolaRating(idUtente, new AsyncCallback<RatingUtente>() {

            @Override
            public void onFailure(Throwable caught) {
                messaggioStato.setText("Errore durante il calcolo del rating.");
            }

            @Override
            public void onSuccess(RatingUtente rating) {

                messaggioStato.setText("");

                contenutoPanel.add(creaSezioneRating(
                        "Prestazione",
                        rating != null && rating.haRecensioniPrestazione(),
                        rating != null ? rating.getMediaPrestazione() : 0,
                        rating != null ? rating.getNumeroRecensioniPrestazione() : 0
                ));

                contenutoPanel.add(creaSezioneRating(
                        "Controprestazione",
                        rating != null && rating.haRecensioniControprestazione(),
                        rating != null ? rating.getMediaControprestazione() : 0,
                        rating != null ? rating.getNumeroRecensioniControprestazione() : 0
                ));

                caricaStorico(contenutoPanel);
            }
        });
    }

    // Recupera lo storico e mostra le due sezioni corrispondenti.
    private void caricaStorico(VerticalPanel contenutoPanel) {

        valutazioneService.recuperaStorico(idUtente, new AsyncCallback<StoricoUtente>() {

            @Override
            public void onFailure(Throwable caught) {
                contenutoPanel.add(new Label("Errore durante il caricamento dello storico."));
            }

            @Override
            public void onSuccess(StoricoUtente storico) {

                List<VoceStorico> storicoPrestazione = storico != null
                        ? storico.getStoricoPrestazione() : null;

                List<VoceStorico> storicoControprestazione = storico != null
                        ? storico.getStoricoControprestazione() : null;

                contenutoPanel.add(creaSezioneStorico("Storico prestazioni", storicoPrestazione));
                contenutoPanel.add(creaSezioneStorico("Storico controprestazioni", storicoControprestazione));
            }
        });
    }

    // Crea la sezione che mostra il rating di un tipo specifico,
    // oppure un messaggio se l'utente non ha ancora ricevuto
    // recensioni di quel tipo.
    private VerticalPanel creaSezioneRating(
            String etichetta,
            boolean haRecensioni,
            double media,
            int numeroRecensioni
    ) {
        VerticalPanel sezione = new VerticalPanel();
        sezione.setSpacing(3);

        Label titoloLabel = new Label(etichetta);
        titoloLabel.getElement().getStyle().setProperty("fontWeight", "bold");
        sezione.add(titoloLabel);

        if (haRecensioni) {
            String mediaFormattata = formattaMedia(media);
            sezione.add(new Label(
                    "Rating: " + mediaFormattata + " (" + numeroRecensioni + " recensioni)"
            ));
        } else {
            sezione.add(new Label("Nessuna recensione ricevuta."));
        }

        return sezione;
    }

    // Crea la sezione che mostra lo storico di scambi di un tipo
    // specifico, oppure un messaggio se lo storico è vuoto.
    private VerticalPanel creaSezioneStorico(String etichetta, List<VoceStorico> voci) {
        VerticalPanel sezione = new VerticalPanel();
        sezione.setSpacing(5);

        Label titoloLabel = new Label(etichetta);
        titoloLabel.getElement().getStyle().setProperty("fontWeight", "bold");
        sezione.add(titoloLabel);

        if (voci == null || voci.isEmpty()) {
            sezione.add(new Label("Lo storico è vuoto."));
            return sezione;
        }

        for (VoceStorico voce : voci) {
            sezione.add(creaRigaStorico(voce));
        }

        return sezione;
    }

    // Crea la singola riga di storico: testo identificativo,
    // punteggio, commento e data.
    private VerticalPanel creaRigaStorico(VoceStorico voce) {
        VerticalPanel riga = new VerticalPanel();
        riga.setSpacing(2);
        riga.setWidth("400px");

        riga.add(new Label(voce.getTesto()));
        riga.add(new Label("Valutazione: " + voce.getPunteggio() + "/" + Valutazione.PUNTEGGIO_MASSIMO));

        if (voce.getCommento() != null && !voce.getCommento().trim().isEmpty()) {
            riga.add(new Label("Commento: " + voce.getCommento()));
        }

        riga.add(new Label(formattaData(voce.getData())));

        return riga;
    }

    // Formatta la media con una sola cifra decimale.
    private String formattaMedia(double media) {
        double arrotondata = Math.round(media * 10) / 10.0;
        return String.valueOf(arrotondata);
    }

    // Converte il timestamp in una data leggibile (gg/mm/aaaa).
    private String formattaData(long timestamp) {
        DateTimeFormat formato = DateTimeFormat.getFormat("dd/MM/yyyy");
        return formato.format(new Date(timestamp));
    }
}