package org.apache.joshua.util;

import java.util.List;
import java.util.Map;

import org.apache.joshua.corpus.Vocabulary;


@SuppressWarnings("ALL")
public class Ngram {

  public static void getNgrams(Map<String, Integer> tbl, int startOrder, int endOrder,
      final int[] wrds) {

    for (int i = 0; i < wrds.length; i++)
      for (int j = startOrder - 1; j < endOrder && j + i < wrds.length; j++) {
        StringBuilder ngram = new StringBuilder();
        for (int k = i; k <= i + j; k++) {
          int t_wrd = wrds[k];
          ngram.append(Vocabulary.word(t_wrd));
          if (k < i + j)
            ngram.append(" ");
        }
        String ngramStr = ngram.toString();
        increaseCount(tbl, ngramStr, 1);
      }
  }

  
  public static void getNgrams(Map<String, Integer> tbl, int startOrder, int endOrder,
      final List<Integer> wrds) {

    for (int i = 0; i < wrds.size(); i++)
      for (int j = startOrder - 1; j < endOrder && j + i < wrds.size(); j++) {
        StringBuilder ngram = new StringBuilder();
        for (int k = i; k <= i + j; k++) {
          int t_wrd = wrds.get(k);
          ngram.append(Vocabulary.word(t_wrd));
          if (k < i + j)
            ngram.append(" ");
        }
        String ngramStr = ngram.toString();
        increaseCount(tbl, ngramStr, 1);
      }
  }

  
  public static void getNgrams(Map<String, Integer> tbl, int startOrder, int endOrder,
      final String[] wrds) {

    for (int i = 0; i < wrds.length; i++)
      for (int j = startOrder - 1; j < endOrder && j + i < wrds.length; j++) {
        StringBuilder ngram = new StringBuilder();
        for (int k = i; k <= i + j; k++) {
          String t_wrd = wrds[k];
          ngram.append(t_wrd);
          if (k < i + j)
            ngram.append(" ");
        }
        String ngramStr = ngram.toString();
        increaseCount(tbl, ngramStr, 1);
      }
  }

  static private void increaseCount(Map<String, Integer> tbl, String feat, int increment) {
    Integer oldCount = tbl.get(feat);
    if (oldCount != null)
      tbl.put(feat, oldCount + increment);
    else
      tbl.put(feat, increment);
  }

}
 
package org.apache.commons.crypto.jna;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.crypto.cipher.AbstractCipherTest;
import org.apache.commons.crypto.stream.AbstractCipherStreamTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public abstract class AbstractCipherJnaStreamTest extends AbstractCipherStreamTest {

    private static final String CIPHER_OPENSSL_JNA = OpenSslJna.getCipherClass().getName();

    @BeforeEach
    public void init() {
        assumeTrue(OpenSslJna.isEnabled());
    }

    
    @Override
    @Timeout(value = 120000, unit = TimeUnit.MILLISECONDS)
    public void testByteBufferRead() throws Exception {
        doByteBufferRead(CIPHER_OPENSSL_JNA, false);

        doByteBufferRead(CIPHER_OPENSSL_JNA, true);
    }

    
    @Override
    @Timeout(value = 120000, unit = TimeUnit.MILLISECONDS)
    public void testByteBufferWrite() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        doByteBufferWrite(CIPHER_OPENSSL_JNA, baos, false);

        doByteBufferWrite(CIPHER_OPENSSL_JNA, baos, true);
    }

    @Override
    @Test
    public void testReadWrite() throws Exception {
        doReadWriteTest(0, CIPHER_OPENSSL_JNA, CIPHER_OPENSSL_JNA, iv);
        doReadWriteTest(count, CIPHER_OPENSSL_JNA, CIPHER_OPENSSL_JNA, iv);
        doReadWriteTest(count, AbstractCipherTest.JCE_CIPHER_CLASSNAME, CIPHER_OPENSSL_JNA, iv);
        doReadWriteTest(count, CIPHER_OPENSSL_JNA, AbstractCipherTest.JCE_CIPHER_CLASSNAME, iv);
        
        for (int i = 0; i < 8; i++) {
            iv[8 + i] = (byte) 0xff;
        }
        doReadWriteTest(count, CIPHER_OPENSSL_JNA, CIPHER_OPENSSL_JNA, iv);
        doReadWriteTest(count, AbstractCipherTest.JCE_CIPHER_CLASSNAME, CIPHER_OPENSSL_JNA, iv);
        doReadWriteTest(count, CIPHER_OPENSSL_JNA, AbstractCipherTest.JCE_CIPHER_CLASSNAME, iv);
    }

    
    @Override
    @Test
    @Timeout(value = 120000, unit = TimeUnit.MILLISECONDS)
    public void testSkip() throws Exception {
        doSkipTest(CIPHER_OPENSSL_JNA, false);

        doSkipTest(CIPHER_OPENSSL_JNA, true);
    }
}