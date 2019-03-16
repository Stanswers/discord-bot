package com.justinthegreat.bots.discord;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.managers.AudioManager;

public class BotRuntime {
    private static final BotRuntime BOT_SINGLETON = new BotRuntime();
    private static final AudioPlayerManager AUDIO_PLAYER_MANAGER = new DefaultAudioPlayerManager();

    static {
        AudioSourceManagers.registerRemoteSources(AUDIO_PLAYER_MANAGER);
        AUDIO_PLAYER_MANAGER.registerSourceManager(new LocalAudioSourceManager());
    }

    private String prefix = "-";
    private TLongObjectHashMap<AudioPlayer> players = new TLongObjectHashMap<>();

    private BotRuntime() {
    }

    public static final BotRuntime getInstance() {
        return BOT_SINGLETON;
    }

    public  AudioPlayerManager getAudioPlayerManager() {
        return AUDIO_PLAYER_MANAGER;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public AudioPlayer getOrCreateAudioPlayer(Guild guild) {
        synchronized (players) {
            AudioPlayer player = players.get(guild.getIdLong());
            if (player == null) {
                player = AUDIO_PLAYER_MANAGER.createPlayer();
                players.put(guild.getIdLong(), player);
                AudioManager manager = guild.getAudioManager();
                player.addListener(new AudioEventAdapter() {
                    @Override
                    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
                        // TODO: Also check the TrackScheduler to see if it is empty before closing connection
                        switch (endReason) {
                            case FINISHED:
                            case LOAD_FAILED:
                            case STOPPED:
                            case CLEANUP:
                                if (manager != null && manager.getConnectedChannel() != null) {
                                    manager.closeAudioConnection();
                                }
                                break;
                            case REPLACED:
                            default:
                                break;
                        }
                    }
                });
            }
            return player;
        }
    }
}
