package ie.com.lovetotravel20.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ie.com.lovetotravel20.R;
import ie.com.lovetotravel20.models.Image;
import ie.com.lovetotravel20.viewholder.ImageGalleryHolder;

public class ImageGallery extends AppCompatActivity {

    RecyclerView imageView;
    ImageView mImageView;
    FirebaseAuth mAuth;
    DatabaseReference mDatabaseRef;
    FirebaseUser mUser;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view);

        imageView = (RecyclerView) findViewById(R.id.image_recycler_view);
        imageView.setHasFixedSize(true);
        imageView.setLayoutManager(new LinearLayoutManager(this));

        setTitle("Image Gallery");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser != null) {
            currentUserId = mUser.getUid();
        }

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("images").child(currentUserId);

        mImageView = (ImageView) findViewById(R.id.image_card_display);

        FirebaseRecyclerAdapter<Image, ImageGalleryHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Image, ImageGalleryHolder>(
                Image.class,
                R.layout.image_card_view,
                ImageGalleryHolder.class,
                mDatabaseRef
        ) {
            @Override
            protected void populateViewHolder(ImageGalleryHolder viewHolder, Image model, int position) {

                viewHolder.setImage(model.getImageUrl());
            }
        };
        imageView.setAdapter(firebaseRecyclerAdapter);

    }
}
