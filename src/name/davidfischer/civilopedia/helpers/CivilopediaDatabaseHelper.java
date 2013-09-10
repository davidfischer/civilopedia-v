package name.davidfischer.civilopedia.helpers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CivilopediaDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = CivilopediaDatabaseHelper.class.getName();
    private static final String DATABASE_NAME = "civilopedia.db";
    private static final int DATABASE_VERSION = 1;
    private static final int BUFFER_SIZE = 1024;

    private Context mContext;
    private String mDatabasePath;

    /**
     * Create a database connection helper and copy the database from assets
     *  if the database does not already exist.
     *
     * @param context the activity context for this database
     * @throws IOException if the database could not be copied
     */
    public CivilopediaDatabaseHelper(Context context) throws IOException {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
        this.mDatabasePath = context.getDatabasePath(DATABASE_NAME).getPath();

        if (this.upgradeRequired()) {
            Log.i(TAG, "Database upgrade required");
            this.copyDatabase();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Do nothing
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (upgradeRequired()) {
            Log.i(TAG, "Database upgrade required");
            try {
                copyDatabase();
            } catch (IOException e) {
                Log.e(TAG, "Failed onUpgrade: " + e.getLocalizedMessage());
            }
        }
    }

    private void copyDatabase() throws IOException {
        Log.i(TAG, "Copying database from assets");
        // Must be called before copying or else
        //  Android will not let the app overwrite the database
        getReadableDatabase();

        InputStream inputFile = mContext.getAssets().open(DATABASE_NAME);
        OutputStream outputFile = new FileOutputStream(mDatabasePath);

        // copy the database from the assets to the database location
        // be that internal memory or the SD card
        byte [] buffer = new byte[BUFFER_SIZE];
        int length;
        while ((length = inputFile.read(buffer)) > 0) {
            outputFile.write(buffer, 0, length);
        }

        //Close the streams
        outputFile.flush();
        outputFile.close();
        inputFile.close();
    }

    private boolean databaseExists() {
        SQLiteDatabase conn = null;

        try {
            conn = SQLiteDatabase.openDatabase(mDatabasePath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            // No connection
            Log.d(TAG, "This connection failure can be safely ignored");
        }

        if (null != conn) {
            conn.close();
            return true;
        }

        return false;
    }

    private boolean upgradeRequired() {
        InputStream existingDatabase = null;
        InputStream newDatabase = null;
        byte [] buffer1 = new byte[BUFFER_SIZE];
        byte [] buffer2 = new byte[BUFFER_SIZE];
        int charsRead1, charsRead2 = 0;

        if (!databaseExists()) {
            return true;
        }

        // Compare the two files byte by byte to ensure they are the same
        try {
            existingDatabase = new FileInputStream(mDatabasePath);
            newDatabase = mContext.getAssets().open(DATABASE_NAME);
            while (true) {
                charsRead1 = existingDatabase.read(buffer1);
                charsRead2 = newDatabase.read(buffer2);
                if (!Arrays.equals(buffer1, buffer2)) {
                    return true;
                } else if (charsRead1 == -1 && charsRead2 == -1) {
                    break;
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "Error reading database: " + e.getLocalizedMessage());
            return true;
        } finally {
            if (null != existingDatabase) {
                try {
                    existingDatabase.close();
                } catch (IOException e1) {
                    Log.w(TAG, e1.getLocalizedMessage());
                }
            }
            if (null != newDatabase) {
                try {
                    newDatabase.close();
                } catch (IOException e1) {
                    Log.w(TAG, e1.getLocalizedMessage());
                }
            }
        }

        return false;
    }

    public SQLiteDatabase openConnection() {
        return SQLiteDatabase.openDatabase(mDatabasePath, null, SQLiteDatabase.OPEN_READONLY);
    }
}
