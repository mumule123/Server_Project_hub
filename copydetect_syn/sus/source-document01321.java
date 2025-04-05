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
package net.sourceforge.fidocadj.primitives;

import java.io.*;
import java.util.*;

import net.sourceforge.fidocadj.circuit.*;
import net.sourceforge.fidocadj.circuit.controllers.*;
import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.circuit.views.*;
import net.sourceforge.fidocadj.dialogs.*;
import net.sourceforge.fidocadj.export.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.layers.*;
import net.sourceforge.fidocadj.graphic.*;



public final class PrimitiveMacro extends GraphicPrimitive
{

    static final int N_POINTS=3;
    private final Map<String, MacroDesc> library;
    private final Vector<LayerDesc> layers;
    private int o;              
    private boolean m;          
    private boolean drawOnlyPads;
    private int drawOnlyLayer;
    private boolean alreadyExported;
    private DrawingModel macro;
    private final MapCoordinates macroCoord;
    private boolean selected;
    private String macroName;
    private String macroDesc;
    private boolean exportInvisible;


    private Drawing drawingAgent;

    
    private int x1, y1;

    
    public void setExportInvisible(boolean s)
    {
        exportInvisible = s;
    }

    
    public int getControlPointNumber()
    {
        return N_POINTS;
    }

    
    public PrimitiveMacro(Map<String, MacroDesc>lib, Vector<LayerDesc> l,
            String f, int size)
    {
        super();
        library=lib;
        layers=l;
        drawOnlyPads=false;
        drawOnlyLayer=-1;
        macro=new DrawingModel();
        macroCoord=new MapCoordinates();
        changed=true;

        initPrimitive(-1, f, size);

        macroStore(layers);
    }

    
    public PrimitiveMacro(Map<String, MacroDesc> lib, Vector<LayerDesc> l,
         int x, int y, String key_t,
         String na, int xa, int ya, String va, int xv, int yv, String macroF,
         int macroS, int oo, boolean mm)
        throws IOException
    {
        super();
        initPrimitive(-1, macroF, macroS);
        library=lib;
        layers=l;
        String key=key_t.toLowerCase(new Locale("en"));
        macro=new DrawingModel();
        macroCoord=new MapCoordinates();
        changed=true;
        setMacroFontSize(macroS);
        o=oo;
        m=mm;

        
        virtualPoint[0].x=x;
        virtualPoint[0].y=y;
        virtualPoint[1].x=xa;
        virtualPoint[1].y=ya;
        virtualPoint[2].x=xv;
        virtualPoint[2].y=yv;

        name=na;
        value=va;

        MacroDesc macro=(MacroDesc)library.get(key);

        
        
        if (macro==null){
            IOException G=new IOException("Unrecognized macro "
                                          + key);
            throw G;
        }
        macroDesc = macro.description;
        macroName = key;
        macroFont = macroF;

        macroStore(layers);
    }


    
    public boolean containsLayer(int l)
    {
        return macro.containsLayer(l);
    }

    
    private void drawMacroContents(GraphicsInterface g, MapCoordinates coordSys,
                              Vector layerV)
    {
        
        if(changed) {
            changed = false;
            x1=virtualPoint[0].x;
            y1=virtualPoint[0].y;

            macroCoord.setXMagnitude(coordSys.getXMagnitude());
            macroCoord.setYMagnitude(coordSys.getYMagnitude());

            macroCoord.setXCenter(coordSys.mapXr(x1,y1));
            macroCoord.setYCenter(coordSys.mapYr(x1,y1));
            macroCoord.setOrientation((o+coordSys.getOrientation())%4);
            macroCoord.mirror=m ^ coordSys.mirror;
            macroCoord.isMacro=true;
            macroCoord.resetMinMax();

            macro.setChanged(true);
        }

        if(getSelected()) {
            new SelectionActions(macro).setSelectionAll(true);
            selected = true;
        } else if (selected) {
            new SelectionActions(macro).setSelectionAll(false);
            selected = false;
        }

        macro.setDrawOnlyLayer(drawOnlyLayer);
        macro.setDrawOnlyPads(drawOnlyPads);

        drawingAgent = new Drawing(macro);
        drawingAgent.draw(g,macroCoord);

        if (macroCoord.getXMax()>macroCoord.getXMin() &&
            macroCoord.getYMax()>macroCoord.getYMin())
        {
            coordSys.trackPoint(macroCoord.getXMax(),macroCoord.getYMax());
            coordSys.trackPoint(macroCoord.getXMin(),macroCoord.getYMin());
        }
    }

    
    public void setChanged(boolean c)
    {
        super.setChanged(c);
        macro.setChanged(c);
    }

    
    private void macroStore(Vector<LayerDesc> layerV)
    {
        macro.setLibrary(library);          
        macro.setLayers(layerV);    
        changed=true;

        if (macroDesc!=null) {
            ParserActions pa = new ParserActions(macro);
            pa.parseString(new StringBuffer(macroDesc));
            
        }
    }

    
    public void setLayers(Vector<LayerDesc> layerV)
    {
        macro.setLayers(layerV);
    }

    
    public void draw(GraphicsInterface g, MapCoordinates coordSys,
                              Vector layerV)
    {
        
        
        setLayer(0);
        if(selectLayer(g,layerV))
            drawText(g, coordSys, layerV, drawOnlyLayer);

        drawMacroContents(g, coordSys, layerV);
    }

    
    public void setDrawOnlyPads(boolean pd)
    {
        drawOnlyPads=pd;
    }

    

