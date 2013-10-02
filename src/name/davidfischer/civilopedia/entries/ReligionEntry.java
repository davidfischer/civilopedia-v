package name.davidfischer.civilopedia.entries;

import java.io.IOException;
import java.util.ArrayList;

import name.davidfischer.civilopedia.helpers.CivilopediaDatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ReligionEntry extends CivilopediaEntry {
    private static final String TAG = ReligionEntry.class.getName();

    private static final String RELIGION_TABLE = "religion";
    private static final String RELIGION_COL_ID = ID;
    private static final String RELIGION_COL_NAME = NAME;
    private static final String RELIGION_COL_CIVILOPEDIA = "civilopedia";
    private static final String RELIGION_COL_TYPE = "type";
    private static final String RELIGION_COL_SORT_ORDER = "sort_order";
    private static final String [] RELIGION_COLS = new String [] {
        RELIGION_COL_ID,
        RELIGION_COL_NAME,
        RELIGION_COL_CIVILOPEDIA,
        RELIGION_COL_TYPE,
        RELIGION_COL_SORT_ORDER,
    };

    // Members
    private String mKey;
    private String mName;
    private String mGroup;
    private String mCivilopedia;

    public ReligionEntry() {
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

    public String getCivilopedia() {
        return mCivilopedia;
    }

    public void setCivilopedia(String civilopedia) {
        this.mCivilopedia = civilopedia;
    }

    public static ArrayList<String> getGroups(Context context) {
        ArrayList<String> result = new ArrayList<String>();
        SQLiteDatabase conn = null;
        Cursor cursor = null;
        try {
            // Gets distinct types sorted by cost
            conn = new CivilopediaDatabaseHelper(context).openConnection();
            cursor = conn.query(RELIGION_TABLE, new String [] { RELIGION_COL_TYPE }, null, null,
                    RELIGION_COL_TYPE, null, RELIGION_COL_SORT_ORDER + "," + RELIGION_COL_NAME);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result.add(cursor.getString(0));
                cursor.moveToNext();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get religion groups: " + e.getLocalizedMessage());
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
            cursor = conn.query(RELIGION_TABLE, RELIGION_COLS, null, null,
                    null, null, RELIGION_COL_NAME + "," + RELIGION_COL_ID);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ReligionEntry belief = new ReligionEntry();
                belief.setKey(cursor.getString(0));
                belief.setName(cursor.getString(1));
                belief.setCivilopedia(cursor.getString(2));
                belief.setGroup(cursor.getString(3));
                result.add(belief);
                cursor.moveToNext();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get beliefs: " + e.getLocalizedMessage());
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

    public static ReligionEntry getBeliefById(Context context, String key) {
        ReligionEntry belief = new ReligionEntry();
        SQLiteDatabase conn = null;
        Cursor cursor = null;

        try {
            conn = new CivilopediaDatabaseHelper(context).openConnection();
            cursor = conn.query(RELIGION_TABLE, RELIGION_COLS,
                    RELIGION_COL_ID + "=?", new String [] { key },
                    null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                belief.setKey(cursor.getString(0));
                belief.setName(cursor.getString(1));
                belief.setCivilopedia(cursor.getString(2));
                belief.setGroup(cursor.getString(3));
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get belief (" + key + "): " + e.getLocalizedMessage());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
            if (null != conn) {
                conn.close();
            }
        }
        return belief;
    }
}
