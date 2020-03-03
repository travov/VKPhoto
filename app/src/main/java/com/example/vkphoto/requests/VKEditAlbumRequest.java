package com.example.vkphoto.requests;

import com.vk.api.sdk.requests.VKRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class VKEditAlbumRequest extends VKRequest<Integer> {

    public VKEditAlbumRequest(int albumId, String title, String description, String[] privacyView, String[] privacyComment) {
        super("photos.editAlbum");
        addParam("album_id", albumId);
        addParam("title", title);
        addParam("description", description);
        addParam("privacy_view", privacyView);
        addParam("privacy_comment", privacyComment);

    }

    @Override
    public Integer parse(@NotNull JSONObject r) {
        return r.optInt("response", 0);
    }
}
