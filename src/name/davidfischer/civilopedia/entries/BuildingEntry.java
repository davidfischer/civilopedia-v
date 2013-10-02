package name.davidfischer.civilopedia.entries;

import java.io.IOException;
import java.util.ArrayList;

import name.davidfischer.civilopedia.helpers.CivilopediaDatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BuildingEntry extends CivilopediaEntry {
    private static final String TAG = BuildingEntry.class.getName();

    private static final String BUILDING_TABLE = "building";
    private static final String BUILDING_COL_ID = ID;
    private static final String BUILDING_COL_NAME = NAME;
    private static final String BUILDING_COL_CIVILOPEDIA = "civilopedia";
    private static final String BUILDING_COL_HELP = "help";
    private static final String BUILDING_COL_STRATEGY = "strategy";
    private static final String BUILDING_COL_COST = "cost";
    private static final String BUILDING_COL_FAITH_COST = "faith_cost";
    private static final String BUILDING_COL_MAINTENANCE = "maintenance";
    private static final String BUILDING_COL_TECHNOLOGY = "technology";
    private static final String BUILDING_COL_TYPE = "type";
    private static final String BUILDING_COL_SORT_ORDER = "sort_order";
    private static final String [] BUILDING_COLS = new String [] {
        BUILDING_COL_ID,
        BUILDING_COL_NAME,
        BUILDING_COL_CIVILOPEDIA,
        BUILDING_COL_HELP,
        BUILDING_COL_STRATEGY,
        BUILDING_COL_COST,
        BUILDING_COL_FAITH_COST,
        BUILDING_COL_MAINTENANCE,
        BUILDING_COL_TECHNOLOGY,
        BUILDING_COL_TYPE,
        BUILDING_COL_SORT_ORDER,
    };

    // Members
    private String mKey;
    private String mName;
    private String mGroup;
    private String mStrategy;
    private String mCivilopedia;
    private String mHelp;
    private int mCost;  // in "hammers"
    private int mFaithCost;
    private int mMaintenance;

    public BuildingEntry() {
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

    public int getFaithCost() {
        return mFaithCost;
    }

    public void setFaithCost(int faithCost) {
        this.mFaithCost = faithCost;
    }

    public int getMaintenance() {
        return mMaintenance;
    }

    public void setMaintenance(int maintenance) {
        this.mMaintenance = maintenance;
    }

    public static ArrayList<String> getGroups(Context context) {
        ArrayList<String> result = new ArrayList<String>();
        SQLiteDatabase conn = null;
        Cursor cursor = null;
        try {
            conn = new CivilopediaDatabaseHelper(context).openConnection();
            cursor = conn.query(BUILDING_TABLE, new String [] { BUILDING_COL_TYPE }, null, null, BUILDING_COL_TYPE,
                    null, BUILDING_COL_SORT_ORDER + "," + BUILDING_COL_NAME);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result.add(cursor.getString(0));
                cursor.moveToNext();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get building groups: " + e.getLocalizedMessage());
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
            cursor = conn.query(BUILDING_TABLE, BUILDING_COLS, null, null, null,
                    null, BUILDING_COL_COST + "," + BUILDING_COL_ID);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                BuildingEntry building = new BuildingEntry();
                building.setKey(cursor.getString(0));
                building.setName(cursor.getString(1));
                building.setCivilopedia(cursor.getString(2));
                building.setHelp(cursor.getString(3));
                building.setStrategy(cursor.getString(4));
                building.setCost(cursor.getInt(5));
                building.setFaithCost(cursor.getInt(6));
                building.setMaintenance(cursor.getInt(7));
                building.setGroup(cursor.getString(9));
                result.add(building);
                cursor.moveToNext();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get buildings: " + e.getLocalizedMessage());
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

    public static BuildingEntry getBuildingById(Context context, String id) {
        BuildingEntry building = new BuildingEntry();
        SQLiteDatabase conn = null;
        Cursor cursor = null;

        try {
            conn = new CivilopediaDatabaseHelper(context).openConnection();
            cursor = conn.query(BUILDING_TABLE, BUILDING_COLS,
                    BUILDING_COL_ID + "=?", new String [] { id },
                    null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                building.setKey(cursor.getString(0));
                building.setName(cursor.getString(1));
                building.setCivilopedia(cursor.getString(2));
                building.setHelp(cursor.getString(3));
                building.setStrategy(cursor.getString(4));
                building.setCost(cursor.getInt(5));
                building.setFaithCost(cursor.getInt(6));
                building.setMaintenance(cursor.getInt(7));
                building.setGroup(cursor.getString(9));
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get building (" + id + "): " + e.getLocalizedMessage());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
            if (null != conn) {
                conn.close();
            }
        }
        return building;
    }
}
