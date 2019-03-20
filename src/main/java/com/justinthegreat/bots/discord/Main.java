package com.justinthegreat.bots.discord;

import com.justinthegreat.bots.discord.command.CommandEventListener;
import com.justinthegreat.bots.discord.command.KillDashNine;
import com.justinthegreat.bots.discord.command.Next;
import com.justinthegreat.bots.discord.command.Pause;
import com.justinthegreat.bots.discord.command.Play;
import com.justinthegreat.bots.discord.command.SoundBoardCommand;
import com.justinthegreat.bots.discord.command.Stop;
import com.justinthegreat.bots.discord.listeners.MessageReceivedEventLogger;
import com.justinthegreat.bots.discord.listeners.SoundBoardEventListener;
import com.justinthegreat.bots.discord.listeners.TrollGeorge;
import com.justinthegreat.bots.discord.player.SoundBoard;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2 || args[0].isEmpty() || args[1].isEmpty()) {
            return;
        }
        try {
            SoundBoard sb = new SoundBoard(args[1]);
            JDABuilder builder = new JDABuilder(args[0]);
            builder.addEventListener(new MessageReceivedEventLogger());
            builder.addEventListener(new CommandEventListener(new SoundBoardCommand(sb), new KillDashNine(), new Play(), new Pause(), new Stop(), new Next()));
            builder.addEventListener(new SoundBoardEventListener(sb));
            builder.addEventListener(new TrollGeorge());
            builder.build().awaitReady();
        } catch (InterruptedException | LoginException e) {
            e.printStackTrace();
        }
    }
}
