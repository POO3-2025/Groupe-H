package be.helha.projets.projetdarktower;


import be.helha.projets.projetdarktower.Item.Coffre;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Model.Personnage;
import be.helha.projets.projetdarktower.Inventaire.InventaireDAOImpl;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

import java.util.List;



public class LanternaInventaireManager {

    private final InventaireDAOImpl inventaireDAO = new InventaireDAOImpl();
    private final WindowBasedTextGUI gui;
    private final Personnage personnage;
    private static int userId;
    public LanternaInventaireManager(WindowBasedTextGUI gui, Personnage personnage) {
        this.gui = gui;
        this.personnage = personnage;
    }

    public void show() {
        BasicWindow window = new BasicWindow("Gestion de l'inventaire - " + personnage.getNom());
        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("=== INVENTAIRE ==="));

        List<Item> inventaire = inventaireDAO.chargerInventaire(personnage.getId());
        List<Item> coffre = inventaireDAO.recupererContenuCoffre(personnage.getId());

        if (inventaire.isEmpty()) {
            panel.addComponent(new Label("Aucun item dans l'inventaire."));
        } else {
            panel.addComponent(new Label("--- Items Inventaire ---"));
            for (Item item : inventaire) {
                if (item == null) continue;
                panel.addComponent(new Button(item.getNom(), () -> {
                    afficherOptionsItem(item, true, window);
                }));
            }
        }

        if (coffre != null && !coffre.isEmpty()) {
            panel.addComponent(new Label("--- Items Coffre ---"));
            for (Item item : coffre) {
                if (item == null) continue;
                panel.addComponent(new Button(item.getNom() + " (Coffre)", () -> {
                    afficherOptionsItem(item, false, window);
                }));
            }
        }

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", window::close));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private void afficherOptionsItem(Item item, boolean depuisInventaire, BasicWindow previousWindow) {
        BasicWindow window = new BasicWindow("Actions sur : " + item.getNom());
        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("Que faire avec : " + item.getNom()));

        panel.addComponent(new Button("Supprimer", () -> {
            if (depuisInventaire) {
                inventaireDAO.DeleteItem(item.getId());
            } else {
                inventaireDAO.supprimerItemDuCoffre(item.getId(), personnage.getId());
            }
            MessageDialog.showMessageDialog(gui, "Suppression", "Item supprimé.");
            window.close();
            previousWindow.close();
            show();
        }));

        if (depuisInventaire) {
            boolean hasCoffre = inventaireDAO.hasCoffreInInventory(String.valueOf(personnage.getId()));
            if (hasCoffre) {
                panel.addComponent(new Button("Ajouter au coffre", () -> {
                    boolean success = inventaireDAO.ajouterItemDansCoffre(item, personnage.getId());
                    if (success) {
                        inventaireDAO.DeleteItem(item.getId());
                        MessageDialog.showMessageDialog(gui, "Succès", "Item déplacé dans le coffre.");
                    } else {
                        MessageDialog.showMessageDialog(gui, "Erreur", "Le coffre est plein ou introuvable.");
                    }
                    window.close();
                    previousWindow.close();
                    show();
                }));
            }
        } else {
            panel.addComponent(new Button("Ajouter à l'inventaire", () -> {
                boolean ok = inventaireDAO.ajouterItem(item, personnage.getId());
                if (ok) {
                    inventaireDAO.supprimerItemDuCoffre(item.getId(), personnage.getId());
                    MessageDialog.showMessageDialog(gui, "Succès", "Item déplacé dans l'inventaire.");
                } else {
                    MessageDialog.showMessageDialog(gui, "Erreur", "Inventaire plein !");
                }
                window.close();
                previousWindow.close();
                show();
            }));
        }

        panel.addComponent(new Button("Retour", window::close));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }
}
