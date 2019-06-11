package com.nexless.devicecollect.httpservice;

import com.google.gson.JsonSyntaxException;
import com.nexless.devicecollect.AppConstants;
import com.nexless.devicecollect.R;
import com.nexless.devicecollect.model.TResponse;
import com.nexless.devicecollect.model.TResponseNoData;
import com.nexless.devicecollect.util.AppPreference;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Calm on 2018/1/30.
 * RxHelper Rx单例类
 */

public class RxHelper {
    private HashMap<String, CompositeDisposable> mTaskDisposable = new HashMap<>();

    private static class RxHelperHolder {
        private static RxHelper instance = new RxHelper();
    }

    private RxHelper() {

    }

    public static RxHelper getInstance() {
        return RxHelperHolder.instance;
    }

    private void addTaskDisposable(String tag, Disposable disposable) {
        if (mTaskDisposable.get(tag) != null) {
            mTaskDisposable.get(tag).add(disposable);
        } else {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(disposable);
            mTaskDisposable.put(tag, compositeDisposable);
        }
    }

    public <T> void sendRequest(String tag, Observable<TResponse<T>> observable, Consumer<TResponse<T>> onNext, Consumer<Throwable> onError) {
        Disposable disposable = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
        addTaskDisposable(tag, disposable);
    }

    public void sendRequestNoData(String tag, Observable<TResponseNoData> observable, Consumer<TResponseNoData> onNext, Consumer<Throwable> onError) {
        Disposable disposable = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
        addTaskDisposable(tag, disposable);
    }

    public void unSubscribeTask(String tag) {
        if (mTaskDisposable.get(tag) != null) {
            mTaskDisposable.get(tag).dispose();
            mTaskDisposable.remove(tag);
        }
    }

    public void unSubscribeTask(String tag, Disposable d) {
        if (mTaskDisposable.get(tag) != null) {
            mTaskDisposable.get(tag).remove(d);
            if (mTaskDisposable.get(tag).size() <= 0) {
                mTaskDisposable.remove(tag);
            }
        }
    }
//    /**
//     * 添加订阅
//     *
//     * @param tag
//     * @param eventClass
//     * @param onEvent
//     * @return
//     */
//    public <E>Disposable subscribeEvent(String tag, Class<E> eventClass, final Consumer<E> onEvent)
//    {
//        Disposable subscription = RxBus2.getInstance().doSubscribe(eventClass,onEvent);
//        if (mEventDisposable == null)
//        {
//            mEventDisposable = new HashMap<>();
//        }
//        if (mEventDisposable.get(tag) != null)
//        {
//            mEventDisposable.get(tag).add(subscription);
//        } else
//        {
//            CompositeDisposable compositeDisposable = new CompositeDisposable();
//            compositeDisposable.add(subscription);
//            mEventDisposable.put(tag, compositeDisposable);
//        }
//        return subscription;
//    }
//
//    /**
//     * 取消tag下对应的Subscription
//     *
//     * @param tag
//     * @param d
//     */
//    public void unsubscribeEvent(String tag, Disposable d)
//    {
//        if (mEventDisposable != null)
//        {
//            if (mEventDisposable.get(tag) != null)
//            {
//                mEventDisposable.get(tag).remove(d);
//            }
//        }
//    }
//
//    /**
//     * 取消tag对应的所有订阅
//     *
//     * @param tag
//     */
//    public void unsubscribeEvent(String tag)
//    {
//        if (mEventDisposable != null)
//        {
//            if (mEventDisposable.get(tag) != null)
//            {
//                mEventDisposable.get(tag).dispose();
//                mEventDisposable.remove(tag);
//            }
//        }
//    }

    public String getErrorInfo(Throwable throwable) {
        String errorInfo;
        if (throwable instanceof SocketTimeoutException) {
            errorInfo = AppPreference.getStringWithId(R.string.timeout_exception);
        } else if (throwable instanceof IOException) {
            errorInfo = AppPreference.getStringWithId(R.string.network_error);
        } else if (throwable instanceof JsonSyntaxException) {
            errorInfo = AppPreference.getStringWithId(R.string.data_format_error);
        } else {
            errorInfo = AppPreference.getStringWithId(R.string.default_request_error);
        }
        return errorInfo;
    }

    public String getResponseCodeName(String code) {

        String codeString;
        switch (code) {
            case AppConstants.RESPONSE_CODE_USERNAME_PWD_INCORRECT:
                codeString = "用户名或者密码错误";
                break;
            case AppConstants.RESPONSE_CODE_NOT_LOGIN:
                codeString = "用户未登录";
                break;
            case AppConstants.RESPONSE_CODE_DEVICE_NOT_EXIST:
                codeString = "设备不存在";
                break;
            default:
                codeString = "未知错误";
                break;
        }

        return codeString;
    }
}
