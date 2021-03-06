package net.inmediahk.reader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import net.inmediahk.reader.Adapter.FeedAdapter;
import net.inmediahk.reader.DAO.FeedManager;
import net.inmediahk.reader.Model.FeedItem;
import net.inmediahk.reader.Util.Utils;

/**
 * A list fragment representing a list of Items. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ItemDetailFragment}.
 * <p/>
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
    boolean mLastUseProxyState;
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;
    private FeedManager mFeedManager;
    private AbsListView mListView;
    private SwipeRefreshLayout mSwipeLayout;
    private FeedAdapter mAdapter;
    private int mCategoryId;
    private boolean mFirstLoad = true;
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private Handler mHandler = new Handler();
    private Runnable mCheckFinish = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(mCheckFinish);
            if (!mFeedManager.isLoading()) {
                notifyDataSetChanged();
                mSwipeLayout.setRefreshing(false);
                if (mFeedManager.get().size() == 0) {
                    refresh(false);
                }
            } else
                mHandler.postDelayed(mCheckFinish, 200);
        }
    };

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

        mFeedManager = new FeedManager(getActivity());
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mCategoryId = getArguments().getInt(ARG_ITEM_ID);
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
        mListView.setEmptyView(view.findViewById(R.id.swipeRefreshLayout_emptyView));
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
//        if (savedInstanceState != null
//                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
////            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
//        }

        if (mFeedManager.size() == 0 && !mFeedManager.isLoading()) {
            refresh(true);
        }
    }

    @Override
    public void onRefresh() {
        if (!mFeedManager.isLoading()) {
            refresh(true);
        }
    }

    private void refresh(boolean useProxy) {
        if (!Utils.isOnline(getActivity())) {
            mFeedManager.loadCache(Settings.CATEGORY_LIST.get(mCategoryId).getUrl(useProxy), mCategoryId);
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mLastUseProxyState && !useProxy) {
            Log.d("GetFeed", "No connection");
            return;
        }
        mLastUseProxyState = useProxy;
        mFeedManager.clear();
        mFeedManager.load(Settings.CATEGORY_LIST.get(mCategoryId).getUrl(useProxy), mFirstLoad, mCategoryId);
        mFirstLoad = false;
        mSwipeLayout.setRefreshing(true);
        mHandler.postDelayed(mCheckFinish, 200);
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
        if (mAdapter == null) {
            mAdapter = new FeedAdapter(getActivity());
            ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        }
        mAdapter.setData(mFeedManager.get());
        mAdapter.notifyDataSetChanged();
//        if (!mFeedManager.isLoading())
//            mSwipeLayout.setRefreshing(false);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(int id, FeedItem item);
    }

}
