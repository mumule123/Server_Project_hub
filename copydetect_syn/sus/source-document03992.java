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

package jakarta.faces.convert;

import jakarta.faces.FacesException;
import jakarta.faces.application.FacesMessage;


public class ConverterException
        extends FacesException
{
    private static final long serialVersionUID = 8668056061177480197L;
    
    private FacesMessage _facesMessage;

    
    public ConverterException()
    {
        super();
    }

    public ConverterException(FacesMessage facesMessage)
    {
        super((facesMessage == null) ? null : facesMessage.getSummary());
        _facesMessage = facesMessage;
    }

    public ConverterException(FacesMessage facesMessage, Throwable cause)
    {
        super(cause);
        _facesMessage = facesMessage;
    }

    public ConverterException(String message)
    {
        super(message);
    }

    public ConverterException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConverterException(Throwable cause)
    {
        super(cause);
    }

    
    public FacesMessage getFacesMessage()
    {
        return _facesMessage;
    }

}