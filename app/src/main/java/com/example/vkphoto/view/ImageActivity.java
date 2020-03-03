package com.example.vkphoto.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.SharedElementCallback;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import com.example.vkphoto.R;
import com.example.vkphoto.model.VKPhoto;
import com.example.vkphoto.utils.ImagePagerAdapter;
import java.util.List;
import java.util.Map;

public class ImageActivity extends AppCompatActivity implements ImagePagerAdapter.PagerClickListener {

    private ViewPager pager;

    private int current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        setContentView(R.layout.activity_image);
        pager = findViewById(R.id.view_pager);
        /*View photoView = getLayoutInflater().inflate(R.layout.photo_item, null);
        GestureImageView image = photoView.findViewById(R.id.photo_item);*/
        Bundle extras = getIntent().getExtras();
        int listSize = extras.getInt("listSize");
        List<VKPhoto> photos = extras.getParcelableArrayList("list");
        current = extras.getInt("current");
        pager.setAdapter(new ImagePagerAdapter(this, listSize, current, this, photos));
        pager.setCurrentItem(current);

    }

    @Override
    public void setStartPostTransition(final View view) {
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                return false;
            }
        });
    }

    @Override
    public void finishAfterTransition() {
        int pos = pager.getCurrentItem();
        Intent intent = new Intent();
        intent.putExtra("exit_position", pos);
        setResult(RESULT_OK, intent);
        if (current != pos) {
            View view = pager.findViewWithTag(pos);
            setSharedElementCallback(view);
        }
        super.finishAfterTransition();
    }

    private void setSharedElementCallback(final View view) {
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                names.clear();
                sharedElements.clear();
                names.add(view.getTransitionName());
                sharedElements.put(view.getTransitionName(), view);
            }
        });
    }
}
