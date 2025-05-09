package be.helha.projets.projetdarktower;

import be.helha.projets.projetdarktower.Inventaire.InventaireDAOImpl;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Item.ItemFactory;
import be.helha.projets.projetdarktower.Model.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import be.helha.projets.projetdarktower.Service.CharacterService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class LanternaCombat {

    private static final int SCREEN_WIDTH = 100;
    private static final int SCREEN_HEIGHT = 20;
    private static final int userId = 1; // Utilisateur fictif
    private static final CharacterService characterService = new CharacterService();
    public static InventaireDAOImpl inventaireDAO = new InventaireDAOImpl();


    public static void main(String[] args) {
        try {
            startCharacterSelection();  // Démarrer avec la sélection du personnage
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startCharacterSelection() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory()
                .setInitialTerminalSize(new TerminalSize(SCREEN_WIDTH, SCREEN_HEIGHT));
        SwingTerminalFrame terminal = terminalFactory.createSwingTerminal();
        terminal.setVisible(true);
        terminal.setResizable(false);

        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

        showCharacterSelection(gui);  // Appeler la méthode de sélection du personnage

        screen.stopScreen();
    }

    private static void showCharacterSelection(MultiWindowTextGUI gui) {
        BasicWindow window = new BasicWindow("Sélection du personnage");

        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("Choisissez un personnage :"));

        // Boutons pour chaque personnage
        panel.addComponent(new Button("Fist Fire", () -> showCharacterDetails(gui, "fistfire", window)));
        panel.addComponent(new Button("Water Wa", () -> showCharacterDetails(gui, "waterwa", window)));
        panel.addComponent(new Button("Jo Wind", () -> showCharacterDetails(gui, "jowind", window)));
        panel.addComponent(new Button("TWood", () -> showCharacterDetails(gui, "twood", window)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", window::close));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private static void showCharacterDetails(MultiWindowTextGUI gui, String characterId, BasicWindow currentWindow) {
        // Instancier le personnage choisi en fonction de l'ID
        Personnage selectedPersonnage = createCharacter(characterId);

        if (selectedPersonnage != null) {
            String message = "Vous avez choisi " + selectedPersonnage.getNom() + " - Points de vie : " + selectedPersonnage.getPointsDeVie();
            MessageDialog.showMessageDialog(gui, "Personnage choisi", message);

            Button BtnItem = new Button("suivant", () -> {
                afficherEtChoisirItem(gui, currentWindow, selectedPersonnage, () -> {
                    BasicWindow combatWindow = createCombatWindow(gui, gui.getScreen(), selectedPersonnage);
                    gui.addWindowAndWait(combatWindow);
                });
            });


            // Ajoute le bouton à l'interface
            Panel panel = new Panel(new GridLayout(1));
            panel.addComponent(new Label("Vous avez sélectionné " + selectedPersonnage.getNom()));
            panel.addComponent(BtnItem);
            panel.addComponent(new Button("Retour", currentWindow::close)); // Pour revenir en arrière

            currentWindow.setComponent(panel);
        } else {
            MessageDialog.showMessageDialog(gui, "Erreur", "Personnage non trouvé.");
        }
    }
    public static void afficherEtChoisirItem(MultiWindowTextGUI gui, BasicWindow parentWindow,
                                             Personnage selectedPersonnage, Runnable onItemChosen) {
        inventaireDAO.initialiserInventaireVide(userId);
        List<Item> stock = new ArrayList<>(ItemFactory.getAllItems().values());
        List<Item> itemsChoisis = new ArrayList<>();
        List<Item> stockAvecOccurrences = new ArrayList<>();
        Random random = new Random();

        for (Item item : stock) {
            int occurrences = (int) item.getChanceDeDrop();
            for (int i = 0; i < occurrences; i++) stockAvecOccurrences.add(item);
        }

        for (int i = 0; i < 3; i++) {
            int index = random.nextInt(stockAvecOccurrences.size());
            itemsChoisis.add(stockAvecOccurrences.get(index));
        }

        BasicWindow choixItemWindow = new BasicWindow("Choisissez un Item");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        for (Item item : itemsChoisis) {
            String itemNom = item.getNom();
            Button itemButton = new Button(itemNom, () -> {
                try {
                    inventaireDAO.ajouterItem(item, userId);
                    MessageDialog.showMessageDialog(gui, "Succès", "L'item " + itemNom + " a été ajouté.");
                } catch (Exception e) {
                    MessageDialog.showMessageDialog(gui, "Erreur", "Ajout de l'item impossible.");
                }

                choixItemWindow.close();
                onItemChosen.run(); // Appeler l'action passée
            });

            panel.addComponent(itemButton);
        }

        choixItemWindow.setComponent(panel);
        gui.addWindowAndWait(choixItemWindow);
    }





    private static Personnage createCharacter(String characterId) {
        switch (characterId) {
            case "fistfire":
                return new FistFire("Fist Fire");
            case "waterwa":
                return new WaterWa("Water Wa");
            case "jowind":
                return new JoWind("Jo Wind");
            case "twood":
                return new TWood("TWood");
            default:
                return null; // Si le personnage n'est pas trouvé
        }
    }



// PARTIE COMBAT

    private static BasicWindow createCombatWindow(MultiWindowTextGUI gui, Screen screen, Personnage joueur) {
        BasicWindow window = new BasicWindow("Combat - DarkTower");

        Etage etage = new Etage(1);
        Tour tour = new Tour(1);
        Minotaurus minotaure = new Minotaurus("Minotaure", etage.getEtage());
        // Création des objets dans le stock

        Panel mainPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        Panel contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        Panel historyPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        Label lblEtage = new Label("Etage : " + etage.getEtage() +"\n");
        Label lblTour = new Label("Tour : " + tour.getTour());
        Label lblJoueurPV = new Label(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
        Label lblMinotaurePV = new Label(minotaure.getNom() + " PV: " + minotaure.getPointsDeVie());

        contentPanel.addComponent(lblEtage);
        contentPanel.addComponent(lblTour);
        contentPanel.addComponent(lblJoueurPV);
        contentPanel.addComponent(lblMinotaurePV);

        historyPanel.addComponent(new Label("Historique:"));
        historyPanel.addComponent(new Label("Début du combat"));

        Button btnAttaquer = new Button("Attaquer", () -> handleAttack(
                joueur, minotaure, tour, etage,
                lblTour, lblJoueurPV, lblMinotaurePV,lblEtage,
                historyPanel, gui, screen, window, mainPanel));

        Button btnQuitter = new Button("Quitter", window::close);

        contentPanel.addComponent(btnAttaquer);
        contentPanel.addComponent(btnQuitter);

        mainPanel.addComponent(contentPanel, LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        mainPanel.addComponent(historyPanel, LinearLayout.createLayoutData(LinearLayout.Alignment.End));

        window.setComponent(mainPanel);
        return window;
    }

    private static void handleAttack(
            Personnage joueur, Minotaurus minotaure, Tour tour, Etage etage,
            Label lblTour, Label lblJoueurPV, Label lblMinotaurePV,Label lblEtage,
            Panel historyPanel, MultiWindowTextGUI gui, Screen screen,
            BasicWindow window, Panel mainPanel) {

        try { sleep(500); } catch (InterruptedException ignored) {}

        int degatsJoueur = joueur.attaquer(minotaure);
        lblMinotaurePV.setText(minotaure.getNom() + " PV: " + minotaure.getPointsDeVie());
        historyPanel.addComponent(new Label("\nVous avez infligé " + degatsJoueur + " dégats"));
        updateGui(gui);

        try { sleep(300); } catch (InterruptedException ignored) {}

        if (minotaure.getPointsDeVie() <= 0) {
            showEndCombat(gui, joueur, minotaure, etage, tour,
                    historyPanel, lblTour, lblJoueurPV, lblMinotaurePV,lblEtage,
                    window, mainPanel);
            return;
        }

        int degatsMinotaure = minotaure.attaquer(joueur);
        lblJoueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
        historyPanel.addComponent(new Label("\nLe Minotaure vous a infligé " + degatsMinotaure + " dégats"));

        tour.incrementer();
        lblTour.setText("Tour : " + tour.getTour());

        try { sleep(100); } catch (InterruptedException ignored) {}

        if (joueur.getPointsDeVie() <= 0 || minotaure.getPointsDeVie() <= 0) {
            showEndCombat(gui, joueur, minotaure, etage, tour,
                    historyPanel, lblTour, lblJoueurPV, lblMinotaurePV,lblEtage,
                    window, mainPanel);
        }

        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void useItem(){

    };

    private static void showEndCombat(
            MultiWindowTextGUI gui, Personnage joueur, Minotaurus minotaure,
            Etage etage, Tour tour, Panel historyPanel,
            Label lblTour, Label lblJoueurPV, Label lblMinotaurePV,Label lblEtage,
            BasicWindow window, Panel mainPanel) {

        String finCombatMsg = (joueur.getPointsDeVie() <= 0)
                ? "Vous avez perdu contre " + minotaure.getNom() + " !"
                : "Félicitations ! Vous avez vaincu l'étage N°" + etage.getEtage();

        Panel endPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        endPanel.addComponent(new Label(finCombatMsg));

        if (joueur.getPointsDeVie() <= 0) {
            endPanel.addComponent(new Button("Recommencer", () -> {
                restartCombat(joueur, minotaure, etage, tour, historyPanel, lblTour, lblJoueurPV, lblMinotaurePV);
                window.setComponent(mainPanel);
            }));
        } else {

            Button btnSuite = new Button ("Suivant", () -> afficherEtChoisirItem(gui, window, joueur, () -> {
                // Étape 2 : après le choix, proposer de continuer
                Panel suitePanel = new Panel(new LinearLayout(Direction.VERTICAL));
                suitePanel.addComponent(new Label("Souhaitez-vous passer à l'étage suivant ?"));

                    if (etage.getEtage() < 20) {
                        etage.incrementer();
                        lblEtage.setText("Etage : " + etage.getEtage());
                        minotaure.setNiveau(etage.getEtage());
                        minotaure.resetPointsDeVie();
                        tour.resetTour();
                        lblTour.setText("Tour : " + tour.getTour());
                        lblMinotaurePV.setText(minotaure.getNom() + " PV: " + minotaure.getPointsDeVie());
                        historyPanel.removeAllComponents();
                        historyPanel.addComponent(new Label("Début du combat"));
                        updateGui(gui);
                        window.setComponent(mainPanel);
                    }
                    else {
                        MessageDialog.showMessageDialog(gui, "Fin", "Vous avez vaincu tous les étages !");
                        window.close();
                    }

                suitePanel.addComponent(new Button("Suivant", () -> {
                    BasicWindow combatWindow = createCombatWindow(gui, gui.getScreen(), joueur);
                    gui.addWindowAndWait(combatWindow);
                }));
                suitePanel.addComponent(new Button("Quitter", window::close));
                window.setComponent(suitePanel);
            }));
            endPanel.addComponent(btnSuite);
        }

        endPanel.addComponent(new Button("Quitter", window::close));
        window.setComponent(endPanel);
    }

    private static void restartCombat(
            Personnage joueur, Minotaurus minotaure, Etage etage, Tour tour,
            Panel historyPanel, Label lblTour, Label lblJoueurPV, Label lblMinotaurePV) {

        historyPanel.removeAllComponents();
        historyPanel.addComponent(new Label("Début du combat"));

        etage.resetEtage();
        minotaure.setNiveau(etage.getEtage());
        minotaure.resetPointsDeVie();
        joueur.resetPointDeVie();
        tour.resetTour();

        lblTour.setText("Tour : " + tour.getTour());
        lblJoueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
        lblMinotaurePV.setText(minotaure.getNom() + " PV: " + minotaure.getPointsDeVie());
    }

    private static void updateGui(MultiWindowTextGUI gui) {
        try {
            gui.updateScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Etage {
        private int etage;
        public Etage(int etage) { this.etage = etage; }
        public int getEtage() { return etage; }
        public void incrementer() { this.etage++; }
        public void resetEtage() { this.etage = 1; }
    }

    static class Tour {
        private int tour;
        public Tour(int tour) { this.tour = tour; }
        public int getTour() { return tour; }
        public void incrementer() { this.tour++; }
        public void resetTour() { this.tour = 1; }
    }
}
