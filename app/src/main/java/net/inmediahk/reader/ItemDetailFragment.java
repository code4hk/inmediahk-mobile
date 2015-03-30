package net.inmediahk.reader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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

    private FeedItem mItem;
    private TextView mTv;
    private Html.ImageGetter imgGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            LevelListDrawable drawable = new LevelListDrawable();
            Drawable empty = getResources().getDrawable(R.drawable.ic_launcher);
            drawable.addLevel(0, 0, empty);
            drawable.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

            new LoadImage().execute(source, drawable);

            return drawable;
        }
    };

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
        mTv = (TextView) rootView.findViewById(R.id.txtMessage);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.txtName)).setText(mItem.getTitle());
            ((TextView) rootView.findViewById(R.id.txtAuthor)).setText("文：" + mItem.getCreator());
            ((TextView) rootView.findViewById(R.id.txtDate)).setText(mItem.getDate());

            if (!mItem.getImageUrl().equals(""))
                Picasso.with(getActivity()).load(mItem.getImageUrl()).into(((ImageView) rootView.findViewById(R.id.imageView)));

            mTv.setText(Html.fromHtml(mItem.getContent(), imgGetter, null));
            mTv.setMovementMethod(LinkMovementMethod.getInstance());
        }

        return rootView;
    }

    class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private LevelListDrawable mDrawable;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            try {
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(bitmap);

                int width = mTv.getWidth();
                int height = bitmap.getHeight() * width / bitmap.getWidth();

                mDrawable.addLevel(1, 1, d);
                mDrawable.setBounds(0, 0, width, height);
                mDrawable.setLevel(1);

                CharSequence t = mTv.getText();
                mTv.setText(t);
            }
        }
    }
}
