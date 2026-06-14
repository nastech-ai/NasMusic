# OpenAI API client for Kotlin (fixed for using Gemini API)

[![Maven Central](https://img.shields.io/maven-central/v/org.nasmusic.gemini-kotlin/openai-client?color=blue&label=Download)](https://central.sonatype.com/namespace/org.nasmusic.gemini-kotlin) [![License](https://img.shields.io/github/license/nastechai/gemini-kotlin?color=yellow)](LICENSE.md) [![Documentation](https://img.shields.io/badge/docs-api-a97bff.svg?logo=kotlin)](https://mouaad.aallam.com/openai-kotlin/)

Kotlin client for [OpenAI's API](https://beta.openai.com/docs/api-reference) with multiplatform and coroutines capabilities, which fixed all errors when using Gemini API.

Forked from [aallam/openai-kotlin](https://github.com/aallam/openai-kotlin)

## 📦 Setup

1. Install OpenAI API Kotlin client by adding the following dependency to your `build.gradle` file:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "org.nasmusic.gemini-kotlin:openai-client:<latest-version>"
}
```

2. Choose and add to your dependencies one of [Ktor's engines](https://ktor.io/docs/http-client-engines.html).

### Everything is the same with [aallam/openai-kotlin](https://github.com/aallam/openai-kotlin). Only changing the namespace of the dependencies to `org.nasmusic.gemini-kotlin`and and fixing this issue [Error when using Gemini OpenAI API · Issue #412 · aallam/openai-kotlin](https://github.com/aallam/openai-kotlin/issues/412)
