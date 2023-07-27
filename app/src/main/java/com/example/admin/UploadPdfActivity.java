package com.example.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class UploadPdfActivity extends AppCompatActivity {

    MaterialCardView addpdf;
    AppCompatEditText pdfTitle;
    AppCompatButton uploadPdfBtn;
    TextView pdfTextView;
    String pdfName;
    DatabaseReference databaseReference;
    String downloadUrl = "";
    StorageReference storageReference;
    ProgressDialog pd ;
    String title;
    private final int REQ = 1;
    private Uri pdfData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);
        init();
        initiate();
    }
    private void init()
    {
        addpdf = findViewById(R.id.addpdf);
        pdfTitle = findViewById(R.id.pdfTitle);
        uploadPdfBtn = findViewById(R.id.uploadPdfBtn);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        pdfTextView = findViewById(R.id.pdfTextView);
        pd = new ProgressDialog(this);

    }
    private void initiate()
    {
        addpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                openGallery();
            }
        });
        uploadPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = pdfTitle.getText().toString();
                if (title.isEmpty())
                {
                    pdfTitle.setError("Empty");
                }
                else if(pdfData==null)
                {
                    Toast.makeText(UploadPdfActivity.this, "Please Select PDF", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    uploadPdf();
                }
            }
        });
    }
    private void uploadPdf()
    {
        pd.setTitle("Please wait");
        pd.setMessage("Uploading PDF");
        pd.show();
        StorageReference reference = storageReference.child("pdf/"+pdfName+"-"+System.currentTimeMillis()+".pdf");
        reference.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isComplete());
                Uri uri = uriTask.getResult();
                uploadData(String.valueOf(uri));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadPdfActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadData(String downloadUrl)
    {
        String uniqueKey = databaseReference.child("pdf").push().getKey();
        HashMap data = new HashMap();
        data.put("pdfTitle",title);
        data.put("pdfUrl",downloadUrl);

        databaseReference.child("pdf").child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(UploadPdfActivity.this, "PDF Uploaded Successfully", Toast.LENGTH_SHORT).show();
                pdfTitle.setText("");
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadPdfActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openGallery()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent,REQ);
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ && resultCode==RESULT_OK)
        {
            pdfData = data.getData();
            if(pdfData.toString().startsWith("content://"))
            {
                Cursor cursor = null;
                try {
                    cursor = UploadPdfActivity.this.getContentResolver().query(pdfData,null,null,null);
                    if(cursor!=null && cursor.moveToFirst())
                    {
                        pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(pdfData.toString().startsWith("file://"))
            {
                pdfName = new File(pdfData.toString()).getName();
            }

            pdfTextView.setText(pdfName);
            pdfTextView.setVisibility(View.VISIBLE);
        }
    }
}