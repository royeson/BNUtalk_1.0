package com.example.huangtianyous.hello;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by huangtianyous on 2016/4/20.
 */
public class personalInfo extends Activity {
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int CHOOSE_PHOTO = 3;
    public static final int PHOTO_SIZE =300;
    private ImageView take_photo;
    private Uri imageUri;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info);
        take_photo = (ImageView) findViewById(R.id.take_photo);
        take_photo.setOnClickListener(photoClick);
    }

    View.OnClickListener photoClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {//设置背景变暗
            getPopupWindow();
            popupWindow.showAtLocation(findViewById(android.R.id.content).
                    getRootView(), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.87f;
            getWindow().setAttributes(lp);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1.0f;
                    getWindow().setAttributes(lp);
                }
            });

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch ((requestCode)) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Intent intent = getPhotoIntent(imageUri);
                    startActivityForResult(intent, CROP_PHOTO);
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream
                                (getContentResolver().openInputStream(imageUri));

                        Bitmap bitmap2 = toRoundBitmap(bitmap);
                     /*   saveBitmap(Environment.getExternalStorageDirectory() + "/crop_"
                            + System.currentTimeMillis() + ".png",bitmap2);*///saveBitmap可以返回文件名，由此找到本地保存的图片
                        take_photo.setImageBitmap(bitmap2);//如果要上传图片，应当上传bitmap2
                    }
                   catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                      imageUri = handleImageOnKitKat(data);

                    } else {
                        imageUri=handleImageBeforeKitKat(data);
                    }
                    Intent intent = getPhotoIntent(imageUri);
                    startActivityForResult(intent, CROP_PHOTO);
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private Uri handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(
                    uri.getAuthority()
            )) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.
                        Media.EXTERNAL_CONTENT_URI, selection);

            } else if ("com.android.providers.downloads.documents".equals(
                    uri.getAuthority()
            )) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        }
        BitmapFactory.Options option = new BitmapFactory.Options();
        // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图
        option.inSampleSize = 1;
        Bitmap bm = BitmapFactory.decodeFile(imagePath, option);
        String S= saveBitmap(Environment.getExternalStorageDirectory() + "/crop_"
                + System.currentTimeMillis() + ".png",bm);

        Uri uri_new = Uri.fromFile(new File(S));
        return uri_new;

    }

    private Uri handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        BitmapFactory.Options option = new BitmapFactory.Options();
        // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图
        option.inSampleSize = 1;
        String imagepath = getImagePath(uri, null);

        Bitmap bm = BitmapFactory.decodeFile(imagepath, option);
        String S= saveBitmap(Environment.getExternalStorageDirectory() + "/crop_"
                + "temp.png",bm);

        Uri uri_new = Uri.fromFile(new File(S));
        return uri_new;

  //      displayImage(imagepath);
    }

    public String saveBitmap(String fileName, Bitmap mBitmap) {
        File f = new File(fileName);
        FileOutputStream fOut = null;
        try {
            f.createNewFile();
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fOut.close();
          //      Toast.makeText(this, "save success", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;

    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public Bitmap toRoundBitmap(Bitmap bitmap) {
        //圆形图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int r ;
        //取最短边做边长
        if(width > height) {
            r = height;
        } else {
            r = width;
        }
        Bitmap backgroundBmp = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(backgroundBmp);       //new一个Canvas，在backgroundBmp上画图
        Paint paint = new Paint();
        //设置边缘光滑，去掉锯齿
        paint.setAntiAlias(true);
        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, width, height);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r/2, r/2, paint);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, null, rect, paint);
        return backgroundBmp;
    }
    public static Intent getPhotoIntent(Uri ima) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(ima, "image/*");
        intent.putExtra("outputX", PHOTO_SIZE);
        intent.putExtra("outputY", PHOTO_SIZE);
        intent.putExtra("maxOutputX",400);
        intent.putExtra("maxOutputY",400);
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("scale", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ima);
        return intent;
    }
/*
    private void displayImage(String imagePath){
        if(imagePath!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            take_photo.setImageBitmap(bitmap);
        }else{
            Toast.makeText(this,"failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
*/

    protected void initPopupWindow(){
    View popupWindow_view=getLayoutInflater().inflate(R.layout.btn_photo, null, false);
        popupWindow=new PopupWindow(popupWindow_view,500,200,true);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(R.style.takePhoto);
        popupWindow_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                return false;
            }
        });
        Button pic=(Button)popupWindow_view.findViewById(R.id.picBtn);
        Button photo=(Button)popupWindow_view.findViewById(R.id.photoBtn);
        Button cancel=(Button)popupWindow_view.findViewById(R.id.cancelBtn);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputImage = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);

                popupWindow.dismiss();
            }
        });
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_PHOTO);

                popupWindow.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }
    private void getPopupWindow(){
        if(null!=popupWindow){
            popupWindow.dismiss();
            return;
        }else{
            initPopupWindow();
        }
    }
}



