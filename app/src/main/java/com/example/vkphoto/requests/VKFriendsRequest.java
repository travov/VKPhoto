package com.example.vkphoto.requests;

import com.example.vkphoto.model.VKUser;
import com.vk.api.sdk.requests.VKRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class VKFriendsRequest extends VKRequest<RequestResult<VKUser>> {

    public VKFriendsRequest() {
        super("friends.get");
        addParam("fields", "photo_200,online");
        addParam("offset", 0);
        addParam("name_case", "nom");
    }

    public VKFriendsRequest(int id) {
        super("friends.get");
        addParam("user_id", id);
        addParam("fields", "photo_200,online");
        addParam("offset", 0);

    }

    @Override
    public RequestResult<VKUser> parse(@NotNull JSONObject r) throws Exception {
        JSONObject response = r.getJSONObject("response");
        JSONArray users = response.getJSONArray("items");
        int totalCount = response.optInt("count", 0);
        List<VKUser> result = new ArrayList<>();
        for (int i = 0;i < users.length();i++) {
            result.add(VKUser.parse(users.getJSONObject(i)));
        }
        return new RequestResult<>(result, totalCount);
    }
}
