package com.example.vkphoto.presenter;

import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public abstract class Presenter {

    Fragment fragment;
    ViewGroup view;

    public Presenter(Fragment fragment, ViewGroup view) {
        this.fragment = fragment;
        this.view = view;
    }

    public abstract void delete(int photoId, int position);

}
