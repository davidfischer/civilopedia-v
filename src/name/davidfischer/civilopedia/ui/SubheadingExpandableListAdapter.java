package name.davidfischer.civilopedia.ui;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class SubheadingExpandableListAdapter extends BaseExpandableListAdapter {

    public static final String ITEM_KEY = "_id";

    private static final String TAG = SubheadingExpandableListAdapter.class.getName();

    private static final int NUM_CHILD_TYPES = 2;
    private static final int SUBHEADING_TYPE = 0;
    private static final int SUBLISTING_TYPE = 1;

    private List<? extends Map<String, ?>> mGroupData;
    private int mGroupLayout;
    private String[] mGroupFrom;
    private int[] mGroupTo;
    private List<? extends List<? extends Map<String, ?>>> mSubheadings;
    private int mSubheadingLayout;
    private String[] mSubheadingFrom;
    private int[] mSubheadingTo;
    private List<? extends List<? extends List<? extends Map<String, ?>>>> mSublistingData;
    private int mSublistingLayout;
    private String[] mSublistingFrom;
    private int[] mSublistingTo;

    private LayoutInflater mInflater;

    public SubheadingExpandableListAdapter(Context context,
            List<? extends Map<String, ?>> groupData, int groupLayout,
            String[] groupFrom, int[] groupTo,
            List<? extends List<? extends Map<String, ?>>> subheadings, int subheadingLayout,
            String[] subheadingFrom, int[] subheadingTo,
            List<? extends List<? extends List<? extends Map<String, ?>>>> sublistingData,
            int sublistingLayout, String[] sublistingFrom, int[] sublistingTo) {
        mGroupData = groupData;
        mGroupLayout = groupLayout;
        mGroupFrom = groupFrom;
        mGroupTo = groupTo;
        mSubheadings = subheadings;
        mSubheadingLayout = subheadingLayout;
        mSubheadingFrom = subheadingFrom;
        mSubheadingTo = subheadingTo;
        mSublistingData = sublistingData;
        mSublistingLayout = sublistingLayout;
        mSublistingFrom = sublistingFrom;
        mSublistingTo = sublistingTo;

        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Verify that the passed in data is the correct sizes
        if (groupData.size() != subheadings.size() || groupData.size() != sublistingData.size()) {
            Log.w(TAG, String.format("Incorrect sizes of lists. len(groups) = %d, len(subheadings) = %d, len(sublistings) = %d", groupData.size(), subheadings.size(), sublistingData.size()));
        } else {
            for (int i = 0; i < groupData.size(); i += 1) {
                if (subheadings.get(i).size() != sublistingData.get(i).size()) {
                    Log.w(TAG, String.format("Incorrect size of sublist for group (%d). len(subheadings.get(%d)) = %d, len(sublistings.get(%d)) = %d", i, i, subheadings.get(i).size(), i, sublistingData.get(i).size()));
                }
            }
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        int index = 0, sublistingCount;
        for (int i = 0; i < mSubheadings.get(groupPosition).size(); i += 1) {

            if (index == childPosition) {
                return mSubheadings.get(groupPosition).get(i);
            }
            index += 1;
            sublistingCount = mSublistingData.get(groupPosition).get(i).size();
            if (childPosition - index < sublistingCount) {
                return mSublistingData.get(groupPosition).get(i).get(childPosition - index);
            } else {
                index += sublistingCount;
            }
        }

        Log.w(TAG, String.format("getChild returned null for (%d, %d)", groupPosition, childPosition));
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Returns either a sublisting view or a subheading view.
     */
    @SuppressWarnings("unchecked")
    @Override
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        View v;
        boolean isSublisting = getChildType(groupPosition, childPosition) == SUBLISTING_TYPE;

        if (null == convertView) {
            if (isSublisting) {
                v = mInflater.inflate(mSublistingLayout, parent, false);
            } else {
                v = mInflater.inflate(mSubheadingLayout, parent, false);
            }
        } else {
            v = convertView;
        }

        if (isSublisting) {
            bindView(v,
                    (Map<String, ?>) getChild(groupPosition, childPosition),
                    mSublistingFrom, mSublistingTo);
        } else {
            bindView(v, (Map<String, ?>) getChild(groupPosition, childPosition),
                    mSubheadingFrom, mSubheadingTo);
        }

        return v;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int count = 0;

        for (List<? extends Map<String, ?>> sublistings : mSublistingData.get(groupPosition)) {
            count += 1; // add one for the section
            count += sublistings.size();
        }
        return count;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupData.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mGroupData.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        View v;

        if (null == convertView) {
            v = mInflater.inflate(mGroupLayout, parent, false);
        } else {
            v = convertView;
        }
        bindView(v, mGroupData.get(groupPosition), mGroupFrom, mGroupTo);
        return v;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * Sublistings are selectable. Subheadings are not.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        if (getChildType(groupPosition, childPosition) == SUBLISTING_TYPE) {
            return true;
        }
        return false;
    }

    /**
     * Returns a either the sublisting type or the subheading type.
     */
    @Override
    public int getChildType(int groupPosition, int childPosition) {
        int index = 0, sublistingCount;
        for (int i = 0; i < mSubheadings.get(groupPosition).size(); i += 1) {

            if (index == childPosition) {
                return SUBHEADING_TYPE;
            }
            index += 1;
            sublistingCount = mSublistingData.get(groupPosition).get(i).size();
            if (childPosition - index < sublistingCount) {
                return SUBLISTING_TYPE;
            } else {
                index += sublistingCount;
            }
        }
        Log.w(TAG, String.format("Default child type returned for (%d, %d)", groupPosition, childPosition));
        return SUBHEADING_TYPE;
    }

    @Override
    public int getChildTypeCount() {
        return NUM_CHILD_TYPES;
    }

    @SuppressWarnings("unchecked")
    public String getChildKey(int groupPosition, int childPosition) {
        if (getChildType(groupPosition, childPosition) != SUBLISTING_TYPE) {
            Log.w(TAG, "Subheadings do not have keys!");
            return null;
        }
        return (String) ((Map<String, ?>) getChild(groupPosition, childPosition)).get(ITEM_KEY);
    }

    private void bindView(View view, Map<String, ?> data, String[] from, int[] to) {
        if (from.length != to.length) {
            Log.w(TAG, String.format("Bind view len(from) = %d, len(to) = %d", from.length, to.length));
        }
        for (int i = 0; i < to.length && i < from.length; i += 1) {
            TextView v = (TextView) view.findViewById(to[i]);
            if (null != v) {
                v.setText((String) data.get(from[i]));
            }
        }
    }
}
