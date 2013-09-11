package name.davidfischer.civilopedia;

import java.util.ArrayList;
import java.util.HashMap;

import name.davidfischer.civilopedia.entries.BuildingEntry;
import name.davidfischer.civilopedia.entries.TechnologyEntry;
import name.davidfischer.civilopedia.entries.UnitEntry;
import name.davidfischer.civilopedia.fragments.BuildingFragment;
import name.davidfischer.civilopedia.fragments.CivilopediaFragment;
import name.davidfischer.civilopedia.fragments.TechnologiesFragment;
import name.davidfischer.civilopedia.fragments.UnitFragment;
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
import android.widget.SimpleExpandableListAdapter;

public class CivilopediaActivity extends Activity {
    private static final String TAG = CivilopediaActivity.class.getName();
    public static final String CATEGORY = "CATEGORY";
    public static final String CATEGORY_SUBITEM = "CATEGORY_SUBITEM";

    // Must match strings.xml
    private static final String TECHNOLOGIES = "TECHNOLOGIES";
    private static final String UNITS = "UNITS";
    private static final String BUILDINGS = "BUILDINGS";

    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private String [] mCategoryNames;
    private ArrayList<HashMap<String, String>> mCategoryList;
    private ArrayList<ArrayList<HashMap<String, String>>> mSubcategoryList;

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

        // Set the adapter for the expandable list view
        // This sets the various category texts into the left panel
        SimpleExpandableListAdapter expListAdapter =
                new SimpleExpandableListAdapter(
                        this,
                        mCategoryList,                         // Creating group List.
                        R.layout.drawer_list_item,             // Group item layout XML.
                        new String[] { CATEGORY },             // the key of group item.
                        new int[] { R.id.category_text },      // ID of each group item.-Data under the key goes into this TextView.
                        mSubcategoryList,                      // childData describes second-level entries.
                        R.layout.drawer_sublist_item,          // Layout for sub-level entries(second level).
                        new String[] { CATEGORY_SUBITEM },     // Keys in childData maps to display.
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
            selectItem(0, 0);
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
        Log.v(TAG, String.format("Replacing main content frame with group, child = (%d, %d)", groupPosition, childPosition));

        // update the main content by replacing fragments
        CivilopediaFragment fragment = getFragmentFromCategoryName(mCategoryNames[groupPosition]);

        if (null != fragment) {
            Bundle args = new Bundle();
            args.putInt(CATEGORY_SUBITEM, childPosition);
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

    private ArrayList<ArrayList<HashMap<String, String>>> createSubcategoryList() {
        ArrayList<ArrayList<HashMap<String, String>>> result = new ArrayList<ArrayList<HashMap<String, String>>>();
        ArrayList<String> subcategories;

        for (int i = 0; i < mCategoryNames.length; i += 1) {
            if (mCategoryNames[i].equalsIgnoreCase(TECHNOLOGIES)) {
                subcategories = TechnologyEntry.getTechnologies(this);
            } else if (mCategoryNames[i].equalsIgnoreCase(UNITS)) {
                subcategories = UnitEntry.getUnits(this);
            } else if (mCategoryNames[i].equalsIgnoreCase(BUILDINGS)) {
                subcategories = BuildingEntry.getBuildings(this);
            } else {
                Log.w(TAG, "Unknown civilopedia category: " + mCategoryNames[i]);
                subcategories = new ArrayList<String>();
            }
            ArrayList<HashMap<String, String>> subList = new ArrayList<HashMap<String, String>>();
            for (int j = 0; j < subcategories.size(); j += 1) {
                HashMap<String, String> child = new HashMap<String, String>();
                child.put(CATEGORY_SUBITEM, subcategories.get(j));
                subList.add(child);
            }
            result.add(subList);
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
