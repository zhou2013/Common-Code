package zzhao.code.concurrent;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

/**
 * 从netty那边看到的，监控线程是不是死了,如果死了就执行一下指定的代码
 * @author zzhao
 * @version 2014-7-11
 */
public class ThreadDeathWatcher {
    private static final Logger logger = Logger.getLogger(ThreadDeathWatcher.class);

    private static final LinkedBlockingDeque<Entry> pendingEntries = new LinkedBlockingDeque<Entry>();
    private static final Watcher watcher = new Watcher();
    private static final AtomicBoolean started = new AtomicBoolean();
    private static volatile Thread watcherThread;

    /**
     * Schedules the specified {@code task} to run when the specified {@code thread} dies.
     *
     * @param thread the {@link Thread} to watch
     * @param task the {@link Runnable} to run when the {@code thread} dies
     *
     * @throws IllegalArgumentException if the specified {@code thread} is not alive
     */
    public static void watch(Thread thread, Runnable task) {
        if (thread == null) {
            throw new NullPointerException("thread");
        }
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (!thread.isAlive()) {
            throw new IllegalArgumentException("thread must be alive.");
        }

        pendingEntries.add(new Entry(thread, task));

        if (started.compareAndSet(false, true)) {
            Thread watcherThread = new Thread(watcher);
            thread.setDaemon(true);
            watcherThread.start();
            ThreadDeathWatcher.watcherThread = watcherThread;
        }
    }

    /**
     * Waits until the thread of this watcher has no threads to watch and terminates itself.
     * Because a new watcher thread will be started again on {@link #watch(Thread, Runnable)},
     * this operation is only useful when you want to ensure that the watcher thread is terminated
     * <strong>after</strong> your application is shut down and there's no chance of calling
     * {@link #watch(Thread, Runnable)} afterwards.
     *
     * @return {@code true} if and only if the watcher thread has been terminated
     */
    public boolean awaitInactivity(long timeout, TimeUnit unit) throws InterruptedException {
        if (unit == null) {
            throw new NullPointerException("unit");
        }

        Thread watcherThread = ThreadDeathWatcher.watcherThread;
        if (watcherThread != null) {
            watcherThread.join(unit.toMillis(timeout));
        }
        return !watcherThread.isAlive();
    }

    private ThreadDeathWatcher() {
    }

    private static final class Watcher implements Runnable {

        private final List<Entry> watchees = Lists.newArrayList();

        @Override
        public void run() {
            for (;;) {
                fetchWatchees();
                notifyWatchees();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {
                    // Ignore the interrupt; do not terminate until all tasks are run.
                }

                if (watchees.isEmpty() && pendingEntries.isEmpty()) {

                    boolean stopped = started.compareAndSet(true, false);
                    assert stopped;

                    if (pendingEntries.isEmpty()) {
                        break;
                    }

                    if (!started.compareAndSet(false, true)) {
                        break;
                    }
                }
            }
        }

        private void fetchWatchees() {
            for (;;) {
                Entry e = pendingEntries.poll();
                if (e == null) {
                    break;
                }

                watchees.add(e);
            }
        }

        private void notifyWatchees() {
            Iterator<Entry> itor = this.watchees.iterator();
            while (itor.hasNext()) {
                Entry e = itor.next();
                if (!e.thread.isAlive()) {
                    itor.remove();
                    try {
                        e.task.run();
                    } catch (Throwable t) {
                        logger.warn("Thread death watcher task raised an exception:", t);
                    }
                }
            }
        }
    }

    private static final class Entry {
        final Thread thread;
        final Runnable task;

        Entry(Thread thread, Runnable task) {
            this.thread = thread;
            this.task = task;
        }
    }
}
