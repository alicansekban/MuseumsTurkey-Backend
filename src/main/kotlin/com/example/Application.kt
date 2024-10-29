package com.example

import com.example.data.dataSource.MongoUserDataSource
import com.example.plugins.*
import com.example.security.hashing.SH256HashingService
import com.example.security.token.JwtTokenService
import com.example.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val mongoPw = System.getenv("MONGO_PW")
    val dbName = "ktor-museums"
    val db = KMongo.createClient(
        connectionString = "mongodb+srv://MuseumsUserDb:$mongoPw@cluster0.agw4a.mongodb.net/$dbName?retryWrites=true&w=majority&appName=Cluster0"
    ).coroutine
        .getDatabase(dbName)
    val userDataSource = MongoUserDataSource(db)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SH256HashingService()

    configureMonitoring()
    configureSerialization()
    configureSecurity(tokenConfig)
    configureRouting(userDataSource, hashingService, tokenService, tokenConfig)
}
