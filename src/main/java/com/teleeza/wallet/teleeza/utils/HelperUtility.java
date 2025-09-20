package com.teleeza.wallet.teleeza.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class HelperUtility {
    @Value("/home/ubuntu/wallet_deployments/certificates")
    private Resource productionCert;

    public static String toBase64String(String value) {
        byte[] data = value.getBytes(StandardCharsets.ISO_8859_1);
        // return Base64.encode(data);
        return Base64.getEncoder().encodeToString(data);
    }

    public static String toJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException exception) {
            return null;
        }
    }

    public static String getTransactionUniqueNumber() {
        RandomStringGenerator stringGenerator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                .build();
        String transactionNumber = stringGenerator.generate(12).toUpperCase();
        return transactionNumber;
    }

    public static String getBillRefNumber() {
        RandomStringGenerator stringGenerator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                .build();
        String transactionNumber = stringGenerator.generate(12).toUpperCase();
        return transactionNumber;
    }

    public static String generateReferralCode() {
        RandomStringGenerator stringGenerator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                .build();

        return stringGenerator.generate(12).toUpperCase();
    }

    // M-Pesa Security Credentials
//    @SneakyThrows
//    public static String getSecurityCredentials(String initiatorPassword) {
//        String encryptedPassword;
//
//        try {
//            Security.addProvider(new BouncyCastleProvider());
//            byte[] input = initiatorPassword.getBytes();
//
//            Resource resource = new ClassPathResource("/ProductionCertificate.cer");
////            Resource resource = new ClassPathResource("SandboxCertificate.cer");
//            InputStream inputStream = resource.getInputStream();
//
////            FileInputStream fin = new FileInputStream(resource.getFile());
//            InputStream fin = HelperUtility.class.getResourceAsStream("/ProductionCertificate.cer");
//            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
//            CertificateFactory cf = CertificateFactory.getInstance("X.509");
//            X509Certificate certificate = (X509Certificate) cf.generateCertificate(fin);
//            PublicKey pk = certificate.getPublicKey();
//            cipher.init(Cipher.ENCRYPT_MODE, pk);
//
//            byte[] cipherText = cipher.doFinal(input);
//
//            // Convert the resulting encrypted byte array into a string using base64 encoding
//            encryptedPassword = Base64.encode(cipherText).trim();
//            return encryptedPassword;
//        } catch (NoSuchAlgorithmException | CertificateException | InvalidKeyException | NoSuchPaddingException |
//                 IllegalBlockSizeException | BadPaddingException | NoSuchProviderException | FileNotFoundException e) {
//            log.error(String.format("Error generating security credentials ->%s", e.getLocalizedMessage()));
//            throw e;
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }

    public static String getStkPushPassword(String shortCode, String passKey, String timestamp) {
        String concatenatedString = String.format("%s%s%s", shortCode, passKey, timestamp);
        return toBase64String(concatenatedString);
    }

    public static String getTransactionTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date());
    }


}
