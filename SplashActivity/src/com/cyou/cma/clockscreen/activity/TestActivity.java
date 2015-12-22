package com.cyou.cma.clockscreen.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cynad.cma.locker.R;

import de.greenrobot.event.EventBus;

public class TestActivity extends Activity {
	private Button clickTextView1;
	private Button clickTextView2;
	private TextView textView1;
	private TextView textView2;
	private EventBus sEventBus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		sEventBus = EventBus.getDefault();
		clickTextView1 = (Button) findViewById(R.id.click1);
		clickTextView2 = (Button) findViewById(R.id.click2);
		textView1 = (TextView) findViewById(R.id.clicktext1);
		textView2 = (TextView) findViewById(R.id.clicktext2);
		clickTextView1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MyEvent myEvent = new MyEvent();
				myEvent.type =1;
				sEventBus.post(myEvent);
			}
		});
		clickTextView2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MyEvent myEvent = new MyEvent();
				myEvent.type =2;
				sEventBus.post(myEvent);
			}
		});
		sEventBus.register(this);
		// sEventBus.register(this, Integer.class, null);
		// sEventBus.re
		// sEventBus.re

	}

	public void onEvent(MyEvent myEvent) {
		if (myEvent.type == 1) {
			textView1.setText("you click me");
			textView2.setText("you click textView1");

		} else {

			textView1.setText("you click textview2");
			textView2.setText("you click me");

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		sEventBus.unregister(this);
	}

	class MyEvent {
		int type;
	}
}
