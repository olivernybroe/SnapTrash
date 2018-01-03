package dk.snaptrash.snaptrash.Services.SnapTrash;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

class ThreadPerTaskExecutor implements Executor {
    public void execute(@NonNull Runnable r) {
        new Thread(r).start();
    }
}