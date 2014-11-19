package com.example.remotecontroller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener{
	public static final String TAG = "RemoteController";
	public static final int KEY_EVENT_PORT = 5050;
	public static final int TEXT_TRANSFER_PORT = 5055;
	
	public static final String KEY_INFO_OK = "com.sohu.inputmethod.sogou.REMOTE_CONTROL_KEY_OK";
	public static final String KEY_INFO_UP = "com.sohu.inputmethod.sogou.REMOTE_CONTROL_KEY_UP";
	public static final String KEY_INFO_LEFT = "com.sohu.inputmethod.sogou.REMOTE_CONTROL_KEY_LEFT";
	public static final String KEY_INFO_DOWN = "com.sohu.inputmethod.sogou.REMOTE_CONTROL_KEY_DOWN";
	public static final String KEY_INFO_RIGHT = "com.sohu.inputmethod.sogou.REMOTE_CONTROL_KEY_RIGHT";
	public static final String KEY_INFO_DEL = "com.sohu.inputmethod.sogou.REMOTE_CONTROL_KEY_DEL";
	public static final String KEY_INFO_ENTER = "com.sohu.inputmethod.sogou.REMOTE_CONTROL_KEY_ENTER";
	public static final String KEY_INFO_BACK = "com.sohu.inputmethod.sogou.REMOTE_CONTROL_KEY_BACK";
	public static final String KEY_INFO_HOME = "com.sohu.inputmethod.sogou.REMOTE_CONTROL_KEY_HOME";
	
	private EditText mTextIp;
	private EditText mTextToSend;
	private Button mButtonIpSet;
	private Button mButtonTextSend;
	private Button mButtonOk;
	private Button mButtonUp;
	private Button mButtonLeft;
	private Button mButtonDown;
	private Button mButtonRight;
	private Button mButtonDel;
	private Button mButtonEnter;
	private Button mButtonBack;
	private Button mButtonHome;
	
	private Socket mKeyEventSocket;
	private Socket mTextTransferSocket;
	
	private String mCurrentKeyEvent;
	private Runnable sendKeyRun = new Runnable(){
		public void run(){
			try {
				if(mKeyEventSocket == null || mTextTransferSocket == null){
					establishConnection();
				}
				OutputStream output = mKeyEventSocket.getOutputStream();
				byte buffer[] = new byte[1024 * 4];
				int tmp = 0;

				String str = mCurrentKeyEvent;
				InputStream input = new ByteArrayInputStream(str.getBytes());

				while ((tmp = input.read(buffer)) != -1) {
				
					output.write(buffer, 0, tmp);
				}
				output.flush();
				mCurrentKeyEvent = null;
//				System.out.println("Message has been sent");
			} catch (UnknownHostException e) {
//				e.printStackTrace();
			} catch (IOException e) {
//				e.printStackTrace();
			}
		}
	};
	
	private Runnable sendTextRun = new Runnable(){
		public void run(){
		try {
				OutputStream output = mTextTransferSocket.getOutputStream();
				byte buffer[] = new byte[1024 * 4];
				int tmp = 0;

				String str = mTextToSend.getText().toString();
				InputStream input = new ByteArrayInputStream(str.getBytes());

				while ((tmp = input.read(buffer)) != -1) {
				
					output.write(buffer, 0, tmp);
				}
				output.flush();
				System.out.println("Message has been sent");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		if(mKeyEventSocket == null || mTextTransferSocket == null)
			Toast.makeText(this, R.string.note_set_ip, Toast.LENGTH_SHORT).show();
	}
	
	
	public void initViews(){
		mTextIp = (EditText) findViewById(R.id.text_ip);
		mTextIp.setOnClickListener(this);
		mTextToSend = (EditText) findViewById(R.id.text_send);
		mTextToSend.setOnClickListener(this);
		mButtonIpSet = (Button) findViewById(R.id.button_ip_set);
		mButtonIpSet.setOnClickListener(this);
		mButtonTextSend = (Button) findViewById(R.id.button_send_text);
		mButtonTextSend.setOnClickListener(this);
		mButtonOk = (Button) findViewById(R.id.key_ok);
		mButtonOk.setOnClickListener(this);
		mButtonUp = (Button) findViewById(R.id.key_up);
		mButtonUp.setOnClickListener(this);
		mButtonLeft = (Button) findViewById(R.id.key_left);
		mButtonLeft.setOnClickListener(this);
		mButtonDown = (Button) findViewById(R.id.key_down);
		mButtonDown.setOnClickListener(this);
		mButtonRight = (Button) findViewById(R.id.key_right);
		mButtonRight.setOnClickListener(this);
		mButtonDel = (Button) findViewById(R.id.key_del);
		mButtonDel.setOnClickListener(this);
		mButtonEnter = (Button) findViewById(R.id.key_enter);
		mButtonEnter.setOnClickListener(this);
		mButtonBack = (Button) findViewById(R.id.key_back);
		mButtonBack.setOnClickListener(this);
		mButtonHome = (Button) findViewById(R.id.key_home);
		mButtonHome.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.button_ip_set){
			clearConnection();
//			establishConnection();
			return;
		}
		
		String str = mTextIp.getText().toString();
		
		if(str == null || (str != null && str.length()== 0)){
			Toast.makeText(this, R.string.note_set_ip, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(v.getId() == R.id.button_send_text){
			new Thread(sendTextRun).start();
			return;
		}
		
		if(v.getId() == R.id.key_ok){
			mCurrentKeyEvent = KEY_INFO_OK;
		}else if(v.getId() == R.id.key_up){
			mCurrentKeyEvent = KEY_INFO_UP;
		}else if(v.getId() == R.id.key_left){
			mCurrentKeyEvent = KEY_INFO_LEFT;
		}else if(v.getId() == R.id.key_down){
			mCurrentKeyEvent = KEY_INFO_DOWN;
		}else if(v.getId() == R.id.key_right){
			mCurrentKeyEvent = KEY_INFO_RIGHT;
		}else if(v.getId() == R.id.key_del){
			mCurrentKeyEvent = KEY_INFO_DEL;
		}else if(v.getId() == R.id.key_enter){
			mCurrentKeyEvent = KEY_INFO_ENTER;
		}else if(v.getId() == R.id.key_back){
			mCurrentKeyEvent = KEY_INFO_BACK;
		}else if(v.getId() == R.id.key_home){
			mCurrentKeyEvent = KEY_INFO_HOME;
		}
		
		new Thread(sendKeyRun).start();
	}
	
	private void clearConnection(){
		if(mKeyEventSocket != null){
			try {
				mKeyEventSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
			mKeyEventSocket = null;
		}
		
		if(mTextTransferSocket != null){
			try {
				mTextTransferSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
			mTextTransferSocket = null;
		}
	}
	
	private void establishConnection(){
		String ip_addr = mTextIp.getText().toString();
		if(ip_addr == null){
//			Toast.makeText(this, R.string.note_set_ip, Toast.LENGTH_SHORT).show();
			Log.d(TAG, "canna-remote:: ip address is null");
			return;
		}
		
		if(ip_addr.length() == 0){
//			Toast.makeText(this, R.string.note_set_ip, Toast.LENGTH_SHORT).show();
			Log.d(TAG, "canna-remote:: ip address is empty");
			return;
		}
		
		try {
			mKeyEventSocket = new Socket(ip_addr, KEY_EVENT_PORT);
//			mTextTransferSocket = new Socket(ip_addr, TEXT_TRANSFER_PORT);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Toast.makeText(this, R.string.note_connect_failed, Toast.LENGTH_SHORT).show();
			Log.d(TAG, "canna-remote:: unKnown exception = " + e);
			clearConnection();
		} catch (IOException se) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Toast.makeText(this, R.string.note_connect_failed, Toast.LENGTH_SHORT).show();
			Log.d(TAG, "canna-remote:: exception = " + se);
			clearConnection();
		}
	}
	
}
