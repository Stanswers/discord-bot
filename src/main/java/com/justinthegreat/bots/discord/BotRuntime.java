package com.justinthegreat.bots.discord;

import com.justinthegreat.bots.discord.player.GuildAudioPlayer;
import com.justinthegreat.bots.discord.player.GuildAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class BotRuntime {
    private static final BotRuntime BOT_SINGLETON = new BotRuntime();
    private static final GuildAudioPlayerManager AUDIO_PLAYER_MANAGER = new GuildAudioPlayerManager();

    static {
        AudioSourceManagers.registerRemoteSources(AUDIO_PLAYER_MANAGER);
        AUDIO_PLAYER_MANAGER.registerSourceManager(new LocalAudioSourceManager());
    }

    private String prefix = "-";

    private BotRuntime() {
    }

    public static final BotRuntime getInstance() {
        return BOT_SINGLETON;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public GuildAudioPlayer getAudioPlayer(Guild guild) {
        return AUDIO_PLAYER_MANAGER.getAudioPlayer(guild);
    }

    public void loadItem(String url, Guild guild, VoiceChannel voiceChannel, MessageChannel channel) {
        AUDIO_PLAYER_MANAGER.loadItem(url, guild, voiceChannel, channel);
    }
}
