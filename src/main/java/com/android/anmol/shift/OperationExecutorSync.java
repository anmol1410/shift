package com.android.anmol.shift;

/**
 * It basically does nothing fancy,
 * but can be used so that all the operations are compatible with their design APIs.
 * <p>
 * It performs the Operation on the Main thread, so take care that not much extensible work is being done here.
 * <p>
 * Or can you {@link OperationExecutorAsync} in that case.
 *
 * @param <T> Value to return.
 */
public abstract class OperationExecutorSync<T> implements OperationSync {

    @Override
    public final T execute() {
        return perform();
    }

    protected abstract T perform();
}