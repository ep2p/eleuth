package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.config.KeyGenerationConfig;
import com.github.ep2p.eleuth.exception.InvalidSignatureException;
import com.github.ep2p.eleuth.model.dto.RingMemberProofDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.node.NodeInformation;
import com.github.ep2p.eleuth.util.Base64Util;
import com.github.ep2p.encore.helper.ChallengedRingKeyVerifierWrapper;
import com.github.ep2p.encore.helper.DefaultRingKeyVerifier;
import com.github.ep2p.encore.helper.RingKeyVerifier;
import com.github.ep2p.encore.key.BytesPublicKeyGenerator;
import com.github.ep2p.encore.key.PubHashUserIdPartial128Generator;
import com.github.ep2p.encore.key.UserIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

import static com.github.ep2p.eleuth.config.KeyGenerationConfig.PARTIAL_RING_KEY_PART_SIZE;

@Service
public class NodeValidatorService {
    private final MessageSignatureService messageSignatureService;
    private final UserIdGenerator<BigInteger> userIdGenerator;
    private final BytesPublicKeyGenerator bytesPublicKeyGenerator;
    private final RingKeyVerifier ringKeyVerifier;
    private final NodeInformation nodeInformation;

    @Autowired
    public NodeValidatorService(MessageSignatureService messageSignatureService, UserIdGenerator<BigInteger> userIdGenerator, NodeInformation nodeInformation) {
        this.messageSignatureService = messageSignatureService;
        this.userIdGenerator = userIdGenerator;
        this.nodeInformation = nodeInformation;
        this.bytesPublicKeyGenerator = new BytesPublicKeyGenerator();
        ringKeyVerifier = new ChallengedRingKeyVerifierWrapper(KeyGenerationConfig.RING_CHALLENGE_INT, new DefaultRingKeyVerifier(new PubHashUserIdPartial128Generator(PARTIAL_RING_KEY_PART_SIZE)),userIdGenerator);
    }

    public boolean isValidRingNode(SignedData<NodeDto> signedNodeDto){
        try {
            messageSignatureService.validate(signedNodeDto);
            BigInteger bigInteger = signedNodeDto.getData().getId();
            return
                    bigInteger.toString().endsWith(KeyGenerationConfig.RING_CHALLENGE_STR) &&
                    userIdGenerator.generate(bytesPublicKeyGenerator.generate(Base64Util.decode(signedNodeDto.getPublicKey()))).equals(bigInteger);
        } catch (InvalidSignatureException e) {
            return false;
        }
    }

    public boolean isValidRingNode(SignedData<NodeDto> signedNodeDto, SignedData<RingMemberProofDto> ringMemberProofDtoSignedData){
        boolean verifiedRingKey = ringKeyVerifier.verify(bytesPublicKeyGenerator.generate(Base64Util.decode(ringMemberProofDtoSignedData.getPublicKey())), Base64Util.decode(signedNodeDto.getSignature()), ringMemberProofDtoSignedData.getData().getKey(), ringMemberProofDtoSignedData.getData().getPart());
        return isValidRingNode(signedNodeDto) && nodeInformation.getRingKey().equals(ringMemberProofDtoSignedData.getData().getKey()) && verifiedRingKey;
    }

}
