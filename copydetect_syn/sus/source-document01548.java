package org.apache.commons.crypto.jna;

import java.nio.ByteBuffer;

import org.apache.commons.crypto.Crypto;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.PointerByReference;

final class OpenSsl11XNativeJna  implements OpenSslInterfaceNativeJna {

    static final boolean INIT_OK;

    static final Throwable INIT_ERROR;

    static {
        boolean ok = false;
        Throwable thrown = null;
        try {
            final String libName = System.getProperty(Crypto.CONF_PREFIX + OpenSslNativeJna.class.getSimpleName(), "crypto");
            OpenSslJna.debug("Native.register('%s')", libName);
            Native.register(libName);
            ok = true;
        } catch (final Exception | UnsatisfiedLinkError e) {
            thrown = e;
        } finally {
            INIT_OK = ok;
            INIT_ERROR = thrown;
        }
    }

    

    
    public static native PointerByReference ENGINE_by_id(String id);

    
    public static native int ENGINE_finish(PointerByReference e);

    
    public static native int ENGINE_free(PointerByReference e);

    
    public static native int ENGINE_init(PointerByReference e);

    
    public static native int ENGINE_set_default(PointerByReference e, int flags);

    
    public static native String ERR_error_string(NativeLong err, char[] null_);

    
    public static native NativeLong ERR_peek_error();

    
    public static native PointerByReference EVP_aes_128_cbc();

    
    public static native PointerByReference EVP_aes_128_ctr();

    
    public static native PointerByReference EVP_aes_192_cbc();

    
    public static native PointerByReference EVP_aes_192_ctr();

    
    public static native PointerByReference EVP_aes_256_cbc();

    
    public static native PointerByReference EVP_aes_256_ctr();

    
    public static native void EVP_CIPHER_CTX_free(PointerByReference c);

    
    public static native PointerByReference EVP_CIPHER_CTX_new();

    

    
    public static native int EVP_CIPHER_CTX_set_padding(PointerByReference c, int pad);

    
    public static native int EVP_CipherFinal_ex(PointerByReference ctx, ByteBuffer bout,
            int[] outl);

    
    

    
    public static native int EVP_CipherInit_ex(PointerByReference ctx, PointerByReference cipher,
            PointerByReference impl, byte[] key, byte[] iv, int enc);

    
    public static native int EVP_CipherUpdate(PointerByReference ctx, ByteBuffer bout, int[] outl,
            ByteBuffer in, int inl);

    
    public static native String OpenSSL_version(int type);

    
    public static native int RAND_bytes(ByteBuffer buf, int num);

    
    
    public static native PointerByReference RAND_get_rand_method();

    

    @Override
    public PointerByReference _ENGINE_by_id(final String string) {
        return ENGINE_by_id(string);
    }

    @Override
    public int _ENGINE_cleanup() {
        return 0; 
    }

    @Override
    public int _ENGINE_finish(final PointerByReference rdrandEngine) {
        return ENGINE_finish(rdrandEngine);
    }

    @Override
    public int _ENGINE_free(final PointerByReference rdrandEngine) {
        return ENGINE_free(rdrandEngine);
    }

    @Override
    public int _ENGINE_init(final PointerByReference rdrandEngine) {
        return ENGINE_init(rdrandEngine);
    }

    @Override
    public void _ENGINE_load_rdrand() {
        
    }

    @Override
    public int _ENGINE_set_default(final PointerByReference rdrandEngine, final int flags) {
        return ENGINE_set_default(rdrandEngine, flags);
    }

    @Override
    public String _ERR_error_string(final NativeLong err, final char[] buff) {
        return ERR_error_string(err, buff);
    }

    @Override
    public NativeLong _ERR_peek_error() {
        return ERR_peek_error();
    }

    @Override
    public PointerByReference _EVP_aes_128_cbc() {
        return EVP_aes_128_cbc();
    }

    @Override
    public PointerByReference _EVP_aes_128_ctr() {
        return EVP_aes_128_ctr();
    }

    @Override
    public PointerByReference _EVP_aes_192_cbc() {
        return EVP_aes_192_cbc();
    }

    @Override
    public PointerByReference _EVP_aes_192_ctr() {
        return EVP_aes_192_ctr();
    }

    @Override
    public PointerByReference _EVP_aes_256_cbc() {
        return EVP_aes_256_cbc();
    }

    @Override
    public PointerByReference _EVP_aes_256_ctr() {
        return EVP_aes_256_ctr();
    }

    @Override
    public void _EVP_CIPHER_CTX_cleanup(final PointerByReference context) {
        
    }

    @Override
    public void _EVP_CIPHER_CTX_free(final PointerByReference context) {
        EVP_CIPHER_CTX_free(context);
    }

    @Override
    public PointerByReference _EVP_CIPHER_CTX_new() {
        return EVP_CIPHER_CTX_new();
    }

    @Override
    public int _EVP_CIPHER_CTX_set_padding(final PointerByReference context, final int padding) {
        return EVP_CIPHER_CTX_set_padding(context, padding);
    }

    @Override
    public int _EVP_CipherFinal_ex(final PointerByReference context, final ByteBuffer outBuffer, final int[] outlen) {
        return EVP_CipherFinal_ex(context, outBuffer, outlen);
    }

