package be.helha.projets.projetdarktower;

import be.helha.projets.projetdarktower.DTO.UseItemResult;
import be.helha.projets.projetdarktower.Inventaire.InventaireDAOImpl;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Item.ItemFactory;
import be.helha.projets.projetdarktower.Item.Potion;
import be.helha.projets.projetdarktower.Item.Weapon;
import be.helha.projets.projetdarktower.Item.Coffre;
import be.helha.projets.projetdarktower.Model.*;
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
import org.bson.codecs.pojo.TypeWithTypeParameters;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class LanternaCombat {

    private static String jwtToken = null;
    private static boolean isLoggedIn = false;
    private static int userId;
    public static Etage etageActuel = new Etage(1);
    public static Tour tourActuel = new Tour(1);

    private static final CharacterService characterService = new CharacterService();
    public static InventaireDAOImpl inventaireDAO = new InventaireDAOImpl();


    public static void main(String[] args) {
        try {
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            terminalFactory.setInitialTerminalSize(new TerminalSize(150, 30));
            SwingTerminalFrame terminal = terminalFactory.createSwingTerminal();
            terminal.setVisible(true);
            terminal.setResizable(false);

            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();

            MultiWindowTextGUI   textGUI = new MultiWindowTextGUI(screen);
            while (true) {
                showMainMenu(textGUI, screen);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void showMainMenu(MultiWindowTextGUI gui, Screen screen) {
        BasicWindow window = new BasicWindow("Menu DarkTower");
        Panel panel = new Panel(new GridLayout(1));
        String introText = "\nDarkTower, le jeu où franchir les étages d'une tour semble impossible. "
                + "\nChaque défi devient plus complexe, rendant l'ascension inaccessibile. "
                + "\nLa tour est un piège où l'échec est inévitable.";
        panel.addComponent(new Label(introText));
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

                // 🔍 Affiche la réponse brute dans la console
                System.out.println("Réponse brute : " + response);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(response);

                JsonNode tokenNode = node.get("token");
                JsonNode usernameNode = node.get("username");
                JsonNode userIdNode = node.get("userId");  // ← bonne clé
                userId = userIdNode.asInt();

                if (tokenNode != null && usernameNode != null && userIdNode != null) {
                    isLoggedIn = true;
                    jwtToken = tokenNode.asText();

                    MessageDialog.showMessageDialog(gui, "Succès",
                            "Bienvenue " + usernameNode.asText() + " (ID: " + usernameNode.asText() + ")");
                    window.close();
                    showLoggedInMenu(gui);
                }
                else if (node.isTextual()) {
                    MessageDialog.showMessageDialog(gui, "Erreur", node.asText());
                } else {
                    MessageDialog.showMessageDialog(gui, "Erreur", "Réponse inattendue : " + response);
                }

            } catch (Exception e) {
                MessageDialog.showMessageDialog(gui, "Erreur", e.getMessage());
            }
        }));



        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", window::close));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private static void showLoggedInMenu(MultiWindowTextGUI gui) {
        BasicWindow window = new BasicWindow("Connecté");
        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("Bienvenue dans DarkTower !"));


        panel.addComponent(new Button("Choisir Personnage", () ->{
            showCharacterSelection(gui);
            window.close();
        }));
        panel.addComponent(new Button("Déconnexion", () -> {
            isLoggedIn = false;
            jwtToken = null;
            MessageDialog.showMessageDialog(gui, "Info", "Déconnecté avec succès.");
            window.close();
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

        // Boutons pour chaque personnage
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
        // Instancier le personnage choisi en fonction de l'ID
        Personnage selectedPersonnage = createCharacter(characterId);

        if (selectedPersonnage != null) {
            // Créer le message de base avec les détails du personnage
            String message = "Vous avez choisi " + selectedPersonnage.getNom() + "\n- Points de vie : "
                    + selectedPersonnage.getPointsDeVie() + "\n- Points D'Attaque : " + selectedPersonnage.getAttaque();

            // Ajouter les passifs, s'il y en a
            String passifsMessage = getPassifsMessage(selectedPersonnage);
            if (!passifsMessage.isEmpty()) {
                message += "\n- Passifs : \n" + passifsMessage;
            }

            // Créer le bouton "Suivant"
            Button BtnItem = new Button("suivant", () -> {
                currentWindow.close();
                envoyerChoixPersonnage(characterId, userId,jwtToken);
                afficherEtChoisirItem(gui, currentWindow, selectedPersonnage, () -> {
                    BasicWindow combatWindow = createCombatWindow(gui, gui.getScreen(), selectedPersonnage);
                    combatWindow.setHints(List.of(Window.Hint.CENTERED));
                    gui.addWindowAndWait(combatWindow);
                });
            });

            // Ajoute le bouton à l'interface
            Panel panel = new Panel(new GridLayout(1));
            panel.addComponent(new Label(message));
            panel.addComponent(new EmptySpace());
            panel.addComponent(BtnItem);
            panel.addComponent(new Button("Retour", () -> {
                currentWindow.close(); // Ferme la fenêtre actuelle
                showCharacterSelection(gui); // Affiche la fenêtre de sélection à nouveau
            }));

            currentWindow.setComponent(panel);
        } else {
            MessageDialog.showMessageDialog(gui, "Erreur", "Personnage non trouvé.");
        }
    }


    //METHODE POUR LES RECOMPENSES D IETM
    public static void afficherEtChoisirItem(
            MultiWindowTextGUI gui, BasicWindow parentWindow,
            Personnage selectedPersonnage, Runnable onItemChosen
    ) {
        inventaireDAO.initialiserInventaireVide(userId);
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
                    boolean plein = inventaireDAO.ajouterItem(item, userId);
                    if (plein) {
                        MessageDialog.showMessageDialog(gui, "Succès", "L'item " + itemNom + " a été ajouté.");
                    }
                    else {
                        MessageDialog.showMessageDialog(gui, "Raté", "L'inventaire est rempli");
                    }
                } catch (Exception e) {
                    MessageDialog.showMessageDialog(gui, "Erreur", "Ajout de l'item impossible.");
                }

                choixItemWindow.close();
                onItemChosen.run(); // Action passée (mise à jour + retour combat)
            });

            panel.addComponent(itemButton);
        }

        choixItemWindow.setComponent(panel);
        gui.addWindowAndWait(choixItemWindow);
    }




