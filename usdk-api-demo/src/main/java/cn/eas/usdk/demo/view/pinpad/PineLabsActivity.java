package cn.eas.usdk.demo.view.pinpad;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.pinpad.KeySystem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.BytesUtil;
import cn.eas.usdk.demo.util.FileUtil;

/**
 * Remote key download scheme customized by PINELABS
 * <p>
 * 1.  loadPineLabsRootPK
 * Preset the public key of signature, need to use overseas version terminal.
 * <p>
 * 2.  updatePineLabsPKCertifacate
 * Update preset public key, The application (server) hold the corresponding private key Pr_kms.
 * <p>
 * 3.  generatePineLabsRsa
 * Get terminalRandomKeyCertificate, get the public key in the certificate(Pu_terminal).
 * Using Pu_terminal to encrypt plaintext key block to get KkmsBlock.
 * Using Pr_kms to sign  Pr_kms then get signature.
 * <p>
 * 4.  loadPineLabsSignedKey
 * Pass in KkmsBlock and signature, if the validation passes, write the key to the specified key area.
 */
public class PineLabsActivity extends BasePinpadActivity {

    private EditText etKeyId;

    @Override
    public int getKeySystem() {
        return KeySystem.KS_MKSK;
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_pinpad_pinelabs);
        setTitle("Pinpad Pine Labs Module");
        initView();

