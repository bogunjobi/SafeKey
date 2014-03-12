package com.example.safekey;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.TextView;

public class Main extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TextView mName = (TextView) findViewById(R.id.hello_name);
		SharedPreferences auth_pref = getSharedPreferences("Login", Context.MODE_PRIVATE);
		String name = auth_pref.getString("name", "no name");
		mName.setText("HELLO " + name.split(" ")[0].toUpperCase() + "!");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}
		return false;
	}

}
