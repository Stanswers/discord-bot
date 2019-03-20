package com.justinthegreat.bots.discord.command;

import com.justinthegreat.bots.discord.player.GuildAudioPlayer;
import com.justinthegreat.bots.discord.player.GuildAudioPlayerManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

public abstract class PlayAudioEventHandler implements CommandEventHandler {
    abstract protected String getUrl(MessageReceivedEvent event, String[] args);

    @Override
    public void handleEvent(MessageReceivedEvent event, String[] args) {
        Guild guild = event.getGuild();
        AudioManager manager = guild.getAudioManager();
        if (manager == null) {
            return;
        }
        GuildAudioPlayer player = GuildAudioPlayerManager.getInstance().getAudioPlayer(guild);
        VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel();
        if (args.length == 1 && player.isPaused()) {
            player.setPaused(false, voiceChannel);
            return;
        }
        MessageChannel channel = event.getChannel();
        if (voiceChannel == null) {
            channel.sendMessage("Connect to a voice channel first!!!").queue();
            return;
        }
        String url = getUrl(event, args);
        if (url == null) {
            return;
        }
        GuildAudioPlayerManager.getInstance().loadItem(url, guild, voiceChannel, channel);
        return;
    }
}
