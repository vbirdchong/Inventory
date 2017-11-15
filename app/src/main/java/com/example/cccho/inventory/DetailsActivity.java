package com.example.cccho.inventory;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cccho.inventory.data.ProductContract;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final static String LOG_TAG = DetailsActivity.class.getSimpleName();

    private final static int EXISTING_PRODUCT_LOADER = 0;

    private static final String IMAGE_UNSPECIFIED = "image/*";

    private Uri mCurrentUri;

    private EditText mNameEditText;

    private EditText mPriceEditText;

    private EditText mQuantityEditText;

    private EditText mSupplierNameEditText;

    private EditText mSupplierEmailAddressEditText;

    private ImageButton mDecreaseQuantity;

    private ImageButton mIncreaseQuantity;

    private Button mPictureButton;

    private ImageView mPictureShow;

    private Uri mPictureUri;

    private final static int CODE_FOR_PERMISSION = 1;

    private final static int REQUEST_PICK_PHOTO = 1;

    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        mNameEditText = (EditText) findViewById(R.id.product_name_edit);
        mPriceEditText = (EditText) findViewById(R.id.price_edit);
        mQuantityEditText = (EditText) findViewById(R.id.quantity_edit);
        mDecreaseQuantity = (ImageButton) findViewById(R.id.decrease_quantity);
        mIncreaseQuantity = (ImageButton) findViewById(R.id.increase_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.supplier_name_edit);
        mSupplierEmailAddressEditText = (EditText) findViewById(R.id.supplier_email_edit);
        mPictureButton = (Button) findViewById(R.id.picture_add_button);
        mPictureShow = (ImageView) findViewById(R.id.picture_view);


        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierEmailAddressEditText.setOnTouchListener(mTouchListener);
        mPictureButton.setOnTouchListener(mTouchListener);

        if (mCurrentUri == null) {
            // add a new product
            setTitle(getString(R.string.detail_activity_title_new_product));

            // Hind the "Delete" menu option when adding a new product
            invalidateOptionsMenu();

            mPictureShow.setImageResource(R.drawable.ic_none);
        } else {
            // Edit the product
            setTitle(getString(R.string.detail_activity_title_edit_product));

            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseQuantity();
                mProductHasChanged = true;
            }
        });

        mIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseQuantity();
                mProductHasChanged = true;
            }
        });

        mPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPictureInfo();
                mProductHasChanged = true;
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL_ADDRESS,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PICTURE};

        return new CursorLoader(this,
                mCurrentUri,
                projection,
                null,
                null,
                null);
    }

    /**
     * 加载数据库信息进行显示
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToNext()) {
            int nameColumnIdx = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIdx = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIdx = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIdx = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierEmailColumnIdx = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL_ADDRESS);
            int pictureUriColumnIdx = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PICTURE);

            String name = data.getString(nameColumnIdx);
            int price = data.getInt(priceColumnIdx);
            int quantity = data.getInt(quantityColumnIdx);
            String supplierName = data.getString(supplierNameColumnIdx);
            String supplierEmail = data.getString(supplierEmailColumnIdx);
            String pictureUri = data.getString(pictureUriColumnIdx);

            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierEmailAddressEditText.setText(supplierEmail);
            mPictureUri = Uri.parse(pictureUri);
            mPictureShow.setImageURI(mPictureUri);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierEmailAddressEditText.setText("");
        mPictureShow.setImageResource(R.drawable.ic_none);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_save:
                saveProduct();
                return true;
            case R.id.action_order:
                orderMore();
                return true;
            case android.R.id.home:
                if(!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked the "Discard" button, close the current activity
                        finish();
                    }
                };
        // show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void saveProduct() {
        String strName = mNameEditText.getText().toString().trim();
        String strPrice = mPriceEditText.getText().toString().trim();
        String strQuantity = mQuantityEditText.getText().toString().trim();
        String strSuppilerName = mSupplierNameEditText.getText().toString().trim();
        String strSuppilerEmail = mSupplierEmailAddressEditText.getText().toString().trim();

        if (mCurrentUri == null
                && TextUtils.isEmpty(strName)
                && TextUtils.isEmpty(strSuppilerName)
                && TextUtils.isEmpty(strSuppilerEmail)
                || (mPictureUri == null)) {
            Toast.makeText(this, getString(R.string.input_valid_info), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(strPrice)) {
            strPrice = "0";
        }

        if (TextUtils.isEmpty(strQuantity)) {
            strQuantity = "0";
        }

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, strName);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, Integer.parseInt(strPrice));
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(strQuantity));
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, strSuppilerName);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL_ADDRESS, strSuppilerEmail);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PICTURE, mPictureUri.toString());

        if (mCurrentUri == null) {
            // add a new product
            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            // update the product
            int rowsUpdate = getContentResolver().update(mCurrentUri, values, null, null);
            if (rowsUpdate == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful), Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    private void deleteProduct() {
        if (mCurrentUri != null) {
            int rowsOfDelete = getContentResolver().delete(mCurrentUri, null, null);
            if (rowsOfDelete != 1) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful), Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUnsavedChangesDialog (
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void decreaseQuantity() {
        String strQuantity = mQuantityEditText.getText().toString();
        if (strQuantity.isEmpty() || strQuantity.equals("0")) {
            return;
        } else {
            mQuantityEditText.setText(String.valueOf(Integer.parseInt(strQuantity) - 1));
        }
    }

    private void increaseQuantity() {
        String strQuantity = mQuantityEditText.getText().toString();
        if (strQuantity.isEmpty()) {
            return;
        } else {
            mQuantityEditText.setText(String.valueOf(Integer.parseInt(strQuantity) + 1));
        }
    }

    private void getPictureInfo() {
        // solution come from: https://stackoverflow.com/questions/25414352/how-to-persist-permission-in-android-api-19-kitkat/29588566#29588566

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    CODE_FOR_PERMISSION);
            return;
        }

        startGetImageThread();
    }

    private void startGetImageThread() {

        Log.i(LOG_TAG, "startGetImageThread");
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType(IMAGE_UNSPECIFIED);
        startActivityForResult(
                Intent.createChooser(intent, getResources().getString(R.string.form_pick_photos)),
                REQUEST_PICK_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.i(LOG_TAG, "onRequestPermissionsResult requestCode:" + requestCode);
        if (requestCode == CODE_FOR_PERMISSION){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGetImageThread();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_PHOTO && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mPictureUri = data.getData();
                Log.i(LOG_TAG, "Picture Uri:" + mPictureUri);
                mPictureShow.setImageURI(mPictureUri);
                mPictureShow.invalidate();
            }
        }
    }

    private void orderMore() {
        Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:" + mSupplierEmailAddressEditText.getText().toString().trim()));
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_subject));
        String bodyMessage = getResources().getString(R.string.email_main_body) + mNameEditText.getText().toString().trim() + ".";
        intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
        startActivity(intent);
    }
}
