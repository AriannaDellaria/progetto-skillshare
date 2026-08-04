package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import java.util.Comparator;
import java.util.List;

// Mostra la cronologia della chat associata a una richiesta accettata.
public class ChatGui {

    private final MessaggioServiceAsync messaggioService = GWT.create(MessaggioService.class);
    private final UtenteServiceAsync utenteService = GWT.create(UtenteService.class);
    private final RichiestaScambioServiceAsync richiestaScambioService = GWT.create(RichiestaScambioService.class);

    private final String idRichiesta;
    private final Runnable azioneIndietro;
    private String nomeAltroPartecipante = "Altro utente";
    private final boolean utenteCorrenteEProprietario;

    public ChatGui(String idRichiesta, Runnable azioneIndietro,  boolean utenteCorrenteEProprietario) {
        this.idRichiesta = idRichiesta;
        this.azioneIndietro = azioneIndietro;
        this.utenteCorrenteEProprietario = utenteCorrenteEProprietario;
    }

    public void mostra() {

        RootPanel.get().clear();

        Utente utente = SessioneUtente.getUtenteCorrente();

        if (utente == null) {
            new LoginGui().mostra();
            return;
        }

        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(10);

        HTML titolo = new HTML("<h2>Chat</h2>");
        Label messaggioStato = new Label("Caricamento conversazione...");

        panel.add(titolo);
        panel.add(messaggioStato);

        VerticalPanel cronologiaPanel = new VerticalPanel();
        cronologiaPanel.setSpacing(8);
        panel.add(cronologiaPanel);

        TextArea testoArea = new TextArea();
        testoArea.getElement().setPropertyString(
                "placeholder",
                "Scrivi un messaggio"
        );
        Button inviaButton = new Button("Invia");
        Label messaggioErrore = new Label();

        inviaButton.addClickHandler(event -> {
            messaggioErrore.setText("");

            String testo = testoArea.getText().trim();

            if (testo.isEmpty()) {
                messaggioErrore.setText("Il testo del messaggio è obbligatorio.");
                return;
            }

            inviaButton.setEnabled(false);

            messaggioService.invia(
                    idRichiesta,
                    utente.getId(),
                    testo,
                    new AsyncCallback<Messaggio>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            inviaButton.setEnabled(true);
                            messaggioErrore.setText(
                                    caught.getMessage() != null
                                            ? caught.getMessage()
                                            : "Errore durante l'invio del messaggio."
                            );
                        }

                        @Override
                        public void onSuccess(Messaggio nuovoMessaggio) {
                            testoArea.setText("");
                            inviaButton.setEnabled(true);
                            // Ricarica l'intera schermata per mostrare
                            // subito il nuovo messaggio nella cronologia.
                            mostra();
                        }
                    }
            );
        });

        panel.add(testoArea);
        panel.add(inviaButton);
        panel.add(messaggioErrore);

        if (utenteCorrenteEProprietario) {

            TextBox linkBox = new TextBox();
            linkBox.getElement().setPropertyString("placeholder", "https://esempio.it/incontro");

            Button condividiLinkButton = new Button("Condividi link");
            Label linkErrore = new Label();

            condividiLinkButton.addClickHandler(event -> {
                linkErrore.setText("");

                String url = linkBox.getText().trim();

                if (url.isEmpty()) {
                    linkErrore.setText("Inserisci un link da condividere.");
                    return;
                }

                if (!ValidatoreLink.linkValido(url)) {
                    linkErrore.setText("Il link inserito non è valido.");
                    return;
                }

                condividiLinkButton.setEnabled(false);

                messaggioService.condividiLink(
                        idRichiesta,
                        utente.getId(),
                        url,
                        new AsyncCallback<Messaggio>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                condividiLinkButton.setEnabled(true);
                                linkErrore.setText(
                                        caught.getMessage() != null
                                                ? caught.getMessage()
                                                : "Errore durante la condivisione del link."
                                );
                            }

                            @Override
                            public void onSuccess(Messaggio nuovoMessaggio) {
                                linkBox.setText("");
                                condividiLinkButton.setEnabled(true);
                                mostra();
                            }
                        }
                );
            });

            panel.add(linkBox);
            panel.add(condividiLinkButton);
            panel.add(linkErrore);
        }

        VerticalPanel terminaScambioPanel = new VerticalPanel();
        panel.add(terminaScambioPanel);

        Button indietroButton = new Button("Indietro");
        indietroButton.addClickHandler(event -> azioneIndietro.run());
        panel.add(indietroButton);

        RootPanel.get().add(panel);

        richiestaScambioService.trovaPerId(
                idRichiesta,
                utente.getId(),
                new AsyncCallback<RichiestaScambio>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        caricaCronologia(utente, messaggioStato, cronologiaPanel, terminaScambioPanel);
                    }

                    @Override
                    public void onSuccess(RichiestaScambio richiesta) {
                        String idAltro = utente.getId().equals(richiesta.getIdMittente())
                                ? richiesta.getIdProprietario()
                                : richiesta.getIdMittente();

                        utenteService.trovaPerId(idAltro, new AsyncCallback<Utente>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                caricaCronologia(utente, messaggioStato, cronologiaPanel, terminaScambioPanel);
                            }

                            @Override
                            public void onSuccess(Utente altro) {
                                if (altro != null) {
                                    nomeAltroPartecipante = altro.getNomeUtente();
                                }
                                caricaCronologia(utente, messaggioStato, cronologiaPanel, terminaScambioPanel);
                            }
                        });
                    }
                }
        );
    }

    private void caricaCronologia(Utente utente, Label messaggioStato, VerticalPanel cronologiaPanel, VerticalPanel terminaScambioPanel) {

        messaggioService.apriChat(
                idRichiesta,
                utente.getId(),
                new AsyncCallback<List<Messaggio>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        messaggioStato.setText(
                                caught.getMessage() != null
                                        ? caught.getMessage()
                                        : "Errore durante l'apertura della chat."
                        );
                    }

                    @Override
                    public void onSuccess(List<Messaggio> messaggi) {

                        if (messaggi == null || messaggi.isEmpty()) {
                            messaggioStato.setText(
                                    "Nessun messaggio in questa conversazione."
                            );
                            return;
                        }

                        messaggioStato.setText("");

                        messaggi.sort(Comparator.comparingLong(Messaggio::getDataInvio));

                        for (Messaggio messaggio : messaggi) {
                            cronologiaPanel.add(creaLabelMessaggio(messaggio, utente));
                        }

                        boolean linkCondiviso = messaggi.stream().anyMatch(Messaggio::isLink);

                        if (linkCondiviso && utenteCorrenteEProprietario) {
                            aggiungiBottoneTerminaScambio(utente, terminaScambioPanel);
                        }
                    }
                }
        );
    }

    /**
     * Aggiunge il bottone per terminare lo scambio, visibile solo al
     * proprietario. Dopo la conferma, la richiesta viene conclusa e
     * la chat non è più accessibile a nessuno dei due partecipanti.
     */
    private void aggiungiBottoneTerminaScambio(Utente utente, VerticalPanel terminaScambioPanel) {

        Button terminaButton = new Button("Termina scambio");
        Label terminaErrore = new Label();

        terminaButton.addClickHandler(event -> {
            terminaErrore.setText("");

            boolean confermato = Window.confirm(
                    "Confermi di voler terminare questo scambio? "
                            + "La chat non sarà più accessibile."
            );

            if (!confermato) {
                return;
            }

            terminaButton.setEnabled(false);

            richiestaScambioService.termina(
                    idRichiesta,
                    utente.getId(),
                    new AsyncCallback<RichiestaScambio>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            terminaButton.setEnabled(true);
                            terminaErrore.setText(
                                    caught.getMessage() != null
                                            ? caught.getMessage()
                                            : "Errore durante la chiusura dello scambio."
                            );
                        }

                        @Override
                        public void onSuccess(RichiestaScambio richiestaConclusa) {
                            // La chat non è più accessibile, torna alla schermata precedente.
                            azioneIndietro.run();
                        }
                    }
            );
        });

        terminaScambioPanel.add(terminaButton);
        terminaScambioPanel.add(terminaErrore);
    }

    private Widget creaLabelMessaggio(Messaggio messaggio, Utente utente) {
        String prefisso = utente.getId().equals(messaggio.getIdMittente())
                ? "Tu: "
                : nomeAltroPartecipante + ": ";

        if (messaggio.isLink()) {
            Anchor link = new Anchor(prefisso + messaggio.getTesto(), messaggio.getTesto(), "_blank");
            return link;
        }

        return new Label(prefisso + messaggio.getTesto());
    }
}