    public void setDrawOnlyLayer(int la)
    {
        drawOnlyLayer=la;
    }

    
    public int getMaxLayer()
    {
        return macro.getMaxLayer();
    }

    
    public void parseTokens(String[] tokens, int N)
        throws IOException
    {
        
        changed=true;
        if (tokens[0].equals("MC")) {   
            if (N<6) {
                IOException E=new IOException("bad arguments on MC");
                throw E;
            }
            
            

            virtualPoint[0].x=Integer.parseInt(tokens[1]);
            virtualPoint[0].y=Integer.parseInt(tokens[2]);
            virtualPoint[1].x=virtualPoint[0].x+10;
            virtualPoint[1].y=virtualPoint[0].y+10;
            virtualPoint[2].x=virtualPoint[0].x+10;
            virtualPoint[2].y=virtualPoint[0].y+5;
            o=Integer.parseInt(tokens[3]);  
            m=Integer.parseInt(tokens[4])==1;  
            macroName=tokens[5];

            
            

            for (int i=6; i<N; ++i)
                macroName+=" "+tokens[i];

            
            

            macroName=macroName.toLowerCase(new Locale("en"));

            
            MacroDesc macro=(MacroDesc)library.get(macroName);

            if (macro==null){

                IOException G=new IOException("Unrecognized macro '"
                                              + macroName+"'");
                throw G;
            }
            macroDesc = macro.description;
            macroStore(layers);

        } else {
            IOException E=new IOException("MC: Invalid primitive:"+tokens[0]+
                                          " programming error?");
            throw E;
        }

    }

    
    public boolean needsHoles()
    {
        return drawingAgent.getNeedHoles();
    }

    
    public int getDistanceToPoint(int px, int py)
    {
        

        int x1=virtualPoint[0].x;
        int y1=virtualPoint[0].y;
        int dt=Integer.MAX_VALUE;

        

        if(checkText(px, py))
            return 0;

        

        int vx=px-x1+100;
        int vy= py-y1+100;

        
        

        if(m) {
            switch(o){
                case 1:
                    vx=py-y1+100;
                    vy=px-x1+100;
                    break;

                case 2:
                    vx=px-x1+100;
                    vy=-(py-y1)+100;
                    break;

                case 3:
                    vx=-(py-y1)+100;
                    vy=-(px-x1)+100;
                    break;

                case 0:
                    vx=-(px-x1)+100;
                    vy=py-y1+100;
                    break;

                default:
                    vx=0;
                    vy=0;
                    break;
            }
        } else {
            switch(o){
                case 1:
                    vx=py-y1+100;
                    vy=-(px-x1)+100;
                    break;

                case 2:
                    vx=-(px-x1)+100;
                    vy=-(py-y1)+100;
                    break;

                case 3:
                    vx=-(py-y1)+100;
                    vy=px-x1+100;
                    break;

                case 0:
                    vx=px-x1+100;
                    vy=py-y1+100;
                    break;

                default:
                    vx= 0;
                    vy= 0;
                    break;
            }
        }

        if (macroDesc==null)
            System.out.println("1-Unrecognized macro "+
                    "WARNING this can be a programming problem...");
        else {
            SelectionActions sa = new SelectionActions(macro);
            EditorActions edt=new EditorActions(macro, sa, null);
            return Math.min(edt.distancePrimitive(vx, vy), dt);
        }
        return Integer.MAX_VALUE;
    }

    
    public boolean selectRect(int px, int py, int w, int h)
    {
        
        
        SelectionActions sa = new SelectionActions(macro);
        EditorActions edt=new EditorActions(macro, sa, null);
        if (edt.distancePrimitive(0, 0)<Integer.MAX_VALUE) {
            return super.selectRect(px, py, w, h);
        } else {
            return false;
        }
    }

    
    public int getOrientation()
    {
        return o;
    }

    
    public boolean isMirrored()
    {
        return m;
    }

    
    public void rotatePrimitive(boolean bCounterClockWise,int ix, int iy)
    {
        super.rotatePrimitive(bCounterClockWise, ix, iy);

        if (bCounterClockWise)
            o=(o+3)%4;
        else
            o=++o%4;

        changed=true;
    }


    
    public void mirrorPrimitive(int xpos)
    {
        super.mirrorPrimitive(xpos);
        m ^= true;
        changed=true;
    }

    
    public String toString(boolean extensions)
    {
        String mirror="0";
        if(m)
            mirror="1";

        String s="MC "+virtualPoint[0].x+" "+virtualPoint[0].y+" "+o+" "
                +mirror+" "+macroName+"\n";

        s+=saveText(extensions);

        return s;
    }

    
    public Vector<ParameterDescription> getControls()
    {
        Vector<ParameterDescription> v=new Vector<ParameterDescription>(10);
        ParameterDescription pd = new ParameterDescription();

        pd.parameter=name;
        pd.description=Globals.messages.getString("ctrl_name");
        pd.isExtension = true;
        v.add(pd);

        pd = new ParameterDescription();

        pd.parameter=value;
        pd.description=Globals.messages.getString("ctrl_value");
        pd.isExtension = true;

        v.add(pd);
        return v;
    }

    
    public int setControls(Vector<ParameterDescription> v)
    {
        int i=0;
        ParameterDescription pd;

        changed=true;

        pd=(ParameterDescription)v.get(i);
        ++i;
        
        if (pd.parameter instanceof String)
            name=((String)pd.parameter);
        else
            System.out.println("Warning: unexpected parameter!"+pd);

        pd=(ParameterDescription)v.get(i);
        ++i;
        
        if (pd.parameter instanceof String)
            value=((String)pd.parameter);
        else
            System.out.println("Warning: unexpected parameter!"+pd);

        return i;
    }

    
    public void resetExport()
    {
        alreadyExported=false;
    }

    
    public void export(ExportInterface exp, MapCoordinates cs)
        throws IOException
    {
        if(alreadyExported)
            return;

        

        if (exp.exportMacro(cs.mapX(virtualPoint[0].x, virtualPoint[0].y),
            cs.mapY(virtualPoint[0].x, virtualPoint[0].y),
            m, o*90, macroName, macroDesc, name,
            cs.mapX(virtualPoint[1].x, virtualPoint[1].y),
            cs.mapY(virtualPoint[1].x, virtualPoint[1].y),
            value,
            cs.mapX(virtualPoint[2].x, virtualPoint[2].y),
            cs.mapY(virtualPoint[2].x, virtualPoint[2].y),
            macroFont,
            (int)(cs.mapYr(getMacroFontSize(),getMacroFontSize())-
                cs.mapYr(0,0)),
            library))
        {
            alreadyExported = true;
            return;
        }
        

        int x1=virtualPoint[0].x;
        int y1=virtualPoint[0].y;

        MapCoordinates macroCoord=new MapCoordinates();

        macroCoord.setXMagnitude(cs.getXMagnitude());
        macroCoord.setYMagnitude(cs.getYMagnitude());

        macroCoord.setXCenter(cs.mapXr(x1,y1));
        macroCoord.setYCenter(cs.mapYr(x1,y1));

        macroCoord.setOrientation((o+cs.getOrientation())%4);
        macroCoord.mirror=m ^ cs.mirror;
        macroCoord.isMacro=true;

        macro.setDrawOnlyLayer(drawOnlyLayer);

        if(getSelected())
            new SelectionActions(macro).setSelectionAll(true);


        macro.setDrawOnlyPads(drawOnlyPads);
        new Export(macro).exportDrawing(exp, exportInvisible, macroCoord);
        exportText(exp, cs, drawOnlyLayer);

    }
        
    public int getNameVirtualPointNumber()
    {
        return 1;
    }

    
    public  int getValueVirtualPointNumber()
    {
        return 2;
    }

    
    public String getMacroDesc()
    {
        return macroDesc;
    }


    
    public void setMacroDesc(String macroDesc)
    {
        this.macroDesc = macroDesc;
    }

}