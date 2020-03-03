package com.example.vkphoto.requests;

import com.example.vkphoto.model.VKAlbum;
import com.vk.api.sdk.requests.VKRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class VKAlbumsRequest extends VKRequest<RequestResult<VKAlbum>> {

    public VKAlbumsRequest() {
        super("photos.getAlbums");
        addParam("need_covers", 1);
    }

    public VKAlbumsRequest(int ownerId) {
        super("photos.getAlbums");
        addParam("owner_id", ownerId);
        addParam("need_covers", 1);
    }

    @Override
    public RequestResult<VKAlbum> parse(@NotNull JSONObject r) throws Exception {
        JSONObject response = r.getJSONObject("response");
        JSONArray users = response.getJSONArray("items");
        int totalCount = response.optInt("count", 0);
        List<VKAlbum> result = new ArrayList<>();
        for (int i = 0;i < users.length();i++) {
            result.add(VKAlbum.parse(users.getJSONObject(i)));
        }
        return new RequestResult<>(result, totalCount);
    }

}
