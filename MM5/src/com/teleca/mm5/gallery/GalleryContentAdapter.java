/**
 * Adapter to be used
 */
package com.teleca.mm5.gallery;

import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

@ClassPreamble (
                author = "Alexander Starchenko",
                description = "class to be used by view as cursor to ensure better visual performance"
)
public class GalleryContentAdapter extends BaseAdapter {
    private Cursor contentCursor;

    public GalleryContentAdapter(Cursor contentCursor) {
        this.setContentCursor(contentCursor);
    }

    @Override
    public int getCount() {
        Integer nCount = 0;

        if(null != contentCursor) {
            nCount = contentCursor.getCount();
        }

        return nCount;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param contentCursor the contentCursor to set
     */
    public void setContentCursor(Cursor contentCursor) {
        this.contentCursor = contentCursor;
    }

    /**
     * @return the contentCursor
     */
    public Cursor getContentCursor() {
        return contentCursor;
    }

}
