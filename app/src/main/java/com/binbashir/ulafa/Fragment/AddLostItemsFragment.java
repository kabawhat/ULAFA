package com.binbashir.ulafa.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.binbashir.ulafa.R;
import com.binbashir.ulafa.Util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddLostItemsFragment extends Fragment {


    EditText editText_lost_item;
    EditText editText_lost_description;
    //EditText editText_lost_phoneNumber;
    Button btn_lost_post;
    ProgressBar progressBar_posting;

    FirebaseAuth firebaseAuth;

    NavController navController;


    public AddLostItemsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_lost, container, false);

        editText_lost_item = view.findViewById(R.id.lost_edit_item);
        editText_lost_description = view.findViewById(R.id.lost_edit_description);
        //  editText_lost_phoneNumber = view.findViewById(R.id.lost_edit_phoneNumber);
        btn_lost_post = view.findViewById(R.id.lost_btn_post);
        progressBar_posting = view.findViewById(R.id.loading_progress);


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);


        firebaseAuth = FirebaseAuth.getInstance();


        btn_lost_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDetails();


            }
        });
    }

    void saveDetails() {

        boolean is_valid_checked = false;

        String lost_item = editText_lost_item.getText().toString().trim();
        String lost_desc = editText_lost_description.getText().toString().trim();
//        String phone = editText_lost_phoneNumber.getText().toString().trim();
//
//
//        if (TextUtils.isEmpty(phone)) {
//            editText_lost_phoneNumber.setError("Phone Number is Empty");
//            editText_lost_phoneNumber.requestFocus();
//            is_valid_checked = true;
//        } else if (!isPhoneValid(phone)) {
//            editText_lost_phoneNumber.setError("Invalid Phone Number");
//            editText_lost_phoneNumber.requestFocus();
//            is_valid_checked = true;
//        }


        if (lost_desc.isEmpty()) {
            editText_lost_description.setError("Enter Description,last location");
            editText_lost_description.requestFocus();
            is_valid_checked = true;
        }

        if (lost_item.isEmpty()) {
            editText_lost_item.setError("Enter What you Lost Please");
            editText_lost_item.requestFocus();
            is_valid_checked = true;
        }


        if (!is_valid_checked) {


            send_to_firestore_lost(getContext(), lost_item,lost_desc);

        }


    }

    private boolean isPhoneValid(String number) {
        return number.contains("0") && number.length() == 11;
    }


    public void send_to_firestore_lost(Context context, String lost_item, String lost_desc) {


        progressBar_posting.setVisibility(View.VISIBLE);
        btn_lost_post.setEnabled(false);
        // String date_and_time;



        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(Constants.FIRESTORE_USER_PROFILE).document(firebaseAuth.getUid()).get().addOnCompleteListener(task -> {

            CollectionReference collectionReference = firebaseFirestore.collection(Constants.FIRESTORE_LOST);
            DocumentReference documentReference = collectionReference.document(DateFormat.format("E-dd-MM-yyyy_hh:mm:ss", new Date(System.currentTimeMillis())).toString());

            if (task.isSuccessful()) {


                Map<String, Object> postMap = new HashMap<>();
                postMap.put("userId", firebaseAuth.getCurrentUser().getUid());
                postMap.put("lostItem", lost_item);
                postMap.put("description", lost_desc);
                postMap.put("phoneNumber", task.getResult().getString(Constants.FIRESTORE_PHONE));
                postMap.put("email", firebaseAuth.getCurrentUser().getEmail());
                postMap.put("date_time",  System.currentTimeMillis());
                postMap.put("document_id", documentReference.getId());
                postMap.put("userName", task.getResult().getString(Constants.FIRESTORE_USERNAME));
                postMap.put("item_image_url", "item_image_url");


                documentReference.set(postMap).addOnCompleteListener(task12 -> {
                    if (task12.isSuccessful()) {
                        Toast.makeText(context, " LOST ITEM POSTED ONLINE ", Toast.LENGTH_LONG).show();
                        navController.navigate(R.id.mainFragment);
                        progressBar_posting.setVisibility(View.GONE);

                    } else {
                        Map<String, Object> postMap1 = new HashMap<>();

                        postMap1.put("userId", firebaseAuth.getCurrentUser().getUid());
                        postMap1.put("lostItem", lost_item);
                        postMap1.put("description", lost_desc);
                        postMap1.put("phoneNumber", task.getResult().getString(Constants.FIRESTORE_PHONE));
                        postMap1.put("email", firebaseAuth.getCurrentUser().getEmail());
                        postMap1.put("date_time", System.currentTimeMillis());
                        postMap1.put("document_id", documentReference.getId());
                        postMap1.put("userName", task.getResult().getString(Constants.FIRESTORE_USERNAME));
                        postMap1.put("item_image_url", "item_image_url");


                        documentReference.set(postMap1).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {

                                        Toast.makeText(context, " LOST ITEM POSTED ONLINE ", Toast.LENGTH_LONG).show();
                                        // navController.navigate(R.id.lostFragment);


                                        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
                                            @Override
                                            public void handleOnBackPressed() {
                                                NavOptions navOptions = new NavOptions.Builder()
                                                        .setPopUpTo(R.id.mainFragment, true).build();
                                                navController.navigate(R.id.mainFragment
                                                        , null, navOptions);
                                            }
                                        };
                                        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
                                        // the callback can enabled or diabled here or in handledonbackpressed()




//                            navController.popBackStack(R.id.postLostFragment, true);
//                            navController.navigate(R.id.lostFragment);


                                progressBar_posting.setVisibility(View.GONE);

                            }
                        });
                    }

                });


                            } else {

                                //Firebase Exception

                            }

                        }).addOnFailureListener(e ->
                                Toast.makeText(context, "Error -> " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        btn_lost_post.setEnabled(true);

                    }

                }

