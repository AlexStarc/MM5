/*
 *            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *                     Version 2, December 2004
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
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details.
 */

package com.sandrstar.android.gallery;
/**
 * This file contains constants to be use by the application like invalid index etc.
 */

public class CGalleryConstants {
    private final Integer value;

    private CGalleryConstants(final Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    public Integer value() {
        return this.value;
    }

    public static final CGalleryConstants GALLERY_INVALID_INDEX = new CGalleryConstants(-1);
    public static final CGalleryConstants GALLERYLIST_PROGRESS_UPDATE_INTERVAL = new CGalleryConstants(1000);
}
