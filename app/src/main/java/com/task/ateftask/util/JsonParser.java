package com.task.ateftask.util;

import com.task.ateftask.model.FacebookUserData;

import org.json.JSONObject;

public class JsonParser {

    public static FacebookUserData getUserFromJson(JSONObject userData) {
        String userName = userData.optString(Constant.Graph.NAME);
        String imgUrl = userData.optJSONObject(Constant.Graph.PICTURE).optJSONObject(Constant.Graph.DATA).optString(Constant.Graph.URL);
        return new FacebookUserData()
                .setName(userName)
                .setImageUrl(imgUrl);
    }
}
