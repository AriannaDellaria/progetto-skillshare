package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import java.util.List;

// Mostra le richieste di scambio ricevute per i propri annunci.
public class RichiesteRicevuteGui {

    private final RichiestaScambioServiceAsync richiestaScambioService =
            GWT.create(RichiestaScambioService.class);

    private final ValutazioneServiceAsync valutazioneService =
            GWT.create(ValutazioneService.class);

    // Visualizza le richieste ricevute dall'utente autenticato.
    public void mostra() {

        RootPanel.get().clear();

        Utente utente = SessioneUtente.getUtenteCorrente();

        if (utente == null) {
            new LoginGui().mostra();
            return;
        }

        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(10);

        HTML titolo = new HTML("<h2>Richieste ricevute</h2>");
        Label messaggio = new Label("Caricamento richieste...");

        panel.add(titolo);
        panel.add(messaggio);

        richiestaScambioService.richiesteRicevute(
                utente.getId(),
                new AsyncCallback<List<RichiestaScambio>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        messaggio.setText(
                                "Errore durante il caricamento delle richieste."
                        );
                    }

                    @Override
                    public void onSuccess(List<RichiestaScambio> richieste) {
                        if (richieste == null || richieste.isEmpty()) {
                            messaggio.setText("Non hai ricevuto richieste di scambio.");
                        } else {
                            messaggio.setText("");
                            for (RichiestaScambio richiesta : richieste) {
                                panel.add(creaSchedaRichiesta(richiesta));
                            }
                        }
                        panel.add(NavigationHelper.creaBottoneIndietroHomeUtente());
                    }
                }
        );

        RootPanel.get().add(panel);
    }

    // Crea la scheda di una singola richiesta ricevuta.
    private VerticalPanel creaSchedaRichiesta(RichiestaScambio richiesta) {
        VerticalPanel scheda = new VerticalPanel();
        scheda.setSpacing(5);
        scheda.setWidth("400px");

        Label annuncio = new Label(
                "Annuncio: " + richiesta.getTitoloAnnuncio()
        );

        Label mittente = new Label(
                "Da: " + richiesta.getNomeMittente()
        );

        Label messaggio = new Label(
                "Messaggio: " + richiesta.getMessaggio()
        );

        Label stato = new Label(
                "Stato: " + richiesta.getStato().getEtichetta()
        );

        scheda.add(annuncio);
        scheda.add(mittente);
        scheda.add(messaggio);
        scheda.add(stato);

        if (richiesta.getStato() == StatoRichiesta.ACCETTATA) {
            Button apriChatButton = new Button("Apri chat");
            apriChatButton.addClickHandler(event ->
                    new ChatGui(
                            richiesta.getId(),
                            () -> new RichiesteRicevuteGui().mostra(),
                            true
                    ).mostra()
            );
            scheda.add(apriChatButton);
        }

        if (richiesta.getStato() == StatoRichiesta.IN_ATTESA) {

            Label esitoOperazione = new Label();

            Button accettaButton = new Button("Accetta");
            Button rifiutaButton = new Button("Rifiuta");

            accettaButton.addClickHandler(event -> {

                boolean confermato = Window.confirm(
                        "Confermi di voler accettare questa richiesta di scambio?"
                );

                if (!confermato) {
                    return;
                }

                accettaButton.setEnabled(false);
                rifiutaButton.setEnabled(false);

                richiestaScambioService.accetta(
                        richiesta.getId(),
                        SessioneUtente.getUtenteCorrente().getId(),
                        new AsyncCallback<RichiestaScambio>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                accettaButton.setEnabled(true);
                                rifiutaButton.setEnabled(true);
                                esitoOperazione.setText(
                                        caught.getMessage() != null
                                                ? caught.getMessage()
                                                : "Errore durante l'accettazione della richiesta."
                                );
                            }

                            @Override
                            public void onSuccess(RichiestaScambio aggiornata) {
                                mostra();
                            }
                        }
                );
            });

            rifiutaButton.addClickHandler(event -> {

                boolean confermato = Window.confirm(
                        "Confermi di voler rifiutare questa richiesta di scambio?"
                );

                if (!confermato) {
                    return;
                }

                accettaButton.setEnabled(false);
                rifiutaButton.setEnabled(false);

                richiestaScambioService.rifiuta(
                        richiesta.getId(),
                        SessioneUtente.getUtenteCorrente().getId(),
                        new AsyncCallback<RichiestaScambio>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                accettaButton.setEnabled(true);
                                rifiutaButton.setEnabled(true);
                                esitoOperazione.setText(
                                        caught.getMessage() != null
                                                ? caught.getMessage()
                                                : "Errore durante il rifiuto della richiesta."
                                );
                            }

                            @Override
                            public void onSuccess(RichiestaScambio aggiornata) {
                                mostra();
                            }
                        }
                );
            });

            scheda.add(accettaButton);
            scheda.add(rifiutaButton);
            scheda.add(esitoOperazione);
        }

        if (richiesta.getStato() == StatoRichiesta.CONCLUSA) {
            Label statoValutazione = new Label("Verifica valutazione...");
            scheda.add(statoValutazione);
            caricaSezioneValutazione(richiesta, scheda, statoValutazione);
        }

        return scheda;
    }



    // Controlla se lo scambio è già stato valutato dall'utente
    // corrente, e in base al risultato mostra il form per valutare
    // oppure un messaggio.
    private void caricaSezioneValutazione(
            RichiestaScambio richiesta,
            VerticalPanel scheda,
            Label statoValutazione
    ) {
        Utente utente = SessioneUtente.getUtenteCorrente();

        if (utente == null) {
            new LoginGui().mostra();
            return;
        }

        valutazioneService.valutazioneGiaEsistente(
                richiesta.getId(),
                utente.getId(),
                new AsyncCallback<Boolean>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        statoValutazione.setText(
                                "Impossibile verificare lo stato della valutazione."
                        );
                    }

                    @Override
                    public void onSuccess(Boolean giaEsistente) {
                        if (Boolean.TRUE.equals(giaEsistente)) {
                            statoValutazione.setText("Hai già valutato questo scambio.");
                        } else {
                            statoValutazione.setText("");
                            scheda.add(creaSezioneValutazione(richiesta));
                        }
                    }
                }
        );
    }

    // Crea il bottone e il form unico per assegnare il punteggio.
    private VerticalPanel creaSezioneValutazione(RichiestaScambio richiesta) {
        VerticalPanel sezione = new VerticalPanel();
        sezione.setSpacing(5);

        Button assegnaButton = new Button("Assegna valutazione");
        VerticalPanel formPanel = new VerticalPanel();
        formPanel.setSpacing(5);
        formPanel.setVisible(false);

        TextBox punteggioBox = new TextBox();
        punteggioBox.getElement().setPropertyString(
                "placeholder",
                "Da " + Valutazione.PUNTEGGIO_MINIMO + " a " + Valutazione.PUNTEGGIO_MASSIMO
        );

        Button salvaButton = new Button("Salva");
        Label esitoValutazione = new Label();

        assegnaButton.addClickHandler(event -> {
            formPanel.setVisible(true);
            assegnaButton.setEnabled(false);
        });

        salvaButton.addClickHandler(event -> {
            esitoValutazione.setText("");

            Utente utente = SessioneUtente.getUtenteCorrente();

            if (utente == null) {
                new LoginGui().mostra();
                return;
            }

            String testoPunteggio = punteggioBox.getText().trim();
            int punteggio;

            try {
                punteggio = Integer.parseInt(testoPunteggio);
            } catch (NumberFormatException e) {
                esitoValutazione.setText("Inserisci un numero valido.");
                return;
            }

            if (punteggio < Valutazione.PUNTEGGIO_MINIMO || punteggio > Valutazione.PUNTEGGIO_MASSIMO) {
                esitoValutazione.setText(
                        "Scegli un numero da " + Valutazione.PUNTEGGIO_MINIMO
                                + " a " + Valutazione.PUNTEGGIO_MASSIMO + "."
                );
                return;
            }

            salvaButton.setEnabled(false);

            valutazioneService.assegna(
                    richiesta.getId(),
                    utente.getId(),
                    punteggio,
                    new AsyncCallback<Valutazione>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            salvaButton.setEnabled(true);
                            esitoValutazione.setText(
                                    "Errore durante l'inserimento della valutazione."
                            );
                        }

                        @Override
                        public void onSuccess(Valutazione valutazione) {
                            if (valutazione == null) {
                                salvaButton.setEnabled(true);
                                esitoValutazione.setText(
                                        "Non è stato possibile inserire la valutazione."
                                );
                                return;
                            }

                            esitoValutazione.setText("Valutazione inserita correttamente.");
                            punteggioBox.setEnabled(false);
                            salvaButton.setEnabled(false);
                        }
                    }
            );
        });

        formPanel.add(punteggioBox);
        formPanel.add(salvaButton);
        formPanel.add(esitoValutazione);

        sezione.add(assegnaButton);
        sezione.add(formPanel);

        return sezione;
    }
}