package ie.com.lovetotravel20.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import ie.com.lovetotravel20.R;
import ie.com.lovetotravel20.authentication.GoogleAuthentication;
import ie.com.lovetotravel20.authentication.Login;
import ie.com.lovetotravel20.models.GoogleUser;
import ie.com.lovetotravel20.models.Journal;
import ie.com.lovetotravel20.viewholder.JournalViewHolder;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class Home extends Base
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView journalView;
    String currentUserId;
    FirebaseUser mUser;
    private GoogleSignInClient mGoogleSignInClient;
    TextView headerName, headerEmail;
    ImageView headerImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        journalView = (RecyclerView) findViewById(R.id.rv_journal_list);
        journalView.setHasFixedSize(true);
        journalView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            currentUserId = mUser.getUid();
        }
        else {
            Intent intent = new Intent(Home.this, GoogleAuthentication.class);
            startActivity(intent);
        }
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("journals").child(currentUserId);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("864845900464-sl1vav3j5c7vohuakgc2sd1g04kl0a7b.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Home.this, Add.class));
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        headerImage = (ImageView) header.findViewById(R.id.nav_header_imageView);
        headerName = (TextView) header.findViewById(R.id.nav_header_tv_name);
        headerEmail = (TextView) header.findViewById(R.id.nav_header_tv_email);

        headerName.setText(mUser.getDisplayName());
        headerEmail.setText(mUser.getEmail());

        Picasso.get()
                .load(mUser.getPhotoUrl())
                .transform(new CropCircleTransformation())
                .resize(75,75)
                .centerCrop()
                .into(headerImage);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Journal, JournalViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Journal, JournalViewHolder>(
                Journal.class,
                R.layout.journal_card_layout,
                JournalViewHolder.class,
                mDatabaseRef
        ) {
            @Override
            protected void populateViewHolder(final JournalViewHolder viewHolder, final Journal model, int position) {

                final String ref_key = getRef(position).getKey();


                viewHolder.setTitle(model.getTitle());
                viewHolder.setEntry(model.getEntry());
                viewHolder.setDate(model.getDate());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Home.this, JournalView.class);
                        intent.putExtra("journal_id", ref_key);
                        startActivity(intent);
                    }
                });

                viewHolder.btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Home.this, Update.class);
                        intent.putExtra("journal_id" ,ref_key);
                        startActivity(intent);
                    }
                });

                viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog dialog = new AlertDialog.Builder(Home.this)
                                .setTitle("Delete Confirmation")
                                .setMessage("Are you sure you want to delete this journal?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mDatabaseRef.child(ref_key).removeValue();
                                        Toast.makeText(Home.this, "Journal Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                });
            }
        };

        journalView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intentHome = new Intent(getApplicationContext(), Home.class);
            startActivity(intentHome);
        } else if (id == R.id.nav_add) {
            Intent intentAdd = new Intent(Home.this, Add.class);
            startActivity(intentAdd);
        } else if (id == R.id.nav_search) {
            Intent intentSearch = new Intent(Home.this, Search.class);
            startActivity(intentSearch);
        } else if (id == R.id.nav_image) {

        } else if (id == R.id.nav_map) {
            Intent intentMaps = new Intent(this, GoogleMaps.class);
            startActivity(intentMaps);
        } else if (id == R.id.nav_logout) {
            mGoogleSignInClient.signOut();
            Intent intentLogout = new Intent(getApplicationContext(), GoogleAuthentication.class);
            intentLogout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Home.this.finish();
            this.startActivity(intentLogout);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
