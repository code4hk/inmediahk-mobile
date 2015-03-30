package net.inmediahk.reader;

import android.content.Intent;
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

import net.inmediahk.reader.Adapter.FacebookFeedAdapter;
import net.inmediahk.reader.DAO.FacebookFeedManager;
import net.inmediahk.reader.Model.FacebookItem;
import net.inmediahk.reader.Util.Utils;

public class FacebookListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_FEED = "feed_item";
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */

    private FacebookFeedManager mFacebookFeedManager;
    private AbsListView mListView;
    private SwipeRefreshLayout mSwipeLayout, mSwipeLayoutEmpty;
    private FacebookFeedAdapter mAdapter;
    private int mCategoryId;
    private FacebookItem mItem;
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
            if (!mFacebookFeedManager.isLoading()) {
                mSwipeLayout.setRefreshing(false);
            } else
                mHandler.postDelayed(mCheckFinish, 200);
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FacebookListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mFacebookFeedManager = new FacebookFeedManager(getActivity());
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mCategoryId = getArguments().getInt(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fb_list, container, false);
        mSwipeLayoutEmpty = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout_emptyView);
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.app_color,
                R.color.app_bg_color,
                R.color.app_color,
                R.color.app_bg_color);

        mListView = (ListView) view.findViewById(R.id.mListView);

        // Set the adapter
        mAdapter = new FacebookFeedAdapter(getActivity());
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), FacebookView.class);
                intent.putExtra("facebook_url", mFacebookFeedManager.get(position).getLink());
                intent.putExtra("facebook_title", mFacebookFeedManager.get(position).getName());
                startActivity(intent);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FacebookView.class);
                intent.putExtra("facebook_url", mFacebookFeedManager.get(position).getLink());
                intent.putExtra("facebook_title", mFacebookFeedManager.get(position).getName());
                startActivity(intent);

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

        if (mFacebookFeedManager.size() == 0 && !mFacebookFeedManager.isLoading()) {
            refresh();
        }
    }

    @Override
    public void onRefresh() {
        if (!mFacebookFeedManager.isLoading()) {
            refresh();
        }
    }

    private void refresh() {
        if (!Utils.isOnline(getActivity())) {
            mFacebookFeedManager.loadCache(Settings.URL_FACEBOOK, mCategoryId);
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            return;
        }
        mFacebookFeedManager.clear();
        mFacebookFeedManager.load(Settings.URL_FACEBOOK, mFirstLoad, mCategoryId);
        mFirstLoad = false;
        mSwipeLayout.setRefreshing(true);
        mHandler.postDelayed(mCheckFinish, 200);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Serialize and persist the activated item position.
        if (mActivatedPosition != ListView.INVALID_POSITION)
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
    }

    public void notifyDataSetChanged() {
        Log.d("GetFeed", "notifyDataSetChanged");
        if (mAdapter == null) {
            mAdapter = new FacebookFeedAdapter(getActivity());
            ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        }
        mAdapter.notifyDataSetChanged();
        mAdapter.setData(mFacebookFeedManager.get());

        if (mFacebookFeedManager != null && mFacebookFeedManager.get().size() > 0) {
            mSwipeLayout.setVisibility(View.VISIBLE);
            mSwipeLayoutEmpty.setVisibility(View.GONE);
        } else {
            mSwipeLayout.setVisibility(View.GONE);
            mSwipeLayoutEmpty.setVisibility(View.VISIBLE);
        }
        Log.d("GetFeed", "size: " + mFacebookFeedManager.get().size());
    }

}
