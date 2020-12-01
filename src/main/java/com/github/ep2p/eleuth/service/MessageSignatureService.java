package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.exception.InvalidSignatureException;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.node.NodeInformation;
import com.github.ep2p.eleuth.util.Base64Util;
import com.github.ep2p.encore.helper.MessageSigner;
import com.github.ep2p.encore.helper.Serializer;
import com.github.ep2p.encore.helper.SignatureVerifier;
import com.github.ep2p.encore.key.BytesPublicKeyGenerator;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.PublicKey;

@Service
public class MessageSignatureService {
    private final MessageSigner messageSigner;
    private final NodeInformation nodeInformation;
    private final BytesPublicKeyGenerator bytesPublicKeyGenerator = new BytesPublicKeyGenerator();

    public MessageSignatureService(NodeInformation nodeInformation) {
        this.nodeInformation = nodeInformation;
        messageSigner = new MessageSigner(nodeInformation.getKeyPair().getPrivate());
    }

    @SneakyThrows
    public <E extends Serializable> SignedData<E> sign(E input, boolean includePublicKey){
        Serializer<E> serializer = new Serializer<>();
        byte[] serializedData = serializer.serialize(input);
        byte[] sign = messageSigner.sign(serializedData);
        return new SignedData<E>(input, includePublicKey ? Base64Util.encode(nodeInformation.getKeyPair().getPublic().getEncoded()) : null, Base64Util.encode(sign));
    }

    public <E extends Serializable> void validate(SignedData<E> signedData) throws InvalidSignatureException {
        Assert.notNull(signedData.getPublicKey(), "Public key can not be null");
        this.validate(signedData, bytesPublicKeyGenerator.generate(Base64Util.decode(signedData.getPublicKey())));
    }

    public <E extends Serializable> void validate(SignedData<E> signedData, PublicKey publicKey) throws InvalidSignatureException {
        Assert.notNull(signedData.getSignature(), "Signature can not be null");
        Serializer<E> serializer = new Serializer<>();
        try {
            byte[] serializedData = serializer.serialize(signedData.getData());
            byte[] signature = Base64Util.decode(signedData.getSignature());
            if (!SignatureVerifier.verify(publicKey, signature, serializedData)) {
                throw new InvalidSignatureException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new InvalidSignatureException(e);
        }
    }

}
