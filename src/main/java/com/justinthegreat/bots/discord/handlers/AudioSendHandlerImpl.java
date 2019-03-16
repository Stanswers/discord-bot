package com.justinthegreat.bots.discord.handlers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.core.audio.AudioSendHandler;

public class AudioSendHandlerImpl implements AudioSendHandler {
    private final AudioPlayer player;
    private AudioFrame frame;

    public AudioSendHandlerImpl(AudioPlayer player) {
        this.player = player;
    }

    @Override
    public byte[] provide20MsAudio() {
        return frame.getData();
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
            return false;
        }
        return frame != null;
    }
}
