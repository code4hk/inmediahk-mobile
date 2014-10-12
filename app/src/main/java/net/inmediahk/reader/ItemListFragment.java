package net.inmediahk.reader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import net.inmediahk.reader.Adapter.FeedAdapter;
import net.inmediahk.reader.DAO.FeedManager;
import net.inmediahk.reader.Model.FeedItem;

/**
 * A list fragment representing a list of Items. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ItemDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link net.inmediahk.reader.ItemListFragment.Callbacks}
 * interface.
 */
public class ItemListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_FEED = "feed_item";
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    /**
     * A dummy implementation of the {@link net.inmediahk.reader.ItemListFragment.Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int id, FeedItem item) {
        }
    };
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;
    private FeedManager mFeedManager;
    private AbsListView mListView;
    private SwipeRefreshLayout mSwipeLayout;
    private FeedAdapter mAdapter;
    private int mTaxonomyId;
    private FeedItem mItem;
    private boolean mFirstLoad= true;
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

//        mFeedManager = FeedManager.getInstance(getActivity());
        mFeedManager = new FeedManager(getActivity());
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mTaxonomyId = getArguments().getInt(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.app_color,
                R.color.app_bg_color,
                R.color.app_color,
                R.color.app_bg_color);

        mListView = (ListView) view.findViewById(R.id.mListView);
        // Set the adapter
        mAdapter = new FeedAdapter(getActivity());
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallbacks.onItemSelected(position, mFeedManager.get(position));
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mCallbacks.onItemSelected(position, mFeedManager.get(position));
                return false;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
//            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }

        if (mFeedManager.size() == 0) {
            refresh();
        }
    }

    @Override
    public void onRefresh() {
        if (!mFeedManager.isLoading()) {
            refresh();
        }
    }

    private void refresh() {
        mFeedManager.clear();
        mFeedManager.load(Settings.CATEGORY_LIST.get(mTaxonomyId).getUrl(), mFirstLoad, mTaxonomyId);
        mFirstLoad = false;
        mSwipeLayout.setRefreshing(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
        mAdapter.setData(mFeedManager.get());
        if (!mFeedManager.isLoading())
            mSwipeLayout.setRefreshing(false);
    }

//    /**
//     * Turns on activate-on-click mode. When this mode is on, list items will be
//     * given the 'activated' state when touched.
//     */
//    public void setActivateOnItemClick(boolean activateOnItemClick) {
//        // When setting CHOICE_MODE_SINGLE, ListView will automatically
//        // give items the 'activated' state when touched.
//        mListView.setChoiceMode(activateOnItemClick
//                ? ListView.CHOICE_MODE_SINGLE
//                : ListView.CHOICE_MODE_NONE);
//    }
//
//    private void setActivatedPosition(int position) {
//        if (position == ListView.INVALID_POSITION) {
//            mListView.setItemChecked(mActivatedPosition, false);
//        } else {
//            mListView.setItemChecked(position, true);
//        }
//
//        mActivatedPosition = position;
//    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(int id, FeedItem item);
    }
}
