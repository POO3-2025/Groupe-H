package be.helha.projets.projetdarktower;

import be.helha.projets.projetdarktower.Model.*;
import be.helha.projets.projetdarktower.Service.CharacterService;

import java.util.Scanner;

public class MainMenuTerminal {

    private static String jwtToken = null;
    private static String userId = "user1"; // Exemple d'utilisateur

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        CharacterService characterService = new CharacterService();
        int choice;

        do {
            System.out.println("===== Bienvenue dans DarkTower =====");
            System.out.println("1. Choisir un personnage");
            System.out.println("2. Quitter");
            System.out.print("Choix: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consommer le newline

            switch (choice) {
                case 1:
                    System.out.println("Selectionnez un personnage ( 1 ) fistfire, 2 ) waterwa, 3 ) jowind, 4 ) twood):");
                    String characterId = scanner.nextLine();
                    CharacterSelectionRequest request = new CharacterSelectionRequest();
                    request.setUserId(userId);
                    request.setCharacterId(characterId);
                    characterService.selectCharacter(characterId); // Assurer la sélection du personnage
                    System.out.println("Personnage " + characterId + " sélectionné !");
                    break;
                case 2:
                    System.out.println("Au revoir!");
                    break;
                default:
                    System.out.println("Choix invalide. Essayez encore.");
            }
        } while (choice != 4);

        scanner.close();
    }
}
