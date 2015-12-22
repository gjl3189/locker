package com.cyou.cma.clockscreen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.activity.ChooseLockPassword;
import com.cyou.cma.clockscreen.activity.ChooseLockPattern;
import com.cyou.cma.clockscreen.adapter.AppLockPwdCategoryAdapter;
import com.cyou.cma.clockscreen.adapter.PwdCategoryAdapter.ItemClickListener;
import com.cyou.cma.clockscreen.util.LockPatternUtils;
import com.cyou.cma.clockscreen.widget.NoScrollListView;

public class AppLockSettingFragment extends Fragment {

	private NoScrollListView mCategoryListView;

	private AppLockPwdCategoryAdapter mCategoryAdapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_applock_setting, null);
		initView(view);
		return view;
	}

	private void initView(View v) {
		mCategoryAdapter = new AppLockPwdCategoryAdapter(getActivity(), new ItemClickListener() {
			
			@Override
			public void onItemClicked(int position) {
				if (position == 0) {
					Intent intent = new Intent(getActivity(),
							ChooseLockPassword.class);
					intent.putExtra("type", LockPatternUtils.APPLOCK_TYPE);
					startActivity(intent);
				} else if (position == 1) {
					Intent intent = new Intent(getActivity(),
							ChooseLockPattern.class);
					intent.putExtra("type", LockPatternUtils.APPLOCK_TYPE);
					startActivity(intent);
				}
			}
		});
		mCategoryListView = (NoScrollListView) v
				.findViewById(R.id.pwdsettings_category);
		mCategoryListView.setAdapter(mCategoryAdapter);
//		mCategoryListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				if (position == 0) {
//					Intent intent = new Intent(getActivity(),
//							ChooseLockPassword.class);
//					intent.putExtra("type", LockPatternUtils.APPLOCK_TYPE);
//					startActivity(intent);
//				} else if (position == 1) {
//					Intent intent = new Intent(getActivity(),
//							ChooseLockPattern.class);
//					intent.putExtra("type", LockPatternUtils.APPLOCK_TYPE);
//					startActivity(intent);
//				}
//			}
//		});
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		mCategoryAdapter.notifyDataSetChanged();
	}

}
