package be.helha.projets.projetdarktower.DTO;

public record UseItemResult(
        String message,
        int degatsInfliges,        // Pour armes, sinon 0
        int pointsDeVieRendues,    // Pour potions, sinon 0
        String itemSupprimeId      // Peut être null si pas supprimé
) {}


