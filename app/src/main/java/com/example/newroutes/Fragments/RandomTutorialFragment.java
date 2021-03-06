package com.example.newroutes.Fragments;

import android.graphics.drawable.Drawable;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.newroutes.R;
import com.example.newroutes.databinding.FragmentRandomTutorialBinding;


public class RandomTutorialFragment extends Fragment {


    private ImageButton ibLeft;
    private ImageButton ibRight;
    private TextView tvPageCounter;
    private ImageView ivGif;
    private TextView tvInstructions;
    private ImageButton ibClose;
    private ProgressBar progressBar;
    private int pageNumber = 1;
    private int numPages = 3;
    public int[] instructions;
    public String[] gifs;
    FragmentRandomTutorialBinding binding;

    public RandomTutorialFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gifs = new String[]{"https://i.imgur.com/OeOOh63.gif", "https://i.imgur.com/6WKXc6h.gif", "https://i.imgur.com/PXmRSoS.gif"};
        instructions = new int[]{R.string.random_page_1,R.string.random_page_2,R.string.random_page_3};

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRandomTutorialBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ibLeft = binding.ibLeft;
        ibRight = binding.ibRight;
        ibClose = binding.ibClose;
        progressBar = binding.progressBar;
        tvInstructions = binding.tvInstructions;
        tvPageCounter = binding.tvPageCounter;
        ivGif = binding.ivGif;

        tvInstructions.setText(getString(instructions[0]));
        ibLeft.setVisibility(View.INVISIBLE);
        tvPageCounter.setText(pageNumber + "/" + numPages);


        loadImage(gifs[pageNumber-1]);

        ibRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNumber++;
                ibLeft.setVisibility(View.VISIBLE);
                tvInstructions.setText(getString(instructions[pageNumber-1]));
                tvPageCounter.setText(pageNumber + "/" + numPages);
                if (pageNumber == numPages) {
                    ibRight.setVisibility(View.INVISIBLE);
                }
                loadImage(gifs[pageNumber-1]);
            }
        });
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNumber--;
                ibRight.setVisibility(View.VISIBLE);
                tvInstructions.setText(getString(instructions[pageNumber - 1]));
                tvPageCounter.setText(pageNumber + "/" + numPages);
                if (pageNumber == 1) {
                    ibLeft.setVisibility(View.INVISIBLE);
                }
                loadImage(gifs[pageNumber-1]);
            }
        });
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(RandomTutorialFragment.this).commit();
            }
        });
    }

    public void loadImage(String imageUrl) {
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(getContext()).load(imageUrl).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.INVISIBLE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.INVISIBLE);
                return false;
            }
        }).into(ivGif);
    }
}