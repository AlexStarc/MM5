/**
 * 
 */
package com.teleca.mm5.gallery;

import java.io.FileInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

/**
 * @author astarche
 *
 */
public class ContentImageLoader implements Runnable {
    private View parent;
    private ImageView iv;
    private String contentPath;
    private Handler callerHdlr;
    private Bitmap bm;
    private Integer nItemId;

    /**
     * Creates an ImageLoader object with the specified parameters.
     * @param iv the image view to load the image into
     * @param urlString the url string for the image
     * @param parent the parent view to refresh when the image has loaded
     */
    public ContentImageLoader(ImageView iv,
                              String contentPath,
                              View parent,
                              Integer nItemId,
                              Handler callerHdlr) {
        this.setParent(parent);
        this.setIv(iv);
        this.contentPath = contentPath;
        this.callerHdlr = callerHdlr;
        this.setnItemId(nItemId);
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            Message callbackMsg = Message.obtain();

            callbackMsg.obj = this;
            callbackMsg.setTarget(callerHdlr);
            bm = (BitmapFactory.decodeStream( new FileInputStream( contentPath ) ));
            callbackMsg.sendToTarget();
        } catch (Exception e) {}
    }

    /**
     * @param iv the iv to set
     */
    public void setIv(ImageView iv) {
        this.iv = iv;
    }

    /**
     * @return the iv
     */
    public ImageView getIv() {
        return iv;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(View parent) {
        this.parent = parent;
    }

    /**
     * @return the parent
     */
    public View getParent() {
        return parent;
    }

    /**
     * @param bm the bm to set
     */
    public void setBm(Bitmap bm) {
        this.bm = bm;
    }

    /**
     * @return the bm
     */
    public Bitmap getBm() {
        return bm;
    }

    /**
     * @param nItemId the nItemId to set
     */
    public void setnItemId(Integer nItemId) {
        this.nItemId = nItemId;
    }

    /**
     * @return the nItemId
     */
    public Integer getnItemId() {
        return nItemId;
    }

}
