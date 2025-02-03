package com.lopez.julz.readandbillhv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.lopez.julz.readandbillhv.adapters.AccountsListAdapter;
import com.lopez.julz.readandbillhv.dao.AppDatabase;
import com.lopez.julz.readandbillhv.dao.DownloadedPreviousReadings;
import com.lopez.julz.readandbillhv.helpers.ObjectHelpers;

import java.util.ArrayList;
import java.util.List;

public class BilledActivity extends AppCompatActivity {

    Toolbar toolbarUnbilled;
    public TextView unbilledTitle;

    public String userId, areaCode, groupCode, servicePeriod;

    public MaterialButton toggleMenu;
    public RecyclerView readingMonitorViewRecyclerView;
    public AccountsListAdapter accountsListAdapter;
    public List<DownloadedPreviousReadings> downloadedPreviousReadingsList;

    public AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billed);

        db = Room.databaseBuilder(this, AppDatabase.class, ObjectHelpers.dbName()).fallbackToDestructiveMigration().build();

        toolbarUnbilled = findViewById(R.id.toolbarUnbilled);
        setSupportActionBar(toolbarUnbilled);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userId = getIntent().getExtras().getString("USERID");

        unbilledTitle = findViewById(R.id.unBilledTitle);

        downloadedPreviousReadingsList = new ArrayList<>();
        readingMonitorViewRecyclerView = findViewById(R.id.readingMonitorViewRecyclerView);
        accountsListAdapter = new AccountsListAdapter(downloadedPreviousReadingsList, BilledActivity.this, userId, true);
        readingMonitorViewRecyclerView.setAdapter(accountsListAdapter);
        readingMonitorViewRecyclerView.setLayoutManager(new LinearLayoutManager(BilledActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ToggleList().execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class ToggleList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            downloadedPreviousReadingsList.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                downloadedPreviousReadingsList.addAll(db.downloadedPreviousReadingsDao().getAllRead());
            } catch (Exception e) {
                Log.e("ERR_GET_LIST", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            accountsListAdapter.notifyDataSetChanged();
            super.onPostExecute(unused);
        }
    }
}