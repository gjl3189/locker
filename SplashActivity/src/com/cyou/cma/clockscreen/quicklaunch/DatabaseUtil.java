package com.cyou.cma.clockscreen.quicklaunch;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.provider.ContactsContract.Contacts;

import com.cyou.cma.clockscreen.LockApplication;
import com.cyou.cma.clockscreen.fragment.QuickContactsFragment;
import com.cyou.cma.clockscreen.quicklaunch.QuickContactDao.Properties;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * 快速启动数据库帮助类
 * 
 * @author jiangbin
 * 
 */
public class DatabaseUtil {
	/**
	 * 获取 几号桩上的 快速联系人
	 * 
	 * @return
	 */
	public static QuickContact getQuickContactOnLaunchSet(long id) {
		QueryBuilder<QuickContact> queryBuilder = LockApplication.mQuickContactDao
				.queryBuilder().where(Properties.LaunchSetIdOfContact.eq(id));
		List<QuickContact> list = queryBuilder.list();
		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	/**
	 * 获取 几号桩上的quick application
	 * 
	 * @return
	 */
	public static QuickApplication getQuickApplicationOnLaunchSet(long id) {
		QueryBuilder<QuickApplication> queryBuilder = LockApplication.mQuickApplicationDao
				.queryBuilder()
				.where(com.cyou.cma.clockscreen.quicklaunch.QuickApplicationDao.Properties.LaunchSetIdOfApplication
						.eq(id));
		List<QuickApplication> list = queryBuilder.list();
		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	/**
	 * 获取几号桩上的 quick folder
	 * 
	 * @param id
	 * @return
	 */
	public static QuickFolder getQuickFolderOnLaunchSet(long id) {
		QueryBuilder<QuickFolder> queryBuilder = LockApplication.mQuickFolderDao
				.queryBuilder()
				.where(com.cyou.cma.clockscreen.quicklaunch.QuickFolderDao.Properties.LaunchSetIdOfFolder
						.eq(id));
		List<QuickFolder> list = queryBuilder.list();
		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public static List<QuickApplication> getQuickApplicationOnFolder(
			long folderId) {

		QueryBuilder<QuickApplication> queryBuilder = LockApplication.mQuickApplicationDao
				.queryBuilder()
				.where(com.cyou.cma.clockscreen.quicklaunch.QuickApplicationDao.Properties.FolderIdOfApplication
						.eq(folderId));
		List<QuickApplication> list = queryBuilder.list();
		return list;

	}

	public static List<QuickContact> getQuickContactOnFolder(long folderId) {

		QueryBuilder<QuickContact> queryBuilder = LockApplication.mQuickContactDao
				.queryBuilder()
				.where(com.cyou.cma.clockscreen.quicklaunch.QuickContactDao.Properties.FolderIdOfContact
						.eq(folderId));
		List<QuickContact> list = queryBuilder.list();
		return list;

	}

	public static List<QuickContact> getQuickContactByContactName(
			String contactName) {
		QueryBuilder<QuickContact> queryBuilder = LockApplication.mQuickContactDao
				.queryBuilder()
				.where(com.cyou.cma.clockscreen.quicklaunch.QuickContactDao.Properties.ContactName
						.eq(contactName));
		List<QuickContact> list = queryBuilder.list();
		return list;
	}

	public static void saveContactToLaunchset(QuickContact quickContact) {
		LockApplication.mQuickContactDao.insertOrReplace(quickContact);
	}

	public static long saveApplicationToLaunchset(
			QuickApplication quickApplication) {
		return LockApplication.mQuickApplicationDao
				.insertOrReplace(quickApplication);
	}

	public static void deleteApplicationOnLaunchset(long id) {
		// LockApplication.mQuickApplicationDao.de
		List<QuickApplication> q = LockApplication.mQuickApplicationDao
				.loadAll();
		for (QuickApplication t : q) {
			try {

				if (t.getLaunchSetIdOfApplication() == id) {
					LockApplication.mQuickApplicationDao.delete(t);
				}
			} catch (Exception e) {
			}
		}
	}

	public static void deleteThingsOnfolder(long id) {
		List<QuickContact> quickContacts = getQuickContactOnFolder(id);
		for (QuickContact quickContact : quickContacts) {
			LockApplication.mQuickContactDao.delete(quickContact);
		}
		List<QuickApplication> quickApplications = getQuickApplicationOnFolder(id);
		for (QuickApplication quickApplication : quickApplications) {
			LockApplication.mQuickApplicationDao.delete(quickApplication);

		}
	}

	public static void saveFolderToLaunchset(QuickFolder quickFolder) {
		LockApplication.mQuickFolderDao.insertOrReplace(quickFolder);
	}

	public static void deleteQuickContact(QuickContact quickContact) {
		LockApplication.mQuickContactDao.delete(quickContact);
	}

	public static void deleteApplicationByPackageName(String packageName) {
		QueryBuilder<QuickApplication> queryBuilder = LockApplication.mQuickApplicationDao
				.queryBuilder()
				.where(com.cyou.cma.clockscreen.quicklaunch.QuickApplicationDao.Properties.PackageName
						.eq(packageName));
		List<QuickApplication> list = queryBuilder.list();
		for (QuickApplication quickApplication : list) {
			// if(quickApplication.getLaunchSetIdOfApplication())
			Long launchSetId = quickApplication.getLaunchSetIdOfApplication();
			Long folderId = quickApplication.getFolderIdOfApplication();
			if (launchSetId != null) {
				LaunchSet launchSet = LockApplication.launchSetDao
						.load(launchSetId);
				if (launchSet != null) {
					launchSet.setType(0);
					LockApplication.launchSetDao.update(launchSet);
				}
			}
			if (folderId != null) {
				QuickFolder quickFolder = LockApplication.mQuickFolderDao
						.load(folderId);
				if (quickFolder != null) {
					int subCount = quickFolder.getSubCount();
					// if(subCount)
					subCount--;
					if (subCount < 0) {
						subCount = 0;
					}
					quickFolder.setSubCount(subCount);
					LockApplication.mQuickFolderDao.update(quickFolder);
				}
			}
			LockApplication.mQuickApplicationDao.delete(quickApplication);
		}
	}

	public static void deleteQuickApplication(QuickApplication quickApplication) {
		LockApplication.mQuickApplicationDao.delete(quickApplication);
	}

	public static void deleteInvalidContact(Cursor cursor) {
		ArrayList<String> displayNames = new ArrayList<String>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				CharArrayBuffer nameBuffer = new CharArrayBuffer(128);
				// cursor.moveToPosition(position);
				cursor.copyStringToBuffer(1, nameBuffer);
				String phoneName = new String(nameBuffer.data, 0,
						nameBuffer.sizeCopied);
				displayNames.add(phoneName);
			}
		}
		List<QuickContact> quickContacts = LockApplication.mQuickContactDao
				.loadAll();
		for (QuickContact quickContact : quickContacts) {
			if (!displayNames.contains(quickContact.getContactName())) {
				Long launchsetId = quickContact.getLaunchSetIdOfContact();
				if (launchsetId != null) {
					LaunchSet launchSet = LockApplication.launchSetDao
							.load(launchsetId);
					if (launchSet != null) {
						launchSet.setType(0);
						LockApplication.launchSetDao.update(launchSet);
					}
				}
				Long folderId = quickContact.getFolderIdOfContact();
				if (folderId != null) {
					QuickFolder quickFolder = LockApplication.mQuickFolderDao
							.load(folderId);
					if (quickFolder != null) {
						int subCount = quickFolder.getSubCount();
						// if(subCount)
						subCount--;
						if (subCount < 0) {
							subCount = 0;
						}
						quickFolder.setSubCount(subCount);
						LockApplication.mQuickFolderDao.update(quickFolder);
					}
				}
				LockApplication.mQuickContactDao.delete(quickContact);
			}
		}
	}

	public static void deleteQuickFolder(QuickFolder quickFolder) {
		LockApplication.mQuickFolderDao.delete(quickFolder);
	}
	public static void deleteInvalidContact(Context context){
		try {
			String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
					+ Contacts.HAS_PHONE_NUMBER + "=1) AND ("
					+ Contacts.DISPLAY_NAME + " != '' ))";
			Cursor cursor = context.getContentResolver().query(
					Contacts.CONTENT_URI,
					QuickContactsFragment.CONTACTS_SUMMARY_PROJECTION, select,
					null, Contacts.SORT_KEY_PRIMARY + " COLLATE LOCALIZED ASC");

			DatabaseUtil.deleteInvalidContact(cursor);
			cursor.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// public int getType() {
	// LockApplication.launchSetDao.
	// }
}
