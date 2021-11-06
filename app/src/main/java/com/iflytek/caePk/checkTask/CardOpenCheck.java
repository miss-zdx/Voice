package com.iflytek.caePk.checkTask;

import com.iflytek.caePk.MainApp;
import com.iflytek.caePk.R;
import com.iflytek.caePk.checkTask.base.BaseCheckTask;
import com.iflytek.caePk.checkTask.base.CheckType;
import com.voice.caePk.CaeOperator;
import com.voice.osCaeHelper.CheckResult;

public class CardOpenCheck extends BaseCheckTask {

    @Override
    public void checkHandler() {
        CheckResult openAndStartRecord = CaeOperator.getInstance().openAndStartRecord(-1);
        if (openAndStartRecord.state) {
            checkDeviceBean.checkResultStatus = 1;
        } else {
            checkDeviceBean.checkResultStatus = 0;
        }
        checkDeviceBean.checkResult = openAndStartRecord.msg;
    }

    @Override
    public void checkStart() {
        checkDeviceBean.checkType = CheckType.CARD_OPEN;
        checkDeviceBean.checkTitle = MainApp.getMainApp().getString(R.string.check_open_card);
    }
}
