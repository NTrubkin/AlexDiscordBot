package com.company.alexdiscordbot.handler;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;

/**
 *
 */
public class RegularHandler {

    private static final String START_MSG = "Alex bot is here!";
    private static final String HELP_MSG = "Wanna play?\nJust send to me @<MyName> blackjack <param>.\n@<MyName> blackjack help for help.";
    private static final int DELAY = 1000;
    private static final HashMap<String, String> reactions = new HashMap<String, String>();

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        loadReactions();
        event.getClient().getChannels().get(0).setTypingStatus(true);
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        event.getClient().getChannels().get(0).sendMessage(START_MSG + "\n\n" + HELP_MSG);
        event.getClient().getChannels().get(0).setTypingStatus(false);
    }

    @EventSubscriber
    public void onMessageReceiveEvent(MessageReceivedEvent event) {
        String msg = event.getMessage().getContent();
        String reaction = reactions.get(msg);
        if (reaction != null) {
            event.getMessage().addReaction(reaction);

        }
    }

    public static void loadReactions() {
        reactions.put("bot is cool", ":heart:");
        reactions.put("bot is stupid", ":stuck_out_tongue_winking_eye:");
    }
}
