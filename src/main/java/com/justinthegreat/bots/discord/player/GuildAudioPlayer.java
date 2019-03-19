package com.justinthegreat.bots.discord.player;

import com.sedmelluq.discord.lavaplayer.filter.PcmFilterFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GuildAudioPlayer implements AudioPlayer {
    private final AudioPlayer player;
    private final Guild guild;
    private long lastFrameProvidedTime = System.currentTimeMillis();

    public GuildAudioPlayer(AudioPlayer player, Guild guild) {
        this.player = player;
        this.guild = guild;
    }

    public void checkIdle(long threshold) {
        AudioManager manager = guild.getAudioManager();
        if (manager != null && manager.isConnected() && System.currentTimeMillis() - lastFrameProvidedTime > threshold) {
            manager.closeAudioConnection();
        }
    }

    public void unPause(VoiceChannel channel) {
        AudioManager manager = guild.getAudioManager();
        if (channel != null && player.isPaused() && manager != null && !manager.isConnected()) {
            manager.openAudioConnection(channel);
        }
        player.setPaused(false);
    }

    @Override
    public AudioFrame provide() {
        AudioFrame f = player.provide();
        if (f != null) {
            lastFrameProvidedTime = System.currentTimeMillis();
        }
        return f;
    }

    @Override
    public AudioFrame provide(long timeout, TimeUnit unit) throws TimeoutException, InterruptedException {
        AudioFrame f = player.provide(timeout, unit);
        if (f != null) {
            lastFrameProvidedTime = System.currentTimeMillis();
        }
        return f;
    }

    @Override
    public boolean provide(MutableAudioFrame targetFrame) {
        boolean b = provide(targetFrame);
        if (b) {
            lastFrameProvidedTime = System.currentTimeMillis();
        }
        return b;
    }

    @Override
    public boolean provide(MutableAudioFrame targetFrame, long timeout, TimeUnit unit) throws TimeoutException, InterruptedException {
        boolean b = player.provide(targetFrame, timeout, unit);
        if (b) {
            lastFrameProvidedTime = System.currentTimeMillis();
        }
        return b;
    }

    @Override
    public AudioTrack getPlayingTrack() {
        return player.getPlayingTrack();
    }

    @Override
    public void playTrack(AudioTrack track) {
        player.playTrack(track);
    }

    @Override
    public boolean startTrack(AudioTrack track, boolean noInterrupt) {
        return player.startTrack(track, noInterrupt);
    }

    @Override
    public void stopTrack() {
        player.stopTrack();
    }

    @Override
    public int getVolume() {
        return player.getVolume();
    }

    @Override
    public void setVolume(int volume) {
        player.setVolume(volume);
    }

    @Override
    public void setFilterFactory(PcmFilterFactory factory) {
        player.setFilterFactory(factory);
    }

    @Override
    public void setFrameBufferDuration(Integer duration) {
        player.setFrameBufferDuration(duration);
    }

    @Override
    public boolean isPaused() {
        return player.isPaused();
    }

    @Override
    public void setPaused(boolean value) {
        player.setPaused(value);
    }

    @Override
    public void destroy() {
        player.destroy();
    }

    @Override
    public void addListener(AudioEventListener listener) {
        player.addListener(listener);
    }

    @Override
    public void removeListener(AudioEventListener listener) {
        player.removeListener(listener);
    }

    @Override
    public void checkCleanup(long threshold) {
        player.checkCleanup(threshold);
    }
}
