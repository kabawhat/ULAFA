package com.binbashir.ulafa.Util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.binbashir.ulafa.Model.Found_Item;
import com.binbashir.ulafa.Model.Lost_Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Util_class {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    public void send_email(Context context) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        // TODO: 8/24/2021  replsace sir
        String[] recipents = {"kabirubashirbin@gmail.com"};
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, recipents);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Contact Me");
        Intent chooser = Intent.createChooser(intent, "Contact Me Via");
        context.startActivity(chooser);

    }


    public void phone_call(Context context, String phone_number) {
        Intent call_intent = new Intent(Intent.ACTION_DIAL);
        call_intent.setData(Uri.parse("tel:" + phone_number));
        context.startActivity(call_intent);
    }


    public boolean checking_current_user() {

        if (firebaseAuth.getCurrentUser() != null) {
            return true;
        } else {
            return false;
        }
    }


    // if the current user is null or not null get the user id
    public String getCurrentUserIdString() {
        if (checking_current_user()) {
            return firebaseAuth.getCurrentUser().getUid();
        } else {
            checking_current_user();
            return firebaseAuth.getCurrentUser().getUid();
        }
    }


    public void send_to_backup_delete_found_item(Context context, Found_Item found_item) {

        CollectionReference collectionReference = firebaseFirestore.collection(Constants.FIRESTORE_DELETED_FOUND);
        DocumentReference documentReference = collectionReference.document(found_item.getDocument_id());

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("userId", found_item.getUserId());
        postMap.put("description", found_item.getDescription());
        postMap.put("phoneNumber", found_item.getPhoneNumber());
        postMap.put("date_time", found_item.getDate_time());
        postMap.put("document_id", found_item.getDocument_id());
        postMap.put("userName", found_item.getUserName());






        documentReference.set(postMap).addOnCompleteListener(task12 -> {
            if (task12.isSuccessful()) {
                deleteDocument(context, Constants.FIRESTORE_FOUND, found_item.getDocument_id());

            }

        }).addOnFailureListener(e ->
                Toast.makeText(context, "Error -> " + e.getMessage(), Toast.LENGTH_SHORT).show());


    }

    public void send_to_backup_delete_lost_item(Context context, Lost_Item lostItem) {

        CollectionReference collectionReference = firebaseFirestore.collection(Constants.FIRESTORE_DELETED_LOST);
        DocumentReference documentReference = collectionReference.document(lostItem.getDocument_id());


        Map<String, Object> postMap = new HashMap<>();
        postMap.put("userId", lostItem.getUserId());
        postMap.put("lostItem", lostItem.getLostItem());
        postMap.put("description", lostItem.getDescription());
        postMap.put("phoneNumber", lostItem.getPhoneNumber());
        postMap.put("email", lostItem.getEmail());
        postMap.put("date_time", lostItem.getDate_time());
        postMap.put("document_id", lostItem.getDocument_id());
        postMap.put("userName", lostItem.getUserName());
        postMap.put("item_image_url", lostItem.getItem_image_url());


        documentReference.set(postMap).addOnCompleteListener(task12 -> {
            if (task12.isSuccessful()) {
                deleteDocument(context, Constants.FIRESTORE_LOST, lostItem.getDocument_id());

            }

        }).addOnFailureListener(e ->
                Toast.makeText(context, "Error -> " + e.getMessage(), Toast.LENGTH_SHORT).show());


    }


    public void deleteDocument(Context context, String collection, String document_id) {


        firebaseFirestore.collection(collection)
                .document(document_id)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Removed", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

}

