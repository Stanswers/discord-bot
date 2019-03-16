package com.justinthegreat.bots.discord.command;

import com.justinthegreat.bots.discord.BotRuntime;
import com.justinthegreat.bots.discord.handlers.AudioSendHandlerImpl;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

public abstract class PlayAudioEventHandler implements CommandEventHandler {
    abstract protected String getUrl(MessageReceivedEvent event, String[] args);
    abstract protected boolean handleNonAudioEvent(MessageReceivedEvent event, String[] args);

    @Override
    public void handleEvent(MessageReceivedEvent event, String[] args) {
        Member member = event.getMember();
        if (member == null) {
            return;
        }
        if (handleNonAudioEvent(event, args)) {
            return;
        }
        Guild guild = event.getGuild();
        if (guild == null) {
            return;
        }
        AudioManager manager = guild.getAudioManager();
        if (manager == null) {
            return;
        }
        AudioPlayer player = BotRuntime.getInstance().getOrCreateAudioPlayer(guild);
        if (args.length == 1 && player.isPaused()) {
            player.setPaused(false);
        }
        MessageChannel channel = event.getChannel();
        VoiceChannel voiceChannel = member.getVoiceState().getChannel();
        if (voiceChannel == null) {
            // User is not connected to a voice channel
            channel.sendMessage("Connect to a voice channel first!!!").queue();
            return;
        }
        String url = getUrl(event, args);
        if (url == null) {
            return;
        }
        BotRuntime.getInstance().getAudioPlayerManager().loadItem(url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                manager.setSendingHandler(new AudioSendHandlerImpl(player));
                if (manager.getConnectedChannel() == null || manager.getConnectedChannel().getIdLong() != voiceChannel.getIdLong()) {
                    manager.openAudioConnection(voiceChannel);
                }
                player.playTrack(track);
                if (player.isPaused()) {
                    player.setPaused(false);
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Failed to play sound").queue();
                if (manager.getConnectedChannel() != null) {
                    manager.closeAudioConnection();
                }
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                if (manager.getConnectedChannel() != null) {
                    manager.closeAudioConnection();
                }
            }
        });
        return;
    }
}
