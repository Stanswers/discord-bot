package com.justinthegreat.bots.discord.command;

import com.justinthegreat.bots.discord.player.GuildAudioPlayer;
import com.justinthegreat.bots.discord.player.GuildAudioPlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Volume implements CommandEventHandler {
    @Override
    public String getLongName() {
        return "volume";
    }

    @Override
    public String getShortName() {
        return "v";
    }

    @Override
    public void handleEvent(MessageReceivedEvent event, String[] args) {
        GuildAudioPlayer player = GuildAudioPlayerManager.getInstance().getAudioPlayer(event.getGuild());
        if (args.length == 1) {
            player.setDefaultVolume();
            return;
        }
        try {
            player.setVolume(Integer.valueOf(args[1]));
        } catch(NumberFormatException e) {
            event.getChannel().sendMessage("Invalid volume: " + args[1]).queue();
        }
    }

    @Override
    public boolean handleHelpEvent(MessageReceivedEvent event, String[] args) {
        if ("help".equals(args[1])) {
            event.getChannel().sendMessage("```" + CommandEventListener.PREFIX + "v [<volume>|help]```").queue();
            return true;
        }
        return false;
    }
}
