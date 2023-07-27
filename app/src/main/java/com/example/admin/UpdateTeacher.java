package com.example.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateTeacher extends AppCompatActivity {

    FloatingActionButton faculty;
    RecyclerView csDepartment,entcDepartment,itDepartment;
    LinearLayout csNoData,entcNoData,itNoData;
    List<TeacherData> list1,list2,list3;
    TeacherAdapter adapter;
    DatabaseReference reference,dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_teacher);
        init();
        initiate();
    }
    private void init()
    {
        faculty = findViewById(R.id.faculty);

        csNoData = findViewById(R.id.csNoData);
        entcNoData = findViewById(R.id.entcNoData);
        itNoData = findViewById(R.id.itNoData);

        csDepartment = findViewById(R.id.csDepartment);
        entcDepartment = findViewById(R.id.entcDepartment);
        itDepartment = findViewById(R.id.itDepartment);

        reference = FirebaseDatabase.getInstance().getReference().child("Teacher");

        csDepartment();
        entcDepartment();
        itDepartment();
    }
    private void initiate()
    {
        faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddTeacher.class));
            }
        });
    }

    private void csDepartment()
    {
        dbRef = reference.child("Computer Science");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
             list1 = new ArrayList<>();
             if(!datasnapshot.exists())
             {
                 csNoData.setVisibility(View.VISIBLE);
                 csDepartment.setVisibility(View.GONE);
             }
             else
             {
                 csNoData.setVisibility(View.GONE);
                 csDepartment.setVisibility(View.VISIBLE);
                 for(DataSnapshot snapshot:datasnapshot.getChildren())
                 {
                     TeacherData data = snapshot.getValue(TeacherData.class);
                     list1.add(data);
                 }
                 csDepartment.setHasFixedSize(true);
                 csDepartment.setLayoutManager(new LinearLayoutManager(UpdateTeacher.this));
                 adapter = new TeacherAdapter(list1,UpdateTeacher.this,"Computer Science");
                 csDepartment.setAdapter(adapter);
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateTeacher.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void entcDepartment()
    {
        dbRef = reference.child("Electronics and Telecommunication");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                list2 = new ArrayList<>();
                if(!datasnapshot.exists())
                {
                    entcNoData.setVisibility(View.VISIBLE);
                    entcDepartment.setVisibility(View.GONE);
                }
                else
                {
                    entcNoData.setVisibility(View.GONE);
                    entcDepartment.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot:datasnapshot.getChildren())
                    {
                        TeacherData data = snapshot.getValue(TeacherData.class);
                        list2.add(data);
                    }
                    entcDepartment.setHasFixedSize(true);
                    entcDepartment.setLayoutManager(new LinearLayoutManager(UpdateTeacher.this));
                    adapter = new TeacherAdapter(list2,UpdateTeacher.this,"Electronics and Telecommunication");
                    entcDepartment.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateTeacher.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void itDepartment()
    {
        dbRef = reference.child("Information Technology");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                list3 = new ArrayList<>();
                if(!datasnapshot.exists())
                {
                    itNoData.setVisibility(View.VISIBLE);
                    itDepartment.setVisibility(View.GONE);
                }
                else
                {
                    itNoData.setVisibility(View.GONE);
                    itDepartment.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot:datasnapshot.getChildren())
                    {
                        TeacherData data = snapshot.getValue(TeacherData.class);
                        list3.add(data);
                    }
                    itDepartment.setHasFixedSize(true);
                    itDepartment.setLayoutManager(new LinearLayoutManager(UpdateTeacher.this));
                    adapter = new TeacherAdapter(list3,UpdateTeacher.this, "Information Technology");
                    itDepartment.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateTeacher.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }
}