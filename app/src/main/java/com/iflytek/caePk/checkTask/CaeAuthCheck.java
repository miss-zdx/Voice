package com.iflytek.caePk.checkTask;

import com.iflytek.caePk.MainApp;
import com.iflytek.caePk.R;
import com.iflytek.caePk.checkTask.base.BaseCheckTask;
import com.iflytek.caePk.checkTask.base.CheckType;
import com.voice.osCaeHelper.CaeCoreHelper;
import com.voice.osCaeHelper.CheckResult;

public class CaeAuthCheck extends BaseCheckTask {
    @Override
    public void checkHandler() {
        CheckResult caeEngineInit = CaeCoreHelper.getCaeOperateHelp().caeEngineInit();
        if (!caeEngineInit.state) {
            caeEngineInit = CaeCoreHelper.getCaeOperateHelp().caeEngineInit();
        }

        if (caeEngineInit.state) {
            checkDeviceBean.checkResultStatus = 1;
        } else {
            checkDeviceBean.checkResultStatus = 0;

        }
        StringBuilder builder = new StringBuilder();

     /*   File(CaeCoreHelper.mIniHlwPath).readLines(Charset.forName("utf-8")).filter {
            it.contains("CAE_RES") || it.contains("IVW_RES")
        }.forEach {
            if (it.contains("CAE_RES")) {
                val split = it.split(";")
                builder.append(split[0].trim()).append(",")
            } else {
                builder.append(it).append(",")
            }
        }
        File(CaeCoreHelper.mCaeModelPath).readLines(Charset.forName("utf-8")).filter {
            it.contains("nMicNum")
        }.forEach {
            builder.append(it)
        }*/
        checkDeviceBean.checkResult = caeEngineInit.msg;
    }

    @Override
    public void checkStart() {
        checkDeviceBean.checkType = CheckType.CAE_AUTH;
        checkDeviceBean.checkTitle = MainApp.getMainApp().getString(R.string.check_cae_auth);
    }
}
