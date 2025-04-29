package be.helha.projets.projetdarktower;

import be.helha.projets.projetdarktower.Model.CharacterSelectionRequest;
import be.helha.projets.projetdarktower.Model.Personnage;
import be.helha.projets.projetdarktower.Service.CharacterService;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import java.io.IOException;

public class MainLanternaCharacterMenu {

    private static final String userId = "user1"; // Utilisateur fictif
    private static final CharacterService characterService = new CharacterService();

    public static void main(String[] args) {
        try {
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            terminalFactory.setInitialTerminalSize(new TerminalSize(100, 20));
            SwingTerminalFrame terminal = terminalFactory.createSwingTerminal();
            terminal.setVisible(true);
            terminal.setResizable(false);

            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();

            WindowBasedTextGUI gui = new MultiWindowTextGUI(screen);
            showMainMenu(gui);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showMainMenu(WindowBasedTextGUI gui) {
        BasicWindow window = new BasicWindow("Menu Principal - DarkTower");

        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("===== Bienvenue dans DarkTower ====="));

        panel.addComponent(new Button("1. Choisir un personnage", () -> {
            window.close();
            showCharacterSelection(gui);
        }));

        panel.addComponent(new Button("2. Quitter", () -> {
            try {
                gui.getGUIThread().invokeLater(() -> System.exit(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private static void showCharacterSelection(WindowBasedTextGUI gui) {
        BasicWindow window = new BasicWindow("Sélection du personnage");

        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("Choisissez un personnage :"));

        // Boutons pour chaque personnage
        panel.addComponent(new Button("Fist Fire", () -> showCharacterDetails(gui, "fistfire", window)));
        panel.addComponent(new Button("Water Wa", () -> showCharacterDetails(gui, "waterwa", window)));
        panel.addComponent(new Button("Jo Wind", () -> showCharacterDetails(gui, "jowind", window)));
        panel.addComponent(new Button("TWood", () -> showCharacterDetails(gui, "twood", window)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", () -> {
            window.close();
            showMainMenu(gui); // Retourne au menu principal
        }));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private static void showCharacterDetails(WindowBasedTextGUI gui, String characterId, BasicWindow currentWindow) {
        Personnage selectedPersonnage = characterService.selectCharacter(characterId);

        if (selectedPersonnage != null) {
            String message = "Vous avez choisi " + selectedPersonnage.getNom() + " - Points de vie : " + selectedPersonnage.getPointsDeVie();
            MessageDialog.showMessageDialog(gui, "Personnage choisi", message);
        } else {
            MessageDialog.showMessageDialog(gui, "Erreur", "Personnage non trouvé.");
        }

        currentWindow.close();
        //showMainMenu(gui); // Retourne au menu principal après sélection
    }
}
