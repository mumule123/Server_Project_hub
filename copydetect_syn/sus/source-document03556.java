package org.apache.commons.crypto.jna;

import java.nio.ByteBuffer;

import org.apache.commons.crypto.Crypto;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.PointerByReference;

final class OpenSsl20XNativeJna implements OpenSslInterfaceNativeJna {

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

    
    public static native int ENGINE_cleanup();

    
    public static native int ENGINE_finish(PointerByReference e);

    
    public static native int ENGINE_free(PointerByReference e);

    
    public static native int ENGINE_init(PointerByReference e);

    
    public static native int ENGINE_set_default(PointerByReference e, int flags);

    
    public static native String ERR_error_string(NativeLong err, char[] null_);

    
    
    public static native void ERR_load_crypto_strings();

    
    public static native NativeLong ERR_peek_error();

    
    public static native PointerByReference EVP_aes_128_cbc();

    
    public static native PointerByReference EVP_aes_128_ctr();

    
    public static native PointerByReference EVP_aes_192_cbc();

    
    public static native PointerByReference EVP_aes_192_ctr();

    
    public static native PointerByReference EVP_aes_256_cbc();

    
    public static native PointerByReference EVP_aes_256_ctr();

    
    public static native void EVP_CIPHER_CTX_cleanup(PointerByReference c);

    
    public static native void EVP_CIPHER_CTX_free(PointerByReference c);

    
    
    public static native void EVP_CIPHER_CTX_init(PointerByReference p);

    
    public static native PointerByReference EVP_CIPHER_CTX_new();

    
    public static native int EVP_CIPHER_CTX_set_padding(PointerByReference c, int pad);

    
    public static native int EVP_CipherFinal_ex(PointerByReference ctx, ByteBuffer bout,
            int[] outl);

    
    public static native int EVP_CipherInit_ex(PointerByReference ctx, PointerByReference cipher,
            PointerByReference impl, byte[] key, byte[] iv, int enc);

    

    
    public static native int EVP_CipherUpdate(PointerByReference ctx, ByteBuffer bout, int[] outl,
            ByteBuffer in, int inl);

    
    public static native int RAND_bytes(ByteBuffer buf, int num);

    
    
    public static native PointerByReference RAND_get_rand_method();

    
    public static native PointerByReference RAND_SSLeay();

    
    public static native NativeLong SSLeay();

    
    public static native String SSLeay_version(int type);

    

    @Override
    public PointerByReference _ENGINE_by_id(final String string) {
        return ENGINE_by_id(string);
    }

    @Override
    public int _ENGINE_cleanup() {
        return ENGINE_cleanup();
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
        EVP_CIPHER_CTX_cleanup(context);
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
        return SSLeay_version(i);
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
        return RAND_SSLeay();
    }
}

package jakarta.faces.view;

import java.beans.BeanInfo;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import jakarta.faces.application.Resource;
import jakarta.faces.application.ResourceVisitOption;
import jakarta.faces.application.ViewVisitOption;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;


public abstract class ViewDeclarationLanguage
{
    
    public static final String FACELETS_VIEW_DECLARATION_LANGUAGE_ID = "java.faces.Facelets";
    
    public abstract void buildView(FacesContext context, UIViewRoot view) throws IOException;

    public abstract UIViewRoot createView(FacesContext context, String viewId);

    public abstract BeanInfo getComponentMetadata(FacesContext context, Resource componentResource);

    public abstract Resource getScriptComponentResource(FacesContext context, Resource componentResource);
    
    public abstract StateManagementStrategy getStateManagementStrategy(FacesContext context, String viewId); 

    public abstract ViewMetadata getViewMetadata(FacesContext context, String viewId);

    public abstract void renderView(FacesContext context, UIViewRoot view) throws IOException;

    public abstract UIViewRoot restoreView(FacesContext context, String viewId);
    
    public void retargetAttachedObjects(FacesContext context, UIComponent topLevelComponent,
                                        List<AttachedObjectHandler> handlers)
    {
        throw new UnsupportedOperationException(); 
    }

    public void retargetMethodExpressions(FacesContext context, UIComponent topLevelComponent)
    {
        throw new UnsupportedOperationException(); 
    }
    
    
    public String getId()
    {
        return this.getClass().getName();
    }
    
    
    public boolean viewExists(FacesContext facesContext, String viewId)
    {
        try
        {
            return facesContext.getExternalContext().getResource(viewId) != null;
        }
        catch (MalformedURLException e)
        {
            Logger log = Logger.getLogger(ViewDeclarationLanguage.class.getName());
            if (log.isLoggable(Level.SEVERE))
            {
                log.log(Level.SEVERE, "Malformed URL viewId: "+viewId, e);
            }
        }
        return false;
    }
    
    
    public UIComponent createComponent(FacesContext context,
                                   String taglibURI,
                                   String tagName,
                                   Map<String,Object> attributes)
    {
        return null;
    }
    
    
    public List<String> calculateResourceLibraryContracts(FacesContext context,
                                                      String viewId)
    {
        return null;
    }
    
    
    public Stream<java.lang.String> getViews(FacesContext facesContext, String path, ViewVisitOption... options)
    {
        return getViews(facesContext, path, Integer.MAX_VALUE, options);
    }
    
    
    
    public Stream<java.lang.String> getViews(FacesContext facesContext, String path, 
            int maxDepth, ViewVisitOption... options)
    {
        
        
        
        
        
        
        
        
        return facesContext.getApplication().getResourceHandler().getViewResources(
                facesContext, path, maxDepth, ResourceVisitOption.TOP_LEVEL_VIEWS_ONLY);
    }
}