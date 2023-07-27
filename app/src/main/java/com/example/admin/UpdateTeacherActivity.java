package com.example.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateTeacherActivity extends AppCompatActivity {

    CircleImageView updateTeacherImage;
    TextInputEditText updateTeacherName,updateTeacherEmail,updateTeacherPost;
    AppCompatButton updateTeacherbtn,deleteTeacherbtn;
    int REQ = 1;
    StorageReference storageReference;
    ProgressDialog pd ;
    DatabaseReference reference,dbref;
    Bitmap bitmap = null;
    String name,email,image,post,downloadurl = "",category,uniquekey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_teacher2);

        init();
        initiate();
    }
    private void init()
    {
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        post = getIntent().getStringExtra("post");
        image = getIntent().getStringExtra("image");


        uniquekey = getIntent().getStringExtra("uniquekey");
        category = getIntent().getStringExtra("category");
        reference = FirebaseDatabase.getInstance().getReference().child("Teacher");

        updateTeacherImage = findViewById(R.id.updateTeacherImage);
        updateTeacherName = findViewById(R.id.updateTeacherName);
        updateTeacherEmail = findViewById(R.id.updateTeacherEmail);
        updateTeacherPost = findViewById(R.id.updateTeacherPost);
        updateTeacherbtn = findViewById(R.id.updateTeacherbtn);
        deleteTeacherbtn = findViewById(R.id.deleteTeacherbtn);
    }
    private void initiate() {

        try {
            Picasso.get().load(image).into(updateTeacherImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateTeacherEmail.setText(email);
        updateTeacherName.setText(name);
        updateTeacherPost.setText(post);

        updateTeacherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        updateTeacherbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = updateTeacherName.getText().toString();
                email = updateTeacherEmail.getText().toString();
                post = updateTeacherPost.getText().toString();
                checkValidation();
            }
        });

        deleteTeacherbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData();
            }
        });
    }

    private void checkValidation()
    {
        if(name.isEmpty())
        {
            updateTeacherName.setError("Empty Name");
            updateTeacherName.requestFocus();
        }
        else if(email.isEmpty())
        {
            updateTeacherEmail.setError("Empty Email");
            updateTeacherEmail.requestFocus();
        }
        else if(post.isEmpty())
        {
            updateTeacherPost.setError("Empty Post");
            updateTeacherPost.requestFocus();
        }
        else if(bitmap==null)
        {
            updateData(image);
        }
        else
        {
            insertImage();
        }
    }

    private void updateData(String s)
    {
        HashMap<String, Object> hp = new HashMap<>();
        hp.put("name",name);
        hp.put("email",email);
        hp.put("post",post);
        hp.put("image",s);

        reference.child(category).child(uniquekey).updateChildren(hp).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(UpdateTeacherActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateTeacherActivity.this,UpdateTeacher.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateTeacherActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void insertImage()
    {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalimg = baos.toByteArray();
        final StorageReference filePath = storageReference.child("Teacher").child(finalimg+"jpg");
        final UploadTask uploadTask = filePath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(UpdateTeacherActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    updateData(downloadurl);

                                }
                            });
                        }
                    });
                }
                else
                {
                    Toast.makeText(UpdateTeacherActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void deleteData()
    {
        reference.child(category).child(uniquekey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(UpdateTeacherActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateTeacherActivity.this,UpdateTeacher.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateTeacherActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
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
            updateTeacherImage.setImageBitmap(bitmap);
        }
    }
}