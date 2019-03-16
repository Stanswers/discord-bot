package com.justinthegreat.bots.discord.command;

import com.justinthegreat.bots.discord.BotRuntime;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Stop implements CommandEventHandler {

    @Override
    public String getLongName() {
        return "stop";
    }

    @Override
    public String getShortName() {
        return "s";
    }

    @Override
    public void handleEvent(MessageReceivedEvent event, String[] args) {
        Guild guild = event.getGuild();
        if (guild == null) {
            return;
        }
        AudioPlayer player = BotRuntime.getInstance().getOrCreateAudioPlayer(guild);
        // TODO: consider clearing track queue (TrackScheduler) and leaving voice channel
        player.stopTrack();
    }
}
