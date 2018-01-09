package dk.snaptrash.snaptrash.Services.SnapTrash;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public class ThreadPerTaskExecutor implements Executor {
    public static final ThreadPerTaskExecutor INSTANCE = new ThreadPerTaskExecutor();

    public void execute(@NonNull Runnable r) {
        new Thread(r).start();
    }
}