package com.justinthegreat.bots.discord.player;

import com.justinthegreat.bots.discord.audio.AudioSendHandlerImpl;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class GuildAudioPlayerManager extends DefaultAudioPlayerManager {
    private static final long CHECK_INTERVAL = TimeUnit.SECONDS.toMillis(10);
    private static final int DEFAULT_CLEANUP_THRESHOLD = (int) TimeUnit.SECONDS.toMillis(30);

    // private final Logger logger = LoggerFactory.getLogger(GuildAudioPlayerManager.class);
    // private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, new DaemonThreadFactory("manager"));
    // private final AudioPlayerLifecycleManager lifecycleManager = new AudioPlayerLifecycleManager(scheduledExecutorService);

    private TLongObjectHashMap<AudioPlayer> players = new TLongObjectHashMap<>();

    public GuildAudioPlayerManager() {
        // lifecycleManager.initialise();
    }

    @Override
    public void shutdown() {
        // lifecycleManager.shutdown();
        // ExecutorTools.shutdownExecutor(scheduledExecutorService, "scheduled operations");
    }

    public AudioPlayer getAudioPlayer(Guild guild) {
        AudioPlayer player;
        synchronized (players) {
            player = players.get(guild.getIdLong());
            if (player != null) {
                return player;
            }
            player = createPlayer();
            players.put(guild.getIdLong(), player);
        }
        AudioManager manager = guild.getAudioManager();
        player.addListener(new AudioEventAdapter() {
            @Override
            public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
                player.stopTrack();
                if (manager != null && manager.getConnectedChannel() != null) {
                    manager.closeAudioConnection();
                }
            }

            @Override
            public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
                switch (endReason) {
                    case STOPPED:
                    case FINISHED:
                    case CLEANUP:
                        if (manager != null && manager.getConnectedChannel() != null) {
                            manager.closeAudioConnection();
                        }
                        break;
                    case LOAD_FAILED:
                    case REPLACED:
                    default:
                        break;
                }
            }
        });
        return player;
    }

    public Future<Void> loadItem(String identifier, Guild guild, VoiceChannel voiceChannel, MessageChannel channel) {
        AudioPlayer player = getAudioPlayer(guild);
        AudioManager manager = guild.getAudioManager();
        return super.loadItem(identifier, new AudioLoadResultHandler() {
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
                for (AudioPlayer player : players.valueCollection()) {
                    // TODO: checkCleanUp doesn't do what I wan't it todo.  Have to write a wrapper around the
                    //       AudioPlayer to keep track of last send time, so I can disconnect from channel if player is idle
                    player.checkCleanup(DEFAULT_CLEANUP_THRESHOLD);
                }
            }
        }
    }
}
