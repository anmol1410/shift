package com.android.anmol.shift;

/**
 * Base class to perform the operations on the worker thread.
 * <br>
 * It will trigger the completion event to the callback provided {@link OperationCallback OperationCallback}'s
 * {@link OperationCallback#onSuccess(T data)} and {@link OperationCallback#onError(String error)} respectively.
 * <br>
 */
public abstract class OperationExecutorAsync<T> implements OperationAsync<T> {

    private OperationCallback<T> mOperationCallback;

    protected OperationExecutorAsync(OperationCallback<T> operationCallback) {
        mOperationCallback = operationCallback;
    }

    @Override
    public final void execute() {
        OperationExecutor.INSTANCE.execute(new Runnable() {
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