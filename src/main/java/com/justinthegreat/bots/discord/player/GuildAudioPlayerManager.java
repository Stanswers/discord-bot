package com.justinthegreat.bots.discord.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.lava.common.tools.DaemonThreadFactory;
import com.sedmelluq.lava.common.tools.ExecutorTools;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class GuildAudioPlayerManager extends DefaultAudioPlayerManager {
    private static final long CHECK_INTERVAL = TimeUnit.SECONDS.toMillis(10);
    private static final long DEFAULT_CLEANUP_THRESHOLD = TimeUnit.SECONDS.toMillis(30);
    private static final GuildAudioPlayerManager AUDIO_PLAYER_MANAGER = new GuildAudioPlayerManager();

    static {
        AudioSourceManagers.registerRemoteSources(AUDIO_PLAYER_MANAGER);
        AUDIO_PLAYER_MANAGER.registerSourceManager(new LocalAudioSourceManager());
    }

    private final Logger logger = LoggerFactory.getLogger(GuildAudioPlayerManager.class);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, new DaemonThreadFactory("manager"));
    private final AudioPlayerLifecycleManager lifecycleManager = new AudioPlayerLifecycleManager(scheduledExecutorService);

    private TLongObjectHashMap<GuildAudioPlayer> players = new TLongObjectHashMap<>();

    private GuildAudioPlayerManager() {
        super();
        lifecycleManager.initialise();
    }

    public static GuildAudioPlayerManager getInstance() {
        return AUDIO_PLAYER_MANAGER;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        lifecycleManager.shutdown();
        ExecutorTools.shutdownExecutor(scheduledExecutorService, "scheduled operations");
    }

    public GuildAudioPlayer getAudioPlayer(Guild guild) {
        GuildAudioPlayer player;
        synchronized (players) {
            player = players.get(guild.getIdLong());
            if (player != null) {
                return player;
            }
            player = new GuildAudioPlayer(createPlayer(), guild);
            players.put(guild.getIdLong(), player);
        }
        return player;
    }

    public Future<Void> loadItem(String identifier, Guild guild, VoiceChannel voiceChannel, MessageChannel channel) {
        GuildAudioPlayer player = getAudioPlayer(guild);
        return super.loadItem(identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                player.trackLoaded(track, voiceChannel);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // TODO: Implement this
                logger.info("Got a playlist loaded: " + playlist);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Failed to play sound").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Failed to play sound").queue();
            }
        });
    }

    public class AudioPlayerLifecycleManager implements Runnable {

        private final ScheduledExecutorService scheduler;
        private final AtomicReference<ScheduledFuture<?>> scheduledTask;

        public AudioPlayerLifecycleManager(ScheduledExecutorService scheduler) {
            this.scheduler = scheduler;
            this.scheduledTask = new AtomicReference<>();
        }

        public void initialise() {
            ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(this, CHECK_INTERVAL, CHECK_INTERVAL, TimeUnit.MILLISECONDS);
            if (!scheduledTask.compareAndSet(null, task)) {
                task.cancel(false);
            }
        }

        public void shutdown() {
            ScheduledFuture<?> task = scheduledTask.getAndSet(null);
            if (task != null) {
                task.cancel(false);
            }
        }

        @Override
        public void run() {
            synchronized (players) {
                for (GuildAudioPlayer player : players.valueCollection()) {
                    player.checkIdle(DEFAULT_CLEANUP_THRESHOLD);
                }
            }
        }
    }
}
