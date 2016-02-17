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
         
         //�������ݰ�ť����
         sendButton.setOnClickListener(new View.OnClickListener() 
         {
             
             @Override
             public void onClick(View v) 
             {
                 // TODO Auto-generated method stub
                 byte[] msgBuffer = null;
                 //���EditTex������
                 String text = mEditText.getText().toString();
                 try {
                     //�ַ�����ת��
                     msgBuffer = text.getBytes("GB2312");
                 } catch (UnsupportedEncodingException e1) {
                     // TODO Auto-generated catch block
                     e1.printStackTrace();
                 }
                             
                 
                 try {
                     //���Socket�������
                     outStream = clientSocket.getOutputStream();
                 } catch (IOException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                 }                                                    
                 
                 
                 try {
                     //��������
                     outStream.write(msgBuffer);
                 } catch (IOException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                 }
                 //�������
                 mEditText.setText("");
                 displayToast("���ͳɹ���");
                 
             }
         });
         //��Ϣ����
         mHandler = new Handler()
         {
             @Override
             public void handleMessage(Message msg)
             {
                 switch(msg.what)
                 {
                     case 0:
                     {
                         //��ʾ�ͻ���IP
                         ipTextView.setText((msg.obj).toString());
                         //ʹ�ܷ��Ͱ�ť
                         sendButton.setEnabled(true);
                         break;
                     }
                     case 1:
                     {
                         //��ʾ���յ�������
                         mTextView.setText((msg.obj).toString());
                         break;
                     }                 
                 }                                           
                 
             }
         };
         
         
         mAcceptThread = new AcceptThread();
         //���������߳�
         mAcceptThread.start();
               
     }
     
     //��ʾToast����
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
                 //ʵ����ServerSocket�������ö˿ں�Ϊ7100
                 mServerSocket = new ServerSocket(6100);
             } catch (IOException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
             
             try {
                 //�ȴ��ͻ��˵����ӣ�������
                 clientSocket = mServerSocket.accept();
             } catch (IOException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
             
             mReceiveThread = new ReceiveThread(clientSocket);
             stop = false;
             //���������߳�
             mReceiveThread.start();
             
             Message msg = new Message();
             msg.what = 0;
             //��ȡ�ͻ���IP
             msg.obj = clientSocket.getInetAddress().getHostAddress();
             //������Ϣ
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
                 //���������
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
                 
                 //��ȡ���������(������)
                 try {
                     this.mInputStream.read(buf);
                 } catch (IOException e1) {
                     // TODO Auto-generated catch block
                     e1.printStackTrace();
                 }
                 
                 //�ַ�����ת��
                 try {
                     this.str = new String(this.buf, "GB2312").trim();
                 } catch (UnsupportedEncodingException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                 }
                 
                 Message msg = new Message();
                 msg.what = 1;        
                 msg.obj = this.str;
                 //������Ϣ
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