package be.helha.projets.projetdarktower.Item;

/**
 * Classe représentant une potion, un type d'item permettant de récupérer des points de vie.
 *
 * <p>Une potion possède un nombre de points de vie rendus et un nombre d'usages possibles.</p>
 */
public class Potion extends Item {
    /**
     * Nombre de points de vie récupérés lors de l'utilisation de la potion.
     */
    private int pointsDeVieRecuperes;

    /**
     * Nombre d'usages possibles de la potion.
     */
    private int usages;

    /**
     * Constructeur principal.
     *
     * @param nom                  Nom de la potion.
     * @param pointsDeVieRecuperes Points de vie que la potion rend.
     * @param chanceDeDrop         Chance de drop en pourcentage.
     * @param usages               Nombre d'usages de la potion.
     */
    public Potion(String nom, int pointsDeVieRecuperes, double chanceDeDrop, int usages) {
        super(nom, chanceDeDrop);
        this.type = "Potion";
        this.pointsDeVieRecuperes = pointsDeVieRecuperes;
        this.usages = usages;
    }

    /**
     * Constructeur par défaut nécessaire pour la désérialisation.
     */
    public Potion() {
        super();
        this.type = "Potion";
    }

    /**
     * Retourne le nombre de points de vie récupérés par la potion.
     *
     * @return points de vie récupérés.
     */
    public int getPointsDeVieRecuperes() {
        return pointsDeVieRecuperes;
    }

    /**
     * Définit le nombre de points de vie récupérés par la potion.
     *
     * @param pointsDeVieRecuperes nouveau nombre de points de vie récupérés.
     */
    public void setPointsDeVieRecuperes(int pointsDeVieRecuperes) {
        this.pointsDeVieRecuperes = pointsDeVieRecuperes;
    }

    /**
     * Retourne le nombre d'usages possibles de la potion.
     *
     * @return nombre d'usages.
     */
    public int getUsages() {
        return usages;
    }

    /**
     * Définit le nombre d'usages possibles de la potion.
     *
     * @param usages nouveau nombre d'usages.
     */
    public void setUsages(int usages) {
        this.usages = usages;
    }

    /**
     * Représentation textuelle de la potion.
     *
     * @return description sous forme de chaîne.
     */
    @Override
    public String toString() {
        return "Potion {" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", pointsDeVieRecuperes=" + pointsDeVieRecuperes +
                '}';
    }
}
