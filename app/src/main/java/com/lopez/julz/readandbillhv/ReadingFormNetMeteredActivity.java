package com.lopez.julz.readandbillhv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonParser;
import com.lopez.julz.readandbillhv.adapters.ReadingListAdapter;
import com.lopez.julz.readandbillhv.api.RequestPlaceHolder;
import com.lopez.julz.readandbillhv.api.RetrofitBuilder;
import com.lopez.julz.readandbillhv.dao.AppDatabase;
import com.lopez.julz.readandbillhv.dao.Bills;
import com.lopez.julz.readandbillhv.dao.DownloadedPreviousReadings;
import com.lopez.julz.readandbillhv.dao.Rates;
import com.lopez.julz.readandbillhv.dao.ReadingImages;
import com.lopez.julz.readandbillhv.dao.ReadingSchedules;
import com.lopez.julz.readandbillhv.dao.Readings;
import com.lopez.julz.readandbillhv.dao.Users;
import com.lopez.julz.readandbillhv.helpers.AlertHelpers;
import com.lopez.julz.readandbillhv.helpers.ObjectHelpers;
import com.lopez.julz.readandbillhv.helpers.ReadingHelpers;
import com.lopez.julz.readandbillhv.helpers.TextLogger;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReadingFormNetMeteredActivity  extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    public Toolbar toolbarReadingForm;
    public TextView accountName, accountNumber;

    /**
     * BUNDLES
     */
    public String id, servicePeriod, userId;

    public AppDatabase db;

    public DownloadedPreviousReadings currentDpr;
    public Rates currentRate;
    public Readings currentReading;
    public Bills currentBill;
    public Double kwhConsumed, kwhExportConsumed;
    public String readingId;
    public Users user;

    /**
     * FORM
     */
    public EditText prevReading, presReading, demandReading, exportReading, prevReadingExport, notes;
    public TextView kwhUsed, kwhExportUsed, accountType, rate, sequenceCode, accountStatus, coreloss, multiplier, seniorCitizen, currentArrears, totalArrears, additionalKwh, meterNo, katas, prepayment, percent2, percent5;
    public MaterialButton billBtn, nextBtn, prevBtn, takePhotoButton, printBtn, saveOnlyBtn;
    public RadioGroup fieldStatus;

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1002;

    /**
     * MAP
     */
    public MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private LocationComponent locationComponent;
    public Style style;
    public SymbolManager symbolManager;

    /**
     * TAKE PHOTOS
     */
    static final int REQUEST_PICTURE_CAPTURE = 1;
    public FlexboxLayout imageFields;
    public String currentPhotoPath;

    /**
     * BT
     */
    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_reading_form_net_metered);

        db = Room.databaseBuilder(this, AppDatabase.class, ObjectHelpers.dbName()).fallbackToDestructiveMigration().build();

        toolbarReadingForm = findViewById(R.id.toolbarReadingForm);
        setSupportActionBar(toolbarReadingForm);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        id = getIntent().getExtras().getString("ID");
        servicePeriod = getIntent().getExtras().getString("SERVICEPERIOD");
        userId = getIntent().getExtras().getString("USERID");
        Log.e("TEST REC", servicePeriod);

        accountName = findViewById(R.id.accountName);
        accountNumber = findViewById(R.id.accountNumber);
        prevReading = findViewById(R.id.prevReading);
        presReading = findViewById(R.id.presReading);
        kwhUsed = findViewById(R.id.kwhUsed);
        billBtn = findViewById(R.id.billBtn);
        prevBtn = findViewById(R.id.prevButton);
        nextBtn = findViewById(R.id.nextButton);
        accountType = findViewById(R.id.accountType);
        rate = findViewById(R.id.rate);
        sequenceCode = findViewById(R.id.sequenceCode);
        notes = findViewById(R.id.notes);
        accountStatus = findViewById(R.id.accountStatus);
        coreloss = findViewById(R.id.coreloss);
        multiplier = findViewById(R.id.multiplier);
        fieldStatus = findViewById(R.id.fieldStatus);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        imageFields = findViewById(R.id.imageFields);
        seniorCitizen = findViewById(R.id.seniorCitizen);
        currentArrears = findViewById(R.id.currentArrears);
        totalArrears = findViewById(R.id.totalArrears);
        printBtn = findViewById(R.id.printBtn);
        additionalKwh = findViewById(R.id.additionalKwh);
        meterNo = findViewById(R.id.meterNo);
        katas = findViewById(R.id.katas);
        prepayment = findViewById(R.id.prepayment);
        saveOnlyBtn = findViewById(R.id.saveOnlyBtn);
        percent2 = findViewById(R.id.percent2);
        percent5 = findViewById(R.id.percent5);
        demandReading = findViewById(R.id.demandReading);
        exportReading = findViewById(R.id.exportReading);
        prevReadingExport = findViewById(R.id.prevReadingExport);
        kwhExportUsed = findViewById(R.id.kwhExportUsed);

        prevBtn.setVisibility(View.GONE);
        nextBtn.setVisibility(View.GONE);

        presReading.requestFocus();

        printBtn.setVisibility(View.GONE);

        fieldStatus.setVisibility(View.GONE);
        takePhotoButton.setVisibility(View.GONE);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // MAP
        mapView = findViewById(R.id.mapviewReadingForm);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FetchAccount().execute("next", currentDpr.getSequenceCode() != null ? (currentDpr.getSequenceCode().length() < 1 ? "001" : currentDpr.getSequenceCode()) : "001");
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FetchAccount().execute("prev", currentDpr.getSequenceCode() != null ? (currentDpr.getSequenceCode().length() < 1 ? "001" : currentDpr.getSequenceCode()) : "001");
            }
        });

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        billBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Object presReadingInput = presReading.getText();
                    Object solarExportInput = exportReading.getText();
                    if (presReadingInput != null && solarExportInput != null) {
                        kwhConsumed = Double.valueOf(ReadingHelpers.getKwhUsed(currentDpr, Double.valueOf(presReadingInput.toString())));
                        kwhExportConsumed = Double.valueOf(ReadingHelpers.getKwhExportUsed(currentDpr, Double.valueOf(solarExportInput.toString())));

                        if (kwhConsumed < 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ReadingFormNetMeteredActivity.this);
                            builder.setTitle("Change Meter or Reset")
                                    .setMessage("You inputted a negative amount. Verify if this reading is correct and the meter has been changed or reset.")
                                    .setPositiveButton("CHANGE METER", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            /**
                                             * SAVE READING
                                             */
                                            Readings reading = new Readings();
                                            reading.setId(readingId);
                                            reading.setAccountNumber(currentDpr.getAccountId());
                                            reading.setServicePeriod(servicePeriod);
                                            reading.setReadingTimestamp(ObjectHelpers.getCurrentTimestamp());
                                            reading.setKwhUsed(presReadingInput.toString());
                                            reading.setSolarKwhUsed(kwhExportConsumed + "");
                                            reading.setDemandKwhUsed(demandReading.getText() != null  | !demandReading.getText().toString().isEmpty()  ? demandReading.getText().toString() : "0");
                                            reading.setFieldStatus("CHANGE METER");
                                            reading.setNotes(notes.getText().toString());
                                            reading.setUploadStatus("UPLOADABLE");
                                            reading.setMeterReader(userId);
                                            kwhConsumed = reading.getKwhUsed() != null ? Double.valueOf(reading.getKwhUsed()) : 0;
                                            if (locationComponent != null) {
                                                try {
                                                    reading.setLatitude(locationComponent.getLastKnownLocation().getLatitude() + "");
                                                    reading.setLongitude(locationComponent.getLastKnownLocation().getLongitude() + "");
                                                } catch (Exception e) {
                                                    Log.e("ERR_GET_LOC", e.getMessage());
                                                }
                                            }

                                            new ReadAndBill().execute(reading);
                                        }
                                    })
                                    .setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();

                                        }
                                    })
                                    .setNegativeButton("RESET", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            /**
                                             * SAVE READING
                                             */
                                            Readings reading = new Readings();
                                            reading.setId(readingId);
                                            reading.setAccountNumber(currentDpr.getAccountId());
                                            reading.setServicePeriod(servicePeriod);
                                            reading.setReadingTimestamp(ObjectHelpers.getCurrentTimestamp());
                                            reading.setKwhUsed(presReadingInput.toString());
                                            reading.setSolarKwhUsed(kwhExportConsumed + "");
                                            reading.setDemandKwhUsed(demandReading.getText() != null  | !demandReading.getText().toString().isEmpty()  ? demandReading.getText().toString() : "0");
                                            reading.setFieldStatus("RESET");
                                            reading.setNotes(notes.getText().toString());
                                            reading.setUploadStatus("UPLOADABLE");
                                            reading.setMeterReader(userId);

                                            kwhConsumed = Math.abs(kwhConsumed);
                                            double ogReading = reading.getKwhUsed() != null ? Double.valueOf(reading.getKwhUsed()) : 0;
                                            double prevReading = currentDpr.getKwhUsed() != null ? Double.valueOf(currentDpr.getKwhUsed()) : 0;
                                            double resetKwh = ReadingHelpers.getNearestRoundCeiling(prevReading);
                                            double resetDif = resetKwh - prevReading;
                                            kwhConsumed = ogReading + resetDif;

                                            if (locationComponent != null) {
                                                try {
                                                    reading.setLatitude(locationComponent.getLastKnownLocation().getLatitude() + "");
                                                    reading.setLongitude(locationComponent.getLastKnownLocation().getLongitude() + "");
                                                } catch (Exception e) {
                                                    Log.e("ERR_GET_LOC", e.getMessage());
                                                }
                                            }

                                            new ReadAndBill().execute(reading);
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else if (kwhConsumed == 0) {
                            /**
                             * SAVE AND BILL
                             */
                            Readings reading = new Readings();
                            reading.setId(readingId);
                            reading.setAccountNumber(currentDpr.getAccountId());
                            reading.setServicePeriod(servicePeriod);
                            reading.setReadingTimestamp(ObjectHelpers.getCurrentTimestamp());
                            reading.setKwhUsed(presReadingInput.toString());
                            reading.setSolarKwhUsed(kwhExportConsumed + "");
                            reading.setDemandKwhUsed(demandReading.getText() != null  | !demandReading.getText().toString().isEmpty()  ? demandReading.getText().toString() : "0");
                            reading.setNotes(notes.getText().toString());
                            reading.setFieldStatus(ObjectHelpers.getSelectedTextFromRadioGroup(fieldStatus, getWindow().getDecorView()));
                            reading.setUploadStatus("UPLOADABLE");
                            reading.setMeterReader(userId);
                            if (locationComponent != null) {
                                try {
                                    reading.setLatitude(locationComponent.getLastKnownLocation().getLatitude() + "");
                                    reading.setLongitude(locationComponent.getLastKnownLocation().getLongitude() + "");
                                } catch (Exception e) {
                                    Log.e("ERR_GET_LOC", e.getMessage());
                                }
                            }

                            new ReadAndBill().execute(reading);
                        } else {
                            String prevKwh = currentDpr.getKwhUsed() != null ? (currentDpr.getKwhUsed().length() > 0 ? currentDpr.getKwhUsed() : "0") : "0";
                            if (kwhConsumed > (Double.valueOf(prevKwh) * 2) && Double.valueOf(prevKwh) > 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ReadingFormNetMeteredActivity.this);
                                builder.setTitle("WARNING")
                                        .setMessage("This consumer's power usage has increased by " + ObjectHelpers.roundTwo(((kwhConsumed / Double.valueOf(prevKwh)) * 100)) + "%. Do you wish to proceed?")
                                        .setNegativeButton("REVIEW READING", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                /**
                                                 * SAVE AND BILL
                                                 */
                                                Readings reading = new Readings();
                                                reading.setId(readingId);
                                                reading.setAccountNumber(currentDpr.getAccountId());
                                                reading.setServicePeriod(servicePeriod);
                                                reading.setReadingTimestamp(ObjectHelpers.getCurrentTimestamp());
                                                reading.setKwhUsed(presReadingInput.toString());
                                                reading.setSolarKwhUsed(kwhExportConsumed + "");
                                                reading.setDemandKwhUsed(demandReading.getText() != null  | !demandReading.getText().toString().isEmpty()  ? demandReading.getText().toString() : "0");
                                                reading.setNotes(notes.getText().toString());
                                                reading.setFieldStatus("OVERREADING");
                                                reading.setUploadStatus("UPLOADABLE");
                                                reading.setReadingTimestamp(ObjectHelpers.getCurrentTimestamp());
                                                reading.setMeterReader(userId);
                                                if (locationComponent != null) {
                                                    try {
                                                        reading.setLatitude(locationComponent.getLastKnownLocation().getLatitude() + "");
                                                        reading.setLongitude(locationComponent.getLastKnownLocation().getLongitude() + "");
                                                    } catch (Exception e) {
                                                        Log.e("ERR_GET_LOC", e.getMessage());
                                                    }
                                                }
                                                new ReadAndBill().execute(reading);
                                                Toast.makeText(ReadingFormNetMeteredActivity.this, "Billed successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                builder.create().show();
                            } else {
                                /**
                                 * SAVE AND BILL
                                 */
                                Readings reading = new Readings();
                                reading.setId(readingId);
                                reading.setAccountNumber(currentDpr.getAccountId());
                                reading.setServicePeriod(servicePeriod);
                                reading.setReadingTimestamp(ObjectHelpers.getCurrentTimestamp());
                                reading.setKwhUsed(presReadingInput.toString());
                                reading.setSolarKwhUsed(kwhExportConsumed + "");
                                reading.setDemandKwhUsed(demandReading.getText() != null  | !demandReading.getText().toString().isEmpty()  ? demandReading.getText().toString() : "0");
                                reading.setNotes(notes.getText().toString());
                                reading.setUploadStatus("UPLOADABLE");
                                reading.setReadingTimestamp(ObjectHelpers.getCurrentTimestamp());
                                reading.setMeterReader(userId);
                                if (locationComponent != null) {
                                    try {
                                        reading.setLatitude(locationComponent.getLastKnownLocation().getLatitude() + "");
                                        reading.setLongitude(locationComponent.getLastKnownLocation().getLongitude() + "");
                                    } catch (Exception e) {
                                        Log.e("ERR_GET_LOC", e.getMessage());
                                    }
                                }
                                new ReadAndBill().execute(reading);
                                Toast.makeText(ReadingFormNetMeteredActivity.this, "Billed successfully", Toast.LENGTH_SHORT).show();
                            }

                        }
                    } else {
                        Toast.makeText(ReadingFormNetMeteredActivity.this, "No inputted import and export reading!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("ERR_COMP", e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(ReadingFormNetMeteredActivity.this, "No inputted import and export reading!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveOnlyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Object presReadingInput = presReading.getText();
                    Object solarExportInput = exportReading.getText();
                    if (presReadingInput != null && solarExportInput != null) {
                        kwhConsumed = Double.valueOf(ReadingHelpers.getKwhUsed(currentDpr, Double.valueOf(presReadingInput.toString())));
                        kwhExportConsumed = Double.valueOf(ReadingHelpers.getKwhExportUsed(currentDpr, Double.valueOf(solarExportInput.toString())));

                        if (kwhConsumed < 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ReadingFormNetMeteredActivity.this);
                            builder.setTitle("Change Meter or Reset")
                                    .setMessage("You inputted a negative amount. Verify if this reading is correct and the meter has been changed or reset.")
                                    .setPositiveButton("CHANGE METER", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            /**
                                             * SAVE READING
                                             */
                                            Readings reading = new Readings();
                                            reading.setId(readingId);
                                            reading.setAccountNumber(currentDpr.getAccountId());
                                            reading.setServicePeriod(servicePeriod);
                                            reading.setReadingTimestamp(ObjectHelpers.getCurrentTimestamp());
                                            reading.setKwhUsed(presReadingInput.toString());
                                            reading.setSolarKwhUsed(kwhExportConsumed + "");
                                            reading.setDemandKwhUsed(demandReading.getText() != null  | !demandReading.getText().toString().isEmpty()  ? demandReading.getText().toString() : "0");
                                            reading.setFieldStatus("CHANGE METER");
                                            reading.setNotes(notes.getText().toString());
                                            reading.setUploadStatus("UPLOADABLE");
                                            reading.setMeterReader(userId);
                                            kwhConsumed = reading.getKwhUsed() != null ? Double.valueOf(reading.getKwhUsed()) : 0;
                                            if (locationComponent != null) {
                                                try {
                                                    reading.setLatitude(locationComponent.getLastKnownLocation().getLatitude() + "");
                                                    reading.setLongitude(locationComponent.getLastKnownLocation().getLongitude() + "");
                                                } catch (Exception e) {
                                                    Log.e("ERR_GET_LOC", e.getMessage());
                                                }
                                            }

                                            new ReadAndBill().execute(reading);
                                        }
                                    })
                                    .setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();

                                        }
                                    })
                                    .setNegativeButton("RESET", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            /**
                                             * SAVE READING
                                             */
                                            Readings reading = new Readings();
                                            reading.setId(readingId);
                                            reading.setAccountNumber(currentDpr.getAccountId());
                                            reading.setServicePeriod(servicePeriod);
                                            reading.setReadingTimestamp(ObjectHelpers.getCurrentTimestamp());
                                            reading.setKwhUsed(presReadingInput.toString());
                                            reading.setSolarKwhUsed(kwhExportConsumed + "");
                                            reading.setDemandKwhUsed(demandReading.getText() != null  | !demandReading.getText().toString().isEmpty()  ? demandReading.getText().toString() : "0");
                                            reading.setFieldStatus("RESET");
                                            reading.setNotes(notes.getText().toString());
                                            reading.setUploadStatus("UPLOADABLE");
                                            reading.setMeterReader(userId);

                                            kwhConsumed = Math.abs(kwhConsumed);
                                            double ogReading = reading.getKwhUsed() != null ? Double.valueOf(reading.getKwhUsed()) : 0;
                                            double prevReading = currentDpr.getKwhUsed() != null ? Double.valueOf(currentDpr.getKwhUsed()) : 0;
                                            double resetKwh = ReadingHelpers.getNearestRoundCeiling(prevReading);
                                            double resetDif = resetKwh - prevReading;
                                            kwhConsumed = ogReading + resetDif;

                                            if (locationComponent != null) {
                                                try {
                                                    reading.setLatitude(locationComponent.getLastKnownLocation().getLatitude() + "");
                                                    reading.setLongitude(locationComponent.getLastKnownLocation().getLongitude() + "");
                                                } catch (Exception e) {
                                                    Log.e("ERR_GET_LOC", e.getMessage());
                                                }
                                            }

                                            new ReadAndBill().execute(reading);
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else if (kwhConsumed == 0) {
                            /**
                             * SAVE AND BILL
                             */
                            Readings reading = new Readings();
                            reading.setId(readingId);
                            reading.setAccountNumber(currentDpr.getAccountId());
                            reading.setServicePeriod(servicePeriod);
                            reading.setReadingTimestamp(ObjectHelpers.getCurrentTimestamp());
                            reading.setKwhUsed(presReadingInput.toString());
                            reading.setSolarKwhUsed(kwhExportConsumed + "");
                            reading.setDemandKwhUsed(demandReading.getText() != null  | !demandReading.getText().toString().isEmpty()  ? demandReading.getText().toString() : "0");
                            reading.setNotes(notes.getText().toString());
                            reading.setFieldStatus(ObjectHelpers.getSelectedTextFromRadioGroup(fieldStatus, getWindow().getDecorView()));
                            reading.setUploadStatus("UPLOADABLE");
                            reading.setMeterReader(userId);
                            if (locationComponent != null) {
                                try {
                                    reading.setLatitude(locationComponent.getLastKnownLocation().getLatitude() + "");
                                    reading.setLongitude(locationComponent.getLastKnownLocation().getLongitude() + "");
                                } catch (Exception e) {
                                    Log.e("ERR_GET_LOC", e.getMessage());
                                }
                            }

                            new ReadAndBillDontPrint().execute(reading);
                        } else {
                            String prevKwh = currentDpr.getKwhUsed() != null ? (currentDpr.getKwhUsed().length() > 0 ? currentDpr.getKwhUsed() : "0") : "0";
                            if (kwhConsumed > (Double.valueOf(prevKwh) * 2) && Double.valueOf(prevKwh) > 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ReadingFormNetMeteredActivity.this);
                                builder.setTitle("WARNING")
                                        .setMessage("This consumer's power usage has increased by " + ObjectHelpers.roundTwo(((kwhConsumed / Double.valueOf(prevKwh)) * 100)) + "%. Do you wish to proceed?")
                                        .setNegativeButton("REVIEW READING", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                /**
                                                 * SAVE AND BILL
                                                 */
                                                Readings reading = new Readings();
                                                reading.setId(readingId);
                                                reading.setAccountNumber(currentDpr.getAccountId());
                                                reading.setServicePeriod(servicePeriod);
                                                reading.setReadingTimestamp(ObjectHelpers.getCurrentTimestamp());
                                                reading.setKwhUsed(presReadingInput.toString());
                                                reading.setSolarKwhUsed(kwhExportConsumed + "");
                                                reading.setDemandKwhUsed(demandReading.getText() != null  | !demandReading.getText().toString().isEmpty()  ? demandReading.getText().toString() : "0");
                                                reading.setNotes(notes.getText().toString());
                                                reading.setFieldStatus("OVERREADING");
                                                reading.setUploadStatus("UPLOADABLE");
                                                reading.setReadingTimestamp(ObjectHelpers.getCurrentTimestamp());
                                                reading.setMeterReader(userId);
                                                if (locationComponent != null) {
                                                    try {
                                                        reading.setLatitude(locationComponent.getLastKnownLocation().getLatitude() + "");
                                                        reading.setLongitude(locationComponent.getLastKnownLocation().getLongitude() + "");
                                                    } catch (Exception e) {
                                                        Log.e("ERR_GET_LOC", e.getMessage());
                                                    }
                                                }
                                                new ReadAndBillDontPrint().execute(reading);
                                                Toast.makeText(ReadingFormNetMeteredActivity.this, "Billed successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                builder.create().show();
                            } else {
                                /**
                                 * SAVE AND BILL
                                 */
                                Readings reading = new Readings();
                                reading.setId(readingId);
                                reading.setAccountNumber(currentDpr.getAccountId());
                                reading.setServicePeriod(servicePeriod);
                                reading.setReadingTimestamp(ObjectHelpers.getCurrentTimestamp());
                                reading.setKwhUsed(presReadingInput.toString());
                                reading.setSolarKwhUsed(kwhExportConsumed + "");
                                reading.setDemandKwhUsed(demandReading.getText() != null  | !demandReading.getText().toString().isEmpty()  ? demandReading.getText().toString() : "0");
                                reading.setNotes(notes.getText().toString());
                                reading.setUploadStatus("UPLOADABLE");
                                reading.setReadingTimestamp(ObjectHelpers.getCurrentTimestamp());
                                reading.setMeterReader(userId);
                                if (locationComponent != null) {
                                    try {
                                        reading.setLatitude(locationComponent.getLastKnownLocation().getLatitude() + "");
                                        reading.setLongitude(locationComponent.getLastKnownLocation().getLongitude() + "");
                                    } catch (Exception e) {
                                        Log.e("ERR_GET_LOC", e.getMessage());
                                    }
                                }
                                new ReadAndBillDontPrint().execute(reading);
                                Toast.makeText(ReadingFormNetMeteredActivity.this, "Billed successfully", Toast.LENGTH_SHORT).show();
                            }

                        }
                    } else {
                        Toast.makeText(ReadingFormNetMeteredActivity.this, "No inputted present reading!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("ERR_COMP", e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(ReadingFormNetMeteredActivity.this, "No inputted present reading!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        presReading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence != null) {
                        Double kwh = Double.valueOf(ReadingHelpers.getKwhUsed(currentDpr, Double.valueOf(charSequence.toString())));
                        Double mult = currentDpr.getMultiplier() != null && ReadingHelpers.isNumeric(currentDpr.getMultiplier()) ? Double.valueOf(currentDpr.getMultiplier()) : 1;
                        kwh = kwh * mult;
                        if (kwh < 0) {
                            kwhUsed.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_baseline_error_outline_18), null);
                            fieldStatus.setVisibility(View.GONE);
                            fieldStatus.clearCheck();
                            revealPhotoButton(false);
                        } else if (kwh == 0 && currentDpr.getAccountStatus().equals("ACTIVE")) {
                            /**
                             * SHOW OPTIONS FOR ZERO READING
                             */
                            fieldStatus.setVisibility(View.VISIBLE);
                            fieldStatus.check(R.id.stuckUp);
                            takePhotoButton.setEnabled(true);
                            revealPhotoButton(true);
                        } else {
                            kwhUsed.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            fieldStatus.setVisibility(View.GONE);
                            fieldStatus.clearCheck();
                            revealPhotoButton(false);
                        }
                        kwhUsed.setText(ObjectHelpers.roundTwo(kwh));
                    } else {
                        kwhUsed.setText("");
                        kwhUsed.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    }
                } catch (Exception e) {
                    kwhUsed.setText("");
                    kwhUsed.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        exportReading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence != null) {
                        Double kwh = Double.valueOf(ReadingHelpers.getKwhExportUsed(currentDpr, Double.valueOf(charSequence.toString())));

                        if (kwh < 0) {
                            kwhExportUsed.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_baseline_error_outline_18), null);
                            fieldStatus.setVisibility(View.GONE);
                            fieldStatus.clearCheck();
                            revealPhotoButton(false);
                        } else {
                            kwhExportUsed.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            fieldStatus.setVisibility(View.GONE);
                            fieldStatus.clearCheck();
                            revealPhotoButton(false);
                        }
                        kwhExportUsed.setText(ObjectHelpers.roundTwo(kwh));
                    } else {
                        kwhExportUsed.setText("");
                        kwhExportUsed.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    }
                } catch (Exception e) {
                    kwhExportUsed.setText("");
                    kwhExportUsed.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                print(currentBill, currentRate, currentDpr);
                printViaEscPos(currentBill, currentRate, currentDpr, currentReading);
            }
        });

        new FetchInitID().execute(id);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        try {
            this.mapboxMap = mapboxMap;
            mapboxMap.setStyle(new Style.Builder()
                    .fromUri(getResources().getString(R.string.mapbox_style)), new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    setStyle(style);

                    plotMarker();

                    enableLocationComponent(style);
                }
            });
        } catch (Exception e) {
            Log.e("ERR_INIT_MAPBOX", e.getMessage());
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        try {
            // Check if permissions are enabled and if not request
            if (PermissionsManager.areLocationPermissionsGranted(this)) {

                // Get an instance of the component
                locationComponent = mapboxMap.getLocationComponent();

                // Activate with options
                locationComponent.activateLocationComponent(
                        LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

                // Enable to make component visible
                locationComponent.setLocationComponentEnabled(true);

                // Set the component's camera mode
                locationComponent.setCameraMode(CameraMode.TRACKING);

                // Set the component's render mode
                locationComponent.setRenderMode(RenderMode.COMPASS);

            } else {
                permissionsManager = new PermissionsManager(this);
                permissionsManager.requestLocationPermissions(this);
            }
        } catch (Exception e) {
            Log.e("ERR_LOAD_MAP", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "READ_PHONE_STATE Denied", Toast.LENGTH_SHORT)
                            .show();
                } else {

                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int res = checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE);
            if (res != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE}, 123);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public class FetchInitID extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            readingId = ObjectHelpers.getTimeInMillis() + "-" + ObjectHelpers.generateRandomString();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                currentDpr = db.downloadedPreviousReadingsDao().getOne(strings[0]);

                // CONFIGURE CONSUMER TYPE
                if (currentDpr.getAccountType().equals("NONE")) {
                    currentDpr.setAccountType("RESIDENTIAL");
                }

                currentRate = db.ratesDao().getOne(ReadingHelpers.getAccountType(currentDpr), currentDpr.getTown(), servicePeriod);
                currentReading = db.readingsDao().getOne(currentDpr.getAccountId(), servicePeriod);
                currentBill = db.billsDao().getOneByAccountNumberAndServicePeriod(currentDpr.getAccountId(), servicePeriod);
                user = db.usersDao().getOneById(userId);
            } catch (Exception e) {
                Log.e("ERR_FETCH_NIT", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            accountName.setText(currentDpr.getServiceAccountName() != null ? currentDpr.getServiceAccountName() : "n/a");
            accountNumber.setText(currentDpr.getOldAccountNo());
            if (currentDpr.getChangeMeterStartKwh() != null) {
                prevReading.setText(currentDpr.getChangeMeterStartKwh());
            } else {
                prevReading.setText(currentDpr.getKwhUsed()!=null ? (currentDpr.getKwhUsed().length() > 0 ? currentDpr.getKwhUsed() : "0") : "0");
            }
            accountType.setText(ReadingHelpers.getAccountType(currentDpr));
            sequenceCode.setText(currentDpr.getSequenceCode());
            rate.setText(currentRate.getTotalRateVATIncluded() != null ? (ObjectHelpers.roundFour(Double.parseDouble(currentRate.getTotalRateVATIncluded()))) : "0");
            accountStatus.setText(currentDpr.getAccountStatus());
            multiplier.setText(currentDpr.getMultiplier());
            coreloss.setText(currentDpr.getCoreloss());
            seniorCitizen.setText(currentDpr.getSeniorCitizen() != null ? currentDpr.getSeniorCitizen() : "No");
            currentArrears.setText(currentDpr.getArrearsLedger() != null ? ObjectHelpers.roundTwo(Double.valueOf(currentDpr.getArrearsLedger())) : "0.0");
            totalArrears.setText(currentDpr.getBalance() != null ? ObjectHelpers.roundTwo(Double.valueOf(currentDpr.getBalance())) : "0.0");
            prevReadingExport.setText(currentDpr.getSolarKwhUsed() != null ? currentDpr.getSolarKwhUsed() : "0");
            if (currentDpr.getSolarResidualCredit() != null) {
                additionalKwh.setText(currentDpr.getSolarResidualCredit());
            } else {
                additionalKwh.setText("0");
            }
            if (currentDpr.getMeterSerial() != null) {
                meterNo.setText(currentDpr.getMeterSerial());
            } else {
                meterNo.setText("-");
            }

            // 2%
            if (currentDpr.getEwt2Percent() != null && currentDpr.getEwt2Percent().equals("Yes")) {
                percent2.setText("Yes");
            } else {
                percent2.setText("No");
            }

            // 5%
            if (currentDpr.getEvat5Percent() != null && currentDpr.getEvat5Percent().equals("Yes")) {
                percent5.setText("Yes");
            } else {
                percent5.setText("No");
            }

            // KATAS
            if (currentDpr.getKatasNgVat() != null) {
                katas.setText(ObjectHelpers.roundTwo(Double.valueOf(currentDpr.getKatasNgVat())));
            } else {
                katas.setText("0.0");
            }

            // PREPAYMENT
            if (currentDpr.getDeposit() != null) {
                prepayment.setText(ObjectHelpers.roundTwo(Double.valueOf(currentDpr.getDeposit())));
            } else {
                prepayment.setText("0.0");
            }

            /**
             * IF ALREADY READ
             */
            if (currentReading != null) {
                presReading.setText(currentReading.getKwhUsed());
                notes.setText(currentReading.getNotes());
                kwhUsed.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_baseline_check_circle_18), null, null, null);
                setSelectedStatus(currentReading.getFieldStatus());

                if (currentReading.getUploadStatus().equals("UPLOADED")) {
                    billBtn.setEnabled(false);
                    saveOnlyBtn.setEnabled(false);
                    takePhotoButton.setEnabled(false);
                } else {
                    billBtn.setEnabled(true);
                    saveOnlyBtn.setEnabled(true);
                    takePhotoButton.setEnabled(true);

                    /**
                     * SHOW TAKE PHOTO BUTTON
                     */
                    revealPhotoButton(true);
                }
            } else {
                presReading.setText("");
                notes.setText("");
                kwhUsed.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                revealPhotoButton(false);
            }

            /**
             * IF HAS BILL
             */
            if (currentBill != null) {
                printBtn.setVisibility(View.VISIBLE);
            } else {
                printBtn.setVisibility(View.GONE);
            }

            new GetPhotos().execute();
        }
    }

    public class FetchAccount extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            readingId = ObjectHelpers.getTimeInMillis() + "-" + ObjectHelpers.generateRandomString();
            prevBtn.setEnabled(false);
            nextBtn.setEnabled(false);
            billBtn.setEnabled(true);
            takePhotoButton.setEnabled(true);
            presReading.setText("");
            presReading.requestFocus();
            demandReading.setText("");
            fieldStatus.clearCheck();
            fieldStatus.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... strings) { //strings[0] = next, prev | strings[1] = sequence
            String areaCode = currentDpr.getTown();
            String groupCode = currentDpr.getGroupCode();
            if (strings[0].equals("prev")) {
                currentDpr = db.downloadedPreviousReadingsDao().getPrevious(Integer.valueOf(strings[1]));

                if (currentDpr == null) {
                    currentDpr = db.downloadedPreviousReadingsDao().getLast();
                    currentRate = db.ratesDao().getOne(ReadingHelpers.getAccountType(currentDpr), currentDpr.getTown(), servicePeriod);
                    currentReading = db.readingsDao().getOne(currentDpr.getAccountId(), servicePeriod);
                    currentBill = db.billsDao().getOneByAccountNumberAndServicePeriod(currentDpr.getAccountId(), servicePeriod);
                } else {
                    currentRate = db.ratesDao().getOne(ReadingHelpers.getAccountType(currentDpr), currentDpr.getTown(), servicePeriod);
                    currentReading = db.readingsDao().getOne(currentDpr.getAccountId(), servicePeriod);
                    currentBill = db.billsDao().getOneByAccountNumberAndServicePeriod(currentDpr.getAccountId(), servicePeriod);
                }
            } else {
                currentDpr = db.downloadedPreviousReadingsDao().getNext(Integer.valueOf(strings[1]));

                if (currentDpr == null) {
                    currentDpr = db.downloadedPreviousReadingsDao().getFirst();
                    currentRate = db.ratesDao().getOne(ReadingHelpers.getAccountType(currentDpr), currentDpr.getTown(), servicePeriod);
                    currentReading = db.readingsDao().getOne(currentDpr.getAccountId(), servicePeriod);
                    currentBill = db.billsDao().getOneByAccountNumberAndServicePeriod(currentDpr.getAccountId(), servicePeriod);
                } else {
                    currentRate = db.ratesDao().getOne(ReadingHelpers.getAccountType(currentDpr), currentDpr.getTown(), servicePeriod);
                    currentReading = db.readingsDao().getOne(currentDpr.getAccountId(), servicePeriod);
                    currentBill = db.billsDao().getOneByAccountNumberAndServicePeriod(currentDpr.getAccountId(), servicePeriod);
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            accountName.setText(currentDpr.getServiceAccountName() != null ? currentDpr.getServiceAccountName() : "n/a");
            accountNumber.setText(currentDpr.getOldAccountNo());
            if (currentDpr.getChangeMeterStartKwh() != null) {
                prevReading.setText(currentDpr.getChangeMeterStartKwh());
            } else {
                prevReading.setText(currentDpr.getKwhUsed()!=null ? currentDpr.getKwhUsed() : "0");
            }
            accountType.setText(ReadingHelpers.getAccountType(currentDpr));
            sequenceCode.setText(currentDpr.getSequenceCode());
            rate.setText(ObjectHelpers.roundFour(Double.parseDouble(currentRate.getTotalRateVATIncluded())));
            accountStatus.setText(currentDpr.getAccountStatus());
            multiplier.setText(currentDpr.getMultiplier());
            coreloss.setText(currentDpr.getCoreloss());
            seniorCitizen.setText(currentDpr.getSeniorCitizen() != null ? currentDpr.getSeniorCitizen() : "No");
            currentArrears.setText(currentDpr.getArrearsLedger() != null ? ObjectHelpers.roundTwo(Double.valueOf(currentDpr.getArrearsLedger())) : "0.0");
            totalArrears.setText(currentDpr.getBalance() != null ? ObjectHelpers.roundTwo(Double.valueOf(currentDpr.getBalance())) : "0.0");
            prevReadingExport.setText(currentDpr.getSolarKwhUsed() != null ? currentDpr.getSolarKwhUsed() : "0");

            if (currentDpr.getSolarResidualCredit() != null) {
                additionalKwh.setText(currentDpr.getSolarResidualCredit());
            } else {
                additionalKwh.setText("0");
            }
            if (currentDpr.getMeterSerial() != null) {
                meterNo.setText(currentDpr.getMeterSerial());
            } else {
                meterNo.setText("-");
            }

            // 2%
            if (currentDpr.getEwt2Percent() != null && currentDpr.getEwt2Percent().equals("Yes")) {
                percent2.setText("Yes");
            } else {
                percent2.setText("No");
            }

            // 5%
            if (currentDpr.getEvat5Percent() != null && currentDpr.getEvat5Percent().equals("Yes")) {
                percent5.setText("Yes");
            } else {
                percent5.setText("No");
            }

            // KATAS
            if (currentDpr.getKatasNgVat() != null) {
                katas.setText(ObjectHelpers.roundTwo(Double.valueOf(currentDpr.getKatasNgVat())));
            } else {
                katas.setText("0.0");
            }

            // PREPAYMENT
            if (currentDpr.getDeposit() != null) {
                prepayment.setText(ObjectHelpers.roundTwo(Double.valueOf(currentDpr.getDeposit())));
            } else {
                prepayment.setText("0.0");
            }

            prevBtn.setEnabled(true);
            nextBtn.setEnabled(true);

            plotMarker();

            /**
             * IF ALREADY READ
             */
            if (currentReading != null) {
                presReading.setText(currentReading.getKwhUsed());
                notes.setText(currentReading.getNotes());
                kwhUsed.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_baseline_check_circle_18), null, null, null);
                setSelectedStatus(currentReading.getFieldStatus());

                if (currentReading.getUploadStatus().equals("UPLOADED")) {
                    billBtn.setEnabled(false);
                    saveOnlyBtn.setEnabled(false);
                    takePhotoButton.setEnabled(false);
                } else {
                    billBtn.setEnabled(true);
                    saveOnlyBtn.setEnabled(true);
                    takePhotoButton.setEnabled(true);

                    /**
                     * SHOW TAKE PHOTO BUTTON
                     */
                    revealPhotoButton(true);
                }
            } else {
                presReading.setText("");
                notes.setText("");
                kwhUsed.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                revealPhotoButton(false);
            }

            /**
             * IF HAS BILL
             */
            if (currentBill != null) {
                printBtn.setVisibility(View.VISIBLE);
            } else {
                printBtn.setVisibility(View.GONE);
            }

            new GetPhotos().execute();
        }
    }

    public boolean hasLatLong(DownloadedPreviousReadings downloadedPreviousReadings) {
        if (downloadedPreviousReadings.getLatitude() != null) {
            return true;
        } else {
            return false;
        }
    }

    public void plotMarker() {
        try {
            if (symbolManager != null) {
                symbolManager.deleteAll();
            }
            symbolManager = new SymbolManager(mapView, mapboxMap, style);

            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);

            if (currentDpr != null) {
                /**
                 * PLOT TO MAP
                 */
                if (hasLatLong(currentDpr)) {
                    SymbolOptions symbolOptions;
                    if (currentDpr.getAccountStatus().equals("ACTIVE")) {
                        symbolOptions = new SymbolOptions()
                                .withLatLng(new LatLng(Double.valueOf(currentDpr.getLatitude()), Double.valueOf(currentDpr.getLongitude())))
                                .withData(new JsonParser().parse("{" +
                                        "'id' : '" + currentDpr.getAccountId() + "'," +
                                        "'svcPeriod' : '" + currentDpr.getServicePeriod() + "'}"))
                                .withIconImage("place-black-24dp")
                                .withIconSize(1f);
                    } else if (currentDpr.getAccountStatus().equals("DISCONNECTED")) {
                        symbolOptions = new SymbolOptions()
                                .withLatLng(new LatLng(Double.valueOf(currentDpr.getLatitude()), Double.valueOf(currentDpr.getLongitude())))
                                .withData(new JsonParser().parse("{" +
                                        "'id' : '" + currentDpr.getAccountId() + "'," +
                                        "'svcPeriod' : '" + currentDpr.getServicePeriod() + "'}"))
                                .withIconImage("level-crossing")
                                .withIconSize(.50f);
                    } else {
                        symbolOptions = new SymbolOptions()
                                .withLatLng(new LatLng(Double.valueOf(currentDpr.getLatitude()), Double.valueOf(currentDpr.getLongitude())))
                                .withData(new JsonParser().parse("{" +
                                        "'id' : '" + currentDpr.getAccountId() + "'," +
                                        "'svcPeriod' : '" + currentDpr.getServicePeriod() + "'}"))
                                .withIconImage("marker-blue")
                                .withIconSize(.50f);
                    }

                    Symbol symbol = symbolManager.create(symbolOptions);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(Double.valueOf(currentDpr.getLatitude()), Double.valueOf(currentDpr.getLongitude())))
                            .zoom(13)
                            .build();

                    if (mapboxMap != null) {
                        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1200);
                    } else {
                        Toast.makeText(ReadingFormNetMeteredActivity.this, "Map is still loading, try again in a couple of seconds", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ERR_PLOT_MRKER", e.getMessage());
        }
    }

    /**
     * SAVE READING AND BILL AND PRINT
     */
    public class ReadAndBill extends AsyncTask<Readings, Void, Boolean> {
        String errors;

        @Override
        protected Boolean doInBackground(Readings... readings) {
            try {
                if (readings != null) {
                    Readings reading = readings[0];
                    if (reading != null) {
                        /** INSERT READING **/
                        if (currentReading != null) {
                            currentReading.setKwhUsed(reading.getKwhUsed());
                            currentReading.setNotes("PERFORMED RE-READING");
                            currentReading.setLatitude(reading.getLatitude());
                            currentReading.setLongitude(reading.getLongitude());
                            db.readingsDao().updateAll(currentReading);
                        } else {
                            db.readingsDao().insertAll(reading);
                        }

                        /** UPDATE STATUS OF DOWNLOADED READING **/
                        currentDpr.setStatus("READ");
                        db.downloadedPreviousReadingsDao().updateAll(currentDpr);

                        /** READ ONLY **/
                        if ((currentDpr.getAccountStatus() != null && (currentDpr.getAccountStatus().equals("DISCONNECTED")) && kwhConsumed == 0) || currentDpr.getChangeMeterAdditionalKwh() != null) {

                        } else {
                            /** PERFORM BILLING **/
                            if (kwhConsumed == 0) {
                                if (reading.getFieldStatus() != null && reading.getFieldStatus().equals("NOT IN USE")) {
                                    if (currentBill != null) {
                                        currentBill = ReadingHelpers.generateNetMeteredBill(currentBill, currentDpr, currentRate, kwhConsumed, Double.valueOf(reading.getKwhUsed()), (ObjectHelpers.checkNull(demandReading)), kwhExportConsumed, ObjectHelpers.checkNull(exportReading), userId);

                                        db.billsDao().updateAll(currentBill);
                                    } else {
                                        currentBill = ReadingHelpers.generateNetMeteredBill(null, currentDpr, currentRate, kwhConsumed, Double.valueOf(reading.getKwhUsed()), (ObjectHelpers.checkNull(demandReading)), kwhExportConsumed, ObjectHelpers.checkNull(exportReading), userId);

                                        db.billsDao().insertAll(currentBill);
                                    }
                                }
                            } else if (kwhConsumed <= -1) {
                                if (reading.getFieldStatus() != null && reading.getFieldStatus().equals("RESET")) {
                                    if (currentBill != null) {
                                        currentBill = ReadingHelpers.generateNetMeteredBill(currentBill, currentDpr, currentRate, kwhConsumed, Double.valueOf(reading.getKwhUsed()), (ObjectHelpers.checkNull(demandReading)), kwhExportConsumed, ObjectHelpers.checkNull(exportReading), userId);

                                        db.billsDao().updateAll(currentBill);
                                    } else {
                                        currentBill = ReadingHelpers.generateNetMeteredBill(null, currentDpr, currentRate, kwhConsumed, Double.valueOf(reading.getKwhUsed()), (ObjectHelpers.checkNull(demandReading)), kwhExportConsumed, ObjectHelpers.checkNull(exportReading), userId);

                                        db.billsDao().insertAll(currentBill);
                                    }
                                }
                            } else {
                                if (currentBill != null) {
                                    currentBill = ReadingHelpers.generateNetMeteredBill(currentBill, currentDpr, currentRate, kwhConsumed, Double.valueOf(reading.getKwhUsed()), (ObjectHelpers.checkNull(demandReading)), kwhExportConsumed, ObjectHelpers.checkNull(exportReading), userId);

                                    db.billsDao().updateAll(currentBill);
                                } else {
                                    currentBill = ReadingHelpers.generateNetMeteredBill(null, currentDpr, currentRate, kwhConsumed, Double.valueOf(reading.getKwhUsed()), (ObjectHelpers.checkNull(demandReading)), kwhExportConsumed, ObjectHelpers.checkNull(exportReading), userId);

                                    db.billsDao().insertAll(currentBill);
                                }
                            }
                        }
                    }
                }
                return true;
            } catch (Exception e) {
                Log.e("ERR_READ_AND_BILL", e.getMessage());
                e.printStackTrace();
                errors = e.getMessage();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                /**
                 * PRINT BILL
                 */
//                print(currentBill, currentRate, currentDpr);
                if (currentBill != null) {
                    try {
                        printViaEscPos(currentBill, currentRate, currentDpr, currentReading);
                    } catch (Exception e) {
                        Log.e("ERROR PRINTING", e.getMessage());
                        Toast.makeText(ReadingFormNetMeteredActivity.this, "Error printing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                try {
                    TextLogger.appendLog(currentReading.getAccountNumber() + "\t" +
                                    currentReading.getReadingTimestamp() + "\t" +
                                    currentReading.getKwhUsed() + "\t" +
                                    currentReading.getDemandKwhUsed() + "\t" +
                                    currentReading.getServicePeriod() + "\t",
                            servicePeriod,
                            ReadingFormNetMeteredActivity.this);
                } catch (Exception e) {
                    Log.e("ERROR_LOGGING_TEXT", e.getMessage());
                }

                /**
                 * PROCEED TO NEXT
                 */
                finish();
//                new FetchAccount().execute("next", currentDpr.getSequenceCode() != null ? (currentDpr.getSequenceCode().length() < 1 ? "001" : currentDpr.getSequenceCode()) : "001");
            } else {
                AlertHelpers.showMessageDialog(ReadingFormNetMeteredActivity.this, "ERROR", "An error occurred while performing the reading. \n" + errors);
            }
        }
    }

    /**
     * SAVE READING AND BILL AND SAVE
     */
    public class ReadAndBillDontPrint extends AsyncTask<Readings, Void, Boolean> {
        String errors;

        @Override
        protected Boolean doInBackground(Readings... readings) {
            try {
                if (readings != null) {
                    Readings reading = readings[0];
                    if (reading != null) {
                        /** INSERT READING **/
                        if (currentReading != null) {
                            currentReading.setKwhUsed(reading.getKwhUsed());
                            currentReading.setNotes("PERFORMED RE-READING");
                            currentReading.setLatitude(reading.getLatitude());
                            currentReading.setLongitude(reading.getLongitude());
                            db.readingsDao().updateAll(currentReading);
                        } else {
                            db.readingsDao().insertAll(reading);
                        }

                        /** UPDATE STATUS OF DOWNLOADED READING **/
                        currentDpr.setStatus("READ");
                        db.downloadedPreviousReadingsDao().updateAll(currentDpr);

                        /** READ ONLY **/
                        if ((currentDpr.getAccountStatus() != null && (currentDpr.getAccountStatus().equals("DISCONNECTED")) && kwhConsumed == 0) || currentDpr.getChangeMeterAdditionalKwh() != null) {

                        } else {
                            /** PERFORM BILLING **/
                            if (kwhConsumed == 0) {
                                if (reading.getFieldStatus() != null && reading.getFieldStatus().equals("NOT IN USE")) {
                                    if (currentBill != null) {
                                        currentBill = ReadingHelpers.generateNetMeteredBill(currentBill, currentDpr, currentRate, kwhConsumed, Double.valueOf(reading.getKwhUsed()), (ObjectHelpers.checkNull(demandReading)), kwhExportConsumed, ObjectHelpers.checkNull(exportReading), userId);

                                        db.billsDao().updateAll(currentBill);
                                    } else {
                                        currentBill = ReadingHelpers.generateNetMeteredBill(null, currentDpr, currentRate, kwhConsumed, Double.valueOf(reading.getKwhUsed()), (ObjectHelpers.checkNull(demandReading)), kwhExportConsumed, ObjectHelpers.checkNull(exportReading), userId);

                                        db.billsDao().insertAll(currentBill);
                                    }
                                }
                            } else if (kwhConsumed <= -1) {
                                if (reading.getFieldStatus() != null && reading.getFieldStatus().equals("RESET")) {
                                    if (currentBill != null) {
                                        currentBill = ReadingHelpers.generateNetMeteredBill(currentBill, currentDpr, currentRate, kwhConsumed, Double.valueOf(reading.getKwhUsed()), (ObjectHelpers.checkNull(demandReading)), kwhExportConsumed, ObjectHelpers.checkNull(exportReading), userId);

                                        db.billsDao().updateAll(currentBill);
                                    } else {
                                        currentBill = ReadingHelpers.generateNetMeteredBill(null, currentDpr, currentRate, kwhConsumed, Double.valueOf(reading.getKwhUsed()), (ObjectHelpers.checkNull(demandReading)), kwhExportConsumed, ObjectHelpers.checkNull(exportReading), userId);

                                        db.billsDao().insertAll(currentBill);
                                    }
                                }
                            } else {
                                if (currentBill != null) {
                                    currentBill = ReadingHelpers.generateNetMeteredBill(currentBill, currentDpr, currentRate, kwhConsumed, Double.valueOf(reading.getKwhUsed()), (ObjectHelpers.checkNull(demandReading)), kwhExportConsumed, ObjectHelpers.checkNull(exportReading), userId);

                                    db.billsDao().updateAll(currentBill);
                                } else {
                                    currentBill = ReadingHelpers.generateNetMeteredBill(null, currentDpr, currentRate, kwhConsumed, Double.valueOf(reading.getKwhUsed()), (ObjectHelpers.checkNull(demandReading)), kwhExportConsumed, ObjectHelpers.checkNull(exportReading), userId);

                                    db.billsDao().insertAll(currentBill);
                                }
                            }
                        }
                    }
                }
                return true;
            } catch (Exception e) {
                Log.e("ERR_READ_AND_BILL", e.getMessage());
                e.printStackTrace();
                errors = e.getMessage();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                /**
                 * PRINT BILL
                 */
//                print(currentBill, currentRate, currentDpr);
                if (currentDpr.getAccountStatus().equals("ACTIVE")) {
                    try {
//                        printViaEscPos(currentBill, currentRate, currentDpr, currentReading);
                    } catch (Exception e) {
                        Log.e("ERROR PRINTING", e.getMessage());
                        Toast.makeText(ReadingFormNetMeteredActivity.this, "Error printing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                try {
                    TextLogger.appendLog(currentReading.getAccountNumber() + "\t" +
                                    currentReading.getReadingTimestamp() + "\t" +
                                    currentReading.getKwhUsed() + "\t" +
                                    currentReading.getDemandKwhUsed() + "\t" +
                                    currentReading.getServicePeriod() + "\t",
                            servicePeriod,
                            ReadingFormNetMeteredActivity.this);
                } catch (Exception e) {
                    Log.e("ERROR_LOGGING_TEXT", e.getMessage());
                }

                /**
                 * PROCEED TO NEXT
                 */
                finish();
//                new FetchAccount().execute("next", currentDpr.getSequenceCode() != null ? (currentDpr.getSequenceCode().length() < 1 ? "001" : currentDpr.getSequenceCode()) : "001");
            } else {
                AlertHelpers.showMessageDialog(ReadingFormNetMeteredActivity.this, "ERROR", "An error occurred while performing the reading. \n" + errors);
            }
        }
    }

    public void setSelectedStatus(String status) {
        try {
            if (status.equals("STUCK-UP")) {
                fieldStatus.check(R.id.stuckUp);
            } else if (status.equals("NOT IN USE")) {
                fieldStatus.check(R.id.notInUse);
            } else if (status.equals("NO DISPLAY")) {
                fieldStatus.check(R.id.noDisplay);
            } else {
                fieldStatus.clearCheck();
            }
        } catch (Exception e) {
            Log.e("ERR_SET_SEL", e.getMessage());
            fieldStatus.clearCheck();
        }
    }

    /**
     * TAKE PHOTOS
     */
    private void dispatchTakePictureIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra( MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);

            File pictureFile = null;
            try {
                pictureFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.lopez.julz.readandbill",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "READING_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        new SavePhotoToDatabase().execute(currentPhotoPath);
        return image;
    }

    public class SavePhotoToDatabase extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (strings != null) {
                    String photo = strings[0];

                    ReadingImages photoObject = new ReadingImages(ObjectHelpers.getTimeInMillis() + "-" + ObjectHelpers.generateRandomString(), photo, null, servicePeriod, currentDpr.getAccountId(), "UPLOADABLE");
                    db.readingImagesDao().insertAll(photoObject);
                }
            } catch (Exception e) {
                Log.e("ERR_SAVE_PHOTO_DB", e.getMessage());
            }

            return null;
        }
    }

    public class GetPhotos extends AsyncTask<Void, Void, Void> {

        List<ReadingImages> photosList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageFields.removeAllViews();
            photosList.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                photosList.addAll(db.readingImagesDao().getAll(servicePeriod, currentDpr.getAccountId()));
            } catch (Exception e) {
                Log.e("ERR_GET_IMGS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            if (photosList != null) {
//                revealPhotoButton(false);
                for (int i = 0; i < photosList.size(); i++) {
                    File file = new File(photosList.get(i).getPhoto());
                    if (file.exists()) {
                        try {
                            Log.e("TEST", file.getPath());
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                            Bitmap scaledBmp = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 8, bitmap.getHeight() / 8, true);
                            ImageView imageView = new ImageView(ReadingFormNetMeteredActivity.this);
                            Constraints.LayoutParams layoutParams = new Constraints.LayoutParams(scaledBmp.getWidth(), scaledBmp.getHeight());
                            imageView.setLayoutParams(layoutParams);
                            imageView.setPadding(0, 5, 5, 0);
                            imageView.setImageBitmap(scaledBmp);
                            imageFields.addView(imageView);

                            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    PopupMenu popup = new PopupMenu(ReadingFormNetMeteredActivity.this, imageView);
                                    //inflating menu from xml resource
                                    popup.inflate(R.menu.image_menu);
                                    //adding click listener
                                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            switch (item.getItemId()) {
                                                case R.id.delete_img:
                                                    file.delete();
                                                    new GetPhotos().execute();
                                                    return true;
                                                default:
                                                    return false;
                                            }
                                        }
                                    });
                                    //displaying the popup
                                    popup.show();
                                    return false;
                                }
                            });
                        } catch (Exception e) {
                            Log.e("ERR_GET_PHOTOS", e.getMessage());
                            Toast.makeText(ReadingFormNetMeteredActivity.this, "An error occurred while fetching photos", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Log.e("ERR_RETRV_FILE", "Error retriveing file");
                    }
                }
            } else {
                revealPhotoButton(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new  File(currentPhotoPath);
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getPath());
            Bitmap scaledBmp = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/8, bitmap.getHeight()/8, true);

            ImageView imageView = new ImageView(ReadingFormNetMeteredActivity.this);
            Constraints.LayoutParams layoutParams = new Constraints.LayoutParams(scaledBmp.getWidth(), scaledBmp.getHeight());
            imageView.setLayoutParams(layoutParams);
            imageView.setPadding(0, 5, 5, 0);
            if (imgFile.exists()) {
                imageView.setImageBitmap(scaledBmp);
            }
            imageFields.addView(imageView);
            revealPhotoButton(false);

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popup = new PopupMenu(ReadingFormNetMeteredActivity.this, imageView);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.image_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete_img:
                                    if (imgFile.exists()) {
                                        imgFile.delete();
                                        new GetPhotos().execute();
                                    }
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();
                    return false;
                }
            });
        }
    }

    public void revealPhotoButton(boolean regex) {
        if (regex) {
            takePhotoButton.setVisibility(View.VISIBLE);
            billBtn.setVisibility(View.GONE);
            saveOnlyBtn.setVisibility(View.GONE);
        } else {
            takePhotoButton.setVisibility(View.GONE);
            billBtn.setVisibility(View.VISIBLE);
            saveOnlyBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * BT PRINTING
     */
    public void printViaEscPos(Bills bills, Rates rates, DownloadedPreviousReadings dpr, Readings readings) {
        try {
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, "No bluetooth adapter available", Toast.LENGTH_SHORT).show();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
//                    Intent enableBluetooth = new Intent(
//                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBluetooth, 0);
                    Toast.makeText(this, "Bluetooth is disabled!", Toast.LENGTH_SHORT).show();
                } else {
                    EscPosPrinter printer = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 28f, 48);
//                    Log.e("TEST", printer.getPrinterWidthMM() + " - " + printer.getPrinterDpi() + " - " + printer.getPrinterNbrCharactersPerLine());
                    printer.printFormattedText(
                            "[C]<font size='tall'>" + getResources().getString(R.string.company) + "</font>\n" +
                                    "[C]" + getResources().getString(R.string.companyAddress) + "\n" +
                                    "[C]" + getResources().getString(R.string.companyTin) + "\n" +
                                    "[C]<b>STATEMENT OF ACCOUNT</b>\n" +
                                    "[C]" + (readings != null ? ObjectHelpers.formatDetailedDate(readings.getReadingTimestamp()) : ObjectHelpers.formatDetailedDate(ObjectHelpers.getCurrentTimestamp())) + "\n" +
                                    "[C]<barcode type='128' height='10'>" + (dpr.getOldAccountNo() != null ? dpr.getOldAccountNo() : bills.getAccountNumber()) + "</barcode>\n" +
                                    "[L]\n" +
                                    "[L]<font size='tall'>Acct No: " + (dpr.getOldAccountNo() != null ? dpr.getOldAccountNo() : bills.getAccountNumber()) + "</font>\n" +
                                    "[L]<font size='tall'>" + dpr.getServiceAccountName() + "</font>\n" +
                                    "[L]" + (dpr.getPurok() + ", " + dpr.getBarangayFull() + ", " + dpr.getTownFull()) + "\n" +
                                    "[L]Bill Mo: " + ObjectHelpers.formatShortDate(bills.getServicePeriod()) + "[R]Mult: " + bills.getMultiplier() + "\n" +
                                    "[L]Meter No: " + (dpr.getMeterSerial() != null ? dpr.getMeterSerial() : '-') + "[R]Type: " + dpr.getAccountType() + "\n" +
                                    "[L]<b>--READING DATE--</b>[R]<b>--READING--</b>[R]<b>--KWH USED--</b>\n" +
                                    "[L]" + (bills.getServiceDateFrom() != null ? bills.getServiceDateFrom() : ObjectHelpers.getPreviousMonth(bills.getBillingDate())) + "[R] " + dpr.getKwhUsed() + "[R]<font size='tall'>" + bills.getKwhUsed() + "</font>\n" +
                                    "[L]" + ObjectHelpers.formatNumericDate(bills.getBillingDate()) + "[R]" + bills.getPresentKwh() + "[R]\n" +
                                    "[L]<b>DUE DATE: " + ObjectHelpers.formatShortDateWithDate(bills.getDueDate()) + "</b>[R]Rate: " + ObjectHelpers.roundFour(Double.valueOf(bills.getEffectiveRate())) + "\n" +
                                    "[L]<b>DISCO DATE: " + ObjectHelpers.formatShortDateWithDate(ObjectHelpers.getDisconnection(bills.getDueDate())) + "</b>\n" +
                                    "[L]<b>Arrears: " + (dpr.getArrearsTotal() != null ? ObjectHelpers.roundTwo(Double.valueOf(dpr.getArrearsTotal())) : "0") + "</b>\n" +
                                    "[L]Termed Payments: " + (dpr.getArrearsLedger() != null ? ObjectHelpers.roundTwo(Double.valueOf(dpr.getArrearsLedger())) : "0") + "[R]Unpaid Balance: " + (dpr.getBalance() != null ? ObjectHelpers.roundTwo(Double.valueOf(dpr.getBalance())) : "0") + "\n" +
                                    "[L]Meter Reader: " + (user != null ? user.getUsername() : '-') + "\n" +
                                    "[L]\n" +
                                    "[L]*[R]Rates[R]Amnt\n" +
                                    "[C]----------------------------------------\n" +
                                    "[L]GEN & TRANS REVENUES\n" +
                                    "[L]Generation Sys.[R]x[R]" + (rates.getGenerationSystemCharge() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getGenerationSystemCharge())) : "0.0000") + "/kWh[R]" + (bills.getGenerationSystemCharge() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getGenerationSystemCharge())) : "0.0") + "\n" +
                                    "[L]Trans. Demand [R]x[R]" + (rates.getTransmissionDeliveryChargeKW() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getTransmissionDeliveryChargeKW())) : "0.0000") + "/kWh[R]" + (bills.getTransmissionDeliveryChargeKW() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getTransmissionDeliveryChargeKW())) : "0.0") + "\n" +
                                    "[L]Trans. System [R]x[R]" + (rates.getTransmissionDeliveryChargeKWH() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getTransmissionDeliveryChargeKWH())) : "0.0000") + "/kWh[R]" + (bills.getTransmissionDeliveryChargeKWH() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getTransmissionDeliveryChargeKWH())) : "0.0") + "\n" +
                                    "[L]System Loss [R]x[R]" + (rates.getSystemLossCharge() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getSystemLossCharge())) : "0.0000") + "/kWh[R]" + (bills.getSystemLossCharge() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getSystemLossCharge())) : "0.0") + "\n" +
                                    "[L]OGA [R]x[R]" + (rates.getOtherGenerationRateAdjustment() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getOtherGenerationRateAdjustment())) : "0.0000") + "/kWh[R]" + (bills.getOtherGenerationRateAdjustment() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getOtherGenerationRateAdjustment())) : "0.0") + "\n" +
                                    "[L]OTCA Demand [R]x[R]" + (rates.getOtherTransmissionCostAdjustmentKW() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getOtherTransmissionCostAdjustmentKW())) : "0.0000") + "/kWh[R]" + (bills.getOtherTransmissionCostAdjustmentKW() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getOtherTransmissionCostAdjustmentKW())) : "0.0") + "\n" +
                                    "[L]OTCA System [R]x[R]" + (rates.getOtherTransmissionCostAdjustmentKWH() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getOtherTransmissionCostAdjustmentKWH())) : "0.0000") + "/kWh[R]" + (bills.getOtherTransmissionCostAdjustmentKWH() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getOtherTransmissionCostAdjustmentKWH())) : "0.0") + "\n" +
                                    "[L]OSLA [R]x[R]" + (rates.getOtherSystemLossCostAdjustment() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getOtherSystemLossCostAdjustment())) : "0.0000") + "/kWh[R]" + (bills.getOtherSystemLossCostAdjustment() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getOtherSystemLossCostAdjustment())) : "0.0") + "\n" +
                                    "[L][R][R]SUB-TOTAL [R]" + ObjectHelpers.roundTwo(ObjectHelpers.getTotalFromItems(bills.getGenerationSystemCharge(), bills.getTransmissionDeliveryChargeKW(), bills.getTransmissionDeliveryChargeKWH(), bills.getSystemLossCharge(), bills.getOtherGenerationRateAdjustment(), bills.getOtherTransmissionCostAdjustmentKW(), bills.getOtherTransmissionCostAdjustmentKWH(), bills.getOtherSystemLossCostAdjustment())) + "\n" +
                                    "[L]DISTRIBUTION REVENUES" + "\n" +
                                    "[L]Dist. Demand [R]x[R]" + (rates.getDistributionDemandCharge() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getDistributionDemandCharge())) : "0.0000") + "/kWh[R]" + (bills.getDistributionDemandCharge() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getDistributionDemandCharge())) : "0.0") + "\n" +
                                    "[L]Dist. System [R]x[R]" + (rates.getDistributionSystemCharge() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getDistributionSystemCharge())) : "0.0000") + "/kWh[R]" + (bills.getDistributionSystemCharge() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getDistributionSystemCharge())) : "0.0") + "\n" +
                                    "[L]Sup. Ret. Cust. [R]x[R]" + (rates.getSupplyRetailCustomerCharge() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getSupplyRetailCustomerCharge())) : "0.0000") + "/kWh[R]" + (bills.getSupplyRetailCustomerCharge() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getSupplyRetailCustomerCharge())) : "0.0") + "\n" +
                                    "[L]Supply System [R]x[R]" + (rates.getSupplySystemCharge() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getSupplySystemCharge())) : "0.0000") + "/kWh[R]" + (bills.getSupplySystemCharge() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getSupplySystemCharge())) : "0.0") + "\n" +
                                    "[L]Metering Ret. [R]x[R]" + (rates.getMeteringRetailCustomerCharge() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getMeteringRetailCustomerCharge())) : "0.0000") + "/kWh[R]" + (bills.getMeteringRetailCustomerCharge() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getMeteringRetailCustomerCharge())) : "0.0") + "\n" +
                                    "[L]Metering Sys. [R]x[R]" + (rates.getMeteringSystemCharge() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getMeteringSystemCharge())) : "0.0000") + "/kWh[R]" + (bills.getMeteringSystemCharge() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getMeteringSystemCharge())) : "0.0") + "\n" +
                                    "[L]RFSC [R]x[R]" + (rates.getRFSC() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getRFSC())) : "0.0") + "/kWh[R]" + (bills.getRFSC() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getRFSC())) : "0.0") + "\n" +
                                    "[L][R][R]SUB-TOTAL [R]" + ObjectHelpers.roundTwo(ObjectHelpers.getTotalFromItems(bills.getDistributionDemandCharge(), bills.getDistributionSystemCharge(), bills.getSupplyRetailCustomerCharge(), bills.getSupplySystemCharge(), bills.getMeteringRetailCustomerCharge(), bills.getMeteringSystemCharge(), bills.getRFSC())) + "\n" +
                                    "[L]UNIVERSAL CHARGES" + "\n" +
                                    "[L]Missionary Elec. [R]x[R]" + (rates.getMissionaryElectrificationCharge() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getMissionaryElectrificationCharge())) : "0.0000") + "/kWh[R]" + (bills.getMissionaryElectrificationCharge() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getMissionaryElectrificationCharge())) : "0.0") + "\n" +
                                    "[L]Environmental [R]x[R]" + (rates.getEnvironmentalCharge() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getEnvironmentalCharge())) : "0.0000") + "/kWh[R]" + (bills.getEnvironmentalCharge() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getEnvironmentalCharge())) : "0.0") + "\n" +
                                    "[L]Stranded Cont. [R]x[R]" + (rates.getStrandedContractCosts() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getStrandedContractCosts())) : "0.0000") + "/kWh[R]" + (bills.getStrandedContractCosts() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getStrandedContractCosts())) : "0.0") + "\n" +
                                    "[L]NPC Str. Debt [R]x[R]" + (rates.getNPCStrandedDebt() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getNPCStrandedDebt())) : "0.0000") + "/kWh[R]" + (bills.getNPCStrandedDebt() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getNPCStrandedDebt())) : "0.0") + "\n" +
                                    "[L]FIT All. [R]x[R]" + (rates.getFeedInTariffAllowance() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getFeedInTariffAllowance())) : "0.0000") + "/kWh[R]" + (bills.getFeedInTariffAllowance() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getFeedInTariffAllowance())) : "0.0") + "\n" +
                                    "[L]REDCI [R]x[R]" + (rates.getMissionaryElectrificationREDCI() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getMissionaryElectrificationREDCI())) : "0.0000") + "/kWh[R]" + (bills.getMissionaryElectrificationREDCI() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getMissionaryElectrificationREDCI())) : "0.0") + "\n" +
                                    "[L][R][R]SUB-TOTAL [R]" + ObjectHelpers.roundTwo(ObjectHelpers.getTotalFromItems(bills.getMissionaryElectrificationCharge(), bills.getEnvironmentalCharge(), bills.getStrandedContractCosts(), bills.getNPCStrandedDebt(), bills.getFeedInTariffAllowance(), bills.getMissionaryElectrificationREDCI())) + "\n" +
                                    "[L]OTHERS" + "\n" +
                                    "[L]Lifeline Rate [R]x[R]" + (rates.getLifelineRate() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getLifelineRate())) : "0.0000") + "/kWh[R]" + (bills.getLifelineRate() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getLifelineRate())) : "0.0") + "\n" +
                                    "[L]ICC Subsidy [R]x[R]" + (rates.getInterClassCrossSubsidyCharge() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getInterClassCrossSubsidyCharge())) : "0.0000") + "/kWh[R]" + (bills.getInterClassCrossSubsidyCharge() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getInterClassCrossSubsidyCharge())) : "0.0") + "\n" +
                                    "[L]PPA Refund [R]x[R]" + (rates.getPPARefund() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getPPARefund())) : "0.0000") + "/kWh[R]" + (bills.getPPARefund() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getPPARefund())) : "0.0") + "\n" +
                                    "[L]Senior Ctzn. Sub. [R]x[R]" + (rates.getSeniorCitizenSubsidy() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getSeniorCitizenSubsidy())) : "0.0000") + "/kWh[R]" + (bills.getSeniorCitizenSubsidy() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getSeniorCitizenSubsidy())) : "0.0") + "\n" +
                                    "[L]OLRA [R]x[R]" + (rates.getOtherLifelineRateCostAdjustment() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getOtherLifelineRateCostAdjustment())) : "0.0000") + "/kWh[R]" + (bills.getOtherLifelineRateCostAdjustment() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getOtherLifelineRateCostAdjustment())) : "0.0") + "\n" +
                                    "[L]SC Disc./Ajd. [R]x[R]" + (rates.getSeniorCitizenDiscountAndSubsidyAdjustment() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getSeniorCitizenDiscountAndSubsidyAdjustment())) : "0.0000") + "/kWh[R]" + (bills.getSeniorCitizenDiscountAndSubsidyAdjustment() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getSeniorCitizenDiscountAndSubsidyAdjustment())) : "0.0") + "\n" +
                                    "[L][R][R]SUB-TOTAL [R]" + ObjectHelpers.roundTwo(ObjectHelpers.getTotalFromItems(bills.getLifelineRate(), bills.getInterClassCrossSubsidyCharge(), bills.getPPARefund(), bills.getSeniorCitizenSubsidy(), bills.getOtherLifelineRateCostAdjustment(), bills.getSeniorCitizenDiscountAndSubsidyAdjustment())) + "\n" +
                                    "[L]TAXES" + "\n" +
                                    "[L]Generation VAT [R]x[R]" + (rates.getGenerationVAT() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getGenerationVAT())) : "0.0000") + "/kWh[R]" + (bills.getGenerationVAT() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getGenerationVAT())) : "0.0") + "\n" +
                                    "[L]Transmission VAT [R]x[R]" + (rates.getTransmissionVAT() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getTransmissionVAT())) : "0.0000") + "/kWh[R]" + (bills.getTransmissionVAT() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getTransmissionVAT())) : "0.0") + "\n" +
                                    "[L]Distribution VAT [R]x[R]" + (rates.getDistributionVAT() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getDistributionVAT())) : "0.0000") + "/kWh[R]" + (bills.getDistributionVAT() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getDistributionVAT())) : "0.0") + "\n" +
                                    "[L]System Loss VAT [R]x[R]" + (rates.getSystemLossVAT() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getSystemLossVAT())) : "0.0000") + "/kWh[R]" + (bills.getSystemLossVAT() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getSystemLossVAT())) : "0.0") + "\n" +
                                    "[L]Franchise Tax [R]x[R]" + (rates.getFranchiseTax() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getFranchiseTax())) : "0.0000") + "/kWh[R]" + (bills.getFranchiseTax() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getFranchiseTax())) : "0.0") + "\n" +
                                    "[L]Business Tax [R]x[R]" + (rates.getBusinessTax() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getBusinessTax())) : "0.0000") + "/kWh[R]" + (bills.getBusinessTax() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getBusinessTax())) : "0.0") + "\n" +
                                    "[L]RP Tax [R]" + (rates.getRealPropertyTax() != null ? ObjectHelpers.roundFour(Double.valueOf(rates.getRealPropertyTax())) : "0.0000") + "/kWh[R]" + (bills.getRealPropertyTax() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getRealPropertyTax())) : "0.0") + "\n" +
                                    "[L]Katas Ng VAT [R]-" + (bills.getKatasNgVat() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getKatasNgVat())) : "0.0") + "\n" +
                                    "[L][R][R]SUB-TOTAL [R]" + ObjectHelpers.roundTwo(ObjectHelpers.getTotalFromItems(bills.getGenerationVAT(), bills.getTransmissionVAT(), bills.getDistributionVAT(), bills.getSystemLossVAT(), bills.getFranchiseTax(), bills.getBusinessTax(), bills.getRealPropertyTax(), bills.getKatasNgVat())) + "\n" +
                                    "[L]2% [R]" + (bills.getEvat2Percent() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getEvat2Percent())) : "0.0") + "\n" +
                                    "[L]5% [R]" + (bills.getEvat5Percent() != null ? ObjectHelpers.roundTwo(Double.valueOf(bills.getEvat5Percent())) : "0.0") + "\n" +
                                    "[R]\n" +
                                    "[C]----------------------------------------\n" +
                                    "[C]Gross Amount Due\n" +
                                    "[C]<b>P " + ObjectHelpers.roundTwo(ObjectHelpers.sumDoubles(ObjectHelpers.validateDouble(bills.getNetAmount()), ObjectHelpers.validateDouble(bills.getEvat5Percent()), ObjectHelpers.validateDouble(bills.getEvat2Percent()))) + "</b>\n" +
                                    "[C]Net Amount Due\n" +
                                    "[C]<font size='big'><b>P " + ObjectHelpers.roundTwo(Double.valueOf(bills.getNetAmount())) + "</b></font>\n" +
                                    (ReadingHelpers.getAccountType(dpr).equals("RESIDENTIAL") ? "" : "[C]Penalty Charges after Due Date\n") +
                                    (ReadingHelpers.getAccountType(dpr).equals("RESIDENTIAL") ? "" : "[C]<b>P " + ReadingHelpers.getPenalty(bills) + "</b>\n") +
                                    (ReadingHelpers.getAccountType(dpr).equals("RESIDENTIAL") ? "" : "[C]Amount Due after Due Date\n") +
                                    (ReadingHelpers.getAccountType(dpr).equals("RESIDENTIAL") ? "" : "[C]<b>P " + ObjectHelpers.roundTwo(Double.valueOf(ReadingHelpers.getPenaltyNoComma(bills)) + Double.valueOf(bills.getNetAmount())) + "</b>\n") +
                                    "[R]\n" +
                                    "[C]NOTE: Please pay this bill within nine (9) days upon receipt hereof to avoid disconnection of your electric services.\n" +
                                    "[C]Rates are net of refund per ERC order 2012-018CF.\n" +
                                    "[C]*PLS PRESENT THIS STATEMENT UPON PAYMENT*\n" +
                                    "[L]\n" +
                                    "[C]----------------------------------------\n" +
                                    "[L]Date: " + ObjectHelpers.getCurrentDate() + " " + ObjectHelpers.getCurrentTime() + "\n" +
                                    "[L]\n" +
                                    "[L]<font size='tall'>Acct No: " + (dpr.getOldAccountNo() != null ? dpr.getOldAccountNo() : bills.getAccountNumber()) + "</font>\n" +
                                    "[L]<font size='tall'>" + dpr.getServiceAccountName() + "</font>\n" +
                                    "[C]<font size='big'><b>P " + ObjectHelpers.roundTwo(Double.valueOf(bills.getNetAmount())) + "</b></font>\n" +
                                    "[L]Bill Mo: " + ObjectHelpers.formatShortDate(bills.getServicePeriod()) + "[R]Mult: " + bills.getMultiplier() + "\n" +
                                    "[L]Meter No: " + (dpr.getMeterSerial() != null ? dpr.getMeterSerial() : '-') +
                                    "[L]KwH Used: " + bills.getKwhUsed() + "\n" +
                                    "[L]Due Date: " + ObjectHelpers.formatShortDateWithDate(bills.getDueDate()) + "\n" +
                                    "[L]Type: " + dpr.getAccountType() + "\n" +
                                    "[L]Meter Reader: " + (user != null ? user.getUsername() : '-') + "\n" +
                                    "[L]Arrears: " + (dpr.getArrearsTotal() != null ? ObjectHelpers.roundTwo(Double.valueOf(dpr.getArrearsTotal())) : "0") + "\n" +
                                    "[L]Termed Payments: " + (dpr.getArrearsLedger() != null ? ObjectHelpers.roundTwo(Double.valueOf(dpr.getArrearsLedger())) : "0") + "\n" +
                                    "[L]Unpaid Balance: " + (dpr.getBalance() != null ? ObjectHelpers.roundTwo(Double.valueOf(dpr.getBalance())) : "0") + "\n\n"
//                                            "[C]<qrcode size='20'>" + bills.getAccountNumber() + "-*-" + bills.getKwhUsed() + "</qrcode>\n" +
//                                            "[L]\n"
                    );

                    printer.disconnectPrinter();
                }
            }
        } catch (EscPosConnectionException e) {
            e.printStackTrace();
        } catch (EscPosBarcodeException e) {
            e.printStackTrace();
        } catch (EscPosEncodingException e) {
            e.printStackTrace();
        } catch (EscPosParserException e) {
            e.printStackTrace();
        }
    }
}