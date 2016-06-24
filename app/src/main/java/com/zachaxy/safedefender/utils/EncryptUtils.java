package com.zachaxy.safedefender.utils;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class EncryptUtils {
    /***
     * 加密方法
     * @param plainText  要加密的明文
     * @param strKey     加密所使用的密码
     * @return           加密后的密文
     */
    public static String encrypt(String plainText, String strKey) {
        byte[] b1 = encryptAES(plainText, strKey);
        return bytesToHexString(b1);
    }

    /***
     * 解密方法
     * @param cipherText 加密后的密文
     * @param strKey     解密密码
     * @return           解密后的明文
     */
    public static String decrypt(String cipherText, String strKey) {
        byte[] b1 = hexStringToBytes(cipherText);
        return decrypt(b1, strKey);
    }

	private static Key getKey(byte[] arrBTmp) {
		byte[] arrB;
		arrB = new byte[16];
		int i = 0;
		int j = 0;
		while (i < arrB.length) {
			if (j > arrBTmp.length - 1) {
				j = 0;
			}
			arrB[i] = arrBTmp[j];
			i++;
			j++;
		}
		Key key = new javax.crypto.spec.SecretKeySpec(arrB, "AES");
		return key;
	}

	private static byte[] encryptAES(String s, String strKey) {
		byte[] r = null;
		try {
			Key key = getKey(strKey.getBytes());
			Cipher c;
			c = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, key);
			r = c.doFinal(s.getBytes());

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return r;
	}

	private static String decrypt(byte[] code, String strKey) {
		String r = null;
		try {
			Key key = getKey(strKey.getBytes());
			Cipher c;
			c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, key);
			byte[] clearByte = c.doFinal(code);
			r = new String(clearByte);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			System.out.println("not padding");
			r = null;
		}

		return r;
	}

	private static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	private static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
}
