package dev.enjarai.trickster.spell.mana.type;

public class Mana {
    private final int color;

    public Mana(int color) {
        this.color = color;
    }

    public boolean isEmpty() {
        return this == Manae.EMPTY;
    }

    public int getColor() {
        return this.color;
    }
}
