pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io") }
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        maven("https://jogamp.org/deployment/maven")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        maven("https://jogamp.org/deployment/maven")
        // NasTech local maven repo (publish here after branding)
        maven(url = "https://raw.githubusercontent.com/nastech-ai/maven-repo/master/repository")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// All modules are now embedded in this repo — no git submodules needed
val coreDir     = File(rootDir, "core")
val serviceDir  = File(rootDir, "core/service")
val mediaDir    = File(rootDir, "core/media")
val libsDir     = File(rootDir, "libs")

rootProject.name = "NasMusic"

include(
    // ── App modules ─────────────────────────────────────────────────────────
    ":androidApp",
    ":composeApp",
    ":desktopApp",

    // ── Core modules (from core/) ────────────────────────────────────────────
    ":common",
    ":data",
    ":domain",

    // ── Service modules (from core/service/ — add when available) ────────────
    ":ktorExt",
    ":kotlinYtmusicScraper",
    ":spotify",
    ":aiService",
    ":lyricsService",
    ":kizzy",

    // ── Media modules (from core/media/ — add when available) ────────────────
    ":media-jvm",
    ":media-jvm-ui",
    ":media3",
    ":media3-ui",

    // ── Crashlytics shim ─────────────────────────────────────────────────────
    ":crashlytics",
    ":crashlytics-empty",

    // ── NasTech libs (from libs/) ─────────────────────────────────────────────
    ":nowplayingcenter",
    ":jmtc",
    ":openai-client",
    ":openai-core",
    ":BravePipeExtractor",
    ":mediaserviceinterfaces",
    ":youtubeapi",
    ":sharedutils",
    ":mediasession-kt",
)

// ── Core ────────────────────────────────────────────────────────────────────
project(":common").projectDir = File(coreDir, "common")
project(":data").projectDir   = File(coreDir, "data")
project(":domain").projectDir = File(coreDir, "domain")

// ── Service (will exist once core/service/ modules are added) ───────────────
if (serviceDir.exists()) {
    project(":ktorExt").projectDir            = File(serviceDir, "ktorExt")
    project(":aiService").projectDir          = File(serviceDir, "aiService")
    project(":lyricsService").projectDir      = File(serviceDir, "lyricsService")
    project(":kotlinYtmusicScraper").projectDir = File(serviceDir, "kotlinYtmusicScraper")
    project(":spotify").projectDir            = File(serviceDir, "spotify")
    project(":kizzy").projectDir              = File(serviceDir, "kizzy")
}

// ── Media (will exist once core/media/ modules are added) ───────────────────
if (mediaDir.exists()) {
    project(":media-jvm").projectDir    = File(mediaDir, "media-jvm")
    project(":media-jvm-ui").projectDir = File(mediaDir, "media-jvm-ui")
    project(":media3").projectDir       = File(mediaDir, "media3")
    project(":media3-ui").projectDir    = File(mediaDir, "media3-ui")
}

// ── NasTech Libraries (embedded in libs/) ────────────────────────────────────
project(":nowplayingcenter").projectDir   = File(libsDir, "NowPlayingCenter/nowplayingcenter")
project(":jmtc").projectDir               = File(libsDir, "NowPlayingCenter/jmtc")
project(":openai-client").projectDir      = File(libsDir, "gemini-kotlin/openai-client")
project(":openai-core").projectDir        = File(libsDir, "gemini-kotlin/openai-core")
project(":BravePipeExtractor").projectDir = File(libsDir, "BravePipeExtractor/extractor")
project(":mediaserviceinterfaces").projectDir = File(libsDir, "MediaServiceCore/mediaserviceinterfaces")
project(":youtubeapi").projectDir         = File(libsDir, "MediaServiceCore/youtubeapi")
project(":sharedutils").projectDir        = File(libsDir, "SharedModules/sharedutils")
project(":mediasession-kt").projectDir    = File(libsDir, "mediasession-kt/library")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
