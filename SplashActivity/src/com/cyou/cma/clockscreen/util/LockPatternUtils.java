/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.cyou.cma.clockscreen.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.cyou.cma.clockscreen.applock.AppLockHelper;
import com.cyou.cma.clockscreen.password.PasswordHelper;
import com.cyou.cma.clockscreen.password.widget.PatternView;

/**
 * Utilities for the lock patten and its settings.
 */
public class LockPatternUtils {
// TODO jiangbin 这里所有的方法都可以重构
    public static final int LOCKSCREEN_TYPE = 1;
    public static final int APPLOCK_TYPE = 2;

    private static final String TAG = "LockPatternUtils";

    private static final String UNLOCK_PASSWORD_DIRECTORY = "password";
    private static final String APPUNLOCK_PASSWORD_DIRECTORY = "apppassword";

    private static final String LOCK_PATTERN_FILE = "gesture.key";
    private static final String LOCK_PASSWORD_FILE = "password.key";

    public static final int FAILED_ATTEMPTS_BEFORE_TIMEOUT = 5;

    public static final int FAILED_ATTEMPTS_BEFORE_RESET = 20;

    public static final long FAILED_ATTEMPT_TIMEOUT_MS = 1000L;

    /**
     * The interval of the countdown for showing progress of the lockout.
     */
    public static final long FAILED_ATTEMPT_COUNTDOWN_INTERVAL_MS = 1000L;

    /**
     * The minimum number of dots in a valid pattern.
     */
    public static final int MIN_LOCK_PATTERN_SIZE = 4;

    /**
     * The minimum number of dots the user must include in a wrong pattern
     * attempt for it to be counted against the counts that affect {@link #FAILED_ATTEMPTS_BEFORE_TIMEOUT} and
     * {@link #FAILED_ATTEMPTS_BEFORE_RESET}
     */
    public static final int MIN_PATTERN_REGISTER_FAIL = 3;

    private final Context mContext;
    private static String sLockPatternFilename;
    private static String sLockPasswordFilename;

    private static String sAppLockPatternFileName;
    private static String sAppLockPasswordFileName;

    /**
     * @param contentResolver Used to look up and save settings.
     */
    public LockPatternUtils(Context context) {
        mContext = context;

        // Initialize the location of gesture & PIN lock files
        if (sLockPatternFilename == null) {
            String dataSystemDirectory = context.getDir(UNLOCK_PASSWORD_DIRECTORY, Context.MODE_PRIVATE)
                    .getAbsolutePath();
            sLockPatternFilename = dataSystemDirectory + "/" + LOCK_PATTERN_FILE;
            sLockPasswordFilename = dataSystemDirectory + "/" + LOCK_PASSWORD_FILE;
        }
        if (sAppLockPasswordFileName == null) {
            String appPasswordDirectory = context.getDir(APPUNLOCK_PASSWORD_DIRECTORY, Context.MODE_PRIVATE)
                    .getAbsolutePath();
            sAppLockPasswordFileName = appPasswordDirectory + "/" + LOCK_PASSWORD_FILE;
            sAppLockPatternFileName = appPasswordDirectory + "/" + LOCK_PATTERN_FILE;
        }
    }

    /**
     * Check to see if a pattern matches the saved pattern. If no pattern exists,
     * always returns true.
     * 
     * @param pattern The pattern to check.
     * @return Whether the pattern matches the stored one.
     */
    public boolean checkPattern(List<PatternView.Cell> pattern, int type) {
        try {
            // Read all the bytes from the file
            String fileName = "";
            if(type==LOCKSCREEN_TYPE){
                fileName = sLockPatternFilename;
            }else{
                fileName = sAppLockPatternFileName;
            }
            RandomAccessFile raf = new RandomAccessFile(fileName, "r");
            final byte[] stored = new byte[(int) raf.length()];
            int got = raf.read(stored, 0, stored.length);
            raf.close();
            if (got <= 0) {
                return true;
            }
            // Compare the hash from the file with the entered pattern's hash
            return Arrays.equals(stored, LockPatternUtils.patternToHash(pattern));
        } catch (FileNotFoundException fnfe) {
            return true;
        } catch (IOException ioe) {
            return true;
        }
    }

