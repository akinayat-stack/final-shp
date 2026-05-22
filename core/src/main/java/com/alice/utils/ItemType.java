package com.alice.utils;

public enum ItemType {
    KEY("KEYS", "key.png"),
    ROSE("ROSES", "rose.png"),
    CARD("CARDS", "card.png");

    public final String label;
    public final String texturePath;

    ItemType(String label, String texturePath) {
        this.label = label;
        this.texturePath = texturePath;
    }
}