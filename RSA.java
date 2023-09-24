import java.security.*;

import javax.crypto.Cipher;

public class RSA {
    private static final String ALGORITMO = "RSA";

    // Gera um par de chaves público e privado aleatório
    public static KeyPair gerarParChaves() throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator geradorParChaves = KeyPairGenerator.getInstance(ALGORITMO);
        // O par gerado terá um tamanho de 2048 bits
        geradorParChaves.initialize(2048, secureRandom);
        return geradorParChaves.generateKeyPair();
    }

    // Usa a mensagem original em texto simples e a chave pública, criptografa a
    // mensagem
    public static byte[] criptografar(byte[] dados, PublicKey chavePublica) throws Exception {
        Cipher cifra = Cipher.getInstance(ALGORITMO);
        cifra.init(Cipher.ENCRYPT_MODE, chavePublica);
        return cifra.doFinal(dados);
    }

    // Usa a mensagem cifrada e a chave privada, descriptografa a mensagem
    public static byte[] descriptografar(byte[] dadosCifrados, PrivateKey chavePrivada) throws Exception {
        Cipher cifra = Cipher.getInstance(ALGORITMO);
        cifra.init(Cipher.DECRYPT_MODE, chavePrivada);
        return cifra.doFinal(dadosCifrados);
    }
}
