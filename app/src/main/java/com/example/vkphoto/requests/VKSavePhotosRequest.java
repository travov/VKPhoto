package com.example.vkphoto.requests;

import com.example.vkphoto.model.VKPhoto;
import com.vk.api.sdk.requests.VKRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class VKSavePhotosRequest extends VKRequest<RequestResult<VKPhoto>> {

    public VKSavePhotosRequest(int albumId, int server, String photosList, String hash) {
        super("photos.save");
        addParam("album_id", albumId);
        addParam("server" , server);
        addParam("photos_list", photosList);
        addParam("hash", hash);
    }

    @Override
    public RequestResult<VKPhoto> parse(@NotNull JSONObject r) throws Exception {
        JSONArray response = r.getJSONArray("response");
        List<VKPhoto> result = new ArrayList<>();
        for (int i = 0;i < response.length();i++) {
            result.add(VKPhoto.parse(response.getJSONObject(i)));
        }
        return new RequestResult<>(result, response.length());
    }
}
