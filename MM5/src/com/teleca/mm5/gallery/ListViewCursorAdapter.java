/**
 * This is adapter for ListView view of ContentGallery
 */
package com.teleca.mm5.gallery;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * @author AlexStarc
 *
 */
public class ListViewCursorAdapter extends CursorAdapter {
    @SuppressWarnings("unused")
    private Context context = null;
    private final String TAG = "ListViewCursorAdapter";
    private OnClickListener playButtonListener = null;
    private Integer nFocus = -1;
    private Integer nPlayIndex = -1;
    private static final String GALLERYVIEW_UNDEFINED_FIELD = "Unknown";
    private static final Integer GALLERYLISTVIEW_ITEM_ACTIVE = 1;
    private static final Integer GALLERYLISTVIEW_ITEM_NONACTIVE = 0;
    private boolean bSelectAnimationPlayed = false;

    /**
     * ListViewCursorAdapter - main constructor
     *
     * @param context - main context;
     * @param c - cursor to be used for adapter;
     * @param autoRequery - boolean flag to allow auto-requery;
     * @param elementLayoutId - layout id to be used for single element;
     * @param clickListener - listener for buttons on list items;
     */
    public ListViewCursorAdapter(Context context, Cursor c, boolean autoRequery, int elementLayoutId) {
        super(context, c, autoRequery);
        this.context = context;
        Log.e(TAG, "created");
    }

    @Override
    public void bindView(View elementView, Context context, Cursor cursor) {
        try {
            int fileNameCol = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
            String fileName = cursor.getString(fileNameCol);

            TextView fileNameView = (TextView)elementView.findViewById(R.id.listview_item_text);

            if(null != fileNameView) {
                fileNameView.setText(fileName);
            }

            GalleryContentItem viewTag = new GalleryContentItem(fileName, cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));

            viewTag.setIndex(cursor.getPosition());
            elementView.setTag(viewTag);

            if(nFocus == cursor.getPosition() &&
               !bSelectAnimationPlayed) {
                // play animation on focused view
                AnimationSet showUpAnimationSet = new AnimationSet(true);

                // Add scaling animation
                showUpAnimationSet.addAnimation( new ScaleAnimation((float)1.0,
                                                                    (float)1.0,
                                                                    (float)0.75,
                                                                    (float)1.0) );
                // Add fade out animation
                showUpAnimationSet.addAnimation( new AlphaAnimation((float)0.3, (float)1.0) );

                showUpAnimationSet.setDuration(400);
                showUpAnimationSet.setInterpolator(new DecelerateInterpolator());

                elementView.startAnimation(showUpAnimationSet);
                bSelectAnimationPlayed = true;
            }

            Button playButton = (Button)elementView.findViewById(R.id.listview_item_play);

            // also, update icon if it's currently playing item
            if( nPlayIndex == cursor.getPosition() ) {
                playButton.setBackgroundResource(R.drawable.listview_item_stop_button);
            } else {
                playButton.setBackgroundResource(R.drawable.listview_item_button);
            }
        } catch(Exception e) {
            Log.e(TAG, "bindView(): " + e.getClass() + " thrown " + e.getMessage());
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View elementView = null;

        try {
            final LayoutInflater inflater = LayoutInflater.from(context);

            if( nFocus == cursor.getPosition() ) {
                elementView = inflater.inflate(R.layout.listviewgallery_item_selected, parent, false);
                populateFocusView(elementView, context, cursor);
            } else {
                elementView = inflater.inflate(R.layout.listviewgallery_item, parent, false);
            }

            int fileNameCol = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
            String fileName = cursor.getString(fileNameCol);
            TextView fileNameView = (TextView)elementView.findViewById(R.id.listview_item_text);
            Button playButton = (Button)elementView.findViewById(R.id.listview_item_play);

            if(null != fileNameView) {
                fileNameView.setText(fileName);
            }

            if(null != playButton) {
                playButton.setOnClickListener(getPlayButtonListener());
                // also, update icon if it's currently playing item
                if( nPlayIndex == cursor.getPosition() ) {
                    playButton.setBackgroundResource(R.drawable.listview_item_stop_button);
                } else {
                    playButton.setBackgroundResource(R.drawable.listview_item_button);
                }
            }

            // create and set GalleryContentItem as tag to every view
            GalleryContentItem viewTag = new GalleryContentItem(fileName, cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));

            viewTag.setIndex(cursor.getPosition());
            elementView.setTag(viewTag);
        } catch(Exception e) {
            Log.e(TAG, "newView(): " + e.getClass() + " thrown " + e.getMessage());
        }

