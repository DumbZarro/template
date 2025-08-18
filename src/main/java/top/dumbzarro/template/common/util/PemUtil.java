package top.dumbzarro.template.common.util;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class PemUtil {

    /**
     * 加载RSA密钥对
     *
     * @param privateKeyRes 私钥资源对象（必填）
     * @param publicKeyRes  公钥资源对象（可为null）
     * @return KeyPair 密钥对
     */
    public static KeyPair loadKeyPair(Resource privateKeyRes, Resource publicKeyRes) throws IOException {
        PrivateKey privateKey = loadPrivateKey(privateKeyRes);
        PublicKey publicKey;
        if (publicKeyRes != null) {
            publicKey = loadPublicKey(publicKeyRes);
        } else {
            publicKey = derivePublicKeyFromPrivate((RSAPrivateKey) privateKey);
        }
        return new KeyPair(publicKey, privateKey);
    }

    /**
     * 加载私钥
     */
    private static PrivateKey loadPrivateKey(Resource resource) throws IOException {
        try (PEMParser parser = new PEMParser(new InputStreamReader(resource.getInputStream()))) {
            Object pemObject = parser.readObject();
            if (pemObject instanceof PrivateKeyInfo) {
                return new JcaPEMKeyConverter().getPrivateKey((PrivateKeyInfo) pemObject);
            }
            throw new PEMException("Invalid private key format");
        }
    }

    /**
     * 加载公钥
     */
    private static PublicKey loadPublicKey(Resource resource) throws IOException {
        try (PEMParser parser = new PEMParser(new InputStreamReader(resource.getInputStream()))) {
            Object pemObject = parser.readObject();
            return new JcaPEMKeyConverter().getPublicKey((org.bouncycastle.asn1.x509.SubjectPublicKeyInfo) pemObject);
        }
    }

    /**
     * 支持同时加载独立公钥文件或从私钥推导公钥，当publicKeyRes参数为null时，自动调用derivePublicKeyFromPrivate方法生成公钥。
     */
    private static RSAPublicKey derivePublicKeyFromPrivate(RSAPrivateKey privateKey) {
        return new RSAPublicKey() {
            public BigInteger getPublicExponent() {
                if (privateKey instanceof RSAPrivateCrtKey) { // PKCS#1格式
                    return ((RSAPrivateCrtKey) privateKey).getPublicExponent();
                } else { // 使用RSA标准公钥指数65537
                    return new BigInteger("65537");
                }
            }

            public String getAlgorithm() {
                return "RSA";
            }

            public String getFormat() {
                return "X.509";
            }

            public byte[] getEncoded() {
                return null;
            }

            public BigInteger getModulus() {
                return privateKey.getModulus();
            }
        };
    }
}
