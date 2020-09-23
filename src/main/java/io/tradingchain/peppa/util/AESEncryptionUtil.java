package io.tradingchain.peppa.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AESEncryptionUtil {

  /** 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。 偏移量必须为16位 */
  public static String encrypt(String sKey, String ivParameter, byte[] sSrc)
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException,
          NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
    return Base64.getEncoder()
        .encodeToString(
            getCipher(sKey, ivParameter, Cipher.ENCRYPT_MODE).doFinal(sSrc)); // 此处使用BASE64做转码。
  }

  /** 解密 */
  public static byte[] decrypt(String sKey, String ivParameter, byte[] sSrc)
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
          InvalidKeyException, NoSuchPaddingException, BadPaddingException,
          IllegalBlockSizeException {
    return getCipher(sKey, ivParameter, Cipher.DECRYPT_MODE)
        .doFinal(Base64.getDecoder().decode(sSrc));
  }

  /** 加密算法 */
  private static Cipher getCipher(String seed, String ivParameter, int encryptMode)
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
          InvalidKeyException {
    byte[] raw = seed.getBytes(StandardCharsets.US_ASCII);
    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
    cipher.init(encryptMode, skeySpec, iv);
    return cipher;
  }
}
