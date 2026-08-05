package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

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

        sezione.add(new Label(etichetta));

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

    // Formatta la media con una sola cifra decimale.
    private String formattaMedia(double media) {
        double arrotondata = Math.round(media * 10) / 10.0;
        return String.valueOf(arrotondata);
    }
}