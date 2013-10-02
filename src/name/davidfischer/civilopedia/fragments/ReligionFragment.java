package name.davidfischer.civilopedia.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import name.davidfischer.civilopedia.R;
import name.davidfischer.civilopedia.entries.CivilopediaEntry;
import name.davidfischer.civilopedia.entries.ReligionEntry;
import name.davidfischer.civilopedia.helpers.CivilopediaHtmlHelper;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class ReligionFragment extends CivilopediaFragment {
    private static final String TAG = ReligionFragment.class.getName();
    private static final String TYPE = "Religion";
    private static final String RELIGION_TEMPLATE = "religion.html";

    public ReligionFragment() {
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

        String key = getArguments().getString(CivilopediaEntry.ID);
        String html = "";

        ReligionEntry belief = ReligionEntry.getBeliefById(getActivity(), key);
        getActivity().setTitle(belief.getName());

        AssetManager manager = getActivity().getAssets();
        try {
            InputStream stream = manager.open(RELIGION_TEMPLATE);
            byte [] buffer = new byte [stream.available()];
            stream.read(buffer);
            html = new String(buffer);
            stream.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to load religion template: " + e.getLocalizedMessage());
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", belief.getKey());
        params.put("name", belief.getName());
        params.put("civilopedia", belief.getCivilopedia());
        layout.loadDataWithBaseURL("file:///android_asset/", CivilopediaHtmlHelper.format(html, params), "text/html", "utf-8", "");

        return layout;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
