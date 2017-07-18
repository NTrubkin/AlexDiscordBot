package com.company.alexdiscordbot.handler;

import com.company.alexdiscordbot.game.BlackJackGame;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 */
public class BlackJackHandler {

    private static final String PLAYING_STATUS = "BLACKJACK";
    private static final String NORMAL_STATUS = "Bot";
    private static final Map<String, String> params;
    private BlackJackGame game = new BlackJackGame();

    static {
        params = new HashMap<String, String>();
        params.put("help", "help");
        params.put("create", "create");
        params.put("start", "start");
        params.put("stop", "stop");
        params.put("join", "join");
        params.put("take", "take");
        params.put("enough", "enough");
        params.put("status", "status");
    }

    @EventSubscriber
    public void onMessageReceiveEvent(MessageReceivedEvent event) {
        String param = readCommand(event.getMessage().getContent(), event.getClient().getApplicationClientID());
        String results = null;
        if (!StringUtils.isEmpty(param)) {
            results = loadMethodByParam(param, event.getAuthor().getLongID());
        }
        if (!StringUtils.isEmpty(results)) {
            event.getChannel().sendMessage(results);
        }
        if(game.isGameStarts()) {
            event.getClient().changePlayingText(PLAYING_STATUS);
        }
        else {
            event.getClient().changePlayingText(NORMAL_STATUS);
        }
    }

    /**
     * Проверяет, является ли сообщение командой
     * Пример комманды: <@123> blackjack param
     * Здесь 123 - id клиента, param - любой параметр команды
     *
     * @param msg команда с параметром
     * @return параметр команды, null, если команда неверна
     */
    private String readCommand(String msg, String clientId) {
        if (StringUtils.isEmpty(msg) || StringUtils.isEmpty(clientId)) {
            throw new IllegalArgumentException("Args cannot be null or empty");
        }

        String commandPattern = " {0,}<@" + clientId + "> {1,}blackjack {1,}";
        String paramPattern = "\\w{1,}";
        if (msg.matches(commandPattern + paramPattern)) {
            return msg.replaceFirst(commandPattern, "");
        }
        else {
            return null;
        }
    }

    private String loadMethodByParam(String param, Long userId) {
        if (StringUtils.isEmpty(param)) {
            throw new IllegalArgumentException("Arg cannot be null or empty");
        }

        for (String i : params.keySet()) {
            if (i.equals(param)) {
                try {
                    return (String) BlackJackGame.class.getMethod(i, Long.class).invoke(game, userId);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
