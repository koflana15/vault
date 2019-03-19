package com.example.kevin.vault;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.AdapterView;

import java.util.ArrayList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.Calendar;

import static android.graphics.Color.rgb;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public ArrayList<MedicationItem> mMedicationList;
    public ArrayList<MedicationItem> mArchivedMedicationList;

    private RecyclerView mRecyclerView;
    private MedicationAdapter mAdapter;
    private MedicationAdapter mAdapter1;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button dateText1;
    private Button editDateText;

    private int startyearval;
    private int startmonthval;
    private int startdayval;
    private int edityearval = 1;
    private int editmonthval = 1;
    private int editdayval = 1;

    private String begindate = "blank";
    private String editdatefinal = "blank";

    private Spinner spinner;
    private static final String[] items = {"Medication Type", "Acute", "As Directed", "Chronic"};
    private int medtype = 0;

    static final int START_DIALOG_ID = 0;
    static final int EDIT_DIALOG_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setDate();
        loadData();
        buildRecyclerView();
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.layout_add, null);
                final EditText mMedicationText = (EditText)mView.findViewById(R.id.mednametext);
                final EditText mDosageText = (EditText)mView.findViewById(R.id.dosagetext);
                final EditText mReasonText = (EditText)mView.findViewById(R.id.reasontext);
                final EditText mInstructionText = (EditText)mView.findViewById(R.id.instructiontext);
                final EditText mPeriodText = (EditText)mView.findViewById(R.id.period);
                Button mAddMedication = (Button) mView.findViewById(R.id.submitmedication);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                spinner = (Spinner)mView.findViewById(R.id.spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, items)
                {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        // Cast the spinner collapsed item (non-popup item) as a text view
                        TextView tv = (TextView) super.getView(position, convertView, parent);

                        // Set the text color of spinner item
                        if(position==0) {
                             tv.setTextColor(Color.parseColor("#9b9b9b"));
                        }
                        else
                        {
                            tv.setTextColor(Color.BLACK);

                        }

                        // Return the view
                        return tv;
                    }

                    @Override
                    public boolean isEnabled(int position) {
                        if (position == 0) {
                            // Disable the first item from Spinner
                            // First item will be use for hint
                            return false;
                        } else {
                            return true;
                        }
                    }

                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            // Set the hint text color gray
                            tv.setTextColor(Color.parseColor("#9b9b9b"));
                        } else {
                                tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }



                };
                spinner.setFocusable(true);
                spinner.setFocusableInTouchMode(true);
                spinner.requestFocus();
                adapter.setDropDownViewResource(R.layout.spinner_item);
                spinner.setAdapter(adapter);

                dateText1 = mView.findViewById(R.id.startdate);
                showDialogOnClick();

                mAddMedication.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){

                        String mreason = mReasonText.getText().toString();
                        String mperiod = mPeriodText.getText().toString();
                        String minstructions = mInstructionText.getText().toString();
                        String mdosage = mDosageText.getText().toString();
                        if(mdosage.equals(""))
                        {
                            mdosage = "No Dosage Specified";
                        }
                        if(mreason.equals(""))
                        {
                            mreason = "No Reason Specified";
                        }
                        if(minstructions.equals(""))
                        {
                            minstructions = "No Instructions Specified";
                        }
                        if(begindate.equals("blank"))
                        {
                            begindate = "No Start Date Specified";
                        }
                        if(mperiod.equals(""))
                        {
                            mperiod = "No Period Specified";
                        }
                        String spinnertext = spinner.getSelectedItem().toString();
                        if(spinnertext.equals("Acute"))
                        {
                            medtype = 1;
                        }
                        else if(spinnertext.equals("As Directed"))
                        {
                            medtype = 2;
                        }
                        else if(spinnertext.equals("Chronic"))
                        {
                            medtype = 3;
                        }
                        if(medtype != 0)
                        {
                            if(!mMedicationText.getText().toString().equals(""))
                            {
                                insertItem(mMedicationText.getText().toString(), mdosage, mreason, minstructions, begindate, medtype, mperiod);
                                saveData();
                                medtype = 0;
                                dialog.dismiss();
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Please enter a Medication Name", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Please select a Medication Type", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

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

    }

    private void setDate()
    {
        final Calendar calendar = Calendar.getInstance();
        startyearval = calendar.get(Calendar.YEAR);
        startmonthval = calendar.get(Calendar.MONTH);
        startdayval = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void setnewEditDate()
    {
        final Calendar calendar = Calendar.getInstance();
        edityearval = calendar.get(Calendar.YEAR);
        editmonthval = calendar.get(Calendar.MONTH);
        editdayval = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void showDialogOnClick() {

        dateText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(START_DIALOG_ID);
            }
        });


    }

    private void showEditDialogOnClick() {

        editDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                showDialog(EDIT_DIALOG_ID);

            }
        });
    }

    protected Dialog onCreateDialog(int id) {
        if(id == START_DIALOG_ID)
        {
            return new DatePickerDialog(this, startdpickerListener, startyearval, startmonthval, startdayval);
        }
        else if(id == EDIT_DIALOG_ID)
        {
            return new DatePickerDialog(this,editdpickerListener, edityearval, editmonthval, editdayval);
        }
        return null;
    }

    private void setEditDate(String dateedittext)
    {
        String[] datesDelimit = dateedittext.split("/");
        edityearval = Integer.valueOf(datesDelimit[2]);
        editmonthval = Integer.valueOf(datesDelimit[1]) - 1;
        editdayval = Integer.valueOf(datesDelimit[0]);
    }

    private DatePickerDialog.OnDateSetListener startdpickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            startyearval = year;
            startmonthval = month + 1;
            startdayval = dayOfMonth;
            begindate = startdayval + "/" + startmonthval + "/" + startyearval;
        }
    };

    private DatePickerDialog.OnDateSetListener editdpickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            edityearval = year;
            editmonthval = month + 1;
            editdayval = dayOfMonth;
            editdatefinal = editdayval + "/" + editmonthval + "/" + edityearval;
        }
    };

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mMedicationList);
        editor.putString("med list", json);
        editor.apply();
    }

    private void saveArchivedData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mArchivedMedicationList);
        editor.putString("archive list", json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("med list", null);
        Type type = new TypeToken<ArrayList<MedicationItem>>() {}.getType();
        mMedicationList = gson.fromJson(json, type);

        if (mMedicationList == null) {
            mMedicationList = new ArrayList<>();
        }
    }

    public void insertItem(String med_name, String med_dose, String med_reason, String med_instruction, String dates, int typeofmed, String period) {

        if(typeofmed == 1) {
            mMedicationList.add(new MedicationItem(R.drawable.ic_acute, med_name, med_dose, med_reason, med_instruction, "Acute", dates, period));
            mAdapter.notifyItemInserted(mMedicationList.size());
        }
        else if(typeofmed == 2) {
            mMedicationList.add(new MedicationItem(R.drawable.ic_asdirected, med_name, med_dose, med_reason, med_instruction, "As Directed", dates, period));
            mAdapter.notifyItemInserted(mMedicationList.size());
        }
        else {
            mMedicationList.add(new MedicationItem(R.drawable.ic_chronic, med_name, med_dose, med_reason, med_instruction, "Chronic", dates, period));
            mAdapter.notifyItemInserted(mMedicationList.size());
        }
    }

    public void removeItem(int position) {
        mMedicationList.remove(position);
        mAdapter.notifyItemRemoved(position);
        saveData();
    }

    private void loadArchivedData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("archive list", null);
        Type type = new TypeToken<ArrayList<MedicationItem>>() {}.getType();
        mArchivedMedicationList = gson.fromJson(json, type);

        if (mArchivedMedicationList == null) {
            mArchivedMedicationList = new ArrayList<>();
        }
    }

    public void moveItem(int position) {
        loadArchivedData();
        mMedicationList.get(position).resetText();
        mArchivedMedicationList.add(mMedicationList.remove(position));
        mAdapter.notifyItemRemoved(position);
        mAdapter1.notifyItemInserted(mArchivedMedicationList.size());
        saveData();
        saveArchivedData();
        Toast.makeText(MainActivity.this, "Moved to Archive", Toast.LENGTH_LONG).show();
    }


    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MedicationAdapter(mMedicationList);
        mAdapter1 = new MedicationAdapter(mArchivedMedicationList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MedicationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position)
            {
                AlertDialog.Builder medBuilder = new AlertDialog.Builder(MainActivity.this);
                View medView = getLayoutInflater().inflate(R.layout.layout_med, null);
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

                textViewMedName.setText(mMedicationList.get(position).getText1());
                textViewMedDosage.setText(mMedicationList.get(position).getText2());
                textViewMedInstruction.setText(mMedicationList.get(position).getText4());
                textViewMedReason.setText(mMedicationList.get(position).getText3());
                textViewMedStartDate.setText(mMedicationList.get(position).getText6());
                textViewMedType.setText(mMedicationList.get(position).getText5());
                textViewMedPeriod.setText(mMedicationList.get(position).getText7());

                medBuilder.setView(medView);
                final AlertDialog dialog2 = medBuilder.create();
                dialog2.show();

                textViewArchive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //code here ...
                        moveItem(position);
                        Toast.makeText(MainActivity.this, "Moved to Archive", Toast.LENGTH_LONG).show();
                        dialog2.dismiss();
                    }
                });

                textViewEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edititem(position);
                        dialog2.dismiss();
                    }
                });

                textViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //code here ...
                        removeItem(position);
                        Toast.makeText(MainActivity.this, "Medication Deleted", Toast.LENGTH_LONG).show();
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
                AlertDialog.Builder medBuilder = new AlertDialog.Builder(MainActivity.this);
                View medView = getLayoutInflater().inflate(R.layout.layout_med, null);
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

                textViewMedName.setText(mMedicationList.get(position).getText1());
                textViewMedDosage.setText(mMedicationList.get(position).getText2());
                textViewMedInstruction.setText(mMedicationList.get(position).getText4());
                textViewMedReason.setText(mMedicationList.get(position).getText3());
                textViewMedStartDate.setText(mMedicationList.get(position).getText6());
                textViewMedType.setText(mMedicationList.get(position).getText5());
                textViewMedPeriod.setText(mMedicationList.get(position).getText7());

                medBuilder.setView(medView);
                final AlertDialog dialog2 = medBuilder.create();
                dialog2.show();

                textViewArchive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //code here ...
                        moveItem(position);
                        Toast.makeText(MainActivity.this, "Moved to Archive", Toast.LENGTH_LONG).show();
                        dialog2.dismiss();
                    }
                });

                textViewEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edititem(position);
                        dialog2.dismiss();
                    }
                });

                textViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //code here ...
                        removeItem(position);
                        Toast.makeText(MainActivity.this, "Medication Deleted", Toast.LENGTH_LONG).show();
                        dialog2.dismiss();
                    }
                });
            }
        });
    }

    public void edititem(final int position)
    {
        AlertDialog.Builder editBuilder = new AlertDialog.Builder(MainActivity.this);
        View editView = getLayoutInflater().inflate(R.layout.layout_edit, null);
        final EditText mMedicationText = (EditText)editView.findViewById(R.id.mednametext);
        final EditText mDosageText = (EditText)editView.findViewById(R.id.dosagetext);
        final EditText mReasonText = (EditText)editView.findViewById(R.id.reasontext);
        final EditText mInstructionText = (EditText)editView.findViewById(R.id.instructiontext);
        final EditText mPeriodText = (EditText)editView.findViewById(R.id.period);
        Button mAddMedication = (Button) editView.findViewById(R.id.submitmedication);

        mMedicationText.setText(mMedicationList.get(position).getText1());

        if(mMedicationList.get(position).getText2().equals("No Dosage Specified"))
        {
            mDosageText.setText("");
        }
        else
        {
            mDosageText.setText(mMedicationList.get(position).getText2());
        }

        if(mMedicationList.get(position).getText3().equals("No Reason Specified"))
        {
            mReasonText.setText("");
        }
        else
        {
            mReasonText.setText(mMedicationList.get(position).getText3());
        }

        if(mMedicationList.get(position).getText4().equals("No Instructions Specified"))
        {
            mInstructionText.setText("");
        }
        else
        {
            mInstructionText.setText(mMedicationList.get(position).getText4());
        }

        if(mMedicationList.get(position).getText7().equals("No Period Specified"))
        {
            mPeriodText.setText("");
        }
        else
        {
            mPeriodText.setText(mMedicationList.get(position).getText7());
        }

        final String datetext = mMedicationList.get(position).getText6();

        String mMedTypeText = mMedicationList.get(position).getText5();

        editBuilder.setView(editView);
        final AlertDialog dialog = editBuilder.create();
        dialog.show();

        spinner = (Spinner)editView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, items)
        {

            public View getView(int position, View convertView, ViewGroup parent) {
                // Cast the spinner collapsed item (non-popup item) as a text view
                TextView tv = (TextView) super.getView(position, convertView, parent);

                // Set the text color of spinner item
                if(position==0) {
                    tv.setTextColor(Color.parseColor("#9b9b9b"));
                }
                else
                {
                    tv.setTextColor(Color.BLACK);

                }

                // Return the view
                return tv;
            }

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.parseColor("#9b9b9b"));
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }



        };
        spinner.setFocusable(true);
        spinner.setFocusableInTouchMode(true);
        spinner.requestFocus();
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        int spinnerdefault = adapter.getPosition(mMedTypeText);
        spinner.setSelection(spinnerdefault);
        editDateText = editView.findViewById(R.id.startdate);


        if(!datetext.equals("No Start Date Specified"))
        {
            setEditDate(datetext);
            showEditDialogOnClick();
        }
        else
        {
            setnewEditDate();
            showEditDialogOnClick();
        }


        mAddMedication.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                String spinnertext = spinner.getSelectedItem().toString();
                String mreason = mReasonText.getText().toString();
                String mperiod = mPeriodText.getText().toString();
                String minstructions = mInstructionText.getText().toString();
                String mdosage = mDosageText.getText().toString();

                if(mdosage.equals(""))
                {
                    mdosage = "No Dosage Specified";
                }
                if(mreason.equals(""))
                {
                    mreason = "No Reason Specified";
                }
                if(minstructions.equals(""))
                {
                    minstructions = "No Instructions Specified";
                }
                if(editdatefinal.equals("blank"))
                {
                    editdatefinal = "No Start Date Specified";
                }
                if(mperiod.equals(""))
                {
                    mperiod = "No Period Specified";
                }

                updatemedication(position, mMedicationText.getText().toString(), mdosage, mreason, minstructions, editdatefinal, spinnertext, mperiod);
                dialog.dismiss();

            }
        });
    }

    public void updatemedication (int position, String med_name, String med_dose, String med_reason, String med_instruction, String dates, String typeofmed, String period)
    {
        mMedicationList.get(position).setText1(med_name);
        mMedicationList.get(position).setText2(med_dose);
        mMedicationList.get(position).setText3(med_reason);
        mMedicationList.get(position).setText4(med_instruction);
        mMedicationList.get(position).setText5(typeofmed);
        mMedicationList.get(position).setText6(dates);
        mMedicationList.get(position).setText7(period);
        int imageres = 0;
        if(typeofmed.equals("Acute"))
        {
            imageres = R.drawable.ic_acute;
        }
        else if(typeofmed.equals("As Directed"))
        {
            imageres = R.drawable.ic_asdirected;
        }
        else if(typeofmed.equals("Chronic"))
        {
            imageres = R.drawable.ic_chronic;
        }
        mMedicationList.get(position).setImage(imageres);
        Toast.makeText(this, "Edited Medication", Toast.LENGTH_SHORT).show();
        saveData();
        mAdapter.notifyDataSetChanged();
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
            i.putExtra("caller", "MainActivity");
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
            overridePendingTransition(0, 0);
        } else if (id == R.id.nav_archive) {
            Intent i = new Intent(getApplicationContext(), ArchiveActivity.class);
            i.putExtra("caller", "MainActivity");
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
