package info.tcpay.peppa.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class AESEncryptionUtil {

  private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8',
      '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  /**
   * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。 偏移量必须为16位
   */
  public static String encrypt(String sKey, String ivParameter, byte[] sSrc)
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
    return new BASE64Encoder()
        .encode(getCipher(sKey, ivParameter, Cipher.ENCRYPT_MODE).doFinal(sSrc));//此处使用BASE64做转码。
  }


  /**
   * 解密
   */
  public static byte[] decrypt(String sKey, String ivParameter, byte[] sSrc)
      throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
    return getCipher(sKey, ivParameter, Cipher.DECRYPT_MODE)
        .doFinal(new BASE64Decoder().decodeBuffer(new String(sSrc)));
  }

  /**
   * 签名前字符串
   */
  public static String getOTCPreSignStr(Map<String, String> data, String secret) {
    Map<String, String> treeMap = new TreeMap<>(data);
    StringBuffer sb = new StringBuffer();
    treeMap.forEach((k, v) -> {
      if (!"sign".equals(k) && null != v) {
        sb.append('&').append(k).append('=').append(v);
      }
    });
    sb.append("&key=").append(secret);
    return sb.toString().substring(1);
  }

  /***
   * md5 加密
   */
  public static String MD5OTC(String s) {
    try {
      byte[] btInput = s.getBytes(StandardCharsets.UTF_8);
      // 获得MD5摘要算法的 MessageDigest 对象
      MessageDigest mdInst = MessageDigest.getInstance("MD5");
      // 使用指定的字节更新摘要
      mdInst.update(btInput);
      // 获得密文
      byte[] md = mdInst.digest();
      // 把密文转换成十六进制的字符串形式
      int j = md.length;
      char[] str = new char[j * 2];
      int k = 0;
      for (byte byte0 : md) {
        str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
        str[k++] = HEX_DIGITS[byte0 & 0xf];
      }
      return new String(str);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 加密算法
   */
  private static Cipher getCipher(String seed, String ivParameter, int encryptMode)
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
    byte[] raw = seed.getBytes(StandardCharsets.US_ASCII);
    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
    cipher.init(encryptMode, skeySpec, iv);
    return cipher;
  }
}
