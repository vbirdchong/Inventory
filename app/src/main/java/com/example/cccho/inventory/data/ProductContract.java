package com.example.cccho.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by cccho on 2017/11/13.
 */

public final class ProductContract {

    private ProductContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCTS = "products";

    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        // name of database table for product
        public final static String TABLE_NAME = "products";

        // ID number for pets
        // TYPE: INTEGER
        public final static String _ID = BaseColumns._ID;

        // name of the product
        // TYPE: TEXT
        public final static String COLUMN_PRODUCT_NAME = "name";

        // price of the product
        // TYPE: INTEGER
        public final static String COLUMN_PRODUCT_PRICE = "price";

        // quantity of the product
        // TYPE: INTEGER
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        // supplier name of the product
        // TYPE: TEXT
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "supplierName";

        // supplier email address of the produce
        // TYPE: TEXT
        public final static String COLUMN_PRODUCT_SUPPLIER_EMAIL_ADDRESS = "supplierEmailAddress";

        // image of the product
        // TYPE: TEXT
        public final static String COLUMN_PRODUCT_PICTURE = "picture";
    }
}
