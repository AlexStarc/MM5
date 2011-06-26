/*
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *                     Version 2, December 2004
 *
 *  Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document, and changing it is allowed as long
 *  as the name is changed.
 *
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   0. You just DO WHAT THE FUCK YOU WANT TO.
 *
 *  This program is free software. It comes without any warranty, to
 *  the extent permitted by applicable law. You can redistribute it
 *  and/or modify it under the terms of the Do What The Fuck You Want
 *  To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details.
 */

/**
 * This is adapter for ListView view of ContentGallery
 */
package com.sandrstar.android.gallery;

import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
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
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author AlexStarc
 *
 */
public class ListViewCursorAdapter extends CursorAdapter implements MediaPlayer.OnCompletionListener {
    private final static String TAG = "ListViewCursorAdapter";
    private OnClickListener playButtonListener = null;
    private Integer mnFocus = CGalleryConstants.GALLERY_INVALID_INDEX.value();
    private Integer mnPlayIndex = CGalleryConstants.GALLERY_INVALID_INDEX.value();
    private static final String GALLERYVIEW_UNDEFINED_FIELD = "Unknown";
    private static final String GALLERYVIEW_START_TIME = "00:00";
    private static final Integer GALLERYLISTVIEW_ITEM_ACTIVE = 1;
    private static final Integer GALLERYLISTVIEW_ITEM_NONACTIVE = 0;
    private boolean mbSelectAnimationPlayed = false;
    Integer mnPlayTime = CGalleryConstants.GALLERY_INVALID_INDEX.value();
    private View mPlayProgressView = null;
    TextView mElapsedText = null;
    ProgressBar mProgressBar = null;
    Handler mPlayProgressUpdateHandler = null;
    // Runnable for progress updates handling
    private Runnable mUpdatePlayProgress = null;
    MediaPlayer mPlayer = null;

    /**
     * ListViewCursorAdapter - main constructor
     *
     * @param context - main context;
     * @param c - cursor to be used for adapter;
     * @param autoReQuery - boolean flag to allow auto-re-query;
     */
    public ListViewCursorAdapter(final Context context, final Cursor c, final boolean autoReQuery) {
        super(context, c, autoReQuery);
        Log.i(TAG, "created");

        this.mPlayProgressUpdateHandler = new Handler();
        // create MediaPlayer for playing of selected sound
        this.mPlayer = new MediaPlayer();
        this.mPlayer.setOnCompletionListener(this);

        this.mUpdatePlayProgress = new Runnable() {
            @Override
            public void run() {
                String itemText;
                final Time trackTime = new Time();

                // make elapsed time bigger
                if( ListViewCursorAdapter.this.mPlayer != null ) {
                    ListViewCursorAdapter.this.mnPlayTime = ListViewCursorAdapter.this.mPlayer.getCurrentPosition() / 1000;

                    if(null != ListViewCursorAdapter.this.mElapsedText &&
                       null != ListViewCursorAdapter.this.mProgressBar) {
                        trackTime.set(ListViewCursorAdapter.this.mnPlayTime * 1000);

                        // determine how time should be shown - currently check only hours existence
                        if(trackTime.hour <= 0) {
                            itemText = trackTime.format("%M:%S");
                        } else {
                            itemText = trackTime.format("%H:%M:%S");
                        }

                        ListViewCursorAdapter.this.mElapsedText.setText(itemText);
                        ListViewCursorAdapter.this.mProgressBar.setProgress(ListViewCursorAdapter.this.mnPlayTime);
                    }

                    ListViewCursorAdapter.this.mPlayProgressUpdateHandler.postAtTime(this, SystemClock.uptimeMillis() + 1000);
                }
            }
        };
    }

    @Override
    public void bindView(final View elementView, final Context context, final Cursor cursor) {
        try {
            final int fileNameCol = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
            final String fileName = cursor.getString(fileNameCol);

            final TextView fileNameView = (TextView)elementView.findViewById(R.id.listview_item_text);

            if(null != fileNameView) {
                fileNameView.setText(fileName);
            }

            final GalleryContentItem viewTag = new GalleryContentItem(fileName, cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));

            viewTag.setIndex(cursor.getPosition());
            elementView.setTag(viewTag);

            if(this.mnFocus == cursor.getPosition()) {
                populateFocusView(elementView, cursor);

                if(!this.mbSelectAnimationPlayed) {
                    // play animation on focused view
                    final AnimationSet showUpAnimationSet = new AnimationSet(true);

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
                    this.mbSelectAnimationPlayed = true;
                }
            }

            final Button playButton = (Button)elementView.findViewById(R.id.listview_item_play);

            // also, update icon if it's currently playing item
            if(this.mnPlayIndex == cursor.getPosition()) {
                playButton.setBackgroundResource(R.drawable.listview_item_stop_button);
            } else {
                playButton.setBackgroundResource(R.drawable.listview_item_button);
            }
        } catch(final Exception e) {
            Log.e(TAG, "bindView(): " + e.getClass() + " thrown " + e.getMessage());
        }
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
        View elementView = null;

