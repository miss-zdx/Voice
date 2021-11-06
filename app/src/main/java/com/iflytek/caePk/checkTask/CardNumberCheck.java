package com.iflytek.caePk.checkTask;

import com.iflytek.caePk.MainApp;
import com.iflytek.caePk.R;
import com.iflytek.caePk.checkTask.base.BaseCheckTask;
import com.iflytek.caePk.checkTask.base.CheckType;
import com.iflytek.caePk.utils.ShellUtils;

public class CardNumberCheck extends BaseCheckTask {
    @Override
    public void checkHandler() {
        int fetchCards = ShellUtils.fetchCards();
        if (fetchCards < 0) {
            checkDeviceBean.checkResultStatus = 0;
            boolean haveRoot = ShellUtils.haveRoot();
            if (haveRoot) {
                checkDeviceBean.checkResult = MainApp.getMainApp().getString(R.string.check_cards_num_no_exist);
            } else {
                checkDeviceBean.checkResult = MainApp.getMainApp().getString(R.string.system_no_root_permission);
            }
        } else {
            checkDeviceBean.checkResultStatus = 1;
            checkDeviceBean.checkResult = String.format(
                    MainApp.getMainApp().getString(
                            R.string.check_cards_num_result
                    ), fetchCards
            );
        }
    }

    @Override
    public void checkStart() {
        checkDeviceBean.checkType = CheckType.CARD_NUMBER;
        checkDeviceBean.checkTitle = MainApp.getMainApp().getString(R.string.check_cards_num_title);
    }
}
