package be.helha.projets.projetdarktower;

import be.helha.projets.projetdarktower.DTO.UseItemResult;
import be.helha.projets.projetdarktower.Inventaire.InventaireDAOImpl;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Item.ItemFactory;
import be.helha.projets.projetdarktower.Item.Potion;
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
import java.net.URLEncoder;
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
        Panel panel = new Panel(new GridLayout(1));

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

                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(response);

                JsonNode tokenNode = node.get("token");
                JsonNode usernameNode = node.get("username");
                JsonNode userIdNode = node.get("userId");
                userId = userIdNode.asInt();

                if (tokenNode != null && usernameNode != null && userIdNode != null) {
                    isLoggedIn = true;
                    jwtToken = tokenNode.asText();

                    MessageDialog.showMessageDialog(gui, "Succès",
                            "Bienvenue " + usernameNode.asText() + " (ID: " + usernameNode.asText() + ")");
                    window.close();
                    showLoggedInMenu(gui);
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

        panel.addComponent(new Button("Choisir Personnage", () -> {
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
                    BasicWindow combatWindow = createCombatWindow(gui, gui.getScreen(), selectedPersonnage);
                    combatWindow.setHints(List.of(Window.Hint.CENTERED));
                    gui.addWindowAndWait(combatWindow);
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

        Button btnQuitter = new Button("Quitter", window::close);

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

            UseItemResult[] resultat = new UseItemResult[1];

            Button itemButton = new Button(nomItem, () -> {
                try {
                    if (item instanceof Potion) {
                        resultat[0] = callUseItemAPI(joueur.getId(), item.getId(), getMinotaurusActuel().getId());
                    } else {
                        resultat[0] = callUseItemAPI(joueur.getId(), item.getId(), getMinotaurusActuel().getId());
                    }

                    // Mise à jour des points de vie dans les objets
                    System.out.println("PV joueur avant : " + joueur.getPointsDeVie());
                    System.out.println("PV minotaure avant : " + minotaureActuel.getPointsDeVie());

// Mise à jour des PV
                    joueur.setPointsDeVie(resultat[0].pvUtilisateur());
                    minotaureActuel.setPointsDeVie(resultat[0].pvCible());

                    System.out.println("PV joueur après : " + joueur.getPointsDeVie());
                    System.out.println("PV minotaure après : " + minotaureActuel.getPointsDeVie());


                    // Mise à jour des labels avec les PV mis à jour
                    lblJoueurPV.setText(joueur.getNom() + " PV: " + joueur.getPointsDeVie());
                    lblMinotaurePV.setText(minotaureActuel.getNom() + " PV: " + minotaureActuel.getPointsDeVie());

                    try{sleep(300);}catch (InterruptedException ignored) {}

                    if (joueur.getPointsDeVie() <= 0 || minotaureActuel.getPointsDeVie() <= 0) {
                        showEndCombat(gui, joueur, minotaureActuel, etage, tour,
                                historyPanel, lblTour, lblJoueurPV, lblMinotaurePV, lblEtage,
                                window, mainPanel);
                    }

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

                    historyPanel.addComponent(new Label("\n" + resultat[0].message()));
                    updateGui(gui);

                    itemWindow.close();
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
                    minotaureActuel.setNiveau(etage.getEtage());
                    minotaureActuel.resetPointsDeVie();
                    tour.resetTour();
                    lblTour.setText("Tour : " + tour.getTour());
                    lblMinotaurePV.setText(minotaureActuel.getNom() + " PV: " + minotaureActuel.getPointsDeVie());
                    historyPanel.removeAllComponents();
                    historyPanel.addComponent(new Label("Début du combat"));
                    updateGui(gui);

                    afficherEtChoisirItem(gui, window, joueur, () -> {
                        lblEtage.setText("Etage : " + etage.getEtage());
                        minotaureActuel.setNiveau(etage.getEtage());
                        minotaureActuel.resetPointsDeVie();
                        lblMinotaurePV.setText(minotaureActuel.getNom() + " PV: " + minotaureActuel.getPointsDeVie());

                        tour.resetTour();
                        lblTour.setText("Tour : " + tour.getTour());

                        historyPanel.removeAllComponents();
                        historyPanel.addComponent(new Label("Début du combat"));

                        updateGui(gui);

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


    private static void restartCombat(
            Personnage joueur, Minotaurus minotaure, Etage etage, Tour tour,
            Panel historyPanel, Label lblTour, Label lblJoueurPV, Label lblMinotaurePV) {

        historyPanel.removeAllComponents();
        historyPanel.addComponent(new Label("Début du combat"));

        etageActuel = new Etage(1);
        minotaureActuel.setNiveau(etage.getEtage());
        minotaureActuel.resetPointsDeVie(); // Réinitialise les PV du Minotaure
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


    public static UseItemResult callUseItemAPI(String joueurId, String itemId, String cibleId) {
        try {
            // Encoder les parties dynamiques de l'URL
            String encodedJoueurId = URLEncoder.encode(joueurId, StandardCharsets.UTF_8.toString());
            String urlString = "http://localhost:8080/Combat/" + encodedJoueurId + "/use-item";
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + jwtToken);
            connection.setDoOutput(true);

            // Construire le JSON
            JSONObject requestBody = new JSONObject();
            requestBody.put("itemId", itemId);
            if (cibleId != null) {
                requestBody.put("cibleId", cibleId);
            }

            System.out.println("Données envoyées : " + requestBody.toString());

            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.toString().getBytes("utf-8"));
            }

            int status = connection.getResponseCode();
            if (status == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    System.out.println("Réponse de l'API : " + response.toString());
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(response.toString(), UseItemResult.class);
                }
            } else {
                System.err.println("Erreur API: " + status);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line.trim());
                    }
                    System.err.println("Détails de l'erreur : " + errorResponse);
                }
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
        if (minotaureActuel == null) {
            minotaureActuel = new Minotaurus("999", etageActuel.getEtage());
        }
        return minotaureActuel;
    }

    public static void setMinotaurusActuel(Minotaurus minotaurus) {
        minotaureActuel = minotaurus;
    }
}