        try {
            final LayoutInflater inflater = LayoutInflater.from(context);

            if( this.mnFocus == cursor.getPosition() ) {
                elementView = inflater.inflate(R.layout.listviewgallery_item_selected, parent, false);
                populateFocusView(elementView, cursor);
            } else {
                elementView = inflater.inflate(R.layout.listviewgallery_item, parent, false);
            }

            final int fileNameCol = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
            final String fileName = cursor.getString(fileNameCol);
            final TextView fileNameView = (TextView)elementView.findViewById(R.id.listview_item_text);
            final Button playButton = (Button)elementView.findViewById(R.id.listview_item_play);

            if(null != fileNameView) {
                fileNameView.setText(fileName);
            }

            if(null != playButton) {
                playButton.setOnClickListener(getPlayButtonListener());
                // also, update icon if it's currently playing item
                if( this.mnPlayIndex == cursor.getPosition() ) {
                    playButton.setBackgroundResource(R.drawable.listview_item_stop_button);
                } else {
                    playButton.setBackgroundResource(R.drawable.listview_item_button);
                }
            }

            // create and set GalleryContentItem as tag to every view
            final GalleryContentItem viewTag = new GalleryContentItem(fileName, cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));

            viewTag.setIndex(cursor.getPosition());
            elementView.setTag(viewTag);
        } catch(final Exception e) {
            Log.e(TAG, "newView(): " + e.getClass() + " thrown " + e.getMessage());
        }

        if(this.mnFocus == cursor.getPosition() &&
           !this.mbSelectAnimationPlayed) {
            // play animation on focused view
            final AnimationSet showUpAnimationSet = new AnimationSet(true);

            // Add scaling animation
            showUpAnimationSet.addAnimation( new ScaleAnimation((float)1.0,
                                                                (float)1.0,
                                                                (float)0.75,
                                                                (float)1.0) );
            // Add fade out animation
            showUpAnimationSet.addAnimation( new AlphaAnimation((float)0.3, (float)1.0) );

            showUpAnimationSet.setDuration(400);
            showUpAnimationSet.setInterpolator(new DecelerateInterpolator());

            if( elementView != null ) {
                elementView.startAnimation(showUpAnimationSet);
            }

            this.mbSelectAnimationPlayed = true;
        }

        return elementView;
    }

    /**
     * @param playButtonListener the playButtonListener to set
     */
    public void setPlayButtonListener(final OnClickListener playButtonListener) {
        this.playButtonListener = playButtonListener;
    }

    /**
     * @return the playButtonListener
     */
    public OnClickListener getPlayButtonListener() {
        return this.playButtonListener;
    }

    /**
     * @param nFocus the nFocus to set
     */
    public void setNFocus(final Integer nFocus) {
        this.mnFocus = nFocus;
        this.mbSelectAnimationPlayed = false;
        notifyDataSetChanged();
    }

    /**
     * @return the nFocus
     */
    public Integer getNFocus() {
        return this.mnFocus;
    }

    /**
     * populateFocusView - to fill up info for focused element fields
     *
     * @param elementView - View of element to be populated with extended data
     * @param cursor - cursor needed to retrieve extended data
     */
    public void populateFocusView(final View elementView, final Cursor cursor) {
        TextView itemTextView;
        String itemText;

        // here's additional information should be populated for the sound file
        itemTextView = (TextView)elementView.findViewById(R.id.listview_item_artist);

        if(null != itemTextView) {
            itemText = cursor.getString(cursor.getColumnIndex(AudioColumns.ARTIST));

            itemText = "Artist: " + ((itemText == null) ? GALLERYVIEW_UNDEFINED_FIELD : itemText);
            itemTextView.setText(itemText);
        }

        itemTextView = (TextView)elementView.findViewById(R.id.listview_item_album);

        if(null != itemTextView) {
            itemText = cursor.getString(cursor.getColumnIndex(AudioColumns.ALBUM));

            itemText = "Album: " + ((itemText == null) ? GALLERYVIEW_UNDEFINED_FIELD : itemText);
            itemTextView.setText(itemText);
        }

        itemTextView = (TextView)elementView.findViewById(R.id.listview_item_duration);

        if(null != itemTextView) {
            itemText = cursor.getString(cursor.getColumnIndex(AudioColumns.DURATION));

            if(itemText == null) {
                itemText = GALLERYVIEW_UNDEFINED_FIELD;
            } else {
                final Time trackTime = new Time();
                Integer nFocusTrackDuration;

                nFocusTrackDuration = Long.valueOf(itemText).intValue();
                // Set maximum value to progress bar from total time
                final ProgressBar progressBar = (ProgressBar)elementView.findViewById(R.id.play_progressbar_bar);
                progressBar.setMax(nFocusTrackDuration / 1000);

                trackTime.set(nFocusTrackDuration);

                // determine how time should be shown - currently check only hours existence
                if( trackTime.hour <= 0 ) {
                    itemText = trackTime.format("%M:%S");
                } else {
                    itemText = trackTime.format("%H:%M:%S");
                }
            }

            itemTextView.setText("Duration: " + itemText);

            itemTextView = (TextView)elementView.findViewById(R.id.play_progressbar_total_time);

            if(null != itemTextView) {
                itemTextView.setText(itemText);
            }
        }

        // check if we're populating now playing view and obtain playing view if missed
        if(this.mPlayProgressView == null && this.mnFocus.equals(this.mnPlayIndex)) {
            initPlayingProgressView(elementView);
        }

        if(this.mPlayProgressView != null) {
            // show up or hide progress if the file is currently playing
            this.mPlayProgressView.setVisibility(this.mnPlayIndex.equals(this.mnFocus) ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * Sets current playing index into adapter and doing setup necessary for playing view
     *
     * @param nPlayIndex the nPlayIndex to set
     * @param listItemView - View of element to be used as playing one
     * @throws java.io.IOException - because of MediaPlayer.prepare()
     */
    public void setNPlayIndex(final Integer nPlayIndex, final View listItemView) throws IOException {
        try {
            this.mPlayer.reset();
            this.mnPlayIndex = nPlayIndex;
            GalleryContentItem itemTag = null;

            // start play of selected file
            if(null != listItemView) {
                itemTag = (GalleryContentItem)listItemView.getTag();
            }

            if(null != itemTag) {
                this.mPlayer.setDataSource(itemTag.getContentPath());
                this.mPlayer.prepare();
                this.mPlayer.start();
            }

            if(nPlayIndex.equals(this.mnFocus) &&
               nPlayIndex >= 0 &&
               null != listItemView) {
                // reset total play time to 0, because we're beginning to play again
                this.mnPlayTime = 0;
                // remember play progress view in order to let it to be properly updated
                initPlayingProgressView(listItemView);

                if(null != this.mPlayProgressView ) {
                    this.mPlayProgressView.setVisibility(View.VISIBLE);
                }
            } else {
                if(nPlayIndex < 0) {
                    if(null != this.mPlayProgressView) {
                        this.mPlayProgressView.setVisibility(View.INVISIBLE);
                        // need to reset progress also in order to eliminate incorrect play begin
                        this.mProgressBar.setProgress(0);
                    }

                    this.mPlayProgressView = null;
                    this.mElapsedText = null;
                    this.mProgressBar = null;
                    this.mnPlayTime = 0;
                    this.mPlayProgressUpdateHandler.removeCallbacks(this.mUpdatePlayProgress);
                }
            }

            notifyDataSetChanged();
        } catch(final Exception e) {
            Log.e(TAG, "setNPlayIndex(): " + e.getClass() + " thrown " + e.getMessage());
        }
    }

    /**
     * @return the nPlayIndex
     */
    public Integer getNPlayIndex() {
        return this.mnPlayIndex;
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
    public int getItemViewType(final int position) {
        int retval = GALLERYLISTVIEW_ITEM_NONACTIVE;

        if(position == this.mnFocus) {
            retval = GALLERYLISTVIEW_ITEM_ACTIVE;
        }

        return retval;
    }

    @Override
    public void onCompletion(final MediaPlayer mediaPlayer) {
        try {
            setNPlayIndex(CGalleryConstants.GALLERY_INVALID_INDEX.value(), null);
            this.mPlayProgressUpdateHandler.removeCallbacks(this.mUpdatePlayProgress);
        } catch (final IOException e) {
            Log.e(TAG, "onCompletion(): " + e.getClass() + " thrown " + e.getMessage());
        }

        notifyDataSetChanged();
    }

    /**
     * For initializing playProgressView and its utility sub-views
     *
     * @param elementView - list element view from which progress view should be retrieved
     */
    private void initPlayingProgressView(final View elementView) {
        // remember play progress view in order to let it to be properly updated
        this.mPlayProgressView = elementView.findViewById(R.id.listview_play_progressbar);
        this.mElapsedText = null;
        this.mProgressBar = null;

        if(null != this.mPlayProgressView ) {
            this.mElapsedText = (TextView)this.mPlayProgressView.findViewById(R.id.play_progressbar_elapsed_time);
            this.mProgressBar = (ProgressBar)this.mPlayProgressView.findViewById(R.id.play_progressbar_bar);

            if(null != this.mProgressBar) {
                this.mProgressBar.setProgress(0);
            }

            if(null != this.mElapsedText) {
                this.mElapsedText.setText(GALLERYVIEW_START_TIME);
            }
            // NOTE: maximum value for progress bar will be set in the adapter

            // here we're also starting timers callbacks for playing
            this.mPlayProgressUpdateHandler.removeCallbacks(this.mUpdatePlayProgress);
            this.mPlayProgressUpdateHandler.postDelayed(this.mUpdatePlayProgress, CGalleryConstants.GALLERYLIST_PROGRESS_UPDATE_INTERVAL.value());
        }
    }
}

