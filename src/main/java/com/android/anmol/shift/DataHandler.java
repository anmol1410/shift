package com.android.anmol.shift;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.util.ArrayList;

import static java.lang.annotation.RetentionPolicy.SOURCE;

final class DataHandler<T> {

    static Bundle create(@Nullable Object data, @Nullable String error) {
        Bundle bundle = new Bundle();
        if (error != null) {
            // If there is error, only pass the error to the callback.
            bundle.putString(DbCallbackType.ERROR, error);
        } else if (data == null) {
            bundle.putString(DbCallbackType.SUCCESS, DbCallbackType.DUMMY);
            setSuccess(bundle, TYPE.VOID);
        } else if (isArrayListOfType(data, Parcelable.class)) {
            //noinspection unchecked
            bundle.putParcelableArrayList(DbCallbackType.SUCCESS, (ArrayList<? extends Parcelable>) data);
            setSuccess(bundle, TYPE.ARRAY_LIST);
        } else if (data instanceof Parcelable) {
            bundle.putParcelable(DbCallbackType.SUCCESS, (Parcelable) data);
            setSuccess(bundle, TYPE.PARCELABLE);
        } else if (data instanceof Boolean) {
            bundle.putBoolean(DbCallbackType.SUCCESS, (Boolean) data);
            setSuccess(bundle, TYPE.BOOLEAN);
        } else if (data instanceof Integer) {
            bundle.putInt(DbCallbackType.SUCCESS, (Integer) data);
            setSuccess(bundle, TYPE.INT);
        } else if (data instanceof Double) {
            bundle.putDouble(DbCallbackType.SUCCESS, (Double) data);
            setSuccess(bundle, TYPE.DOUBLE);
        } else if (data instanceof String) {
            bundle.putString(DbCallbackType.SUCCESS, (String) data);
            setSuccess(bundle, TYPE.STRING);
        } else if (data instanceof Character) {
            bundle.putChar(DbCallbackType.SUCCESS, (Character) data);
            setSuccess(bundle, TYPE.CHAR);
        } else if (data instanceof Parcelable[]) {
            bundle.putParcelableArray(DbCallbackType.SUCCESS, (Parcelable[]) data);
            setSuccess(bundle, TYPE.PARCELABLE_ARRAY);
        } else if (isArrayListOfType(data, Integer.class)) {
            //noinspection unchecked
            bundle.putIntegerArrayList(DbCallbackType.SUCCESS, (ArrayList<Integer>) data);
            setSuccess(bundle, TYPE.INT_ARRRAYLIST);
        } else if (isArrayListOfType(data, String.class)) {
            //noinspection unchecked
            bundle.putStringArrayList(DbCallbackType.SUCCESS, (ArrayList<String>) data);
            setSuccess(bundle, TYPE.INT_ARRRAYLIST);
        } else if (data instanceof double[]) {
            //noinspection unchecked
            bundle.putDoubleArray(DbCallbackType.SUCCESS, (double[]) data);
            setSuccess(bundle, TYPE.DOUBLE_ARRAY);
        } else {
            throw new RuntimeException(data.getClass() + " data type is not supported, please pass Parcelable Objects instead.");
        }
        return bundle;
    }

    private static void setSuccess(Bundle bundle, @TYPE String type) {
        bundle.putString(DbCallbackType.SUCCESS_TYPE, type);
    }

    static boolean isSuccess(Bundle bundle) {
        return getDataType(bundle) != null;
    }

    private static String getDataType(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        return bundle.getString(DbCallbackType.SUCCESS_TYPE);
    }

    private static boolean isArrayListOfType(Object data, Class<?> clazz) {
        boolean isValidArrayList = false;
        if (data instanceof ArrayList) {
            ArrayList list = (ArrayList) data;
            for (Object element : list) {
                if (clazz.isAssignableFrom(element.getClass())) {
                    isValidArrayList = true;
                    break;
                }
            }
        }
        return isValidArrayList;
    }

    @SuppressWarnings("unchecked")
    final T parse(Bundle bundle) {
        T data = null;
        if (bundle != null) {
            String dataType = getDataType(bundle);
            if (dataType != null) {
                try {
                    switch (dataType) {
                        case TYPE.ARRAY_LIST:
                            data = (T) bundle.getParcelableArrayList(DbCallbackType.SUCCESS);
                            break;
                        case TYPE.STRING:
                            data = (T) bundle.getString(DbCallbackType.SUCCESS);
                            break;
                        case TYPE.PARCELABLE:
                            data = bundle.getParcelable(DbCallbackType.SUCCESS);
                            break;
                        case TYPE.BOOLEAN:
                            data = (T) Boolean.valueOf(bundle.getBoolean(DbCallbackType.SUCCESS));
                            break;
                        case TYPE.INT:
                            data = (T) Integer.valueOf(bundle.getInt(DbCallbackType.SUCCESS));
                            break;
                        case TYPE.DOUBLE:
                            data = (T) Double.valueOf(bundle.getDouble(DbCallbackType.SUCCESS));
                            break;
                        case TYPE.CHAR:
                            data = (T) Character.valueOf(bundle.getChar(DbCallbackType.SUCCESS));
                            break;
                        case TYPE.PARCELABLE_ARRAY:
                            data = (T) bundle.getParcelableArray(DbCallbackType.SUCCESS);
                            break;
                        case TYPE.INT_ARRRAYLIST:
                            data = (T) bundle.getIntegerArrayList(DbCallbackType.SUCCESS);
                            break;
                        case TYPE.DOUBLE_ARRAY:
                            data = (T) bundle.getDoubleArray(DbCallbackType.SUCCESS);
                            break;
                    }
                } catch (ClassCastException exc) {
                    Log.e(OperationCallback.class.getSimpleName(), "Error while Casting: " + exc);
                }
            }
        }
        return data;
    }

    String parseError(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        return bundle.getString(DbCallbackType.ERROR);
    }

    @Retention(SOURCE)
    @StringDef({
            DbCallbackType.DUMMY,
            DbCallbackType.ERROR,
            DbCallbackType.SUCCESS,
            DbCallbackType.SUCCESS_TYPE})
    @interface DbCallbackType {
        String DUMMY = "DUMMY";
        String ERROR = "MSG.ERROR";
        String SUCCESS = "MSG.SUCCESS";
        String SUCCESS_TYPE = "SUCCESS_TYPE";
    }

    @Retention(SOURCE)
    @StringDef({
            TYPE.VOID,
            TYPE.ARRAY_LIST,
            TYPE.STRING,
            TYPE.PARCELABLE,
            TYPE.BOOLEAN,
            TYPE.INT,
            TYPE.DOUBLE,
            TYPE.CHAR,
            TYPE.PARCELABLE_ARRAY,
            TYPE.INT_ARRRAYLIST,
            TYPE.DOUBLE_ARRAY})
    @interface TYPE {
        String ARRAY_LIST = "ARRAY_LIST";
        String STRING = "STRING";
        String VOID = "VOID";
        String PARCELABLE = "PARCELABLE";
        String BOOLEAN = "BOOLEAN";
        String INT = "INT";
        String DOUBLE = "DOUBLE";
        String CHAR = "CHAR";
        String PARCELABLE_ARRAY = "PARCELABLE_ARRAY";
        String INT_ARRRAYLIST = "INT_ARRRAYLIST";
        String DOUBLE_ARRAY = "DOUBLE_ARRAY";
    }
}
