package be.helha.projets.projetdarktower;

import be.helha.projets.projetdarktower.Model.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.Arrays;

public class LanternaCombat {

    public static void main(String[] args) {
        try {
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            Screen screen = terminalFactory.createScreen();
            screen.startScreen();

            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            BasicWindow window = new BasicWindow("Combat : Joueur vs Minautor");

            Panel contentPanel = new Panel();
            contentPanel.setLayoutManager(new GridLayout(1));

            Label title = new Label("Début du combat !");
            contentPanel.addComponent(title);

            // Crée un personnage et un bot
            Personnage joueur = new FistFire("1");
            Personnage minautor = new Personnage("bot1", "Minautor", 80, 20) {
                @Override
                public void attaquer(Personnage cible, String typeAttaque) {
                    int degats = typeAttaque.equals("physique") ? attaque : attaque + 5;
                    cible.pointsDeVie -= degats;
                    System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
                }
            };

            // Combat simulé
            joueur.attaquer(minautor, "magique");
            minautor.attaquer(joueur, "physique");

            contentPanel.addComponent(new Label(joueur.getNom() + " PV: " + joueur.getPointsDeVie()));
            contentPanel.addComponent(new Label(minautor.getNom() + " PV: " + minautor.getPointsDeVie()));

            Button quitButton = new Button("Quitter", window::close);
            contentPanel.addComponent(quitButton);

            window.setComponent(contentPanel);
            gui.addWindowAndWait(window);

            screen.stopScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
