package com.example.myloginapp100;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtEmail,edtPassword;
    private Button btnSignUp;
    private ProgressDialog progressDialog;
    LinearLayout layoutSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        InitUI();
        InitListener();
    }
    private void InitUI(){
        edtEmail = findViewById(R.id.edt_email_regis);
        edtPassword = findViewById(R.id.edt_password_regis);
        btnSignUp = findViewById(R.id.btn_sign_up);
        layoutSignIn = findViewById(R.id.layout_sign_in);

        progressDialog = new ProgressDialog(this);
    }
    private void InitListener(){
        layoutSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignUp();
            }
        });
    }

    private void onClickSignUp() {
        String strEmail = edtEmail.getText().toString();
        String strPasswd = edtPassword.getText().toString();
        // thực hiện các validate
        if(TextUtils.isEmpty(strEmail)){
            Toast.makeText(this,"Type in your email!",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty((strPasswd))){
            Toast.makeText(this,"Please type in your password!",Toast.LENGTH_SHORT).show();
        }
        // hiện progress dialog
        progressDialog.show();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(strEmail,strPasswd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finishAffinity();
                } else {
                    Toast.makeText(SignUpActivity.this,"Authentication fail.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}