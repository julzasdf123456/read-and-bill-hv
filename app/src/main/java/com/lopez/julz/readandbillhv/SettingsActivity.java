package com.lopez.julz.readandbillhv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lopez.julz.readandbillhv.api.RequestPlaceHolder;
import com.lopez.julz.readandbillhv.api.RetrofitBuilder;
import com.lopez.julz.readandbillhv.dao.AppDatabase;
import com.lopez.julz.readandbillhv.dao.Settings;
import com.lopez.julz.readandbillhv.helpers.ObjectHelpers;
import com.lopez.julz.readandbillhv.objects.BAPA;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    public Toolbar toolbar;

    public Spinner serverSelect;
    public List<String> townCodes;

    public FloatingActionButton saveBtn;

    public AppDatabase db;
    public RetrofitBuilder retrofitBuilder;
    private RequestPlaceHolder requestPlaceHolder;
    public Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = Room.databaseBuilder(this, AppDatabase.class, ObjectHelpers.dbName()).fallbackToDestructiveMigration().build();

        toolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        serverSelect = findViewById(R.id.serverSelect);
        saveBtn = findViewById(R.id.saveBtn);
        townCodes = new ArrayList<>();

        populateSpinners();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SaveSettings().execute(serverSelect.getSelectedItem().toString(), "");
                finish();
                Toast.makeText(SettingsActivity.this, "Settings saved!", Toast.LENGTH_SHORT).show();
            }
        });

        serverSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                new SaveSettings().execute(serverSelect.getSelectedItem().toString(),"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void populateSpinners() {
        try {
            // add town codes
            townCodes.add("01");
            townCodes.add("02");
            townCodes.add("03");
            townCodes.add("04");
            townCodes.add("05");
            townCodes.add("06");
            townCodes.add("07");
            townCodes.add("08");
            townCodes.add("09");

            // servers
            List<String> servers = new ArrayList<>();
            servers.add("192.168.100.10");
            servers.add("192.168.100.20");
            servers.add("192.168.100.30");
            servers.add("192.168.100.40");
            servers.add("192.168.100.50");
            servers.add("192.168.100.60");
            servers.add("192.168.100.70");
            servers.add("192.168.100.80");
            servers.add("192.168.100.90");
            servers.add("192.168.100.4");
            servers.add("192.168.100.1");
            servers.add("192.168.110.94");
            servers.add("203.177.135.179:8443");
            servers.add("192.168.10.161");
            servers.add("192.168.1.8");
            ArrayAdapter serversAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, servers.toArray());
            serversAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            serverSelect.setAdapter(serversAdapter);


        } catch (Exception e) {
            Log.e("ERR_POP_SPINNRS", e.getMessage());
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class SaveSettings extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                Settings settingsLoc = new Settings(strings[0], "", strings[1]); // 0 = server, 1 = bapa name
                db.settingsDao().insertAll(settingsLoc);
                settings = settingsLoc;
            } catch (Exception e) {
                Log.e("ERR_SV_SETTINGS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (settings != null) {
                retrofitBuilder = new RetrofitBuilder(settings.getDefaultServer());
                requestPlaceHolder = retrofitBuilder.getRetrofit().create(RequestPlaceHolder.class);

            }
        }
    }
}