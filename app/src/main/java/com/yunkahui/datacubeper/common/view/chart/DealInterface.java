package com.yunkahui.datacubeper.common.view.chart;

/**
 * Created by clint on 2017/9/23.
 */

public interface DealInterface<T> {
    void success(T object);
    void failure(String error);
}