package net.inmediahk.reader;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import net.inmediahk.reader.Util.Utils;

import de.greenrobot.event.EventBus;

public class ItemListActivity extends ActionBarActivity
        implements ItemListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Utils.setTranslucentStatus(this, getWindow(), true);
        }

        EventBus.getDefault().register(this);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
//            ((ItemListFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.item_list))
//                    .setActivateOnItemClick(true);
        } else {

            new Handler().post(new Runnable() {
                public void run() {
                    try {
                        if (getSupportFragmentManager().findFragmentById(R.id.item_list) == null)
                            getSupportFragmentManager().beginTransaction()
                                    .add(R.id.item_list, new ItemListFragment())
                                    .commit();
                    } catch (IllegalStateException e ) {

                    }
                }
            });
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    public void onEventMainThread(Events.FeedAdapterUpdatedEvent event) {
        ((ItemListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.item_list)).notifyDataSetChanged();
    }

    /**
     * Callback method from {@link ItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(int id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(ItemDetailFragment.ARG_ITEM_ID, id);
            mFragment = new ItemDetailFragment();
            mFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, mFragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ItemDetailActivity.class);
            detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private ShareActionProvider mShareActionProvider;
    private ItemDetailFragment mFragment;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mTwoPane && mFragment!=null) {
            getMenuInflater().inflate(R.menu.share_menu, menu);

            // Set up ShareActionProvider's default share intent
            MenuItem shareItem = menu.findItem(R.id.menu_item_share);
            mShareActionProvider = (ShareActionProvider)
                    MenuItemCompat.getActionProvider(shareItem);
            mShareActionProvider.setShareIntent(mFragment.getDefaultIntent());

            return super.onCreateOptionsMenu(menu);
        }
        return super.onCreateOptionsMenu(menu);
    }
}
