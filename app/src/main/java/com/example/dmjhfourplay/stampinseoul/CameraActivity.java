package com.example.dmjhfourplay.stampinseoul;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgPhoto, btnCapture;
    private Button btnSave, btnExit;
    private EditText edtPola, edtTitle, edtContents;
    private String imageFilepath;
    private Uri phortUri;
    static final int RE = 672;
    public static Bitmap imagesave = null;
    private ArrayList<CameraData> list = new ArrayList<>();
    private String imageFile;
    public static String title;
    private SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        findViewByIdFunction();

        title = getIntent().getStringExtra("title");

        TedPermission.with(getApplicationContext()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        }).setRationaleMessage("카메라 권한이 필요합니다.").setDeniedMessage("거부").setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE).check();

        btnCapture.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnExit.setOnClickListener(this);
    }

    public void toastDispaly(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void findViewByIdFunction() {
        imgPhoto = findViewById(R.id.imgPhoto);
        btnCapture = findViewById(R.id.btnCapture);

        btnSave = findViewById(R.id.btnSave);
        btnExit = findViewById(R.id.btnExit);

        edtPola = findViewById(R.id.edtPola);
        edtTitle = findViewById(R.id.edtTitle);
        edtContents = findViewById(R.id.edtContents);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnCapture) {
            //1번 카메라를 띄워라

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (intent.resolveActivity(getPackageManager()) != null) {

                File file = null;

                //파일명을 만들어야됨 파일명.jpg
                try {

                    file = imageFileSave();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (file != null) {
                    phortUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), file);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, phortUri);

                startActivityForResult(intent, RE);

            }

        } else if (v.getId() == R.id.btnSave) {

            MainActivity.db=MainActivity.dbHelper.getWritableDatabase();

            MainActivity.db.execSQL("UPDATE STAMP_"+LoginActivity.userId+" SET picture='"+imageFilepath
                    +"', content_pola='"+edtPola.getText().toString() //한줄
                    +"', content_title='"+edtTitle.getText().toString() //제목
                    +"', contents='"+edtContents.getText().toString() //내용
                    +"', complete="+1 //성공여부
                    +" WHERE title='"+title+"';");

            MainActivity.db.close();

            finish();

        } else if (v.getId() == R.id.btnExit){

            finish();

        }
    }

    private int exiforToDe(int exifOrientation) {
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
        }
        return 0;

    }
    private Bitmap rotate(Bitmap bitmap, int exifDegres) {

        Matrix matrix = new Matrix();

        matrix.postRotate(exifDegres);

        Bitmap teepre = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return teepre;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RE && resultCode == RESULT_OK){
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilepath);
            ExifInterface exifInterface = null;

            //속성을 체크해야된다.
            try {

                exifInterface = new ExifInterface(imageFilepath);

            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;//방향
            int exifDegres; //각도

            if (exifInterface != null) {

                exifOrientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegres = exiforToDe(exifOrientation);

            } else {
                exifDegres = 0;
            }

            Bitmap bitmapTeep = rotate(bitmap, exifDegres);

            imgPhoto.setImageBitmap(bitmapTeep);
        }
    }

    private File imageFileSave() throws IOException {

        SimpleDateFormat date = new SimpleDateFormat("yyyymmdd_HHmmss");

        // 현재 날짜를 이름에 넣어서 중복을 없애기
        String string = date.format(new Date());
        imageFile = "test_" + string + "_";

        // 외부장치의 디렉토리
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFile, ".jpg", directory);
        imageFilepath = image.getAbsolutePath();

        return image;
    }
}