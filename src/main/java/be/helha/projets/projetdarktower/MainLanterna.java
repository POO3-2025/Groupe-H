package be.helha.projets.projetdarktower;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainLanterna {

    private static String jwtToken = null;
    private static boolean isLoggedIn = false;
    private static int userId = 0;

    public static void main(String[] args) {
        try {
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            terminalFactory.setInitialTerminalSize(new TerminalSize(150, 30));
            SwingTerminalFrame terminal = terminalFactory.createSwingTerminal();
            terminal.setVisible(true);
            terminal.setResizable(false);

            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();

            WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
            while (true) {
                showMainMenu(textGUI, screen);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showMainMenu(WindowBasedTextGUI gui, Screen screen) {
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

    private static void showRegisterMenu(WindowBasedTextGUI gui) {
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

    private static void showLoginMenu(WindowBasedTextGUI gui) {
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

    private static void showLoggedInMenu(WindowBasedTextGUI gui) {
        BasicWindow window = new BasicWindow("Connecté");
        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("Bienvenue dans DarkTower !"));

        // Ajouter un bouton pour gérer l'inventaire
        panel.addComponent(new Button("Gérer l'inventaire", () -> {
            window.close();  // Ferme la fenêtre actuelle
            LanternaInventaireManager inventaireManager = new LanternaInventaireManager(gui, userId); // Passe userId ici
            inventaireManager.show();  // Affiche la gestion de l'inventaire
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

}
