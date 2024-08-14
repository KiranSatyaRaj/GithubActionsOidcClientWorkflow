package ce.kiran;

import dev.sigstore.KeylessSigner;
import dev.sigstore.KeylessSignerException;
import dev.sigstore.bundle.Bundle;
import dev.sigstore.oidc.client.GithubActionsOidcClient;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

public class Main {
    public static void main(String[] args) throws InvalidAlgorithmParameterException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, KeylessSignerException {
        Path filePath = Paths.get("src/main/java/ce/kiran/hello.txt");
        GithubActionsOidcClient client = GithubActionsOidcClient.builder().build();
        KeylessSigner signer = new KeylessSigner.Builder().sigstorePublicDefaults().build();
        Bundle result = signer.signFile(filePath);
        System.out.println("Signature is " + result.getMessageSignature().get().getSignature());
    }
}