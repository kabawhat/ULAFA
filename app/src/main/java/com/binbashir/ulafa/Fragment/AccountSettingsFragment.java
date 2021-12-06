package com.binbashir.ulafa.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.binbashir.ulafa.R;
import com.binbashir.ulafa.Util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AccountSettingsFragment extends Fragment {


    private ProgressBar progressBar;
    private ImageView userImg;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fireStore;
    private EditText Name, phone;
    Button Submit;
    private String currentUserId;
    Button logout_btn;
    Button login_btn;

    NavController navController;

    public AccountSettingsFragment() {
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

        View view = inflater.inflate(R.layout.fragment_account_settings, container, false);
        Submit = view.findViewById(R.id.submit);
        progressBar = view.findViewById(R.id.AccountSettingsBar);
        Name = view.findViewById(R.id.account_edit_username);
        phone = view.findViewById(R.id.account_edit_phone);
        userImg = view.findViewById(R.id.profile);
        logout_btn = view.findViewById(R.id.logout);
        login_btn = view.findViewById(R.id.setup_account_login);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        if (checking_current_user()) {
            progressBar.setVisibility(View.VISIBLE);

            //Evertime settings load check if data is already present in FireStore, if yes retrive and set name and image using glide
            fireStore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            Name.setText(task.getResult().getString(Constants.FIRESTORE_USERNAME));
                            phone.setText(task.getResult().getString(Constants.FIRESTORE_PHONE));
                            progressBar.setVisibility(View.INVISIBLE);

                        } else {
                            Toast.makeText(getContext(), "NO DATA EXISTS", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });


            logout_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logout();
                }
            });


            Submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String uName = Name.getText().toString();
                    final String uPhone = phone.getText().toString();

                    boolean Invalid_check = false;

                    if (TextUtils.isEmpty(uPhone)) {
                        phone.setError("Phone Number is Empty");
                        phone.requestFocus();
                        Invalid_check = true;
                    } else if (!isPhoneValid(uPhone)) {
                        phone.setError("Invalid Phone Number");
                        phone.requestFocus();
                        Invalid_check = true;
                    }

                    if (uName.isEmpty()) {
                        Name.setError(" please enter Username Here");
                        Name.requestFocus();
                        Invalid_check = true;
                    }
                    if (Invalid_check) {

                    } else {

                        progressBar.setVisibility(View.VISIBLE);
                        saveToFirestore(uName, uPhone);

                    }
                }

            });


        } else {


            logout_btn.setVisibility(View.GONE);
            Name.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
            Submit.setVisibility(View.GONE);

            login_btn.setVisibility(View.VISIBLE);

            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(getContext(), "login here", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.loginFragment);
                }
            });

        }

    }

    //    private void saveToFirestore(@NonNull Task<UploadTask.TaskSnapshot> task,String uName) {
    private void saveToFirestore(final String uName, String phone) {

        HashMap<String, Object> update = new HashMap<>();
        update.put(Constants.FIRESTORE_USERNAME, uName);
        update.put(Constants.FIRESTORE_PHONE, phone);
        fireStore.collection("USERS").document(currentUserId).update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    navController.navigate(R.id.mainFragment);
                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        navController.navigate(R.id.mainFragment);

        // Toast.makeText(getActivity(), "Login in here ", Toast.LENGTH_LONG).show();
    }

    private boolean checking_current_user() {
        if (firebaseAuth.getCurrentUser() != null) {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
            return true;
        } else {
            return false;
        }
    }


    private void logout() {

        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.signOut();
        }

        Toast.makeText(this.getActivity(), "Log Out", Toast.LENGTH_SHORT).show();
        navController.navigate(R.id.mainFragment);
    }

    private boolean isPhoneValid(String number) {
        return number.contains("0") && number.length() == 11;
    }


}