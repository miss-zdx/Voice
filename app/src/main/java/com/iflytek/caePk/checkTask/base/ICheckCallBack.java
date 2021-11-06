package com.iflytek.caePk.checkTask.base;

import com.iflytek.caePk.bean.CheckDeviceBean;

public interface ICheckCallBack {
    void onCheckStart(CheckDeviceBean checkDeviceBean);

    void onCheckEnd(CheckDeviceBean checkDeviceBean);
}
