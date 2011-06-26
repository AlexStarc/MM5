/*            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
                    Version 2, December 2004

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>

 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document, and changing it is allowed as long
 as the name is changed.

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

  0. You just DO WHAT THE FUCK YOU WANT TO.
*/

/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */
package com.sandrstar.android.gallery;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

/**
 * @author astarche
 *
 * This is class for encapsulation of OptionsBar functionality including show/hide logic, population of options list, etc.
 */
public class GalleryOptionsBar {
    private static final String TAG = "GalleryOptionsBar";
    private Animation mOptionBarShow = null;
    private Animation mOptionBarHide = null;
    ViewGroup optionsBarView = null;
    private Handler.Callback optionsHandler = null;
    Integer[] optionsItems = null;

    /**
     * Main constructor for OptionsBar
     *
     * @param parent - parent Gallery view of this options bar
     * @param optionBarViewId - options bar view id inside parent GalleryView
     */
    public GalleryOptionsBar(final Activity parent, final Integer optionBarViewId) {
        try {
            this.mOptionBarShow = AnimationUtils.loadAnimation(parent.getApplicationContext(), R.anim.thumbnail_optionbar_show);
            this.mOptionBarHide = AnimationUtils.loadAnimation(parent.getApplicationContext(), R.anim.thumbnail_optionbar_hide);

            this.optionsBarView = (ViewGroup)parent.findViewById(optionBarViewId);
        } catch(final Exception e) {
            Log.e(TAG, "GalleryOptionsBar(): " + e.getClass() + " thrown " + e.getMessage());
        }
    }

    public void showOptionBar() {
        if(null != this.optionsBarView) {
            this.optionsBarView.startAnimation(this.mOptionBarShow);
            this.optionsBarView.setVisibility(View.VISIBLE);
        }
    }

    public void hideOptionBar() {
        if(null != this.optionsBarView) {
            this.optionsBarView.startAnimation(this.mOptionBarHide);
            this.optionsBarView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * @param optionsHandler the optionsHandler to set
     */
    public void setOptionsHandler(final Handler.Callback optionsHandler) {
        this.optionsHandler = optionsHandler;

        // handler was set, so setup messages sending and options array
        // add listeners to all options in separate thread
        final Thread enumerationOptions = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    View buttonOption = null;

                    GalleryOptionsBar.this.optionsItems = new Integer[GalleryOptionsBar.this.optionsBarView.getChildCount()];

                    for(int i = 0; i < GalleryOptionsBar.this.optionsItems.length; i++) {
                        buttonOption = GalleryOptionsBar.this.optionsBarView.getChildAt(i);

                        // Listeners will be added only to Buttons items
                        if(null != buttonOption &&
                           (buttonOption instanceof Button)) {
                            GalleryOptionsBar.this.optionsItems[i] = buttonOption.getId();

                            buttonOption.setOnClickListener(new GalleryOptionsBarClickListener(i));
                        }
                    }
                } catch(final Exception e) {
                    Log.e(TAG, "enumerationOptions(): " + e.getClass() + " thrown " + e.getMessage());
                }
            }
        });

        enumerationOptions.start();
    }

    /**
     * @return the optionsHandler
     */
    public Handler.Callback getOptionsHandler() {
        return this.optionsHandler;
    }

    public void handleOptionSelection(final Integer index) {
        // find id and send proper message to options owner
        final Message msg = Message.obtain();

        msg.arg1 = this.optionsItems[index];

        this.optionsHandler.handleMessage(msg);
    }

    class GalleryOptionsBarClickListener implements View.OnClickListener {
        private Integer index = -1;

        public GalleryOptionsBarClickListener(final Integer index) {
            this.index = index;
        }

        @Override
        public void onClick(final View v) {
            handleOptionSelection(this.index);
        }
    }
}
