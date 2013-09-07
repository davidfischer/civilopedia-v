package name.davidfischer.civilopedia.entries;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import name.davidfischer.civilopedia.CivilopediaActivity;
import name.davidfischer.civilopedia.R;
import name.davidfischer.civilopedia.helpers.CivilopediaDatabaseHelper;
import name.davidfischer.civilopedia.helpers.CivilopediaDatabaseHelper.TechnologyEntry;
import name.davidfischer.civilopedia.helpers.CivilopediaHtmlHelper;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class TechnologiesFragment extends CivilopediaFragment {
    private static final String TAG = TechnologiesFragment.class.getName();
    private static final String TYPE = "Technologies";
    private static final String TECHNOLOGY_TEMPLATE = "technologies.html";
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
        WebView layout = (WebView) inflater.inflate(R.layout.fragment_civilopedia_entry, container, false);

        int index = getArguments().getInt(CivilopediaActivity.CATEGORY_SUBITEM);
        String html = "";
        String techName = mTechnologyNames.get(index);
        getActivity().setTitle(techName);
        TechnologyEntry tech = mDatabase.getTechnologyByName(techName);

        AssetManager manager = getActivity().getAssets();
        try {
            InputStream stream = manager.open(TECHNOLOGY_TEMPLATE);
            byte [] buffer = new byte [stream.available()];
            stream.read(buffer);
            html = new String(buffer);
            stream.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to load technology template: " + e.getLocalizedMessage());
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", tech.getKey());
        params.put("name", tech.getName());
        params.put("quote", tech.getQuote());
        params.put("help", tech.getHelp());
        params.put("civilopedia", tech.getCivilopedia());
        layout.loadDataWithBaseURL("file:///android_asset/", CivilopediaHtmlHelper.format(html, params), "text/html", "utf-8", "");

        return layout;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mDatabase) {
            mDatabase.close();
        }
    }

    public CivilopediaDatabaseHelper databaseConnect() {
        if (null == mDatabase) {
            try {
                mDatabase = new CivilopediaDatabaseHelper(getActivity());
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
        return mDatabase;
    }

    private void loadTechnologies() {
        databaseConnect();
        if (null == mTechnologyNames) {
            mTechnologyNames = mDatabase.getTechnologies();
        }
    }
}
