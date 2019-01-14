package fr.uga.miashs.chooseevent;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.IOException;

public class ChoosePicture extends AppCompatActivity {

    ImageView imageView;
    static final int PICK_PICTURE_REQUEST = 1; // The request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_picture);

        // Call method when the AddContact button is pressed
        onClickAddContact();

        // Call method when the AddEvent button is pressed
        onClickAddEvents();

        //Initialization of the different components
        imageView = findViewById(R.id.imgV);
        // Call method to create the intent and choose and image from the gallery
        pickPicture();
    }

    /**
     * Add Events button handler
     * Code that will be  executed after user presses button to associate an event to the photo
     */
    private void onClickAddEvents() {
        final ImageButton btnAddEvents = findViewById(R.id.btnAddEvents);
        btnAddEvents.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ChoosePicture.this, ChooseEvent.class);
                ChoosePicture.this.startActivity(intent);
            }
        });
    }


    /**
     * Add Contacts button handler
     * Code that will be  executed after user presses button to associate a contact to the photo
     */
    private void onClickAddContact() {
        final ImageButton btnAddContact = findViewById(R.id.btnAddContact);
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ChoosePicture.this, ChooseContact.class);
                ChoosePicture.this.startActivity(intent);
            }
        });
    }

    /**
     * Action to access the gallery using an intent and PICK_ACTION
     */
    public void pickPicture(){
        // open the gallery and choose a photo

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture") , PICK_PICTURE_REQUEST );
    }


    /**
     * Action to choose an image from the Gallery and display it in the activity (the main activity)
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PICTURE_REQUEST){
            if (resultCode == RESULT_OK) {
                if(null !=data && null != data.getData()){
                    Uri selectedImage = data.getData();
                    String[] projection = { MediaStore.Images.Media.DATA };


                    Cursor cursor = getContentResolver().query(selectedImage,
                            projection, null, null, null);

                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String picturePath = cursor.getString(columnIndex);

                    cursor.close();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 500, 600, false));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * Action given by the teacher but could not make it work in this project! To come on later
     */
    public void displayPicture(Uri img){
        ImageView iv = findViewById(R.id.imgV);

        Cursor c = MediaStore.Images.Media.query(this.getContentResolver(),img,new String[]{MediaStore.Images.Media._ID});
        c.moveToFirst();

        int pickId = c.getInt(0);

        BitmapFactory.Options opt = new BitmapFactory.Options();
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(this.getContentResolver(),pickId,MediaStore.Images.Thumbnails.MINI_KIND,opt);
        iv.setImageBitmap(bitmap);
        c.close();
    }



}
