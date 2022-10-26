package com.lopez.julz.readandbillhv.helpers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.core.app.ActivityCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ObjectHelpers {
    public static String dbName() {
        return "ReadAndBill";
    }

    public static String getCurrentTimestamp() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            return sdf.format(new Date());
        } catch (Exception e) {
            return null;
        }
    }

    public static String getCurrentTime() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            return sdf.format(new Date());
        } catch (Exception e) {
            return null;
        }
    }

    public static String getCurrentDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            return sdf.format(new Date());
        } catch (Exception e) {
            return null;
        }
    }

    public static String generateRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    public static String getTimeInMillis() {
        try {
            return new Date().getTime() + "";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatShortDateWithoutYear(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(date);
            sdf = new SimpleDateFormat("MMM");
            return sdf.format(d);
        } catch (Exception e) {
            Log.e("ERR_FORMAT_DATE", e.getMessage());
            return "";
        }
    }

    public static String formatShortDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(date);
            sdf = new SimpleDateFormat("MMM yyyy");
            return sdf.format(d);
        } catch (Exception e) {
            Log.e("ERR_FORMAT_DATE", e.getMessage());
            return "";
        }
    }

    public static String formatDetailedDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(date);
            sdf = new SimpleDateFormat("MMM dd, yyyy (EEE) hh:mm aa");
            return sdf.format(d);
        } catch (Exception e) {
            Log.e("ERR_FORMAT_DATE", e.getMessage());
            return "";
        }
    }

    public static String formatShortDateWithDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(date);
            sdf = new SimpleDateFormat("MMM dd, yyyy");
            return sdf.format(d);
        } catch (Exception e) {
            Log.e("ERR_FORMAT_DATE", e.getMessage());
            return "";
        }
    }

    public static String formatNumericDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(date);
            return sdf.format(d);
        } catch (Exception e) {
            Log.e("ERR_FORMAT_DATE", e.getMessage());
            return "";
        }
    }

    public static String formatSqlDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(date);
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(d);
        } catch (Exception e) {
            Log.e("ERR_FORMAT_DATE", e.getMessage());
            return "";
        }
    }

    public static String roundFour(Double doubleX) {
        try {
            DecimalFormat df = new DecimalFormat("#,###.0000");
            return df.format(doubleX);
        } catch (Exception e) {
            return "";
        }
    }

    public static String roundFour(String doubleX) {
        try {
            DecimalFormat df = new DecimalFormat("#,###,###.####");
            return df.format(doubleX);
        } catch (Exception e) {
            return "";
        }
    }

    public static String roundFourNoComma(Double doubleX) {
        try {
            DecimalFormat df = new DecimalFormat("#.####");
            return df.format(doubleX);
        } catch (Exception e) {
            return "";
        }
    }

    public static String roundTwo(Double doubleX) {
        try {
            DecimalFormat df = new DecimalFormat("#,###.00");
            return df.format(doubleX);
        } catch (Exception e) {
            return "";
        }
    }

    public static String roundTwoNoComma(Double doubleX) {
        try {
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(doubleX);
        } catch (Exception e) {
            return "";
        }
    }

    public static String roundTwo(String doubleX) {
        try {
            DecimalFormat df = new DecimalFormat("#,###,###.##");
            return df.format(doubleX);
        } catch (Exception e) {
            return "";
        }
    }

    public static Double doubleStringNull(String regex) {
        try {
            if (regex.equals(null)) {
                return 0.0;
            } else {
                return Double.valueOf(regex);
            }
        } catch (Exception e) {
            Log.e("ERR_DBL_STRNG_NLL", e.getMessage());
            return 0.0;
        }
    }

    public static String getSelectedTextFromRadioGroup(RadioGroup rg, View view) {
        try {
            int selectedId = rg.getCheckedRadioButtonId();
            RadioButton radioButton = (RadioButton) view.findViewById(selectedId);
            return radioButton.getText().toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String[] getPreviousMonths(int numberOfMonths) {
        try {
            String[] months = new String[numberOfMonths];

            for (int i=0; i<months.length; i++) {
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.MONTH, -i);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

                months[i] =  sdf.format(c.getTime()) + "-01";
            }

            return months;
        } catch (Exception e) {
            return new String[]{};
        }
    }

    public static String getPreviousMonth(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(d);
            c.add(Calendar.MONTH, -1);

            SimpleDateFormat sdfx = new SimpleDateFormat("yyyy-MM-dd");

            return sdfx.format(c.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    public static String getDisconnection(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(d);
            c.add(Calendar.DATE, +3);

            SimpleDateFormat sdfx = new SimpleDateFormat("yyyy-MM-dd");

            return sdfx.format(c.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    public static String generateIDandRandString() {
        return getTimeInMillis() + "-" + generateRandomString();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static double getTotalFromItems(String... items) {
        try {
            double total = 0.0;

            for(int i=0; i<items.length; i++) {
                if (items[i] != null) {
                    Double item = Double.parseDouble(items[i]);
                    total += item;
                }
            }

            return total;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static double validateDouble(String value) {
        try {
            if (value != null) {
                return Double.valueOf(value);
            } else {
                return 0;
            }
        } catch (Exception e) {
            Log.e("ERR_VAL_DBL", e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public static double sumDoubles(Double... values) {
        try {
            if (values != null) {
                int size = values.length;
                double sum = 0;
                for (int i=0; i<size; i++) {
                    sum += values[i];
                }
                return sum;
            } else {
                return 0;
            }
        } catch (Exception e) {
            Log.e("ER_SUM_DBL", e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public static double checkNull(EditText editText) {
        try {
            return Double.valueOf(editText.getText().toString());
        } catch (Exception e) {
            Log.e("ERR_GET_STRNG", e.getMessage());
            return 0;
        }
    }
}
