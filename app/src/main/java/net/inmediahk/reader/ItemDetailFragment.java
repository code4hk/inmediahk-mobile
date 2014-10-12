package net.inmediahk.reader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.inmediahk.reader.Model.FeedItem;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@MainActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_FEED = "feed_item";
//    private FeedManager mFeedManager;
    private FeedItem mItem;

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

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = getArguments().getParcelable(ARG_FEED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.txtTitle)).setText(mItem.getTitle());
            ((TextView) rootView.findViewById(R.id.txtAuthor)).setText("文：" + mItem.getCreator());
            ((TextView) rootView.findViewById(R.id.txtDate)).setText(mItem.getDate());
            ((TextView) rootView.findViewById(R.id.txtDesc)).setText(Html.fromHtml(mItem.getContent()));
            if (!mItem.getImageUrl().equals(""))
                Picasso.with(getActivity()).load(mItem.getImageUrl()).into(((ImageView) rootView.findViewById(R.id.imageView)));
            ((TextView) rootView.findViewById(R.id.txtDesc)).setMovementMethod(LinkMovementMethod.getInstance());
        }

        return rootView;
    }

}
