package com.cyou.cma.clockscreen.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.LockApplication;
import com.cyou.cma.clockscreen.activity.QuickAppsActivity;
import com.cyou.cma.clockscreen.activity.QuickContactsActivity;
import com.cyou.cma.clockscreen.activity.SelectFolderActivity;
import com.cyou.cma.clockscreen.event.SendEvent;
import com.cyou.cma.clockscreen.quicklaunch.DatabaseUtil;
import com.cyou.cma.clockscreen.quicklaunch.LaunchSet;
import com.cyou.cma.clockscreen.quicklaunch.QuickApplication;
import com.cyou.cma.clockscreen.quicklaunch.QuickContact;
import com.cyou.cma.clockscreen.quicklaunch.QuickFolder;
import com.cyou.cma.clockscreen.util.LauchSetType;
import com.cyou.cma.clockscreen.util.ResolveInfoUtil;
import com.cyou.cma.clockscreen.util.ToastMaster;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.CustomAlertDialog;
import com.cyou.cma.clockscreen.widget.CustomListDialogBuilder;

import de.greenrobot.event.EventBus;

public class QuickLaunchFragment extends Fragment implements
		View.OnClickListener {
	private ImageView launch1;
	private ImageView launch2;
	private ImageView launch3;
	private ImageView launch4;
	private ImageView launch5;
	private TextView mTextView1;
	private TextView mTextView2;
	private TextView mTextView3;
	private TextView mTextView4;
	private TextView mTextView5;
	private ArrayList<TextView> mTextViews = new ArrayList<TextView>();
	private ArrayList<ImageView> maskView4Launchs = new ArrayList<ImageView>();
	private CustomAlertDialog mSingleChoiceDialog;
	private CustomListDialogBuilder mSingleChoiceBuilder;
	private CustomAlertDialog mSingleChoiceDialog2;
	private CustomListDialogBuilder mSingleChoiceBuilder2;
	private HashMap mLaunchSetObject = new HashMap();
	private CustomAlertDialog mAlertDialog;
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// View view =
		// LayoutInflater.from(getActivity()).inflate(R.layout.dialog_layout,
		// null);
		// mSingleChoiceDialog = new
		// CustomAlertDialog.Builder(getActivity()).setView(view).create();

		mSingleChoiceBuilder = new CustomListDialogBuilder(getActivity(), -1,
				true);
		mSingleChoiceDialog = mSingleChoiceBuilder
				.setTitle(R.string.dialog_title)
				.setVisible(new CustomAlertDialog.VisibleCallback() {

					@Override
					public void onclick() {
						mSingleChoiceDialog.dismiss();
					}
				}).setItems(R.array.dialog_titles, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// Intent intent = null;
						switch (arg1) {
						case 0:
							intent = new Intent(getActivity(),
									QuickContactsActivity.class);
							// if(mLaunchSetObject.containsKey(key))
							Object obj = mLaunchSetObject.get(tag);
							if (obj != null) {
								if (obj instanceof QuickContact) {
									LockApplication.mQuickContact = (QuickContact) obj;
								} else {
									if (!Util
											.getPreferenceBoolean(
													getActivity(),
													Util.HEHEHEHE, false)) {
										LockApplication.mQuickContact = null;
										mAlertDialog.show();
										Util.putPreferenceBoolean(
												getActivity(), Util.HEHEHEHE,
												true);
									} else {
										deleteSomething();
										intent = new Intent(getActivity(),
												QuickContactsActivity.class);
										startActivity(intent);
										mSingleChoiceDialog.dismiss();
									}
									break;
								}
							} else {
								LockApplication.mQuickContact = null;
							}

							startActivity(intent);
							mSingleChoiceDialog.dismiss();
							break;
						case 1:
							intent = new Intent(getActivity(),
									QuickAppsActivity.class);
							Object obj3 = mLaunchSetObject.get(tag);
							if (obj3 != null) {
								if (obj3 instanceof QuickApplication) {
									LockApplication.mQuickApplication = (QuickApplication) obj3;
								} else {
									if (!Util
											.getPreferenceBoolean(
													getActivity(),
													Util.HEHEHEHE, false)) {
										mAlertDialog.show();
										LockApplication.mQuickApplication = null;
										Util.putPreferenceBoolean(
												getActivity(), Util.HEHEHEHE,
												true);
									} else {
										deleteSomething();
										intent = new Intent(getActivity(),
												QuickAppsActivity.class);
										startActivity(intent);
										mSingleChoiceDialog.dismiss();
									}
									break;
								}
							} else {
								LockApplication.mQuickApplication = null;
							}
							startActivity(intent);
							mSingleChoiceDialog.dismiss();
							break;
						case 2:
							intent = new Intent(getActivity(),
									SelectFolderActivity.class);
							Object obj2 = mLaunchSetObject.get(tag);
							if (obj2 != null) {
								if (obj2 instanceof QuickFolder) {
									LockApplication.mQuickFolder = (QuickFolder) obj2;
								} else {
									if (!Util
											.getPreferenceBoolean(
													getActivity(),
													Util.HEHEHEHE, false)) {
										mAlertDialog.show();
										LockApplication.mQuickFolder = null;
										Util.putPreferenceBoolean(
												getActivity(), Util.HEHEHEHE,
												true);
									} else {

										deleteSomething();
										intent = new Intent(getActivity(),
												SelectFolderActivity.class);
										startActivity(intent);
										mSingleChoiceDialog.dismiss();

									}
									break;
								}
							} else {
								LockApplication.mQuickFolder = null;
							}
							startActivity(intent);
							mSingleChoiceDialog.dismiss();
							break;

						case 3:
							deleteSomething();
							break;
						}
					}
				}).create();
		mSingleChoiceBuilder2 = new CustomListDialogBuilder(getActivity(), -1,
				true);
		mSingleChoiceDialog2 = mSingleChoiceBuilder2
				.setTitle(R.string.dialog_title)
				.setVisible(new CustomAlertDialog.VisibleCallback() {

					@Override
					public void onclick() {
						mSingleChoiceDialog2.dismiss();
					}
				}).setItems(R.array.dialog_titles2, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// Intent intent = null;
						switch (arg1) {
						case 0:
							intent = new Intent(getActivity(),
									QuickContactsActivity.class);
							// if(mLaunchSetObject.containsKey(key))
							Object obj = mLaunchSetObject.get(tag);
							if (obj != null) {
								if (obj instanceof QuickContact) {
									LockApplication.mQuickContact = (QuickContact) obj;
								} else {
									if (Util.getPreferenceBoolean(
											getActivity(), Util.HEHEHEHE, false)) {
										mAlertDialog.show();
										LockApplication.mQuickContact = null;
										Util.putPreferenceBoolean(
												getActivity(), Util.HEHEHEHE,
												true);
									} else {
										deleteSomething();
										intent = new Intent(getActivity(),
												QuickContactsActivity.class);
										startActivity(intent);
										mSingleChoiceDialog2.dismiss();
									}
									break;
								}
							} else {
								LockApplication.mQuickContact = null;
							}

							startActivity(intent);
							mSingleChoiceDialog.dismiss();
							break;
						case 1:
							intent = new Intent(getActivity(),
									QuickAppsActivity.class);
							Object obj3 = mLaunchSetObject.get(tag);
							if (obj3 != null) {
								if (obj3 instanceof QuickApplication) {
									LockApplication.mQuickApplication = (QuickApplication) obj3;
								} else {
									if (Util.getPreferenceBoolean(
											getActivity(), Util.HEHEHEHE, false)) {
										mAlertDialog.show();
										LockApplication.mQuickApplication = null;
										Util.putPreferenceBoolean(
												getActivity(), Util.HEHEHEHE,
												true);
									} else {
										deleteSomething();
										intent = new Intent(getActivity(),
												QuickAppsActivity.class);
										startActivity(intent);
										mSingleChoiceDialog2.dismiss();
									}
									break;
								}
							} else {
								LockApplication.mQuickApplication = null;
							}
							startActivity(intent);
							mSingleChoiceDialog.dismiss();
							break;
						case 2:
							intent = new Intent(getActivity(),
									SelectFolderActivity.class);
							Object obj2 = mLaunchSetObject.get(tag);
							if (obj2 != null) {
								if (obj2 instanceof QuickFolder) {
									LockApplication.mQuickFolder = (QuickFolder) obj2;
								} else {
									if (Util.getPreferenceBoolean(
											getActivity(), Util.HEHEHEHE, false)) {
										mAlertDialog.show();
										LockApplication.mQuickFolder = null;
										Util.putPreferenceBoolean(
												getActivity(), Util.HEHEHEHE,
												true);
									} else {
										deleteSomething();
										intent = new Intent(getActivity(),
												SelectFolderActivity.class);
										startActivity(intent);
										mSingleChoiceDialog2.dismiss();
									}
									break;
								}
							} else {
								LockApplication.mQuickFolder = null;
							}
							startActivity(intent);
							mSingleChoiceDialog.dismiss();
							break;

						}
					}
				}).create();

		mAlertDialog = new CustomAlertDialog.Builder(getActivity())
				.setTitle(R.string.dialog_notice)
				.setMessage(R.string.dialog_switch)
				.setIcon(R.drawable.icon_notice)
				.setVisible(new CustomAlertDialog.VisibleCallback() {

					@Override
					public void onclick() {
						mAlertDialog.dismiss();
					}
				})
				.setPositiveButton(R.string.dialog_grade_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								deleteSomething();
								startActivity(intent);
							}
						})
				.setNegativeButton(R.string.dialog_grade_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).create();

		EventBus.getDefault().register(this);
	}

	public static int CONTACT_TYPE = 1;
	public static int APP_TYPE = 2;
	public static int FOLDER_TYPE = 3;

	private void deleteSomething() {
		Object obj4 = mLaunchSetObject.get(tag);
		if (obj4 != null) {
			if (obj4 instanceof QuickContact) {
				QuickContact quickContact = (QuickContact) obj4;
				LaunchSet launchSet = LockApplication.sLaunchSet.get(tag);
				launchSet.setType(0);
				LockApplication.launchSetDao.update(launchSet);
				LockApplication.mQuickContactDao.delete(quickContact);
				mLaunchSetObject.remove(tag);
				LockApplication.mQuickContact = null;

			} else if (obj4 instanceof QuickApplication) {
				QuickApplication quickApplication = (QuickApplication) obj4;
				LaunchSet launchSet = LockApplication.sLaunchSet.get(tag);
				launchSet.setType(0);
				LockApplication.launchSetDao.update(launchSet);
				LockApplication.mQuickApplicationDao.delete(quickApplication);
				mLaunchSetObject.remove(tag);
				LockApplication.mQuickApplication = null;
			} else if (obj4 instanceof QuickFolder) {
				QuickFolder quickFolder = (QuickFolder) obj4;
				LaunchSet launchSet = LockApplication.sLaunchSet.get(tag);
				launchSet.setType(0);
				LockApplication.launchSetDao.update(launchSet);
				LockApplication.mQuickFolderDao.delete(quickFolder);
				// LockApplication.mQuickApplicationDao.de
				List<QuickApplication> quickApplications = DatabaseUtil
						.getQuickApplicationOnFolder(quickFolder.getId());
				for (QuickApplication q : quickApplications) {
					LockApplication.mQuickApplicationDao.delete(q);
				}
				List<QuickContact> quickContacts = DatabaseUtil
						.getQuickContactOnFolder(quickFolder.getId());
				for (QuickContact quickContact : quickContacts) {
					LockApplication.mQuickContactDao.delete(quickContact);
				}
				mLaunchSetObject.remove(tag);
				LockApplication.mQuickFolder = null;
			}
		}
		showAddOnLaunchset(maskView4Launchs.get(tag), mTextViews.get(tag));
	}

	public void onEvent(SendEvent event) {
		switch (event.eventType) {
		case SendEvent.CONTACT_TYPE:
			QuickContact quickContact = DatabaseUtil
					.getQuickContactOnLaunchSet(ID);
			// maskView4Launchs.get(tag)
			LockApplication.sLaunchSet.get(tag).setType(
					LauchSetType.CONTACT_TYPE);
			showContactOnLaunchset(quickContact, maskView4Launchs.get(tag),
					mTextViews.get(tag));
			LockApplication.launchSetDao.update(LockApplication.sLaunchSet
					.get(tag));
			mLaunchSetObject.put(tag, quickContact);
			break;
		case SendEvent.APP_TYPE:
			ResolveInfo resolveInfo = event.resolveInfo;
			// maskView4Launchs.get(tag).setImageDrawable(
			// ResolveInfoUtil.getLogo(resolveInfo, getActivity()
			// .getPackageManager()));
			maskView4Launchs.get(tag).setImageBitmap(
					LockApplication.getInstance().getQuickLaunchWithMaskBitmap(
							ResolveInfoUtil.getLogo(resolveInfo, getActivity()
									.getPackageManager()), false));
			mTextViews.get(tag).setText(
					ResolveInfoUtil.getAppName(resolveInfo, getActivity()
							.getPackageManager()));
			mTextViews.get(tag).setVisibility(View.VISIBLE);
			LockApplication.sLaunchSet.get(tag).setType(LauchSetType.APP_TYPE);
			LockApplication.launchSetDao.update(LockApplication.sLaunchSet
					.get(tag));
			mLaunchSetObject.put(tag, event.quickApplication);
			// TODO edit
			break;

		case SendEvent.FOLDER_TYPE:
			QuickFolder quickFolder = DatabaseUtil
					.getQuickFolderOnLaunchSet(ID);
			mLaunchSetObject.put(tag, quickFolder);
			showQuickFolder(quickFolder, maskView4Launchs.get(tag),
					mTextViews.get(tag));
			LockApplication.sLaunchSet.get(tag).setType(
					LauchSetType.FOLDER_TYPE);
			LockApplication.launchSetDao.update(LockApplication.sLaunchSet
					.get(tag));
			break;
		case SendEvent.NONE:

			showAddOnLaunchset(maskView4Launchs.get(tag), mTextViews.get(tag));
			LockApplication.sLaunchSet.get(tag).setType(4);
			LockApplication.launchSetDao.update(LockApplication.sLaunchSet
					.get(tag));
			mLaunchSetObject.remove(tag);
			break;

		}
	}

	private TextView mTextView12;
	private List<LaunchSet> launchSets;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		long startTime = System.currentTimeMillis();
		maskView4Launchs.clear();
		mTextViews.clear();
		View view = inflater.inflate(R.layout.activity_quicklaunch, null);
		launch1 = (ImageView) view.findViewById(R.id.launch0);
		launch2 = (ImageView) view.findViewById(R.id.launch1);
		launch3 = (ImageView) view.findViewById(R.id.launch2);
		launch4 = (ImageView) view.findViewById(R.id.launch3);
		launch5 = (ImageView) view.findViewById(R.id.launch4);
		maskView4Launchs.add(launch1);
		maskView4Launchs.add(launch2);
		maskView4Launchs.add(launch3);
		maskView4Launchs.add(launch4);
		maskView4Launchs.add(launch5);

		mTextView12 = (TextView) view.findViewById(R.id.launch_bar_tip);
		if (Util.getPreferenceBoolean(getActivity(), Util.HASHASHAS, false)) {
			mTextView12.setVisibility(View.INVISIBLE);
		}
		mTextView1 = (TextView) view.findViewById(R.id.textview1);
		mTextView2 = (TextView) view.findViewById(R.id.textview2);
		mTextView3 = (TextView) view.findViewById(R.id.textview3);
		mTextView4 = (TextView) view.findViewById(R.id.textview4);
		mTextView5 = (TextView) view.findViewById(R.id.textview5);

		mTextViews.add(mTextView1);
		mTextViews.add(mTextView2);
		mTextViews.add(mTextView3);
		mTextViews.add(mTextView4);
		mTextViews.add(mTextView5);

		launch1.setOnClickListener(this);
		launch2.setOnClickListener(this);
		launch3.setOnClickListener(this);
		launch4.setOnClickListener(this);
		launch5.setOnClickListener(this);

		// DatabaseUtil.getQuickApplicationOnLaunchSet(1);
		launchSets = LockApplication.launchSetDao.loadAll();
		for (long i = 1; i <= 5; i++) {
			int j = (int) (i - 1);
			int type = launchSets.get((int) (i - 1)).getType();

			if (type == LauchSetType.CONTACT_TYPE) {
				QuickContact quickContact = DatabaseUtil
						.getQuickContactOnLaunchSet(i);
				if (quickContact != null) {
					mLaunchSetObject.put(j, quickContact);
					showContactOnLaunchset(quickContact,
							maskView4Launchs.get((int) (i - 1)),
							mTextViews.get((int) (i - 1)));
				}
			} else if (type == LauchSetType.APP_TYPE) {
				QuickApplication quickApplication = DatabaseUtil
						.getQuickApplicationOnLaunchSet(i);
				if (quickApplication != null) {
					mLaunchSetObject.put(j, quickApplication);
					showQuickApplication(quickApplication,
							maskView4Launchs.get((int) (i - 1)),
							mTextViews.get((int) (i - 1)), (int) (i - 1));
				}
			} else if (type == LauchSetType.FOLDER_TYPE) {
				QuickFolder quickFolder = DatabaseUtil
						.getQuickFolderOnLaunchSet(i);
				if (quickFolder != null) {
					mLaunchSetObject.put(j, quickFolder);
					showQuickFolder(quickFolder,
							maskView4Launchs.get((int) (i - 1)),
							mTextViews.get((int) (i - 1)));
				}
			} else {
				showAddOnLaunchset(maskView4Launchs.get((int) (i - 1)),
						mTextViews.get((int) (i - 1)));
			}

		}

		// if()
		long endTime = System.currentTimeMillis();

		return view;
	}

	public void showAddOnLaunchset(ImageView view, TextView textView) {
		view.setImageResource(R.drawable.icon_quicklaunch_add);
		textView.setVisibility(View.INVISIBLE);
	}

	public void showContactOnLaunchset(QuickContact quickContact,
			ImageView view, TextView textView) {
		view.setImageResource(R.drawable.icon_quicklaunch_contact);
		textView.setText(quickContact.getContactName());
		textView.setVisibility(View.VISIBLE);
	}

	public void showQuickApplication(QuickApplication quickApplication,
			ImageView view, TextView textView, int i) {
		String packageName = quickApplication.getPackageName();
		PackageManager pm = getActivity().getPackageManager();
		Drawable drawable = null;
		try {
			drawable = pm.getApplicationIcon(pm.getApplicationInfo(packageName,
					PackageManager.GET_META_DATA));
		} catch (NameNotFoundException e) {
			Util.printException(e);
			textView.setVisibility(View.GONE);
			LaunchSet launchSet = launchSets.get(i);
			launchSet.setType(0);
			LockApplication.launchSetDao.update(launchSet);
			mLaunchSetObject.put(i, null);
			return;
		}
		// view.setImageDrawable(drawable);
		view.setImageBitmap(LockApplication.getInstance()
				.getQuickLaunchWithMaskBitmap(drawable, false));
		try {
			textView.setText(pm.getApplicationLabel(pm.getApplicationInfo(
					packageName, PackageManager.GET_META_DATA)));
			textView.setVisibility(View.VISIBLE);
		} catch (NameNotFoundException e) {
			textView.setVisibility(View.INVISIBLE);
		}

	}

	public void showQuickFolder(QuickFolder quickFolder, ImageView view,
			TextView textView) {
		view.setImageResource(R.drawable.icon_quicklaunch_folder);
		textView.setText(quickFolder.getFolderName());
		textView.setVisibility(View.VISIBLE);
	}

	public static long ID = 0;
	public static int tag;
	private boolean shouldShow = false;

	@Override
	public void onClick(View view) {
		tag = Integer.parseInt((String) view.getTag());
		ID = LockApplication.sLaunchSet.get(tag).getId();
		if (mLaunchSetObject.get(tag) != null) {
			Object obj = mLaunchSetObject.get(tag);
			if (obj instanceof QuickContact) {
				mSingleChoiceBuilder.setSelectedIndex(0);
			} else if (obj instanceof QuickApplication) {
				mSingleChoiceBuilder.setSelectedIndex(1);
			} else if (obj instanceof QuickFolder) {
				mSingleChoiceBuilder.setSelectedIndex(2);
			}
			mSingleChoiceDialog.show();
		} else {
			mSingleChoiceBuilder.setSelectedIndex(-1);
			mSingleChoiceBuilder2.setSelectedIndex(-1);
			mSingleChoiceDialog2.show();
		}
		
	}

	@Override
	public void onResume() {
		super.onResume();
	
	}
	@Override
	public void onDestroy() {

		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
