package com.example.kevin.vault;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ArchiveActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public ArrayList<MedicationItem> mArchivedMedicationList;
    public ArrayList<MedicationItem> mMedicationList;



    private RecyclerView mRecyclerView1;
    private MedicationAdapter mAdapter;
    private MedicationAdapter mAdapter1;
    private RecyclerView.LayoutManager mLayoutManager1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadData();
        buildRecyclerView();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mArchivedMedicationList);
        editor.putString("archive list", json);
        editor.apply();
    }

    private void saveMainData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mMedicationList);
        editor.putString("med list", json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("archive list", null);
        Type type = new TypeToken<ArrayList<MedicationItem>>() {}.getType();
        mArchivedMedicationList = gson.fromJson(json, type);

        if (mArchivedMedicationList == null) {
            mArchivedMedicationList = new ArrayList<>();
        }
    }

    private void loadmainData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("med list", null);
        Type type = new TypeToken<ArrayList<MedicationItem>>() {}.getType();
        mMedicationList = gson.fromJson(json, type);

        if (mMedicationList == null) {
            mMedicationList = new ArrayList<>();
        }
    }

    public void removeItem(int position) {
        mArchivedMedicationList.remove(position);
        mAdapter.notifyItemRemoved(position);
        saveData();
    }

    public void moveItem(int position) {
        loadmainData();
        mArchivedMedicationList.get(position).resetText();
        mMedicationList.add(mArchivedMedicationList.remove(position));
        mAdapter.notifyItemRemoved(position);
        mAdapter1.notifyItemInserted(mMedicationList.size());
        saveData();
        saveMainData();
        Toast.makeText(ArchiveActivity.this, "Moved to Current Medications", Toast.LENGTH_LONG).show();
    }


    public void buildRecyclerView() {
        mRecyclerView1 = findViewById(R.id.recyclerView1);
        mRecyclerView1.setHasFixedSize(true);
        mLayoutManager1 = new LinearLayoutManager(this);
        mAdapter = new MedicationAdapter(mArchivedMedicationList);
        mAdapter1 = new MedicationAdapter(mMedicationList);

        mRecyclerView1.setLayoutManager(mLayoutManager1);
        mRecyclerView1.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MedicationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                AlertDialog.Builder medBuilder = new AlertDialog.Builder(ArchiveActivity.this);
                View medView = getLayoutInflater().inflate(R.layout.layout_medarchive, null);
                final TextView textViewMedName = (TextView)medView.findViewById(R.id.medView1);
                final TextView textViewMedDosage = (TextView)medView.findViewById(R.id.medView3);
                final TextView textViewMedInstruction = (TextView)medView.findViewById(R.id.medView4);
                final TextView textViewMedReason = (TextView)medView.findViewById(R.id.medView5);
                final TextView textViewMedStartDate = (TextView)medView.findViewById(R.id.medView6);
                final TextView textViewMedPeriod = (TextView)medView.findViewById(R.id.medView7);
                final TextView textViewMedType = (TextView)medView.findViewById(R.id.medView8);

                final TextView textViewArchive = (TextView) medView.findViewById(R.id.archivebutton);
                final TextView textViewEdit = (TextView) medView.findViewById(R.id.editbutton);
                final TextView textViewDelete = (TextView) medView.findViewById(R.id.deletebutton);

                textViewMedName.setText(mArchivedMedicationList.get(position).getText1());
                textViewMedDosage.setText(mArchivedMedicationList.get(position).getText2());
                textViewMedInstruction.setText(mArchivedMedicationList.get(position).getText4());
                textViewMedReason.setText(mArchivedMedicationList.get(position).getText3());
                textViewMedStartDate.setText(mArchivedMedicationList.get(position).getText6());
                textViewMedType.setText(mArchivedMedicationList.get(position).getText5());
                textViewMedPeriod.setText(mArchivedMedicationList.get(position).getText7());

                medBuilder.setView(medView);
                final AlertDialog dialog2 = medBuilder.create();
                dialog2.show();

                textViewArchive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //code here ...
                        moveItem(position);
                        Toast.makeText(ArchiveActivity.this, "Moved to Archive", Toast.LENGTH_LONG).show();
                        dialog2.dismiss();
                    }
                });

                textViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //code here ...
                        removeItem(position);
                        Toast.makeText(ArchiveActivity.this, "Medication Deleted", Toast.LENGTH_LONG).show();
                        dialog2.dismiss();
                    }
                });

            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });

        mAdapter.setOnLongClickListener(new MedicationAdapter.OnLongClickListener() {
            @Override
            public void onLongItemClick(final int position) {
                AlertDialog.Builder medBuilder = new AlertDialog.Builder(ArchiveActivity.this);
                View medView = getLayoutInflater().inflate(R.layout.layout_medarchive, null);
                final TextView textViewMedName = (TextView)medView.findViewById(R.id.medView1);
                final TextView textViewMedDosage = (TextView)medView.findViewById(R.id.medView3);
                final TextView textViewMedInstruction = (TextView)medView.findViewById(R.id.medView4);
                final TextView textViewMedReason = (TextView)medView.findViewById(R.id.medView5);
                final TextView textViewMedStartDate = (TextView)medView.findViewById(R.id.medView6);
                final TextView textViewMedPeriod = (TextView)medView.findViewById(R.id.medView7);
                final TextView textViewMedType = (TextView)medView.findViewById(R.id.medView8);

                final TextView textViewArchive = (TextView) medView.findViewById(R.id.archivebutton);
                final TextView textViewEdit = (TextView) medView.findViewById(R.id.editbutton);
                final TextView textViewDelete = (TextView) medView.findViewById(R.id.deletebutton);

                textViewMedName.setText(mArchivedMedicationList.get(position).getText1());
                textViewMedDosage.setText(mArchivedMedicationList.get(position).getText2());
                textViewMedInstruction.setText(mArchivedMedicationList.get(position).getText4());
                textViewMedReason.setText(mArchivedMedicationList.get(position).getText3());
                textViewMedStartDate.setText(mArchivedMedicationList.get(position).getText6());
                textViewMedType.setText(mArchivedMedicationList.get(position).getText5());
                textViewMedPeriod.setText(mArchivedMedicationList.get(position).getText7());

                medBuilder.setView(medView);
                final AlertDialog dialog2 = medBuilder.create();
                dialog2.show();

                textViewArchive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //code here ...
                        moveItem(position);
                        Toast.makeText(ArchiveActivity.this, "Moved to Archive", Toast.LENGTH_LONG).show();
                        dialog2.dismiss();
                    }
                });

                textViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //code here ...
                        removeItem(position);
                        Toast.makeText(ArchiveActivity.this, "Medication Deleted", Toast.LENGTH_LONG).show();
                        dialog2.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.archive, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_medications) {
            // Handle the camera action
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("caller", "ArchiveActivity");
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
            overridePendingTransition(0, 0);
        } else if (id == R.id.nav_archive) {
            Intent i = new Intent(getApplicationContext(), ArchiveActivity.class);
            i.putExtra("caller", "ArchiveActivity");
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
            overridePendingTransition(0, 0);
        } else if (id == R.id.nav_allergies) {
            Intent i = new Intent(getApplicationContext(), AllergiesActivity.class);
            i.putExtra("caller", "AllergiesActivity");
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
            overridePendingTransition(0, 0);
        }  else if (id == R.id.nav_logout) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.putExtra("caller", "LoginActivity");
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
            overridePendingTransition(0, 0);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
