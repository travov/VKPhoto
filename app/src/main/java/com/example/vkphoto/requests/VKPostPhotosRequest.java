package com.example.vkphoto.requests;

import java.io.File;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class VKPostPhotosRequest {

    final static String TAG = VKPostPhotosRequest.class.getSimpleName();

    private VKPostPhotosRequest() {
    }

    public static Request uploadPhotos(String uploadUrl, List<String> paths) {

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (int i = 0; i < paths.size(); i++) {
            String path = paths.get(i);
            File sourceFile = new File(path);
            if (sourceFile.exists()) {
                final MediaType MEDIA_TYPE = MediaType.parse(path.endsWith("png") ? "image/png" : "image/jpeg");
                builder.addFormDataPart("file" + (i + 1), sourceFile.getName(), RequestBody.create(MEDIA_TYPE, sourceFile));
            }
        }

        RequestBody requestBody = builder.build();
        Request req = new Request.Builder().url(uploadUrl).post(requestBody).build();
        return req;
    }
}
