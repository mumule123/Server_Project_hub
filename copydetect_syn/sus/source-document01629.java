package net.bither.bitherj.crypto.hd;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import net.bither.bitherj.crypto.ECKey;

import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.params.KeyParameter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;


public final class HDUtils {
    private static final Joiner PATH_JOINER = Joiner.on("/");

    static HMac createHmacSha512Digest(byte[] key) {
        SHA512Digest digest = new SHA512Digest();
        HMac hMac = new HMac(digest);
        hMac.init(new KeyParameter(key));
        return hMac;
    }

    static byte[] hmacSha512(HMac hmacSha512, byte[] input) {
        hmacSha512.reset();
        hmacSha512.update(input, 0, input.length);
        byte[] out = new byte[64];
        hmacSha512.doFinal(out, 0);
        return out;
    }

    public static byte[] hmacSha512(byte[] key, byte[] data) {
        return hmacSha512(createHmacSha512Digest(key), data);
    }

    static byte[] toCompressed(byte[] uncompressedPoint) {
        return ECKey.CURVE.getCurve().decodePoint(uncompressedPoint).getEncoded(true);
    }

    static byte[] longTo4ByteArray(long n) {
        byte[] bytes = Arrays.copyOfRange(ByteBuffer.allocate(8).putLong(n).array(), 4, 8);
        assert bytes.length == 4 : bytes.length;
        return bytes;
    }

    public static ImmutableList<ChildNumber> append(List<ChildNumber> path, ChildNumber childNumber) {
        return ImmutableList.<ChildNumber>builder().addAll(path).add(childNumber).build();
    }

    public static String formatPath(List<ChildNumber> path) {
        return PATH_JOINER.join(Iterables.concat(Collections.singleton("M"), path));
    }

    
    public static List<ChildNumber> parsePath(@Nonnull String path) {
        String[] parsedNodes = path.replace("M", "").split("/");
        List<ChildNumber> nodes = new ArrayList<ChildNumber>();

        for (String n : parsedNodes) {
            n = n.replaceAll(" ", "");
            if (n.length() == 0) continue;
            boolean isHard = n.endsWith("H");
            if (isHard) n = n.substring(0, n.length() - 1);
            int nodeNumber = Integer.parseInt(n);
            nodes.add(new ChildNumber(nodeNumber, isHard));
        }

        return nodes;
    }
}


package net.bither.bitherj.db;

import net.bither.bitherj.core.EnterpriseHDMAddress;
import net.bither.bitherj.core.EnterpriseHDMKeychain;

import java.util.List;

public interface IEnterpriseHDMProvider {


    String getEnterpriseEncryptMnemonicSeed(int hdSeedId);

    String getEnterpriseEncryptHDSeed(int hdSeedId);

    String getEnterpriseHDFristAddress(int hdSeedId);

    boolean isEnterpriseHDMSeedFromXRandom(int hdSeedId);

    void addEnterpriseHDMAddress(List<EnterpriseHDMAddress> enterpriseHDMAddressList);

    List<EnterpriseHDMAddress> getEnterpriseHDMAddress(EnterpriseHDMKeychain keychain);

    void addMultiSignSet(int n, int m);

    void updateSyncComplete(EnterpriseHDMAddress enterpriseHDMAddress);

    List<Integer> getEnterpriseHDMKeychainIds();

    int getEnterpriseHDMSeedId();

    int getPubCount();

    int getThreshold();
}