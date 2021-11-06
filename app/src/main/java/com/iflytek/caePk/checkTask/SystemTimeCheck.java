package com.iflytek.caePk.checkTask;

import com.iflytek.caePk.MainApp;
import com.iflytek.caePk.R;
import com.iflytek.caePk.checkTask.base.BaseCheckTask;
import com.iflytek.caePk.checkTask.base.CheckType;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SystemTimeCheck extends BaseCheckTask {
    private long timeValid = 1618979930000l;  // 2021-04-21

    @Override
    public void checkHandler() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        long currentTimeMillis = System.currentTimeMillis();
        String format = simpleDateFormat.format(currentTimeMillis);
        checkDeviceBean.checkResult =
                String.format(MainApp.getMainApp().getString(R.string.check_time_result), format);
        if (currentTimeMillis - timeValid > 0) {
            checkDeviceBean.checkResultStatus = 1;
        } else {
            checkDeviceBean.checkResultStatus = 0;
        }
    }

    @Override
    public void checkStart() {
        checkDeviceBean.checkType = CheckType.TIME;
        checkDeviceBean.checkTitle = MainApp.getMainApp().getString(R.string.check_time_title);
    }
}
