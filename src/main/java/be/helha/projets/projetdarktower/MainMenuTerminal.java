package be.helha.projets.projetdarktower;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainMenuTerminal {

    private static String jwtToken = null;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("===== MENU =====");
            System.out.println("1. S'inscrire");
            System.out.println("2. Se connecter");
            System.out.println("3. Quitter");
            System.out.print("Choix : ");
            choice = scanner.nextInt();
            scanner.nextLine(); // consomme le retour ligne

            switch (choice) {
                case 1 -> register(scanner);
                case 2 -> login(scanner);
                case 3 -> System.out.println("À bientôt !");
                default -> System.out.println("Choix invalide.");
            }
        } while (choice != 3);

        scanner.close();
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

        if (response.contains("Token JWT")) {
            jwtToken = response.split("Token JWT : ")[1].trim();
            System.out.println("Connexion réussie !");
            System.out.println("Bonjour " + username + " !");
            System.out.println("Token : " + jwtToken);

            // Tester un endpoint protégé
            /*System.out.println("\nAccès au profil sécurisé :");
            String secure = sendRequest("http://localhost:8080/profile", "GET", null, jwtToken);
            System.out.println("Réponse : " + secure); */
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

