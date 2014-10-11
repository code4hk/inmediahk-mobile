package net.inmediahk.reader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.inmediahk.reader.DAO.FeedManager;
import net.inmediahk.reader.Model.FeedItem;


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {

    private FeedManager mFeedManager;
    private FeedItem mItem;
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mFeedManager = FeedManager.getInstance(getActivity());
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = mFeedManager.get(getArguments().getInt(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.txtTitle)).setText(mItem.getTitle());
            ((TextView) rootView.findViewById(R.id.txtDate)).setText(mItem.getDate());
            ((TextView) rootView.findViewById(R.id.txtDesc)).setText(Html.fromHtml(mItem.getContent()));
            if (!mItem.getImageUrl().equals(""))
                Picasso.with(getActivity()).load(mItem.getImageUrl()).into(((ImageView) rootView.findViewById(R.id.imageView)));
            ((TextView) rootView.findViewById(R.id.txtDesc)).setMovementMethod(LinkMovementMethod.getInstance());
        }

        return rootView;
    }

    public Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_SUBJECT, mItem.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, mItem.getLink());
        return intent;
    }
}
