package be.helha.projets.projetdarktower.Item;

/**
 * Classe représentant une arme, un type d'item qui inflige des dégâts.
 *
 * <p>Une arme possède un nombre de dégâts ainsi qu'un nombre d'usages limités.</p>
 */
public class Weapon extends Item {
    /**
     * Nombre de dégâts infligés par l'arme.
     */
    private int degats;

    /**
     * Nombre d'usages restants pour l'arme.
     */
    private int usages;

    /**
     * Constructeur principal.
     *
     * @param nom          Nom de l'arme.
     * @param degats       Dégâts infligés par l'arme.
     * @param chanceDeDrop Chance de drop en pourcentage.
     * @param usages       Nombre d'usages possibles.
     */
    public Weapon(String nom, int degats, double chanceDeDrop, int usages) {
        super(nom, chanceDeDrop);
        this.type = "Weapon";
        this.degats = degats;
        this.usages = usages;
    }

    /**
     * Constructeur par défaut nécessaire pour la désérialisation.
     */
    public Weapon() {
        super();
        this.type = "Weapon";
    }

    /**
     * Retourne le nombre de dégâts infligés par l'arme.
     *
     * @return dégâts.
     */
    public int getDegats() {
        return degats;
    }

    /**
     * Définit le nombre de dégâts de l'arme.
     *
     * @param degats nouveaux dégâts.
     */
    public void setDegats(int degats) {
        this.degats = degats;
    }

    /**
     * Retourne le nombre d'usages restants.
     *
     * @return usages restants.
     */
    public int getUsages() {
        return usages;
    }

    /**
     * Définit le nombre d'usages restants.
     *
     * @param usages nouveau nombre d'usages.
     */
    public void setUsages(int usages) {
        this.usages = usages;
    }

    /**
     * Représentation textuelle de l'arme.
     *
     * @return description sous forme de chaîne.
     */
    @Override
    public String toString() {
        return "Weapon {" + super.toString() + ", dégâts=" + degats + '}';
    }
}
