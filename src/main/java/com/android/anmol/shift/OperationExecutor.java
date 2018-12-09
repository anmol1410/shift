package com.android.anmol.shift;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

enum OperationExecutor implements Cloneable, IOperationCallback {

    INSTANCE;
    /**
     * Handles the messages/runnable in its own separate MessageQueue on worker thread.
     */
    private DbHandler mDbHandler;
    private ThreadPoolExecutor mThreadPoolExecutor;
    private ThreadLocal<String> mAsyncOpError = new ThreadLocal<>();

    void enqueue(Runnable runnable) {
        if (mDbHandler == null) {
            HandlerThread thread = new HandlerThread("DB_HANDLER_THREAD");
            thread.start();
            Looper looper = thread.getLooper();
            mDbHandler = new DbHandler(looper);
        }
        if (runnable != null) {
            mDbHandler.post(runnable);
        }
    }

    void execute(Runnable runnable) {
        if (mThreadPoolExecutor == null) {
            final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
            // We want at least 2 threads and at most 4 threads in the core pool,
            // preferring to have 1 less than the CPU count to avoid saturating
            // the CPU with background work
            mThreadPoolExecutor = new ThreadPoolExecutor(
                    Math.max(2, Math.min(CPU_COUNT - 1, 4)),
                    CPU_COUNT * 2 + 1,
                    30, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(128));
        }
        if (runnable != null) {
            mThreadPoolExecutor.execute(runnable);
        }
    }

    @Override
    public <T> void onExecution(T response, OperationCallback operationCallback) {
        if (operationCallback != null) {
            Message message = Message.obtain();
            message.setData(DataHandler.create(response, mAsyncOpError.get()));
            mAsyncOpError.set(null);
            new Handler(Looper.getMainLooper(), operationCallback).sendMessage(message);
        }
    }

    void notifyError(String error) {
        mAsyncOpError.set(error);
        // Serial one runs in separate thread than threads in Thread pool.
        // So we can use same variable to store errors, as it will correspond to different threads,
        // so can solve our purpose of both serial/parallel operation executions.
    }

    final class DbHandler extends Handler {
        DbHandler(Looper looper) {
            super(looper);
        }
    }
}