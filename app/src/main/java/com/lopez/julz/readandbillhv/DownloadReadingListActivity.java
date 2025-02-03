package com.lopez.julz.readandbillhv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.lopez.julz.readandbillhv.adapters.AccountsListAdapter;
import com.lopez.julz.readandbillhv.adapters.DownloadReadingListAdapter;
import com.lopez.julz.readandbillhv.api.RequestPlaceHolder;
import com.lopez.julz.readandbillhv.api.RetrofitBuilder;
import com.lopez.julz.readandbillhv.dao.AppDatabase;
import com.lopez.julz.readandbillhv.dao.DownloadedPreviousReadings;
import com.lopez.julz.readandbillhv.dao.Rates;
import com.lopez.julz.readandbillhv.dao.ReadingSchedules;
import com.lopez.julz.readandbillhv.dao.Settings;
import com.lopez.julz.readandbillhv.helpers.AlertHelpers;
import com.lopez.julz.readandbillhv.helpers.ObjectHelpers;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadReadingListActivity extends AppCompatActivity {

    public Toolbar downloadReadingListToolbar;

    public List<DownloadedPreviousReadings> downloadedPreviousReadingsList;
    public AccountsListAdapter accountsListAdapter;
    public RecyclerView downloadReadingListRecyclerview;

    public List<Rates> ratesList;

    public RetrofitBuilder retrofitBuilder;
    private RequestPlaceHolder requestPlaceHolder;

    public AppDatabase db;
    public Settings settings;
    public FloatingActionButton downloadBtn;
    public Spinner selectTown, selectBillMonth;
    public List<String> townCodes;

    public MaterialButton filterButton;
    public CircularProgressIndicator progressBar;

    public String userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_reading_list);

        db = Room.databaseBuilder(this, AppDatabase.class, ObjectHelpers.dbName()).fallbackToDestructiveMigration().build();

        downloadReadingListToolbar = findViewById(R.id.downloadReadingListToolbar);
        setSupportActionBar(downloadReadingListToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        downloadBtn = findViewById(R.id.downloadBtn);
        filterButton = findViewById(R.id.filterButton);
        selectTown = findViewById(R.id.selectTown);
        selectBillMonth = findViewById(R.id.selectBillMonth);
        progressBar = findViewById(R.id.progressBar);
        townCodes = new ArrayList<>();
        ratesList = new ArrayList<>();

        downloadBtn.setEnabled(false);
        progressBar.setVisibility(View.GONE);

        userId = getIntent().getExtras().getString("USERID");

        new FetchSettings().execute();

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchDownloadableHvAccounts(townCodes.get(selectTown.getSelectedItemPosition()), selectBillMonth.getSelectedItem().toString());
            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SaveAccounts().execute();
                new SaveRates().execute(ratesList);
            }
        });
    }

    public void populateSpinners() {
        try {
            townCodes.add("00");
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
            List<String> towns = new ArrayList<>();
            towns.add("All");
            towns.add("Cadiz City");
            towns.add("EB Magalona");
            towns.add("Manapla");
            towns.add("Victorias City");
            towns.add("San Carlos City");
            towns.add("Sagay City");
            towns.add("Escalante City");
            towns.add("Calatrava");
            towns.add("Toboso");
            ArrayAdapter townsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, towns.toArray());
            townsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectTown.setAdapter(townsAdapter);

            selectBillMonth.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ObjectHelpers.getPreviousMonths(12)));
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

    public class FetchSettings extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                settings = db.settingsDao().getSettings();
            } catch (Exception e) {
                Log.e("ERR_FETCH_SETTINGS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (settings != null) {
                retrofitBuilder = new RetrofitBuilder(settings.getDefaultServer());
                requestPlaceHolder = retrofitBuilder.getRetrofit().create(RequestPlaceHolder.class);

                downloadReadingListRecyclerview = findViewById(R.id.downloadReadingListRecyclerview);
                downloadedPreviousReadingsList = new ArrayList<>();
                accountsListAdapter = new AccountsListAdapter(downloadedPreviousReadingsList, DownloadReadingListActivity.this, userId, false);
                downloadReadingListRecyclerview.setAdapter(accountsListAdapter);
                downloadReadingListRecyclerview.setLayoutManager(new LinearLayoutManager(DownloadReadingListActivity.this));

                populateSpinners();
//                fetchDownloadableHvAccounts(townCodes.get(selectTown.getSelectedItemPosition()), selectBillMonth.getSelectedItem().toString());

            } else {
                AlertHelpers.showMessageDialog(DownloadReadingListActivity.this, "Settings Not Initialized", "Failed to load settings. Go to settings and set all necessary parameters to continue.");
            }
        }
    }

    public void fetchDownloadableHvAccounts(String town, String period) {
        downloadBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        fetchRates(period);
        try {
            Call<List<DownloadedPreviousReadings>> downloadCall = requestPlaceHolder.downloadHvAccounts(town, period, userId);

            downloadCall.enqueue(new Callback<List<DownloadedPreviousReadings>>() {
                @Override
                public void onResponse(Call<List<DownloadedPreviousReadings>> call, Response<List<DownloadedPreviousReadings>> response) {
                    if (response.isSuccessful()) {
                        downloadedPreviousReadingsList.clear();
                        accountsListAdapter.notifyDataSetChanged();
                        downloadedPreviousReadingsList.addAll(response.body());
                        accountsListAdapter.notifyDataSetChanged();
                        downloadBtn.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        if (response.code() == 404) {
                            AlertHelpers.showMessageDialog(DownloadReadingListActivity.this, "Rate Unavailable", "Rate is not yet available for this billing month.");
                        } else {
                            AlertHelpers.showMessageDialog(DownloadReadingListActivity.this, "Oops!", "Error downloading HV Accounts \n" + response.raw());
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<List<DownloadedPreviousReadings>> call, Throwable t) {
                    AlertHelpers.showMessageDialog(DownloadReadingListActivity.this, "Oops!", "Error downloading HV Accounts \n" + t.getMessage());
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            AlertHelpers.showMessageDialog(this, "Oops!", "Error downloading HV Accounts \n" + e.getMessage());
            progressBar.setVisibility(View.GONE);
        }
    }

    public class SaveAccounts extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                int size = downloadedPreviousReadingsList.size();
                for(int i=0; i<size; i++) {
                    DownloadedPreviousReadings dl = downloadedPreviousReadingsList.get(i);
                    dl.setId(ObjectHelpers.generateIDandRandString());

                    if (db.downloadedPreviousReadingsDao().getOneSpecific(dl.getAccountId(), dl.getServicePeriod()) != null) {

                    } else {
                        db.downloadedPreviousReadingsDao().insertAll(dl);
                    }
                }
            } catch (Exception e) {
                Log.e("ERR_SV_DL", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Toast.makeText(DownloadReadingListActivity.this, "Readings downloaded", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Download Rates
     */
    public void fetchRates(String servicePeriod) {
        ratesList.clear();
        try {
            Call<List<Rates>> ratesCall = requestPlaceHolder.downloadRates(servicePeriod);
            ratesCall.enqueue(new Callback<List<Rates>>() {
                @Override
                public void onResponse(Call<List<Rates>> call, Response<List<Rates>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
//                            new SaveRates().execute(response.body());
                            ratesList.addAll(response.body());
                        } else {

                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Rates>> call, Throwable t) {
                    Log.e("ERR_GET_RATE", t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("ERR_GET_RATE", e.getMessage());
        }
    }

    /**
     * SAVE RATES
     */
    public class SaveRates extends AsyncTask<List<Rates>, Integer, Void> {

        @Override
        protected Void doInBackground(List<Rates>... lists) {
            try {
                if (lists != null) {
                    List<Rates> ratesList = lists[0];

                    for (Rates rate : ratesList) {
                        if (db.ratesDao().getOneById(rate.getId()) != null) {
                            // update
                            db.ratesDao().updateAll(rate);
                        } else {
                            //save
                            db.ratesDao().insertAll(rate);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("ERR_SAVE_RATE", e.getMessage());
            }
            return null;
        }
    }
}