package com.binbashir.ulafa.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.binbashir.ulafa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    EditText editText_email;
    EditText editText_password;
    Button button_login;
    TextView text_goto_register;
    ProgressBar progressBar_login;
    NavController navController;


    public LoginFragment() {
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        editText_email = view.findViewById(R.id.login_edit_email);
        editText_password = view.findViewById(R.id.login_edit_password);
        button_login = view.findViewById(R.id.login_btn_login);
        text_goto_register = view.findViewById(R.id.login_txt_register);
        progressBar_login = view.findViewById(R.id.login_progress);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);


        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginWithEmail();
            }
        });

        text_goto_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.registerFragment);
            }
        });


    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private void loginWithEmail() {

        String username = editText_email.getText().toString().trim();
        String passworld = editText_password.getText().toString().trim();

        boolean Invalid_check = false;


        if (passworld.isEmpty()) {
            editText_password.setError(" please Enter your password ");
            editText_password.requestFocus();
            Invalid_check = true;
        }
        if (TextUtils.isEmpty(username)) {
            editText_email.setError(" Email is Empty ");
            editText_email.requestFocus();
            Invalid_check = true;
        } else if (!isEmailValid(username)) {
            editText_email.setError("Invalid Email");
            editText_email.requestFocus();
            Invalid_check = true;

        }


        if (!Invalid_check) {
            progressBar_login.setVisibility(View.VISIBLE);
            button_login.setEnabled(false);

            FirebaseAuth mAuth = FirebaseAuth.getInstance();



            mAuth.signInWithEmailAndPassword(username, passworld).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // Checking the is task is sucessfully or not
                    if (task.isSuccessful()) {

                        progressBar_login.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Logged In Successfully", Toast.LENGTH_SHORT).show();

                        navController.popBackStack();
                        navController.popBackStack();


                    } else {


                        Toast.makeText(getActivity(), "No internet Connection, Please Try again", Toast.LENGTH_SHORT).show();
                        progressBar_login.setVisibility(View.GONE);
                        button_login.setEnabled(true);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}