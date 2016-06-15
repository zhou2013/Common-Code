package zzhao.code.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * 首先载入jvm默认的证书库， 再将一些需要额外信任的网站证书添加到里面去
 * 大部分代码都是open jdk的默认实现
 * @author zzhao
 * @version 2013-12-18
 */
public class SecurityUtils {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    private static final String ALIAS_PREFIX = "zzhao";

    public static void reloadTrustManager() {
        try {
            KeyStore keyStore = getMergeKeyStore();
            TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustFactory.init(keyStore);
            TrustManager[] trustManagers = trustFactory.getTrustManagers();

            // initialize an ssl context to use these managers and set as default
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            SSLContext.setDefault(sslContext);
            logger.info("**************cacerts loaded. *****************");
        } catch (Exception e) {
            logger.error("set trust manager failed!", e);
        }
    }

    private static KeyStore getMergeKeyStore() {
        try {
            KeyStore systemKeyStore = getCacertsKeyStore();
            mergeKeyStore(systemKeyStore, "/test.cacert", "test");
            return systemKeyStore;
        }catch(Exception e){
            logger.error("failed to load keyStore!", e);
        }
        return null;
    }

    private static void mergeKeyStore(KeyStore targetKeyStore, String keyStorToMerger, String keyStoreAlias) {
        try {
            // 这边是载入我们自己的信任网站
            InputStream trustStream = SecurityUtils.class.getResourceAsStream(keyStorToMerger);
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] trustPassword = "changeit".toCharArray();
            trustStore.load(trustStream, trustPassword);

            // 把我们证书中受信任的网站加到系统的证书中
            Set<X509Certificate> certs = getTrustedCerts(trustStore);
            int count = 0;
            for (X509Certificate cert : certs) {
                // alias 不要重复
                targetKeyStore.setCertificateEntry(ALIAS_PREFIX + keyStoreAlias + count, cert);
                count++;
            }
        } catch (Exception e) {
            logger.error("merge failed!", e);
        }
    }

    /**
     * Returns the keystore with the configured CA certificates.
     */
    private static KeyStore getCacertsKeyStore() throws Exception {
        String storeFileName = null;
        File storeFile = null;
        FileInputStream fis = null;
        String defaultTrustStoreType;
        String defaultTrustStoreProvider;
        final HashMap<String, String> props = Maps.newHashMap();
        final String sep = File.separator;
        KeyStore ks = null;

        AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws Exception {
                props.put("trustStore", System.getProperty("javax.net.ssl.trustStore"));
                props.put("javaHome", System.getProperty("java.home"));
                props.put("trustStoreType", System.getProperty("javax.net.ssl.trustStoreType", KeyStore.getDefaultType()));
                props.put("trustStoreProvider", System.getProperty("javax.net.ssl.trustStoreProvider", ""));
                props.put("trustStorePasswd", System.getProperty("javax.net.ssl.trustStorePassword", ""));
                return null;
            }
        });

        storeFileName = props.get("trustStore");
        if (!"NONE".equals(storeFileName)) {
            if (storeFileName != null) {
                storeFile = new File(storeFileName);
                fis = getFileInputStream(storeFile);
            } else {
                String javaHome = props.get("javaHome");
                storeFile = new File(javaHome + sep + "lib" + sep + "security" + sep + "jssecacerts");
                if ((fis = getFileInputStream(storeFile)) == null) {
                    storeFile = new File(javaHome + sep + "lib" + sep + "security" + sep + "cacerts");
                    fis = getFileInputStream(storeFile);
                }
            }

            if (fis != null) {
                storeFileName = storeFile.getPath();
            } else {
                storeFileName = "No File Available, using empty keystore.";
            }
        }

        defaultTrustStoreType = props.get("trustStoreType");
        defaultTrustStoreProvider = props.get("trustStoreProvider");
        /*
         * Try to initialize trust store.
         */
        if (defaultTrustStoreType.length() != 0) {
            if (defaultTrustStoreProvider.length() == 0) {
                ks = KeyStore.getInstance(defaultTrustStoreType);
            } else {
                ks = KeyStore.getInstance(defaultTrustStoreType, defaultTrustStoreProvider);
            }
            char[] passwd = null;
            String defaultTrustStorePassword = props.get("trustStorePasswd");
            if (defaultTrustStorePassword.length() != 0)
                passwd = defaultTrustStorePassword.toCharArray();

            ks.load(fis, passwd);
            if (passwd != null) {
                for (int i = 0; i < passwd.length; i++) {
                    passwd[i] = (char) 0;
                }
            }
        }
        if (fis != null) {
            fis.close();
        }
        return ks;
    }

    private static FileInputStream getFileInputStream(final File file) throws Exception {
        return AccessController.doPrivileged(new PrivilegedExceptionAction<FileInputStream>() {
            public FileInputStream run() throws Exception {
                try {
                    if (file.exists()) {
                        return new FileInputStream(file);
                    } else {
                        return null;
                    }
                } catch (FileNotFoundException e) {
                    // couldn't find it, oh well.
                    return null;
                }
            }
        });
    }

    private static Set<X509Certificate> getTrustedCerts(KeyStore ks) {
        Set<X509Certificate> set = new HashSet<X509Certificate>();
        try {
            for (Enumeration<String> e = ks.aliases(); e.hasMoreElements();) {
                String alias = e.nextElement();
                if (ks.isCertificateEntry(alias)) {
                    Certificate cert = ks.getCertificate(alias);
                    if (cert instanceof X509Certificate) {
                        set.add((X509Certificate) cert);
                    }
                } else if (ks.isKeyEntry(alias)) {
                    Certificate[] certs = ks.getCertificateChain(alias);
                    if ((certs != null) && (certs.length > 0) && (certs[0] instanceof X509Certificate)) {
                        set.add((X509Certificate) certs[0]);
                    }
                }
            }
        } catch (KeyStoreException e) {

        }
        return set;
    }
}
