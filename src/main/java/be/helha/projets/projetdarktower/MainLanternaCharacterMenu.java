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
        }));panel.addComponent(new Button("3. Inventaire", () -> {
            window.close();
            //showInventory(gui);
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

    private static void showCharacterDetails(WindowBasedTextGUI gui, String characterId, BasicWindow previousWindow) {
        Personnage selectedPersonnage = characterService.selectCharacter(characterId);

        if (selectedPersonnage == null) {
            MessageDialog.showMessageDialog(gui, "Erreur", "Personnage non trouvé.");
            return;
        }

        // Ferme la fenêtre précédente (sélection)
        previousWindow.close();

        // Nouvelle fenêtre de détails
        BasicWindow detailWindow = new BasicWindow("Détails du personnage");
        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("Vous avez choisi : " + selectedPersonnage.getNom()));
        panel.addComponent(new Label("Points de vie : " + selectedPersonnage.getPointsDeVie()));

        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("OK", detailWindow::close)); // ouvrir le menu qui gere les items
        panel.addComponent(new Button("Retour", () -> {
            detailWindow.close();
            showCharacterSelection(gui);
        }));

        detailWindow.setComponent(panel);
        gui.addWindowAndWait(detailWindow);
    }
    private static void showInventory(WindowBasedTextGUI gui) {
        BasicWindow inventoryWindow = new BasicWindow("Inventaire");

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("=== Inventaire de l'utilisateur ==="));

        // Exemple d'objets, à remplacer par un vrai service si nécessaire
        panel.addComponent(new Label("- Potion de soin"));
        panel.addComponent(new Label("- Épée rouillée"));
        panel.addComponent(new Label("- Parchemin de feu"));

        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("Retour", () -> {
            inventoryWindow.close();
            showMainMenu(gui);
        }));

        inventoryWindow.setComponent(panel);
        gui.addWindowAndWait(inventoryWindow);
    }


}
