package ie.com.lovetotravel20.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import ie.com.lovetotravel20.R;

public class Camera extends Base {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView imagePreview;
    Button btnCapture, btnStore;
    private Uri imageUri;
    Bitmap image;

    FirebaseUser mUser;
    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    String currentUserId;

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

        if (mUser != null) {
            currentUserId = mUser.getUid();
        }

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference("image").child(currentUserId);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });

        /*btnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });*/

    }

    //https://stackoverflow.com/questions/48124441/android-send-camera-intent-image-to-firebase
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        image = (Bitmap) data.getExtras().get("data");
        imagePreview.setImageBitmap(image);
        submit();
    }


    public void submit() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] bytes = baos.toByteArray();
        StorageReference imageReference = mStorageRef.child("image/" + System.currentTimeMillis());
        imageReference.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(Camera.this, "Image upload successful", Toast.LENGTH_SHORT).show();
                Intent intentHome = new Intent(Camera.this, Home.class);
                startActivity(intentHome);
            }
        });
    }
}
