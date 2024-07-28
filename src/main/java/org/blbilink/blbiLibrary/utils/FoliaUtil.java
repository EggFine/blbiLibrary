package org.blbilink.blbiLibrary.utils;

import org.blbilink.blbiLibrary.BlbiLibrary;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class FoliaUtil {
    public Boolean isFolia = false;
    private final BlbiLibrary blbiLibrary = BlbiLibrary.blbiLibrary;
    private final Plugin plugin;

    public FoliaUtil(Plugin plugin) {
        this.plugin = plugin;
        this.isFolia = checkFolia(true);
    }

    public boolean checkFolia(boolean supportFolia) {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            if (supportFolia) {
                blbiLibrary.getLogger().info(AnsiColor.AQUA + "[√] 检测到兼容 Folia 核心，并使用与其兼容的插件 [" + plugin.getName() + "] ，正在为您加载" + AnsiColor.RESET);
                plugin.getLogger().info(AnsiColor.AQUA + "[√] 检测到您正在使用 Folia 服务端核心，插件 [" + plugin.getName() + "] 已对其兼容，放心使用" + AnsiColor.RESET);
            } else {
                blbiLibrary.getLogger().warning("[×] 检测到不兼容 Folia 核心的插件 [" + plugin.getName() + "] 请停用该插件！");
                plugin.getLogger().warning("[×] 插件 [" + plugin.getName() + "] 暂时尚未支持 Folia 请停用该插件！");
            }
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public void runTaskAsync(Plugin plugin, Runnable run) {
        if (!isFolia) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, run);
        } else {
            Executors.defaultThreadFactory().newThread(run).start();
        }
    }

    public Cancellable runTaskTimerAsync(Plugin plugin, Consumer<Cancellable> run, long delay, long period) {
        delay = Math.max(delay, 1L);
        period = Math.max(period, 1L);

        if (!isFolia) {
            BukkitRunnable bukkitRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    run.accept(() -> this.cancel());
                }
            };
            BukkitTask task = bukkitRunnable.runTaskTimerAsynchronously(plugin, delay, period);
            return new BukkitCancellable(task);
        } else {
            try {
                Method getSchedulerMethod = Server.class.getDeclaredMethod("getGlobalRegionScheduler");
                getSchedulerMethod.setAccessible(true);
                Object globalRegionScheduler = getSchedulerMethod.invoke(Bukkit.getServer());
                Class<?> schedulerClass = globalRegionScheduler.getClass();
                Method executeMethod = schedulerClass.getDeclaredMethod("runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class);
                executeMethod.setAccessible(true);

                CancellableTask cancellableTask = new CancellableTask();
                Consumer<Object> wrappedConsumer = (t) -> {
                    if (!cancellableTask.isCancelled()) {
                        run.accept(cancellableTask);
                    }
                };

                Object task = executeMethod.invoke(globalRegionScheduler, plugin, wrappedConsumer, delay, period);
                cancellableTask.setTask(task);

                return cancellableTask;
            } catch (Exception e) {
                e.printStackTrace();
                return DummyCancellable.INSTANCE;
            }
        }
    }

    public void runTask(Plugin plugin, Consumer<Object> run) {
        if (!isFolia) {
            Bukkit.getScheduler().runTask(plugin, () -> run.accept(null));
        } else {
            try {
                Method getSchedulerMethod = Server.class.getDeclaredMethod("getGlobalRegionScheduler");
                getSchedulerMethod.setAccessible(true);
                Object globalRegionScheduler = getSchedulerMethod.invoke(Bukkit.getServer());
                Class<?> schedulerClass = globalRegionScheduler.getClass();
                Method executeMethod = schedulerClass.getDeclaredMethod("run", Plugin.class, Consumer.class);
                executeMethod.setAccessible(true);
                executeMethod.invoke(globalRegionScheduler, plugin, run);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void runTaskForEntity(Entity entity, Plugin plugin, Runnable run, Runnable retired, long delay) {
        delay = Math.max(delay, 1L);
        if (!isFolia) {
            Bukkit.getScheduler().runTaskLater(plugin, run, delay);
        } else {
            try {
                Method getSchedulerMethod = Entity.class.getDeclaredMethod("getScheduler");
                Object entityScheduler = getSchedulerMethod.invoke(entity);
                Class<?> schedulerClass = entityScheduler.getClass();
                Method executeMethod = schedulerClass.getMethod("execute", Plugin.class, Runnable.class, Runnable.class, long.class);
                executeMethod.invoke(entityScheduler, plugin, run, retired, delay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void runTaskLater(Plugin plugin, Runnable task, long delay) {
        delay = Math.max(delay, 1L);
        if (!isFolia) {
            Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        } else {
            try {
                Method getSchedulerMethod = Server.class.getDeclaredMethod("getGlobalRegionScheduler");
                getSchedulerMethod.setAccessible(true);
                Object globalRegionScheduler = getSchedulerMethod.invoke(Bukkit.getServer());
                Class<?> schedulerClass = globalRegionScheduler.getClass();
                Method executeMethod = schedulerClass.getDeclaredMethod("runDelayed", Plugin.class, Runnable.class, long.class);
                executeMethod.setAccessible(true);
                executeMethod.invoke(globalRegionScheduler, plugin, task, delay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface Cancellable {
        void cancel();
    }

    private class CancellableTask implements Cancellable {
        private Object task;
        private boolean cancelled = false;

        public void setTask(Object task) {
            this.task = task;
        }

        @Override
        public void cancel() {
            if (task != null && !cancelled) {
                try {
                    Method cancelMethod = task.getClass().getDeclaredMethod("cancel");
                    cancelMethod.setAccessible(true);
                    cancelMethod.invoke(task);
                    cancelled = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cancelled = true;
            }
        }

        public boolean isCancelled() {
            return cancelled;
        }
    }

    private static class BukkitCancellable implements Cancellable {
        private final BukkitTask task;

        BukkitCancellable(BukkitTask task) {
            this.task = task;
        }

        @Override
        public void cancel() {
            task.cancel();
        }
    }

    private static class DummyCancellable implements Cancellable {
        static final DummyCancellable INSTANCE = new DummyCancellable();

        @Override
        public void cancel() {
            // Do nothing
        }
    }
}