package be.helha.projets.projetdarktower;

import be.helha.projets.projetdarktower.DTO.UseItemResult;
import be.helha.projets.projetdarktower.Inventaire.InventaireDAOImpl;
import be.helha.projets.projetdarktower.Item.*;
import be.helha.projets.projetdarktower.Model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import be.helha.projets.projetdarktower.Service.CharacterService;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;

public class LanternaCombat {

    private static String jwtToken = null;
    private static boolean isLoggedIn = false;
    private static int userId;
    private static String NomUser = null;
    public static Etage etageActuel = new Etage(1);
    public static Tour tourActuel = new Tour(1);
    public static Minotaurus minotaureActuel = new Minotaurus("999", etageActuel.getEtage());

    private static final CharacterService characterService = new CharacterService();

    public static void main(String[] args) {
        try {
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            terminalFactory.setInitialTerminalSize(new TerminalSize(150, 30));
            SwingTerminalFrame terminal = terminalFactory.createSwingTerminal();
            terminal.setVisible(true);
            terminal.setResizable(false);

            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();

            MultiWindowTextGUI textGUI = new MultiWindowTextGUI(screen);
            while (true) {
                showMainMenu(textGUI, screen);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showMainMenu(MultiWindowTextGUI gui, Screen screen) {
        BasicWindow window = new BasicWindow("Menu DarkTower");
        window.setHints(List.of(Window.Hint.CENTERED));
        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new EmptySpace()); // espace vide pour l'esthétique
        panel.addComponent(new Label("Oserez-vous gravir les 20 étages de la DarkTower ?"));
        panel.addComponent(new Label("Affrontez le Minotaurus à chaque niveau, gérez vos ressources,"));
        panel.addComponent(new Label("et devenez le maître de la tour dans ce jeu stratégique au tour par tour."));
        panel.addComponent(new EmptySpace());

        panel.addComponent(new Label("===== MENU DarkTower ====="));
        panel.addComponent(new Button("1. S'inscrire", () -> {
            window.close();
            showRegisterMenu(gui);
        }));
        panel.addComponent(new Button("2. Se connecter", () -> {
            window.close();
            showLoginMenu(gui);
        }));
        panel.addComponent(new Button("3. Quitter", () -> {
            try {
                screen.stopScreen();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private static void showRegisterMenu(MultiWindowTextGUI gui) {
        BasicWindow window = new BasicWindow("Inscription");
        window.setHints(List.of(Window.Hint.CENTERED));
        Panel panel = new Panel(new GridLayout(2));

        TextBox usernameBox = new TextBox();
        TextBox passwordBox = new TextBox().setMask('*');

        panel.addComponent(new Label("Nom d'utilisateur :"));
        panel.addComponent(usernameBox);
        panel.addComponent(new Label("Mot de passe :"));
        panel.addComponent(passwordBox);

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("S'inscrire", () -> {
            try {
                String username = usernameBox.getText();
                String password = passwordBox.getText();

                if (username.isEmpty() || password.isEmpty()) {
                    MessageDialog.showMessageDialog(gui, "Erreur", "Veuillez remplir tous les champs.");
                    return;
                }

                String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
                String response = sendRequest("http://localhost:8080/register", "POST", json, null);

                if (response.contains("existe déjà") || response.contains("409")) {
                    MessageDialog.showMessageDialog(gui, "Nom déjà utilisé", "Ce nom d'utilisateur est déjà pris. Choisissez-en un autre.");
                } else if (response.contains("succès")) {
                    MessageDialog.showMessageDialog(gui, "Inscription réussie", response);
                    window.close();
                } else {
                    MessageDialog.showMessageDialog(gui, "Réponse", response);
                }

            } catch (Exception e) {
                MessageDialog.showMessageDialog(gui, "Erreur", "Erreur lors de l'inscription : " + e.getMessage());
            }
        }));

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", window::close));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private static void showLoginMenu(MultiWindowTextGUI gui) {
        BasicWindow window = new BasicWindow("Connexion");
        window.setHints(List.of(Window.Hint.CENTERED));
        Panel panel = new Panel(new GridLayout(2));

        TextBox usernameBox = new TextBox();
        TextBox passwordBox = new TextBox().setMask('*');

        panel.addComponent(new Label("Nom d'utilisateur :"));
        panel.addComponent(usernameBox);
        panel.addComponent(new Label("Mot de passe :"));
        panel.addComponent(passwordBox);

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Connexion", () -> {
            try {
                String json = "{\"username\":\"" + usernameBox.getText() + "\",\"password\":\"" + passwordBox.getText() + "\"}";
                String response = sendRequest("http://localhost:8080/login", "POST", json, null);

                // Si c’est une erreur
                if (response.startsWith("ERROR_403:") || response.startsWith("ERROR_401:")) {
                    String errorJson = response.substring(response.indexOf(":") + 1);
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode errorNode = mapper.readTree(errorJson);
                        String message = errorNode.has("error") ? errorNode.get("error").asText() : "Nom d'utilisateur ou mot de passe incorrect.";
                        MessageDialog.showMessageDialog(gui, "Erreur", message);
                    } catch (Exception ex) {
                        MessageDialog.showMessageDialog(gui, "Erreur", "Nom d'utilisateur ou mot de passe incorrect.");
                    }
                    return;
                }


                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(response);

                JsonNode tokenNode = node.get("token");
                JsonNode usernameNode = node.get("username");
                JsonNode userIdNode = node.get("userId");
                userId = userIdNode.asInt();

                if (tokenNode != null && usernameNode != null && userIdNode != null) {
                    isLoggedIn = true;
                    jwtToken = tokenNode.asText();
                    NomUser = usernameNode.asText();
                    userId = userIdNode.asInt();

                    MessageDialog.showMessageDialog(gui, "Succès", "Bienvenue " + NomUser);
                    window.close();
                    showLoggedInMenu(gui);
                } else {
                    MessageDialog.showMessageDialog(gui, "Erreur", "Réponse inattendue : " + response);
                }

            } catch (Exception e) {
                MessageDialog.showMessageDialog(gui, "Erreur", "Une erreur est survenue : " + e.getMessage());
            }
        }));

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", window::close));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private static void showLoggedInMenu(MultiWindowTextGUI gui) {
        BasicWindow window = new BasicWindow("Connecté");
        window.setHints(List.of(Window.Hint.CENTERED));
        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("Bienvenue dans DarkTower !"));

        panel.addComponent(new Button("Jouer", () -> {
            window.close();
            showCharacterSelection(gui);

        }));
        panel.addComponent(new Button("Déconnexion", () -> {
            window.close();
            isLoggedIn = false;
            jwtToken = null;
            MessageDialog.showMessageDialog(gui, "Info", "Déconnecté avec succès.");
        }));

        panel.addComponent(new Button("Quitter", () -> {
            try {
                window.close();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private static void showCharacterSelection(MultiWindowTextGUI gui) {
        BasicWindow window = new BasicWindow("Sélection du personnage");
        window.setHints(List.of(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("Choisissez un personnage :"));

        panel.addComponent(new Button("Fist Fire", () -> showCharacterDetails(gui, "1", window)));
        panel.addComponent(new Button("Water Wa", () -> showCharacterDetails(gui, "2", window)));
        panel.addComponent(new Button("Jo Wind", () -> showCharacterDetails(gui, "3", window)));
        panel.addComponent(new Button("TWood", () -> showCharacterDetails(gui, "4", window)));

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", window::close));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private static void showCharacterDetails(MultiWindowTextGUI gui, String characterId, BasicWindow currentWindow) {
        Personnage selectedPersonnage = createCharacter(characterId);

        if (selectedPersonnage != null) {
            String message = "Vous avez choisi " + selectedPersonnage.getNom() + "\n- Points de vie : "
                    + selectedPersonnage.getPointsDeVie() + "\n- Points D'Attaque : " + selectedPersonnage.getAttaque();

            String passifsMessage = getPassifsMessage(selectedPersonnage);
            if (!passifsMessage.isEmpty()) {
                message += "\n- Passifs : \n" + passifsMessage;
            }

            Button BtnItem = new Button("suivant", () -> {
                viderInventaire(userId);
                currentWindow.close();
                envoyerChoixPersonnage(characterId, userId, jwtToken);
                afficherEtChoisirItem(gui, currentWindow, selectedPersonnage, () -> {
                    currentWindow.close();
                    showInventaire(gui,selectedPersonnage);
                });
            });

            Panel panel = new Panel(new GridLayout(1));
            panel.addComponent(new Label(message));
            panel.addComponent(new EmptySpace());
            panel.addComponent(BtnItem);
            panel.addComponent(new Button("Retour", () -> {
                currentWindow.close();
                showCharacterSelection(gui);
            }));

            currentWindow.setComponent(panel);
        } else {
            MessageDialog.showMessageDialog(gui, "Erreur", "Personnage non trouvé.");
        }
    }

    public static void showInventaire(MultiWindowTextGUI gui,Personnage personnage) {
        BasicWindow window = new BasicWindow("Gestion de l'inventaire - " + NomUser);
        window.setHints(List.of(Window.Hint.CENTERED));
        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("=== INVENTAIRE ==="));
        panel.addComponent(new EmptySpace());

        List<Item> inventaire = chargerInventaire(userId);
        List<Item> coffre = recupererContenuCoffre(userId);

        if (inventaire.isEmpty()) {
            panel.addComponent(new Label("Aucun item dans l'inventaire."));
        } else {
            panel.addComponent(new Label("--- Items Inventaire ---"));
            for (Item item : inventaire) {
                if (item == null) continue;
                panel.addComponent(new Button(item.getNom(), () -> {
                    afficherOptionsItem(gui, item, true, window,personnage);
                }));
                panel.addComponent(new EmptySpace());
            }
        }

        if (coffre != null && !coffre.isEmpty()) {
            panel.addComponent(new Label("--- Items Coffre ---"));
            for (Item item : coffre) {
                if (item == null) continue;
                panel.addComponent(new Button(item.getNom() + " (Coffre)", () -> {
                    afficherOptionsItem(gui, item, false, window,personnage);
                }));
            }
        }

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("suivant", () -> {
            BasicWindow combatWindow = createCombatWindow(gui, gui.getScreen(), personnage);
            combatWindow.setHints(List.of(Window.Hint.CENTERED));
            window.close(); // ferme la fenêtre d'inventaire actuelle
            gui.addWindowAndWait(combatWindow);
        }));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private static void afficherOptionsItem(MultiWindowTextGUI gui, Item item, boolean depuisInventaire, BasicWindow previousWindow,Personnage personnage) {
        BasicWindow window = new BasicWindow("Actions sur : " + item.getNom());
        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("Que faire avec : " + item.getNom()));

        panel.addComponent(new Button("Supprimer", () -> {
            if (depuisInventaire) {
                supprimerItem(item.getId());
            } else {
                supprimerItemDuCoffre(item.getId(), userId);
            }
            MessageDialog.showMessageDialog(gui, "Suppression", "Item supprimé.");
            window.close();
            previousWindow.close();
            showInventaire(gui,personnage);
        }));

        if (depuisInventaire) {
            boolean hasCoffre = hasCoffreInInventory(userId);
            System.out.println("DEBUG: hasCoffreInInventory(" + userId + ") = " + hasCoffre);

            // N'affiche pas l'option si l'item est un coffre
            if (hasCoffre && !"Coffre".equals(item.getType())) {
                panel.addComponent(new Button("Ajouter au coffre", () -> {
                    boolean success = ajouterItemDansCoffre(item, userId);
                    System.out.println("le bool : " + success);
                    if (success) {
                        supprimerItem(item.getId());
                        MessageDialog.showMessageDialog(gui, "Succès", "Item déplacé dans le coffre.");
                    } else {
                        MessageDialog.showMessageDialog(gui, "Erreur", "Le coffre est plein ou introuvable.");
                    }
                    window.close();
                    previousWindow.close();
                    showInventaire(gui,personnage);
                }));
            }
        }
        else {
            panel.addComponent(new Button("Ajouter à l'inventaire", () -> {
                boolean ok = ajouterItem(userId,item);
                if (ok) {
                    supprimerItemDuCoffre(item.getId(), userId);
                    MessageDialog.showMessageDialog(gui, "Succès", "Item déplacé dans l'inventaire.");
                } else {
                    MessageDialog.showMessageDialog(gui, "Erreur", "Inventaire plein !");
                }
                window.close();
                previousWindow.close();
                showInventaire(gui,personnage);
            }));
        }

        panel.addComponent(new Button("Retour", window::close));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    public static void afficherEtChoisirItem(
            MultiWindowTextGUI gui, BasicWindow parentWindow,
            Personnage selectedPersonnage, Runnable onItemChosen
    ) {
        initialiserInventaire(userId);
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
        choixItemWindow.setHints(List.of(Window.Hint.CENTERED));
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        for (Item item : itemsChoisis) {
            String itemNom = item.getNom();
            Button itemButton = new Button(itemNom, () -> {
                try {
                    boolean plein = ajouterItem(userId,item);
                    if (plein) {
                        MessageDialog.showMessageDialog(gui, "Succès", "L'item " + itemNom + " a été ajouté.");
                    } else {
                        MessageDialog.showMessageDialog(gui, "Raté", "L'inventaire est rempli");
                    }
                } catch (Exception e) {
                    MessageDialog.showMessageDialog(gui, "Erreur", "Ajout de l'item impossible.");
                }

                choixItemWindow.close();
                onItemChosen.run();
            });

            panel.addComponent(itemButton);
        }

        choixItemWindow.setComponent(panel);
        gui.addWindowAndWait(choixItemWindow);
    }

    private static BasicWindow createCombatWindow(MultiWindowTextGUI gui, Screen screen, Personnage joueur) {
        BasicWindow window = new BasicWindow("Combat - DarkTower");
        window.setHints(List.of(Window.Hint.CENTERED));


        Panel mainPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        Panel contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        Panel historyPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        Label lblEtage = new Label("Etage : " + etageActuel.getEtage() + "\n");
        Label lblTour = new Label("Tour : " + tourActuel.getTour());
        Label lblJoueurPV = new Label(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
        Label lblMinotaurePV = new Label(minotaureActuel.getNom() + " PV: " + minotaureActuel.getPointsDeVie());

        contentPanel.addComponent(lblEtage);
        contentPanel.addComponent(lblTour);
        contentPanel.addComponent(lblJoueurPV);
        contentPanel.addComponent(lblMinotaurePV);

        historyPanel.addComponent(new Label("Historique:"));
        historyPanel.addComponent(new Label("Début du combat"));

        Button btnAttaquer = new Button("Attaquer", () -> handleAttack(
                joueur, minotaureActuel, tourActuel, etageActuel,
                lblTour, lblJoueurPV, lblMinotaurePV, lblEtage,
                historyPanel, gui, screen, window, mainPanel));

        Button btnItem = new Button("Utiliser Item", () -> useItem(joueur, minotaureActuel, tourActuel, etageActuel,
                lblTour, lblJoueurPV, lblMinotaurePV, lblEtage,
                historyPanel, gui, screen, window, mainPanel));

        Button btnQuitter = new Button("Abandonner ", () -> {
            try {
                // Ferme d'abord la fenêtre active (combat)
                Window activeWindow = gui.getActiveWindow();
                if (activeWindow != null) {
                    activeWindow.close();
                }

                // Ensuite, ferme toutes les autres fenêtres si jamais il en reste
                for (Window w : gui.getWindows()) {
                    w.close();
                }

                // Recharge le menu connecté
                showLoggedInMenu(gui);

            } catch (Exception e) {
                e.printStackTrace();
                MessageDialog.showMessageDialog(gui, "Erreur", "Une erreur est survenue :\n" + e.toString());
            }
        });

        contentPanel.addComponent(btnAttaquer);
        contentPanel.addComponent(btnItem);
        contentPanel.addComponent(btnQuitter);

        mainPanel.addComponent(contentPanel, LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        mainPanel.addComponent(historyPanel, LinearLayout.createLayoutData(LinearLayout.Alignment.End));

        window.setComponent(mainPanel);
        return window;
    }

    private static void handleAttack(
            Personnage joueur, Minotaurus minotaure, Tour tour, Etage etage,
            Label lblTour, Label lblJoueurPV, Label lblMinotaurePV, Label lblEtage,
            Panel historyPanel, MultiWindowTextGUI gui, Screen screen,
            BasicWindow window, Panel mainPanel) {

        int degatsJoueur = joueur.attaquer(minotaureActuel);
        lblMinotaurePV.setText(minotaureActuel.getNom() + " PV: " + minotaureActuel.getPointsDeVie());
        historyPanel.addComponent(new Label("\nVous avez infligé " + degatsJoueur + " dégats"));
        updateGui(gui);

        if (minotaureActuel.getPointsDeVie() <= 0) {
            showEndCombat(gui, joueur, minotaureActuel, etage, tour,
                    historyPanel, lblTour, lblJoueurPV, lblMinotaurePV, lblEtage,
                    window, mainPanel);
            return;
        }

        try {
            sleep(300);
        } catch (InterruptedException ignored) {}

        int degatsMinotaure = minotaureActuel.attaquer(joueur);
        lblJoueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
        historyPanel.addComponent(new Label("\nLe Minotaure vous a infligé " + degatsMinotaure + " dégats"));

        tour.incrementer();
        lblTour.setText("Tour : " + tour.getTour());

        if (joueur.getPointsDeVie() <= 0 || minotaureActuel.getPointsDeVie() <= 0) {
            showEndCombat(gui, joueur, minotaureActuel, etage, tour,
                    historyPanel, lblTour, lblJoueurPV, lblMinotaurePV, lblEtage,
                    window, mainPanel);
        }
        if (joueur instanceof TWood) {
            ((TWood) joueur).regenererPV();
        }

        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void useItem(
            Personnage joueur, Minotaurus minotaure, Tour tour, Etage etage,
            Label lblTour, Label lblJoueurPV, Label lblMinotaurePV, Label lblEtage,
            Panel historyPanel, MultiWindowTextGUI gui, Screen screen,
            BasicWindow window, Panel mainPanel) {

        BasicWindow itemWindow = new BasicWindow("Utiliser un objet");
        itemWindow.setHints(List.of(Window.Hint.CENTERED));
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        List<Item> inventaire = chargerInventaire(userId);

        if (inventaire.isEmpty()) {
            MessageDialog.showMessageDialog(gui, "Inventaire vide", "Vous n'avez aucun objet à utiliser.");
            return;
        }

        for (Item item : inventaire) {
            String nomItem = item.getNom();

            Button itemButton = new Button(nomItem, () -> {
                try {
                    if (item instanceof Potion || item instanceof Weapon) {
                        // Appel API pour utiliser potion ou arme
                        UseItemResult resultat = callUseItemAPI(joueur.getId(),userId ,item.getId(), getMinotaurusActuel().getId());

                        // Mise à jour des PV joueur et minotaure
                        int pvRecuperer = resultat.pointsDeVieRendues() + joueur.getPointsDeVie();
                        int degatsInfligeMinotaure = minotaureActuel.getPointsDeVie() - resultat.degatsInfliges();

                        joueur.setPointsDeVie(pvRecuperer);
                        minotaureActuel.setPointsDeVie(degatsInfligeMinotaure);

                        // Mise à jour des labels
                        lblJoueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
                        lblMinotaurePV.setText(minotaureActuel.getNom() + " PV: " + minotaureActuel.getPointsDeVie());

                        try { Thread.sleep(300); } catch (InterruptedException ignored) {}

                        if (minotaureActuel.getPointsDeVie() <= 0) {
                            showEndCombat(gui, joueur, minotaureActuel, etage, tour,
                                    historyPanel, lblTour, lblJoueurPV, lblMinotaurePV, lblEtage,
                                    window, mainPanel);
                        }

                        int degatsMinotaure = minotaureActuel.attaquer(joueur);
                        lblJoueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
                        historyPanel.addComponent(new Label("\nLe Minotaure vous a infligé " + degatsMinotaure + " dégâts"));

                        tour.incrementer();
                        lblTour.setText("Tour : " + tour.getTour());

                        if (joueur.getPointsDeVie() <= 0 || minotaureActuel.getPointsDeVie() <= 0) {
                            showEndCombat(gui, joueur, minotaureActuel, etage, tour,
                                    historyPanel, lblTour, lblJoueurPV, lblMinotaurePV, lblEtage,
                                    window, mainPanel);
                        }

                        if (joueur instanceof TWood) {
                            int pvjoueur = joueur.getPointsDeVie() + 10;
                            joueur.setPointsDeVie(pvjoueur);
                            lblJoueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
                        }

                        historyPanel.addComponent(new Label("\n" + resultat.message()));
                        updateGui(gui);

                        itemWindow.close();

                    } else if (item instanceof Coffre) {
                        // Gestion du coffre : afficher le contenu et créer un bouton par item du coffre
                        List<Item> contenuCoffre = recupererContenuCoffre(userId);

                        if (contenuCoffre.isEmpty()) {
                            MessageDialog.showMessageDialog(gui, "Coffre vide", "Le coffre est vide.");
                            return;
                        }

                        // Nouvelle fenêtre pour afficher contenu du coffre
                        BasicWindow coffreWindow = new BasicWindow("Contenu du coffre");
                        coffreWindow.setHints(List.of(Window.Hint.CENTERED));
                        Panel coffrePanel = new Panel(new LinearLayout(Direction.VERTICAL));

                        for (Item i : contenuCoffre) {
                            String nomItemCoffre = i.getNom();

                            Button itemButtonCoffre = new Button(nomItemCoffre, () -> {
                                try {
                                    UseItemResult resultatCoffre = null;

                                    if (i instanceof Potion || i instanceof Weapon) {
                                        resultatCoffre = callUseItemAPI(joueur.getId(),userId ,i.getId(), getMinotaurusActuel().getId());

                                        // Mise à jour des PV joueur et minotaure
                                        int pvRecup = resultatCoffre.pointsDeVieRendues() + joueur.getPointsDeVie();
                                        int degatsMin = minotaureActuel.getPointsDeVie() - resultatCoffre.degatsInfliges();

                                        joueur.setPointsDeVie(pvRecup);
                                        minotaureActuel.setPointsDeVie(degatsMin);

                                        // Mise à jour des labels
                                        lblJoueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
                                        lblMinotaurePV.setText(minotaureActuel.getNom() + " PV: " + minotaureActuel.getPointsDeVie());

                                        try { Thread.sleep(300); } catch (InterruptedException ignored) {}

                                        if (minotaureActuel.getPointsDeVie() <= 0) {
                                            showEndCombat(gui, joueur, minotaureActuel, etage, tour,
                                                    historyPanel, lblTour, lblJoueurPV, lblMinotaurePV, lblEtage,
                                                    window, mainPanel);
                                        }

                                        int degatsMinotaure = minotaureActuel.attaquer(joueur);
                                        lblJoueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
                                        historyPanel.addComponent(new Label("\nLe Minotaure vous a infligé " + degatsMinotaure + " dégâts"));

                                        tour.incrementer();
                                        lblTour.setText("Tour : " + tour.getTour());

                                        if (joueur.getPointsDeVie() <= 0 || minotaureActuel.getPointsDeVie() <= 0) {
                                            showEndCombat(gui, joueur, minotaureActuel, etage, tour,
                                                    historyPanel, lblTour, lblJoueurPV, lblMinotaurePV, lblEtage,
                                                    window, mainPanel);
                                        }

                                        if (joueur instanceof TWood) {
                                            int pvjoueur = joueur.getPointsDeVie() + 10;
                                            joueur.setPointsDeVie(pvjoueur);
                                            lblJoueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
                                        }

                                        historyPanel.addComponent(new Label("\n" + resultatCoffre.message()));
                                        updateGui(gui);

                                        coffreWindow.close();
                                        itemWindow.close();

                                    } else {
                                        MessageDialog.showMessageDialog(gui, "Erreur", "Cet item ne peut pas être utilisé.");
                                    }

                                } catch (Exception e) {
                                    System.err.println("Erreur lors de l'utilisation de l'item du coffre : " + e.getMessage());
                                    MessageDialog.showMessageDialog(gui, "Erreur", "Erreur lors de l'utilisation de l'objet du coffre : " + e.getMessage());
                                }
                            });

                            coffrePanel.addComponent(itemButtonCoffre);
                        }

                        coffrePanel.addComponent(new Button("Retour", coffreWindow::close));

                        coffreWindow.setComponent(coffrePanel);
                        gui.addWindowAndWait(coffreWindow);
                    } else {
                        MessageDialog.showMessageDialog(gui, "Erreur", "Type d'item non supporté.");
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'utilisation de l'item : " + e.getMessage());
                    MessageDialog.showMessageDialog(gui, "Erreur", "Une erreur est survenue lors de l'utilisation de l'objet : " + e.getMessage());
                }
            });

            panel.addComponent(itemButton);
        }

        panel.addComponent(new Button("Retour", () -> {
            itemWindow.close();
            window.setComponent(mainPanel);
        }));

        itemWindow.setComponent(panel);
        gui.addWindowAndWait(itemWindow);
    }


    private static void showEndCombat(
            MultiWindowTextGUI gui, Personnage joueur, Minotaurus minotaure,
            Etage etage, Tour tour, Panel historyPanel,
            Label lblTour, Label lblJoueurPV, Label lblMinotaurePV, Label lblEtage,
            BasicWindow window, Panel mainPanel) {

        String finCombatMsg = (joueur.getPointsDeVie() <= 0)
                ? "Vous avez perdu contre " + minotaureActuel.getNom() + " !"
                : "Félicitations ! Vous avez vaincu l'étage N°" + etage.getEtage();

        Panel endPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        endPanel.addComponent(new Label(finCombatMsg));

        if (joueur.getPointsDeVie() <= 0) {
            endPanel.addComponent(new Button("Recommencer", () -> {
                restartCombat(joueur, minotaureActuel, etage, tour, historyPanel, lblTour, lblJoueurPV, lblMinotaurePV);
                window.setComponent(mainPanel);
            }));
        } else {
            endPanel.addComponent(new Button("Suivant", () -> {
                if (etage.getEtage() < 20) {
                    etage.incrementer();
                    lblEtage.setText("Etage : " + etage.getEtage());
                    minotaureActuel = new Minotaurus("999", etage.getEtage());
                    tour.resetTour();
                    lblTour.setText("Tour : " + tour.getTour());
                    lblMinotaurePV.setText(minotaureActuel.getNom() + " PV: " + minotaureActuel.getPointsDeVie());
                    historyPanel.removeAllComponents();
                    historyPanel.addComponent(new Label("Début du combat"));
                    updateGui(gui);

                    afficherEtChoisirItem(gui, window, joueur, () -> {
                        lblEtage.setText("Etage : " + etage.getEtage());
                        minotaureActuel = new Minotaurus("999", etage.getEtage());
                        lblMinotaurePV.setText(minotaureActuel.getNom() + " PV: " + minotaureActuel.getPointsDeVie());

                        tour.resetTour();
                        lblTour.setText("Tour : " + tour.getTour());

                        historyPanel.removeAllComponents();
                        historyPanel.addComponent(new Label("Début du combat"));

                        updateGui(gui);

                        window.setComponent(mainPanel);
                        showInventaire(gui,joueur);
                    });

                } else {
                    MessageDialog.showMessageDialog(gui, "Fin", "Vous avez vaincu tous les étages !");
                    window.close();
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

        etageActuel = new Etage(1);
        minotaureActuel = new Minotaurus("999", etage.getEtage()); // Réinitialise les PV du Minotaure
        joueur.resetPointDeVie(); // Réinitialise les PV du joueur
        tour.resetTour();

        lblTour.setText("Tour : " + tour.getTour());
        lblJoueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
        lblMinotaurePV.setText(minotaureActuel.getNom() + " PV: " + minotaureActuel.getPointsDeVie());
        viderInventaire(userId);
    }

    private static void updateGui(MultiWindowTextGUI gui) {
        try {
            gui.updateScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void viderInventaire(int idPersonnage) {
        try {
            sendRequest("http://localhost:8080/inventaire/" + idPersonnage + "/vider", "POST", null, jwtToken);
        } catch (Exception e) {
            System.err.println("Erreur lors de la vidange de l'inventaire : " + e.getMessage());
        }
    }

    private static void initialiserInventaire(int idPersonnage) {
        try {
            sendRequest("http://localhost:8080/inventaire/" + idPersonnage + "/initialiser", "POST", null, jwtToken);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de l'inventaire : " + e.getMessage());
        }
    }

    private static boolean ajouterItem(int idPersonnage, Item item) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(item);
            String response = sendRequest("http://localhost:8080/inventaire/" + idPersonnage + "/ajouter", "POST", json, jwtToken);
            return response.contains("succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout de l'item : " + e.getMessage());
            return false;
        }
    }

    private static List<Item> chargerInventaire(int idPersonnage) {
        try {
            String response = sendRequest("http://localhost:8080/inventaire/" + idPersonnage + "/charger", "GET", null, jwtToken);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, new TypeReference<List<Item>>() {});
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'inventaire : " + e.getMessage());
            return new ArrayList<>();
        }
    }
    private static List<Item> recupererContenuCoffre(int idPersonnage) {
        try {
            String url = "http://localhost:8080/inventaire/" + idPersonnage + "/coffre";
            String response = sendRequest(url, "GET", null, jwtToken);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, new TypeReference<List<Item>>() {});
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du contenu du coffre : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static boolean ajouterItemDansCoffre(Item item, int idPersonnage) {
        try {
            String url = "http://localhost:8080/inventaire/" + idPersonnage + "/coffre/ajouter";
            ObjectMapper mapper = new ObjectMapper();
            String body = mapper.writeValueAsString(item);
            String response = sendRequest(url, "POST", body, jwtToken);
            System.out.println("Response API: " + response);

            return Boolean.parseBoolean(response);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout de l'item dans le coffre : " + e.getMessage());
            return false;
        }
    }



    private static boolean supprimerItemDuCoffre(String itemId, int idPersonnage) {
        try {
            String url = "http://localhost:8080/inventaire/" + idPersonnage + "/coffre/" + itemId;
            String response = sendRequest(url, "DELETE", null, jwtToken);
            return response.contains("succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de l'item du coffre : " + e.getMessage());
            return false;
        }
    }

    private static boolean supprimerItem(String itemId) {
        try {
            String url = "http://localhost:8080/inventaire/item/" + itemId;
            String response = sendRequest(url, "DELETE", null, jwtToken);
            return response.contains("succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de l'item : " + e.getMessage());
            return false;
        }
    }

    private static boolean hasCoffreInInventory(int idPersonnage) {
        try {
            String url = "http://localhost:8080/inventaire/coffre/existe/" + idPersonnage;
            System.out.println("JWT Token utilisé : " + jwtToken);
            String response = sendRequest(url, "GET", null, jwtToken);
            return Boolean.parseBoolean(response);
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de la présence d'un coffre : " + e.getMessage());
            return false;
        }
    }


    private static String sendRequest(String urlString, String method, String body, String token) throws Exception {
        URI uri = new URI(urlString); // Remplacer String par URI
        URL url = uri.toURL(); // Conversion en URL
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", "application/json");

        if (token != null) {
            con.setRequestProperty("Authorization", "Bearer " + token);
        }

        // Ne mettre doOutput à true QUE si on a un corps à envoyer (POST, PUT...)
        if (body != null && !body.isEmpty()) {
            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = body.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }

        InputStream inputStream;
        int responseCode = con.getResponseCode();
        if (responseCode >= 400) {
            inputStream = con.getErrorStream();
        } else {
            inputStream = con.getInputStream();
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {

            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (responseCode >= 400) {
            return "ERROR_" + responseCode + ":" + response.toString();
        }

        return response.toString();
    }

    private static Personnage createCharacter(String characterId) {
        switch (characterId) {
            case "1":
                return new FistFire("1");
            case "2":
                return new WaterWa("2");
            case "3":
                return new JoWind("3");
            case "4":
                return new TWood("4");
            default:
                return null; // Si le personnage n'est pas trouvé
        }
    }

    public static UseItemResult callUseItemAPI(String idPersonnage, int idUser, String itemId, String cibleId) {
        try {
            String encodedIdPersonnage = URLEncoder.encode(idPersonnage, StandardCharsets.UTF_8);
            String urlString = "http://localhost:8080/Combat/" + idUser + "/" + encodedIdPersonnage + "/use-item";

            JSONObject requestBody = new JSONObject();
            requestBody.put("itemId", itemId);
            if (cibleId != null) {
                requestBody.put("cibleId", cibleId);
            }

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + jwtToken)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();

            if (status == 200) {
                String responseBody = response.body();
                System.out.println("Réponse de l'API : " + responseBody);

                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(responseBody, UseItemResult.class);
            } else {
                System.err.println("Erreur API: " + status);
                System.err.println("Détails de l'erreur : " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new UseItemResult("Erreur lors de l'utilisation de l'objet.", 0, 0, null);
    }


    public static void envoyerChoixPersonnage(String characterId, int userId, String jwtToken) {
        try {
            URL url = new URL("http://localhost:8080/characters/select");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);

            String json = String.format("{\"characterId\":\"%s\", \"userId\":\"%s\"}", characterId, userId);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Personnage sélectionné avec succès.");
            } else {
                System.err.println("Erreur lors de la sélection du personnage.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getPassifsMessage(Personnage personnage) {
        StringBuilder passifsMessage = new StringBuilder();

        if (personnage instanceof FistFire) {
            passifsMessage.append("- Chance de coup critique de 40%\n");
        }
        if (personnage instanceof JoWind) {
            passifsMessage.append("- Passif: 30% de chance d'esquiver l'attaque\n");
        }
        if (personnage instanceof WaterWa) {
            passifsMessage.append("- Passif: Diminution des dégats subis par 2\n");
        }
        if (personnage instanceof TWood) {
            passifsMessage.append("-Passif: Regénére 10 PV après chaque tour");
        }

        return passifsMessage.toString();
    }

    public static Minotaurus getMinotaurusActuel() {

        return minotaureActuel;
    }

    public static void setMinotaurusActuel(Minotaurus minotaurus) {
        minotaureActuel = minotaurus;
    }
}
