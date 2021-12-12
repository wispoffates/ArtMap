package me.Fupery.ArtMap.Utils;

import me.Fupery.ArtMap.ArtMap;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class Scheduler {
    //todo add checks that ArtMap isn't disabled
    private final ArtMap plugin;
    public TaskScheduler SYNC = new TaskScheduler() {
        @Override
        public BukkitTask run(Runnable runnable) {
            return Bukkit.getScheduler().runTask(plugin, runnable);
        }

        @Override
        public BukkitTask runLater(Runnable runnable, int delay) {
            return Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        }

        @Override
        public BukkitTask runTimer(Runnable runnable, int startDelay, int period) {
            return Bukkit.getScheduler().runTaskTimer(plugin, runnable, startDelay, period);
        }
    };
    public TaskScheduler ASYNC = new TaskScheduler() {
        @Override
        public BukkitTask run(Runnable runnable) {
            return Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        }

        @Override
        public BukkitTask runLater(Runnable runnable, int delay) {
            return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
        }

        @Override
        public BukkitTask runTimer(Runnable runnable, int startDelay, int period) {
            return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, startDelay, period);
        }
    };

    public Scheduler(ArtMap plugin) {
        this.plugin = plugin;
    }

    public TaskHandler getTaskHandler(BukkitRunnable runnable) {
        return new TaskHandler(runnable);
    }

    public void runSafely(Runnable runnable) {
        if (!Bukkit.getServer().isPrimaryThread()) {
            SYNC.run(runnable);
        } else {
            runnable.run();
        }
    }

    public <T> T callSync(Callable<T> callable) {
        if (!Bukkit.getServer().isPrimaryThread()) {
            final BukkitFuture<T> future = new BukkitFuture<>(callable);
            future.run();
            synchronized (future.getLock()) {
                while (!future.isReady()) {
                    try {
                        future.getLock().wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            }

            return future.get();
        }
        
        try {
            return callable.call();
        } catch (Exception e) {
            ArtMap.instance().getLogger().log(Level.SEVERE,"Error in sync call!",e);
            return null;
        }
    }

    public interface TaskScheduler {
        BukkitTask run(Runnable runnable);

        BukkitTask runLater(Runnable runnable, int delay);

        BukkitTask runTimer(Runnable runnable, int startDelay, int period);
    }

    public class TaskHandler {
        private BukkitRunnable runnable;

        private TaskHandler(BukkitRunnable runnable) {
            this.runnable = runnable;
        }

        public void run(boolean async) {
            if (async) runnable.runTaskAsynchronously(plugin);
            else runnable.runTask(plugin);
        }

        public void runLater(boolean async, int delay) {
            if (async) runnable.runTaskLaterAsynchronously(plugin, delay);
            else runnable.runTaskLater(plugin, delay);
        }

        public void runTimer(boolean async, int startDelay, int period) {
            if (async) runnable.runTaskTimerAsynchronously(plugin, startDelay, period);
            else runnable.runTaskTimer(plugin, startDelay, period);
        }
    }

    public static class BukkitFuture<t> {
        private final AtomicBoolean isReady;
        private final AtomicReference<t> reference;
        private final Object lock;
        private final Callable<t> callable;

        BukkitFuture(Callable<t> callable) {
            this.isReady = new AtomicBoolean(false);
            this.reference = new AtomicReference<>(null);
            this.lock = new Object();
            this.callable = callable;
        }

        void run() {
            ArtMap.instance().getScheduler().SYNC.run(() -> {
                synchronized (lock) {
                    try {
                        reference.set(callable.call());
                    } catch (Exception e) {
                        ArtMap.instance().getLogger().log(Level.SEVERE,"Error in sync call!",e);
                    }
                    lock.notifyAll();
                }
            });
        }

        t get() {
            return reference.get();
        }

        Object getLock() {
            return lock;
        }

        boolean isReady() {
            return isReady.get();
        }
    }
}
