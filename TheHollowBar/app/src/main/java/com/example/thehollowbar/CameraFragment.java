package com.example.thehollowbar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.Manifest;

public class CameraFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;

    private ImageView imageView;
    private Button btnTakePhoto;
    private Button btnSavePhoto;

    private String currentPhotoPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Vincular los elementos del diseño
        imageView = view.findViewById(R.id.imageView);
        btnTakePhoto = view.findViewById(R.id.btn_take_photo);
        btnSavePhoto = view.findViewById(R.id.btn_save_photo);

        // Verificar permisos
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CAMERA_PERMISSION);
        }

        // Configurar evento para tomar una foto
        btnTakePhoto.setOnClickListener(v -> openCamera());

        // Evento para guardar la foto
        btnSavePhoto.setOnClickListener(v -> savePhotoToGallery());
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Asegurarse de que hay una actividad de cámara disponible
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Crear un archivo donde se almacenará la foto
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getActivity(), "Error al crear el archivo de imagen", Toast.LENGTH_SHORT).show();
            }

            // Continuar solo si se creó el archivo con éxito
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.thehollowbar.fileprovider", // Cambiar al paquete de tu app
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Crear un nombre único para el archivo de imagen
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(null); // Directorio específico de la app
        File image = File.createTempFile(
                imageFileName,  /* prefijo */
                ".jpg",         /* sufijo */
                storageDir      /* directorio */
        );

        // Guardar la ruta para usarla más tarde
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == requireActivity().RESULT_OK) {
            // Verifica si la foto existe en la ruta
            File imgFile = new File(currentPhotoPath);
            Log.d("CameraFragment", "currentPhotoPath: " + currentPhotoPath);  // Añade un log para verificar la ruta
            if (imgFile.exists()) {
                imageView.setImageURI(Uri.fromFile(imgFile));
            } else {
                Toast.makeText(getActivity(), "Imagen no encontrada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePhotoToGallery() {
        // Guardar en la galería
        try {
            File imgFile = new File(currentPhotoPath);
            if (imgFile.exists()) {
                // Crear valores de contenido para insertar en MediaStore
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "Foto_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
                values.put(MediaStore.Images.Media.DESCRIPTION, "Foto tomada con la aplicación");
                values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

                // Insertar la imagen en MediaStore (galería)
                Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                // Abrir un OutputStream para escribir la imagen en la galería
                try (OutputStream outputStream = getActivity().getContentResolver().openOutputStream(uri)) {
                    // Decodificar el archivo de la imagen en un Bitmap
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(imgFile));
                    // Escribir el Bitmap en el OutputStream
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                }

                // Notificar al sistema para que la galería se actualice
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(currentPhotoPath))));

                Toast.makeText(getActivity(), "Foto guardada en la galería", Toast.LENGTH_SHORT).show();

                // Resetear el ImageView después de guardar la foto
                imageView.setImageResource(0); // O imageView.setImageURI(null);
            } else {
                Toast.makeText(getActivity(), "No se pudo encontrar la imagen", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    // Manejar la respuesta de los permisos en tiempo de ejecución
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();  // Permiso concedido, abre la cámara
                } else {
                    Toast.makeText(getActivity(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    savePhotoToGallery(); // Permiso concedido, guarda la foto
                } else {
                    Toast.makeText(getActivity(), "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
