package net.inmediahk.reader.DAO;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import net.inmediahk.reader.Events;
import net.inmediahk.reader.Model.FeedItem;
import net.inmediahk.reader.Util.OkHTTPClient;
import net.inmediahk.reader.Util.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class FeedManager {

    GetFeed mGetFeed = new GetFeed();
    /**
     * Holds the single instance of a NewsManager that is shared by the process.
     */
//    private static FeedManager sInstance;
    private int mTabId = 0;
    /**
     * Holds the images and related data that have been downloaded
     */
    private ArrayList<FeedItem> mFeeds = new ArrayList<FeedItem>();
    /**
     * True if we are in the process of loading
     */
    private boolean mLoading;

    //    public static FeedManager getInstance(Context c) {
//        if (sInstance == null) {
//            sInstance = new FeedManager(c.getApplicationContext());
//            for(int x = 0;x<Settings.TOTAL_TABS;x++){
//                sInstance.mFeeds.add(new ArrayList<FeedItem>());
//            }
//        }
//        return sInstance;
//    }
    private Context mContext;

    public FeedManager(Context c) {
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
    public FeedItem get(int position) {
        return mFeeds.get(position);
    }

    /**
     * Gets the all items
     */
    public ArrayList<FeedItem> get() {
        return mFeeds;
    }

    public void load(String url, boolean firstLoad, int tabId) {
        mTabId = tabId;
        final ArrayList<FeedItem> feedItems = Utils.getStringProperty(mContext, url);
        if (feedItems != null && feedItems.size() > 0 && !firstLoad) {
            mFeeds = feedItems;
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
        final ArrayList<FeedItem> feedItems = Utils.getStringProperty(mContext, url);
        if (feedItems.size() > 0) {
            mTabId = tabId;
            mFeeds = feedItems;
            notifyObservers();
        }
    }

    /**
     * Called when something changes in our data set. Cleans up any weak references that are no longer
     * valid along the way.
     */
    private void notifyObservers() {
        EventBus.getDefault().post(new Events.FeedAdapterUpdatedEvent(mTabId));
    }

    private class GetFeed extends AsyncTask<String, Integer, Boolean> {

        public final static String DEFAULT_ENCODE = "UTF-8";
        private OkHTTPClient mHTTPClient = new OkHTTPClient();

        @Override
        protected void onPreExecute() {
            mFeeds.clear();
        }

        @Override
        protected Boolean doInBackground(String... param) {
            Thread.currentThread().setName("GetFeed");

            final String result = mHTTPClient.get(param[0]);

            Log.d("inmediahk", "---- url: " + param[0]);

            try {
                final Elements items = Jsoup.parse(result).select("channel > item");

                for (Element item : items) {
                    FeedItem feed = new FeedItem();
                    feed.setData(item.select("title").text(), item.textNodes().get(2).toString(), item.select("pubDate").text(), item.select("dc|creator").text(),
                            item.select("description").text());
                    mFeeds.add(feed);
                }
                Utils.setStringProperty(mContext, param[0], mFeeds);
                Log.d("inmediahk", "---- done: " + param[0] + " : " + mFeeds.get(0).getTitle());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            mLoading = false;
            notifyObservers();
        }
    }
}