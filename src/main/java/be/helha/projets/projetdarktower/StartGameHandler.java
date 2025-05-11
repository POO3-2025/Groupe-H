package be.helha.projets.projetdarktower;

import be.helha.projets.projetdarktower.Model.FistFire;
import be.helha.projets.projetdarktower.Model.JoWind;
import be.helha.projets.projetdarktower.Model.Personnage;
import be.helha.projets.projetdarktower.Model.TWood;
import be.helha.projets.projetdarktower.Model.WaterWa;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import java.io.IOException;

public class StartGameHandler {

    private static Screen screen;
    private static WindowBasedTextGUI gui;
    private static final String userId = "user1"; // Utilisateur fictif

    public static void main(String[] args) {
        try {
            // Initialisation du terminal et de l'écran
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            terminalFactory.setInitialTerminalSize(new TerminalSize(100, 20));
            SwingTerminalFrame terminal = terminalFactory.createSwingTerminal();
            terminal.setVisible(true);
            terminal.setResizable(false);

            screen = new TerminalScreen(terminal);
            screen.startScreen();
            gui = new MultiWindowTextGUI(screen);

            // Lancement du menu principal
            showMainMenu();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Nettoyer l'écran avant d'afficher un nouveau menu
    private static void clearScreen() {
        try {
            screen.clear();
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Menu principal : Commencer Partie, Utiliser Item, Quitter
    private static void showMainMenu() {
        clearScreen();
        BasicWindow window = new BasicWindow("Menu Principal - DarkTower");
        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("===== Menu Principal ====="));
        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("1. Commencer Partie", () -> {
            window.close();
            showCharacterSelection();
        }));

        panel.addComponent(new Button("2. Utiliser Item", () -> {
            MessageDialog.showMessageDialog(gui, "Utiliser Item", "Fonctionnalité à venir.");
        }));

        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("3. Quitter", () -> {
            try {
                gui.getGUIThread().invokeLater(() -> System.exit(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    // Sélection du personnage
    private static void showCharacterSelection() {
        clearScreen();
        BasicWindow window = new BasicWindow("Sélection du personnage");
        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("Choisissez un personnage :"));
        panel.addComponent(new EmptySpace());

        // Boutons pour chaque personnage
        panel.addComponent(new Button("Fist Fire", () -> {
            window.close();
            showSoloOrDuo(new FistFire(userId));
        }));
        panel.addComponent(new Button("Water Wa", () -> {
            window.close();
            showSoloOrDuo(new WaterWa(userId));
        }));
        panel.addComponent(new Button("Jo Wind", () -> {
            window.close();
            showSoloOrDuo(new JoWind(userId));
        }));
        panel.addComponent(new Button("TWood", () -> {
            window.close();
            showSoloOrDuo(new TWood(userId));
        }));

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", () -> {
            window.close();
            showMainMenu();
        }));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    // Choix du mode (Solo ou Duo) pour le personnage sélectionné
    private static void showSoloOrDuo(Personnage personnage) {
        clearScreen();
        BasicWindow window = new BasicWindow("Mode de jeu");
        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("Personnage : " + personnage.getNom()));
        panel.addComponent(new Label("Points de vie : " + personnage.getPointsDeVie()));
        panel.addComponent(new Label("Attaque : " + personnage.getAttaque()));
        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("1. Solo", () -> {
            window.close();
            startSoloMode(personnage);
        }));

        panel.addComponent(new Button("2. Duo (à venir)", () -> {
            MessageDialog.showMessageDialog(gui, "Mode Duo", "Le mode Duo n'est pas encore disponible.");
        }));

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", () -> {
            window.close();
            showCharacterSelection();
        }));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    // Démarrer le mode Solo et afficher les étages
    private static void startSoloMode(Personnage personnage) {
        clearScreen();
        BasicWindow window = new BasicWindow("Mode Solo");
        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("Mode Solo : " + personnage.getNom()));
        panel.addComponent(new Label("Points de vie : " + personnage.getPointsDeVie()));
        panel.addComponent(new Label("Attaque : " + personnage.getAttaque()));
        panel.addComponent(new EmptySpace());

        // Exemple de 5 étages
        for (int i = 1; i <= 5; i++) {
            panel.addComponent(new Label("Étage " + i + " : description de l'étage..."));
        }
        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("Terminer Partie", () -> {
            window.close();
            showMainMenu();
        }));

        panel.addComponent(new Button("Retour", () -> {
            window.close();
            showCharacterSelection();
        }));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }
}
