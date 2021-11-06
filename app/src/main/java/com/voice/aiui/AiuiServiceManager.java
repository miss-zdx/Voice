package com.voice.aiui;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.voice.aiui.base.BaseService;
import com.voice.aiui.base.NlpBaseBean;
import com.voice.aiui.base.ServiceType;
import com.voice.aiui.service.IflytekMusicProService;
import com.voice.aiui.service.IflytekNewsService;
import com.voice.aiui.service.IflytekStoryService;
import com.voice.aiui.service.LightSmartHomeService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AiuiServiceManager {
    private static final String TAG = "AiuiServiceManager";
    private static AiuiServiceManager serviceManager = new AiuiServiceManager();
    private Map<String, BaseService> hashMap = new HashMap<>();

    private AiuiServiceManager() {
        hashMap.put(ServiceType.musicPro.name(), new IflytekMusicProService());
        hashMap.put(ServiceType.story.name(), new IflytekStoryService());
        hashMap.put(ServiceType.light_smartHome.name(), new LightSmartHomeService());
        hashMap.put(ServiceType.news.name(), new IflytekNewsService());
    }

    private BaseService prevBaseService = null;

    public static AiuiServiceManager getInstance() {
        return serviceManager;
    }


    public void handlerWake() {
        Set<String> strings = hashMap.keySet();
        for (String string : strings) {
            hashMap.get(string).handlerWake();
        }
    }

    public String parseResult(String string) {
        JSONObject intent = null;
        String answer = "";
        try {
            intent = new JSONObject(string);
            String service = intent.optString("service");
            JSONObject answerObj = intent.optJSONObject("answer");

            if (answerObj != null) {
                answer = answerObj.optString("text");
            }
            Log.e(TAG, "parseResult: " + service + "   " + answer + "  " + ServiceType.news.name());
            BaseService baseService = hashMap.get(service);
            if (baseService != null) {
                prevBaseService = baseService;
                if (baseService.semanticArray()) {
                    NlpBaseBean<JsonArray> nlpBaseBean =
                            new Gson().fromJson(string, new TypeToken<NlpBaseBean<JsonArray>>() {
                            }.getType());
                    answer = baseService.handlerNlp(nlpBaseBean.getSemantic(), answer, nlpBaseBean.getDataResult());
                } else {
                    NlpBaseBean<JsonObject> nlpBaseBean =
                            new Gson().fromJson(string, new TypeToken<NlpBaseBean<JsonObject>>() {
                            }.getType());
                    answer = baseService.handlerNlp(nlpBaseBean.getSemantic(), answer, nlpBaseBean.getDataResult());
                }

            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answerHandler(answer);
    }

    private String answerHandler(String answer) {
        if (TextUtils.isEmpty(answer)) {
//            answer = "这个问题我还在学习中";
            answer = "";
        } else {
            handlerWake();
        }
        return answer;
    }

    public void handleDestroy() {
        if (prevBaseService != null) {
            prevBaseService.handlerWake();
        }
    }
}
