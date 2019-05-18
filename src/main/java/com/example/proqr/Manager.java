package com.example.proqr;

import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class Manager extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        Button btnpost = (Button)findViewById(R.id.btnpost);
        //---------------绑定UI组件--------------------------------
        final EditText editTextId = (EditText)findViewById(R.id.editId);
        final EditText editTextName = (EditText)findViewById(R.id.editNa);
        final EditText editTextMath = (EditText)findViewById(R.id.editMa);
        final EditText editTextEnglish = (EditText)findViewById(R.id.editEn);
        final EditText editTextChinese = (EditText)findViewById(R.id.editCh);

        //-------------------------------------------------------------

        //---------------设置post监听事件---------------------------
        btnpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id,name,math,english,chinese;
                //获取输入框的值
                id = editTextId.getText().toString();
                name = editTextName.getText().toString();
                math = editTextMath.getText().toString();
                english = editTextEnglish.getText().toString();
                chinese = editTextChinese.getText().toString();
                if(!id.isEmpty()&& !name.isEmpty() && !math.isEmpty() && !english.isEmpty() && !chinese.isEmpty()){
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                insertData(id,name,math,english,chinese);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }else Toast.makeText(Manager.this,"请完善信息后录入！",Toast.LENGTH_SHORT).show();

            }
        });
    }


    /**
     * 向指定服务器发送socket连接
     * @param id
     * @param math
     * @param english
     * @param chinese
     * @throws IOException
     */
    private void insertData(String id,String name,String math,String english,String chinese)
            throws IOException {
        //1.创建客户端Socket，指定服务器地址和端口
        Socket socket = new Socket("192.168.1.113", 6666);
        //2.获取输出流，向服务器端发送信息
//        OutputStream os = socket.getOutputStream();//字节输出流
//        PrintWriter pw = new PrintWriter(os);//将输出流包装为打印流
        PrintStream ps = new PrintStream(socket.getOutputStream());
        //依次发送数据
        ps.println(id);
        ps.println(name);
        ps.println(math);
        ps.println(english);
        ps.println(chinese);


        //下面等待接收服务器发来的信息
        InputStream is=null;
        InputStreamReader isr=null;
        BufferedReader br=null;
        OutputStream os=null;

        is = socket.getInputStream();     //获取输入流
        isr = new InputStreamReader(is,"UTF-8");
        br = new BufferedReader(isr);
        String status = null;
        status=br.readLine();//读取成绩录入状态

        Log.i("信息","你们猜！我读到了什么？");
        Log.i("服务器返回的信息","信息就是："+status);
        if(!"error".equals(status)){
            Looper.prepare();
            Toast.makeText(Manager.this,"成绩录入成功！",Toast.LENGTH_SHORT).show();
            Looper.loop();
        } else{
            Looper.prepare();
            Toast.makeText(Manager.this,"成绩录入失败！",Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
        //至此，线程终结
    }
}
