package com.example.adityacomputers.contentproviderdemo;

import android.app.AlertDialog;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST_WRITE_CONTACTS =10 ;
    EditText etname,etnumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initilize the resources
        etname=(EditText)findViewById(R.id.editText);
        etnumber=(EditText)findViewById(R.id.editText2);
        Button btnsave=(Button)findViewById(R.id.button);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //read the name and phone number
                String name = etname.getText().toString();
                String phoneno = etnumber.getText().toString();
                //check permission is there to write contact if there then call add_new_contact method
                if(isPermissionthere()) {
                    add_new_contact(name, phoneno);
                }
                //otherwise request for write contact permission
                else
                {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.WRITE_CONTACTS},MY_PERMISSION_REQUEST_WRITE_CONTACTS);

                }
            }
        });

    }
    //method to add new contact using contentprovider and contentresolver
    public void add_new_contact(String name,String phoneno)
    {

        ArrayList<ContentProviderOperation> ops=new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE,"praveen.tilavalli931@gmail.com")
        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME,"com.google")
        .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
        .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,name)
        .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
        .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,phoneno)
        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE)
        .build());
        try
        {
            ContentResolver cr=getContentResolver();
            cr.applyBatch(ContactsContract.AUTHORITY,ops);
            Toast.makeText(this,"Contact has been added successfully with name="+name+" and phone number="+phoneno,Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();}


    }
    //method to check permission is there to write the contact
    public boolean isPermissionthere()
    {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CONTACTS)!= PackageManager.PERMISSION_GRANTED)
            return false;
        else
            return true;
    }

    //method overriden for processing request of write contact
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //check the request code and then check permssion granted if granted add new contact otherwise display message
        switch(requestCode)
        {
            case MY_PERMISSION_REQUEST_WRITE_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String name = etname.getText().toString();
                    String phoneno = etnumber.getText().toString();
                    add_new_contact(name,phoneno);

                }
                else {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                            android.Manifest.permission.WRITE_CONTACTS)) {
                        android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(this);
                        builder.setTitle("permission denied");
                        builder.setMessage("Write contact permission denied please goto settings and change it");
                        builder.show();

                }
                }
                break;
        }
        }
}
