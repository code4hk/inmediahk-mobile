package net.inmediahk.reader;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import net.inmediahk.reader.Model.FeedItem;
import net.inmediahk.reader.Util.Utils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener,
        ItemListFragment.Callbacks {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
//    SamplePagerAdapter mSamplePagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    FeedItem currentItem;
    private boolean mTwoPane;
    private List<Fragment> mFragmentArray = new ArrayList<Fragment>();
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Utils.setTranslucentStatus(this, getWindow(), true);
        }

        EventBus.getDefault().register(this);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Bundle arguments = new Bundle();
        FacebookListFragment fragment = new FacebookListFragment();
        fragment.setArguments(arguments);
        mFragmentArray.add(getCategory(0));
        mFragmentArray.add(fragment);
        for (int x = 1; x < Settings.TOTAL_TABS; x++) {
            mFragmentArray.add(getCategory(x));
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    getSupportActionBar().newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public Fragment getCategory(int position) {
        Bundle arguments = new Bundle();
        arguments.putInt(ItemListFragment.ARG_ITEM_ID, position);
        ItemListFragment fragment = new ItemListFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onItemSelected(int id, FeedItem item) {
        currentItem = item;
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(ItemListFragment.ARG_ITEM_ID, id);
            arguments.putParcelable(ItemListFragment.ARG_FEED, item);
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            mFragmentArray.add(id, fragment);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ItemDetailActivity.class);
            detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
            detailIntent.putExtra(ItemDetailFragment.ARG_FEED, item);
            startActivity(detailIntent);
        }
    }

    public void onEventMainThread(Events.FeedAdapterUpdatedEvent event) {
//        mSamplePagerAdapter.notifyDataSetChanged(event.id);
        Log.d("GetFeed", "FeedAdapterUpdatedEvent id: " + event.id);
        if (event.id == 1)
            ((FacebookListFragment) mFragmentArray.get(event.id)).notifyDataSetChanged();
        else
            ((ItemListFragment) mFragmentArray.get(event.id)).notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mTwoPane) {
            getMenuInflater().inflate(R.menu.share_menu, menu);

            // Set up ShareActionProvider's default share intent
            MenuItem shareItem = menu.findItem(R.id.menu_item_share);
            mShareActionProvider = (ShareActionProvider)
                    MenuItemCompat.getActionProvider(shareItem);
            mShareActionProvider.setShareIntent(getDefaultIntent());

            return super.onCreateOptionsMenu(menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public Intent getDefaultIntent() {
        if (currentItem == null) return null;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_SUBJECT, currentItem.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, currentItem.getLink());
        return intent;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return mFragmentArray.get(position);
        }

        @Override
        public int getCount() {
            return Settings.TOTAL_TABS + 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return getString(R.string.fb_news);
                case 2:
                    return Settings.CATEGORY_LIST.get(1).getName();
                case 3:
                    return Settings.CATEGORY_LIST.get(2).getName();
                case 4:
                    return Settings.CATEGORY_LIST.get(3).getName();
                case 5:
                    return Settings.CATEGORY_LIST.get(4).getName();
                case 6:
                    return Settings.CATEGORY_LIST.get(5).getName();
                case 7:
                    return Settings.CATEGORY_LIST.get(6).getName();
                case 8:
                    return Settings.CATEGORY_LIST.get(7).getName();
                case 9:
                    return Settings.CATEGORY_LIST.get(8).getName();
                case 0:
                default:
                    return Settings.CATEGORY_LIST.get(0).getName();
            }
        }
    }
}
