package com.iflytek.caePk.checkTask;

import android.text.TextUtils;

import com.iflytek.caePk.MainApp;
import com.iflytek.caePk.R;
import com.iflytek.caePk.checkTask.base.BaseCheckTask;
import com.iflytek.caePk.checkTask.base.CheckType;
import com.iflytek.caePk.utils.ShellUtils;

public class CardPermissionCheck extends BaseCheckTask {
    @Override
    public void checkHandler() {
        String fetchCardPermission = ShellUtils.fetchCardPermission();
        if (!TextUtils.isEmpty(fetchCardPermission)) {
            checkDeviceBean.checkResult = String.format(
                    MainApp.getMainApp().getString(R.string.check_cards_permission_result), ShellUtils.cardN,
                    fetchCardPermission
            );
            // crwx rwx rwx
            char[] toCharArray = fetchCardPermission.toCharArray();
            if (toCharArray[1] == 'r' && toCharArray[2] == 'w' &&
                    toCharArray[4] == 'r' && toCharArray[5] == 'w' &&
                    toCharArray[7] == 'r' && toCharArray[8] == 'w'
            ) {
                checkDeviceBean.checkResultStatus = 1;
            } else {
                checkDeviceBean.checkResultStatus = 0;
            }
        } else {
            checkDeviceBean.checkResultStatus = 0;
            checkDeviceBean.checkResult =
                    MainApp.getMainApp().getString(R.string.check_cards_permission_result_none);
        }
    }

    @Override
    public void checkStart() {
        checkDeviceBean.checkType = CheckType.CARD_PERMISSION;
        checkDeviceBean.checkTitle = MainApp.getMainApp().getString(R.string.check_cards_permission_title);
    }
}
