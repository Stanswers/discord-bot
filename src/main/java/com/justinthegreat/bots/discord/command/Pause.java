package com.justinthegreat.bots.discord.command;

import com.justinthegreat.bots.discord.player.GuildAudioPlayer;
import com.justinthegreat.bots.discord.player.GuildAudioPlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Pause implements CommandEventHandler {
    @Override
    public String getLongName() {
        return "pause";
    }

    @Override
    public String getShortName() {
        return "pa";
    }

    @Override
    public void handleEvent(MessageReceivedEvent event, String[] args) {
        GuildAudioPlayer player = GuildAudioPlayerManager.getInstance().getAudioPlayer(event.getGuild());
        player.setPaused(!player.isPaused(), event.getMember().getVoiceState().getChannel());
    }

    @Override
    public boolean handleHelpEvent(MessageReceivedEvent event, String[] args) {
        if ("help".equals(args[1])) {
            event.getChannel().sendMessage("```" + CommandEventListener.PREFIX + "pa [help]```").queue();
            return true;
        }
        return false;
    }
}
