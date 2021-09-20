package services;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Shedule {
    private static Shedule instance;
    private final ScheduledExecutorService scheduledExecutorService;

    private Shedule(int corePoolSize) {
        scheduledExecutorService = new ScheduledThreadPoolExecutor(corePoolSize, new ThreadFactoryBuilder().setDaemon(true).build());
    }

    public static Shedule getInstance() {
        if (instance == null) {
            instance = new Shedule(5);
        }
        return instance;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
}
