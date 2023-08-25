package net.yuuzu.common

import net.yuuzu.config
import net.yuuzu.security.token.TokenConfig

object JWTSetup {
    val tokenConfig = TokenConfig(
        issuer = config.property("jwt.issuer").getString(),
        audience = config.property("jwt.audience").getString(),
        expiresIn = 7L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )
}