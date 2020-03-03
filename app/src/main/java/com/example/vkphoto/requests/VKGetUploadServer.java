package com.example.vkphoto.requests;

import com.vk.api.sdk.requests.VKRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class VKGetUploadServer extends VKRequest<JSONObject> {
    public VKGetUploadServer(int albumId) {
        super("photos.getUploadServer");
        addParam("album_id", albumId);
    }

    @Override
    public JSONObject parse(@NotNull JSONObject r) throws Exception {
        return r;
    }
}