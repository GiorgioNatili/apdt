/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/
package com.gnstudio.apdt;

import java.security.NoSuchAlgorithmException;
import java.util.prefs.Preferences;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.gnstudio.apdt.APDTLog;

public class APDTPref {
	private APDTPref() {
		throw new AssertionError();
	}

	
	private static final Preferences _PREF = Preferences.userRoot().node(
			"/com/gnstudio/apdt/system/v_1");

	private static final byte[] ___KEY = (new byte[] { 65, 80, 68,
			84, 45, 35, 45, 115, 121, 115, 45, 49, 45, 48, 45, 48 });

	public static String get(String key, String def) {
		return _PREF.get(key, def);
	}

	public boolean getBoolean(String key, boolean def) {
		return _PREF.getBoolean(key, def);
	}

	public static byte[] getByteArray(String key, byte[] def) {
		return _PREF.getByteArray(key, def);
	}

	public static double getDouble(String key, double def) {
		return _PREF.getDouble(key, def);
	}

	public static float getFloat(String key, float def) {
		return _PREF.getFloat(key, def);
	}

	public static int getInt(String key, int def) {
		return _PREF.getInt(key, def);
	}

	public static long getLong(String key, long def) {
		return _PREF.getLong(key, def);
	}

	public static void put(String key, String value) {
		_PREF.put(key, value);
	}

	public void putBoolean(String key, boolean value) {
		_PREF.putBoolean(key, value);
	}

	public static void putByteArray(String key, byte[] value) {
		_PREF.putByteArray(key, value);
	}

	public static void putDouble(String key, double value) {
		_PREF.putDouble(key, value);
	}

	public void putFloat(String key, float value) {
		_PREF.putFloat(key, value);
	}

	public static void putInt(String key, int value) {
		_PREF.putInt(key, value);
	}

	public static void putLong(String key, long value) {
		_PREF.putLong(key, value);
	}

	public static void remove(String key) {
		_PREF.remove(key);
	}

	public static String getDecrypt(String key,String def) {
		byte[] decrypt = _PREF.getByteArray(key, new byte[0]);

		if (KeyEnc.checkBytes(decrypt)) {
			decrypt = KeyEnc.decrypt(___KEY, decrypt);
		}
		return KeyEnc.checkBytes(decrypt) ? new String(decrypt) : def;
	}

	public static void putEncrypt(String key, String value) {
		if (value == null || value.length() == 0) {
			_PREF.putByteArray(key, new byte[0]);
		} else {

			byte[] encrypt = KeyEnc.encrypt(___KEY, value);
			_PREF.putByteArray(key, encrypt);
		}

	}

	private static class KeyEnc {
		private static final String ALGORITHEM_DES = "DES";

		private static final String SALT = new String(new byte[] { 0x01, 0x23,
				0x45, 0x67 });
		private static Cipher cipher = null;

		// get an instance of cipher
		static {
			try {
				cipher = Cipher.getInstance(ALGORITHEM_DES);
			} catch (NoSuchAlgorithmException ex) {
				APDTLog.logException(ex);
			} catch (NoSuchPaddingException ex) {
				APDTLog.logException(ex);
			}
		}

		private KeyEnc() {
			throw new AssertionError();
		}

		/**
		 * Returns an encrypted <code>byte[]</code> of the given key, rawString.
		 * 
		 * @param key
		 *            The encryption key.
		 * @param rawString
		 *            The string to be encrypted.
		 * @return <code>byte[]</code> the raw string as an encrypted byte[].
		 */
		public static byte[] encrypt(final  byte[] key, final String rawString) {
			try {
				StringBuffer buffer = new StringBuffer(SALT);
				buffer.append(new String(key));
				DESKeySpec secretKeySpec = new DESKeySpec(
						getKeyBytes(buffer.toString()));
				SecretKeyFactory keyFactory = SecretKeyFactory
						.getInstance(ALGORITHEM_DES);
				cipher.init(Cipher.ENCRYPT_MODE,
						keyFactory.generateSecret(secretKeySpec));

				return cipher.doFinal(rawString.getBytes());
			} catch (Exception ex) {
				APDTLog.log(ex);
			}
			return new byte[0];
		}

		private static byte[] getKeyBytes(String key) {
			byte[] keyBytes = key.getBytes();
			return keyBytes;
		}

		/**
		 * Returns a decrypted <code>byte[]</code> of the given key,
		 * encryptionBytes.
		 * 
		 * @param key
		 *            The decryption key.
		 * @param encryptionBytes
		 *            The encryption bytes to be decrypted.
		 * @return <code>byte[]</code> the decrypted value.
		 */
		public static byte[] decrypt(final  byte[] key,
				final byte[] encryptionBytes) {

			if (checkBytes(encryptionBytes)) {
				try {
					StringBuffer buffer = new StringBuffer(SALT);
					buffer.append(new String(key));
					DESKeySpec secretKeySpec = new DESKeySpec(
							getKeyBytes(buffer.toString()));
					SecretKeyFactory keyFactory = SecretKeyFactory
							.getInstance(ALGORITHEM_DES);
					cipher.init(Cipher.DECRYPT_MODE,
							keyFactory.generateSecret(secretKeySpec));
					byte[] decryptedBytes = cipher.doFinal(encryptionBytes);
					return decryptedBytes;
				} catch (Exception ex) {
					APDTLog.log(ex);
				}
			}
			return new byte[0];
		}

		public static boolean checkBytes(byte[] bytes) {
			return (bytes != null && bytes.length > 0) ? true : false;
		}
	}

}
