package ce.kiran;

import dev.sigstore.KeylessSigner;
import dev.sigstore.TrustedRootProvider;
import dev.sigstore.encryption.signers.Signers;
import dev.sigstore.fulcio.client.FulcioClient;
import dev.sigstore.oidc.client.OidcClients;
import dev.sigstore.rekor.client.RekorClient;
import dev.sigstore.tuf.SigstoreTufClient;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

public class SetDefaults {
    private KeylessSigner.Builder builder = new KeylessSigner.Builder();

    public KeylessSigner setOidcClients(OidcClients clients) throws InvalidAlgorithmParameterException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        this.builder.oidcClients(clients);
        this.setDefaults();
        return builder.build();
    }

    private void setDefaults() {
        this.builder.signer(Signers.newEcdsaSigner())
                .trustedRootProvider(TrustedRootProvider.from(SigstoreTufClient.builder().usePublicGoodInstance()))
                .fulcioUrl(FulcioClient.PUBLIC_GOOD_URI)
                .rekorUrl(RekorClient.PUBLIC_GOOD_URI)
                .minSigningCertificateLifetime(KeylessSigner.DEFAULT_MIN_SIGNING_CERTIFICATE_LIFETIME);
    }
}
