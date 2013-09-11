package name.davidfischer.civilopedia.entries;

import java.io.IOException;
import java.util.ArrayList;

import name.davidfischer.civilopedia.helpers.CivilopediaDatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WonderEntry extends CivilopediaEntry {
    private static final String TAG = WonderEntry.class.getName();

    private static final String WONDER_TABLE = "wonder";
    private static final String WONDER_COL_ID = "_id";
    private static final String WONDER_COL_NAME = "name";
    private static final String WONDER_COL_CIVILOPEDIA = "civilopedia";
    private static final String WONDER_COL_HELP = "help";
    private static final String WONDER_COL_STRATEGY = "strategy";
    private static final String WONDER_COL_QUOTE = "quote";
    private static final String WONDER_COL_COST = "cost";
    private static final String [] WONDER_COLS = new String [] {
        WONDER_COL_ID,
        WONDER_COL_NAME,
        WONDER_COL_CIVILOPEDIA,
        WONDER_COL_HELP,
        WONDER_COL_STRATEGY,
        WONDER_COL_QUOTE,
        WONDER_COL_COST,
    };

    // Members
    private String mKey;
    private String mName;
    private String mStrategy;
    private String mQuote;
    private String mCivilopedia;
    private String mHelp;
    private int mCost;  // in "hammers"

    public WonderEntry() {
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getStrategy() {
        return mStrategy;
    }

    public void setStrategy(String strategy) {
        this.mStrategy = strategy;
    }

    public String getCivilopedia() {
        return mCivilopedia;
    }

    public void setCivilopedia(String civilopedia) {
        this.mCivilopedia = civilopedia;
    }

    public String getHelp() {
        return mHelp;
    }

    public void setHelp(String help) {
        this.mHelp = help;
    }

    public int getCost() {
        return mCost;
    }

    public void setCost(int cost) {
        this.mCost = cost;
    }

    public String getQuote() {
        return mQuote;
    }

    public void setQuote(String quote) {
        this.mQuote = quote;
    }

    public static ArrayList<String> getWonders(Context context) {
        ArrayList<String> result = new ArrayList<String>();
        SQLiteDatabase conn = null;
        Cursor cursor = null;
        try {
            conn = new CivilopediaDatabaseHelper(context).openConnection();
            cursor = conn.query(WONDER_TABLE,
                    new String [] { WONDER_COL_NAME }, null, null, null,
                    null, WONDER_COL_COST + "," + WONDER_COL_ID);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String a = cursor.getString(0);
                result.add(a);
                cursor.moveToNext();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get wonders: " + e.getLocalizedMessage());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
            if (null != conn) {
                conn.close();
            }
        }

        return result;
    }

    public static WonderEntry getWonderByName(Context context, String wonderName) {
        WonderEntry wonder = new WonderEntry();
        SQLiteDatabase conn = null;
        Cursor cursor = null;

        try {
            conn = new CivilopediaDatabaseHelper(context).openConnection();
            cursor = conn.query(WONDER_TABLE, WONDER_COLS,
                    WONDER_COL_NAME + "=?", new String [] { wonderName },
                    null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                wonder.setKey(cursor.getString(0));
                wonder.setName(cursor.getString(1));
                wonder.setCivilopedia(cursor.getString(2));
                wonder.setHelp(cursor.getString(3));
                wonder.setStrategy(cursor.getString(4));
                wonder.setQuote(cursor.getString(5));
                wonder.setCost(cursor.getInt(6));
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get wonder (" + wonderName + "): " + e.getLocalizedMessage());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
            if (null != conn) {
                conn.close();
            }
        }
        return wonder;
    }
}
