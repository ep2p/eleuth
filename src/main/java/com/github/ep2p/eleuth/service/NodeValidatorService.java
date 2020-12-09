package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.config.KeyGenerationConfig;
import com.github.ep2p.eleuth.exception.InvalidSignatureException;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.util.Base64Util;
import com.github.ep2p.encore.key.BytesPublicKeyGenerator;
import com.github.ep2p.encore.key.UserIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class NodeValidatorService {
    private final MessageSignatureService messageSignatureService;
    private final UserIdGenerator<BigInteger> userIdGenerator;
    private final BytesPublicKeyGenerator bytesPublicKeyGenerator;

    @Autowired
    public NodeValidatorService(MessageSignatureService messageSignatureService, UserIdGenerator<BigInteger> userIdGenerator) {
        this.messageSignatureService = messageSignatureService;
        this.userIdGenerator = userIdGenerator;
        this.bytesPublicKeyGenerator = new BytesPublicKeyGenerator();
    }

    public boolean isValidRingNode(SignedData<NodeDto> signedNodeDto){
        try {
            messageSignatureService.validate(signedNodeDto);
            BigInteger bigInteger = signedNodeDto.getData().getId();
            return
                    bigInteger.toString().endsWith(KeyGenerationConfig.RING_CHALLANGE_STR) &&
                    userIdGenerator.generate(bytesPublicKeyGenerator.generate(Base64Util.decode(signedNodeDto.getPublicKey()))).equals(bigInteger);
        } catch (InvalidSignatureException e) {
            return false;
        }
    }

}
