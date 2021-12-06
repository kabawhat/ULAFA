package com.binbashir.ulafa.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.binbashir.ulafa.R;
import com.binbashir.ulafa.Util.Util_class;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

// TODO: 8/15/2021 only login user can post


public class ViewDetailsFragment extends Fragment {

    TextView view_fragment_txt_title;
    ImageView view_fragment_imageview_image;
    Button view_fragment_btn_email;
    Button view_fragment_btn_call;
    ViewDetailsFragmentArgs viewDetailsFragmentArgs;
    NavController navController;


    public ViewDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_image, container, false);

        view_fragment_txt_title = view.findViewById(R.id.view_fragment_text_desc);
        view_fragment_imageview_image = view.findViewById(R.id.view_fragment_imageview_image);
        view_fragment_btn_email = view.findViewById(R.id.view_fragment_btn_email);
        view_fragment_btn_call = view.findViewById(R.id.view_fragment_btn_call);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);


        if (getArguments() != null) {


            viewDetailsFragmentArgs = viewDetailsFragmentArgs.fromBundle(getArguments());


            view_fragment_txt_title.setText("Have You Seen this " + viewDetailsFragmentArgs.getLostItem().toUpperCase() + " ?" + " if found please submit to the security division, and type in message in the notification field to help notify the mislayer");

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.no_image_available)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .fitCenter();

            Glide.with(getContext())
                    .setDefaultRequestOptions(options)
                    .load(viewDetailsFragmentArgs.getItemImageUrl())
                    .thumbnail(0.1f)
                    .into(view_fragment_imageview_image);


            view_fragment_btn_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Util_class().phone_call(getActivity(), viewDetailsFragmentArgs.getPhoneNumber());

                }
            });

            view_fragment_btn_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (new Util_class().checking_current_user()) {
                        new Util_class().send_email(getActivity());
                    } else {
                        navController.navigate(R.id.loginFragment);
                    }
                }
            });


        }
    }
}