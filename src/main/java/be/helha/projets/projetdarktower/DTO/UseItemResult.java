package be.helha.projets.projetdarktower.DTO;

/**
 * Résultat de l'utilisation d'un item dans le jeu.
 *
 * @param message           Message décrivant l'effet ou le résultat de l'utilisation.
 * @param degatsInfliges    Nombre de dégâts infligés (pour les armes), 0 sinon.
 * @param pointsDeVieRendues Points de vie rendus (pour les potions), 0 sinon.
 * @param itemSupprimeId    Identifiant de l'item supprimé après usage, peut être null si aucun item supprimé.
 */
public record UseItemResult(
        String message,
        int degatsInfliges,
        int pointsDeVieRendues,
        String itemSupprimeId
) {}
