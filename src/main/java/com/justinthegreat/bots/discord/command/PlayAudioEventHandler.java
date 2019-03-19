package com.justinthegreat.bots.discord.command;

import com.justinthegreat.bots.discord.BotRuntime;
import com.justinthegreat.bots.discord.player.GuildAudioPlayer;
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
        GuildAudioPlayer player = BotRuntime.getInstance().getAudioPlayer(guild);
        VoiceChannel voiceChannel = member.getVoiceState().getChannel();
        if (args.length == 1 && player.isPaused()) {
            player.unPause(voiceChannel);
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
        BotRuntime.getInstance().loadItem(url, guild, voiceChannel, channel);
        return;
    }
}
