package com.creek.common.interfaces;

public interface CommonCallBack<T,P> {
    void success(T t);

    void fail(P p);
}
