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
import com.binbashir.ulafa.Interfaces.OnLostClicked;
import com.binbashir.ulafa.Model.Lost_Item;
import com.binbashir.ulafa.R;
import com.binbashir.ulafa.Util.Constants;
import com.binbashir.ulafa.Util.Util_class;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class LostAdapter
        extends RecyclerView.Adapter<LostAdapter.ViewHolder> {


    // creating variables for our ArrayList and context
    private ArrayList<Lost_Item> lostItemArrayList;
    private Context context;
    OnLostClicked onLostClicked;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    // creating constructor for our adapter class
    public LostAdapter(ArrayList<Lost_Item> lostItemArrayList, Context context, OnLostClicked onLostClicked) {
        this.lostItemArrayList = lostItemArrayList;
        this.context = context;
        this.onLostClicked = onLostClicked;
    }

    @NonNull
    @Override
    public LostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LostAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        Lost_Item lostItem = lostItemArrayList.get(position);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        holder.text_item.setText(lostItem.getLostItem());
        holder.text_desc.setText(lostItem.getDescription());
        holder.text_phone.setText(lostItem.getPhoneNumber());
        holder.text_username.setText(lostItem.getUserName());

        try {
            String dateString = DateFormat.format("E-dd-MM-yyyy hh:mm:ss", new Date(lostItem.getDate_time())).toString();
            holder.text_date_time.setText(dateString);
        } catch (Exception e) {
            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // this is for setting the imageLetter which is now the number of the
        String firstletter = String.valueOf(position + 1);
        holder.setImageLetter(firstletter, Color.parseColor("#4CAF50"));


        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.no_image_available)
                .format(DecodeFormat.PREFER_RGB_565)
                .fitCenter();

        Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(lostItemArrayList.get(position).getItem_image_url())
                .thumbnail(0.1f)
                .into(holder.imageView_image);


        if (new Util_class().checking_current_user()) {

            if (new Util_class().getCurrentUserIdString().equalsIgnoreCase(lostItem.getUserId())) {
                holder.linear_options.setVisibility(View.VISIBLE);
                holder.linear_unknown.setVisibility(View.GONE);

                holder.linear_messages.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onLostClicked.onViewMessageClicked(lostItem);
                    }
                });

                holder.linear_add_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onLostClicked.onAddImageClicked(lostItem.getDocument_id(), lostItem.getItem_image_url()
                        );
                    }
                });
                holder.linear_delete_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onLostClicked.onDeletePostClicked(lostItem);
                    }
                });


            } else {
                holder.linear_unknown.setVisibility(View.VISIBLE);
                holder.linear_options.setVisibility(View.GONE);

            }

        } else {
            holder.linear_unknown.setVisibility(View.VISIBLE);
            holder.linear_options.setVisibility(View.GONE);
        }
        holder.linear_call_messsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLostClicked.onViewMessageClicked(lostItem);
            }
        });

        holder.linear_parent_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLostClicked.onViewParentClicked(lostItem);
            }
        });
        holder.linear_call_unknown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Util_class().phone_call(context, lostItem.getPhoneNumber());
            }
        });


        //this is for counting the number comment available
        firebaseFirestore.collection(Constants.FIRESTORE_LOST + "/" + lostItem.getDocument_id() + "/" + Constants.FIRESTORE_LOST_MESSAGE).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        return lostItemArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.

        private TextView text_item;
        private TextView text_desc;
        private TextView text_phone;
        private TextView text_username;
        private TextView text_date_time;
        private TextDrawable drawableh;
        private LinearLayout linear_add_image;
        private LinearLayout linear_delete_post;
        private LinearLayout linear_parent_item;
        private LinearLayout linear_messages;
        private TextView txt_count_message;
        private TextView txt_count_message_unknown;
        private LinearLayout linear_options;
        private LinearLayout linear_unknown;
        private LinearLayout linear_call_unknown;
        private LinearLayout linear_call_messsage;


        private ImageView imageView_image;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            text_item = itemView.findViewById(R.id.single_item_text_item);
            text_desc = itemView.findViewById(R.id.single_item_text_desc);
            text_phone = itemView.findViewById(R.id.single_item_text_phone);
            text_username = itemView.findViewById(R.id.single_item_text_username);
            text_date_time = itemView.findViewById(R.id.single_item_text_time);
            linear_add_image = itemView.findViewById(R.id.single_item_linear_add_image);
            linear_delete_post = itemView.findViewById(R.id.single_item_linear_delete_post);
            imageView_image = itemView.findViewById(R.id.single_item_imageView_image);
            linear_parent_item = itemView.findViewById(R.id.single_item_linear_parent);
            linear_messages = itemView.findViewById(R.id.single_item_linear_messages_linear);
            txt_count_message = itemView.findViewById(R.id.single_item_txt_messages_counter);
            txt_count_message_unknown = itemView.findViewById(R.id.single_item_txt_messages_counter_unknown);
            linear_options = itemView.findViewById(R.id.single_item_linear_options);
            linear_unknown = itemView.findViewById(R.id.single_item_linear_unknown);
            linear_call_unknown = itemView.findViewById(R.id.single_item_linear_call_post_unknown);
            linear_call_messsage = itemView.findViewById(R.id.single_item_linear_messages_linear_unknown);


        }

        public void notificationCounter(int count) {


            if (count <= 1) {
                txt_count_message.setText(count + " Message");
                txt_count_message_unknown.setText(count + " Message");
            } else {

                txt_count_message.setText(count + " Messages");
                txt_count_message_unknown.setText(count + " Messages");

            }
        }

        public void setImageLetter(String firstletter, int color) {

            drawableh = TextDrawable.builder()
                    .buildRound(firstletter, color);
            ImageView rounded_image = itemView.findViewById(R.id.single_item_image_item_count);
            rounded_image.setImageDrawable(drawableh);


        }
    }


}
