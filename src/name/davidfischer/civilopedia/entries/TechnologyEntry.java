package name.davidfischer.civilopedia.entries;

import java.io.IOException;
import java.util.ArrayList;

import name.davidfischer.civilopedia.helpers.CivilopediaDatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TechnologyEntry extends CivilopediaEntry {
    private static final String TAG = TechnologyEntry.class.getName();

    private static final String TECHNOLOGY_TABLE = "technology";
    private static final String TECHNOLOGY_COL_ID = ID;
    private static final String TECHNOLOGY_COL_NAME = NAME;
    private static final String TECHNOLOGY_COL_CIVILOPEDIA = "civilopedia";
    private static final String TECHNOLOGY_COL_HELP = "help";
    private static final String TECHNOLOGY_COL_QUOTE = "quote";
    private static final String TECHNOLOGY_COL_COST = "cost";
    private static final String TECHNOLOGY_COL_ERA = "era";
    private static final String [] TECHNOLOGY_COLS = new String [] {
        TECHNOLOGY_COL_ID,
        TECHNOLOGY_COL_NAME,
        TECHNOLOGY_COL_CIVILOPEDIA,
        TECHNOLOGY_COL_HELP,
        TECHNOLOGY_COL_QUOTE,
        TECHNOLOGY_COL_COST,
        TECHNOLOGY_COL_ERA,
    };

    // Members
    private String mKey;
    private String mName;
    private String mGroup;
    private String mQuote;
    private String mCivilopedia;
    private String mHelp;
    private int mCost;  // in "beakers"

    public TechnologyEntry() {
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

    public String getQuote() {
        return mQuote;
    }

    public void setQuote(String quote) {
        this.mQuote = quote;
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

    public static ArrayList<String> getGroups(Context context) {
        ArrayList<String> result = new ArrayList<String>();
        SQLiteDatabase conn = null;
        Cursor cursor = null;
        try {
            // Gets distinct eras sorted by cost
            conn = new CivilopediaDatabaseHelper(context).openConnection();
            cursor = conn.query(TECHNOLOGY_TABLE, new String [] { TECHNOLOGY_COL_ERA }, null, null,
                    TECHNOLOGY_COL_ERA, null, TECHNOLOGY_COL_COST + "," + TECHNOLOGY_COL_ID);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result.add(cursor.getString(0));
                cursor.moveToNext();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get technology groups: " + e.getLocalizedMessage());
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
            cursor = conn.query(TECHNOLOGY_TABLE, TECHNOLOGY_COLS, null, null,
                    null, null, TECHNOLOGY_COL_NAME + "," + TECHNOLOGY_COL_ID);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TechnologyEntry tech = new TechnologyEntry();
                tech.setKey(cursor.getString(0));
                tech.setName(cursor.getString(1));
                tech.setCivilopedia(cursor.getString(2));
                tech.setHelp(cursor.getString(3));
                tech.setQuote(cursor.getString(4));
                tech.setCost(cursor.getInt(5));
                tech.setGroup(cursor.getString(6));
                result.add(tech);
                cursor.moveToNext();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get technologies: " + e.getLocalizedMessage());
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

    public static TechnologyEntry getTechnologyById(Context context, String key) {
        TechnologyEntry tech = new TechnologyEntry();
        SQLiteDatabase conn = null;
        Cursor cursor = null;

        try {
            conn = new CivilopediaDatabaseHelper(context).openConnection();
            cursor = conn.query(TECHNOLOGY_TABLE, TECHNOLOGY_COLS,
                    TECHNOLOGY_COL_ID + "=?", new String [] { key },
                    null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                tech.setKey(cursor.getString(0));
                tech.setName(cursor.getString(1));
                tech.setCivilopedia(cursor.getString(2));
                tech.setHelp(cursor.getString(3));
                tech.setQuote(cursor.getString(4));
                tech.setCost(cursor.getInt(5));
                tech.setGroup(cursor.getString(6));
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get technology (" + key + "): " + e.getLocalizedMessage());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
            if (null != conn) {
                conn.close();
            }
        }
        return tech;
    }
}
