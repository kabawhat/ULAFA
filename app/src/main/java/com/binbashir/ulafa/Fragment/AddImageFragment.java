package com.binbashir.ulafa.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aminography.choosephotohelper.ChoosePhotoHelper;
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback;
import com.binbashir.ulafa.R;
import com.binbashir.ulafa.Util.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddImageFragment extends Fragment {

    private ChoosePhotoHelper choosePhotoHelper;
    StorageReference storageReference;

    private Uri filepath;

    public AddImageFragment() {
        // Required empty public constructor
    }

    ImageView imageView_selected;
    Button button_upload;
    Button button_add_image;
    Uri image_uri;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_image, container, false);
        imageView_selected = view.findViewById(R.id.add_image_fragment_imageview_image);
        button_upload = view.findViewById(R.id.add_image_fragment_btn_upload);
        button_add_image = view.findViewById(R.id.add_image_fragment_btn_add);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


     String image_id =  AddImageFragmentArgs.fromBundle(getArguments()).getDocumentId();


     RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.no_image_available)
                .format(DecodeFormat.PREFER_RGB_565)
                .fitCenter();

        Glide.with(getContext())
                .setDefaultRequestOptions(options)
                .load(AddImageFragmentArgs.fromBundle(getArguments()).getImageUrl())
                .thumbnail(0.1f)
                .into(imageView_selected);



        choosePhotoHelper = choosePhotoHelper.with(this).asUri()
                .withState(savedInstanceState).build(new ChoosePhotoCallback<Uri>() {
                    @Override
                    public void onChoose(Uri uri) {
                        if (uri != null) {
                            image_uri = uri;
                            //imageView_selected.setImageURI(image_uri);

                            Glide.with(getContext())
                                    .setDefaultRequestOptions(options)
                                    .load(uri)
                                    .thumbnail(0.1f)
                                    .into(imageView_selected);

                        } else {
                            Toast.makeText(getContext(), "Image removed ", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                );

        button_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoHelper.showChooser();

            }
        });


        button_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (image_uri != null) {
                    imageView_selected.setImageURI(image_uri);

                    Toast.makeText(getActivity(), "Uploading Images Please wait", Toast.LENGTH_LONG).show();

                    uploadFile(image_uri,image_id);

                } else {
                    Toast.makeText(getContext(), "Please select image first", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        choosePhotoHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        choosePhotoHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        choosePhotoHelper.onSaveInstanceState(outState);
    }

    private void uploadFile(Uri uri, String filename) {
        if (uri != null) {

            storageReference = FirebaseStorage.getInstance().getReference(Constants.UPLOAD_PATH_LOST_IMAGES);


            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading Image..." );
            progressDialog.show();
            progressDialog.setCancelable(false);

            StorageReference sref = storageReference.
                    child(filename);

            sref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();


                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri firebaseURL = urlTask.getResult();

                            firestore_add_image(getContext(),firebaseURL.toString());

                            Toast.makeText(getContext(), "Image uploaded", Toast.LENGTH_SHORT).show();


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot snapshot) {

                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot
                                    .getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }

    }
    public void firestore_add_image(Context context, String image_link) {

        HashMap<String, Object> updatex = new HashMap<>();
        updatex.put("item_image_url", image_link);


        if (getArguments() != null) {
            AddImageFragmentArgs addImageFragmentArgs = AddImageFragmentArgs.fromBundle(getArguments());

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            firebaseFirestore.collection(Constants.FIRESTORE_LOST).
                    document(addImageFragmentArgs.getDocumentId())
                    .update(updatex).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                } else {

                    //Firebase Exception

                }

            }).addOnFailureListener(e ->
                    Toast.makeText(context, "Error -> " + e.getMessage(), Toast.LENGTH_SHORT).show());

        }
    }
}