package net.yuuzu.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import net.yuuzu.common.JWTSetup
import net.yuuzu.config
import net.yuuzu.data.model.user.UserDataSource
import org.koin.java.KoinJavaComponent.inject

fun Application.configureSecurity() {
    // db
    val userDataSource: UserDataSource by inject(UserDataSource::class.java)
    // Please read the jwt property from the config file if you are using EngineMain
    val jwtAudience = JWTSetup.tokenConfig.audience
    val jwtDomain = config.property("jwt.domain").getString()
    val jwtRealm = config.property("jwt.realm").getString()
    val jwtSecret = System.getenv("JWT_SECRET")

    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asString()
                val user = userDataSource.getUserById(userId)

                if (credential.payload.audience.contains(jwtAudience) && user != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
