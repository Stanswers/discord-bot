package com.justinthegreat.bots.discord;

import com.justinthegreat.bots.discord.command.*;
import com.justinthegreat.bots.discord.listeners.MessageReceivedEventLogger;
import com.justinthegreat.bots.discord.listeners.SoundBoardEventListener;
import com.justinthegreat.bots.discord.player.SoundBoard;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2 || args[0].isEmpty() || args[1].isEmpty()) {
            return;
        }
        try {
            SoundBoard sb = new SoundBoard(args[1]);
            JDABuilder builder = JDABuilder.createDefault(args[0]);
            builder.addEventListeners(new MessageReceivedEventLogger(), new CommandEventListener(new SoundBoardCommand(sb), new KillDashNine(), new Play(), new Pause(), new Stop(), new Next(), new Volume()), new SoundBoardEventListener(sb));
            builder.build().awaitReady();
        } catch (InterruptedException | LoginException e) {
            e.printStackTrace();
        }
    }
}
