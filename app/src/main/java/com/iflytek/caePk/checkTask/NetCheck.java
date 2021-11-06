package com.iflytek.caePk.checkTask;

import com.iflytek.caePk.MainApp;
import com.iflytek.caePk.R;
import com.iflytek.caePk.checkTask.base.BaseCheckTask;
import com.iflytek.caePk.checkTask.base.CheckType;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetCheck extends BaseCheckTask {
    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build();

    @Override
    public void checkHandler() {
        try {
            Request request = new Request.Builder().url("http://www.baidu.com").build();
            Response execute = okHttpClient.newCall(request).execute();
            if (execute.isSuccessful()) {
                checkDeviceBean.checkResult = MainApp.getMainApp().getString(R.string.check_net_result_success);
                checkDeviceBean.checkResultStatus = 1;
            } else {
                checkDeviceBean.checkResult = MainApp.getMainApp().getString(R.string.check_net_result_error);
                checkDeviceBean.checkResultStatus = 0;
            }
        } catch (Exception e) {
            checkDeviceBean.checkResult = MainApp.getMainApp().getString(R.string.check_net_result_error);
            checkDeviceBean.checkResultStatus = 0;
        }
    }

    @Override
    public void checkStart() {
        checkDeviceBean.checkType = CheckType.NET;
        checkDeviceBean.checkTitle = MainApp.getMainApp().getString(R.string.check_net_title);
    }
}
