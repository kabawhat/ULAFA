package com.binbashir.ulafa.Util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.binbashir.ulafa.R;

public class Util_Temp {


    public void forgotten_password(Activity activity , Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Type your Mail Address here");
        final LayoutInflater inflater =  activity.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.changelayout, null);
        final EditText email_text = (EditText) add_menu_layout.findViewById(R.id.change_layout_edittext);
        email_text.setHint("Enter Your Mail");
        alertDialog.setView(add_menu_layout);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(email_text.getText().toString().trim())){




                }
            else {

                email_text.setError("oieroier");
                    Toast.makeText(activity, "Please Provide Your Mail Address", Toast.LENGTH_LONG).show();
                }


            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();


    }


    // TODO: 8/25/2021 please check this code sir
//    yourDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//        @Override
//        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    Map<String, Object> map = document.getData();
//                    if (map.size() == 0) {
//                        Log.d(TAG, "Document is empty!");
//                    } else {
//                        Log.d(TAG, "Document is not empty!");
//                    }
//                }
//            }
//        }
//    });


}
