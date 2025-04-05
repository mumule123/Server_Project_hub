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

package org.apache.tiles.access;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.apache.tiles.NoSuchContainerException;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;


public class TilesAccessTest {

    
    @Test
    public void testSetContainer() {
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<String, Object>();
        expect(context.getApplicationScope()).andReturn(attribs);
        replay(context, container);
        TilesAccess.setContainer(context, container);
        assertEquals(attribs.size(), 1);
        assertEquals(attribs.get(TilesAccess.CONTAINER_ATTRIBUTE), container);
        verify(context, container);
    }

    
    @Test
    public void testSetContainerWithKey() {
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<String, Object>();
        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();
        replay(context, container);
        TilesAccess.setContainer(context, container, "myKey");
        assertEquals(1, attribs.size());
        assertEquals(container, attribs.get("myKey"));

        TilesAccess.setContainer(context, null, "myKey");
        assertEquals(0, attribs.size());

        TilesAccess.setContainer(context, container, null);
        assertEquals(1, attribs.size());
        assertEquals(container, attribs.get(TilesAccess.CONTAINER_ATTRIBUTE));
        verify(context, container);
    }

    
    @Test
    public void testGetContainer() {
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<String, Object>();
        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();

        replay(context, container);
        attribs.put(TilesAccess.CONTAINER_ATTRIBUTE, container);
        assertEquals(container, TilesAccess.getContainer(context));
        verify(context, container);
    }

    
    @Test
    public void testGetContainerWithKey() {
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<String, Object>();
        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();

        replay(context, container);
        attribs.put(TilesAccess.CONTAINER_ATTRIBUTE, container);
        attribs.put("myKey", container);
        assertEquals(container, TilesAccess.getContainer(context, null));
        assertEquals(container, TilesAccess.getContainer(context, "myKey"));
        verify(context, container);
    }

    
    @Test
    public void testSetCurrentContainer() {
        Request request = createMock(Request.class);
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<String, Object>();
        attribs.put("myKey", container);
        Map<String, Object> requestScope = new HashMap<String, Object>();

        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();
        expect(request.getContext("request")).andReturn(requestScope);
        expect(request.getApplicationContext()).andReturn(context);
        replay(request, context, container);
        TilesAccess.setCurrentContainer(request, "myKey");
        assertEquals(container, requestScope.get(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME));
        verify(request, context, container);
    }

    
    @Test(expected = NoSuchContainerException.class)
    public void testSetCurrentContainerException() {
        Request request = createMock(Request.class);
        ApplicationContext context = createMock(ApplicationContext.class);
        Map<String, Object> attribs = new HashMap<String, Object>();

        expect(request.getApplicationContext()).andReturn(context);
        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();
        replay(request, context);
        try {
            TilesAccess.setCurrentContainer(request, "myKey");
        } finally {
            verify(request, context);
        }
    }

    
    @Test
    public void testSetCurrentContainerWithContainer() {
        Request request = createMock(Request.class);
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<String, Object>();
        attribs.put("myKey", container);
        Map<String, Object> requestScope = new HashMap<String, Object>();

        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();
        expect(request.getContext("request")).andReturn(requestScope);

        replay(request, context, container);
        TilesAccess.setCurrentContainer(request, container);
        assertEquals(container, requestScope.get(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME));
        verify(request, context, container);
    }

    
    @Test(expected = NullPointerException.class)
    public void testSetCurrentContainerWithContainerException() {
        Request request = createMock(Request.class);
        ApplicationContext context = createMock(ApplicationContext.class);
        Map<String, Object> attribs = new HashMap<String, Object>();

        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();

        replay(request, context);
        try {
            TilesAccess.setCurrentContainer(request, (TilesContainer) null);
        } finally {
            verify(request, context);
        }
    }

    
    @Test
    public void testGetCurrentContainer() {
        Request request = createMock(Request.class);
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<String, Object>();
        attribs.put("myKey", container);
        Map<String, Object> requestScope = new HashMap<String, Object>();
        requestScope.put(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME, container);

        expect(request.getApplicationContext()).andReturn(context);
        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();
        expect(request.getContext("request")).andReturn(requestScope);

        replay(request, context, container);
        assertEquals(container, TilesAccess.getCurrentContainer(request));
        verify(request, context, container);
    }

    
    @Test
    public void testGetCurrentContainerDefault() {
        Request request = createMock(Request.class);
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<String, Object>();
        attribs.put(TilesAccess.CONTAINER_ATTRIBUTE, container);
        Map<String, Object> requestScope = new HashMap<String, Object>();

        expect(request.getApplicationContext()).andReturn(context);
        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();
        expect(request.getContext("request")).andReturn(requestScope);

        replay(request, context, container);
        assertEquals(container, TilesAccess.getCurrentContainer(request));
        verify(request, context, container);
    }
}