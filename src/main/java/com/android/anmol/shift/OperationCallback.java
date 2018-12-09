package com.android.anmol.shift;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class OperationCallback<T> implements Handler.Callback {

    protected abstract void onSuccess(T data);

    protected abstract void onError(String errorCause);

    @SuppressWarnings({"unchecked"})
    @Override
    public final boolean handleMessage(Message msg) {
        Bundle bundle = msg.getData();
        DataHandler<T> dataHandler = new DataHandler<>();
        if (DataHandler.isSuccess(bundle)) {
            onSuccess(dataHandler.parse(bundle));
        } else {
            onError(dataHandler.parseError(bundle));
        }
        return true;
    }
}