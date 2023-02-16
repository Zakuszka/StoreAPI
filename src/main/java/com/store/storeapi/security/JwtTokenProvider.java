package com.store.storeapi.security;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.store.storeapi.exception.BaseException;
import com.store.storeapi.exception.InternalServerErrorException;
import com.store.storeapi.ws.error.ErrorMessage;

@Component
public class JwtTokenProvider implements JwtDecoder {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

	private static final String CLAIMS = "token";

	@Value("${password-jwtSecret}")
	private String passwordJwtSecret;

	@Value("${password-jwtPayloadEncriptionKey}")
	private String passwordPayloadEncriptionSecret;

	@Value("${password-jwtExpirationInMs}")
	private Long passwordTokenExpirationInMs;


	public Integer getPasswordTokenExpirationInMinutes() {
		return (int) (passwordTokenExpirationInMs / 60 / 1000);
	}

	public String generatePasswordToken(Map<String, String> claims) throws BaseException {
		return generateJWEToken(claims, passwordJwtSecret, passwordPayloadEncriptionSecret, passwordTokenExpirationInMs, CLAIMS);
	}

	public Map<String, String> verifyAndGetKeyFromPasswordToken(String jweTokenString) throws BaseException {
		return verifyAndGetKeyFromToken(jweTokenString, passwordJwtSecret, passwordPayloadEncriptionSecret, CLAIMS);
	}

	private static String generateJWEToken(Map<String, String> data, String jwtSecret, String payloadEncriptionSecret, Long tokenExpirationInMs, String claims)
			throws BaseException {
		try {
			Date currentDate = new Date();

			JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().issueTime(currentDate).expirationTime(new Date(currentDate.getTime() + tokenExpirationInMs))
					.claim(claims, data).build();


			JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
			SignedJWT signedJWT = new SignedJWT(header, claimsSet);
			JWSSigner signer = new MACSigner(jwtSecret);
			signedJWT.sign(signer);

			JWEObject jweObject =
					new JWEObject(new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256).contentType("JWT").build(), new Payload(signedJWT));
			JWEEncrypter encrypter = new DirectEncrypter(payloadEncriptionSecret.getBytes());
			jweObject.encrypt(encrypter);

			return jweObject.serialize();
		} catch (Exception exception) {
			LOGGER.error("[generateChangePasswordToken] Failed to generate encrypted JWE with nested JWT: ", exception);
			throw new InternalServerErrorException(ErrorMessage.INTERNAL_SERVER_ERROR.getCode(), ErrorMessage.INTERNAL_SERVER_ERROR.getMessage());
		}
	}


	private static HashMap<String, String> verifyAndGetKeyFromToken(String jweTokenString, String jwtSecret, String payloadEncriptionSecret, String claims)
			throws BaseException {
		JWEObject jweObject = null;
		try {
			jweObject = JWEObject.parse(jweTokenString);
		} catch (Exception exception) {
			LOGGER.warn(String.format("[verifyAndGetKeyFromToken] Failed to parse JWE from token string, trying to parse as JWT."));
		}

		try {
			SignedJWT signedJWT;
			if (jweObject != null) {
				JWEDecrypter decrypter = new DirectDecrypter(payloadEncriptionSecret.getBytes());
				jweObject.decrypt(decrypter);

				signedJWT = jweObject.getPayload().toSignedJWT();
			} else {
				signedJWT = SignedJWT.parse(jweTokenString);
			}

			JWSVerifier verifier = new MACVerifier(jwtSecret);
			signedJWT.verify(verifier);

			JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
			DefaultJWTClaimsVerifier<SecurityContext> claimsSetVerifier = new DefaultJWTClaimsVerifier<>(null, claimsSet, null);
			claimsSetVerifier.verify(claimsSet, null);

			return (HashMap<String, String>) claimsSet.getClaim(claims);
		} catch (Exception exception) {
			LOGGER.warn(String.format("[verifyAndGetKeyFromToken] Failed to read token: %s", exception.getMessage()));
			throw new InternalServerErrorException(ErrorMessage.INVALID_TOKEN.getCode(),
					ErrorMessage.INVALID_TOKEN.getMessage());
		}
	}

	@Override
	public Jwt decode(String token) throws JwtException {
		try {
			JWEObject jweObject = JWEObject.parse(token);

			SignedJWT signedJWT;
			if (jweObject != null) {
				JWEDecrypter decrypter = new DirectDecrypter(passwordPayloadEncriptionSecret.getBytes());
				jweObject.decrypt(decrypter);

				signedJWT = jweObject.getPayload().toSignedJWT();
			} else {
				signedJWT = SignedJWT.parse(token);
			}

			JWSVerifier verifier = new MACVerifier(passwordJwtSecret);
			signedJWT.verify(verifier);
			JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
			DefaultJWTClaimsVerifier<SecurityContext> claimsSetVerifier = new DefaultJWTClaimsVerifier<>(null, claimsSet, null);
			claimsSetVerifier.verify(claimsSet, null);

			Map<String, Object> headers = new LinkedHashMap<>(signedJWT.getHeader().toJSONObject());
			Map<String, Object> claims = (Map<String, Object>) claimsSet.getClaim(CLAIMS);

			for (String key : claimsSet.getClaims().keySet()) {
				Object value = claimsSet.getClaims().get(key);
				if (key.equals("exp") || key.equals("iat")) {
					value = ((Date) value).toInstant();
				}

				claims.put(key, value);
			}

			return Jwt.withTokenValue(token).headers(h -> h.putAll(headers)).claims(c -> c.putAll(claims)).build();
		} catch (Exception exception) {
			LOGGER.warn(String.format("[decode] Failed to decode token"));
			throw new BadJwtException("Invalid token");
		}

	}

}
