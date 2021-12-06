package com.binbashir.ulafa.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.binbashir.ulafa.Adapters.FoundAdapter;
import com.binbashir.ulafa.Interfaces.OnFoundClicked;
import com.binbashir.ulafa.Model.Found_Item;
import com.binbashir.ulafa.R;
import com.binbashir.ulafa.Util.Constants;
import com.binbashir.ulafa.Util.Util_class;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FoundFragment extends Fragment implements OnFoundClicked {

    FloatingActionButton fragment_found_fab;
    NavController navController;

    private ArrayList<Found_Item> itemArrayList;
    private FoundAdapter foundAdapter;
    private FirebaseFirestore db;
    private RecyclerView foundRecyclerView;
    ProgressBar loadingPB;

    public FoundFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_found, container, false);
        foundRecyclerView = view.findViewById(R.id.fragment_found_recyclerview);
        fragment_found_fab = view.findViewById(R.id.fragment_found_fab);
        loadingPB = view.findViewById(R.id.idProgressBar);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: 8/7/2021 real name controller
        navController = Navigation.findNavController(getActivity(), R.id.fragment_container);


        fragment_found_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new Util_class().checking_current_user()) {
                    navController.navigate(R.id.action_mainFragment_to_postFoundFragment);


                } else {
                    navController.navigate(R.id.loginFragment);


                }
            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();


    db = FirebaseFirestore.getInstance();


        // creating our new array list
        itemArrayList = new ArrayList<>();
        foundRecyclerView.setHasFixedSize(true);
        foundRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // adding our array list to our recycler view adapter class.
        foundAdapter = new FoundAdapter(itemArrayList, getContext(),this);

        // setting adapter to our recycler view.
        foundRecyclerView.setAdapter(foundAdapter);



        db.collection(Constants.FIRESTORE_FOUND).orderBy("date_time", Query.Direction.ASCENDING)
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
                                    itemArrayList.add(dc.getNewIndex(),
                                            dc.getDocument().toObject(Found_Item.class));
                                    foundAdapter.notifyItemInserted(dc.getNewIndex());
                                    break;
                                case MODIFIED:
                                    if (dc.getOldIndex() == dc.getNewIndex()) {
                                        // Item changed but remained in same position
                                        itemArrayList.set(dc.getOldIndex(), dc.getDocument().toObject(Found_Item.class));
                                        foundAdapter.notifyItemChanged(dc.getOldIndex());
                                    } else {
                                        // Item changed and changed position
                                        itemArrayList.remove(dc.getOldIndex());
                                        itemArrayList.add(dc.getNewIndex(), dc.getDocument().toObject(Found_Item.class));
                                        foundAdapter.notifyItemMoved(dc.getOldIndex(), dc.getNewIndex());
                                    }
                                    break;
                                case REMOVED:
                                    itemArrayList.remove(dc.getOldIndex());
                                    foundAdapter.notifyItemRemoved(dc.getOldIndex());
                                    break;
                            }
                            loadingPB.setVisibility(View.GONE);
                        }
                    }
                });

}

    @Override
    public void onDeletePostClicked(Found_Item found_item) {

            FlatDialog flatDialog = new FlatDialog(getContext());
            flatDialog.setCancelable(true);


            flatDialog.setTitle("Delete post")

                    .setSubtitle("Are You Sure You Want To Delete =>  this post")
                    .setFirstButtonText("Delete")
                    .setSecondButtonText("Cancel")


                    .setBackgroundColor((ContextCompat.getColor(getContext(), R.color.colorPrimary)))
                    .setFirstButtonColor((ContextCompat.getColor(getContext(), R.color.green1)))
                    .setSecondButtonColor(ContextCompat.getColor(getContext(), R.color.red1))



                    .withFirstButtonListner(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //  first method her
                            new Util_class().send_to_backup_delete_found_item(getContext(), found_item);

                            flatDialog.dismiss();
                        }
                    })

                    .withSecondButtonListner(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            flatDialog.dismiss();
                        }
                    }).show();


    }

    @Override
    public void onViewMessageClicked(Found_Item found_item) {
        Bundle doc_id_bundle = new Bundle();
        doc_id_bundle.putString("doc_id", (found_item.getDocument_id()));
        navController.navigate(R.id.action_mainFragment_to_foundChatFragment, doc_id_bundle);


    }
}