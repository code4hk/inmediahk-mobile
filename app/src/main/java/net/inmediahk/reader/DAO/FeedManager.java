package net.inmediahk.reader.DAO;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import net.inmediahk.reader.Events;
import net.inmediahk.reader.Model.FeedItem;
import net.inmediahk.reader.Util.OkHTTPClient;

import de.greenrobot.event.EventBus;

public class FeedManager {

    /**
     * Holds the single instance of a NewsManager that is shared by the process.
     */
    private static FeedManager sInstance;

    /**
     * Holds the images and related data that have been downloaded
     */
    private List<FeedItem> mFeeds = new ArrayList<FeedItem>();

    /**
     * True if we are in the process of loading
     */
    private boolean mLoading;

    private Context mContext;

    public static FeedManager getInstance(Context c) {
        if (sInstance == null) {
            sInstance = new FeedManager(c.getApplicationContext());
        }
        return sInstance;
    }

    private FeedManager(Context c) {
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
     * Add an item to and notify observers of the change.
     *
     * @param item The item to add
     */
    private void add(FeedItem item) {
        mFeeds.add(item);
        notifyObservers();
    }

    private void add(ArrayList<FeedItem> items) {
        mFeeds = items;
        notifyObservers();
    }

    /**
     * @return The number of items displayed so far
     */
    public int size() {
        return mFeeds.size();
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
    public List<FeedItem> get() {
        return mFeeds;
    }

    public void load() {
        mLoading = true;
        mGetFeed.cancel(true);
        mGetFeed = new GetFeed();
        final String url = "http://www.inmediahk.net/full/feed";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mGetFeed.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        else
            mGetFeed.execute(url);
    }

    /**
     * Called when something changes in our data set. Cleans up any weak references that are no longer
     * valid along the way.
     */
    private void notifyObservers() {
        EventBus.getDefault().post(new Events.FeedAdapterUpdatedEvent());
    }

    GetFeed mGetFeed = new GetFeed();
    private class GetFeed extends AsyncTask<String, Integer, Boolean> {

        private OkHTTPClient mHTTPClient = new OkHTTPClient();
        public final static String DEFAULT_ENCODE = "UTF-8";

        @Override
        protected void onPreExecute() {
            mFeeds.clear();
        }

        @Override
        protected Boolean doInBackground(String... param) {
            Thread.currentThread().setName("GetFeed");

            final String result = mHTTPClient.get(param[0]);

            try {
                final Elements items = Jsoup.parse(result).select("channel > item");

                for (Element item : items) {
                    FeedItem feed = new FeedItem();
                    feed.setData(item.select("title").text(), item.textNodes().get(2).toString(), item.select("pubDate").text(), item.select("dc|creator").text(),
                            item.select("description").text());
                    mFeeds.add(feed);
                }
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