        open();
    }

    private void initView() {
        etKeyId = bindViewById(R.id.etKeyId);
    }

    public void loadPineLabsRootPK(View v) {
        outputBlueText(">>> loadPineLabsRootPK");
        try {
			// pinelabs_root_pk_signed is a file, include KMS root certificate and Ingenico signature
            InputStream inputStream = getAssets().open("pinelabs_root_pk_signed");
            byte[] signedKmsRootKey = new byte[inputStream.available()];
            inputStream.read(signedKmsRootKey);
            // Verifies the signature of data representative of the KMS Root public key (RSA, public part,KPubKMS). Stores the KMS Root public key (RSA, public part,KPubKMS) in the secure area.
            boolean isSucc = pinpad.loadPineLabsRootPK(signedKmsRootKey);
            if (isSucc) {
                outputText("loadPineLabsRootPK success");
            } else {
                outputPinpadError("loadPineLabsRootPK fail");
            }
        } catch (IOException e) {
            outputRedText("IOException: " + e.getMessage());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void updatePineLabsPKCertifacate(View v) {
        outputBlueText(">>> updatePineLabsPKCertifacate");
        try {
			// cacert.cer is a certificate,it is signed by previously KMS Root private key (RSA, private part,KPrivKMS)
            InputStream inputStream = getAssets().open("cacert.cer");
            byte[] kmsKeyCertificate = new byte[inputStream.available()];
            inputStream.read(kmsKeyCertificate);
		    // Verifies the signature of the X509 certificate using the previously, loaded KMS Root key (RSA, public part) or the last loaded KMS key (RSA, public part)
            // Replace the last stored KMS key by KMS key included in the certificate
            boolean isSucc = pinpad.updatePineLabsPKCertifacate(kmsKeyCertificate);
            if (isSucc) {
                outputText("updatePineLabsPKCertifacate success");
            } else {
                outputPinpadError("updatePineLabsPKCertifacate fail");
            }
        } catch (IOException e) {
            outputRedText("IOException: " + e.getMessage());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void generatePineLabsRsa(View v) {
        outputBlueText(">>> generatePineLabsRsa");
        try {
            BytesValue termRandomKeyCert = new BytesValue();
            BytesValue termSubCA = new BytesValue();
            BytesValue termCA = new BytesValue();
			//Generates a RSA key pairs(KPrivTer and KPubTer),stores the private key in secured area,Exports its public key with a X.509 certificate. And exports KPubSub,KPubIs,KPubTer;
            boolean isSucc = pinpad.generatePineLabsRsa(termRandomKeyCert, termSubCA, termCA);
            if (isSucc) {
                outputText("generatePineLabsRsa success");
            } else {
                outputPinpadError("generatePineLabsRsa fail");
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void loadPineLabsSignedKey(View v) {
        try {
            outputBlueText(">>> generatePineLabsRsa");
            BytesValue termRandomKeyCrt = new BytesValue();
            BytesValue termSubCA = new BytesValue();
            BytesValue termCA = new BytesValue();
            boolean isSucc = pinpad.generatePineLabsRsa(termRandomKeyCrt, termSubCA, termCA);
            if (!isSucc) {
                outputPinpadError("generatePineLabsRsa fail");
                return;
            }
            outputText("generatePineLabsRsa success");

            outputBlueText(">>> loadPineLabsSignedKey");
            byte[] kmsBlock = getKMSBlock(termRandomKeyCrt.getData(), termSubCA.getData(), termCA.getData());
            byte[] signData = getSignData(kmsBlock);
            isSucc = pinpad.loadPineLabsSignedKey(getKeyId(), signData, kmsBlock);
            if (!isSucc) {
                outputPinpadError("loadPineLabsSignedKey fail");
                return;
            }
            outputText("loadPineLabsSignedKey success");

            checkKCV();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private byte[] getKMSBlock(byte[] termRandomKeyCrt, byte[] termSubCA, byte[] termCA) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate X509TerminalRandomKeyCert = (X509Certificate) cf.generateCertificate(
                    new ByteArrayInputStream(termRandomKeyCrt));
            X509Certificate X509terminalSubCA = (X509Certificate) cf.generateCertificate(
                    new ByteArrayInputStream(termSubCA));
            X509Certificate X509terminalCA = (X509Certificate) cf.generateCertificate(
                    new ByteArrayInputStream(termCA));

            X509TerminalRandomKeyCert.verify(X509terminalSubCA.getPublicKey());
            X509terminalSubCA.verify(X509terminalCA.getPublicKey());

            saveFileForCheck(termRandomKeyCrt, termSubCA, termCA);

            // ======================== construct key block ========================
            byte[] key_value = new byte[16];
            for (int i = 0; i < key_value.length; i++) {
                key_value[i] = 0x31;
            }

            byte[] plain_text_key_block = new byte[64];
            plain_text_key_block[0] = 0x30;
            plain_text_key_block[1] = (byte) (plain_text_key_block.length - 2);
            plain_text_key_block[2] = 2;
            plain_text_key_block[3] = (byte) key_value.length;
            System.arraycopy(key_value, 0, plain_text_key_block, 4, key_value.length);

            //key block (Kkms+type+length) ciphered with KPubTer RSA public key.
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, X509TerminalRandomKeyCert.getPublicKey());
            return cipher.doFinal(plain_text_key_block, 0, plain_text_key_block.length);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Save the certificate file for verification by the underlying developers
     */
    private void saveFileForCheck(byte[] termRandomKeyCrt, byte[] termSubCA, byte[] termCA) {
        String path = "/sdcard/usdk/";
        FileUtil.saveDataToFile(path, "termRandomKeyCrt.cer", termRandomKeyCrt);
        FileUtil.saveDataToFile(path, "termSubCA.cer", termSubCA);
        FileUtil.saveDataToFile(path, "termCA.cer", termCA);
    }

    private byte[] getSignData(byte[] kmsBlock) {
        //Signs key block with KPrivKMS root private key (In this demo, KPrivKMS root private key is generated by programmers and solidified in code.)
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
            PrivateKey privatekey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(getEncodeKey()));

            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initSign(privatekey);
            signature.update(kmsBlock);
            return signature.sign();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] getEncodeKey() {
        // ============================== construct signature ===================================
        String pemString = "MIIEpAIBAAKCAQEA4VlGSxKftBhs4hKRC3xAstHskwMpZByy8TG+COZmFWN5YD3/tZlVPDRr+DrwRHYAvrT5dTcf2US4R4VZI61+XtPVp4zNEVEgQiQOTqjdsQqGhZ2m/ON8BCY20Fn1focBd5FGJsJRVFlvAp2oieWxs0uqYmsa3W3nqE316ZidXZovHnuGuXLQzFGNaLi4WACrcuZF9n4ZPypLt6es60KZLF6LKYowGGK9lLImw4hAKPlkTtGE4nrTMRtqZHgnaAPSzQnGHJTZHzYfb+O8NZx6Lx1BV2E5q1bE03OcjC/41idmsZLZxKfq16Nu+M1fgDj/kdSuvt/vV7/MZmzuXkJ0ywIDAQABAoIBAFh7mls969FmSdKW4bb1yd2tCcxL0IPLtQN5uZl7Bhhd37nHflFK7KngF4j3c4IeJ3q6Sr0YsPu5vPXwSSj2JYmMefb5FUg4Z4cw6yE9sxaiAGIUng3sMLTcYGd8+yT8U7okI7xyTkbleu43A4dp1lWFX1iXn4vUbMKsIePgFeI7PIheVVdBYowVss9kIYO4uxNnx7BOBEb/vjLq/tCZyvPwIFBHsdACYRpZe5U0cewD9eM6jCF0u1oU9TfOD/8reI/IJ+vXM3PDgoyOsCgObVuN+knuSDUPu6ffwQVbvOLRyRj/LEjrcB/SzPJNucSWGdiFeeTNJ+UiRAyT07OU7yECgYEA+RhbO9nZj9aWbidBQgpHMjz34V3gtFpofp3SKUiUSrob7TSAylnDF/lNzCoogY/ozUCIWpDFzbQwRfOn0iL8+dTreQwpugTGkU7iySI0IMqWVHgr+FRyF5viRxrSeWQkVivyKBevyAfrK3PzrSi6EcF1NgOOK0Xf9Ckf6M7CUJsCgYEA55hoUjgpSZSVTLVsv6yFAhF7ZkG1kpsekTcmWM/eurTiIDTYWfBdAIR0j3Um433behq0+LyaZtL6cydzvv9+Siib+JpcTjgFveZqbKWSiS9AH+rpWSRvnhwG1ZiN1ZKEMcqTqLEASIDCwGYsS/r/aDtS9mLZOaVld/iecib8t5ECgYEA9jm7RWhMhMBrpqsq+KrMQb6+kImJqv2LOU2sBp715wAcxtcVT/B2xuXqnxw1Og0U5H8bydo9jN7GsP7vLurjAdoUgW0iefxqhAxkZMlomdlnzEuVuz1OIkQ24m6DCveQa5W5IyR239XkAun1P394nkCLKj9Nczz46ZMVNb3bmm0CgYBWUR2Al2H8BJ7oqi3P+pLQRIfDgvYToDDyWqW2MDOJTuWAr3F7g/SFvthap4VJAJ2OkumkU1cQv82MocWoCD2fkicfrxh8JrEtJ/W+bK+C5dN2ke1KxiO7wCJj9anXUfZnQGNsWhwK6cKsRuh3oSMUTBgNsKXZsGVzI3yaeyFxoQKBgQDOpGC1feciiWOXE+86CtvLHc1QaImHbjP6fb/6CxQtXhULkkSBw0U+9TaJoKhmb5MNeamvK/m4HSIHQXCCBAdEARl3CGnxcGMmvCntnPTsmWtIzsDoEQldxK3H8slbcks/+6sfvEntVLCwYf+dXuItsY/GW7W2rdeYcojOyZmW6A==\n";
        return Base64.decode(pemString, Base64.DEFAULT);
    }

    private int getKeyId() {
        String keyId = etKeyId.getText().toString();
        if (keyId.isEmpty()) {
            keyId = etKeyId.getHint().toString();
        }
        return Integer.parseInt(keyId);
    }

    private void checkKCV() throws RemoteException {
        outputBlueText(">>> calcKCV");
        byte[] kcv = pinpad.calcKCV(getKeyId());
        byte[] actualKcv = new byte[3];
        System.arraycopy(kcv, 0, actualKcv, 0, 3);
        outputText("kcv: 40826A");
        outputText("actualKcv:" + BytesUtil.bytes2HexString(actualKcv));
    }
}
