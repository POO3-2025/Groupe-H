package be.helha.projets.projetdarktower;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import be.helha.projets.projetdarktower.Inventaire.InventaireDAOImpl;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Model.*;
import be.helha.projets.projetdarktower.Service.CharacterService;

public class MainMenuTerminal {

    private static String jwtToken = null;
    private static boolean isLoggedIn = false; // Variable pour vérifier si l'utilisateur est connecté

    public static void main(String[] args) throws Exception {
        InventaireDAOImpl dao = new InventaireDAOImpl();
        String objectId = "682271a08ca2e216ce73a681";

        Item item = dao.recupererItemParId(objectId,1);
        System.out.println(item);
    }
}
