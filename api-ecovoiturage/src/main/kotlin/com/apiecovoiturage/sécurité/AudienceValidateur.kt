package com.écovoiturage.écovoiturage.sécurité

import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.util.Assert

class AudienceValidateur(private val audience: String) : OAuth2TokenValidator<Jwt> {

    init {
        Assert.hasText(audience, "audience is null or empty")
    }

    override fun validate(jwt: Jwt): OAuth2TokenValidatorResult {
        val audiences = jwt.audience
        return if (audiences.contains(audience)) {
            OAuth2TokenValidatorResult.success()
        } else {
            val err = OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN)
            OAuth2TokenValidatorResult.failure(err)
        }
    }
}