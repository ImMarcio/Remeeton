plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    // necessário para o Firebase/Firestore
    id("com.google.gms.google-services") version "4.4.2" apply false
    // necessário para o Room
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
}
true




