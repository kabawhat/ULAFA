package com.binbashir.ulafa.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.binbashir.ulafa.R;
import com.binbashir.ulafa.Util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddFoundItemsFragment extends Fragment {


    EditText editText_found_description;
    Button btn_found_post;
    FirebaseAuth firebaseAuth;
    NavController navController;

    public AddFoundItemsFragment() {
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

        View view = inflater.inflate(R.layout.fragment_post_found, container, false);


        editText_found_description = view.findViewById(R.id.found_edit_description);
        btn_found_post = view.findViewById(R.id.found_btn_post);
        return view;


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);


        firebaseAuth = FirebaseAuth.getInstance();


        btn_found_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDetails();
                navController.navigate(R.id.mainFragment);
            }
        });
    }

    void saveDetails() {

        boolean is_valid_checked = false;

        String found_desc = editText_found_description.getText().toString().trim();

        if (found_desc.isEmpty()) {
            editText_found_description.setError("Enter Description,last location");
            editText_found_description.requestFocus();
            is_valid_checked = true;
        }


        if (!is_valid_checked) {

            sent_to_firestore_found(getContext(),found_desc);



        }


    }






    public void sent_to_firestore_found(Context context, String desc) {


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();




        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(Constants.FIRESTORE_USER_PROFILE).document(firebaseAuth.getUid()).get().addOnCompleteListener(task -> {


            CollectionReference collectionReference = firebaseFirestore.collection(Constants.FIRESTORE_FOUND);

            DocumentReference documentReference = collectionReference.document();


            if (task.isSuccessful()) {

                Map<String, Object> postMap = new HashMap<>();
                postMap.put("userId", firebaseAuth.getUid());
                postMap.put("description", desc);
                postMap.put("phoneNumber", task.getResult().getString(Constants.FIRESTORE_PHONE));
                postMap.put("date_time", System.currentTimeMillis());
                postMap.put("document_id", documentReference.getId());
                postMap.put("userName", task.getResult().getString(Constants.FIRESTORE_USERNAME));

                documentReference.set(postMap).addOnCompleteListener(task12 -> {
                    if (task12.isSuccessful()) {
                        Toast.makeText(context, " ITEM POSTED SUCCESSFULLY", Toast.LENGTH_LONG).show();

                         navController.navigate(R.id.mainFragment);

                    } else {
                        Map<String, Object> postMap1 = new HashMap<>();

                        postMap1.put("userId", firebaseAuth.getUid());
                        postMap1.put("description", desc);
                        postMap1.put("phoneNumber", task.getResult().getString(Constants.FIRESTORE_PHONE));
                        postMap1.put("date_time", System.currentTimeMillis());
                        postMap1.put("document_id", documentReference.getId());
                        postMap1.put("userName", task.getResult().getString(Constants.FIRESTORE_USERNAME));

                        documentReference.set(postMap1).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(context, " ITEM POSTED SUCCESSFULLY ", Toast.LENGTH_LONG).show();
                                navController.navigate(R.id.mainFragment);

                            }
                        });
                    }

                });


            } else {

                //Firebase Exception

            }

        }).addOnFailureListener(e ->
                Toast.makeText(context, "Error -> " + e.getMessage(), Toast.LENGTH_SHORT).show());

    }

}