        if(nFocus == cursor.getPosition() &&
           !bSelectAnimationPlayed) {
            // play animation on focused view
            AnimationSet showUpAnimationSet = new AnimationSet(true);

            // Add scaling animation
            showUpAnimationSet.addAnimation( new ScaleAnimation((float)1.0,
                                                                (float)1.0,
                                                                (float)0.75,
                                                                (float)1.0) );
            // Add fade out animation
            showUpAnimationSet.addAnimation( new AlphaAnimation((float)0.3, (float)1.0) );

            showUpAnimationSet.setDuration(400);
            showUpAnimationSet.setInterpolator(new DecelerateInterpolator());

            elementView.startAnimation(showUpAnimationSet);
            bSelectAnimationPlayed = true;
        }

        return elementView;
    }

    /**
     * @param playButtonListener the playButtonListener to set
     */
    public void setPlayButtonListener(OnClickListener playButtonListener) {
        this.playButtonListener = playButtonListener;
    }

    /**
     * @return the playButtonListener
     */
    public OnClickListener getPlayButtonListener() {
        return playButtonListener;
    }

    /**
     * @param nFocus the nFocus to set
     */
    public void setnFocus(Integer nFocus) {
        this.nFocus = nFocus;
        bSelectAnimationPlayed = false;
        notifyDataSetChanged();
    }

    /**
     * @return the nFocus
     */
    public Integer getnFocus() {
        return nFocus;
    }

    /**
     * populateFocusView - to fill up info for focused element fields
     *
     * @param elementView
     * @param context
     * @param cursor
     */
    public void populateFocusView(View elementView, Context context, Cursor cursor) {
        TextView itemTextView = null;
        String itemText = null;

        // here's additional information should be populated for the sound file
        itemTextView = (TextView)elementView.findViewById(R.id.listview_item_artist);

        if(null != itemTextView) {
            itemText = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

            if(itemText == null) {
                itemText = GALLERYVIEW_UNDEFINED_FIELD;
            }

            itemText = "Artist: " + itemText;
            itemTextView.setText(itemText);
        }

        itemTextView = (TextView)elementView.findViewById(R.id.listview_item_album);

        if(null != itemTextView) {
            itemText = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));

            if(itemText == null) {
                itemText = GALLERYVIEW_UNDEFINED_FIELD;
            }

            itemText = "Album: " + itemText;
            itemTextView.setText(itemText);
        }

        itemTextView = (TextView)elementView.findViewById(R.id.listview_item_duration);

        if(null != itemTextView) {
            itemText = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

            if(itemText == null) {
                itemText = GALLERYVIEW_UNDEFINED_FIELD;
            } else {
                Time trackTime = new Time();

                trackTime.set(Long.parseLong(itemText));

                // determine how time should be shown - currently check only hours existence
                if( trackTime.hour <= 0 ) {
                    itemText = trackTime.format("%M:%S");
                } else {
                    itemText = trackTime.format("%H:%M:%S");
                }
            }

            itemText = "Duration: " + itemText;

            itemTextView.setText(itemText);
        }
    }

    /**
     * @param nPlayIndex the nPlayIndex to set
     */
    public void setnPlayIndex(Integer nPlayIndex) {
        this.nPlayIndex = nPlayIndex;
    }

    /**
     * @return the nPlayIndex
     */
    public Integer getnPlayIndex() {
        return nPlayIndex;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == nFocus)
        {
            return GALLERYLISTVIEW_ITEM_ACTIVE;
        } else {
            return GALLERYLISTVIEW_ITEM_NONACTIVE;
        }
    }
}