    /**
     * Check to see if a password matches the saved password. If no password exists,
     * always returns true.
     * 
     * @param password The password to check.
     * @return Whether the password matches the stored one.
     */
    public boolean checkPassword(String password, int type) {
        try {
            String fileName = "";
            if (type == LOCKSCREEN_TYPE) {
                fileName = sLockPasswordFilename;
            } else {
                fileName = sAppLockPasswordFileName;
            }
            RandomAccessFile raf = new RandomAccessFile(fileName, "r");
            final byte[] stored = new byte[(int) raf.length()];
            int got = raf.read(stored, 0, stored.length);
            raf.close();
            if (got <= 0) {
                return true;
            }
            // Compare the hash from the file with the entered password's hash
            return Arrays.equals(stored, passwordToHash(password));
        } catch (FileNotFoundException fnfe) {
            return true;
        } catch (IOException ioe) {
            return true;
        }
    }

    /**
     * Return true if the user has ever chosen a pattern. This is true even if the pattern is
     * currently cleared.
     * 
     * @return True if the user has ever chosen a pattern.
     */

    /**
     * Clear any lock pattern or password.
     */
    public void clearLock(int type) {
        saveLockPassword(null, type);
        saveLockPattern(null, type);
    }

    /**
     * Save a lock pattern.
     * 
     * @param pattern The new pattern to save.
     */
    public void saveLockPattern(List<PatternView.Cell> pattern, int type) {
        String fileName = "";
        if (type == LOCKSCREEN_TYPE) {
            fileName = sLockPatternFilename;
        } else {
            fileName = sAppLockPatternFileName;
        }
        // Compute the hash
        final byte[] hash = LockPatternUtils.patternToHash(pattern);
        try {
            // Write the hash to file
            RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
            // Truncate the file if pattern is null, to clear the lock
            if (pattern == null) {
                raf.setLength(0);
            } else {
                raf.write(hash, 0, hash.length);
            }
            raf.close();
            if (pattern != null) {
                if (type == LOCKSCREEN_TYPE) {
                    PasswordHelper.setUnlockType(PasswordHelper.PATTERN_TYPE, mContext);
                    PasswordHelper.setPasswordEverSet(true, mContext);
                } else {
                    AppLockHelper.setPasswordEverSet(true, mContext);
                    AppLockHelper.setAppLockType(AppLockHelper.PATTERN_APP_LOCKER, mContext);
                }
            }
        } catch (FileNotFoundException fnfe) {
            Log.e(TAG, "Unable to save lock pattern to " + sLockPatternFilename);
        } catch (IOException ioe) {
            Log.e(TAG, "Unable to save lock pattern to " + sLockPatternFilename);
        }
    }

    public void saveLockPassword(String password, int type) {
        String fileName = "";
        if (type == LOCKSCREEN_TYPE) {
            fileName = sLockPasswordFilename;
        } else {
            fileName = sAppLockPasswordFileName;
        }
        final byte[] hash = passwordToHash(password);
        try {
            // Write the hash to file
            RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
            // Truncate the file if pattern is null, to clear the lock
            if (password == null) {
                raf.setLength(0);
            } else {
                raf.write(hash, 0, hash.length);
            }
            raf.close();
            if (password != null) {
                // TODO jiangbin
                if (type == LOCKSCREEN_TYPE) {
                    PasswordHelper.setUnlockType(PasswordHelper.PASSWORD_TYPE, mContext);
                    PasswordHelper.setPasswordEverSet(true, mContext);

                } else {
                    AppLockHelper.setPasswordEverSet(true, mContext);
                    AppLockHelper.setAppLockType(AppLockHelper.PIN_APP_LOCKER, mContext);
                }
            }
        } catch (FileNotFoundException fnfe) {
            // Cant do much, unless we want to fail over to using the settings provider
            Log.e(TAG, "Unable to save lock pattern to " + sLockPasswordFilename);
        } catch (IOException ioe) {
            // Cant do much
            Log.e(TAG, "Unable to save lock pattern to " + sLockPasswordFilename);
        }
    }

