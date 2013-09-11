package name.davidfischer.civilopedia.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import name.davidfischer.civilopedia.CivilopediaActivity;
import name.davidfischer.civilopedia.R;
import name.davidfischer.civilopedia.entries.BuildingEntry;
import name.davidfischer.civilopedia.helpers.CivilopediaHtmlHelper;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class BuildingFragment extends CivilopediaFragment {
    private static final String TAG = BuildingFragment.class.getName();
    private static final String TYPE = "Buildings";
    private static final String BUILDING_TEMPLATE = "buildings.html";
    private ArrayList<String> mBuildingNames = null;

    public BuildingFragment() {
        // Empty ctor for fragments required
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadBuildings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        WebView layout = (WebView) inflater.inflate(R.layout.fragment_civilopedia_entry, container, false);

        int index = getArguments().getInt(CivilopediaActivity.CATEGORY_SUBITEM);
        String html = "";
        String buildingName = mBuildingNames.get(index);
        getActivity().setTitle(buildingName);
        BuildingEntry building = BuildingEntry.getBuildingByName(getActivity(), buildingName);

        AssetManager manager = getActivity().getAssets();
        try {
            InputStream stream = manager.open(BUILDING_TEMPLATE);
            byte [] buffer = new byte [stream.available()];
            stream.read(buffer);
            html = new String(buffer);
            stream.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to load building template: " + e.getLocalizedMessage());
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", building.getKey());
        params.put("name", building.getName());
        params.put("strategy", building.getStrategy());
        params.put("help", building.getHelp());
        params.put("civilopedia", building.getCivilopedia());
        params.put("cost", "" + building.getCost());
        params.put("faith_cost", "" + building.getFaithCost());
        params.put("maintenance", "" + building.getMaintenance());
        layout.loadDataWithBaseURL("file:///android_asset/", CivilopediaHtmlHelper.format(html, params), "text/html", "utf-8", "");

        return layout;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void loadBuildings() {
        if (null == mBuildingNames) {
            mBuildingNames = BuildingEntry.getBuildings(getActivity());
        }
    }
}
