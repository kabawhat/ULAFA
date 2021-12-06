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
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.binbashir.ulafa.R;
import com.binbashir.ulafa.Util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    EditText editText_register_username;
    EditText editText_register_adm_number;
    EditText editText_register_email;
    EditText editText_register_password;
    EditText editText_register_confirm_password;
    EditText editText_register_phone;

    Button  button_registeration_button;
    TextView textView_goto_login;
    ProgressBar progressBar_register;



    NavController navController;

    public RegisterFragment() {
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        editText_register_username = view.findViewById(R.id.register_edit_username);
        editText_register_adm_number = view.findViewById(R.id.register_edit_adm_numb);
        editText_register_email = view.findViewById(R.id.register_edit_email);
        editText_register_password = view.findViewById(R.id.register_edit_password);
        editText_register_confirm_password = view.findViewById(R.id.register_edit_confirm_password);
        button_registeration_button = view.findViewById(R.id.register_btn_register);
        progressBar_register  = view.findViewById(R.id.register_progress);
        textView_goto_login = view.findViewById(R.id.register_txt_goto_login);
        editText_register_phone = view.findViewById(R.id.register_edit_phone);


        return view;
    }

    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        button_registeration_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               attemptRegistration();
            }
        });

        textView_goto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.loginFragment, true).build();
                navController.navigate(R.id.action_registerFragment_to_loginFragment
                        , null, navOptions);


            }
        });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isAdmNoValid(String admno) {
        return admno.length() == 10;
    }
    private boolean isPhoneValid(String number) {
        return number.contains("0") && number.length() == 11;
    }



    private boolean isPasswordValid(String password) {
        String confirmPassworld = editText_register_confirm_password.getText().toString().trim();
        return confirmPassworld.equalsIgnoreCase(password) && password.length() > 4;
    }

    private void attemptRegistration() {
        // Store values at the time of the login attempt.
        String username = editText_register_username.getText().toString().trim();
        String admno = editText_register_adm_number.getText().toString().trim();
        String email = editText_register_email.getText().toString().trim();
        String pass = editText_register_password.getText().toString().trim();
        String confirm_pass = editText_register_confirm_password.getText().toString().trim();
        String phone_number = editText_register_phone.getText().toString().trim();

        // the boolean to test case here
        boolean Invalid_check = false;
        // View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(pass) && !isPasswordValid(pass)) {
            editText_register_confirm_password.setError(" MisMatch Or password is  less than four(4) characters ");
            editText_register_confirm_password.requestFocus();
            Invalid_check = true;
        }


        if(confirm_pass.isEmpty()){
            editText_register_confirm_password.setError(" Please Enter Confirmation Password ");
            editText_register_confirm_password.requestFocus();
            Invalid_check = true;
        }



        if (pass.isEmpty()) {
            editText_register_password.requestFocus();
            editText_register_password.setError(" please  Enter your password Here ");
            Invalid_check = true;
        }

        if (TextUtils.isEmpty(phone_number)) {
            editText_register_phone.setError("Phone Number is Empty");
            editText_register_phone.requestFocus();
            Invalid_check = true;
        } else if (!isPhoneValid(phone_number)) {
            editText_register_phone.setError("Invalid Phone Number");
            editText_register_phone.requestFocus();
            Invalid_check = true;
        }



        if (TextUtils.isEmpty(email)) {
            editText_register_email.setError(" Email is Empty ");
            editText_register_email.requestFocus();
            Invalid_check = true;
        } else if (!isEmailValid(email)) {
            editText_register_email.setError("Invalid Email");
            editText_register_email.requestFocus();
            Invalid_check = true;
        }

        if (TextUtils.isEmpty(admno)) {
            editText_register_adm_number.setError(" Admission Number is Empty ");
            editText_register_adm_number.requestFocus();
            Invalid_check = true;
        } else if (!isAdmNoValid(admno)) {
            editText_register_adm_number.setError("Invalid Admission Number");
            editText_register_adm_number.requestFocus();
            Invalid_check = true;
        }



        if(username.isEmpty()){
            editText_register_username.setError(" please enter Username Here");
            editText_register_username.requestFocus();
            Invalid_check = true;
        }



        if (Invalid_check) {

        } else {

           FirebaseAuth mAuth = FirebaseAuth.getInstance();
            button_registeration_button.setEnabled(false);
            progressBar_register.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // adding the user to database here
                        String user_id = mAuth.getCurrentUser().getUid();
                       saveUSerDetailToFirestore(user_id + "",username,admno,email,phone_number);
                    }
                    else {
                        snackbar_alert("No internet Connection,  Please Try again");
                        progressBar_register.setVisibility(View.GONE);
                        button_registeration_button.setEnabled(true);


                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure( Exception e) {

                    snackbar_alert(e.getMessage());
                    button_registeration_button.setEnabled(true);
                }
            });

        }


    }
    private void saveUSerDetailToFirestore(String userId,String UserName,String admNO,String email,String phone ){


        final FirebaseFirestore rootRef = FirebaseFirestore.getInstance();


        Map<String, String> userMap = new HashMap<>();
        userMap.put(Constants.FIRESTORE_USERNAME, UserName);
        userMap.put(Constants.FIRESTORE_UID, userId);
        userMap.put(Constants.FIRESTORE_ADM_NO,admNO );
        userMap.put(Constants.FIRESTORE_EMAIL, email);
        userMap.put(Constants.FIRESTORE_PHONE, phone);
        rootRef.collection(Constants.FIRESTORE_USER_PROFILE).document(userId).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(getContext(), "Signed-Up Successfully", Toast.LENGTH_SHORT).show();
                  
                    progressBar_register.setVisibility(View.GONE);
                   navController.popBackStack();
                    navController.popBackStack();




                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getContext(), "Email already exist", Toast.LENGTH_SHORT).show();
                        progressBar_register.setVisibility(View.GONE);
                        button_registeration_button.setEnabled(true);
                    } else {

                        Toast.makeText(getContext(), task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                        progressBar_register.setVisibility(View.GONE);
                        button_registeration_button.setEnabled(true);

                    }

                }

            }
        });


    }
    private void snackbar_alert(String message) {
        Snackbar.make(button_registeration_button, message, Snackbar.LENGTH_LONG).show();

    }
}