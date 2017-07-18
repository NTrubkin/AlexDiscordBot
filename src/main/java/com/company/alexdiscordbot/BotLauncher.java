package com.company.alexdiscordbot;

import com.company.alexdiscordbot.handler.BlackJackHandler;
import com.company.alexdiscordbot.handler.RegularHandler;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

import java.io.*;

/**
 * Created by TrubkinN on 16.07.2017.
 */
public class BotLauncher {

    private static final String TOKEN_FILE = "src/main/resources/token.txt";
    private static IDiscordClient client;

    public static void main(String[] args) throws DiscordException, InterruptedException, IOException {
        client = new ClientBuilder().withToken(loadTokenFromFile()).build();
        client.getDispatcher().registerListener(new RegularHandler());
        client.getDispatcher().registerListener(new BlackJackHandler());
        client.login();
    }

    private static String loadTokenFromFile() throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(TOKEN_FILE)))) {
            return bufferedReader.readLine();
        } catch (IOException exc) {
            exc.printStackTrace();
            throw new IOException("Troubles with token file", exc);
        }
    }
}
