package com.lopez.julz.readandbillhv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lopez.julz.readandbillhv.adapters.HomeMenuAdapter;
import com.lopez.julz.readandbillhv.dao.AppDatabase;
import com.lopez.julz.readandbillhv.dao.Settings;
import com.lopez.julz.readandbillhv.helpers.AlertHelpers;
import com.lopez.julz.readandbillhv.helpers.ObjectHelpers;
import com.lopez.julz.readandbillhv.objects.HomeMenu;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView menu_recyclerview;
    public List<HomeMenu> homeMenuList;
    public HomeMenuAdapter homeMenuAdapter;

    public String userId;

    public FloatingActionButton settingsBtn;
    public TextView bottomBarNotif;

    public AppDatabase db;
    public Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_home);

        db = Room.databaseBuilder(this, AppDatabase.class, ObjectHelpers.dbName()).fallbackToDestructiveMigration().build();

        userId = getIntent().getExtras().getString("USERID");

        settingsBtn = findViewById(R.id.settingsBtn);
        bottomBarNotif = findViewById(R.id.bottomBarNotif);

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new FetchSettings().execute();
    }

    public void addMenu() {
        try {
            homeMenuList.add(new HomeMenu(getDrawable(R.drawable.ic_baseline_download_for_offline_18), "Download", "#4caf50"));
            homeMenuList.add(new HomeMenu(getDrawable(R.drawable.ic_baseline_cloud_upload_24), "Upload", "#ff7043"));
            homeMenuList.add(new HomeMenu(getDrawable(R.drawable.ic_baseline_data_thresholding_24), "Reading List", "#5c6bc0"));
//            homeMenuList.add(new HomeMenu(getDrawable(R.drawable.ic_baseline_edit_location_alt_24), "Reading Tracks", "#78909c"));
//            homeMenuList.add(new HomeMenu(getDrawable(R.drawable.ic_baseline_domain_disabled_24), "Disconnection", "#f44336"));

            homeMenuAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("ERR_ADD_MENU", e.getMessage());
        }
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
                if (settings.getDefaultServer() != null && settings.getDefaultServer().equals("203.177.135.179:8443")) {
                    bottomBarNotif.setText("Server: " + settings.getDefaultServer() + " (via internet)");
                    bottomBarNotif.setBackgroundResource(R.color.orange_500);
                } else {
                    bottomBarNotif.setText("Server: " + settings.getDefaultServer() + " (via local network)");
                    bottomBarNotif.setBackgroundResource(R.color.grey_100);
                }

                menu_recyclerview = findViewById(R.id.menu_recyclerview);
                homeMenuList = new ArrayList<>();
                homeMenuAdapter = new HomeMenuAdapter(homeMenuList, HomeActivity.this, userId, settings.getBAPAName());
                menu_recyclerview.setAdapter(homeMenuAdapter);
                menu_recyclerview.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2));
                addMenu();
            } else {
                AlertHelpers.showMessageDialog(HomeActivity.this, "Settings Not Initialized", "Failed to load settings. Go to settings and set all necessary parameters to continue.");
            }
        }
    }
}