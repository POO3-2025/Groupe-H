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
            final Tour tourObj = new Tour(1);
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            BasicWindow window = new BasicWindow("Etage " + etageObj.getEtage());

            // Panel principal avec centrage
            Panel mainPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));

            // Panel de contenu centré pour les PV, Tour, Etage
            Panel contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));

            // Panel pour l'historique
            Panel historyPanel = new Panel(new LinearLayout(Direction.VERTICAL));

            // Création du personnage joueur
            Personnage joueur = new FistFire("Joueur");

            // Création du minotaure
            Minotaurus minautor = new Minotaurus("Minotaure", etageObj.getEtage());

            // Affichage des points de vie
            Label NbrTour = new Label("Tour : " + tourObj.getTour());
            Label joueurPV = new Label(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
            Label minautorPV = new Label(minautor.getNom() + " PV: " + minautor.getPointsDeVie());
            Label etageLabel = new Label("Etage : " + etageObj.getEtage());

            contentPanel.addComponent(NbrTour);
            contentPanel.addComponent(joueurPV);
            contentPanel.addComponent(minautorPV);
            contentPanel.addComponent(etageLabel);

            // Historique des actions
            Label historiqueLabel = new Label("Historique:");
            historyPanel.addComponent(historiqueLabel);
            historyPanel.addComponent(new Label("Début du combat"));

            // Boutons de quitter et attaquer
            Button quitButton = new Button("Quitter", window::close);
            Button attackButton = new Button("Attaquer", () -> {
                // Attaque du joueur
                int degats = joueur.attaquer(minautor);
                NbrTour.setText("Tour : " + tourObj.getTour());
                joueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
                minautorPV.setText(minautor.getNom() + " PV: " + minautor.getPointsDeVie());
                int degatsMin = minautor.attaquer(joueur);
                NbrTour.setText("Tour : " + tourObj.getTour());
                joueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
                minautorPV.setText(minautor.getNom() + " PV: " + minautor.getPointsDeVie());

                // Ajout à l'historique
                historyPanel.addComponent(new Label("\nVous avez infligé " + degats + "dégats\n"));
                historyPanel.addComponent(new Label("\nLe Minotaure vous a infligé " + degatsMin + "dégats\n"));

                // Incrémentation du tour
                tourObj.incrementer();

                if (joueur.getPointsDeVie() <= 0 || minautor.getPointsDeVie() <= 0) {
                    String messageFin = (joueur.getPointsDeVie() <= 0)
                            ? "Vous avez perdu contre " + minautor.getNom() + " !"
                            : "Félicitations ! Vous avez vaincu l'étage N°" + etageObj.getEtage() + "\nVeuillez passer à l'étage suivant";

                    // Panel de fin de combat
                    Panel endPanel = new Panel(new LinearLayout(Direction.VERTICAL));
                    endPanel.addComponent(new Label(messageFin));
                    if (joueur.getPointsDeVie() <= 0){
                        Button RestartButton = new Button("Recommencez?", () -> {
                            // Réinitialiser l'historique
                            historyPanel.removeAllComponents();  // Vider l'historique actuel
                            historyPanel.addComponent(new Label("Début du combat"));  // Ajouter un message de redémarrage

                            etageObj.resetEtage();  // Réinitialiser l'étage
                            minautor.setNiveau(etageObj.getEtage());  // Mise à jour du niveau du Minotaure
                            minautor.resetPointsDeVie();  // Réinitialiser les PV du Minotaure
                            joueur.resetPointDeVie();  // Réinitialiser les PV du joueur
                            tourObj.resetTour();  // Réinitialiser le tour

                            // Mise à jour de l'interface
                            window.setTitle("Etage " + etageObj.getEtage());
                            NbrTour.setText("Tour : " + tourObj.getTour());
                            joueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
                            minautorPV.setText(minautor.getNom() + " PV: " + minautor.getPointsDeVie());

                            // Mise à jour du panneau principal
                            window.setComponent(mainPanel);
                            gui.addWindowAndWait(window);
                        });
                        endPanel.addComponent(RestartButton);
                    }
                    else {
                        Button nextButton = new Button("Suivant", () -> {
                            if (etageObj.getEtage() < 20) {
                                etageObj.incrementer(); // Incrémenter l'étage
                                minautor.setNiveau(etageObj.getEtage());  // Mise à jour du niveau du Minotaure
                                minautor.resetPointsDeVie(); // Réinitialiser les PV du Minotaure
                                tourObj.resetTour();

                                window.setTitle("Etage " + etageObj.getEtage());
                                NbrTour.setText("Tour : " + tourObj.getTour());
                                joueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
                                minautorPV.setText(minautor.getNom() + " PV: " + minautor.getPointsDeVie());

                                window.setComponent(mainPanel);
                                gui.addWindowAndWait(window);
                            }
                        });
                        endPanel.addComponent(nextButton);
                    };

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

            // Ajouter le panneau contentPanel et historyPanel au mainPanel (panneau principal)
            mainPanel.addComponent(contentPanel, LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
            mainPanel.addComponent(historyPanel, LinearLayout.createLayoutData(LinearLayout.Alignment.End)); // à droite

            // Mise à jour de la fenêtre avec les deux panneaux
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
        public void resetEtage(){
            this.etage = 1;
        }
    }

    // Classe pour encapsuler le tour et le rendre mutable
    static class Tour {
        private int tour;

        public Tour(int tour) {
            this.tour = tour;
        }

        public int getTour() {
            return tour;
        }

        public void incrementer() {
            this.tour++;
        }

        public void resetTour() {
            this.tour = 1;
        }
    }
}
