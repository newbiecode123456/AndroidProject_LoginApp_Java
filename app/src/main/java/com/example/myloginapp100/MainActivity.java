package com.example.myloginapp100;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final int MY_REQUEST_CODE = 10;

    public static final int UPDATE_ACTIVITY_REQUEST_CODE = 1;
    private ImageView imgAvatar;
    private TextView tvName,tvEmail;
    private Button btnSignOut,btnUpdateMyProfile,btnChangePassword,btnOpenChatAi;
    private ProgressDialog progressDialog;
    private TextView tvFirebaseMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Firebase.class
        AnhXa();
        showUserInformation();
        readDatabase();
        InitListener();

    }

    private void InitListener() {

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });
        btnUpdateMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MyProfileActivity.class);
                intent.putExtra("methodName", "showUserInformation");
                startActivityForResult(intent, UPDATE_ACTIVITY_REQUEST_CODE);
                //startActivity(intent);
            }
        });
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        btnOpenChatAi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,DemoChat.class);
                startActivity(i);
            }
        });
    }

    private void AnhXa(){
        progressDialog = new ProgressDialog(this);
        imgAvatar = findViewById(R.id.img_avatar);
        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        btnSignOut = findViewById(R.id.nav_sign_out);
        btnUpdateMyProfile = findViewById(R.id.nav_change_profile);
        btnChangePassword = findViewById(R.id.nav_change_password);
        btnOpenChatAi = findViewById(R.id.nav_open_chatai);
        tvFirebaseMessage = findViewById(R.id.tv_firebase_message);

    }

    private void showUserInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }

        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("DisplayName");
        myRef.setValue(name);

        if(name == null){
            tvName.setText("No name");
        } else {
            tvName.setText(name);
        }
        tvEmail.setText(email);
        Glide.with(this).load(photoUrl).error(R.drawable.ic_avatar_default).into(imgAvatar);
    }

    private void readDatabase(){


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("DisplayName");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                Toast.makeText(MainActivity.this,"Get: "+value,Toast.LENGTH_LONG).show();
                tvFirebaseMessage.setText("Hello, " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvFirebaseMessage.setText("Error...");
                Toast.makeText(MainActivity.this,"Got Error",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Handle the result data here
                String stringData1 = data.getStringExtra("stringData1");
                String stringData2 = data.getStringExtra("stringData2");
                Uri imageData = Uri.parse(data.getStringExtra("imageData"));

                // Do something with the data
                if(stringData1 == null){
                    tvName.setText("No name");
                } else {
                    tvName.setText(stringData1);
                }
                tvEmail.setText(stringData2);
                Glide.with(this).load(imageData).error(R.drawable.ic_avatar_default).into(imgAvatar);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Handle the case where the called activity was canceled
            }
        }
    }

}