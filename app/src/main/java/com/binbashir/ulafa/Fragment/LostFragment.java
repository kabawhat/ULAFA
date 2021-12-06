package com.binbashir.ulafa.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aminography.choosephotohelper.ChoosePhotoHelper;
import com.binbashir.ulafa.Adapters.LostAdapter;
import com.binbashir.ulafa.Interfaces.OnLostClicked;
import com.binbashir.ulafa.Model.Lost_Item;
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

public class LostFragment extends Fragment implements OnLostClicked {


    // creating variables for our recycler view,
    // array list, adapter, firebase firestore
    // and our progress bar.
    private RecyclerView lostRecyclerView;
    private ArrayList<Lost_Item> lostItemArrayList;
    private LostAdapter lostAdapter;
    private FirebaseFirestore db;
    ProgressBar loadingPB;
    private ChoosePhotoHelper choosePhotoHelper;
    NavController navController;

    private FloatingActionButton fragment_lost_fab;

    public LostFragment() {
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
        View view = inflater.inflate(R.layout.fragment_lost, container, false);

        // initializing our variables.
        lostRecyclerView = view.findViewById(R.id.fragment_lost_recyclerview);
        loadingPB = view.findViewById(R.id.idProgressBar);
        fragment_lost_fab = view.findViewById(R.id.fragment_lost_fab);

        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // TODO: 8/7/2021 real name controller
        navController = Navigation.findNavController(getActivity(), R.id.fragment_container);
        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();


        // creating our new array list
        lostItemArrayList = new ArrayList<>();
        lostRecyclerView.setHasFixedSize(true);
        lostRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // adding our array list to our recycler view adapter class.
        lostAdapter = new LostAdapter(lostItemArrayList, getContext(), this);

        // setting adapter to our recycler view.
        lostRecyclerView.setAdapter(lostAdapter);


        db.collection(Constants.FIRESTORE_LOST).orderBy("date_time", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                         @Override
                                         public void onEvent(@Nullable QuerySnapshot snapshots,
                                                             @Nullable FirebaseFirestoreException e) {


                                             if (e != null) {
                                                 Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                 loadingPB.setVisibility(View.GONE);
                                                 return;
                                             }

                                             for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                                 switch (dc.getType()) {
                                                     case ADDED:
                                                         lostItemArrayList.add(dc.getNewIndex(),
                                                                 dc.getDocument().toObject(Lost_Item.class));
                                                         lostAdapter.notifyItemInserted(dc.getNewIndex());
                                                         break;
                                                     case MODIFIED:
                                                         if (dc.getOldIndex() == dc.getNewIndex()) {
                                                             // Item changed but remained in same position
                                                             lostItemArrayList.set(dc.getOldIndex(), dc.getDocument().toObject(Lost_Item.class));
                                                             lostAdapter.notifyItemChanged(dc.getOldIndex());
                                                         } else {
                                                             // Item changed and changed position
                                                             lostItemArrayList.remove(dc.getOldIndex());
                                                             lostItemArrayList.add(dc.getNewIndex(), dc.getDocument().toObject(Lost_Item.class));
                                                             lostAdapter.notifyItemMoved(dc.getOldIndex(), dc.getNewIndex());
                                                         }
                                                         break;
                                                     case REMOVED:
                                                         lostItemArrayList.remove(dc.getOldIndex());
                                                         lostAdapter.notifyItemRemoved(dc.getOldIndex());
                                                         break;
                                                 }

                                                 loadingPB.setVisibility(View.GONE);


                                             }

                                         }
                                     }
                );


        fragment_lost_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (new Util_class().checking_current_user()) {
                    navController.navigate(R.id.postLostFragment);


                } else {
                    navController.navigate(R.id.loginFragment);

                }


            }
        });


    }

    @Override
    public void onAddImageClicked(String  doc_id,String image_url ) {
        Bundle image_bundle = new Bundle();
        image_bundle.putString("document_id", doc_id);
        image_bundle.putString("image_url", image_url);
        navController.navigate(R.id.action_mainFragment_to_addImageFragment, image_bundle);


    }


    @Override
    public void onDeletePostClicked(Lost_Item lostItem) {


        FlatDialog flatDialog = new FlatDialog(getContext());
        flatDialog.setCancelable(true);


        flatDialog.setTitle("Remove post")

                .setSubtitle("Are You Sure You Want To Remove =>  this post")
                .setFirstButtonText("Found")
                .setSecondButtonText("Delete")
                .setThirdButtonText("Cancel")

                .setBackgroundColor((ContextCompat.getColor(getContext(), R.color.colorPrimary)))
                .setFirstButtonColor((ContextCompat.getColor(getContext(), R.color.green1)))
                .setSecondButtonColor(ContextCompat.getColor(getContext(), R.color.red1))
                .setSecondButtonColor(ContextCompat.getColor(getContext(), R.color.pink))


                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //  first method her
                        new Util_class().send_to_backup_delete_lost_item(getContext(), lostItem);

                        flatDialog.dismiss();
                    }
                })
                .withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Util_class().deleteDocument(getContext(), Constants.FIRESTORE_LOST, lostItem.getDocument_id());
                        flatDialog.dismiss();

                    }
                })
                .withThirdButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        flatDialog.dismiss();
                    }
                }).show();

    }

    @Override
    public void onViewParentClicked(Lost_Item lostItem) {
        navController.navigate(R.id.action_mainFragment_to_viewImageFragment, item_bundle(lostItem));

    }

    @Override
    public void onViewMessageClicked(Lost_Item lostItem) {

        Bundle doc_id_bundle = new Bundle();
        doc_id_bundle.putString("doc_id", (lostItem.getDocument_id()));
        navController.navigate(R.id.action_mainFragment_to_messageFragment, doc_id_bundle);

    }


    private Bundle item_bundle(Lost_Item lostItem) {
        Bundle item_bundle = new Bundle();
        item_bundle.putString("userId", (lostItem.getUserId()));
        item_bundle.putString("description", (lostItem.getDescription()));
        item_bundle.putString("phoneNumber", (lostItem.getPhoneNumber()));
        item_bundle.putLong("date_time", (lostItem.getDate_time()));
        item_bundle.putString("document_id", (lostItem.getDocument_id()));
        item_bundle.putString("userName", (lostItem.getUserName()));
        item_bundle.putString("lostItem", (lostItem.getLostItem()));
        item_bundle.putString("email", (lostItem.getEmail()));
        item_bundle.putString("item_image_url", (lostItem.getItem_image_url()));
        return item_bundle;
    }
}

