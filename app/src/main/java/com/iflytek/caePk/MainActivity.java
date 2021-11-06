package com.iflytek.caePk;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.iflytek.caePk.adapter.CheckDeviceAdapter;
import com.iflytek.caePk.bean.CheckDeviceBean;
import com.iflytek.caePk.checkTask.CaeAuthCheck;
import com.iflytek.caePk.checkTask.CardNumberCheck;
import com.iflytek.caePk.checkTask.CardOpenCheck;
import com.iflytek.caePk.checkTask.CardPermissionCheck;
import com.iflytek.caePk.checkTask.NetCheck;
import com.iflytek.caePk.checkTask.PlatformMicCheck;
import com.iflytek.caePk.checkTask.SystemTimeCheck;
import com.iflytek.caePk.checkTask.TaskCheckMgr;
import com.iflytek.caePk.checkTask.base.BaseCheckTask;
import com.iflytek.caePk.checkTask.base.ICheckTaskCallBack;
import com.iflytek.caePk.utils.ActivityExt;
import com.iflytek.caePk.utils.ShellUtils;
import com.voice.caePk.CaeOperator;
import com.voice.osCaeHelper.CheckResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private final String[] permissionArray = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.REORDER_TASKS,
            Manifest.permission.READ_PHONE_STATE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkPermissions()) {
            initUIEvent();
        }
    }

    /**
     * 初始化UI相关
     */
    private List<CheckDeviceBean> checkList = new ArrayList<>();
    private CheckDeviceAdapter checkDeviceAdapter = new CheckDeviceAdapter(checkList);
    private ArrayList<BaseCheckTask> taskList = new ArrayList<>();

    private RecyclerView recyclerView;

    private void initUIEvent() {
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setAdapter(checkDeviceAdapter);

        TextView headVersion = findViewById(R.id.headVersion);
        headVersion.setText(String.format(getString(R.string.boot_check), ActivityExt.getAppVersionName(this)));
        startCheckTask();
    }

    private boolean isCheckSuccess = true;

    private void startCheckTask() {
        addCheckTask();
        TaskCheckMgr.getInstance().startCheckTask(new ICheckTaskCallBack() {
            @Override
            public void onAllTaskCheckEnd() {
                runOnUiThread(() -> {
                    for (BaseCheckTask it : taskList) {
                        if (!it.isCheckSuccess()) {
                            isCheckSuccess = false;
                        }
                    }
                    if (isCheckSuccess) {
                        startProgress();
                        checkDeviceAdapter.addFooterViewTo(isCheckSuccess, false);
                    } else {//如果失败
                        if (isHaveRoot) { //如果有权限，
                            checkDeviceAdapter.addFooterViewTo(isCheckSuccess, false);
                        } else {
                            if (!cardNumberCheck.isCheckSuccess() && !cardPermissionCheck.isCheckSuccess()
                                    && netCheck.isCheckSuccess() && caeAuthCheck.isCheckSuccess() /*&& systemTimeCheck.isCheckSuccess()*/
                            ) {
                                checkDeviceAdapter.addFooterViewTo(isCheckSuccess, true);
                                findViewById(R.id.openCard).setVisibility(View.VISIBLE);
                                findViewById(R.id.edit_card_number).setVisibility(View.VISIBLE);

                            } else {
                                checkDeviceAdapter.addFooterViewTo(isCheckSuccess, false);
                            }
                        }
                    }
                });
            }

            @Override
            public void onCheckStart(CheckDeviceBean checkDeviceBean) {
                checkList.add(checkDeviceBean);
                runOnUiThread(() -> checkDeviceAdapter.updateData());
            }

            @Override
            public void onCheckEnd(CheckDeviceBean checkDeviceBean) {
                runOnUiThread(() -> checkDeviceAdapter.updateData(checkDeviceBean));
            }
        });
    }

    private boolean isHaveRoot = false;
    private CardNumberCheck cardNumberCheck = new CardNumberCheck();
    private CardPermissionCheck cardPermissionCheck = new CardPermissionCheck();
    private SystemTimeCheck systemTimeCheck = new SystemTimeCheck();
    private NetCheck netCheck = new NetCheck();
    private CaeAuthCheck caeAuthCheck = new CaeAuthCheck();

    private void addCheckTask() {
        isHaveRoot = ShellUtils.haveRoot();
        if (!BuildConfig.MicType.equals("1mic")) {
            taskList.add(cardNumberCheck);
            taskList.add(cardPermissionCheck);
        }
//            add(systemTimeCheck)
        taskList.add(netCheck);
        taskList.add(new PlatformMicCheck());
        taskList.add(caeAuthCheck);
        if (BuildConfig.MicType.equals("1mic")) {
            taskList.add(new CardOpenCheck());
        } else {
            taskList.add(new CardOpenCheck());
        }
        TaskCheckMgr.getInstance().addTask(taskList);
    }

    private void startProgress() {
        ProgressBar initProgress = findViewById(R.id.initProgress);
        initProgress.setVisibility(View.VISIBLE);
        ValueAnimator anim = ValueAnimator.ofInt(0, 100);
        anim.setDuration(3000);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int fl = (int) animation.getAnimatedValue();
                initProgress.setProgress(fl);
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                goToNlpActivity();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    private void goToNlpActivity() {
        startService(new Intent(this, VoiceService.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean checkPermission = true;
        for (int i : grantResults) {
            if (i != PackageManager.PERMISSION_GRANTED) {
                checkPermission = false;
            }
        }
        if (checkPermission) {
            initUIEvent();
        }
    }

    /**
     * 权限检查
     */
    private boolean checkPermissions() {
        ArrayList<String> list = new ArrayList<>();
        for (String it : permissionArray) {
            if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
                list.add(it);
            }
        }
        if (list.size() > 0) {
            String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                array[i] = list.get(i);
            }
            ActivityCompat.requestPermissions(this, array, 1);
            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (!isCheckSuccess) {
            CaeOperator.getInstance().stopRecord();
        }
    }

    public void back(View view) {
        TaskCheckMgr.getInstance().stopCheCkTask();
        isCheckSuccess = false;
        finish();
        System.exit(0);
    }
}
