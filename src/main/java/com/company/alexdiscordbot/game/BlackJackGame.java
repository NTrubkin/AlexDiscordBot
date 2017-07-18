package com.company.alexdiscordbot.game;

import java.util.*;

/**
 * Created by TrubkinN on 17.07.2017.
 */
public class BlackJackGame {

    private enum GameState {NonPlaying, Recruting, Playing, Finishing}

    private GameState currentState = GameState.NonPlaying;
    private List<PlayingCard> deck = new ArrayList<>();
    private LinkedHashMap<Long, List<PlayingCard>> players = new LinkedHashMap<>();
    private Long hostPlayerId;
    private Long currentPlayerId = 0L;
    private List<PlayingCard> dealerHand = new ArrayList<>();
    private static final int WIN_SCORE = 21;


    private static final String HELP_MSG = "command params: help, create, start, stop, join, take, enough, status";
    private static final String PERM_MSG = "Sorry, you dont have permission";
    private static final String STATE_MSG = "Cant perform command while game state is ";
    private static final String TURN_MSG = "Not your turn";

    public String help(Long userId) {
        return HELP_MSG;
    }

    public String create(Long userId) {
        if (currentState != GameState.Finishing && currentState != GameState.NonPlaying) {
            return STATE_MSG + currentState;
        }

        players.clear();
        hostPlayerId = userId;
        currentState = GameState.Recruting;
        join(userId);
        return "New blackjack game";
    }

    public String start(Long userId) {
        if (currentState != GameState.Recruting) {
            return STATE_MSG + currentState;
        }
        if (!userId.equals(hostPlayerId)) {
            return PERM_MSG;
        }

        generateDeck();
        shuffleDeck();
        dealerHand.clear();
        String log = takeStartCards();
        currentPlayerId = 0L;
        nextPlayer();
        currentState = GameState.Playing;
        return "BlackJack game has started\n" + log;
    }

    public String stop(Long userId) {
        if (currentState != GameState.Recruting && currentState != GameState.Playing && currentState != GameState.Finishing) {
            return STATE_MSG + currentState;
        }
        if (!userId.equals(hostPlayerId)) {
            return PERM_MSG;
        }

        currentState = GameState.NonPlaying;
        return "Game has stopped";
    }

    public String join(Long userId) {
        if (currentState != GameState.Recruting) {
            return STATE_MSG + currentState;
        }

        if (players.keySet().contains(userId)) {
            return "This player is already joined";
        }

        players.put(userId, new ArrayList<>());
        return "Player has joined";
    }

    public String take(Long userId) {
        if (currentState != GameState.Playing) {
            return STATE_MSG + currentState;
        }
        if (!userId.equals(currentPlayerId)) {
            return PERM_MSG;
        }

        PlayingCard card = takeCard(players.get(currentPlayerId));
        int value = countHandValue(players.get(currentPlayerId));
        String log = card.showCardLabel();
        if(value == WIN_SCORE) {
            if(nextPlayer()) {
                return log + " BLACKJACK!!! \nNext player " + makeMention(currentPlayerId);
            }
            else {
                return log + " BLACKJACK!!! \n" + finishGame(userId);
            }
        }
        else if(value > WIN_SCORE) {
            if(nextPlayer()) {
                return log + " " + value + " is more than " + WIN_SCORE + "\nNext player " + makeMention(currentPlayerId);
            }
            else {
                return log + " " + value + " is more than " + WIN_SCORE + "\n" + finishGame(userId);
            }
        }

        return log;
    }

    public String enough(Long userId) {
        if (currentState != GameState.Playing) {
            return STATE_MSG + currentState;
        }
        if (!userId.equals(currentPlayerId)) {
            return PERM_MSG;
        }

        if(nextPlayer()) {
            return "Ok, enough\nNext player "  + makeMention(currentPlayerId);
        }
        else {
            return "Ok, enough\n" + finishGame(userId);
        }
    }

    public String status(Long userId) {
        String log = "Current game state is " + currentState + "\n";
        switch (currentState) {
            case NonPlaying:
                return log + "";
            case Recruting:
                return log + "Waiting for players";
            case Playing:
                for(Long playerId : players.keySet()) {
                    log += makeMention(playerId) + ":  ";
                    for(PlayingCard card : players.get(playerId)) {
                        log += card.showCardLabel();
                    }
                    log += "\nTotal:" + countHandValue(players.get(playerId)) + "\n";
                }
                log += "Current turn is: " + makeMention(currentPlayerId) + "\n";
                return log + "";
            case Finishing:
                for(Long playerId : players.keySet()) {
                    log += makeMention(playerId) + ": ";
                    for(PlayingCard card : players.get(playerId)) {
                        log += card.showCardLabel();
                    }
                    log += "\nTotal:" + countHandValue(players.get(playerId)) + "\n";
                }
                return log + "Game has finished!";
            default:
                throw new UnsupportedOperationException();
        }
    }

    private String finishGame(Long userId) {
        currentState = GameState.Finishing;
        String log = status(userId);
        currentState = GameState.NonPlaying;
        return log;
    }

    private void generateDeck() {
        deck.clear();
        for (PlayingCard.Suit suit : PlayingCard.Suit.values()) {
            for (PlayingCard.Rank rank : PlayingCard.Rank.values()) {
                deck.add(new PlayingCard(suit, rank));
            }
        }
    }

    private void shuffleDeck() {
        long seed = System.nanoTime();
        Collections.shuffle(deck, new Random(seed));
    }

    /**
     * Оставляет за собой право изменять состояние значения карты на более низкое (additionalValue), если достигнут перебор
     * @param hand
     * @return
     */
    private int countHandValue(List<PlayingCard> hand) {
        int sum;
        do {
            sum = 0;
            for(PlayingCard card : hand) {
                sum += card.getValue();
            }
        } while (sum > WIN_SCORE && decreaseValue(hand));
        return sum;
    }

    /**
     *
     * @return true - значение руки было уменьшено
     */
    private boolean decreaseValue(List<PlayingCard> hand) {
        for(PlayingCard card : hand) {
            if(card.hasAdditionalValue() && !card.getUseAdditionalValue()) {
                card.setUseAdditionalValue(true);
                return true;
            }
        }
        return false;
    }

    private boolean nextPlayer() {
        boolean nextIsGood = (currentPlayerId.equals(0L) ? true : false);
        for (Long player : players.keySet()) {
            if(nextIsGood) {
                currentPlayerId = player;
                return true;
            }
            if(player.equals(currentPlayerId)) {
                nextIsGood = true;
            }
        }
        return false;
    }

    private PlayingCard takeCard(List<PlayingCard> hand) {
        PlayingCard card = deck.remove(0);
        hand.add(card);
        return card;
    }

    private String takeStartCards() {
        String log = "";
        for(Long playerId : players.keySet()) {
            log += makeMention(playerId) + ": ";
            log += takeCard(players.get(playerId)).showCardLabel();
            log += takeCard(players.get(playerId)).showCardLabel();
        }
        return log;
    }

    public boolean isGameStarts() {
        return currentState != GameState.NonPlaying;
    }

    private String makeMention(Long userId) {
        return "<@" + userId + ">";
    }
}
