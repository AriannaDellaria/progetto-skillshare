package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Schermata per pubblicare un nuovo annuncio.
 */
public class CreaAnnuncioGui {

    private final AnnuncioServiceAsync annuncioService =
            GWT.create(AnnuncioService.class);

    // Mostra il form di creazione dell'annuncio.
    public void mostra() {
        RootPanel.get().clear();

        Utente utente = SessioneUtente.getUtenteCorrente();

        // Impedisce la pubblicazione senza login.
        if (utente == null) {
            new LoginGui().mostra();
            return;
        }

        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(10);

        Label titoloPagina = new Label("Crea annuncio");

        TextBox titoloBox = new TextBox();
        titoloBox.getElement().setPropertyString(
                "placeholder",
                "Es. Lezioni di Java"
        );

        TextArea descrizioneBox = new TextArea();
        descrizioneBox.setVisibleLines(5);
        descrizioneBox.setCharacterWidth(40);
        descrizioneBox.getElement().setPropertyString(
                "placeholder",
                "Descrivi ciò che vuoi insegnare"
        );

        TextBox categoriaBox = new TextBox();
        categoriaBox.getElement().setPropertyString(
                "placeholder",
                "Es. Informatica"
        );

        Label categorieSuggeriteLabel = new Label(
                "Categorie consigliate: "
                        + String.join(", ", CategorieSuggerite.elenco())
        );

        // Permette di indicare se l'annuncio è disponibile.
        ListBox disponibilitaBox = new ListBox();
        disponibilitaBox.addItem("Seleziona...");
        disponibilitaBox.addItem("Sì");
        disponibilitaBox.addItem("No");
        disponibilitaBox.setSelectedIndex(0);

        TextBox controprestazioneBox = new TextBox();
        controprestazioneBox.getElement().setPropertyString(
                "placeholder",
                "Es. lezioni di inglese in cambio"
        );

        Button pubblicaButton = new Button("Pubblica");
        Label messaggio = new Label();

        pubblicaButton.addClickHandler(event -> {
            messaggio.setText("");

            String titolo = titoloBox.getText().trim();
            String descrizione = descrizioneBox.getText().trim();
            String categoria = categoriaBox.getText().trim();
            String disponibilita = disponibilitaBox.getSelectedValue();
            String controprestazione = controprestazioneBox.getText().trim();

            if (titolo.isEmpty()) {
                messaggio.setText(
                        "Inserisci il titolo dell'annuncio."
                );
                return;
            }

            if (descrizione.isEmpty()) {
                messaggio.setText("Inserisci la descrizione dell'annuncio.");
                return;
            }

            if (disponibilitaBox.getSelectedIndex() == 0) {
                messaggio.setText("Seleziona la disponibilità.");
                return;
            }

            if (controprestazione.isEmpty()) {
                messaggio.setText("Inserisci la controprestazione richiesta.");
                return;
            }

            pubblicaButton.setEnabled(false);

            annuncioService.crea(
                    utente.getId(),
                    titolo,
                    descrizione,
                    categoria,
                    disponibilita,
                    controprestazione,
                    new AsyncCallback<Annuncio>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            pubblicaButton.setEnabled(true);

                            if (caught.getMessage() != null
                                    && !caught.getMessage()
                                    .trim()
                                    .isEmpty()) {
                                messaggio.setText(caught.getMessage());
                            } else {
                                messaggio.setText("Errore durante la pubblicazione.");
                            }
                        }

                        @Override
                        public void onSuccess(Annuncio annuncio) {
                            pubblicaButton.setEnabled(true);

                            if (annuncio == null) {
                                messaggio.setText("Impossibile pubblicare l'annuncio.");
                                return;
                            }

                            messaggio.setText("Annuncio pubblicato correttamente.");

                            titoloBox.setText("");
                            descrizioneBox.setText("");
                            categoriaBox.setText("");
                            disponibilitaBox.setSelectedIndex(0);
                            controprestazioneBox.setText("");
                        }
                    }
            );
        });

        panel.add(titoloPagina);

        panel.add(new Label("Titolo *"));
        panel.add(titoloBox);

        panel.add(new Label("Descrizione"));
        panel.add(descrizioneBox);

        panel.add(new Label("Categoria"));
        panel.add(categoriaBox);
        panel.add(categorieSuggeriteLabel);

        panel.add(new Label("Disponibilità *"));
        panel.add(disponibilitaBox);

        panel.add(new Label("Controprestazione richiesta *"));
        panel.add(controprestazioneBox);

        panel.add(new Label("* Campi obbligatori"));

        panel.add(pubblicaButton);
        panel.add(messaggio);

        panel.add(
                NavigationHelper.creaBottoneIndietroHomeUtente()
        );

        RootPanel.get().add(panel);
    }
}