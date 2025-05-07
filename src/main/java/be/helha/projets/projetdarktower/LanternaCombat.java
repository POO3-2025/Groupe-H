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

            // Utilisation d'un objet mutable pour gérer l'étage
            final Etage etageObj = new Etage(1); // Création de l'objet "Etage" qui sera mutable
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            BasicWindow window = new BasicWindow("Etage " + etageObj.getEtage());

            // Panel principal avec centrage
            Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));
            mainPanel.addComponent(new EmptySpace(new TerminalSize(1, 1))); // espace haut

            // Panel de contenu centré
            Panel contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));

            // Création du personnage joueur
            Personnage joueur = new FistFire("Joueur");

            // Création du minotaure
            Minotaurus minautor = new Minotaurus("Minotaure", etageObj.getEtage());

            // Affichage des points de vie
            Label joueurPV = new Label(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
            Label minautorPV = new Label(minautor.getNom() + " PV: " + minautor.getPointsDeVie());

            contentPanel.addComponent(joueurPV);
            contentPanel.addComponent(minautorPV);

            // Boutons de quitter et attaquer
            Button quitButton = new Button("Quitter", window::close);
            Button attackButton = new Button("Attaquer", () -> {
                // Attaque du joueur
                joueur.attaquer(minautor);
                joueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
                minautorPV.setText(minautor.getNom() + " PV: " + minautor.getPointsDeVie());

                if (joueur.getPointsDeVie() <= 0 || minautor.getPointsDeVie() <= 0) {
                    String messageFin = (joueur.getPointsDeVie() <= 0)
                            ? "Vous avez perdu contre " + minautor.getNom() + " !"
                            : "Félicitations ! Vous avez vaincu l'étage N°" + etageObj.getEtage() + "\nVeuillez passer à l'étage suivant";

                    Panel endPanel = new Panel(new LinearLayout(Direction.VERTICAL));
                    endPanel.addComponent(new Label(messageFin));

                    // Si le joueur est à moins de 20 étages, on passe à l'étage suivant
                    Button nextButton = new Button("Suivant", () -> {
                        if (etageObj.getEtage() < 20) {
                            etageObj.incrementer(); // Incrémenter l'étage
                            minautor.setNiveau(etageObj.getEtage());  // Mise à jour du niveau du Minotaure
                            minautor.resetPointsDeVie(); // Réinitialiser les PV du Minotaure

                            // Mettre à jour le titre de la fenêtre avec le nouvel étage
                            window.setTitle("Etage " + etageObj.getEtage());

                            // Mettre à jour la fenêtre avec le combat suivant
                            joueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
                            minautorPV.setText(minautor.getNom() + " PV: " + minautor.getPointsDeVie());

                            // Mettre à jour la fenêtre
                            window.setComponent(mainPanel);
                            gui.addWindowAndWait(window);
                        } else {
                            // Si 20 étages sont atteints, le jeu est fini
                            Panel gameOverPanel = new Panel(new LinearLayout(Direction.VERTICAL));
                            gameOverPanel.addComponent(new Label("Vous avez terminé les 20 étages !"));
                            gameOverPanel.addComponent(new Button("Quitter", window::close));

                            window.setComponent(gameOverPanel);
                            gui.addWindowAndWait(window);
                        }
                    });

                    endPanel.addComponent(nextButton);
                    endPanel.addComponent(new Button("Arreter", window::close));

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

            // Ajout des boutons et autres composants à la fenêtre
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

    // Classe pour encapsuler l'étage et le rendre mutable
    static class Etage {
        private int etage;

        public Etage(int etage) {
            this.etage = etage;
        }

        public int getEtage() {
            return etage;
        }

        public void incrementer() {
            this.etage++;
        }
    }
}