    /**
     * Deserialize a pattern.
     * 
     * @param string The pattern serialized with {@link #patternToString}
     * @return The pattern.
     */
    public static List<PatternView.Cell> stringToPattern(String string) {
        List<PatternView.Cell> result = Lists.newArrayList();

        final byte[] bytes = string.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            result.add(PatternView.Cell.of(b / 3, b % 3));
        }
        return result;
    }

    /**
     * Serialize a pattern.
     * 
     * @param pattern The pattern.
     * @return The pattern in string form.
     */
    public static String patternToString(List<PatternView.Cell> pattern) {
        if (pattern == null) {
            return "";
        }
        final int patternSize = pattern.size();

        byte[] res = new byte[patternSize];
        for (int i = 0; i < patternSize; i++) {
            PatternView.Cell cell = pattern.get(i);
            res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
        }
        String s = new String(res);
        return s;
    }

    /*
     * Generate an SHA-1 hash for the pattern. Not the most secure, but it is
     * at least a second level of protection. First level is that the file
     * is in a location only readable by the system process.
     * @param pattern the gesture pattern.
     * @return the hash of the pattern in a byte array.
     */
    private static byte[] patternToHash(List<PatternView.Cell> pattern) {
        if (pattern == null) {
            return null;
        }

        final int patternSize = pattern.size();
        byte[] res = new byte[patternSize];
        for (int i = 0; i < patternSize; i++) {
            PatternView.Cell cell = pattern.get(i);
            res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(res);
            return hash;
        } catch (NoSuchAlgorithmException nsa) {
            return res;
        }
    }

    /*
     * Generate a hash for the given password. To avoid brute force attacks, we use a salted hash.
     * Not the most secure, but it is at least a second level of protection. First level is that
     * the file is in a location only readable by the system process.
     * @param password the gesture pattern.
     * @return the hash of the pattern in a byte array.
     */
    public byte[] passwordToHash(String password) {
        if (password == null) {
            return null;
        }
        String algo = null;
        byte[] hashed = null;
        try {
            byte[] saltedPassword = (password).getBytes();
            byte[] sha1 = MessageDigest.getInstance(algo = "SHA-1").digest(saltedPassword);
            byte[] md5 = MessageDigest.getInstance(algo = "MD5").digest(saltedPassword);
            hashed = (toHex(sha1) + toHex(md5)).getBytes();
            Util.Logjb("hash", "hashed --->" + Arrays.toString(hashed));
        } catch (NoSuchAlgorithmException e) {
            Log.w(TAG, "Failed to encode string because of missing algorithm: " + algo);
        }
        return hashed;
    }

    private static String toHex(byte[] ary) {
        final String hex = "0123456789ABCDEF";
        String ret = "";
        for (int i = 0; i < ary.length; i++) {
            ret += hex.charAt((ary[i] >> 4) & 0xf);
            ret += hex.charAt(ary[i] & 0xf);
        }
        return ret;
    }

    /**
     * @return The elapsed time in millis in the future when the user is allowed to
     *         attempt to enter his/her lock pattern, or 0 if the user is welcome to
     *         enter a pattern.
     */
    public long getLockoutAttemptDeadline() {
        return 30000L;
    }

    /**
     * Resumes a call in progress. Typically launched from the EmergencyCall button
     * on various lockscreens.
     * 
     * @return true if we were able to tell InCallScreen to show.
     */
    public long setLockoutAttemptDeadline() {
        final long deadline = SystemClock.elapsedRealtime() + FAILED_ATTEMPT_TIMEOUT_MS;
        return deadline;
    }

}
