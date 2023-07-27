package com.example.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

public class DeleteNoticeActivity extends AppCompatActivity {

    RecyclerView deleteNoticeRecycler;
    ProgressBar progressBar;
    ArrayList<NoticeData> list;
    NoticeAdapter adapter;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_notice);
        init();
        initiate();
    }
    private void init()
    {
        deleteNoticeRecycler = findViewById(R.id.deleteNoticeRecycler);
        progressBar = findViewById(R.id.progressBar);
        reference = FirebaseDatabase.getInstance().getReference().child("Notice");
    }
    private void initiate()
    {
        deleteNoticeRecycler.setLayoutManager(new LinearLayoutManager(this));
        deleteNoticeRecycler.setHasFixedSize(true);

        getNotice();
    }

    private void getNotice()
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list  = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    NoticeData data = snapshot.getValue(NoticeData.class);
                    list.add(data);
                }

                adapter = new NoticeAdapter(DeleteNoticeActivity.this,list);
                adapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
                deleteNoticeRecycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DeleteNoticeActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }
}