package com.example.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddTeacher extends AppCompatActivity {
    CircleImageView addTeacherimage;
    EditText addTeacherName,addTeacherEmail,addTeacherPost;
    Spinner addTeacherCategory;
    Button addTeacherbtn;
    String category;
    DatabaseReference reference,dbref;
    int REQ = 1;
    StorageReference storageReference;
    ProgressDialog pd ;
    Bitmap bitmap = null;
    String name,email,post,downloadurl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        init();
        initiate();
    }
    private void init()
    {
        addTeacherimage = findViewById(R.id.addTeacherimage);
        addTeacherName = findViewById(R.id.addTeacherName);
        addTeacherEmail = findViewById(R.id.addTeacherEmail);
        addTeacherPost = findViewById(R.id.addTeacherPost);
        addTeacherCategory = findViewById(R.id.addTeacherCategory);
        addTeacherbtn = findViewById(R.id.addTeacherbtn);
        reference = FirebaseDatabase.getInstance().getReference().child("Teacher");
        storageReference = FirebaseStorage.getInstance().getReference().child("Teacher");
        pd = new ProgressDialog(this);
    }
    private void initiate()
    {
        addTeacherimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        addTeacherCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = addTeacherCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        addTeacherbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });
    }
    private void checkValidation()
    {
        name = addTeacherName.getText().toString();
        email = addTeacherEmail.getText().toString();
        post = addTeacherPost.getText().toString();

        if(name.isEmpty())
        {
            addTeacherName.setError("Enter Name");
            addTeacherName.requestFocus();
        }
        else if(email.isEmpty())
        {
            addTeacherEmail.setError("Enter Email");
            addTeacherEmail.requestFocus();
        }
        else if(post.isEmpty())
        {
            addTeacherPost.setError("Enter Post");
            addTeacherPost.requestFocus();
        }
        else if(category.equals("Select"))
        {
            Toast.makeText(this, "Please Select Category", Toast.LENGTH_SHORT).show();
        }
        else if(bitmap == null)
        {
            insertData();
        }
        else
        {
            insertImage();
        }
    }

    private void insertData()
    {
        dbref = reference.child(category);
        final String uniqueKey = dbref.push().getKey();

        TeacherData teacherData = new TeacherData(name,email,post,downloadurl,uniqueKey);
        dbref.child(uniqueKey).setValue(teacherData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(AddTeacher.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddTeacher.this,UpdateTeacher.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddTeacher.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void insertImage()
    {
        pd.setMessage("Uploading Please wait...");
        pd.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalimg = baos.toByteArray();
        final StorageReference filePath = storageReference.child("Teacher").child(finalimg+"jpg");
        final UploadTask uploadTask = filePath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(AddTeacher.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful())
                {
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadurl = String.valueOf(uri);
                                    insertData();

                                }
                            });
                        }
                    });
                }
                else
                {
                    pd.dismiss();
                    Toast.makeText(AddTeacher.this, "Error Occured", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void openGallery()
    {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage,REQ);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ && resultCode==RESULT_OK)
        {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            addTeacherimage.setImageBitmap(bitmap);
        }
    }
}