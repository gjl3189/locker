/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyou.cma.clockscreen.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ContentResolver;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ResourceCursorAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.LockApplication;
import com.cyou.cma.clockscreen.adapter.InstalledAppAdapter4QuickLaunch;
import com.cyou.cma.clockscreen.event.SelectEvent;
import com.cyou.cma.clockscreen.event.SendEvent;
import com.cyou.cma.clockscreen.quicklaunch.DatabaseUtil;
import com.cyou.cma.clockscreen.quicklaunch.QuickContact;
import com.cyou.cma.clockscreen.util.ToastMaster;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.SideBar;
import com.cyou.cma.clockscreen.widget.SideBar.OnTouchingLetterChangedListener;
import com.cyou.cma.clockscreen.widget.material.LImageButton;

import de.greenrobot.event.EventBus;

public class QuickContactsFragment extends Fragment implements
		OnTouchingLetterChangedListener, OnClickListener {
	public static final String[] CONTACTS_SUMMARY_PROJECTION = new String[] {
			Contacts._ID, // 0
			Contacts.DISPLAY_NAME, // 1
			Contacts.STARRED, // 2
			Contacts.TIMES_CONTACTED, // 3
			Contacts.CONTACT_PRESENCE, // 4
			Contacts.PHOTO_ID, // 5
			Contacts.LOOKUP_KEY, // 6
			Contacts.HAS_PHONE_NUMBER, // 7
			Contacts.SORT_KEY_PRIMARY };

	static final int SUMMARY_ID_COLUMN_INDEX = 0;
	static final int SUMMARY_NAME_COLUMN_INDEX = 1;
	static final int SUMMARY_STARRED_COLUMN_INDEX = 2;
	static final int SUMMARY_TIMES_CONTACTED_COLUMN_INDEX = 3;
	static final int SUMMARY_PRESENCE_STATUS_COLUMN_INDEX = 4;
	static final int SUMMARY_PHOTO_ID_COLUMN_INDEX = 5;
	static final int SUMMARY_LOOKUP_KEY = 6;
	static final int SUMMARY_HAS_PHONE_COLUMN_INDEX = 7;
	static final int SUMMARY_SORT_KEY = 8;
	Cursor cursor;
	ContactListItemAdapter adapter;
	private ListView mListView;
	private SideBar mSideBar;
	private View mHeadView;
	private TextView mTitleTextView;
	private LImageButton mBackImageButton;
	private TextView mContactTextView;
	private TextView mContactNumberTextView;
	public static ConcurrentHashMap<String, QuickContact> mHasSelectedApp = new ConcurrentHashMap<String, QuickContact>();
	public static List<QuickContact> mIndatabaseContacts = new ArrayList<QuickContact>();
	private LinearLayout mHasSettedLayout;
	private RelativeLayout mRelativeLayout;
	private View mTianchongView1;
	private View mTianchongView2;
	private TextView mOthersView;
	private ImageButton mDeleteImageButton;
	private ConcurrentHashMap<Integer, String> mPhoneNumberHashMap = new ConcurrentHashMap<Integer, String>();
	public static ConcurrentHashMap<String, QuickContact> mFolderApp = new ConcurrentHashMap<String, QuickContact>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
				+ Contacts.HAS_PHONE_NUMBER + "=1) AND ("
				+ Contacts.DISPLAY_NAME + " != '' ))";
		cursor = getActivity().getContentResolver().query(Contacts.CONTENT_URI,
				CONTACTS_SUMMARY_PROJECTION, select, null,
				Contacts.SORT_KEY_PRIMARY + " COLLATE LOCALIZED ASC");
		
		DatabaseUtil.deleteInvalidContact(cursor);
		getActivity().startManagingCursor(cursor);
		adapter = new ContactListItemAdapter(getActivity(),
				R.layout.quick_contacts, cursor);
		// setListAdapter(adapter);
		// getListView().setScrollBarStyle(0);
		// setno
		EventBus.getDefault().register(this);
		mIndatabaseContacts.clear();
		// if (isFolder) {
		// mIndatabaseContacts.clear();
		// mIndatabaseContacts = DatabaseUtil
		// .getQuickContactOnFolder(QuickLaunchFragment.ID);
		mIndatabaseContacts = LockApplication.mQuickContactDao.loadAll();
		for (QuickContact quickContact : mIndatabaseContacts) {
			mHasSelectedApp.put(
					quickContact.getContactName()
							+ quickContact.getContactNumber(), quickContact);
			if (quickContact.getFolderIdOfContact() != null
					&& LockApplication.mQuickFolder != null) {
				if (quickContact.getFolderIdOfContact().longValue() == LockApplication.mQuickFolder
						.getId().longValue()) {
					mFolderApp.put(
							quickContact.getContactName()
									+ quickContact.getContactNumber(),
							quickContact);
				}
			}
			// }
		}

	}

	private boolean hidden;

	public void setHeadViewHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void hideHeardView() {
		mHeadView.setVisibility(View.GONE);
		mTianchongView1.setVisibility(View.GONE);
		mTianchongView2.setVisibility(View.GONE);
		mOthersView.setVisibility(View.GONE);
		mUnderLineView.setVisibility(View.GONE);

	}

	private View mUnderLineView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_quick_contacts, null);
		mContactTextView = (TextView) view.findViewById(R.id.use_contact);
		mContactNumberTextView = (TextView) view
				.findViewById(R.id.use_contact_number);
		mHasSettedLayout = (LinearLayout) view
				.findViewById(R.id.has_setted_layout);
		mRelativeLayout = (RelativeLayout) view
				.findViewById(R.id.relative_layout);
		mTianchongView1 = view.findViewById(R.id.tianchong);
		mTianchongView2 = view.findViewById(R.id.tianchong2);
		mOthersView = (TextView) view.findViewById(R.id.textview_other);
		mListView = (ListView) view.findViewById(R.id.listview);
		mListView.setAdapter(adapter);
		mSideBar = (SideBar) view.findViewById(R.id.sidrbar);
		mSideBar.setOnTouchingLetterChangedListener(this);
		mHeadView = view.findViewById(R.id.tab_titlebar);
		mUnderLineView = view.findViewById(R.id.underline);
		mDeleteImageButton = (ImageButton) view
				.findViewById(R.id.delete_button);
		mDeleteImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mUnderLineView.setVisibility(View.GONE);
				mOthersView.setVisibility(View.GONE);
				mHasSettedLayout.setVisibility(View.GONE);
				mContactTextView.setVisibility(View.GONE);
				mContactNumberTextView.setVisibility(View.GONE);
				mRelativeLayout.setVisibility(View.GONE);
				LockApplication.mQuickContactDao.delete(mHasSelectedApp
						.get(LockApplication.mQuickContact.getContactName()
								+ LockApplication.mQuickContact
										.getContactNumber()));
				mHasSelectedApp.remove(LockApplication.mQuickContact
						.getContactName()
						+ LockApplication.mQuickContact.getContactNumber());
				LockApplication.mQuickContact = null;

				adapter.notifyDataSetChanged();
				SendEvent sendEvent = new SendEvent();
				sendEvent.eventType = SendEvent.NONE;
				EventBus.getDefault().post(sendEvent);
			}
		});
		if (LockApplication.mQuickContact != null) {
			mContactTextView.setText(LockApplication.mQuickContact
					.getContactName());
			mContactNumberTextView.setText(LockApplication.mQuickContact
					.getContactNumber());
			mTianchongView2.setVisibility(View.VISIBLE);
			mOthersView.setVisibility(View.VISIBLE);
			mTianchongView1.setVisibility(View.VISIBLE);
		} else {
			mUnderLineView.setVisibility(View.GONE);
			mOthersView.setVisibility(View.GONE);
			mHasSettedLayout.setVisibility(View.GONE);
			mContactTextView.setVisibility(View.GONE);
			mContactNumberTextView.setVisibility(View.GONE);
			mRelativeLayout.setVisibility(View.GONE);
			mTianchongView2.setVisibility(View.GONE);
			mTianchongView1.setVisibility(View.GONE);
		}

		if (hidden) {
			hideHeardView();
		}

		mTitleTextView = (TextView) mHeadView.findViewById(R.id.tv_title);
		mBackImageButton = (LImageButton) mHeadView.findViewById(R.id.btn_left);
		mTitleTextView.setText(R.string.quick_contacts);
		mBackImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				getActivity().finish();
			}
		});
		// mListView.setOnItemClickListener();
		return view;

	}

	public void saveContact(int position) {

		// adapter.getItem(arg2);
		CharArrayBuffer nameBuffer = new CharArrayBuffer(128);
		cursor.moveToPosition(position);
		cursor.copyStringToBuffer(SUMMARY_NAME_COLUMN_INDEX, nameBuffer);
		// String phoneName = String.valueOf(nameBuffer.data);
		String phoneName = new String(nameBuffer.data, 0, nameBuffer.sizeCopied);
		// Util.Logjb("jiangbinb", "phonename -->" + phoneName);
		final long contactId = cursor.getLong(SUMMARY_ID_COLUMN_INDEX);
		final String lookupKey = cursor.getString(SUMMARY_LOOKUP_KEY);
		final String phoneNumber = getPhoneNumber(getActivity(), contactId);
		if (LockApplication.mQuickContact != null) {
			LockApplication.mQuickContactDao
					.delete(LockApplication.mQuickContact);
		}
		QuickContact contact = new QuickContact();
		contact.setContactName(phoneName);
		contact.setContactNumber(phoneNumber);
		contact.setLaunchSetIdOfContact(QuickLaunchFragment.ID);
		DatabaseUtil.saveContactToLaunchset(contact);
		getActivity().finish();
		SendEvent sendEvent = new SendEvent();
		sendEvent.eventType = SendEvent.CONTACT_TYPE;
		EventBus.getDefault().post(sendEvent);
		// contact.set

	}

	public QuickContact getContactAtPostion(int position) {

		// adapter.getItem(arg2);
		CharArrayBuffer nameBuffer = new CharArrayBuffer(128);
		cursor.moveToPosition(position);
		cursor.copyStringToBuffer(SUMMARY_NAME_COLUMN_INDEX, nameBuffer);
		String phoneName = new String(nameBuffer.data, 0, nameBuffer.sizeCopied);
		// Util.Logjb("jiangbinb", "phonename -->" + phoneName);
		final long contactId = cursor.getLong(SUMMARY_ID_COLUMN_INDEX);
		final String lookupKey = cursor.getString(SUMMARY_LOOKUP_KEY);
		final String phoneNumber = getPhoneNumber(getActivity(), contactId);
		QuickContact contact = new QuickContact();
		contact.setContactName(phoneName);
		contact.setContactNumber(phoneNumber);
		return contact;
	}

	public void onEvent(SidebarTouchEvent event) {
		String s = event.letter;
		char a = s.charAt(0);
		int position = adapter.getPositionForSection(a);
		if (position == -1) {
			position = 0;
		}
		if (a != '#' && position == 0) {
		} else {
			mListView.setSelection(position);
		}
	}

	private final class ContactListItemAdapter extends ResourceCursorAdapter
			implements SectionIndexer {
		public ContactListItemAdapter(Context context, int layout, Cursor c) {
			super(context, layout, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final ContactListItemCache cache = (ContactListItemCache) view
					.getTag();
			cache.mDeleteButton.setTag("1:" + cursor.getPosition());
			cache.mAddButton.setTag("2:" + cursor.getPosition());
			// Set the name
			cursor.copyStringToBuffer(SUMMARY_NAME_COLUMN_INDEX,
					cache.nameBuffer);
			int position = cursor.getPosition();
			int size = cache.nameBuffer.sizeCopied;
			// String phoneName = new String(cache.nameBuffer.data);
			String phoneName = new String(cache.nameBuffer.data, 0, size);
			Util.Logjb("jiangbinb", "phonename -->" + phoneName);
			cache.nameView.setText(cache.nameBuffer.data, 0, size);
			final long contactId = cursor.getLong(SUMMARY_ID_COLUMN_INDEX);
			final String lookupKey = cursor.getString(SUMMARY_LOOKUP_KEY);
			String phoneNumber = "";
			if (mPhoneNumberHashMap.get(position) != null) {
				phoneNumber = mPhoneNumberHashMap.get(position);
			} else {
				phoneNumber = getPhoneNumber(getActivity(), contactId);
				mPhoneNumberHashMap.put(position, phoneNumber);
			}
			cache.numberView.setText(phoneNumber);

			int section = getSectionForPosition(position);
			int sectionPostion = getPositionForSection(section);
			if (position == sectionPostion) {
				cache.headerView.setVisibility(View.VISIBLE);
				String title = "";
				title = String.valueOf((char) section).toUpperCase();
				cache.headerView.setText(title);
			} else {
				if (sectionPostion == -1 && position == 0) {
					cache.headerView.setVisibility(View.VISIBLE);
					cache.headerView.setText("#");
				} else {
					cache.headerView.setVisibility(View.GONE);
				}
			}
			// if (LockApplication.mQuickContact != null) {
			// if (phoneNumber.equals(LockApplication.mQuickContact
			// .getContactNumber())) {
			// // view.setVisibility(view.GONE);
			// cache.mAddButton.setVisibility(View.GONE);
			// cache.mDeleteButton.setVisibility(View.VISIBLE);
			// cache.mInotherFolder.setVisibility(View.GONE);
			// } else {
			// cache.mAddButton.setVisibility(View.VISIBLE);
			// }
			// }

			// if (mHasSelectedApp.containsKey(phoneName + phoneNumber)) {
			// cache.mAddButton.setVisibility(View.GONE);
			// } else {
			// cache.mAddButton.setVisibility(View.VISIBLE);
			// }
			
			if (mFolderApp.containsKey(phoneName + phoneNumber)) {
				cache.mAddButton.setVisibility(View.GONE);
				cache.mDeleteButton.setVisibility(View.VISIBLE);
				cache.mInotherFolder.setVisibility(View.GONE);
			} else {
				if (mHasSelectedApp.containsKey(phoneName + phoneNumber)) {

					cache.mAddButton.setVisibility(View.GONE);
					cache.mDeleteButton.setVisibility(View.GONE);
					cache.mInotherFolder.setVisibility(View.VISIBLE);

				} else {
					cache.mAddButton.setVisibility(View.VISIBLE);
					cache.mDeleteButton.setVisibility(View.GONE);
					cache.mInotherFolder.setVisibility(View.GONE);
				}
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = super.newView(context, cursor, parent);
			ContactListItemCache cache = new ContactListItemCache();
			cache.nameView = (TextView) view.findViewById(R.id.contact_name);
			cache.numberView = (TextView) view
					.findViewById(R.id.contact_number);
			cache.headerView = (TextView) view.findViewById(R.id.headview);
			cache.mAddButton = (ImageButton) view
					.findViewById(R.id.contact_add);
			cache.mDeleteButton = (ImageButton) view
					.findViewById(R.id.contact_delete);
		
			cache.mAddButton.setOnClickListener(QuickContactsFragment.this);
			cache.mDeleteButton.setOnClickListener(QuickContactsFragment.this);
			cache.mInotherFolder = (ImageButton) view
					.findViewById(R.id.contact_hasselect);
			view.setTag(cache);

			return view;
		}

		private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		// public ContentAdapter(Context context, int textViewResourceId,
		// List<String> objects) {
		// super(context, textViewResourceId, objects);
		// }

		@Override
		public int getPositionForSection(int section) {
			// Cursor cursor = getCursor();
			if (section < 65 || (section > 90 && section < 97) || section > 122) {
				return -1;
			}
			int i = -1;
			cursor.moveToFirst();
			do {
				i++;
				CharArrayBuffer nameBuffer = new CharArrayBuffer(128);
				cursor.copyStringToBuffer(SUMMARY_SORT_KEY, nameBuffer);
				char index = nameBuffer.data[0];
				if (index == section || (index - 32) == section) {
					return i;

				}
			} while (cursor.moveToNext());

			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			// return 0;
			cursor.moveToPosition(position);
			CharArrayBuffer nameBuffer = new CharArrayBuffer(128);
			cursor.copyStringToBuffer(SUMMARY_SORT_KEY, nameBuffer);
			char index = nameBuffer.data[0];
			return index;

		}

		@Override
		public Object[] getSections() {
			String[] sections = new String[mSections.length()];
			for (int i = 0; i < mSections.length(); i++)
				sections[i] = String.valueOf(mSections.charAt(i));
			return sections;
		}

	}

	public static String getPhoneNumber(Context mContext, long sid) {
		// List<ContactBean> contactList = new ArrayList<ContactBean>();
		String[] PHONES_PROJECTION = new String[] { Phone.DISPLAY_NAME,
				Phone.NUMBER, Phone.CONTACT_ID };
		String selection = null;
		String[] selectionArg = null;
		if (sid > 0) {
			selection = Phone.CONTACT_ID + " = ?";
			selectionArg = new String[] { String.valueOf(sid) };
		}
		ContentResolver resolver = mContext.getContentResolver();
		Cursor phoneCursor = null;
		try {
			phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION,
					selection, selectionArg, null);

			if (phoneCursor != null) {
				int idIndex = phoneCursor.getColumnIndex(Phone.CONTACT_ID);
				int nameIndex = phoneCursor.getColumnIndex(Phone.DISPLAY_NAME);
				int numberIndex = phoneCursor.getColumnIndex(Phone.NUMBER);
				// ContactBean bean;
				while (phoneCursor.moveToNext()) {
					String phoneNumber = phoneCursor.getString(numberIndex);
					if (TextUtils.isEmpty(phoneNumber))
						continue;
					phoneNumber = phoneNumber.replaceAll("-", "")
							.replaceAll(" ", "").replaceAll("\\(", "")
							.replaceAll("\\)", "");
					// bean = new ContactBean();
					// bean.setContact_mobile(phoneNumber);
					// bean.setName(phoneCursor.getString(nameIndex));
					// bean.setSid(phoneCursor.getLong(idIndex));
					// contactList.add(bean);
					return phoneNumber;
				}
				phoneCursor.close();
			}
		} catch (Exception e) {
			if (phoneCursor != null) {
				try {
					phoneCursor.close();
				} catch (Exception e1) {

				}
			}
		} finally {
			phoneCursor.close();
		}
		// return contactList;
		return "";
	}

	final static class ContactListItemCache {
		public TextView nameView;
		public TextView headerView;
		public TextView numberView;
		public ImageButton mAddButton;
		public ImageButton mDeleteButton;
		public ImageButton mInotherFolder;
		public CharArrayBuffer nameBuffer = new CharArrayBuffer(128);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		// if (!isFolder) {
		// mHasSelectedApp.clear();
		// mFolderApp.clear();
		// }
		LockApplication.mQuickContact = null;
	}

	@Override
	public void onTouchingLetterChanged(String s) {

		SidebarTouchEvent event = new SidebarTouchEvent();
		event.letter = s;
		EventBus.getDefault().post(event);

	}

	public static class SidebarTouchEvent {
		public String letter;

	}

	@Override
	public void onClick(View view) {
		String tag = view.getTag().toString();
		String tags[] = tag.split(":");
		int op = Integer.parseInt(tags[0]);
		int position = Integer.parseInt(tags[1]);
		// int position = Integer.parseInt(view.getTag().toString());
		if (!isFolder) {
			saveContact(position);
		} else {
			if (op == 2) {
				if (InstalledAppAdapter4QuickLaunch.sQuickHashMap.size()
						+ QuickContactsFragment.mFolderApp.size() == 8) {
					ToastMaster.makeText(getActivity(), R.string.most_eight,
							Toast.LENGTH_SHORT);
					return;
				}
				QuickContact quickContact = getContactAtPostion(position);
				mHasSelectedApp.put(quickContact.getContactName()
						+ quickContact.getContactNumber(), quickContact);
				mFolderApp
						.put(quickContact.getContactName()
								+ quickContact.getContactNumber(), quickContact);
				SelectEvent event = new SelectEvent();
				EventBus.getDefault().post(event);
				adapter.notifyDataSetChanged();
			} else if (op == 1) {// Delete
				QuickContact quickContact = getContactAtPostion(position);
				mHasSelectedApp.remove(quickContact.getContactName()
						+ quickContact.getContactNumber());
				mFolderApp.remove(quickContact.getContactName()
						+ quickContact.getContactNumber());
				SelectEvent event = new SelectEvent();
				EventBus.getDefault().post(event);
				adapter.notifyDataSetChanged();
			}
		}
	}

	public boolean isFolder;

	public void setIsFolder(boolean isFolder) {
		this.isFolder = isFolder;
		// mIndatabaseContacts.clear();
		// // if (isFolder) {
		// // mIndatabaseContacts.clear();
		// // mIndatabaseContacts = DatabaseUtil
		// // .getQuickContactOnFolder(QuickLaunchFragment.ID);
		// mIndatabaseContacts = LockApplication.mQuickContactDao.loadAll();
		// for (QuickContact quickContact : mIndatabaseContacts) {
		// mHasSelectedApp.put(quickContact.getContactName()
		// + quickContact.getContactNumber(), quickContact);
		// // }
		// }
	}
}
