package com.example.proqr;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proqr.util.Constant;
import com.google.zxing.activity.CaptureActivity;
import com.google.zxing.util.QrCodeGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends Activity implements View.OnClickListener {
    Button btnQrCode; // 扫码
    TextView tvResult; // 结果


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btnQrCode = (Button) findViewById(R.id.btn_qrcode);
        btnQrCode.setOnClickListener(this);

        tvResult = (TextView) findViewById(R.id.txt_result);
    }

    // 开始扫码
    private void startQrCode() {
        // 申请相机权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .CAMERA)) {
                Toast.makeText(this, "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.REQ_PERM_CAMERA);
            return;
        }

        // 二维码扫码
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, Constant.REQ_QR_CODE);
    }


    @Override
    public void onClick(View view) {
                Log.i("提示：","我曹，怎么回事");
        startQrCode();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            final String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
            //将扫描出的信息显示出来
            tvResult.setText(scanResult);

            /**
             * 将扫描出的信息发到服务器
             * 在数据库中查询信息
             */
            new Thread() {
                @Override
                public void run() {
                    try {
                        checkData(scanResult);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }













    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.REQ_PERM_CAMERA:
                // 摄像头权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    Toast.makeText(MainActivity.this, "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show();
                }
                break;
            case Constant.REQ_PERM_EXTERNAL_STORAGE:
                // 文件读写权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    Toast.makeText(MainActivity.this, "请至权限中心打开本应用的文件读写权限", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * 向指定服务器发送socket连接
     * @param command
     * @throws IOException
     */
    private void checkData(String command) throws IOException {
        //1.创建客户端Socket，指定服务器地址和端口
        Socket socket = new Socket("192.168.1.113", 12348);
        //2.获取输出流，向服务器端发送信息
//        OutputStream os = socket.getOutputStream();//字节输出流
//        PrintWriter pw = new PrintWriter(os);//将输出流包装为打印流
        PrintStream ps = new PrintStream(socket.getOutputStream());
        //获取客户端的IP地址
        ps.println(command);


        //下面等待接收服务器发来的信息
        InputStream is=null;
        InputStreamReader isr=null;
        BufferedReader br=null;
        OutputStream os=null;

        is = socket.getInputStream();     //获取输入流
        isr = new InputStreamReader(is,"UTF-8");
        br = new BufferedReader(isr);
        String info1 = null;
        String info2 = null;
        String info3 = null;
        String info4 = null;
        info1=br.readLine();//读取name
        info2=br.readLine();//读取math
        info3=br.readLine();//读取english
        info4=br.readLine();//读取chinese

        Log.i("信息","你们猜！我读到了什么？");
        Log.i("服务器返回的信息","信息就是："+info1);
        Log.i("服务器返回的信息","信息就是："+info2);
        Log.i("服务器返回的信息","信息就是："+info3);
        Log.i("服务器返回的信息","信息就是："+info4);
        //br.close();is.close();isr.close();
        //ps.flush();
        //ps.close();
        if("manager".equals(info1)){
            //跳转管理员页面
            Intent intent = new Intent(MainActivity.this, Manager.class);
            startActivity(intent);
        } else if(!"NO".equals(info1)){
            Intent intent = new Intent(MainActivity.this, sysPage.class);
            Bundle bundle = new Bundle();
            bundle.putCharSequence("id",command);
            bundle.putCharSequence("name",info1);
            bundle.putCharSequence("math",info2);
            bundle.putCharSequence("english",info3);
            bundle.putCharSequence("chinese",info4);
            intent.putExtras(bundle);
            startActivity(intent);
        } else{
            Looper.prepare();
            Toast.makeText(MainActivity.this,"您没有权限登录，请重新扫码！",Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
        //至此，线程终结



    }

}
