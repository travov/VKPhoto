package com.example.vkphoto.presenter;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.vkphoto.R;
import com.example.vkphoto.model.VKPhoto;
import com.example.vkphoto.requests.RequestResult;
import com.example.vkphoto.requests.VKDeletePhotoRequest;
import com.example.vkphoto.requests.VKEditAlbumRequest;
import com.example.vkphoto.requests.VKGetUploadServer;
import com.example.vkphoto.requests.VKPhotosRequest;
import com.example.vkphoto.requests.VKPostPhotosRequest;
import com.example.vkphoto.requests.VKSavePhotosRequest;
import com.example.vkphoto.utils.PhotoRecyclerViewAdapter;
import com.example.vkphoto.view.ImageActivity;
import com.example.vkphoto.view.fragments.HomeFragment;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class AlbumsPhotoPresenter extends Presenter {

    private final static String TAG = AlbumsPhotoPresenter.class.getSimpleName();
    public final static int RESULT_LOAD_IMG = 23;
    private int albumId;
    private String title;
    private String description;
    private String privacyView;
    private String privacyComment;
    private RecyclerView rv;
    private ProgressBar progressBar;
    private TextView tw;

    final static int COUNT = 20;
    private boolean loading;
    private int visibleThreshold;
    private int previousTotal;
    private int offset;

    public AlbumsPhotoPresenter(Fragment fragment, ViewGroup view, int albumId, String title, String description, String privacyView, String privacyComment) {
        super(fragment, view);
        this.albumId = albumId;
        this.title = title;
        this.description = description;
        this.privacyView = privacyView;
        this.privacyComment = privacyComment;
        offset = 0;
        visibleThreshold = 10;
        previousTotal = 0;
        rv = view.findViewById(R.id.albums_photo_recycler);
        progressBar = view.findViewById(R.id.progress_bar);
        tw = view.findViewById(R.id.photo_not_added_yet);

    }

    public void init() {
        progressBar.setVisibility(View.VISIBLE);
        List<VKPhoto> photos = new ArrayList<>();
        rv.setLayoutManager(new GridLayoutManager(fragment.getContext(), 2));
        PhotoRecyclerViewAdapter recyclerAdapter = new PhotoRecyclerViewAdapter(photos, (image, photo, listSize, adapterPosition) -> {
            Intent intent = new Intent(fragment.getActivity(), ImageActivity.class);
            intent.putExtra("url", photo.getMax().getUrl());
            intent.putExtra("transition_name", image.getTransitionName());
            intent.putExtra("listSize", listSize);
            intent.putExtra("current", adapterPosition);
            intent.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) photos);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(fragment.getActivity(), image, image.getTransitionName());
            fragment.startActivity(intent, options.toBundle());
        });
        rv.setAdapter(recyclerAdapter);

        recyclerAdapter.setPresenter(this);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();   //смотрим сколько элементов на экране
                int totalItemCount = layoutManager.getItemCount();      //сколько всего элементов
                int firstVisibleItems = layoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }

                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItems + visibleThreshold)) {
                    get(albumId);
                    //addOffset();
                    loading = true;
                }
            }
        });
    }

    public SwipeRefreshLayout setRefreshBehaviour(SwipeRefreshLayout layout) {
        //view.removeView(layout);
        layout.setOnRefreshListener(() -> {
            ((PhotoRecyclerViewAdapter)rv.getAdapter()).clear();
            offset = 0;
            get(albumId);
            previousTotal = 0;
            layout.setRefreshing(false);
        });
        return layout;
    }

    public void scrollToExitPosition(int exitPosition) {
        RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();
        View viewAtPosition = layoutManager.findViewByPosition(exitPosition);
        if (viewAtPosition == null || layoutManager.isViewPartiallyVisible(viewAtPosition, false, true)) {
            layoutManager.scrollToPosition(exitPosition);
        }
    }

    public void get(int albumId) {
        VKPhotosRequest req = new VKPhotosRequest(albumId, offset, COUNT);
        ProgressBar bar = view.findViewById(R.id.progress_bar);
        PhotoRecyclerViewAdapter adapter = (PhotoRecyclerViewAdapter) rv.getAdapter();
        VK.execute(req, new VKApiCallback<RequestResult<VKPhoto>>() {
            @Override
            public void success(RequestResult<VKPhoto> result) {
                Log.i(TAG, "get all photos in album with id " + albumId);
                adapter.addPhotos(result.getList());
                adapter.notifyDataSetChanged();
                bar.setVisibility(View.GONE);
                addOffset();
                checkMessageVisibility(adapter);
            }

            @Override
            public void fail(@NotNull Exception e) {
                Toast.makeText(view.getContext(), fragment.getString(R.string.error_photos), Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.getMessage());
                bar.setVisibility(View.GONE);
                checkMessageVisibility(adapter);

            }
        });
    }

    public void save(int albumId, List<String> paths) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(fragment.getContext());
        alertDialogBuilder.setCancelable(false);
        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.setTitle(R.string.uploading);
        dialog.setView(fragment.getLayoutInflater().inflate(R.layout.progress_bar_layout, null));
        dialog.show();
        VKGetUploadServer req = new VKGetUploadServer(albumId);
        VK.execute(req, new VKApiCallback<JSONObject>() {
            @Override
            public void success(JSONObject jsonObject) {
                try {
                    JSONObject obj = jsonObject.getJSONObject("response");
                    String uploadUrl = obj.optString("upload_url", "");
                    //String userId = jsonObject.optString("user_id", "");
                    Request request = VKPostPhotosRequest.uploadPhotos(uploadUrl, paths);
                    OkHttpClient client = new OkHttpClient();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(fragment.getContext(), fragment.getString(R.string.error_post_images), Toast.LENGTH_SHORT).show();
                            Log.e(HomeFragment.TAG, e.getMessage());
                            dialog.dismiss();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                JSONObject responseObj = new JSONObject(response.body().string());
                                int server = responseObj.optInt("server");
                                String photosList = responseObj.optString("photos_list");
                                String hash = responseObj.optString("hash");
                                VKSavePhotosRequest saveRequest = new VKSavePhotosRequest(albumId, server, photosList, hash);
                                VK.execute(saveRequest, new VKApiCallback<RequestResult<VKPhoto>>() {
                                    @Override
                                    public void success(RequestResult<VKPhoto> result) {
                                        PhotoRecyclerViewAdapter adapter = (PhotoRecyclerViewAdapter) rv.getAdapter();
                                        adapter.addPhotos(result.getList());
                                        adapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void fail(@NotNull Exception e) {
                                        Log.e(TAG, e.getMessage());
                                        dialog.dismiss();
                                        Toast.makeText(view.getContext(), fragment.getString(R.string.error_post_images), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                                dialog.dismiss();
                            }
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    dialog.dismiss();
                }
            }

            @Override
            public void fail(@NotNull Exception e) {
                Toast.makeText(view.getContext(), fragment.getString(R.string.error_get_server), Toast.LENGTH_SHORT).show();
                Log.e(HomeFragment.TAG, e.getMessage());
            }
        });

    }

    public void edit(int albumId, String title, String description, String[] privacyView, String[] privacyComment, AlertDialog dialog) {
        VKEditAlbumRequest req = new VKEditAlbumRequest(albumId, title, description, privacyView, privacyComment);
        VK.execute(req, new VKApiCallback<Integer>() {
            @Override
            public void success(Integer integer) {
                dialog.dismiss();
            }

            @Override
            public void fail(@NotNull Exception e) {
                dialog.dismiss();
                Toast.makeText(view.getContext(), fragment.getString(R.string.error_edit_album), Toast.LENGTH_SHORT).show();
                Log.e(HomeFragment.TAG, e.getMessage());
            }
        });
    }

    @Override
    public void delete(int photoId, int position) {
        VKDeletePhotoRequest req = new VKDeletePhotoRequest(photoId);
        VK.execute(req, new VKApiCallback<Integer>() {
            @Override
            public void success(Integer integer) {
                PhotoRecyclerViewAdapter adapter = (PhotoRecyclerViewAdapter) rv.getAdapter();
                adapter.remove(position);
                checkMessageVisibility(adapter);
            }

            @Override
            public void fail(@NotNull Exception e) {
                Toast.makeText(view.getContext(), fragment.getString(R.string.error_delete), Toast.LENGTH_SHORT).show();
                Log.e(HomeFragment.TAG, e.getMessage());
            }
        });

    }

    public void checkMessageVisibility(PhotoRecyclerViewAdapter adapter) {
        if (adapter.getPhotos().isEmpty())
            tw.setVisibility(View.VISIBLE);
        else
            tw.setVisibility(View.INVISIBLE);
    }

    public void menuSelectedItem(int itemId) {
        switch (itemId) {
            case R.id.add_photo:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});
                SharedPreferences settings = fragment.getActivity().getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.putInt("albumId", albumId);
                editor.commit();
                if (Build.VERSION.SDK_INT >= 23) {
                    int permissionCheck = ContextCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(fragment.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                    else
                        fragment.getActivity().startActivityForResult(intent, RESULT_LOAD_IMG);
                }
                else
                    fragment.getActivity().startActivityForResult(intent, RESULT_LOAD_IMG);
                break;
            case R.id.edit_album:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(fragment.getContext());
                alertDialogBuilder.setTitle(R.string.edit_album);
                AlertDialog dialog = alertDialogBuilder.create();
                View layout = fragment.getLayoutInflater().inflate(R.layout.add_album_layout, null);
                Spinner privacyViewSpinner = layout.findViewById(R.id.privacy_view);
                Spinner privacyCommentSpinner = layout.findViewById(R.id.privacy_comment);
                EditText titleEdit = layout.findViewById(R.id.album_title);
                EditText descriptionEdit = layout.findViewById(R.id.album_description);
                titleEdit.setText(title);
                descriptionEdit.setText(description);
                switch (privacyView) {
                    case "all":
                        privacyViewSpinner.setSelection(0);
                        break;
                    case "friends":
                        privacyViewSpinner.setSelection(1);
                        break;
                    case "friends_of_friends":
                        privacyViewSpinner.setSelection(2);
                        break;
                    case "only_me":
                        privacyViewSpinner.setSelection(3);
                        break;
                }
                switch (privacyComment) {
                    case "all":
                        privacyCommentSpinner.setSelection(0);
                        break;
                    case "friends":
                        privacyCommentSpinner.setSelection(1);
                        break;
                    case "friends_of_friends":
                        privacyCommentSpinner.setSelection(2);
                        break;
                    case "only_me":
                        privacyCommentSpinner.setSelection(3);
                        break;
                }
                dialog.setView(layout);
                dialog.setButton(Dialog.BUTTON_NEGATIVE, fragment.getResources().getString(R.string.cancel), (dialog12, which) -> dialog.dismiss());
                dialog.setButton(Dialog.BUTTON_POSITIVE, fragment.getResources().getString(R.string.apply),
                        (dialog2, which) -> {
                            String privacyViewCategory = "";
                            String privacyCommentCategory = "";
                            switch (privacyViewSpinner.getSelectedItem().toString()) {
                                case "by all":
                                    privacyViewCategory = "all";
                                    break;
                                case "only by friends":
                                    privacyViewCategory = "friends";
                                    break;
                                case "by friends and their friends":
                                    privacyViewCategory = "friends_of_friends";
                                    break;
                                case "only by me":
                                    privacyViewCategory = "only_me";
                                    break;
                            }
                            switch (privacyCommentSpinner.getSelectedItem().toString()) {
                                case "by all":
                                    privacyCommentCategory = "all";
                                    break;
                                case "only by friends":
                                    privacyCommentCategory = "friends";
                                    break;
                                case "by friends and their friends":
                                    privacyCommentCategory = "friends_of_friends";
                                    break;
                                case "only by me":
                                    privacyCommentCategory = "only_me";
                                    break;
                            }
                            String title = titleEdit.getText().toString();
                            String description = descriptionEdit.getText().toString();
                            edit(albumId, title, description, new String[]{privacyViewCategory}, new String[]{privacyCommentCategory}, dialog);
                        });
                dialog.show();
                dialog.getButton(Dialog.BUTTON_POSITIVE).setTextColor(fragment.getResources().getColor(R.color.colorPrimary));
                dialog.getButton(Dialog.BUTTON_NEGATIVE).setTextColor(fragment.getResources().getColor(R.color.colorPrimary));
        }
    }

    public List<String> convertContentIntoFile(List<Uri> uris) {
        List<String> list = new ArrayList<>();
        for (int i = 0;i < uris.size();i++) {
            Uri uri = uris.get(i);
            String filePath;
            Cursor cursor = fragment.getContext().getContentResolver().query(uri, new String[] {android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
            cursor.moveToFirst();
            filePath = cursor.getString(0);
            cursor.close();
            list.add(filePath);
        }
        return list;
    }

    private void addOffset() {
        offset += COUNT;
    }

}
