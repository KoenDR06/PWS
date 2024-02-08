val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

val exposed_version: String by project
val h2_version: String by project
plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.7"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

group = "me.koendev.pws"
version = "0.0.1"

application {
    mainClass.set("me.koendev.pws.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes("Main-Class" to "me.koendev.pws.ApplicationKt")
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-cors-jvm")
    implementation("io.ktor:ktor-server-double-receive-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-resources")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-compression-jvm")
    implementation("io.ktor:ktor-server-default-headers-jvm")
    implementation("io.ktor:ktor-server-forwarded-header-jvm")
    implementation("io.ktor:ktor-server-http-redirect-jvm")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-html-builder")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("com.h2database:h2:$h2_version")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.3.2")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
}

tasks.register<Jar>("fatJarRecipeData") {
    archiveBaseName.set("ConvertRecipeDataToDB")
    archiveVersion.set("0.0.1")
    archiveClassifier.set("fat")

    from(sourceSets.main.get().output)

    // Include runtime classpath dependencies in the JAR
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    manifest {
        attributes["Main-Class"] = "me.koendev.pws.singleuse.ConvertRecipeDataToDbKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Jar>("fatJarTagData") {
    archiveBaseName.set("ConvertTagDataToDB")
    archiveVersion.set("0.0.1")
    archiveClassifier.set("fat")

    from(sourceSets.main.get().output)

    // Include runtime classpath dependencies in the JAR
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    manifest {
        attributes["Main-Class"] = "me.koendev.pws.singleuse.ConvertTagDataToDbKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Jar>("fatJarIngredientsTable") {
    archiveBaseName.set("FillIngredientsTable")
    archiveVersion.set("0.0.1")
    archiveClassifier.set("fat")

    from(sourceSets.main.get().output)

    // Include runtime classpath dependencies in the JAR
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    manifest {
        attributes["Main-Class"] = "me.koendev.pws.singleuse.FillIngredientsTableKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Jar>("fatJarStepTable") {
    archiveBaseName.set("FillStepTable")
    archiveVersion.set("0.0.1")
    archiveClassifier.set("fat")

    from(sourceSets.main.get().output)

    // Include runtime classpath dependencies in the JAR
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    manifest {
        attributes["Main-Class"] = "me.koendev.pws.singleuse.FillStepTableKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Jar>("fatJarRecipeToTag") {
    archiveBaseName.set("RecipeToTagConversion")
    archiveVersion.set("0.0.1")
    archiveClassifier.set("fat")

    from(sourceSets.main.get().output)

    // Include runtime classpath dependencies in the JAR
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    manifest {
        attributes["Main-Class"] = "me.koendev.pws.singleuse.RecipeToTagConversionKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}