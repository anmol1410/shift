package com.android.anmol.shift;

interface IOperationCallback {
    <T> void onExecution(T response, OperationCallback operationCallback);
}
