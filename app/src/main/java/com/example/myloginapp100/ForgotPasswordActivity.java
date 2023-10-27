package com.example.myloginapp100;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity{
    private EditText edtEmailReset;
    private Button btnExitResetPassword,btnResetPassword;
    ProgressDialog progressDialog;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initUI();
        initListener();
    }

    private void initListener() {
        btnExitResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickResetPassword();
            }
        });
    }

    private void onClickResetPassword() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String strEmailRessetPassword = edtEmailReset.getText().toString();
        progressDialog.show();
        auth.sendPasswordResetEmail(strEmailRessetPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(ForgotPasswordActivity.this,"Email sent",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ForgotPasswordActivity.this,"Email sent fail!!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initUI() {
        edtEmailReset = findViewById(R.id.edt_email_forgot);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        btnExitResetPassword =findViewById(R.id.btn_exit_reset_password);
        progressDialog = new ProgressDialog(this);
    }
}
