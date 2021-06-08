package com.justinthegreat.bots.discord.player;

import com.sedmelluq.discord.lavaplayer.filter.PcmFilterFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GuildAudioPlayer implements AudioPlayer {
    private final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();
    private final AudioSendHandlerImpl sendHandler = new AudioSendHandlerImpl(this);
    private final AudioPlayer player;
    private final Guild guild;
    private long lastFrameProvidedTime = System.currentTimeMillis();

    public GuildAudioPlayer(AudioPlayer player, Guild guild) {
        this.player = player;
        setDefaultVolume();
        this.guild = guild;
        this.addListener(new AudioEventAdapter() {
            @Override
            public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
                if (endReason.mayStartNext) {
                    next();
                }
            }
        });
    }

    public boolean next() {
        if (!queue.isEmpty()) {
            player.startTrack(queue.poll(), false);
            return true;
        }
        return false;
    }

    public void trackLoaded(AudioTrack track, VoiceChannel voiceChannel) {
        AudioManager manager = guild.getAudioManager();
        if (manager == null) {
            return;
        }
        if (manager.getSendingHandler() == null) {
            manager.setSendingHandler(sendHandler);
        }
        if (manager.getConnectedChannel() == null || manager.getConnectedChannel().getIdLong() != voiceChannel.getIdLong()) {
            manager.openAudioConnection(voiceChannel);
        }
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void checkIdle(long threshold) {
        AudioManager manager = guild.getAudioManager();
        if (manager != null && manager.isConnected() && System.currentTimeMillis() - lastFrameProvidedTime > threshold) {
            manager.closeAudioConnection();
        }
    }

    public void setPaused(boolean value, VoiceChannel channel) {
        AudioManager manager = guild.getAudioManager();
        if (!value && player.getPlayingTrack() != null && channel != null && manager != null) {
            if (!manager.isConnected() || manager.getConnectedChannel().getIdLong() != channel.getIdLong()) {
                manager.openAudioConnection(channel);
            }
        }
        player.setPaused(value);
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
        queue.clear();
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

    public void setDefaultVolume() {
        player.setVolume(20);
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
