/**
 * This is adapter for ListView view of ContentGallery
 */
package com.teleca.mm5.gallery;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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
    private Integer elementLayout = 0;
    private final String TAG = "ListViewCursorAdapter";
    private OnClickListener playButtonListener = null;

    /**
     * ListViewCursorAdapter - main constructor
     *
     * @param context - main context;
     * @param c - cursor to be used for adapter;
     * @param autoRequery - boolean flag to allow auto-requery;
     * @param elementLayoutId - layout id to be used for single element;
     */
    public ListViewCursorAdapter(Context context, Cursor c, boolean autoRequery, int elementLayoutId, OnClickListener clickListener) {
        super(context, c, autoRequery);
        this.context = context;
        elementLayout = elementLayoutId;
        playButtonListener = clickListener;
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
        } catch(Exception e) {
            Log.e(TAG, "bindView(): " + e.getClass() + " thrown " + e.getMessage());
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View elementView = null;

        try {
            final LayoutInflater inflater = LayoutInflater.from(context);
            elementView = inflater.inflate(elementLayout, parent, false);
            int fileNameCol = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
            String fileName = cursor.getString(fileNameCol);
            TextView fileNameView = (TextView)elementView.findViewById(R.id.listview_item_text);
            Button playButton = (Button)elementView.findViewById(R.id.listview_item_play);

            if(null != fileNameView) {
                fileNameView.setText(fileName);
            }

            if(null != playButton) {
                playButton.setOnClickListener(playButtonListener);
            }
        } catch(Exception e) {
            Log.e(TAG, "newView(): " + e.getClass() + " thrown " + e.getMessage());
        }

        return elementView;
    }

}
