package net.inmediahk.reader;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

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
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private boolean mTwoPane;
    private List<Fragment> mFragmentArray = new ArrayList<Fragment>();

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

        for(int x = 0;x<Settings.TOTAL_TABS;x++){
            mFragmentArray.add(getTaxonomy(x));
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
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
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

//        if (findViewById(R.id.item_detail_container) != null) {
//            // The detail container view will be present only in the
//            // large-screen layouts (res/values-large and
//            // res/values-sw600dp). If this view is present, then the
//            // activity should be in two-pane mode.
//            mTwoPane = true;
//
//            // In two-pane mode, list items should be given the
//            // 'activated' state when touched.
////            ((ItemListFragment) getSupportFragmentManager()
////                    .findFragmentById(R.id.item_list))
////                    .setActivateOnItemClick(true);
//        } else {
//
//            new Handler().post(new Runnable() {
//                public void run() {
//                    try {
//                        if (getSupportFragmentManager().findFragmentById(R.id.item_list) == null)
//                            getSupportFragmentManager().beginTransaction()
//                                    .add(R.id.item_list, new ItemListFragment())
//                                    .commit();
//                    } catch (IllegalStateException e ) {
//
//                    }
//                }
//            });
//        }
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

    public Fragment getTaxonomy(int position) {
        Bundle arguments = new Bundle();
        arguments.putInt(ItemListFragment.ARG_ITEM_ID, position);
        ItemListFragment fragment = new ItemListFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onItemSelected(int id, FeedItem item) {
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
        ((ItemListFragment) mFragmentArray.get(event.id)).notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
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
            return Settings.TOTAL_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 1:
                    return Settings.CATEGORY_LIST.get(1).getName();
                case 2:
                    return Settings.CATEGORY_LIST.get(2).getName();
                case 3:
                    return Settings.CATEGORY_LIST.get(3).getName();
                case 4:
                    return Settings.CATEGORY_LIST.get(4).getName();
                case 5:
                    return Settings.CATEGORY_LIST.get(5).getName();
                case 6:
                    return Settings.CATEGORY_LIST.get(6).getName();
                case 7:
                    return Settings.CATEGORY_LIST.get(7).getName();
                case 8:
                    return Settings.CATEGORY_LIST.get(8).getName();
                case 0:
                default:
                    return Settings.CATEGORY_LIST.get(0).getName();
            }
        }
    }

}
