package com.example.gym;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Images extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    DatabaseReference databaseReference,databaseReference2;
    ProgressBar progressBar;
    static String str = null;
    TextView text;
    //SharedPreferences sharedPreferences;
    //SharedPreferences.Editor editor;
    List<Modal> list;
    Intent e;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        recyclerView= findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        text= findViewById(R.id.msg);
        text.setVisibility(View.INVISIBLE);

        progressBar= findViewById(R.id.progress_circular);
        list= new ArrayList<>();

        //sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        //editor= sharedPreferences.edit();

        e= getIntent();
        str= e.getStringExtra("key");
        Toast.makeText(Images.this,str,Toast.LENGTH_LONG).show();
        databaseReference= FirebaseDatabase.getInstance().getReference(str);
       //databaseReference2= FirebaseDatabase.getInstance().getReference("ALL");//.child("ALL");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int c=0;
                for (DataSnapshot p: dataSnapshot.getChildren())
                {
                    Modal md= p.getValue(Modal.class);//*\
                    list.add(md); c++;//**\

                    /*
                    for (DataSnapshot dp: p.getChildren())
                    {
                        Modal md= dp.getValue(Modal.class);
                        list.add(md);
                        c++;
                    }
                    */
                    //<--------UNCOMMENT TO MAKE ADMIN'S APP; ALSO TAKE //*\ AND //**\ LINES UNDER COMMENT SECTION--------->
                }
                imageAdapter= new ImageAdapter(Images.this,list);

                if (c==0) {
                    text.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(null);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else{
                    text.setText("CLICK TO DELETE ALL");
                    text.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(imageAdapter);
                progressBar.setVisibility(View.INVISIBLE);}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Images.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
