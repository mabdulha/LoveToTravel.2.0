package ie.com.lovetotravel20.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import ie.com.lovetotravel20.R;
import ie.com.lovetotravel20.models.Image;

public class Camera extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView imagePreview;
    Button btnCapture, btnStore;
    Bitmap image;

    FirebaseUser mUser;
    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    FirebaseAuth mAuth;
    DatabaseReference mDatabaseRef;
    String currentUserId;

    // This will be used to check if an image has been take so we can upload to firebase storage
    boolean imageVerify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setTitle("Camera");

        imagePreview = (ImageView) findViewById(R.id.camera_image_preview);
        btnCapture = (Button) findViewById(R.id.camera_btn_capture);
        btnStore = (Button) findViewById(R.id.camera_btn_store);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        currentUserId = mUser.getUid();

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference("images").child(currentUserId);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("images").child(currentUserId);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });

        btnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If imageVerify is true, we can submit the image onto firebase storage
                if(imageVerify) {
                    submit();
                }
                else {
                    Toast.makeText(Camera.this, "Please Snap a picture before uploading", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //https://stackoverflow.com/questions/48124441/android-send-camera-intent-image-to-firebase
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (data != null) {
            image = (Bitmap) data.getExtras().get("data");
            // Setting the value true so we can test that there is an image available
            imageVerify = true;
        }
        else {
            Intent intent = new Intent(this, Camera.class);
            startActivity(intent);
        }
        imagePreview.setImageBitmap(image);
    }


    public void submit() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] bytes = baos.toByteArray();
        final StorageReference imageReference = mStorageRef.child("images/" + System.currentTimeMillis());

        //https://stackoverflow.com/questions/52123204/when-ever-i-use-firebase-method-to-retrieve-download-url-from-firebase-storage
        imageReference.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        final String downloadUrl = uri.toString();
                        Toast.makeText(Camera.this, "Image upload successful", Toast.LENGTH_SHORT).show();

                        Image image = new Image(downloadUrl);
                        String uploadId = mDatabaseRef.push().getKey();
                        if (uploadId != null) {
                            mDatabaseRef.child(uploadId).setValue(image);
                        }
                        Intent intentHome = new Intent(Camera.this, Home.class);
                        startActivity(intentHome);
                    }
                });
            }
        });
    }
}
