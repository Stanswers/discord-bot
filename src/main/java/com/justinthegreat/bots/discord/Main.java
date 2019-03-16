package com.justinthegreat.bots.discord;

import com.justinthegreat.bots.discord.command.*;
import com.justinthegreat.bots.discord.listeners.CommandEventListener;
import com.justinthegreat.bots.discord.listeners.MessageReceivedEventLogger;
import com.justinthegreat.bots.discord.listeners.TrollGeorge;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println(Arrays.toString(args));
        if (args.length != 2 || args[0].isEmpty() || args[1].isEmpty()) {
            return;
        }
        try {
            JDABuilder builder =  new JDABuilder(args[0]);
            builder.addEventListener(new CommandEventListener(new SoundBoardCommand(Arrays.copyOfRange(args, 1, args.length)), new KillDashNine(), new Play(), new Pause(), new Stop()));
            builder.addEventListener(new TrollGeorge());
            builder.addEventListener(new MessageReceivedEventLogger());
            builder.build().awaitReady();
        } catch (InterruptedException | LoginException e) {
            e.printStackTrace();
        }
    }
}
