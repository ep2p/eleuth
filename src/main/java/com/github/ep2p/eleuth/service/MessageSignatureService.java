package com.github.ep2p.eleuth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;
    private final BytesPublicKeyGenerator bytesPublicKeyGenerator = new BytesPublicKeyGenerator();

    public MessageSignatureService(NodeInformation nodeInformation, ObjectMapper objectMapper) {
        this.nodeInformation = nodeInformation;
        messageSigner = new MessageSigner(nodeInformation.getKeyPair().getPrivate());
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public <E extends Serializable> SignedData<E> sign(E input, boolean includePublicKey){
        Serializer<String> serializer = new Serializer<>();
        String valueAsString = objectMapper.writeValueAsString(input);
        byte[] serializedData = serializer.serialize(valueAsString);
        byte[] sign = messageSigner.sign(serializedData);
        return new SignedData<E>(input, includePublicKey ? Base64Util.encode(nodeInformation.getKeyPair().getPublic().getEncoded()) : null, Base64Util.encode(sign));
    }

    public <E extends Serializable> void validate(SignedData<E> signedData) throws InvalidSignatureException {
        Assert.notNull(signedData.getPublicKey(), "Public key can not be null");
        this.validate(signedData, getPublicKey(signedData));
    }

    public <E extends Serializable> void validate(SignedData<E> signedData, String publicKey) throws InvalidSignatureException {
        Assert.notNull(signedData.getPublicKey(), "Public key can not be null");
        this.validate(signedData, getPublicKey(publicKey));
    }

    public <E extends Serializable> void validate(SignedData<E> signedData, PublicKey publicKey) throws InvalidSignatureException {
        Assert.notNull(signedData.getSignature(), "Signature can not be null");
        Serializer<String> serializer = new Serializer<>();
        try {
            String valueAsString = objectMapper.writeValueAsString(signedData.getData());
            byte[] serializedData = serializer.serialize(valueAsString);
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

    public PublicKey getPublicKey(SignedData<?> signedData){
        Assert.notNull(signedData.getPublicKey(), "Public key can not be null");
        return bytesPublicKeyGenerator.generate(Base64Util.decode(signedData.getPublicKey()));
    }

    public PublicKey getPublicKey(String encoded){
        Assert.notNull(encoded, "Public key can not be null");
        return bytesPublicKeyGenerator.generate(Base64Util.decode(encoded));
    }

}
