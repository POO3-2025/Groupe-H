package be.helha.projets.projetdarktower.Menu;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

public class MainMenuHandler {

    private Screen screen;

    public MainMenuHandler() throws Exception {
        // Initialisation de Lanterna
        screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
    }

    public void showMenu() {
        // Création du GUI avec plusieurs fenêtres possibles
        WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

        // Création de la fenêtre du menu
        Window window = new BasicWindow("Main Menu");
        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        // Ajout des boutons dans le menu
        contentPanel.addComponent(new Button("1) Commencer Partie", () -> startGame()));
        contentPanel.addComponent(new Button("2) Utiliser Item", () -> useItem()));
        contentPanel.addComponent(new Button("3) Retour", () -> goBack()));
        contentPanel.addComponent(new Button("4) Quitter", () -> quit()));

        window.setComponent(contentPanel);
        textGUI.addWindowAndWait(window); // Affichage et attente de l'action de l'utilisateur
    }

    private void startGame() {
        System.out.println("Commencer Partie");
        // Ajoute ici la logique pour démarrer la partie
        // Exemple: tu peux afficher un message, ou appeler une autre méthode pour démarrer le jeu.
        goBack(); // Retourner après avoir démarré la partie
    }

    private void useItem() {
        System.out.println("Utiliser Item");
        // Ajoute ici la logique pour utiliser un item
        // Exemple: tu peux afficher un menu pour choisir un item à utiliser.
        goBack(); // Retourner après avoir utilisé l'item
    }

    private void goBack() {
        System.out.println("Retour au menu principal");
        // Logique pour revenir au menu des personnages ou à un autre menu
        // Si tu as une méthode pour revenir à un autre menu, appelle-la ici.
        // Exemple:
        // MainLanternaCharacterMenu.main(null); // Si tu veux revenir au menu de sélection des personnages
    }

    private void quit() {
        System.out.println("Quitter");
        System.exit(0); // Quitter l'application
    }

    public static void main(String[] args) throws Exception {
        MainMenuHandler menuHandler = new MainMenuHandler();
        menuHandler.showMenu(); // Affichage du menu
    }
}
