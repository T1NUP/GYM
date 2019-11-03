package com.example.gym;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button upload, choose, retreave, sgn;
    ProgressBar progressBar1;
    EditText name;
    ImageView imageView;
    ProgressBar progressBar, prgc;
    Uri uri;
    String string;
    StorageReference storageReference;
    DatabaseReference databaseReference,databaseReference2;
    StorageTask st;
    GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 123;
    final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        sgn = findViewById(R.id.login);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        sgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.login:
                        signIn();
                        break;
                    // ...
                }
            }
        });


    }

    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SIGN_IN:
                super.onActivityResult(requestCode, resultCode, data);

                if (requestCode == RC_SIGN_IN) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                }
                break;
            case PICK_IMAGE_REQUEST:
                super.onActivityResult(requestCode, resultCode, data);

                if ((requestCode == PICK_IMAGE_REQUEST) && (resultCode == RESULT_OK) && (data != null) && (data.getData() != null)) {
                    uri = data.getData();
                    Picasso.with(this).load(uri).into(imageView);
                }
                break;
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);

        } catch (ApiException e) {
            Toast.makeText(this, "LOGIN FAILED", Toast.LENGTH_LONG).show();
            //updateUI(null);
        }
    }

    public void updateUI(final GoogleSignInAccount a) {
        setContentView(R.layout.activity_main);

        string = a.getDisplayName();

        upload = findViewById(R.id.up);
        choose = findViewById(R.id.ch);
        retreave = findViewById(R.id.ret);
        name = findViewById(R.id.nam);
        imageView = findViewById(R.id.ivf);
        progressBar = findViewById(R.id.pb);
        storageReference = FirebaseStorage.getInstance().getReference("Pics");
        databaseReference = FirebaseDatabase.getInstance().getReference(a.getDisplayName());
        databaseReference2= FirebaseDatabase.getInstance().getReference("ALL").child(a.getDisplayName());

        //name.setVisibility(View.INVISIBLE);
        //progressBar.setVisibility(View.INVISIBLE);
        //choose.setVisibility(View.INVISIBLE);
        //upload.setVisibility(View.INVISIBLE);
        /*<-----UNCOMMENT THEM TO MAKE ADMIN'S APP----->*/

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openFile();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((st != null) && (st.isInProgress()))
                    Toast.makeText(MainActivity.this, "WAIT", Toast.LENGTH_LONG).show();
                else {
                    uploadImg();
                }
            }
        });

        retreave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openImageActivity();
            }
        });
    }

    public void openImageActivity() {
        Intent intent= new Intent(MainActivity.this,Images.class);
        intent.putExtra("key",string);
        startActivity(intent);
    }

    public String getFileExt(Uri u) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mi = MimeTypeMap.getSingleton();
        return mi.getExtensionFromMimeType(cr.getType(u));
    }

    public void uploadImg() {

        if (uri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(uri));
            st = fileReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    }, 5000);
                    Toast.makeText(MainActivity.this, "UPLOADED!", Toast.LENGTH_SHORT).show();

                    int r= (int)(Math.random()*100);
                    String s;//in place of taskSnapshot.getDownloadUrl used  below codes:
                    s = DateFormat.getDateTimeInstance().format(new Date())+ r;//TOKEN ID <DATE>+<RANDOM NO.>;USED IN MODAL CLASS AS STRING NAME

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri du = urlTask.getResult();

                    Modal m = new Modal(s.trim(), du.toString());
                    String upl = databaseReference.push().getKey();
                    databaseReference.child(upl).setValue(m);
                    databaseReference2.child(upl).setValue(m);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    int prg = (int) (((100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount())));
                    progressBar.setProgress(prg);
                }
            });
        } else
            Toast.makeText(MainActivity.this, "choose Image", Toast.LENGTH_LONG).show();
    }

    public void openFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
}
