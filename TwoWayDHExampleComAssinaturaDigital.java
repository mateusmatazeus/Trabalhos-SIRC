import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class TwoWayDHExampleComAssinaturaDigital{
    private static BigInteger g512 = new BigInteger(
            "153d5d6172adb43045b68ae8e1de1070b6137005686d29d3d73a7"
            + "749199681ee5b212c9b96bfdcfa5b20cd5e3fd2044895d609cf9b"
            + "410b7a0f12ca1cb9a428cc", 16);

    private static BigInteger p512 = new BigInteger(
            "9494fec095f3b85ee286542b3836fc81a5dd0a0349b4c239dd387"
            + "44d488cf8e31db8bcb7d33b41abb9e5a33cca9144b1cef332c94b"
            + "f0573bf047a3aca98cdf3b", 16);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        DHParameterSpec dhParams = new DHParameterSpec(p512, g512);

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH", "BC");
        keyGen.initialize(dhParams, new SecureRandom());

        KeyPair aPair = keyGen.generateKeyPair();
        KeyPair bPair = keyGen.generateKeyPair();

        KeyAgreement aKeyAgree = KeyAgreement.getInstance("DH", "BC");
        aKeyAgree.init(aPair.getPrivate());
        aKeyAgree.doPhase(bPair.getPublic(), true);

        KeyAgreement bKeyAgree = KeyAgreement.getInstance("DH", "BC");
        bKeyAgree.init(bPair.getPrivate());
        bKeyAgree.doPhase(aPair.getPublic(), true);

        MessageDigest hash = MessageDigest.getInstance("SHA-256", "BC");
        byte[] aShared = hash.digest(aKeyAgree.generateSecret());
        byte[] bShared = hash.digest(bKeyAgree.generateSecret());
        
        Signature signature = Signature.getInstance("SHA256withDSA", "BC");
        signature.initSign(aPair.getPrivate(), new SecureRandom());

        byte[] message = "Esta mensagem eh segura".getBytes();
        signature.update(message);
        byte[] signatureBytes = signature.sign();

        signature.initVerify(bPair.getPublic());
        signature.update(message);

        if (signature.verify(signatureBytes)) {
            System.out.println("Verificacao de assinatura bem sucedida.");
        } else {
            System.out.println("Verificacao de assinatura falhou.");
        }
    }
}
