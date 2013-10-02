package name.davidfischer.civilopedia.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import name.davidfischer.civilopedia.R;
import name.davidfischer.civilopedia.entries.CivilopediaEntry;
import name.davidfischer.civilopedia.entries.WonderEntry;
import name.davidfischer.civilopedia.helpers.CivilopediaHtmlHelper;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class WonderFragment extends CivilopediaFragment {
    private static final String TAG = WonderFragment.class.getName();
    private static final String TYPE = "Wonders";
    private static final String WONDER_TEMPLATE = "wonders.html";

    public WonderFragment() {
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
        WonderEntry wonder = WonderEntry.getWonderById(getActivity(), id);
        getActivity().setTitle(wonder.getName());

        AssetManager manager = getActivity().getAssets();
        try {
            InputStream stream = manager.open(WONDER_TEMPLATE);
            byte [] buffer = new byte [stream.available()];
            stream.read(buffer);
            html = new String(buffer);
            stream.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to load wonder template: " + e.getLocalizedMessage());
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", wonder.getKey());
        params.put("name", wonder.getName());
        params.put("strategy", wonder.getStrategy());
        params.put("help", wonder.getHelp());
        params.put("civilopedia", wonder.getCivilopedia());
        params.put("quote", wonder.getQuote());
        params.put("cost", "" + wonder.getCost());
        layout.loadDataWithBaseURL("file:///android_asset/", CivilopediaHtmlHelper.format(html, params), "text/html", "utf-8", "");

        return layout;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
