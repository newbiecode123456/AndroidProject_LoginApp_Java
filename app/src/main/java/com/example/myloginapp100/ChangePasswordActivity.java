package com.example.myloginapp100;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText edtOldPassword,edtNewPassword,edtReEnterPassword;
    private Button btnChangePassword,btnExitChangePassword;
    ProgressDialog progressDialog;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_layout);

        initUI();
        initListener();
    }

    private void initListener() {
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChangePassword();
            }
        });
        btnExitChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void onClickChangePassword() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String strOldPassword = edtOldPassword.getText().toString();
        String strNewPassword = edtNewPassword.getText().toString();
        String strReEnterPassword = edtReEnterPassword.getText().toString();
        String strEmail = user.getEmail();
        progressDialog.show();
        if(!strNewPassword.endsWith(strReEnterPassword)){
            progressDialog.dismiss();
            edtOldPassword.setText("");
            Toast.makeText(this, "Password not match!!!", Toast.LENGTH_SHORT).show();
        }else {
            AuthCredential credential = EmailAuthProvider.getCredential(strEmail, strOldPassword);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(strNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ChangePasswordActivity.this, "Change password successfull!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    progressDialog.dismiss();
                                    edtOldPassword.setText("");
                                    Toast.makeText(ChangePasswordActivity.this, "Change password fail!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        edtOldPassword.setText("");
                        Toast.makeText(ChangePasswordActivity.this, "Please enter your current password correctly to proceed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void initUI() {
        edtOldPassword = findViewById(R.id.edt_old_password);
        edtNewPassword = findViewById(R.id.edt_new_password);
        edtReEnterPassword = findViewById(R.id.edt_reenter_password);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnExitChangePassword = findViewById(R.id.btn_exit_change_password);
        progressDialog = new ProgressDialog(this);
    }
}