// PARTIE COMBAT

    //METHODE QUI PERMET LA FENETRE DE COMBAT
    private static BasicWindow createCombatWindow(MultiWindowTextGUI gui, Screen screen, Personnage joueur) {
        BasicWindow window = new BasicWindow("Combat - DarkTower");
        window.setHints(List.of(Window.Hint.CENTERED));


        Minotaurus minotaure = new Minotaurus("999", etageActuel.getEtage());
        // Création des objets dans le stock

        Panel mainPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        Panel contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        Panel historyPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        Label lblEtage = new Label("Etage : " + etageActuel.getEtage() +"\n");
        Label lblTour = new Label("Tour : " + tourActuel.getTour());
        Label lblJoueurPV = new Label(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
        Label lblMinotaurePV = new Label(minotaure.getNom() + " PV: " + minotaure.getPointsDeVie());

        contentPanel.addComponent(lblEtage);
        contentPanel.addComponent(lblTour);
        contentPanel.addComponent(lblJoueurPV);
        contentPanel.addComponent(lblMinotaurePV);

        historyPanel.addComponent(new Label("Historique:"));
        historyPanel.addComponent(new Label("Début du combat"));

        Button btnAttaquer = new Button("Attaquer", () -> handleAttack(
                joueur, minotaure, tourActuel, etageActuel,
                lblTour, lblJoueurPV, lblMinotaurePV,lblEtage,
                historyPanel, gui, screen, window, mainPanel));
        Button btnItem = new Button("Utiser Item", () -> useItem(joueur, minotaure, tourActuel, etageActuel,
                lblTour, lblJoueurPV, lblMinotaurePV,lblEtage,
                historyPanel, gui, screen, window, mainPanel));

        Button btnQuitter = new Button("Quitter", window::close);

        contentPanel.addComponent(btnAttaquer);
        contentPanel.addComponent(btnItem);
        contentPanel.addComponent(btnQuitter);

        mainPanel.addComponent(contentPanel, LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        mainPanel.addComponent(historyPanel, LinearLayout.createLayoutData(LinearLayout.Alignment.End));

        window.setComponent(mainPanel);
        return window;
    }

    //METHODE STATIQUE POUR GERER L ATTAQUE
    private static void handleAttack(
            Personnage joueur, Minotaurus minotaure, Tour tour, Etage etage,
            Label lblTour, Label lblJoueurPV, Label lblMinotaurePV,Label lblEtage,
            Panel historyPanel, MultiWindowTextGUI gui, Screen screen,
            BasicWindow window, Panel mainPanel) {



        int degatsJoueur = joueur.attaquer(minotaure);
        lblMinotaurePV.setText(minotaure.getNom() + " PV: " + minotaure.getPointsDeVie());
        historyPanel.addComponent(new Label("\nVous avez infligé " + degatsJoueur + " dégats"));
        updateGui(gui);



        if (minotaure.getPointsDeVie() <= 0) {
            showEndCombat(gui, joueur, minotaure, etage, tour,
                    historyPanel, lblTour, lblJoueurPV, lblMinotaurePV,lblEtage,
                    window, mainPanel);
            return;
        }
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}

        int degatsMinotaure = minotaure.attaquer(joueur);
        lblJoueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
        historyPanel.addComponent(new Label("\nLe Minotaure vous a infligé " + degatsMinotaure + " dégats"));

        tour.incrementer();
        lblTour.setText("Tour : " + tour.getTour());



        if (joueur.getPointsDeVie() <= 0 || minotaure.getPointsDeVie() <= 0) {
            showEndCombat(gui, joueur, minotaure, etage, tour,
                    historyPanel, lblTour, lblJoueurPV, lblMinotaurePV,lblEtage,
                    window, mainPanel);
        }
        if (joueur instanceof TWood){
            ((TWood) joueur).regenererPV();
        }

        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //METHODE STATIQUE POUR GERER L UTILISATION ITEM
    private static void useItem(
            Personnage joueur, Minotaurus minotaure, Tour tour, Etage etage,
            Label lblTour, Label lblJoueurPV, Label lblMinotaurePV, Label lblEtage,
            Panel historyPanel, MultiWindowTextGUI gui, Screen screen,
            BasicWindow window, Panel mainPanel
    ) {
        BasicWindow itemWindow = new BasicWindow("Utiliser un objet");
        itemWindow.setHints(List.of(Window.Hint.CENTERED));
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        // Récupération de l'inventaire du joueur
        List<Item> inventaire = inventaireDAO.chargerInventaire(userId);

        if (inventaire.isEmpty()) {
            MessageDialog.showMessageDialog(gui, "Inventaire vide", "Vous n'avez aucun objet à utiliser.");
            return;
        }

        for (Item item : inventaire) {
            String nomItem = item.getNom();

            Button itemButton = new Button(nomItem, () -> {
                UseItemResult resultat = callUseItemAPI(joueur.getId(), item.getId(), minotaure.getId());

                // Mise à jour des points de vie dans les objets
                joueur.setPointsDeVie(resultat.pvUtilisateur());
                minotaure.setPointsDeVie(resultat.pvCible());

                // Mise à jour de l'historique avec le message retourné
                historyPanel.addComponent(new Label("\n" + resultat.message()));

                // Mise à jour des labels avec les PV mis à jour
                lblJoueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
                lblMinotaurePV.setText(minotaure.getNom() + " PV: " + minotaure.getPointsDeVie());
                updateGui(gui);

                // Vérification de la fin du combat
                if (joueur.getPointsDeVie() <= 0 || minotaure.getPointsDeVie() <= 0) {
                    itemWindow.close();
                    showEndCombat(gui, joueur, minotaure, etage, tour,
                            historyPanel, lblTour, lblJoueurPV, lblMinotaurePV, lblEtage,
                            window, mainPanel);
                    return;
                }

                // Tour du Minotaure
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                int degatsMinotaure = minotaure.attaquer(joueur);
                lblJoueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
                historyPanel.addComponent(new Label("\nLe Minotaure vous a infligé " + degatsMinotaure + " dégâts"));
                tour.incrementer();
                lblTour.setText("Tour : " + tour.getTour());

                if (joueur.getPointsDeVie() <= 0 || minotaure.getPointsDeVie() <= 0) {
                    itemWindow.close();
                    showEndCombat(gui, joueur, minotaure, etage, tour,
                            historyPanel, lblTour, lblJoueurPV, lblMinotaurePV, lblEtage,
                            window, mainPanel);
                }

                itemWindow.close();
            });

            panel.addComponent(itemButton);
        }

        itemWindow.setComponent(panel);
        gui.addWindowAndWait(itemWindow);
    }


    //METHODE STATIQUE POUR AFFICHER LA FIN DU COMBAT
    private static void showEndCombat(
            MultiWindowTextGUI gui, Personnage joueur, Minotaurus minotaure,
            Etage etage, Tour tour, Panel historyPanel,
            Label lblTour, Label lblJoueurPV, Label lblMinotaurePV, Label lblEtage,
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
                    // Préparation étage suivant
                    etage.incrementer();
                    lblEtage.setText("Etage : " + etage.getEtage());
                    minotaure.setNiveau(etage.getEtage());
                    minotaure.resetPointsDeVie();
                    tour.resetTour();
                    lblTour.setText("Tour : " + tour.getTour());
                    lblMinotaurePV.setText(minotaure.getNom() + " PV: " + minotaure.getPointsDeVie());
                    historyPanel.removeAllComponents();
                    historyPanel.addComponent(new Label("Début du combat"));
                    updateGui(gui);

                    // Choix de l'item puis combat
                    afficherEtChoisirItem(gui, window, joueur, () -> {
                        // Mise à jour de l'état
                        lblEtage.setText("Etage : " + etage.getEtage());
                        minotaure.setNiveau(etage.getEtage());
                        minotaure.resetPointsDeVie();
                        lblMinotaurePV.setText(minotaure.getNom() + " PV: " + minotaure.getPointsDeVie());

                        tour.resetTour();
                        lblTour.setText("Tour : " + tour.getTour());

                        historyPanel.removeAllComponents();
                        historyPanel.addComponent(new Label("Début du combat"));

                        updateGui(gui);

                        // Revenir sur le mainPanel
                        window.setComponent(mainPanel);
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

    //REDEMARRER LE COMBAT
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

    //CLASSE CREER EN STATIC


    private static String sendRequest(String urlString, String method, String body, String token) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", "application/json");

        if (token != null) {
            con.setRequestProperty("Authorization", "Bearer " + token);
        }

        con.setDoOutput(true);
        if (body != null) {
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = body.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }

        InputStream inputStream;
        if (con.getResponseCode() >= 400) {
            inputStream = con.getErrorStream();
        } else {
            inputStream = con.getInputStream();
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    public static void envoyerChoixPersonnage(String characterId, int userId, String jwtToken) {
        try {
            URL url = new URL("http://localhost:8080/characters/select");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);  // <- ici !

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

        // Exemple de passifs : si le personnage a des passifs, les ajouter à la chaîne
        if (personnage instanceof FistFire) {
            passifsMessage.append("- Chance de coup critique de 40%\n");
        }
        if (personnage instanceof JoWind) {
            passifsMessage.append("- Passif: 30% de chance d'esquiver l'attaque\n");
        }
        if (personnage instanceof WaterWa) {
            passifsMessage.append("- Passif: Diminution des dégats subis par 2\n");
        }
        if (personnage instanceof TWood){
            passifsMessage.append("-Passif: Regénére 10 PV après chaque tour");
        }

        // Ajouter d'autres passifs selon le type de personnage
        // Tu peux aussi avoir un attribut "passifs" dans la classe Personnage pour une gestion plus dynamique

        return passifsMessage.toString();
    }

    private static UseItemResult callUseItemAPI(String joueurId, String itemId, String cibleId) {
        try {
            // URL de l'API
            String urlString = "http://localhost:8080/Combat/" + joueurId + "/use-item";
            URL url = new URL(urlString);

            // Connexion HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + jwtToken);
            connection.setDoOutput(true);

            // Corps de la requête
            JSONObject requestBody = new JSONObject();
            requestBody.put("itemId", itemId);
            if (cibleId != null) {
                requestBody.put("cibleId", cibleId);
            }

            // Envoi de la requête
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Lecture de la réponse
            int status = connection.getResponseCode();
            if (status == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    // Conversion de la réponse JSON en UseItemResult
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(response.toString(), UseItemResult.class);
                }
            } else {
                System.err.println("Erreur API: " + status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retourne un résultat par défaut en cas d'erreur
        return new UseItemResult("Erreur lors de l'utilisation de l'objet.", 0, 0, null);
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

}
