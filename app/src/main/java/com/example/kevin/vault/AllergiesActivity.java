package com.example.kevin.vault;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AllergiesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public ArrayList<MedicationItem> mAllergyList;

    private RecyclerView mRecyclerView2;
    private MedicationAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergies);

        loadData();
        //createExampleList();
        //mMedicationList = new ArrayList<>();
        buildRecyclerView();
        setButtons();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //displayText = findViewById(R.id.textView2);
        //displayBarcode = findViewById(R.id.textView5);

        //readFile();

    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mAllergyList);
        editor.putString("allergy list", json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("allergy list", null);
        Type type = new TypeToken<ArrayList<MedicationItem>>() {}.getType();
        mAllergyList = gson.fromJson(json, type);

        if (mAllergyList == null) {
            mAllergyList = new ArrayList<>();
        }
    }

    public void insertItem(String med_name, String med_dose, String med_reason, String med_instruction, String medtype, String dates, String period) {
        mAllergyList.add(new MedicationItem (R.drawable.ic_warning_black_24dp, med_name, med_dose, med_reason, med_instruction, "Allergy", dates, period));
        mAdapter.notifyItemInserted(mAllergyList.size());
    }

    public void removeItem(int position) {
        mAllergyList.remove(position);
        mAdapter.notifyItemRemoved(position);
        saveData();
    }


    public void buildRecyclerView() {
        mRecyclerView2 = findViewById(R.id.recyclerView2);
        mRecyclerView2.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MedicationAdapter(mAllergyList);

        mRecyclerView2.setLayoutManager(mLayoutManager);
        mRecyclerView2.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MedicationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                AlertDialog.Builder newBuilder = new AlertDialog.Builder(AllergiesActivity.this);
                View newView = getLayoutInflater().inflate(R.layout.layout_delete, null);
                TextView mDelete = (TextView) newView.findViewById(R.id.delete);
                TextView mEdit = (TextView) newView.findViewById(R.id.edit);
                TextView mCause = (TextView) newView.findViewById(R.id.medView1);
                TextView mReaction = (TextView) newView.findViewById(R.id.medView8);

                mCause.setText(mAllergyList.get(position).getText1());
                mReaction.setText(mAllergyList.get(position).getText2());
                newBuilder.setView(newView);
                final AlertDialog dialog = newBuilder.create();
                dialog.show();

                mEdit.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        edititem(position);
                        dialog.dismiss();
                    }
                });


                mDelete.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        removeItem(position);
                        dialog.dismiss();
                        Toast.makeText(AllergiesActivity.this, "Allergy Item Deleted", Toast.LENGTH_SHORT).show();
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
            public void onLongItemClick(int position) {
                //moveItem(position);
            }
        });
    }

    public void edititem(final int position)
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AllergiesActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.layout_control, null);
        final EditText mCauseText = (EditText)mView.findViewById(R.id.causetext);
        final EditText mReactionText = (EditText)mView.findViewById(R.id.reactiontext);
        Button mAddAllergy = (Button) mView.findViewById(R.id.submitallergy);

        mCauseText.setText(mAllergyList.get(position).getText1());
        mReactionText.setText(mAllergyList.get(position).getText2());

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mAddAllergy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                updateitem(position, mCauseText.getText().toString(), mReactionText.getText().toString(), "", "", "", "", "");
                saveData();
                dialog.dismiss();
                mAdapter.notifyDataSetChanged();
                Toast.makeText(AllergiesActivity.this, "Allergy Edited", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void updateitem(int position, String cause, String reaction, String var1, String var2, String var3, String var4, String var5)
    {
        mAllergyList.get(position).setText1(cause);
        mAllergyList.get(position).setText2(reaction);
    }

    public void setButtons() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(AllergiesActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.layout_control, null);
                final EditText mCauseText = (EditText)mView.findViewById(R.id.causetext);
                final EditText mReactionText = (EditText)mView.findViewById(R.id.reactiontext);
                Button mAddAllergy = (Button) mView.findViewById(R.id.submitallergy);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                
                mAddAllergy.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        insertItem(mCauseText.getText().toString(), mReactionText.getText().toString(), "", "", "", "", "");
                        saveData();
                        dialog.dismiss();
                        //Toast.makeText(AllergiesActivity.this, "A", Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.allergies, menu);
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
        } else if (id == R.id.nav_logout) {
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
