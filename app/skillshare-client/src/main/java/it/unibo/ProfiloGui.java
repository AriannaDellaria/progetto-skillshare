package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Mostra il profilo dell'utente autenticato.
 */
public class ProfiloGui {

    private final UtenteServiceAsync utenteService = GWT.create(UtenteService.class);

    /**
     * Visualizza le informazioni del profilo.
     */
    public void mostra() {

        RootPanel.get().clear();

        Utente utente = SessioneUtente.getUtenteCorrente();

        // Impedisce l'accesso senza login.
        if (utente == null) {
            new LoginGui().mostra();
            return;
        }

        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(10);

        HTML titolo = new HTML("<h2>Il mio profilo</h2>");

        Label nomeLabel = new Label("Nome: " + utente.getNomeUtente());
        Label emailLabel = new Label("Email: " + utente.getEmail());
        Label bioLabel = new Label("Biografia ");
        TextArea bioBox = new TextArea();
        bioBox.setVisibleLines(4);
        bioBox.setCharacterWidth(40);
        bioBox.setText(
                utente.getBio()
        );

        Label fotoLabel = new Label(
                "Foto profilo (formati consentiti: PNG, JPG, JPEG)"
        );
        FileUpload fotoUpload = new FileUpload();

        Image anteprimaFoto = new Image();
        anteprimaFoto.setWidth("150px");
        anteprimaFoto.setHeight("150px");

        //rendo tonda l'anteprima della foto
        anteprimaFoto.getElement()
                .getStyle()
                .setProperty(
                        "borderRadius",
                        "50%"
                );

        if (utente.getFotoProfilo() != null && !utente.getFotoProfilo().isEmpty()) {
            anteprimaFoto.setUrl(
                    utente.getFotoProfilo()
            );
        }

        fotoUpload.setName("fotoProfilo");
        fotoUpload.getElement().setAttribute(
                "accept",
                ".png,.jpg,.jpeg,image/png,image/jpeg"
        );

        // Permette di sapere se il file selezionato è valido.
        final boolean[] fotoValida = {true};

        // Contiene la foto aggiornata prima del salvataggio.
        final String[] nuovaFoto = {
                utente.getFotoProfilo()
        };

        Label fotoSelezionataLabel = new Label();

        fotoUpload.addChangeHandler(event -> {

            String nomeFile = fotoUpload.getFilename();

            if (nomeFile == null || nomeFile.trim().isEmpty()) {
                fotoValida[0] = true;
                fotoSelezionataLabel.setText("Nessuna immagine selezionata");
                anteprimaFoto.setUrl("");
                return;
            }


            String nomeMinuscolo = nomeFile.toLowerCase();

            boolean formatoSupportato =
                    nomeMinuscolo.endsWith(".png")
                            || nomeMinuscolo.endsWith(".jpg")
                            || nomeMinuscolo.endsWith(".jpeg");

            if (!formatoSupportato) {
                fotoValida[0] = false;
                fotoSelezionataLabel.setText("Tipo di file non supportato. Usa PNG, JPG o JPEG.");
                fotoUpload.getElement().setPropertyString("value", "");
                anteprimaFoto.setUrl("");
                return;
            }

            fotoValida[0] = true;
            fotoSelezionataLabel.setText("Immagine scelta: " + nomeFile);

            caricaAnteprimaFoto(
                    fotoUpload,
                    anteprimaFoto,
                    nuovaFoto
            );
        });

        Label tagLabel = new Label("Competenze separate da virgola");
        TextBox tagBox = new TextBox();
        tagBox.setText(String.join(", ", utente.getTagCompetenze()));

        Button salvaButton = new Button("Salva modifiche");
        Label messaggio = new Label();

        salvaButton.addClickHandler(event -> {

                    utenteService.aggiornaProfilo(
                            utente.getId(),
                            bioBox.getText(),
                            nuovaFoto[0],
                            convertiTag(tagBox.getText()),
                            new AsyncCallback<Utente>() {

                                @Override
                                public void onFailure(Throwable caught) {
                                    messaggio.setText("Errore durante il salvataggio");
                                }

                                @Override
                                public void onSuccess(Utente utenteAggiornato) {
                                    if (utenteAggiornato == null) {
                                        messaggio.setText(
                                                "Impossibile aggiornare la biografia"
                                        );
                                        return;
                                    }

                                    // Aggiorna la sessione con i nuovi dati.
                                    SessioneUtente.accedi(utenteAggiornato);
                                    messaggio.setText("Aggiornamento avvenuto con successo");
                                }
                            }
                    );
                }
        );

        panel.add(titolo);
        panel.add(anteprimaFoto);
        panel.add(fotoUpload);
        panel.add(fotoSelezionataLabel);
        panel.add(nomeLabel);
        panel.add(emailLabel);
        panel.add(bioLabel);
        panel.add(bioBox);
        panel.add(fotoLabel);
        panel.add(tagLabel);
        panel.add(tagBox);
        panel.add(salvaButton);
        panel.add(messaggio);
        panel.add(NavigationHelper.creaBottoneIndietroHomeUtente());

        RootPanel.get().add(panel);
    }

    /**
     * Trasforma il testo inserito in una lista di competenze.
     */
    private List<String> convertiTag(String testo) {
        List<String> tag = new ArrayList<>();

        if (testo == null || testo.trim().isEmpty()) {
            return tag;
        }

        for (String valore : testo.split(",")) {
            String tagPulito = valore.trim();

            if (!tagPulito.isEmpty()) {
                tag.add(tagPulito);
            }
        }

        return tag;
    }

    /**
     * Mostra l'anteprima della foto selezionata.
     */
    private native void caricaAnteprimaFoto(
            FileUpload upload,
            Image immagine,
            String[] nuovaFoto
    ) /*-{

    var file =
        upload.@com.google.gwt.user.client.ui.FileUpload::getElement()()
        .files[0];

    if (!file) {
        return;
    }

    var reader = new FileReader();

    reader.onload = function(e) {

        var url = e.target.result;

        immagine.@com.google.gwt.user.client.ui.Image::setUrl(Ljava/lang/String;)(url);

        nuovaFoto[0] = url;
    };

    reader.readAsDataURL(file);

    }-*/;
}