package com.keshav.imagelabeler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button camera, detect;
    ImageView image;

    public Bitmap imageDetect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = findViewById(R.id.camera);
        detect = findViewById(R.id.detect);
        image = findViewById(R.id.image);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,100);
            }
        });

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    detectLabel();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "No Image Clicked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 100) {
                imageDetect = (Bitmap) data.getExtras().get("data");
                image.setImageBitmap(imageDetect);
            }
        }catch (Exception e){
            Toast.makeText(this, "No Image Clicked", Toast.LENGTH_SHORT).show();
        }
    }//onActivityResult

    void detectLabel(){
        FirebaseVisionImage imagef = FirebaseVisionImage.fromBitmap(imageDetect);

        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();

        labeler.processImage(imagef).addOnCompleteListener(new OnCompleteListener<List<FirebaseVisionImageLabel>>() {
            @Override
            public void onComplete(@NonNull Task<List<FirebaseVisionImageLabel>> task) {
                String outD = "";
                for(FirebaseVisionImageLabel labeld:task.getResult()){
                    outD+=labeld.getText()+"\t"+labeld.getConfidence()*100+"%\n";
                }
                Toast.makeText(MainActivity.this, ""+outD, Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
