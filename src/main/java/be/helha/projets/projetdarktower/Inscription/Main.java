package be.helha.projets.projetdarktower.Inscription;

import java.util.Scanner;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Menu ===");
        System.out.println("1: S'inscrire");
        System.out.print("Votre choix : ");
        int choix = scanner.nextInt();
        scanner.nextLine(); // consommer le retour à la ligne

        if (choix == 1) {
            System.out.print("Entrez votre nom d'utilisateur : ");
            String username = scanner.nextLine();

            System.out.print("Entrez votre mot de passe : ");
            String password = scanner.nextLine();

            try {
                String jsonInput = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

                URL url = new URL("http://localhost:8080/api/auth/register");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setDoOutput(true);

                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int code = con.getResponseCode();
                if (code == 200 || code == 201) {
                    System.out.println("Inscription réussie !");
                } else {
                    System.out.println("Échec de l'inscription. Code: " + code);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Choix invalide.");
        }

        scanner.close();
    }
}
