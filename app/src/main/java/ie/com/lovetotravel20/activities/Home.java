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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import ie.com.lovetotravel20.R;
import ie.com.lovetotravel20.authentication.GoogleAuthentication;
import ie.com.lovetotravel20.authentication.Login;
import ie.com.lovetotravel20.models.Journal;
import ie.com.lovetotravel20.viewholder.JournalViewHolder;

public class Home extends Base
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView journalView;
    String currentUserId;
    FirebaseAuth.AuthStateListener mAuthListener;

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
        currentUserId = mAuth.getCurrentUser().getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("journals").child(currentUserId);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() == null) {

                    Intent intent = new Intent(Home.this, GoogleAuthentication.class);
                    startActivity(intent);
                }
            }
        };

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
    }

    @Override
    protected void onStart() {
        super.onStart();

        //mAuth.addAuthStateListener(mAuthListener);

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

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
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
            mAuth.signOut();
            finish();
            Intent intentLogout = new Intent(this, Login.class);
            this.startActivity(intentLogout);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
