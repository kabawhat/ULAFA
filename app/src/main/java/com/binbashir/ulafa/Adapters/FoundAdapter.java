package com.binbashir.ulafa.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.binbashir.ulafa.Interfaces.OnFoundClicked;
import com.binbashir.ulafa.Model.Found_Item;
import com.binbashir.ulafa.R;
import com.binbashir.ulafa.Util.Constants;
import com.binbashir.ulafa.Util.Util_class;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class FoundAdapter extends RecyclerView.Adapter<FoundAdapter.ViewHolder> {


    // creating variables for our ArrayList and context
    private ArrayList<Found_Item> found_items;
    private Context context;

    OnFoundClicked onFoundClicked;

    FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    // creating constructor for our adapter class
    public FoundAdapter(ArrayList<Found_Item> itemArrayList, Context context, OnFoundClicked onFoundClicked) {
        this.onFoundClicked = onFoundClicked;
        this.found_items = itemArrayList;
        this.context = context;

    }


    @NonNull
    @Override
    public FoundAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        // passing our layout file for displaying our card item
        return new FoundAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.found_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FoundAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        firebaseAuth = FirebaseAuth.getInstance();
        Found_Item found_item = found_items.get(position);
        firebaseFirestore = FirebaseFirestore.getInstance();

        holder.text_username.setText(found_item.getUserName());
        holder.text_message.setText(found_item.getDescription());


        try {
            String dateString = DateFormat.format("E-dd-MM-yyyy hh:mm:ss", new Date(found_item.getDate_time())).toString();
            holder.text_date_time.setText(dateString);
        } catch (Exception e) {
            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // this is for setting the imageLetter which is now the number of the
        String firstletter = String.valueOf(position + 1);
        holder.setImageLetter(firstletter, Color.parseColor("#4CAF50"));


        if (new Util_class().checking_current_user()) {

            if (firebaseAuth.getCurrentUser().getUid().equalsIgnoreCase(found_item.getUserId())) {
                holder.linearLayout_poster_parent.setVisibility(View.VISIBLE);
                holder.linearLayout_non_poster_parent.setVisibility(View.GONE);


                holder.linearLayout_poster_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onFoundClicked.onViewMessageClicked(found_item);
                    }
                });

                holder.linearLayout_poster_delete_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onFoundClicked.onDeletePostClicked(found_item);
                    }
                });
            } else {
                holder.linearLayout_non_poster_parent.setVisibility(View.VISIBLE);
                holder.linearLayout_poster_parent.setVisibility(View.GONE);

            }


        } else {

            holder.linearLayout_non_poster_parent.setVisibility(View.VISIBLE);
            holder.linearLayout_poster_parent.setVisibility(View.GONE);

        }

        holder.linearLayout_non_poster_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Util_class().phone_call(context, found_item.getPhoneNumber());

            }
        });
        holder.linearLayout_non_poster_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFoundClicked.onViewMessageClicked(found_item);
            }
        });


        //this is for counting the number comment available
        firebaseFirestore.collection(Constants.FIRESTORE_FOUND + "/" + found_item.getDocument_id() + "/" + Constants.FIRESTORE_FOUND_MESSAGE).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {
                    int count = documentSnapshots.size();
                    holder.notificationCounter(count);

                } else {
                    holder.notificationCounter(0);

                }

            }
        });


    }

    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return found_items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.

        private TextView text_username;
        private TextView text_date_time;
        private TextView text_message;
        private TextDrawable drawableh;

        private LinearLayout linearLayout_non_poster_parent;
        private LinearLayout linearLayout_poster_parent;
        private LinearLayout linearLayout_non_poster_call;
        private LinearLayout linearLayout_non_poster_message;
        private LinearLayout linearLayout_poster_message;
        private LinearLayout linearLayout_poster_delete_post;

        TextView text_message_counter_non_poster;
        TextView text_message_counter_poster;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.

            text_message_counter_poster = itemView.findViewById(R.id.found_item_text_poster_message_counter);
            text_message_counter_non_poster = itemView.findViewById(R.id.found_item_text_non_poster_message_counter);
            text_username = itemView.findViewById(R.id.found_item_txt_item_username);
            text_date_time = itemView.findViewById(R.id.found_item_txt_item_date_time);
            text_message = itemView.findViewById(R.id.found_item_txt_item_message);
            linearLayout_non_poster_parent = itemView.findViewById(R.id.found_item_linear_non_poster_parent);
            linearLayout_poster_parent = itemView.findViewById(R.id.found_item_linear_poster_parent);
            linearLayout_non_poster_call = itemView.findViewById(R.id.found_item_linear_non_poster_call);
            linearLayout_non_poster_message = itemView.findViewById(R.id.found_item_linear_non_poster_message);
            linearLayout_poster_message = itemView.findViewById(R.id.found_item_linear_poster_message);
            linearLayout_poster_delete_post = itemView.findViewById(R.id.found_item_linear_poster_delete_post);


        }

        public void setImageLetter(String firstletter, int color) {
            drawableh = TextDrawable.builder()
                    .buildRound(firstletter, color);
            ImageView rounded_image = itemView.findViewById(R.id.found_item_image_item_count);
            rounded_image.setImageDrawable(drawableh);
        }


        public void notificationCounter(int count) {


            if (count <= 1) {
                text_message_counter_non_poster.setText(count + " Message");
                text_message_counter_poster.setText(count + " Message");
            } else {

                text_message_counter_non_poster.setText(count + " Messages");
                text_message_counter_poster.setText(count + " Messages");

            }
        }
    }
}
