package com.sansan.vikashtutorial.Other;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sansan.vikashtutorial.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomBottomSheet extends BottomSheetDialogFragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageButton imageButton;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private StorageReference mStore;
    private CircleImageView circleImageView;
    private TextInputEditText nameEditText, emailEditText, phoneEditText, pinEditText, cityEditText, districtEditText, stateEditText, landmarkEditText;
    private Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        mStore = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        pinEditText = view.findViewById(R.id.pincodeEditText);
        landmarkEditText = view.findViewById(R.id.landmarkEditText);
        cityEditText = view.findViewById(R.id.cityEditText);
        stateEditText = view.findViewById(R.id.stateEditText);
        districtEditText = view.findViewById(R.id.districtEditText);
        circleImageView = view.findViewById(R.id.userImg);
        imageButton = view.findViewById(R.id.choosePhoto);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        button = view.findViewById(R.id.updateBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupUpdateButton();
            }
        });
        fetchUserDetails();
        return view;
    }

    private void fetchUserDetails() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            String phone = documentSnapshot.getString("phone");
                            String photo = documentSnapshot.getString("photoUrl");
                            String district = documentSnapshot.getString("district");
                            String landmark = documentSnapshot.getString("landmark");
                            String state = documentSnapshot.getString("state");
                            String pin = documentSnapshot.getString("pin");
                            String city = documentSnapshot.getString("city");

                            nameEditText.setText(name);
                            emailEditText.setText(email);
                            phoneEditText.setText(phone);
                            pinEditText.setText(pin);
                            landmarkEditText.setText(landmark);
                            stateEditText.setText(state);
                            districtEditText.setText(district);
                            cityEditText.setText(city);

                            if (photo != null && !photo.isEmpty()) {
                                Glide.with(this)
                                        .load(photo)
                                        .placeholder(R.mipmap.ic_launcher)
                                        .error(R.mipmap.ic_launcher)
                                        .into(circleImageView);
                            } else {
                                circleImageView.setImageResource(R.mipmap.ic_launcher);
                            }
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getActivity(), "Failed to load user details", Toast.LENGTH_SHORT).show());
        }
    }

    private void setupUpdateButton() {
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                String userId = currentUser.getUid();

                String updatedName = nameEditText.getText().toString().trim();
                String updatedEmail = emailEditText.getText().toString().trim();
                String updatedPhone = phoneEditText.getText().toString().trim();
                String updatedPin = pinEditText.getText().toString().trim();
                String updatedLandmark = landmarkEditText.getText().toString().trim();
                String updatedCity = cityEditText.getText().toString().trim();
                String updatedDistrict = districtEditText.getText().toString().trim();
                String updatedState = stateEditText.getText().toString().trim();


                Map<String, Object> updatedUserData = new HashMap<>();
                updatedUserData.put("name", updatedName);
                updatedUserData.put("email", updatedEmail);
                updatedUserData.put("phone", updatedPhone);
                updatedUserData.put("pin", updatedPin);
                updatedUserData.put("landmark", updatedLandmark);
                updatedUserData.put("city", updatedCity);
                updatedUserData.put("district", updatedDistrict);
                updatedUserData.put("state", updatedState);

                db.collection("users").document(userId)
                        .update(updatedUserData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getActivity(), "Details updated successfully!", Toast.LENGTH_SHORT).show();
                            dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "Failed to update details", Toast.LENGTH_SHORT).show();
                        });
            }
        }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                circleImageView.setImageBitmap(bitmap);
                uploadProfileImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProfileImage() {
        if (imageUri != null) {
            progressDialog.show();

            String userId = mAuth.getCurrentUser().getUid();
            StorageReference imageRef = mStore.child("profileImages").child(userId + ".jpg");

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            updateProfileImageUrl(uri.toString()); // Update Firestore with new image URL
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), "Failed to get image URL", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateProfileImageUrl(String imageUrl) {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.update("photoUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Profile image updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Failed to update profile image", Toast.LENGTH_SHORT).show();
                });
    }
}
