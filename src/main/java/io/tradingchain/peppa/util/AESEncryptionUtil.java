package io.tradingchain.peppa.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class AESEncryptionUtil {

  /**
   * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
   * 偏移量必须为16位
   * @param sKey
   * @param ivParameter
   * @param sSrc
   * @return
   */
  public static String encrypt(String sKey, String ivParameter, byte[] sSrc)
      throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
      return new BASE64Encoder()
          .encode(getCipher(sKey, ivParameter, Cipher.ENCRYPT_MODE).doFinal(sSrc));//此处使用BASE64做转码。
  }

  /**
   * 解密
   * @param sKey key
   * @param ivParameter 偏移量
   * @param sSrc
   * @return
   */
  public static byte[] decrypt(String sKey, String ivParameter, byte[] sSrc)
      throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
      return getCipher(sKey, ivParameter, Cipher.DECRYPT_MODE)
          .doFinal(new BASE64Decoder().decodeBuffer(new String(sSrc)));
  }

  /**
   * 加密算法
   * @param seed key
   * @param ivParameter 偏移量
   * @param encryptMode 模式
   * @return
   * @throws UnsupportedEncodingException
   * @throws NoSuchPaddingException
   * @throws NoSuchAlgorithmException
   * @throws InvalidAlgorithmParameterException
   * @throws InvalidKeyException
   */
  private static Cipher getCipher(String seed, String ivParameter, int encryptMode)
      throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
    byte[] raw = seed.getBytes("ASCII");
    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
    cipher.init(encryptMode, skeySpec, iv);
    return cipher;
  }
}
