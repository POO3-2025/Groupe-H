package be.helha.projets.projetdarktower.DTO;

public record UseItemResult(
        String message,
        int pvUtilisateur,
        int pvCible,
        String itemSupprimeId // peut être null si pas supprimé
) {}

