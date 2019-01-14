package fr.uga.miashs.chooseevent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    static final int PICK_PICTURE_REQUEST = 1; // The request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Action to access the gallery using an intent and PICK_ACTION
     */
    public void onClickAddPicture(){
        // open the gallery and choose a photo
        final ImageButton btnAddPicture = (ImageButton)findViewById(R.id.mainAddBtn);
        btnAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture") , PICK_PICTURE_REQUEST );
            }
        });
    }
}
