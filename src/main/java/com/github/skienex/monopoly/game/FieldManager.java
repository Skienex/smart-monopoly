package com.github.skienex.monopoly.game;

import com.github.skienex.monopoly.GameManager;
import com.github.skienex.monopoly.utils.json.Player;

public class FieldManager {
    public static void fieldManager(GameManager manager, Player player) {
        FieldData fieldData = manager.fieldData(player);
        System.out.println(fieldData);
        switch (fieldData) {
            case OWNED_BY_PLAYER -> {
                // Spieler besitz das Feld
                // TODO: Show field menu (field owned)
                System.out.println("Spieler besitz das Feld schon");
            }
            case OWNED_BY_OTHER_PLAYER -> {
                // Feld von anderem Spieler
                // TODO: Show rent of the street and owner.
                System.out.println("Feld von anderem Spieler");
            }
            case FREE -> {
                // Feld frei
                // TODO: Show buying menu
                System.out.println("Feld kaufbar");
            }
            case NO_STREET -> {
                // Keine Straße -> nicht bebeaubar (Los-Feld, etc.)
                // TODO: Zeige Feld an und aktiviere Special Methoden (ins Gefängnis, etc.)
                System.out.println("Keine Straße -> nicht bebaubar");
            }
            default -> {
                System.err.println("Error at FieldManager.java --> Field data check failed! <--");
            }
        }
    }
}
