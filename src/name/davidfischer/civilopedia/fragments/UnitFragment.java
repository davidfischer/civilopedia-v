package name.davidfischer.civilopedia.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import name.davidfischer.civilopedia.R;
import name.davidfischer.civilopedia.entries.CivilopediaEntry;
import name.davidfischer.civilopedia.entries.UnitEntry;
import name.davidfischer.civilopedia.helpers.CivilopediaDatabaseHelper;
import name.davidfischer.civilopedia.helpers.CivilopediaHtmlHelper;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class UnitFragment extends CivilopediaFragment {
    private static final String TAG = UnitFragment.class.getName();
    private static final String TYPE = "Units";
    private static final String UNIT_TEMPLATE = "units.html";
    private CivilopediaDatabaseHelper mDatabase = null;

    public UnitFragment() {
        // Empty ctor for fragments required
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        WebView layout = (WebView) inflater.inflate(R.layout.fragment_civilopedia_entry, container, false);

        String id = getArguments().getString(CivilopediaEntry.ID);
        String html = "";
        UnitEntry unit = UnitEntry.getUnitById(getActivity(), id);
        getActivity().setTitle(unit.getName());

        AssetManager manager = getActivity().getAssets();
        try {
            InputStream stream = manager.open(UNIT_TEMPLATE);
            byte [] buffer = new byte [stream.available()];
            stream.read(buffer);
            html = new String(buffer);
            stream.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to load unit template: " + e.getLocalizedMessage());
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", unit.getKey());
        params.put("name", unit.getName());
        params.put("strategy", unit.getStrategy());
        params.put("help", unit.getHelp());
        params.put("civilopedia", unit.getCivilopedia());
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
}
