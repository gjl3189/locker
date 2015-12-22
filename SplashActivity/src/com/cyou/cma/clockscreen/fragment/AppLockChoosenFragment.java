package com.cyou.cma.clockscreen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.widget.InstalledLayout;

public class AppLockChoosenFragment extends Fragment {
	private InstalledLayout mInstalledLayout;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = View.inflate(getActivity(),
				R.layout.fragment_applock_choosen, null);
		initView(view);
		return view;
	}

	private void initView(View v) {
		mInstalledLayout = (InstalledLayout) v
				.findViewById(R.id.applock_choosen_layout);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onPause() {
		super.onPause();
		mInstalledLayout.saveData();
	}

	@Override
	public void onResume() {
		super.onResume();
		mInstalledLayout.refreshData();
	}
	
	public void setIsGuide(boolean isGuide, int hintHeight){
		if(mInstalledLayout != null){
			mInstalledLayout.setIsGuide(isGuide,hintHeight);
		}
	}

}