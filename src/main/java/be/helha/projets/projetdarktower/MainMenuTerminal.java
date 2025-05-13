package be.helha.projets.projetdarktower;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import be.helha.projets.projetdarktower.Model.*;
import be.helha.projets.projetdarktower.Service.CharacterService;

public class MainMenuTerminal {

    private static String jwtToken = null;
    private static boolean isLoggedIn = false; // Variable pour vérifier si l'utilisateur est connecté

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        CharacterService characterService = new CharacterService();
        int choice;

        do {
            // Afficher le menu principal si l'utilisateur n'est pas connecté
            if (!isLoggedIn) {
                showMainMenu(scanner);
            } else {
                // Afficher le menu après connexion
                showLoggedInMenu(scanner);
            }
        } while (true);


    }

    private static void showMainMenu(Scanner scanner) throws Exception {
        System.out.println("===== MENU DarkTower =====");
        System.out.println("1. S'inscrire");
        System.out.println("2. Se connecter");
        System.out.println("3. Quitter");
        System.out.print("Choix : ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consomme le retour ligne

        switch (choice) {
            case 1 -> register(scanner);
            case 2 -> login(scanner);
            case 3 -> {
                System.out.println("A bientot !");
                System.exit(0); // Quitter l'application
            }
            default -> System.out.println("Choix invalide.");
        }
    }

    private static void showLoggedInMenu(Scanner scanner) {
        System.out.println("===== Bienvenue dans DarkTower =====");
        System.out.println("1. Quitter");
        System.out.println("2. Deconnexion");
        System.out.print("Choix : ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consomme le retour ligne

        switch (choice) {
            case 1 -> {
                System.out.println("A bientot !");
                System.exit(0); // Quitter l'application
            }
            case 2 -> {
                System.out.println("Deconnexion reussie !");
                isLoggedIn = false;  // Réinitialiser l'état de connexion
                jwtToken = null;  // Effacer le token JWT
                System.out.println("Vous etes deconnecte.");
            }
            default -> System.out.println("Choix invalide.");
        }
    }

    private static void register(Scanner scanner) throws Exception {
        System.out.print("Nom d'utilisateur : ");
        String username = scanner.nextLine();

        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        String response = sendRequest("http://localhost:8080/register", "POST", json, null);
        System.out.println("Réponse : " + response);
    }

    private static void login(Scanner scanner) throws Exception {
        System.out.print("Nom d'utilisateur : ");
        String username = scanner.nextLine();

        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        String response = sendRequest("http://localhost:8080/login", "POST", json, null);

        if (response.contains("Bienvenue")) {
            System.out.println(response); // Afficher le message de bienvenue
            isLoggedIn = true; // Utilisateur connecté
        } else {
            System.out.println("Échec de la connexion : " + response);
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

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        } catch (IOException e) {
            return "Erreur : " + con.getResponseCode();
        }
    }
}
