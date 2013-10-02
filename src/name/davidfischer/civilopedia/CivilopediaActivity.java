package name.davidfischer.civilopedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.davidfischer.civilopedia.entries.BuildingEntry;
import name.davidfischer.civilopedia.entries.CivilopediaEntry;
import name.davidfischer.civilopedia.entries.ReligionEntry;
import name.davidfischer.civilopedia.entries.TechnologyEntry;
import name.davidfischer.civilopedia.entries.UnitEntry;
import name.davidfischer.civilopedia.entries.WonderEntry;
import name.davidfischer.civilopedia.fragments.BuildingFragment;
import name.davidfischer.civilopedia.fragments.CivilopediaFragment;
import name.davidfischer.civilopedia.fragments.ReligionFragment;
import name.davidfischer.civilopedia.fragments.TechnologiesFragment;
import name.davidfischer.civilopedia.fragments.UnitFragment;
import name.davidfischer.civilopedia.fragments.WonderFragment;
import name.davidfischer.civilopedia.ui.SubheadingExpandableListAdapter;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

public class CivilopediaActivity extends Activity {
    private static final String TAG = CivilopediaActivity.class.getName();
    public static final String CATEGORY = "CATEGORY";
    public static final String CATEGORY_SUBHEADING = "CATEGORY_SUBHEADING";

