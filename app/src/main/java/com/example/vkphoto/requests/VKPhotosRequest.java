package com.example.vkphoto.requests;

import com.example.vkphoto.model.VKPhoto;
import com.vk.api.sdk.requests.VKRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class VKPhotosRequest extends VKRequest<RequestResult<VKPhoto>> {
    public VKPhotosRequest(int albumId, int offset, int count) {
        super("photos.get");
        addParam("album_id", albumId);
        addParam("extended", 1);
        addParam("photo_sizes", 1);
        addParam("offset", offset);
        addParam("count", count);
    }

    public VKPhotosRequest(int ownerId, int albumId, int offset, int count) {
        super("photos.get");
        addParam("owner_id", ownerId);
        addParam("album_id", albumId);
        addParam("extended", 1);
        addParam("photo_sizes", 1);
        addParam("offset", offset);
        addParam("count", count);
    }

    @Override
    public RequestResult<VKPhoto> parse(@NotNull JSONObject r) throws Exception {
        JSONObject response = r.getJSONObject("response");
        JSONArray users = response.getJSONArray("items");
        int totalCount = response.optInt("count", 0);
        List<VKPhoto> result = new ArrayList<>();
        for (int i = 0;i < users.length();i++) {
            result.add(VKPhoto.parse(users.getJSONObject(i)));
        }
        return new RequestResult<>(result, totalCount);
    }
}
