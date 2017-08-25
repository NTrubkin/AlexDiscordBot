package com.company.alexdiscordbot.game;

/**
 * Created by TrubkinN on 17.07.2017.
 */
public class PlayingCard {
    public enum Suit {
        DIAMONDS("♦"), CLUBS("♣"), HEARTS("♥"), SPADES("♠");
        private final String label;

        Suit(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum Rank {
        TWO("2", 2), THREE("3", 3), FOUR("4", 4), FIVE("5", 5), SIX("6", 6), SEVEN("7", 7), EIGHT("8", 8), NINE("9", 9), TEN("10", 10), JACK("J", 10), QUEEN("Q", 10), KING("K", 10), ACE("A", 11, 1);
        private final String label;
        private final int value;
        private final int additionalValue;

        Rank(String label, int value) {
            this(label, value, value);
        }

        Rank(String label, int value, int additionalValue) {
            if(additionalValue > value) {
                throw new IllegalArgumentException("additionalValue must be smaller than value");
            }
            this.label = label;
            this.value = value;
            this.additionalValue = additionalValue;
        }

        private String getLabel() {
            return label;
        }


        private boolean hasAdditionalValue() {
            return value != additionalValue;
        }
    }

    private final Suit suit;
    private final Rank rank;
    private boolean useAdditionalValue = false;

    public PlayingCard(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public boolean hasAdditionalValue() {
        return rank.hasAdditionalValue();
    }

    public int getValue() {
        if (hasAdditionalValue() && useAdditionalValue) {
            return rank.additionalValue;
        }
        else {
            return rank.value;
        }
    }

    public void setUseAdditionalValue(boolean b) {
        useAdditionalValue = b;
    }

    public boolean getUseAdditionalValue() {
        return useAdditionalValue;
    }

    public String showCardLabel() {
        return rank.getLabel() + suit.getLabel();
    }

    @Override
    public String toString() {
        return super.toString() + "( " + showCardLabel() + " )";
    }
}
