package name.davidfischer.civilopedia;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CivilopediaActivity extends Activity {
    private String[] mCategoryTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;

    private static final String TAG = "Civilopedia";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mCategoryTitles = getResources().getStringArray(
                R.array.civ_categories_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mCategoryTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

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

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    //Swaps fragments in the main content view.
    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new CivilopediaFragment();
        Bundle args = new Bundle();
        args.putInt(CivilopediaFragment.ARG_CIVILOPEDIA_CATEGORY, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mCategoryTitles[position]);
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