    @Override
    public int _EVP_CipherInit_ex(final PointerByReference context, final PointerByReference algo, final PointerByReference impl, final byte[] encoded,
            final byte[] iv, final int cipherMode) {
        return EVP_CipherInit_ex(context, algo, impl, encoded, iv, cipherMode);
    }

    @Override
    public int _EVP_CipherUpdate(final PointerByReference context, final ByteBuffer outBuffer, final int[] outlen, final ByteBuffer inBuffer,
            final int remaining) {
        return EVP_CipherUpdate(context, outBuffer, outlen, inBuffer, remaining);
    }

    @Override
    public Throwable _INIT_ERROR() {
        return INIT_ERROR;
    }

    @Override
    public boolean _INIT_OK() {
        return INIT_OK;
    }

    @Override
    public String _OpenSSL_version(final int i) {
        return OpenSSL_version(i);
    }

    @Override
    public int _RAND_bytes(final ByteBuffer buf, final int length) {
        return RAND_bytes(buf, length) ;
    }

    @Override
    public PointerByReference _RAND_get_rand_method() {
        return RAND_get_rand_method();
    }

    @Override
    public PointerByReference _RAND_SSLeay() {
        return null; 
    }

}

package jakarta.faces.component;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.myfaces.core.api.shared.lang.Assert;

class _ViewAttributeMap implements Map<String, Object>, Serializable
{
    private static final long serialVersionUID = -9106832109394257866L;

    private static final String RESET_SAVE_STATE_MODE_KEY = 
            "oam.view.resetSaveStateMode";

    
    private static final String RESOURCE_DEPENDENCY_UNIQUE_ID_KEY =
              "oam.view.resourceDependencyUniqueId";
    private static final String UNIQUE_ID_COUNTER_KEY =
              "oam.view.uniqueIdCounter";
    
    private Map<String, Object> _delegate;
    private UIViewRoot _root;

    public _ViewAttributeMap(UIViewRoot root, Map<String, Object> delegate)
    {
        this._delegate = delegate;
        this._root = root;
    }

    @Override
    public int size()
    {
        return _delegate.size();
    }

    @Override
    public boolean isEmpty()
    {
        return _delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key)
    {
        return _delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return _delegate.containsValue(value);
    }

    @Override
    public Object get(Object key)
    {
        checkKey(key);
        int keyLength = ((String)key).length();

        if (RESET_SAVE_STATE_MODE_KEY.length() == keyLength
            && RESET_SAVE_STATE_MODE_KEY.equals(key))
        {
            return _root.getResetSaveStateMode();
        }
        if (RESOURCE_DEPENDENCY_UNIQUE_ID_KEY.length() == keyLength
            && RESOURCE_DEPENDENCY_UNIQUE_ID_KEY.equals(key))
        {
            return _root.isResourceDependencyUniqueId();
        }
        if (UNIQUE_ID_COUNTER_KEY.length() == keyLength
            && UNIQUE_ID_COUNTER_KEY.equals(key))
        {
            return _root.getStateHelper().get(UIViewRoot.PropertyKeys.uniqueIdCounter);
        }
        return _delegate.get(key);
    }

    @Override
    public Object put(String key, Object value)
    {
        int keyLength = key.length();

        if (RESET_SAVE_STATE_MODE_KEY.length() == keyLength
            && RESET_SAVE_STATE_MODE_KEY.equals(key))
        {
            Integer b = _root.getResetSaveStateMode();
            _root.setResetSaveStateMode(value == null ? 0 : (Integer) value);
            return b;
        }
        if (RESOURCE_DEPENDENCY_UNIQUE_ID_KEY.length() == keyLength
            && RESOURCE_DEPENDENCY_UNIQUE_ID_KEY.equals(key))
        {
            boolean b = _root.isResourceDependencyUniqueId();
            _root.setResourceDependencyUniqueId(value == null ? false : (Boolean) value);
            return b;
        }
        if (UNIQUE_ID_COUNTER_KEY.length() == keyLength
            && UNIQUE_ID_COUNTER_KEY.equals(key))
        {
            Integer v = (Integer) _root.getStateHelper().get(UIViewRoot.PropertyKeys.uniqueIdCounter);
            _root.getStateHelper().put(UIViewRoot.PropertyKeys.uniqueIdCounter, value);
            return v;
        }
        return _delegate.put(key, value);
    }

    @Override
    public Object remove(Object key)
    {
        return _delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m)
    {
        _delegate.putAll(m);
    }

    @Override
    public void clear()
    {
        _delegate.clear();
    }

    @Override
    public Set<String> keySet()
    {
        return _delegate.keySet();
    }

    @Override
    public Collection<Object> values()
    {
        return _delegate.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet()
    {
        return _delegate.entrySet();
    }

    @Override
    public boolean equals(Object o)
    {
        return _delegate.equals(o);
    }

    @Override
    public int hashCode()
    {
        return _delegate.hashCode();
    }

    @Override
    public String toString()
    {
        return _delegate.toString();
    }
    
    
    private void checkKey(Object key)
    {
        Assert.notNull(key, "key");

        if (!(key instanceof String))
        {
            throw new ClassCastException("key is not a String");
        }
    }
}