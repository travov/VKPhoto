package com.example.vkphoto.requests;

import com.example.vkphoto.model.VKPhoto;
import com.vk.api.sdk.requests.VKRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class VKFavePhotosRequest extends VKRequest<RequestResult<VKPhoto>> {

    public VKFavePhotosRequest(int offset, int count) {
        super("fave.getPhotos");
        addParam("count", count);
        addParam("offset", offset);
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
