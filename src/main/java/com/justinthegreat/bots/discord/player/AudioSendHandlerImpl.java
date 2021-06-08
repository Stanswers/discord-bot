package com.justinthegreat.bots.discord.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

public class AudioSendHandlerImpl implements AudioSendHandler {
    private final AudioPlayer player;
    private AudioFrame frame;

    public AudioSendHandlerImpl(AudioPlayer player) {
        this.player = player;
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(frame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    @Override
    public boolean canProvide() {
        try {
            frame = player.provide();
        } catch (UnsupportedOperationException e) {
            // For some reason the last frame of a local file throws this exception
            frame = null;
            return false;
        }
        return frame != null;
    }
}
