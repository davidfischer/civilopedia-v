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
    private static final String WONDER_COL_ID = ID;
    private static final String WONDER_COL_NAME = NAME;
    private static final String WONDER_COL_CIVILOPEDIA = "civilopedia";
    private static final String WONDER_COL_HELP = "help";
    private static final String WONDER_COL_STRATEGY = "strategy";
    private static final String WONDER_COL_QUOTE = "quote";
    private static final String WONDER_COL_COST = "cost";
    private static final String WONDER_COL_TYPE = "type";
    private static final String [] WONDER_COLS = new String [] {
        WONDER_COL_ID,
        WONDER_COL_NAME,
        WONDER_COL_CIVILOPEDIA,
        WONDER_COL_HELP,
        WONDER_COL_STRATEGY,
        WONDER_COL_QUOTE,
        WONDER_COL_COST,
        WONDER_COL_TYPE,
    };

    // Members
    private String mKey;
    private String mName;
    private String mGroup;
    private String mStrategy;
    private String mQuote;
    private String mCivilopedia;
    private String mHelp;
    private int mCost;  // in "hammers"

    public WonderEntry() {
    }

    @Override
    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    @Override
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    @Override
    public String getGroup() {
        return mGroup;
    }

    public void setGroup(String group) {
        mGroup = group;
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

    public static ArrayList<String> getGroups(Context context) {
        ArrayList<String> result = new ArrayList<String>();
        SQLiteDatabase conn = null;
        Cursor cursor = null;
        try {
            conn = new CivilopediaDatabaseHelper(context).openConnection();
            cursor = conn.query(WONDER_TABLE, new String [] { WONDER_COL_TYPE }, null, null, WONDER_COL_TYPE,
                    null, WONDER_COL_COST + "," + WONDER_COL_ID);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result.add(cursor.getString(0));
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

    public static ArrayList<CivilopediaEntry> getEntries(Context context) {
        ArrayList<CivilopediaEntry> result = new ArrayList<CivilopediaEntry>();
        SQLiteDatabase conn = null;
        Cursor cursor = null;
        try {
            conn = new CivilopediaDatabaseHelper(context).openConnection();
            cursor = conn.query(WONDER_TABLE, WONDER_COLS, null, null, null,
                    null, WONDER_COL_NAME + "," + WONDER_COL_ID);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WonderEntry wonder = new WonderEntry();
                wonder.setKey(cursor.getString(0));
                wonder.setName(cursor.getString(1));
                wonder.setGroup("Wonders");
                wonder.setCivilopedia(cursor.getString(2));
                wonder.setHelp(cursor.getString(3));
                wonder.setStrategy(cursor.getString(4));
                wonder.setQuote(cursor.getString(5));
                wonder.setCost(cursor.getInt(6));
                wonder.setGroup(cursor.getString(7));
                result.add(wonder);
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

    public static WonderEntry getWonderById(Context context, String id) {
        WonderEntry wonder = new WonderEntry();
        SQLiteDatabase conn = null;
        Cursor cursor = null;

        try {
            conn = new CivilopediaDatabaseHelper(context).openConnection();
            cursor = conn.query(WONDER_TABLE, WONDER_COLS,
                    WONDER_COL_ID + "=?", new String [] { id },
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
                wonder.setGroup(cursor.getString(7));
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get wonder (" + id + "): " + e.getLocalizedMessage());
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
