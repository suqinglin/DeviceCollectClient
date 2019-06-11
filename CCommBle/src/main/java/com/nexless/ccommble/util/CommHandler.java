package com.nexless.ccommble.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * @date: 2019/1/22
 * @author: su qinglin
 * @description: 弱引用Handler，为避免内存泄漏
 */
public class CommHandler extends Handler {
    public interface MessageHandler {
        void handleMessage(Message msg);
    }

    private WeakReference<MessageHandler> mMessageHandler;

    public CommHandler(MessageHandler msgHandler) {
        mMessageHandler = new WeakReference<>(msgHandler);
    }

    public CommHandler(Looper looper, MessageHandler msgHandler) {
        super(looper);
        mMessageHandler = new WeakReference<>(msgHandler);
    }

    @Override
    public void handleMessage(Message msg) {
        MessageHandler realHandler = mMessageHandler.get();
        if (realHandler != null) {
            realHandler.handleMessage(msg);
        }
    }
}
