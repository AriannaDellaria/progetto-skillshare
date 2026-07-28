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

// Schermata per modificare un annuncio esistente
public class ModificaAnnuncioGui {

    private final AnnuncioServiceAsync annuncioService =
            GWT.create(AnnuncioService.class);

    private final Annuncio annuncio;

    public ModificaAnnuncioGui(Annuncio annuncio) {
        this.annuncio = annuncio;
    }

    // Mostra il form con i dati attuali dell'annuncio.
    public void mostra() {
        RootPanel.get().clear();

        Utente utente = SessioneUtente.getUtenteCorrente();

        if (utente == null) {
            new LoginGui().mostra();
            return;
        }

        // Impedisce la modifica di annunci altrui.
        if (!utente.getId().equals(annuncio.getIdAutore())) {
            new MieiAnnunciGui().mostra();
            return;
        }

        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(10);

        Label titoloPagina =
                new Label("Modifica annuncio");

        TextBox titoloBox = new TextBox();
        titoloBox.setText(
                valoreOvuoto(annuncio.getTitolo())
        );

        TextArea descrizioneBox = new TextArea();
        descrizioneBox.setVisibleLines(5);
        descrizioneBox.setCharacterWidth(40);
        descrizioneBox.setText(
                valoreOvuoto(annuncio.getDescrizione())
        );

        TextBox categoriaBox = new TextBox();
        categoriaBox.setText(
                valoreOvuoto(annuncio.getCategoria())
        );

        ListBox disponibilitaBox = new ListBox();
        disponibilitaBox.addItem("Sì");
        disponibilitaBox.addItem("No");

        // Seleziona il valore già presente nell'annuncio.
        if ("No".equalsIgnoreCase(annuncio.getDisponibilita())) {
            disponibilitaBox.setSelectedIndex(1);
        } else {
            disponibilitaBox.setSelectedIndex(0);
        }

        TextBox controprestazioneBox = new TextBox();
        controprestazioneBox.setText(
                valoreOvuoto(
                        annuncio.getControprestazione()
                )
        );

        Button salvaButton =
                new Button("Salva modifiche");

        Button annullaButton =
                new Button("Annulla");

        Label messaggio = new Label();

        salvaButton.addClickHandler(event -> {
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

            if (controprestazione.isEmpty()) {
                messaggio.setText(
                        "Inserisci la controprestazione richiesta."
                );
                return;
            }

            salvaButton.setEnabled(false);

            annuncioService.modificaAnnuncio(
                    annuncio.getId(),
                    utente.getId(),
                    titolo,
                    categoria,
                    descrizione,
                    disponibilita,
                    controprestazione,
                    new AsyncCallback<Annuncio>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            salvaButton.setEnabled(true);

                            messaggio.setText(
                                    "Errore durante la modifica."
                            );
                        }

                        @Override
                        public void onSuccess(
                                Annuncio annuncioModificato
                        ) {
                            salvaButton.setEnabled(true);

                            if (annuncioModificato == null) {
                                messaggio.setText(
                                        "Impossibile modificare "
                                                + "l'annuncio."
                                );
                                return;
                            }

                            new HomeGui().mostra();
                        }
                    }
            );
        });

        annullaButton.addClickHandler(event ->
                new MieiAnnunciGui().mostra()
        );

        panel.add(titoloPagina);

        panel.add(new Label("Titolo *"));
        panel.add(titoloBox);

        panel.add(new Label("Descrizione"));
        panel.add(descrizioneBox);

        panel.add(new Label("Categoria"));
        panel.add(categoriaBox);

        panel.add(new Label("Disponibilità *"));
        panel.add(disponibilitaBox);

        panel.add(
                new Label(
                        "Controprestazione richiesta *"
                )
        );
        panel.add(controprestazioneBox);

        panel.add(new Label("* Campi obbligatori"));

        panel.add(salvaButton);
        panel.add(annullaButton);
        panel.add(messaggio);

        RootPanel.get().add(panel);
    }

    // Evita valori nulli nei campi.
    private String valoreOvuoto(String valore) {
        return valore == null ? "" : valore;
    }
}
