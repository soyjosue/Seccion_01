package com.example.seccion_01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ThirdActivity extends AppCompatActivity {

    private EditText editTextPhone;
    private EditText editTextWeb;
    private EditText editTextCorreo;
    private ImageButton imgBtnPhone;
    private ImageButton imgBtnWeb;
    private ImageButton imgBtnCamera;
    private ImageButton imgBtnCorreo;
    private Button btnContacto;

    private final int PHONE_CALL_CODE = 100;
    private final int PICTURE_FROM_CAMERA = 50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        // Activar flecha e ir atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextWeb = (EditText) findViewById(R.id.editTextWeb);
        editTextCorreo = (EditText) findViewById(R.id.editTextCorreo);
        imgBtnPhone = (ImageButton) findViewById(R.id.imageButtonPhone);
        imgBtnCamera = (ImageButton) findViewById(R.id.imageButtonCamera);
        imgBtnWeb = (ImageButton) findViewById(R.id.imageButtonWeb);
        imgBtnCorreo = (ImageButton) findViewById(R.id.imageButtonCorreo);
        btnContacto = (Button) findViewById(R.id.buttonContactos);

        imgBtnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = editTextPhone.getText().toString();
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    // Comprobar version act ual de android que estamos corriendo
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                         NewerVersions(phoneNumber);
                    } else {
                        OlderVersions(phoneNumber);
                    }
                } else {
                    Toast.makeText(ThirdActivity.this, "Insert a phone number.", Toast.LENGTH_SHORT).show();
                }
            }

            private void OlderVersions(String phoneNumber) {
                Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneNumber));
                if (CheckPermission(Manifest.permission.CALL_PHONE)) {
                    startActivity(intentCall);
                } else {
                    Toast.makeText(ThirdActivity.this, "You decline the access.", Toast.LENGTH_SHORT).show();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            private void NewerVersions(String phoneNumber) {

                // Comprobar si ha aceptado, no ha aceptado, o nunca se le ha preguntado
                if(CheckPermission(Manifest.permission.CALL_PHONE)) {
                    // Ha aceptado
                    Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneNumber));
                    startActivity(i);
                } else {
                    // HA denegado o es la primera vez que se le pregunta
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)){
                        // No se le ha preguntado aún
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PHONE_CALL_CODE);
                    } else {
                        // Ha denegado
                        Toast.makeText(ThirdActivity.this, "Please, enable the permission", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        i.setData(Uri.parse("package:" + getPackageName()));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(i);
                    }
                }
            }

        });

        // Boton para la direccion web
        imgBtnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = editTextWeb.getText().toString();
                if (url != null && !url.isEmpty()) {
                    Intent intentWeb = new Intent();
                    intentWeb.setAction(Intent.ACTION_VIEW);
                    intentWeb.setData(Uri.parse("http://"+url));

                    startActivity(intentWeb);
                }
            }
        });

        // Boton para correo rapido
        imgBtnCorreo.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View v) {
                String email = editTextCorreo.getText().toString();
                // EMAIL RAPIDO
                Intent intentMailTO = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"+email));

                // EMAIL COMPLETO
                Intent intentMail = new Intent(Intent.ACTION_SEND, Uri.parse(email));
                intentMail.setType("plain/text");
                intentMail.putExtra(Intent.EXTRA_SUBJECT, "Mail's title");
                intentMail.putExtra(Intent.EXTRA_TEXT, "Hi there, I love MyForm app, but... ");
                intentMail.putExtra(Intent.EXTRA_EMAIL, new String[] {"fernando@gmail.com", "antonio@gmail.com"});
                // startActivity(Intent.createChooser(intentMail, "Elige cliente de correo"));

                // Telefono 2, sin permisos requeridos
                Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:666111222"));
//                startActivity(intentPhone);


            }
        });

        // Boton para ver los contactos
        btnContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentContact = new Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people"));
                startActivity(intentContact);
            }
        });

        // Boton para abrir camara
        imgBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCamara = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intentCamara, PICTURE_FROM_CAMERA);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PICTURE_FROM_CAMERA:
                    if (resultCode == Activity.RESULT_OK) {
                        String result = data.toUri(0);
                        Toast.makeText(this, "Result:"+result, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "There was an error with the picture, try again.", Toast.LENGTH_LONG).show();
                    }
                break;
            default: super.onActivityResult(requestCode, resultCode, data);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Estamos en el caso del teléfono
        switch (requestCode) {
            case PHONE_CALL_CODE:

                String permission = permissions[0];
                int result = grantResults[0];

                if (permission.equals(Manifest.permission.CALL_PHONE)) {

                    // Comprobar si ha sido aceptado o denegado la petición de permiso
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        // Concedió su permiso
                        String phoneNumber = editTextPhone.getText().toString();
                        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneNumber));
                        startActivity(intentCall);
                    } else {
                        // no concedió su permiso
                        Toast.makeText(ThirdActivity.this, "You declined the access", Toast.LENGTH_LONG).show();
                    }
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private boolean CheckPermission(String permission) {
        int result = this.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

}