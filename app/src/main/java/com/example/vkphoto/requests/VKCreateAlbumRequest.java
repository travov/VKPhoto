package com.example.vkphoto.requests;

import com.example.vkphoto.model.VKAlbum;
import com.vk.api.sdk.requests.VKRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class VKCreateAlbumRequest extends VKRequest<VKAlbum> {

    public VKCreateAlbumRequest(String title, String description, String[] privacyView, String[] privacyComment) {
        super("photos.createAlbum");
        addParam("privacy_view", privacyView);
        addParam("privacy_comment", privacyComment);
        addParam("title", title);
        addParam("description", description);
    }

    @Override
    public VKAlbum parse(@NotNull JSONObject r) throws Exception {
        JSONObject response = r.getJSONObject("response");
        return VKAlbum.parse(response);
    }

}
