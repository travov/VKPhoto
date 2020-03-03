package com.example.vkphoto.presenter;

import android.app.Dialog;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.vkphoto.R;
import com.example.vkphoto.model.VKPhoto;
import com.example.vkphoto.requests.RequestResult;
import com.example.vkphoto.requests.VKAllPhotosRequest;
import com.example.vkphoto.requests.VKDeletePhotoRequest;
import com.example.vkphoto.utils.PhotoRecyclerViewAdapter;
import com.example.vkphoto.view.ImageActivity;
import com.example.vkphoto.view.fragments.HomeFragment;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class HomePresenter extends Presenter {

    private final static String TAG = HomePresenter.class.getSimpleName();
    private RecyclerView rv;
    private ProgressBar progressBar;

    final static int COUNT = 20;
    private boolean loading;
    private int visibleThreshold;
    private int previousTotal;
    private int offset;

    public HomePresenter(Fragment fragment, ViewGroup view) {
        super(fragment, view);
        offset = 0;
        visibleThreshold = 10;
        previousTotal = 0;
        rv = view.findViewById(R.id.home_recycler);
        progressBar = view.findViewById(R.id.progress_bar);
    }


    public void init() {
        progressBar.setVisibility(View.VISIBLE);
        List<VKPhoto> photos = new ArrayList<>();
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
        recyclerAdapter.setPresenter(this);

        rv.setAdapter(recyclerAdapter);
        rv.setLayoutManager(new GridLayoutManager(fragment.getActivity(), 2));

        //loading = true;
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
                    getAll();
                    //addOffset();
                    loading = true;
                }
            }
        });
    }

    public SwipeRefreshLayout setRefreshBehaviour(SwipeRefreshLayout layout) {
        view.removeView(layout);
        layout.setOnRefreshListener(() -> {
            ((PhotoRecyclerViewAdapter)rv.getAdapter()).clear();
            offset = 0;
            getAll();
            //offset += COUNT;
            previousTotal = 0;
            layout.setRefreshing(false);
        });
        return layout;
    }

    public void menuSelectedItem(int id) {
        switch (id) {
            case R.id.columns:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(fragment.getContext());
                alertDialogBuilder.setTitle(R.string.columns);
                AlertDialog dialog = alertDialogBuilder.create();
                View setColumnsView = fragment.getLayoutInflater().inflate(R.layout.columns_layout, null);
                RadioGroup colGroup = setColumnsView.findViewById(R.id.radio_columns);
                GridLayoutManager manager = (GridLayoutManager) rv.getLayoutManager();
                int currentSpanCount = manager.getSpanCount();
                switch (currentSpanCount) {
                    case 1:
                        RadioButton one = setColumnsView.findViewById(R.id.one_col_radio);
                        one.setChecked(true);
                        break;
                    case 2:
                        RadioButton two = setColumnsView.findViewById(R.id.two_col_radio);
                        two.setChecked(true);
                        break;
                    case 3:
                        RadioButton three = setColumnsView.findViewById(R.id.three_col_radio);
                        three.setChecked(true);
                        break;
                    case 4:
                        RadioButton four = setColumnsView.findViewById(R.id.four_col_radio);
                        four.setChecked(true);
                        break;
                }
                dialog.setView(setColumnsView);
                dialog.setButton(Dialog.BUTTON_NEGATIVE, fragment.getResources().getString(R.string.cancel), (dialog12, which) -> dialog.dismiss());
                dialog.setButton(Dialog.BUTTON_POSITIVE, fragment.getResources().getString(R.string.apply),
                        (dialog2, which) -> {
                            int selectedItem = colGroup.getCheckedRadioButtonId();
                            RadioButton rb = setColumnsView.findViewById(selectedItem);
                            manager.setSpanCount(Integer.parseInt(rb.getText().toString().substring(0 , 1)));
                        });
                dialog.show();
                dialog.getButton(Dialog.BUTTON_POSITIVE).setTextColor(fragment.getResources().getColor(R.color.colorPrimary));
                dialog.getButton(Dialog.BUTTON_NEGATIVE).setTextColor(fragment.getResources().getColor(R.color.colorPrimary));
                break;

        }
    }

    public void scrollToExitPosition(int exitPosition) {
        RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();
        View viewAtPosition = layoutManager.findViewByPosition(exitPosition);
        if (viewAtPosition == null || layoutManager.isViewPartiallyVisible(viewAtPosition, false, true)) {
            layoutManager.scrollToPosition(exitPosition);
        }
    }

    public int getRecyclerState() {
        return ((GridLayoutManager)rv.getLayoutManager()).findFirstVisibleItemPosition();
    }

    public void setScrollPosition(int position) {
        ((GridLayoutManager)rv.getLayoutManager()).scrollToPositionWithOffset(position, 0);
    }

    @Override
    public void delete(int photoId, int position) {
        VKDeletePhotoRequest req = new VKDeletePhotoRequest(photoId);
        VK.execute(req, new VKApiCallback<Integer>() {
            @Override
            public void success(Integer integer) {
                PhotoRecyclerViewAdapter adapter = (PhotoRecyclerViewAdapter) rv.getAdapter();
                adapter.remove(position);
            }

            @Override
            public void fail(@NotNull Exception e) {
                Toast.makeText(view.getContext(), fragment.getString(R.string.error_delete), Toast.LENGTH_SHORT).show();
                Log.e(HomeFragment.TAG, e.getMessage());
            }
        });
    }

    public void getAll() {
        VKAllPhotosRequest req = new VKAllPhotosRequest(offset, COUNT);
        ProgressBar bar = view.findViewById(R.id.progress_bar);
        VK.execute(req, new VKApiCallback<RequestResult<VKPhoto>>() {
            @Override
            public void success(RequestResult<VKPhoto> result) {
                Log.i(TAG, "get all photos with offset " + offset);
                PhotoRecyclerViewAdapter adapter = (PhotoRecyclerViewAdapter) rv.getAdapter();
                adapter.addPhotos(result.getList());
                adapter.notifyDataSetChanged();
                bar.setVisibility(View.GONE);
                addOffset();
            }

            @Override
            public void fail(@NotNull Exception e) {
                Toast.makeText(view.getContext(), fragment.getString(R.string.error_photos), Toast.LENGTH_SHORT).show();
                Log.e(HomeFragment.TAG, e.getMessage());
                bar.setVisibility(View.GONE);
            }
        });
    }

    private void addOffset() {
        offset += COUNT;
    }

}
