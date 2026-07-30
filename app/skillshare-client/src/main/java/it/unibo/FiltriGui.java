package it.unibo;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Schermata per selezionare i filtri da applicare agli annunci.
public class FiltriGui {

    private final FiltroAnnunci filtroAttuale;

    public FiltriGui(FiltroAnnunci filtroAttuale) {
        this.filtroAttuale = filtroAttuale;
    }

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

        Label titoloPagina = new Label("Filtra annunci");
        panel.add(titoloPagina);

        panel.add(new Label("Categoria"));

        List<CheckBox> categoriaCheckBoxes = new ArrayList<>();

        for (String categoria : CategorieSuggerite.elenco()) {
            CheckBox checkBox = new CheckBox(categoria);

            if (filtroAttuale != null
                    && filtroAttuale.getCategorieSelezionate() != null
                    && filtroAttuale.getCategorieSelezionate().contains(categoria)) {
                checkBox.setValue(true);
            }

            categoriaCheckBoxes.add(checkBox);
            panel.add(checkBox);
        }

        panel.add(new Label("Altro"));

        TextBox altroBox = new TextBox();
        altroBox.getElement().setPropertyString(
                "placeholder",
                "Es. Fotografia"
        );

        if (filtroAttuale != null && filtroAttuale.getAltroCategoria() != null) {
            altroBox.setText(filtroAttuale.getAltroCategoria());
        }

        panel.add(altroBox);

        panel.add(new Label("Disponibilità"));

        ListBox disponibilitaBox = new ListBox();
        disponibilitaBox.addItem("Tutte");
        disponibilitaBox.addItem("Sì");
        disponibilitaBox.addItem("No");
        disponibilitaBox.setSelectedIndex(0);

        if (filtroAttuale != null
                && filtroAttuale.getDisponibilitaSelezionata() != null) {
            String disponibilitaSalvata = filtroAttuale.getDisponibilitaSelezionata();

            for (int i = 0; i < disponibilitaBox.getItemCount(); i++) {
                if (disponibilitaBox.getItemText(i).equalsIgnoreCase(disponibilitaSalvata)) {
                    disponibilitaBox.setSelectedIndex(i);
                }
            }
        }

        panel.add(disponibilitaBox);

        Button azzeraButton = new Button("Azzera filtri");
        Button applicaButton = new Button("Applica filtri");

        azzeraButton.addClickHandler(event -> new AnnunciGui().mostra());

        applicaButton.addClickHandler(event -> {
            FiltroAnnunci filtro = new FiltroAnnunci();

            Set<String> categorieSelezionate = new HashSet<>();
            for (CheckBox checkBox : categoriaCheckBoxes) {
                if (checkBox.getValue()) {
                    categorieSelezionate.add(checkBox.getText());
                }
            }
            filtro.setCategorieSelezionate(categorieSelezionate);

            filtro.setAltroCategoria(altroBox.getText().trim());

            if (disponibilitaBox.getSelectedIndex() > 0) {
                filtro.setDisponibilitaSelezionata(
                        disponibilitaBox.getSelectedValue()
                );
            }

            new AnnunciGui().mostraConFiltri(filtro);
        });

        panel.add(azzeraButton);
        panel.add(applicaButton);

        panel.add(
                NavigationHelper.creaBottoneIndietroHomeUtente()
        );

        RootPanel.get().add(panel);
    }
}