    // Must match strings.xml
    private static final String TECHNOLOGIES = "TECHNOLOGIES";
    private static final String UNITS = "UNITS";
    private static final String BUILDINGS = "BUILDINGS";
    private static final String WONDERS = "WONDERS";
    private static final String RELIGION = "RELIGION";

    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private String [] mCategoryNames;
    private List<? extends Map<String, ?>> mCategoryList;
    private List<? extends List<? extends List<? extends Map<String, ?>>>> mSubcategoryList;
    private List<? extends List<? extends Map<String, ?>>> mSubheadingList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mCategoryNames = getResources().getStringArray(R.array.civ_categories);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ExpandableListView) findViewById(R.id.left_drawer);
        mCategoryList = createCategoryList();
        mSubcategoryList = createSubcategoryList();
        mSubheadingList = createSubheadingList();

        // Set the adapter for the expandable list view
        // This sets the various category texts into the left panel
        SubheadingExpandableListAdapter expListAdapter =
                new SubheadingExpandableListAdapter(
                        this,
                        mCategoryList,                         // Creating group List.
                        R.layout.drawer_list_item,             // Group item layout XML.
                        new String[] { CATEGORY },             // the key of group item.
                        new int[] { R.id.category_text },      // ID of each group item.-Data under the key goes into this TextView.
                        mSubheadingList,
                        R.layout.drawer_sublist_heading,
                        new String[] { CATEGORY_SUBHEADING },
                        new int[] { R.id.category_child_heading },
                        mSubcategoryList,                      // childData describes second-level entries.
                        R.layout.drawer_sublist_item,          // Layout for sub-level entries(second level).
                        new String[] { CivilopediaEntry.NAME },     // Keys in childData maps to display.
                        new int[] { R.id.category_child}       // Data under the keys above go into these TextViews.
                    );
        mDrawerList.setAdapter(expListAdapter);

        // Set the list's click listener
        mDrawerList.setOnChildClickListener(new DrawerChildClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
            this,                  // host Activity
            mDrawerLayout,         // DrawerLayout object
            R.drawable.ic_drawer,  // nav drawer image to replace 'Up' caret
            R.string.drawer_open,  // "open drawer" description for accessibility
            R.string.drawer_close  // "close drawer" description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();  // calls onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();  // calls onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // If no item is selected (no app state) then open the drawer
        if (null == savedInstanceState) {
            setTitle(R.string.app_name);
            selectItem(0, 1);
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private class DrawerChildClickListener implements ExpandableListView.OnChildClickListener {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v,
                int groupPosition, int childPosition, long id) {
            selectItem(groupPosition, childPosition);
            return true;
        }
    }

    //Swaps fragments in the main content view.
    private void selectItem(int groupPosition, int childPosition) {
        SubheadingExpandableListAdapter expListAdapter = (SubheadingExpandableListAdapter) mDrawerList.getExpandableListAdapter();
        String key = expListAdapter.getChildKey(groupPosition, childPosition);

        Log.v(TAG, String.format("Replacing main content frame with group, child = (%d, %d), key = %s", groupPosition, childPosition, key));

        // update the main content by replacing fragments
        CivilopediaFragment fragment = getFragmentFromCategoryName(mCategoryNames[groupPosition]);

        if (null != fragment) {
            Bundle args = new Bundle();
            args.putString(CivilopediaEntry.ID, key);
            fragment.setArguments(args);

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack (enable the back button)
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_frame, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setSelectedChild(groupPosition, childPosition, true);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            Log.w(TAG, String.format("Failed to instantiate fragment (%d, %d)", groupPosition, childPosition));
        }
    }

    private CivilopediaFragment getFragmentFromCategoryName(String name) {
        CivilopediaFragment fragment = null;

        if (name.equalsIgnoreCase(TECHNOLOGIES)) {
            fragment = new TechnologiesFragment();
        } else if (name.equalsIgnoreCase(UNITS)) {
            fragment = new UnitFragment();
        } else if (name.equalsIgnoreCase(BUILDINGS)) {
            fragment = new BuildingFragment();
        } else if (name.equalsIgnoreCase(WONDERS)) {
            fragment = new WonderFragment();
        } else if (name.equalsIgnoreCase(RELIGION)) {
            fragment = new ReligionFragment();
        }

        return fragment;
    }

    private ArrayList<HashMap<String, String>> createCategoryList() {
        ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < mCategoryNames.length; i += 1) {
            HashMap<String, String> group = new HashMap<String, String>();
            group.put(CATEGORY, mCategoryNames[i]);
            result.add(group);
        }
        return result;
    }

    private ArrayList<ArrayList<HashMap<String, String>>> createSubheadingList() {
        ArrayList<ArrayList<HashMap<String, String>>> result = new ArrayList<ArrayList<HashMap<String, String>>>();
        ArrayList<String> sectionNames;

        for (int i = 0; i < mCategoryNames.length; i += 1) {
            if (mCategoryNames[i].equalsIgnoreCase(TECHNOLOGIES)) {
                sectionNames = TechnologyEntry.getGroups(this);
            } else if (mCategoryNames[i].equalsIgnoreCase(UNITS)) {
                sectionNames = UnitEntry.getGroups(this);
            } else if (mCategoryNames[i].equalsIgnoreCase(BUILDINGS)) {
                sectionNames = BuildingEntry.getGroups(this);
            } else if (mCategoryNames[i].equalsIgnoreCase(WONDERS)) {
                sectionNames = WonderEntry.getGroups(this);
            } else if (mCategoryNames[i].equalsIgnoreCase(RELIGION)) {
                sectionNames = ReligionEntry.getGroups(this);
            } else {
                Log.w(TAG, "Unknown civilopedia category: " + mCategoryNames[i]);
                sectionNames = new ArrayList<String>();
            }
            ArrayList<HashMap<String, String>> arr = new ArrayList<HashMap<String, String>>();
            for (int j = 0; j < sectionNames.size(); j += 1) {
                HashMap<String, String> group = new HashMap<String, String>();
                group.put(CATEGORY_SUBHEADING, sectionNames.get(j));
                arr.add(group);
            }
            result.add(arr);
        }
        return result;
    }

    /**
     * [
     *   [
     *     [
     *       // individual entries here (eg. compass)
     *     ]
     *   ]  // sections (eg. ancient era)
     * ]  // categories  (eg. technologies)
     */
    private ArrayList<ArrayList<ArrayList<HashMap<String, String>>>> createSubcategoryList() {
        ArrayList<ArrayList<ArrayList<HashMap<String, String>>>> result = new ArrayList<ArrayList<ArrayList<HashMap<String, String>>>>();
        ArrayList<CivilopediaEntry> subcategories;
        ArrayList<String> sectionNames;

        for (int i = 0; i < mCategoryNames.length; i += 1) {
            if (mCategoryNames[i].equalsIgnoreCase(TECHNOLOGIES)) {
                sectionNames = TechnologyEntry.getGroups(this);
                subcategories = TechnologyEntry.getEntries(this);
            } else if (mCategoryNames[i].equalsIgnoreCase(UNITS)) {
                sectionNames = UnitEntry.getGroups(this);
                subcategories = UnitEntry.getEntries(this);
            } else if (mCategoryNames[i].equalsIgnoreCase(BUILDINGS)) {
                sectionNames = BuildingEntry.getGroups(this);
                subcategories = BuildingEntry.getEntries(this);
            } else if (mCategoryNames[i].equalsIgnoreCase(WONDERS)) {
                sectionNames = WonderEntry.getGroups(this);
                subcategories = WonderEntry.getEntries(this);
            } else if (mCategoryNames[i].equalsIgnoreCase(RELIGION)) {
                sectionNames = ReligionEntry.getGroups(this);
                subcategories = ReligionEntry.getEntries(this);
            } else {
                Log.w(TAG, "Unknown civilopedia category: " + mCategoryNames[i]);
                subcategories = new ArrayList<CivilopediaEntry>();
                sectionNames = new ArrayList<String>();
            }

            // Put the entries into the appropriate subsection
            ArrayList<ArrayList<HashMap<String, String>>> section = new ArrayList<ArrayList<HashMap<String, String>>>();
            for (int j = 0; j < sectionNames.size(); j += 1) {
                ArrayList<HashMap<String, String>> subList = new ArrayList<HashMap<String, String>>();
                for (int k = 0; k < subcategories.size(); k += 1) {
                    if (subcategories.get(k).getGroup().equals(sectionNames.get(j))) {
                        HashMap<String, String> child = new HashMap<String, String>();
                        child.put(CivilopediaEntry.ID, subcategories.get(k).getKey());
                        child.put(CivilopediaEntry.NAME, subcategories.get(k).getName());
                        subList.add(child);
                    }
                }
                section.add(subList);
            }
            result.add(section);
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.menu_about:
                showAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    /**
     * Switch to AboutActivity via intent.
     */
    public void showAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}
