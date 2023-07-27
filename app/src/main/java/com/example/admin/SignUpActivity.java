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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText nameedt, regedt, phoneedt, passwordedt;
    Button signupbtn;
    FirebaseAuth auth;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        init();
        instance();
        SignUp();
    }

    void init() {
        nameedt = findViewById(R.id.userNameEDT);
        regedt = findViewById(R.id.userregEDT);
        phoneedt = findViewById(R.id.userPhoneEDT);
        passwordedt = findViewById(R.id.userPassEDT);
        signupbtn = findViewById(R.id.SignUpBtn);
    }

    private void instance() {
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
    }

    private void SignUp() {
        signupbtn.setOnClickListener(view -> initateSignUp());
    }

    private void initateSignUp() {
        String name = nameedt.getText().toString();
        String mail = regedt.getText().toString();
        String phone = phoneedt.getText().toString();
        String password = passwordedt.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show();
        } else if (mail.isEmpty()) {
            Toast.makeText(this, "Email Address cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (phone.isEmpty()) {
            Toast.makeText(this, "Mobile number is empty", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
        } else if (!isValidEmail(mail)) {
            Toast.makeText(this, "Email id is not valid", Toast.LENGTH_SHORT).show();
        } else if (!isValidPhone(phone)) {
            Toast.makeText(this, "Mobile number is not valid", Toast.LENGTH_SHORT).show();
        } else if (!isValidName(name)) {
            Toast.makeText(this, "Name is not valid", Toast.LENGTH_SHORT).show();
        } else if (!isValidPass(password)) {
            Toast.makeText(this, "Password is not valid, it should contain atleast 8 charecters", Toast.LENGTH_LONG).show();
        } else if (!mail.endsWith("ms.pict.edu")) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Creating an account, Please wait...");
            progressDialog.show();

            auth.createUserWithEmailAndPassword(mail, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(SignUpActivity.this, "succesfully registered", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = auth.getCurrentUser();
                    DocumentReference df = fstore.collection("Students").document(auth.getCurrentUser().getUid());
                    Map<String, Object> userInfo = new HashMap<>();


                    userInfo.put("Name", name);
                    userInfo.put("Email", mail);
                    userInfo.put("Password", password);
                    userInfo.put("Phone number", phone);
                    userInfo.put("isStudent", "1");
                    userInfo.put("isTeacher", null);
                    userInfo.put("isHOD", null);

                    df.set(userInfo);
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }


    public static boolean isValidEmail(CharSequence target) {
        return (Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidPass(CharSequence target) {
        return (!TextUtils.isEmpty(target) && target.length() >= 8);
    }

    public static boolean isValidPhone(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches() && target.length() == 10);
    }

    public static boolean isValidName(CharSequence target) {
        return (!TextUtils.isEmpty(target) && target.length() >= 2);
    }
}