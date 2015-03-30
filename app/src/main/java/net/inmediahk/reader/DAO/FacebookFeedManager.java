package net.inmediahk.reader.DAO;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;

import net.inmediahk.reader.Events;
import net.inmediahk.reader.Model.FacebookItem;
import net.inmediahk.reader.Model.FacebookResponse;
import net.inmediahk.reader.Util.OkHTTPClient;
import net.inmediahk.reader.Util.Utils;

import java.util.ArrayList;
import java.util.Iterator;

import de.greenrobot.event.EventBus;

public class FacebookFeedManager {

    GetFeed mGetFeed = new GetFeed();
    /**
     * Holds the single instance of a NewsManager that is shared by the process.
     */
//    private static FeedManager sInstance;
    private int mTabId = 0;
    /**
     * Holds the images and related data that have been downloaded
     */
    private ArrayList<FacebookItem> mFeeds = new ArrayList<>();
    /**
     * True if we are in the process of loading
     */
    private boolean mLoading;

    //    public static FeedManager getInstance(Context c) {
//        if (sInstance == null) {
//            sInstance = new FeedManager(c.getApplicationContext());
//            for(int x = 0;x<Settings.TOTAL_TABS;x++){
//                sInstance.mFeeds.add(new ArrayList<FacebookFeed>());
//            }
//        }
//        return sInstance;
//    }
    private Context mContext;

    public FacebookFeedManager(Context c) {
        mContext = c;
    }

    /**
     * @return True if we are still loading content
     */
    public boolean isLoading() {
        return mLoading;
    }

    /**
     * Clear all downloaded content
     */
    public void clear() {
        mFeeds.clear();
        notifyObservers();
    }

    /**
     * @return The number of items displayed so far
     */
    public int size() {
        try {
            return mFeeds.size();
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }

    /**
     * Gets the item at the specified position
     */
    public FacebookItem get(int position) {
        return mFeeds.get(position);
    }

    /**
     * Gets the all items
     */
    public ArrayList<FacebookItem> get() {
        return mFeeds;
    }

    public void load(String url, boolean firstLoad, int tabId) {
        mTabId = tabId;
        final ArrayList<FacebookItem> facebookItems = Utils.getFacebookProperty(mContext, url);
        if (facebookItems != null && facebookItems.size() > 0 && !firstLoad) {
            mFeeds = facebookItems;
            mLoading = false;
            notifyObservers();
        } else {
            mLoading = true;
            mGetFeed.cancel(true);
            mGetFeed = new GetFeed();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                mGetFeed.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
            else
                mGetFeed.execute(url);
        }
    }

    public void loadCache(String url, int tabId) {
        final ArrayList<FacebookItem> facebookItems = Utils.getFacebookProperty(mContext, url);
        if (facebookItems != null && facebookItems.size() > 0) {
            mTabId = tabId;
            mFeeds = facebookItems;
            notifyObservers();
        }
    }

    /**
     * Called when something changes in our data set. Cleans up any weak references that are no longer
     * valid along the way.
     */
    private void notifyObservers() {
        Log.d("GetFeed", "notifyObservers");
        EventBus.getDefault().post(new Events.FeedAdapterUpdatedEvent(1));
    }

    private class GetFeed extends AsyncTask<String, Integer, Boolean> {

        private OkHTTPClient mHTTPClient = new OkHTTPClient();
        private FacebookResponse facebookResponse;

        @Override
        protected void onPreExecute() {
            mFeeds.clear();
        }

        @Override
        protected Boolean doInBackground(String... param) {
            Thread.currentThread().setName("GetFeed");

            final String result = mHTTPClient.get(param[0]);
            Log.d("GetFeed", "Start get feed");

            try {
                facebookResponse = new Gson().fromJson(result, FacebookResponse.class);
                mFeeds = facebookResponse.getData();
                for (Iterator<FacebookItem> it = mFeeds.iterator(); it.hasNext(); ) {
                    FacebookItem item = it.next();
                    if (!item.isSelfPost()) {
                        Log.d("GetFeed", "Remove: " + item.getName());
                        it.remove();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            Log.d("GetFeed", "No error");
            return true;
        }

        protected void onPostExecute(Boolean result) {
            Log.d("GetFeed", "size: " + mFeeds.size());
            mLoading = false;
            notifyObservers();
        }
    }
}