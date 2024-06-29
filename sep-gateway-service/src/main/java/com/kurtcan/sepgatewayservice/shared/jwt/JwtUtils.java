package com.kurtcan.sepgatewayservice.shared.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProperties jwtProperties;

    private final AlgorithmConstraints algorithmConstraints = new AlgorithmConstraints(
            AlgorithmConstraints.ConstraintType.PERMIT,
            AlgorithmIdentifiers.RSA_USING_SHA256
    );

    public boolean isTokenValid(String token) {
        PublicKey publicKey = getPublicKey(jwtProperties.getPublicKeyBase64());

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setVerificationKey(publicKey)
                .setAllowedClockSkewInSeconds(jwtProperties.getAllowedClockSkewInSeconds())
                .setExpectedIssuer(jwtProperties.getIssuer())
                .setSkipDefaultAudienceValidation()
//                .setExpectedAudience(jwtProperties.getAudienceList().toArray(new String[0]))
                .setRequireExpirationTime()
                .setRequireSubject()
                .setJwsAlgorithmConstraints(algorithmConstraints)
                .build();

        try {
            jwtConsumer.processToClaims(token);
            return true;
        } catch (InvalidJwtException e) {
            if (e.hasExpired()) {
                log.warn("JWT expired");
            } else if (e.hasErrorCode(ErrorCodes.AUDIENCE_INVALID)) {
                log.warn("JWT had wrong audience");
            } else {
                log.warn(e.getMessage());
            }
        }

        return false;
    }

    private PublicKey getPublicKey(String base64PublicKey) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            return null;
        }
    }

}