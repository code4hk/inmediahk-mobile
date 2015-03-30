package net.inmediahk.reader;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import net.inmediahk.reader.Util.Utils;


public class FacebookView extends ActionBarActivity {

    private String mUrl, mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Utils.setTranslucentStatus(this, getWindow(), true);
        }

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        mTitle = i.getStringExtra("facebook_title");
        mUrl = i.getStringExtra("facebook_url");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, FacebookViewFragment.newInstance(mUrl))
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_facebook_view, menu);
        // Set up ShareActionProvider's default share intent
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        ShareActionProvider shareActionProvider;
        shareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(shareItem);
        shareActionProvider.setShareIntent(getDefaultIntent());
        return true;
    }

    public Intent getDefaultIntent() {
        if (mUrl == null) return null;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
        intent.putExtra(Intent.EXTRA_TEXT, mUrl);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
