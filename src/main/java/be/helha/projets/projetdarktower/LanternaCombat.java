package be.helha.projets.projetdarktower;

import be.helha.projets.projetdarktower.Model.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import java.io.IOException;

public class LanternaCombat {

    public static void main(String[] args) {
        try {
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            terminalFactory.setInitialTerminalSize(new TerminalSize(100, 20));
            SwingTerminalFrame terminal = terminalFactory.createSwingTerminal();
            terminal.setVisible(true);
            terminal.setResizable(false);
            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();

            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            BasicWindow window = new BasicWindow("Combat : Joueur vs Minotaure");

            // Panel principal avec centrage
            Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));
            mainPanel.addComponent(new EmptySpace(new TerminalSize(1, 1))); // espace haut

            // Panel de contenu centré
            Panel contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));

            Personnage joueur = new FistFire("Joueur");
            Minotaurus minautor = new Minotaurus("Minotaure", 1);

            Label joueurPV = new Label(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
            Label minautorPV = new Label(minautor.getNom() + " PV: " + minautor.getPointsDeVie());

            contentPanel.addComponent(joueurPV);
            contentPanel.addComponent(minautorPV);

            Button quitButton = new Button("Quitter", window::close);
            Button attackButton = new Button("Attaquer", () -> {
                joueur.attaquer(minautor);
                joueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
                minautorPV.setText(minautor.getNom() + " PV: " + minautor.getPointsDeVie());

                if (joueur.getPointsDeVie() <= 0 || minautor.getPointsDeVie() <= 0) {
                    String messageFin = (joueur.getPointsDeVie() <= 0)
                            ? "Vous avez perdu contre " + minautor.getNom() + " !"
                            : "Félicitations ! Vous avez vaincu " + minautor.getNom() + " !";

                    Panel endPanel = new Panel(new LinearLayout(Direction.VERTICAL));
                    endPanel.addComponent(new Label(messageFin));
                    endPanel.addComponent(new Button("Quitter", window::close));

                    Panel finalPanel = new Panel(new LinearLayout(Direction.VERTICAL));
                    finalPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)));
                    finalPanel.addComponent(endPanel, LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
                    finalPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)));

                    window.setComponent(finalPanel);
                }

                try {
                    screen.refresh();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            contentPanel.addComponent(attackButton);
            contentPanel.addComponent(quitButton);

            mainPanel.addComponent(contentPanel, LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
            mainPanel.addComponent(new EmptySpace(new TerminalSize(1, 1))); // espace bas

            window.setComponent(mainPanel);
            gui.addWindow(window);
            screen.refresh();
            gui.addWindowAndWait(window);
            screen.stopScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
