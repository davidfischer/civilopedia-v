package name.davidfischer.civilopedia.entries;

import java.io.IOException;
import java.util.ArrayList;

import name.davidfischer.civilopedia.helpers.CivilopediaDatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UnitEntry extends CivilopediaEntry {
    private static final String TAG = UnitEntry.class.getName();

    private static final String UNIT_TABLE = "unit";
    private static final String UNIT_COL_ID = ID;
    private static final String UNIT_COL_NAME = NAME;
    private static final String UNIT_COL_CIVILOPEDIA = "civilopedia";
    private static final String UNIT_COL_HELP = "help";
    private static final String UNIT_COL_STRATEGY = "strategy";
    private static final String UNIT_COL_COST = "cost";
    private static final String UNIT_COL_FAITH_COST = "faith_cost";
    private static final String UNIT_COL_COMBAT = "combat";
    private static final String UNIT_COL_RANGED_COMBAT = "ranged_combat";
    private static final String UNIT_COL_MOVES = "moves";
    private static final String UNIT_COL_RANGE = "range";
    private static final String UNIT_COL_TECHNOLOGY = "technology";
    private static final String UNIT_COL_TYPE = "type";
    private static final String [] UNIT_COLS = new String [] {
        UNIT_COL_ID,
        UNIT_COL_NAME,
        UNIT_COL_CIVILOPEDIA,
        UNIT_COL_HELP,
        UNIT_COL_STRATEGY,
        UNIT_COL_COST,
        UNIT_COL_FAITH_COST,
        UNIT_COL_COMBAT,
        UNIT_COL_RANGED_COMBAT,
        UNIT_COL_MOVES,
        UNIT_COL_RANGE,
        UNIT_COL_TECHNOLOGY,
        UNIT_COL_TYPE,
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
    private int mCombat;
    private int mRangedCombat;
    private int mMoves;
    private int mRange;

    public UnitEntry() {
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

    public int getCombat() {
        return mCombat;
    }

    public void setCombat(int combat) {
        this.mCombat = combat;
    }

    public int getRangedCombat() {
        return mRangedCombat;
    }

    public void setRangedCombat(int rangedCombat) {
        this.mRangedCombat = rangedCombat;
    }

    public int getMoves() {
        return mMoves;
    }

    public void setMoves(int moves) {
        this.mMoves = moves;
    }

    public int getRange() {
        return mRange;
    }

    public void setRange(int range) {
        this.mRange = range;
    }

    public static ArrayList<String> getGroups(Context context) {
        ArrayList<String> result = new ArrayList<String>();
        SQLiteDatabase conn = null;
        Cursor cursor = null;
        try {
            conn = new CivilopediaDatabaseHelper(context).openConnection();
            cursor = conn.query(UNIT_TABLE, new String [] { UNIT_COL_TYPE }, null, null, UNIT_COL_TYPE,
                    null, UNIT_COL_COST + "," + UNIT_COL_ID);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result.add(cursor.getString(0));
                cursor.moveToNext();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get unit groups: " + e.getLocalizedMessage());
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
            cursor = conn.query(UNIT_TABLE, UNIT_COLS, null, null, null,
                    null, UNIT_COL_NAME + "," + UNIT_COL_ID);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UnitEntry unit = new UnitEntry();
                unit.setKey(cursor.getString(0));
                unit.setName(cursor.getString(1));
                unit.setCivilopedia(cursor.getString(2));
                unit.setHelp(cursor.getString(3));
                unit.setStrategy(cursor.getString(4));
                unit.setCost(cursor.getInt(5));
                unit.setFaithCost(cursor.getInt(6));
                unit.setCombat(cursor.getInt(7));
                unit.setRangedCombat(cursor.getInt(8));
                unit.setMoves(cursor.getInt(9));
                unit.setRange(cursor.getInt(10));
                unit.setGroup(cursor.getString(12));
                result.add(unit);
                cursor.moveToNext();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get units: " + e.getLocalizedMessage());
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

    public static UnitEntry getUnitById(Context context, String id) {
        UnitEntry unit = new UnitEntry();
        SQLiteDatabase conn = null;
        Cursor cursor = null;

        try {
            conn = new CivilopediaDatabaseHelper(context).openConnection();
            cursor = conn.query(UNIT_TABLE, UNIT_COLS,
                        UNIT_COL_ID + "=?", new String [] { id },
                        null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                unit.setKey(cursor.getString(0));
                unit.setName(cursor.getString(1));
                unit.setCivilopedia(cursor.getString(2));
                unit.setHelp(cursor.getString(3));
                unit.setStrategy(cursor.getString(4));
                unit.setCost(cursor.getInt(5));
                unit.setFaithCost(cursor.getInt(6));
                unit.setCombat(cursor.getInt(7));
                unit.setRangedCombat(cursor.getInt(8));
                unit.setMoves(cursor.getInt(9));
                unit.setRange(cursor.getInt(10));
                unit.setGroup(cursor.getString(12));
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get unit (" + id + "): " + e.getLocalizedMessage());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
            if (null != conn) {
                conn.close();
            }
        }

        return unit;
    }
}
