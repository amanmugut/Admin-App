package com.example.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    EditText emailedt,passwordedt;
    Button loginbtn;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        init();
        instance();
        initiatelogin();
    }
    void init()
    {
        emailedt = findViewById(R.id.email_log);
        passwordedt = findViewById(R.id.pass_log);
        loginbtn = findViewById(R.id.LogInBtn);
    }
    private void instance()
    {
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }
    private void initiatelogin()
    {
        loginbtn.setOnClickListener(view -> login());
    }
    private void login()
    {
        String email = emailedt.getText().toString();
        String password = passwordedt.getText().toString();
        if(email.isEmpty()){
            Toast.makeText(this,"Email cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else if(password.isEmpty()){
            Toast.makeText(this,"Password cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else if(!isValidEmail(email)){
            Toast.makeText(this,"Email is not valid", Toast.LENGTH_SHORT).show();
        }
        else if(!isValidPass(password)){
            Toast.makeText(this,"Password is not valid", Toast.LENGTH_LONG).show();
        }
        else
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Signing in, Please wait...");
            progressDialog.show();
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    progressDialog.dismiss();
                    checkUserAccesLevel(authResult.getUser().getUid());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public static boolean isValidEmail(CharSequence target) {
        return (Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    public static boolean isValidPass(CharSequence target) {
        return (!TextUtils.isEmpty(target) && target.length()>=8);
    }

    private void checkUserAccesLevel(String uid) {
        DocumentReference df = fStore.collection("Students").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getString("isStudent") != null)
                {
                    Toast.makeText(LoginActivity.this, "You are a student", Toast.LENGTH_SHORT).show();
                }
            }
        });


        DocumentReference df2 = fStore.collection("Teachers").document(uid);
        df2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getString("isTeacher") != null)
                {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
            }
        });
    }
}