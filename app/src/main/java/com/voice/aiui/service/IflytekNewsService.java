package com.voice.aiui.service;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.voice.aiui.base.BaseService;
import com.voice.aiui.base.SemanticNormal;
import com.voice.aiui.base.ServiceType;
import com.voice.aiui.bean.NewsBean;
import com.voice.player.PlayerManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class IflytekNewsService extends BaseService<JsonArray> {
    private static final String TAG = "IflytekNewsService";
    private List<NewsBean> newsBeanList = new ArrayList<>();
    private int index = 0;

    @Override
    public ServiceType getType() {
        return ServiceType.news;
    }

    @Override
    public String handlerNlp(JsonArray semanticJson, String answer, JsonArray resultArray) {
        List<SemanticNormal> semanticList = gson.fromJson(semanticJson.toString(), new TypeToken<List<SemanticNormal>>() {
        }.getType());
        if (semanticList != null && semanticList.size() > 0) {
            SemanticNormal semanticNormal = semanticList.get(0);
            String intent = semanticNormal.getIntent();

            Log.e(TAG, "handlerNlp: " + intent);
            if (intent.equals("NEXT") || intent.equals("PREVIOUS")) {
                if (newsBeanList.size() > 0) {
                    playNews();
                    answer = "好的";
                }
            } else if (intent.equals("PLAY")) {
                String s = resultArray.toString();
                Type type = new TypeToken<List<NewsBean>>() {
                }.getType();
                List<NewsBean> storyBeanList = gson.fromJson(s, type);
                if (storyBeanList != null && storyBeanList.size() > 0) {
                    newsBeanList.clear();
                    newsBeanList.addAll(storyBeanList);
                    playNews();
                }
            } else if (intent.equals("PAUSE")) {
                PlayerManager.getInstance().pause();
                answer = "好的";
            } else if (intent.equals("INSTRUCTION")) {
                List<SemanticNormal.Slots> slotsList = semanticNormal.getSlots();
                if (slotsList != null && slotsList.size() > 0) {
                    SemanticNormal.Slots slots = slotsList.get(0);
                    String value = slots.getValue();
                    Log.e(TAG, "handlerNlp: " + value);
                    if ("PREVIOUS".equals(value) || "NEXT".equals(value)) {
                        playNews();
                        answer = "好的";
                    } else if ("PAUSE".equals(value) || "CLOSE".equals(value)) {
                        PlayerManager.getInstance().stop();
                    }
                }
            }
        }

        return "好的";
    }

    private void playNews() {
        if (index >= 0 && newsBeanList.size() > 0) {
            NewsBean newsBean = newsBeanList.get(index++ % newsBeanList.size());
            PlayerManager.getInstance().playUrl(newsBean.getUrl(), (state, msg) -> {
                playNews();
            });
        }
    }

    @Override
    public void handlerWake() {
        index = 0;
        PlayerManager.getInstance().stop();
    }
}
