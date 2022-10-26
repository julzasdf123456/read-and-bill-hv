package com.lopez.julz.readandbillhv;

import static com.lopez.julz.readandbillhv.helpers.ObjectHelpers.hasPermissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.lopez.julz.readandbillhv.adapters.AccountsListAdapter;
import com.lopez.julz.readandbillhv.dao.AppDatabase;
import com.lopez.julz.readandbillhv.dao.DownloadedPreviousReadings;
import com.lopez.julz.readandbillhv.helpers.ObjectHelpers;

import java.util.ArrayList;
import java.util.List;

public class ReadingListViewActivity extends AppCompatActivity {

    Toolbar toolbarReadingListView;

    public String userId, areaCode, groupCode, servicePeriod;

    public EditText search;
    public TextView readingListTitle;

    public RecyclerView readingListViewRecyclerView;
    public AccountsListAdapter accountsListAdapter;
    public List<DownloadedPreviousReadings> downloadedPreviousReadingsList;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    public AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_list_view);

        db = Room.databaseBuilder(this, AppDatabase.class, ObjectHelpers.dbName()).fallbackToDestructiveMigration().build();

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        toolbarReadingListView = findViewById(R.id.toolbarReadingListView);
        setSupportActionBar(toolbarReadingListView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userId = getIntent().getExtras().getString("USERID");

        search = findViewById(R.id.searchList);
        downloadedPreviousReadingsList = new ArrayList<>();
        readingListViewRecyclerView = findViewById(R.id.readingListViewRecyclerView);
        accountsListAdapter = new AccountsListAdapter(downloadedPreviousReadingsList, ReadingListViewActivity.this,  userId);
        readingListViewRecyclerView.setAdapter(accountsListAdapter);
        readingListViewRecyclerView.setLayoutManager(new LinearLayoutManager(ReadingListViewActivity.this));
        readingListTitle = findViewById(R.id.readingListTitle);
        readingListTitle.setText("Unread HV and Net Metered Accounts");

        new GetReadingList().execute();

//        search.setRawInputType(Configuration.KEYBOARD_QWERTY);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                new Search().execute(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class Search extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            downloadedPreviousReadingsList.clear();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (strings[0] != null) {
                    String searchRegex = "%" + strings[0] + "%";
                    downloadedPreviousReadingsList.addAll(db.downloadedPreviousReadingsDao().getSearch(searchRegex));
                } else {
                    downloadedPreviousReadingsList.addAll(db.downloadedPreviousReadingsDao().getAllUnread());
                }

            } catch (Exception e) {
                Log.e("ERR_GET_SEARCH", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            accountsListAdapter.notifyDataSetChanged();
        }
    }

    public class GetReadingList extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            downloadedPreviousReadingsList.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                downloadedPreviousReadingsList.addAll(db.downloadedPreviousReadingsDao().getAllUnread());
            } catch (Exception e) {
                Log.e("ERR_GET_LIST", e.getMessage());
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            accountsListAdapter.notifyDataSetChanged();
        }
    }
}