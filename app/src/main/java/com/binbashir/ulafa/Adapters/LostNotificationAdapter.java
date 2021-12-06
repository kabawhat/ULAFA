package com.binbashir.ulafa.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.binbashir.ulafa.Model.Notification_Item;
import com.binbashir.ulafa.R;
import com.binbashir.ulafa.Util.Util_class;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class LostNotificationAdapter extends RecyclerView.Adapter<LostNotificationAdapter.ViewHolder> {


    // creating variables for our ArrayList and context
    private ArrayList<Notification_Item> notification_items;
    private Context context;

    FirebaseFirestore firebaseFirestore;

    // creating constructor for our adapter class
    public LostNotificationAdapter(ArrayList<Notification_Item> itemArrayList, Context context) {
        this.notification_items = itemArrayList;
        this.context = context;

    }


    @NonNull
    @Override
    public LostNotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                 int viewType) {
        // passing our layout file for displaying our card item
        return new LostNotificationAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LostNotificationAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        Notification_Item notification_item = notification_items.get(position);
        firebaseFirestore = FirebaseFirestore.getInstance();

        holder.text_username.setText(notification_item.getUsername());
        holder.text_admno.setText(notification_item.getAdmno());
        holder.text_message.setText(notification_item.getMessage());


        try {
            String dateString = DateFormat.format("E-dd-MM-yyyy hh:mm:ss", new Date(notification_item.getTime_reported())).toString();
            holder.text_date_time.setText(dateString);
        } catch (Exception e) {
            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // this is for setting the imageLetter which is now the number of the
        String firstletter = String.valueOf(position + 1);
        holder.setImageLetter(firstletter, Color.parseColor("#4CAF50"));


        holder.btn_call_founder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Util_class().phone_call(context, notification_item.getPhone());
            }
        });

        holder.btn_delete_founder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new Util_class().checking_current_user()) {

                    if (new Util_class().getCurrentUserIdString().equalsIgnoreCase(notification_item.getUserid())) {
                        new Util_class().deleteDocument(context, notification_item.getParent_id(), notification_item.getDocument_id());
                    } else {
                        Toast.makeText(context, "Only Original Poster Can Delete this ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Please Login", Toast.LENGTH_LONG).show();
                }

            }

        });

    }


    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return notification_items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.

        private TextView text_username;
        private TextView text_date_time;
        private TextView text_admno;
        private TextView text_message;
        private ImageView imageView_image;
        private Button btn_call_founder;
        private Button btn_delete_founder;
        private TextDrawable drawableh;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            text_username = itemView.findViewById(R.id.notification_item_txt_item_username);
            text_date_time = itemView.findViewById(R.id.notification_item_txt_item_date_time);
            text_admno = itemView.findViewById(R.id.notification_item_txt_item_adm_no);
            text_message = itemView.findViewById(R.id.notification_item_txt_item_message);
            imageView_image = itemView.findViewById(R.id.notification_item_image_item_count);
            btn_call_founder = itemView.findViewById(R.id.notification_item_btn_call);
            btn_delete_founder = itemView.findViewById(R.id.notification_item_btn_delete);
            //imageView_image   = itemView.findViewById(R.id.notification_item_image_item_count);

        }

        public void setImageLetter(String firstletter, int color) {
            drawableh = TextDrawable.builder()
                    .buildRound(firstletter, color);
            ImageView rounded_image = itemView.findViewById(R.id.notification_item_image_item_count);
            rounded_image.setImageDrawable(drawableh);
        }

    }
}
