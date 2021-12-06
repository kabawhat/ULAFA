package com.binbashir.ulafa.Fragment;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.binbashir.ulafa.Adapters.LostNotificationAdapter;
import com.binbashir.ulafa.Model.Notification_Item;
import com.binbashir.ulafa.R;
import com.binbashir.ulafa.Util.Constants;
import com.binbashir.ulafa.Util.Util_class;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LostChatFragment extends Fragment {


    private RecyclerView fragment_notification_RecyclerView;
    private ArrayList<Notification_Item> notification_items;
    private LostNotificationAdapter lostNotificationAdapter;
    private FirebaseFirestore db;
    private ImageView send_image_view;
    private EditText desc_edittext;
    String document_id;
    private NavController navController;


    public LostChatFragment() {
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
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        // initializing our variables.
        fragment_notification_RecyclerView = view.findViewById(R.id.fragment_notification_recycler);
         send_image_view = view.findViewById(R.id.fragment_lost_chat_image_send);
         desc_edittext = view.findViewById(R.id.fragment_lost_chat_edit_desc);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull  View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        document_id =  LostChatFragmentArgs.fromBundle(getArguments()).getDocId();



        send_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_message_notification(document_id);
            }
        });


        String documentId = LostChatFragmentArgs.fromBundle(getArguments()).getDocId();

        if (documentId != null) {


            // firestore and getting its instance.
            db = FirebaseFirestore.getInstance();


            // creating our new array list
            notification_items = new ArrayList<>();
            fragment_notification_RecyclerView.setHasFixedSize(true);
            fragment_notification_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // adding our array list to our recycler view adapter class.
            lostNotificationAdapter = new LostNotificationAdapter(notification_items, getContext());

            // setting adapter to our recycler view.
            fragment_notification_RecyclerView.setAdapter(lostNotificationAdapter);


            db.collection(Constants.FIRESTORE_LOST + "/" + documentId + "/" + Constants.FIRESTORE_LOST_MESSAGE)
                    .orderBy("time_reported", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("TAG", "listen:error", e);
                                return;
                            }

                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        notification_items.add(dc.getNewIndex(),
                                                dc.getDocument().toObject(Notification_Item.class));
                                        lostNotificationAdapter.notifyItemInserted(dc.getNewIndex());
                                        break;
                                    case MODIFIED:
                                        if (dc.getOldIndex() == dc.getNewIndex()) {
                                            // Item changed but remained in same position
                                            notification_items.set(dc.getOldIndex(), dc.getDocument().toObject(Notification_Item.class));
                                            lostNotificationAdapter.notifyItemChanged(dc.getOldIndex());
                                        } else {
                                            // Item changed and changed position
                                            notification_items.remove(dc.getOldIndex());
                                            notification_items.add(dc.getNewIndex(), dc.getDocument().toObject(Notification_Item.class));
                                            lostNotificationAdapter.notifyItemMoved(dc.getOldIndex(), dc.getNewIndex());
                                        }
                                        break;
                                    case REMOVED:
                                        notification_items.remove(dc.getOldIndex());
                                        lostNotificationAdapter.notifyItemRemoved(dc.getOldIndex());
                                        break;
                                }

                            }
                        }
                    });

        } else {
            Toast.makeText(getContext(), "No message Received Yet", Toast.LENGTH_SHORT).show();
        }
    }

    public void send_message_notification(String parent_id) {
        if (new Util_class().checking_current_user()) {
            String notification_message = desc_edittext.getText().toString().trim();
            boolean Invalid_check = false;
            if (notification_message.isEmpty() || notification_message.length() < 2) {
                desc_edittext.setError("Message cant be empty");
                desc_edittext.requestFocus();
                Invalid_check = true;
            }
            if (!Invalid_check) {

                sent_notification(notification_message,parent_id);
                send_image_view.setEnabled(false);
            }
        } else {

            navController.navigate(R.id.loginFragment);
        }


    }



    public void sent_notification(String notification_message,String parent_id) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth  = FirebaseAuth.getInstance();
        DocumentReference notifcation_document = firebaseFirestore.collection(Constants.FIRESTORE_LOST)
               // .document(lostChatFragmentArgs.getDocId()).collection(Constants.FIRESTORE_LOST_NOTIFICATION).document(DateFormat.format("E-dd-MM-yyyy_hh:mm:ss", new Date(System.currentTimeMillis())).toString());

          .document(parent_id).collection(Constants.FIRESTORE_LOST_MESSAGE).document(DateFormat.format("E-dd-MM-yyyy_hh:mm:ss", new Date(System.currentTimeMillis())).toString());

        firebaseFirestore.collection(Constants.FIRESTORE_USER_PROFILE).document(new Util_class().getCurrentUserIdString()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                Map<String, Object> notificationMap = new HashMap<>();
                notificationMap.put("document_id",notifcation_document.getId());
                notificationMap.put("message", notification_message);


                notificationMap.put("parent_id", Constants.FIRESTORE_LOST+"/"+parent_id+"/"+Constants.FIRESTORE_LOST_MESSAGE+"/");
                notificationMap.put("time_reported", System.currentTimeMillis());
                notificationMap.put("Phone", task.getResult().getString(Constants.FIRESTORE_PHONE));
                notificationMap.put("username", task.getResult().getString(Constants.FIRESTORE_USERNAME));
                notificationMap.put("admno", task.getResult().getString(Constants.FIRESTORE_ADM_NO));
                notificationMap.put("userid",firebaseAuth.getUid());


                notifcation_document.set(notificationMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        desc_edittext.setText("");
                        send_image_view.setEnabled(true);

                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Snackbar.make(desc_edittext,
                                e.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();


                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                       send_image_view.setEnabled(true);
                        Snackbar.make(desc_edittext,
                                "Notification Sent Successfully ", BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                });


            }
        });
    }


}