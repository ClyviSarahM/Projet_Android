package fr.uga.miashs.chooseevent;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ChooseContact extends AppCompatActivity {

    static final int PICK_CONTACT_REQUEST = 1; // The request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_contact);
        pickContact();
    }


    private void pickContact() {
        Intent pickContactIntent = new Intent(
                Intent.ACTION_PICK,
                Uri.parse("content://contacts"));

        pickContactIntent.setType(ContactsContract.Contacts.CONTENT_TYPE); // show user
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) // Make sure the request was successful

            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();// The user picked a contact.

                // Defining a projection to say what info we want to retrieve from the URI
                String[] projection = { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                // Do something with the contact here
                Cursor cursor = getApplicationContext().getContentResolver().query(contactData, projection,
                        null, null, null);

                cursor.moveToFirst();

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                // retrieve the name
                String name = cursor.getString(nameColumnIndex);

                // Retrieving the text view
                TextView text = findViewById(R.id.tVName);

                // Display the same
                //text.setText(name);
                if (text.getText() == ""){
                    text.setText(name);
                }else {
                    text.append("\n" + name);
                }
                cursor.close();
            }
        }
    }


    // livedata : DAO : on a un observer (la methode on change a implémenter) sur people async

