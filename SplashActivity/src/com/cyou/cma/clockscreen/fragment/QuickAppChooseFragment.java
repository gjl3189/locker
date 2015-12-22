package com.cyou.cma.clockscreen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.bean.InstalledAppBean;
import com.cyou.cma.clockscreen.widget.InstalledLayout4QuickLaunch;

public class QuickAppChooseFragment extends Fragment {
	private InstalledLayout4QuickLaunch mInstalledLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.fragment_quicklaunch,
				null);
		initView(view);

		// mInstalledLayout.mGridView
		// .setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1,
		// int arg2, long arg3) {
		// InstalledAppBean installedAppBean = (InstalledAppBean)
		// mInstalledLayout.mAdapter
		// .getItem(arg2);
		// }
		// });
		return view;
	}

	private void initView(View v) {
		mInstalledLayout = (InstalledLayout4QuickLaunch) v
				.findViewById(R.id.quickapp_choosen_layout);
		mInstalledLayout.setIsFolder(isFolder);
	}

	@Override
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
		// mInstalledLayout.saveData();
	}

	@Override
	public void onResume() {
		super.onResume();
		// mInstalledLayout.refreshData();
	}

	// public void setIsGuide(boolean isGuide, int hintHeight) {
	// if (mInstalledLayout != null) {
	// mInstalledLayout.setIsGuide(isGuide, hintHeight);
	// }
	// }
	private boolean isFolder;

	public void setIsFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}
}