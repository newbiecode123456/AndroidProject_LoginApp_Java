package com.example.myloginapp100;

import static com.example.myloginapp100.MainActivity.MY_REQUEST_CODE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MyProfileActivity extends AppCompatActivity {
    private ImageView imgAvatar;
    private EditText edtFullName,edtEmail;
    private Button btnUpdateProfile;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button selectImageButton;
    private Button exitMyProfile;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private Button btnUpdateMail;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile_layout);

        initUI();
        // show các info of user
        setUserInfo();
        initListener();
    }

    private void initListener() {
        // thực hiện upload ảnh từ galery
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpdateProfile();
            }
        });
        exitMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnUpdateMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpdateMail();
            }
        });
    }

    private void onClickUpdateMail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null) return;
        progressDialog.show();
        String strFullName = edtFullName.getText().toString();
        String strEmail = edtEmail.getText().toString();
        user.updateEmail(strEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    // Get the method name from the intent
                    String methodName = getIntent().getStringExtra("methodName");

                    // Call the method
                    if (methodName.equals("showUserInformation")) {
                        showUserInformation();
                    }
                    Toast.makeText(MyProfileActivity.this, "Update Email success", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("stringData1", strFullName);
                    resultIntent.putExtra("stringData2", strEmail);
                    resultIntent.putExtra("imageData", imageUri.toString());

                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else{
                    progressDialog.dismiss();
                    Toast.makeText(MyProfileActivity.this, "Update Email fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void onClickUpdateProfile(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null) return;
        progressDialog.show();
        String strFullName = edtFullName.getText().toString();
        String strEmail = edtEmail.getText().toString();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(strFullName).setPhotoUri(imageUri!=null?imageUri:user.getPhotoUrl()).build();
        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    // Get the method name from the intent
                    String methodName = getIntent().getStringExtra("methodName");

                    // Call the method
                    if (methodName.equals("showUserInformation")) {
                        showUserInformation();
                    }
                    Toast.makeText(MyProfileActivity.this, "Update Profile success", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("stringData1", strFullName);
                    resultIntent.putExtra("stringData2", strEmail);
                    resultIntent.putExtra("imageData", imageUri!=null?imageUri.toString():user.getPhotoUrl().toString());

                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(MyProfileActivity.this, "Update Profile fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showUserInformation(){}

    private void onClickRequestPermission(){
        /*
        String methodName = getIntent().getStringExtra("methodName");
        if (methodName.equals("openGallery")) {
            openGallery();
        }
         */
        // check if android version dưới 6
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            openGallery();
            return;
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgAvatar.setImageURI(imageUri);
            Glide.with(this)
                    .load(imageUri)
                    .into(imgAvatar);
            //uploadImage();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            /*
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("uploads/" + System.currentTimeMillis() + ".jpg");
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully
                            Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error occurred while uploading the image
                            Toast.makeText(MainActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
             */
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            return;
        }
        edtFullName.setText(user.getDisplayName());
        edtEmail.setText(user.getEmail());
        Glide.with(this).load(user.getPhotoUrl()).error(R.drawable.ic_avatar_default).into(imgAvatar);
    }

    private void initUI() {
        imgAvatar = findViewById(R.id.img_avatar);
        edtEmail = findViewById(R.id.edt_email);
        edtFullName = findViewById(R.id.edt_full_name);
        btnUpdateProfile = findViewById(R.id.btn_update_profile);
        btnUpdateMail = findViewById(R.id.btn_update_mail);
        selectImageButton = findViewById(R.id.selectImageButton);
        exitMyProfile = findViewById(R.id.btn_exit_my_profile);
        progressDialog = new ProgressDialog(this);

    }

}
