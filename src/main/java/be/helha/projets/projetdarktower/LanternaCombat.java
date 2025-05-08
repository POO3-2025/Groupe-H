package be.helha.projets.projetdarktower;

import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Item.ItemFactory;
import be.helha.projets.projetdarktower.Model.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class LanternaCombat {

    private static final int SCREEN_WIDTH = 100;
    private static final int SCREEN_HEIGHT = 20;

    public static void main(String[] args) {
        try {
            startCombatScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startCombatScreen() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory()
                .setInitialTerminalSize(new TerminalSize(SCREEN_WIDTH, SCREEN_HEIGHT));
        SwingTerminalFrame terminal = terminalFactory.createSwingTerminal();
        terminal.setVisible(true);
        terminal.setResizable(false);

        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
        BasicWindow window = createCombatWindow(gui, screen);

        gui.addWindowAndWait(window);
        screen.stopScreen();
    }

    private static BasicWindow createCombatWindow(MultiWindowTextGUI gui, Screen screen) {
        BasicWindow window = new BasicWindow("Combat - DarkTower");

        Etage etage = new Etage(1);
        Tour tour = new Tour(1);
        Personnage joueur = new FistFire("Joueur");
        Minotaurus minotaure = new Minotaurus("Minotaure", etage.getEtage());
        // Création des objets pour l'inventaire du joueur
        List<Item> stock = new ArrayList<>(ItemFactory.getAllItems().values());




        Panel mainPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        Panel contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        Panel historyPanel = new Panel(new LinearLayout(Direction.VERTICAL));

        Label lblTour = new Label("Tour : " + tour.getTour());
        Label lblJoueurPV = new Label(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
        Label lblMinotaurePV = new Label(minotaure.getNom() + " PV: " + minotaure.getPointsDeVie());
        Label lblEtage = new Label("Etage : " + etage.getEtage());

        contentPanel.addComponent(lblTour);
        contentPanel.addComponent(lblJoueurPV);
        contentPanel.addComponent(lblMinotaurePV);
        contentPanel.addComponent(lblEtage);

        historyPanel.addComponent(new Label("Historique:"));
        historyPanel.addComponent(new Label("Début du combat"));

        Button btnAttaquer = new Button("Attaquer", () -> handleAttack(
                joueur, minotaure, tour, etage,
                lblTour, lblJoueurPV, lblMinotaurePV,
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
            Label lblTour, Label lblJoueurPV, Label lblMinotaurePV,
            Panel historyPanel, MultiWindowTextGUI gui, Screen screen,
            BasicWindow window, Panel mainPanel) {

        try { sleep(500); } catch (InterruptedException ignored) {}

        int degatsJoueur = joueur.attaquer(minotaure);
        lblMinotaurePV.setText(minotaure.getNom() + " PV: " + minotaure.getPointsDeVie());
        historyPanel.addComponent(new Label("\nVous avez infligé " + degatsJoueur + " dégats"));
        updateGui(gui);

        try { sleep(300); } catch (InterruptedException ignored) {}

        int degatsMinotaure = minotaure.attaquer(joueur);
        lblJoueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
        historyPanel.addComponent(new Label("\nLe Minotaure vous a infligé " + degatsMinotaure + " dégats"));

        tour.incrementer();
        lblTour.setText("Tour : " + tour.getTour());

        try { sleep(100); } catch (InterruptedException ignored) {}

        if (joueur.getPointsDeVie() <= 0 || minotaure.getPointsDeVie() <= 0) {
            showEndCombat(gui, joueur, minotaure, etage, tour,
                    historyPanel, lblTour, lblJoueurPV, lblMinotaurePV,
                    window, mainPanel);
        }

        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showEndCombat(
            MultiWindowTextGUI gui, Personnage joueur, Minotaurus minotaure,
            Etage etage, Tour tour, Panel historyPanel,
            Label lblTour, Label lblJoueurPV, Label lblMinotaurePV,
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
            endPanel.addComponent(new Button("Suivant", () -> {
                if (etage.getEtage() < 20) {
                    etage.incrementer();
                    minotaure.setNiveau(etage.getEtage());
                    minotaure.resetPointsDeVie();
                    tour.resetTour();
                    lblTour.setText("Tour : " + tour.getTour());
                    lblMinotaurePV.setText(minotaure.getNom() + " PV: " + minotaure.getPointsDeVie());
                    window.setComponent(mainPanel);
                }
            }));
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
