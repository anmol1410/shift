package com.android.anmol.shift;

/**
 * Base class to perform the DB operations on the worker thread.
 * <br><br>
 * It will trigger the completion event to the callback provided {@link OperationCallback OperationCallback}'s
 * {@link OperationCallback#onSuccess(Object)} and {@link OperationCallback#onError(String)} respectively.
 * <br>
 */
public abstract class OperationExecutorSerial<T> implements OperationAsync {

    private OperationCallback<T> mOperationCallback;

    protected OperationExecutorSerial(OperationCallback<T> operationCallback) {
        mOperationCallback = operationCallback;
    }

    @Override
    public final void execute() {
        OperationExecutor.INSTANCE.enqueue(new Runnable() {
            @Override
            public void run() {
                OperationExecutor.INSTANCE.onExecution(perform(), mOperationCallback);
            }
        });
    }

    protected void sendError(String errorType) {
        OperationExecutor.INSTANCE.notifyError(errorType);
    }

    protected abstract T perform();
}