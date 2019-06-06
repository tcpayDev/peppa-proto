package io.tradingchain.peppa.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class AESEncryptionUtil {

  /**
   * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
   */

  // 加密
  public static String encrypt(String seed, String ivParameter, byte[] original) {


    try {
      return new String(Base64.encodeBase64(getCipher(seed,ivParameter, Cipher.ENCRYPT_MODE).doFinal(original)));
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
    } catch (InvalidAlgorithmParameterException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

  // 解密
  public static byte[] decrypt(String seed, String ivParameter, byte[] encrypted) {
    try {
      return getCipher(seed,ivParameter, Cipher.DECRYPT_MODE).doFinal(Base64.decodeBase64(encrypted));
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
    } catch (InvalidAlgorithmParameterException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    return null;
  }

  private static Cipher getCipher(String seed, String ivParameter, int encryptMode)
      throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
    SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
    random.setSeed(seed.getBytes());
    KeyGenerator generator = KeyGenerator.getInstance("AES");
    generator.init(128, random);
    SecretKey key = new SecretKeySpec(generator.generateKey().getEncoded(), "AES");
    System.out.println(key.getFormat());
//    IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(encryptMode, key);
    return cipher;
  }
}
