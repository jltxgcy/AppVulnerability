package com.server;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.UnsupportedEncodingException;
 import java.net.ServerSocket;
 import java.net.Socket;
 
 import android.app.Activity;
 import android.os.Bundle;
 import android.os.Handler;
 import android.os.Message;
 import android.view.View;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.TextView;
 import android.widget.Toast;
 
 
 public class MyServerActivity extends Activity 
 {
     private TextView ipTextView = null;
     private EditText mEditText = null;
     private Button sendButton = null;
     private TextView mTextView = null;
     
     private OutputStream outStream = null;
     private Socket clientSocket = null;
     private ServerSocket mServerSocket = null;
     
     private Handler mHandler = null;
     
     private AcceptThread mAcceptThread = null;
     private ReceiveThread mReceiveThread = null;
     private boolean stop = true;
     
     /** Called when the activity is first created. */
     @Override
     public void onCreate(Bundle savedInstanceState) 
     {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.main);
         
         ipTextView = (TextView)this.findViewById(R.id.iptextview);
         mEditText = (EditText)this.findViewById(R.id.sedittext);
         sendButton = (Button)this.findViewById(R.id.sendbutton);
         sendButton.setEnabled(false);
         mTextView = (TextView)this.findViewById(R.id.textview);
         
         //发送数据按钮监听
         sendButton.setOnClickListener(new View.OnClickListener() 
         {
             
             @Override
             public void onClick(View v) 
             {
                 // TODO Auto-generated method stub
                 byte[] msgBuffer = null;
                 //获得EditTex的内容
                 String text = mEditText.getText().toString();
                 try {
                     //字符编码转换
                     msgBuffer = text.getBytes("GB2312");
                 } catch (UnsupportedEncodingException e1) {
                     // TODO Auto-generated catch block
                     e1.printStackTrace();
                 }
                             
                 
                 try {
                     //获得Socket的输出流
                     outStream = clientSocket.getOutputStream();
                 } catch (IOException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                 }                                                    
                 
                 
                 try {
                     //发送数据
                     outStream.write(msgBuffer);
                 } catch (IOException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                 }
                 //清空内容
                 mEditText.setText("");
                 displayToast("发送成功！");
                 
             }
         });
         //消息处理
         mHandler = new Handler()
         {
             @Override
             public void handleMessage(Message msg)
             {
                 switch(msg.what)
                 {
                     case 0:
                     {
                         //显示客户端IP
                         ipTextView.setText((msg.obj).toString());
                         //使能发送按钮
                         sendButton.setEnabled(true);
                         break;
                     }
                     case 1:
                     {
                         //显示接收到的数据
                         mTextView.setText((msg.obj).toString());
                         break;
                     }                 
                 }                                           
                 
             }
         };
         
         
         mAcceptThread = new AcceptThread();
         //开启监听线程
         mAcceptThread.start();
               
     }
     
     //显示Toast函数
     private void displayToast(String s)
     {
         Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
     }
     
     
     private class AcceptThread extends Thread
     {
         @Override
         public void run()
         {
             try {
                 //实例化ServerSocket对象并设置端口号为7100
                 mServerSocket = new ServerSocket(6100);
             } catch (IOException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
             
             try {
                 //等待客户端的连接（阻塞）
                 clientSocket = mServerSocket.accept();
             } catch (IOException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
             
             mReceiveThread = new ReceiveThread(clientSocket);
             stop = false;
             //开启接收线程
             mReceiveThread.start();
             
             Message msg = new Message();
             msg.what = 0;
             //获取客户端IP
             msg.obj = clientSocket.getInetAddress().getHostAddress();
             //发送消息
             mHandler.sendMessage(msg);
             
         }
         
     }
     
     
     private class ReceiveThread extends Thread
     {
         private InputStream mInputStream = null;
         private byte[] buf ;  
         private String str = null;
         
         ReceiveThread(Socket s)
         {
             try {
                 //获得输入流
                 this.mInputStream = s.getInputStream();
             } catch (IOException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
         }
         
         @Override
         public void run()
         {
             while(!stop)
             {
                 this.buf = new byte[512];
                 
                 //读取输入的数据(阻塞读)
                 try {
                     this.mInputStream.read(buf);
                 } catch (IOException e1) {
                     // TODO Auto-generated catch block
                     e1.printStackTrace();
                 }
                 
                 //字符编码转换
                 try {
                     this.str = new String(this.buf, "GB2312").trim();
                 } catch (UnsupportedEncodingException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                 }
                 
                 Message msg = new Message();
                 msg.what = 1;        
                 msg.obj = this.str;
                 //发送消息
                 mHandler.sendMessage(msg);
                 
             }
         }
     }
     
       
     @Override
     public void onDestroy()
     {
         super.onDestroy();
         
         if(mReceiveThread != null)
         {
             stop = true;
             mReceiveThread.interrupt();
         }
     }
     
     
 }