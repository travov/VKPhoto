package com.example.vkphoto.requests;

import com.vk.api.sdk.requests.VKRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class VKDeleteAlbumRequest extends VKRequest<Integer> {

    public VKDeleteAlbumRequest(int albumId) {
        super("photos.deleteAlbum");
        addParam("album_id", albumId);
    }

    @Override
    public Integer parse(@NotNull JSONObject r) {
        return r.optInt("response", 0);
    }
}
