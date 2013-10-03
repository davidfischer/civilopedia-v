package name.davidfischer.civilopedia.entries;

import java.io.IOException;
import java.util.ArrayList;

import name.davidfischer.civilopedia.helpers.CivilopediaDatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PolicyEntry extends CivilopediaEntry {
    private static final String TAG = PolicyEntry.class.getName();

    private static final String POLICY_TABLE = "policy";
    private static final String POLICY_COL_ID = ID;
    private static final String POLICY_COL_NAME = NAME;
    private static final String POLICY_COL_CIVILOPEDIA = "civilopedia";
    private static final String POLICY_COL_HELP = "help";
    private static final String POLICY_COL_TYPE = "type";
    private static final String POLICY_COL_SORT_ORDER = "sort_order";
    private static final String [] POLICY_COLS = new String [] {
        POLICY_COL_ID,
        POLICY_COL_NAME,
        POLICY_COL_CIVILOPEDIA,
        POLICY_COL_HELP,
        POLICY_COL_TYPE,
    };

    // Members
    private String mKey;
    private String mName;
    private String mGroup;
    private String mCivilopedia;
    private String mHelp;

    public PolicyEntry() {
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

    public String getHelp() {
        return mHelp;
    }

    public void setHelp(String help) {
        this.mHelp = help;
    }

    public static ArrayList<String> getGroups(Context context) {
        ArrayList<String> result = new ArrayList<String>();
        SQLiteDatabase conn = null;
        Cursor cursor = null;
        try {
            // Gets distinct types sorted by the sort order
            conn = new CivilopediaDatabaseHelper(context).openConnection();
            cursor = conn.query(POLICY_TABLE, new String [] { POLICY_COL_TYPE }, null, null,
                    POLICY_COL_TYPE, null, POLICY_COL_SORT_ORDER + "," + POLICY_COL_NAME);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result.add(cursor.getString(0));
                cursor.moveToNext();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get policy groups: " + e.getLocalizedMessage());
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
            cursor = conn.query(POLICY_TABLE, POLICY_COLS, null, null,
                    null, null, POLICY_COL_NAME + "," + POLICY_COL_ID);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                PolicyEntry policy = new PolicyEntry();
                policy.setKey(cursor.getString(0));
                policy.setName(cursor.getString(1));
                policy.setCivilopedia(cursor.getString(2));
                policy.setHelp(cursor.getString(3));
                policy.setGroup(cursor.getString(4));
                result.add(policy);
                cursor.moveToNext();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get policies: " + e.getLocalizedMessage());
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

    public static PolicyEntry getPolicyById(Context context, String key) {
        PolicyEntry policy = new PolicyEntry();
        SQLiteDatabase conn = null;
        Cursor cursor = null;

        try {
            conn = new CivilopediaDatabaseHelper(context).openConnection();
            cursor = conn.query(POLICY_TABLE, POLICY_COLS,
                    POLICY_COL_ID + "=?", new String [] { key },
                    null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                policy.setKey(cursor.getString(0));
                policy.setName(cursor.getString(1));
                policy.setCivilopedia(cursor.getString(2));
                policy.setHelp(cursor.getString(3));
                policy.setGroup(cursor.getString(4));
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get policy (" + key + "): " + e.getLocalizedMessage());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
            if (null != conn) {
                conn.close();
            }
        }
        return policy;
    }
}
