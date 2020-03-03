package com.example.vkphoto.requests;

import com.vk.api.sdk.requests.VKRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class VKDeletePhotoRequest extends VKRequest<Integer> {

    public VKDeletePhotoRequest(int photoId) {
        super("photos.delete");
        addParam("photo_id", photoId);
    }

    @Override
    public Integer parse(@NotNull JSONObject r) throws Exception {
        return r.optInt("response", 0);
    }
}
