package com.example.newroutes.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newroutes.R;
import com.example.newroutes.databinding.FragmentRandomTutorialBinding;


public class CustomTutorialFragment extends RandomTutorialFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gifs = new String[]{"https://i.imgur.com/0FQr28g.gif","https://i.imgur.com/tJtVq6S.gif","https://i.imgur.com/AGB4QQS.gif"};
        instructions = new int[]{R.string.custom_page_1,R.string.custom_page_2,R.string.custom_page_3};
    }
}