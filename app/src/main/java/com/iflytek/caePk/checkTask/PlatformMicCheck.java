package com.iflytek.caePk.checkTask;

import com.iflytek.caePk.BuildConfig;
import com.iflytek.caePk.MainApp;
import com.iflytek.caePk.R;
import com.iflytek.caePk.checkTask.base.BaseCheckTask;
import com.iflytek.caePk.checkTask.base.CheckType;

public class PlatformMicCheck extends BaseCheckTask {
    @Override
    public void checkHandler() {
        checkDeviceBean.checkResult = String.format(
                MainApp.getMainApp().getString(R.string.platform_mic),
                BuildConfig.Platform,
                BuildConfig.MicType
        );
        checkDeviceBean.checkResultStatus = 1;
    }

    @Override
    public void checkStart() {
        checkDeviceBean.checkType = CheckType.PLATFORM_MIC;
        checkDeviceBean.checkTitle = MainApp.getMainApp().getString(R.string.check_platform_mic);
    }
}
