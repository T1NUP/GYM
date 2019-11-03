package com.example.gym;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.gym.Images.str;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {


    Context mContext;
    public List<Modal> mupload;//Object list of Modal class
    public ImageAdapter(Context context, List<Modal> uplod) {
        mContext = context;
        mupload = uplod;

    }
    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageview;
        private TextView textView;

        public ImageViewHolder(View imageView) {
            super(imageView);
            imageview = imageView.findViewById(R.id.img);
            textView = imageView.findViewById(R.id.na);
        }
    }
        @NonNull
        @Override
        public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, viewGroup, false);

            //     view1.setOnClickListener(new MyOnClickListener());
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    DatabaseReference daNode,dbNode = FirebaseDatabase.getInstance().getReference().getRoot().child(str);
                    dbNode.setValue(null);//TO DELETE PARTICULAR USER'S RECORD

                    /*-------->  daNode= FirebaseDatabase.getInstance().getReference("ALL");
                    daNode.setValue(null);  <------*/
                    //UNCOMMENT FOR MAKING ADMIN'S APP, DELETES EVERY USER RECORD


                    //<<<-----------------------------------------------------NOT FOR READING_-------->>
                    //int itemPosition = recyclerView.indexOfChild(v);
                    //    Toast.makeText(MainActivity.this,"Selected item position is---"+ itemPosition,Toast.LENGTH_SHORT).show();
     /*               TextView t = v.findViewById(R.id.na);
                    ImageView iv= v.findViewById(R.id.img);
                    Uri u= iv.ge
                    Intent it= new Intent(mContext,MainActivity.class);
                    it.putExtra("key",t.getText().toString());
                    mContext.startActivity(it);
                    //mContext.startActivity(new Intent(mContext, MainActivity.class));
                    //System.out.println(t.toString());
                    Toast.makeText(mContext,"Selected val of clicked position is---"+ t.getText().toString(),Toast.LENGTH_SHORT).show();
       */       ///<<-------------------------------------------------------------------------------------------------------------------------->>>>
                }
            });


            return new ImageViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder imageViewHolder, int i) {
            Modal md = mupload.get(i);
            imageViewHolder.textView.setText(md.getName());
            //Picasso.with(mContext).load(md.getUrl()).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(imageViewHolder.imageview);
            Picasso.with(mContext).load(md.getUrl()).placeholder(R.mipmap.ic_launcher).into(imageViewHolder.imageview);
            //imageViewHolder.imageview.setImageURI(md.getUrl());
        }

        @Override
        public int getItemCount() {
            return mupload.size();
        }
    }
