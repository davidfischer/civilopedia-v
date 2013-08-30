package name.davidfischer.civilopedia;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

public class CivilopediaActivity extends Activity {
    private String[] mCategoryTitles;
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;

    private static final String TAG = "Civilopedia";
    private static final String CATEGORY = "CATEGORY";
    private static final String CATEGORY_SUBITEM = "CATEGORY_SUBITEM";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mCategoryTitles = getResources().getStringArray(
                R.array.civ_categories_array);
        new ArrayList<HashMap<String, String>>();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ExpandableListView) findViewById(R.id.left_drawer);

        // Set the adapter for the expandable list view
        // This sets the various category texts into the left panel
        SimpleExpandableListAdapter expListAdapter =
                new SimpleExpandableListAdapter(
                        this,
                        createGroupList(mCategoryTitles),      // Creating group List.
                        R.layout.drawer_list_item,             // Group item layout XML.
                        new String[] { CATEGORY },             // the key of group item.
                        new int[] { R.id.category_text },      // ID of each group item.-Data under the key goes into this TextView.
                        createChildList(mCategoryTitles),      // childData describes second-level entries.
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
            this, /* host Activity */
            mDrawerLayout, /* DrawerLayout object */
            R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
            R.string.drawer_open, /* "open drawer" description for accessibility */
            R.string.drawer_close /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // If no item is selected (no app state) then open the drawer
        if (savedInstanceState == null) {
            setTitle(R.string.app_name);
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private ArrayList<HashMap<String, String>> createGroupList(String [] categories) {
        ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < categories.length; i += 1) {
            HashMap<String, String> group = new HashMap<String, String>();
            group.put("CATEGORY", categories[i]);
            result.add(group);
        }
        return result;
    }

    private ArrayList<ArrayList<HashMap<String, String>>> createChildList(
            String[] categories) {
        ArrayList<ArrayList<HashMap<String, String>>> result = new ArrayList<ArrayList<HashMap<String, String>>>();
        for (int i = 0; i < categories.length; ++i) {
            ArrayList<HashMap<String, String>> subList = new ArrayList<HashMap<String, String>>();
            for (int n = 0; n < 3; n++) {
                HashMap<String, String> child = new HashMap<String, String>();
                child.put(CATEGORY_SUBITEM, "Sub Item " + n);
                subList.add(child);
            }
            result.add(subList);
        }
        return result;
    }

    private class DrawerChildClickListener implements ExpandableListView.OnChildClickListener {
        /*@Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }*/

        @Override
        public boolean onChildClick(ExpandableListView parent, View v,
                int groupPosition, int childPosition, long id) {
            selectItem(groupPosition, childPosition);
            return true;
        }
    }

    //Swaps fragments in the main content view.
    private void selectItem(int groupPosition, int childPosition) {
        // update the main content by replacing fragments
        Fragment fragment = new CivilopediaFragment();
        Bundle args = new Bundle();
        args.putInt(CivilopediaFragment.ARG_CIVILOPEDIA_CATEGORY, groupPosition);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setSelectedChild(groupPosition, childPosition, true);
        setTitle(mCategoryTitles[groupPosition]);
        mDrawerLayout.closeDrawer(mDrawerList);
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

    /**
     * Fragment appears in the "content_frame"; shows a Civilopedia entry.
     */
    public static class CivilopediaFragment extends Fragment {
        public static final String ARG_CIVILOPEDIA_CATEGORY = "civilopedia_item";

        public CivilopediaFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_civilopedia_entry, container, false);
            int i = getArguments().getInt(ARG_CIVILOPEDIA_CATEGORY);
            String civilopediaCategory = getResources().getStringArray(R.array.civ_categories_array)[i];

            if (civilopediaCategory.equalsIgnoreCase("Technologies")) {
                
            } else if (civilopediaCategory.equalsIgnoreCase("Units")) {
                
            } else if (civilopediaCategory.equalsIgnoreCase("Buildings")) {
                
            } else if (civilopediaCategory.equalsIgnoreCase("Wonders")) {
                
            } else if (civilopediaCategory.equalsIgnoreCase("Social Policies")) {
                
            } else if (civilopediaCategory.equalsIgnoreCase("Civilizations")) {
                
            } else if (civilopediaCategory.equalsIgnoreCase("Religion")) {
                
            } else {
                Log.w(TAG, "Unknown Civilopedia category: " + civilopediaCategory);
            }

            getActivity().setTitle(civilopediaCategory);

            return rootView;
        }
    }
}
