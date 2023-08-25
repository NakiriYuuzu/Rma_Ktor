package net.yuuzu.route

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.yuuzu.common.JWTSetup
import net.yuuzu.data.model.user.User
import net.yuuzu.data.model.user.UserDataSource
import net.yuuzu.data.request.AuthRequest
import net.yuuzu.data.response.AuthResponse
import net.yuuzu.security.hashing.HashingService
import net.yuuzu.security.hashing.SaltedHash
import net.yuuzu.security.token.TokenClaim
import net.yuuzu.security.token.TokenService
import org.apache.commons.codec.digest.DigestUtils


fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
) {
    post("/signup") {
        val request = kotlin.runCatching { call.receiveNullable<AuthRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Request body is missing")
            return@post
        }

        val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
        val isPwTooShort = request.password.length < 3
        if(areFieldsBlank || isPwTooShort) {
            call.respond(HttpStatusCode.Conflict, "Username or password is blank or password is too short")
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            username = request.username,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )

        val wasAcknowledged = userDataSource.insertUser(user)
        if(!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict, "Username already exists")
            return@post
        }

        call.respond(HttpStatusCode.OK)
    }
}

fun Route.signIn(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
) {
    post("signin") {
        val request = kotlin.runCatching { call.receiveNullable<AuthRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUserByUsername(request.username)
        if(user == null) {
            // response error with json
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if(!isValidPassword) {
            println("Entered hash: ${DigestUtils.sha256Hex("${user.salt}${request.password}")}, Hashed PW: ${user.password}")
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        val token = tokenService.generate(
            config = JWTSetup.tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )
    }
}

fun Route.authenticate(
    userDataSource: UserDataSource
) {
    authenticate {
        get("authenticate") {
            val userId = call.principal<JWTPrincipal>()?.getClaim("userId", String::class)

            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Missing user ID"))
                return@get
            }

            val userData = userDataSource.getUserById(userId)
            if (userData == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "User not found"))
                return@get
            }

            call.respond(HttpStatusCode.OK, mapOf("message" to "Authenticated"))
        }
    }
}