package name.davidfischer.civilopedia.entries;

import java.io.IOException;
import java.util.ArrayList;

import name.davidfischer.civilopedia.R;
import name.davidfischer.civilopedia.helpers.CivilopediaDatabaseHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TechnologiesFragment extends CivilopediaFragment {
    private static final String TAG = TechnologiesFragment.class.getName();
    private static final String TYPE = "Technologies";
    private CivilopediaDatabaseHelper mDatabase = null;
    private ArrayList<String> mTechnologyNames = null;

    public TechnologiesFragment() {
        // Empty ctor for fragments required
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadTechnologies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        databaseConnect();
        return  inflater.inflate(R.layout.fragment_civilopedia_entry, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mDatabase) {
            mDatabase.close();
        }
    }

    private void databaseConnect() {
        if (null == mDatabase) {
            try {
                mDatabase = new CivilopediaDatabaseHelper(getActivity());
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }

    private void loadTechnologies() {
        databaseConnect();
        if (null == mTechnologyNames) {
            mTechnologyNames = mDatabase.getTechnologies();
        }
    }
}
