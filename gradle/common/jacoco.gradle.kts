val exclusions = listOf(
    "**/databinding/*Binding.*", // Exclude data binding classes
    "**/databinding/*", // General exclusion for all data binding-related files
    "**/R.class", // Exclude R class
    "**/R$*.class", // Exclude inner R classes
    "**/BuildConfig.*", // Exclude BuildConfig
    "**/Manifest*.*", // Exclude Manifest classes
    "**/*Test*.*", // Exclude all test classes
    "android/**/*.*", // Exclude Android auto-generated files
    "**/*Lambda$*.class", // Exclude lambda generated classes
    "**/*Lambda.class", // Exclude lambda classes
    "**/*Lambda*.class", // Exclude all lambda-related classes
    "**/Lambda$*.class", // Exclude specific lambda classes
    "**/*Dagger*.*", // Exclude Dagger generated classes
    "**/*Hilt*.*", // Exclude Hilt generated classes
    "**/Dagger*Component*.*", // Exclude Dagger components
    "**/*Component*.*", // Exclude all Component-related classes
    "**/*_Factory*.*", // Exclude factory classes
    "**/*_MembersInjector*.*", // Exclude member injectors
    "**/*_Provide*Factory*.*", // Exclude provider factory classes
    "**/*Module*.*", // Exclude Dagger modules
    "**/*Module_*Factory.class", // Exclude Module factory classes
    "**/di/module/*", // Exclude DI modules package
    "**/hilt_aggregated_deps/*", // Exclude Hilt aggregated dependencies
    "**/Hilt_*.*", // Exclude Hilt related classes
    "**/*Extensions*.*", // Exclude extension files
    "**/*_Impl*.*", // Exclude generated implementation files
    "**/*_GeneratedInjector*.*", // Exclude Hilt generated injectors
    "**/*MapperImpl*.*", // Exclude Mapper implementation classes
    "**/*_ViewBinding*.*", // Exclude view binding files
    "**/*_Injector*.*", // Exclude Injector related classes
    "**/AutoValue_*.*", // Exclude AutoValue generated classes
    "**/*_ViewModels*.*", // Exclude generated ViewModel files
    "**/*_AssistedFactory*.*", // Exclude Assisted Inject factory classes
    "**/generated/**/*.*", // Exclude generated source files
    "**/*Kt$*.*", // Exclude Kotlin generated classes (e.g., synthetic classes)
    "**/data/network/*", // Exclude network
    "**/presentation/**/*Screen*.*", // Exclude compose screen classes
    "**/presentation/navigation/*", // Exclude navigation
    "**/presentation/theme/*", // Exclude theme
)

val testDebugUnitTest = "testDebugUnitTest"
val jacocoTestReport = "jacocoTestReport"
val jacocoTestCoverageVerification = "jacocoTestCoverageVerification"

tasks.withType(Test::class) {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register<JacocoReport>(jacocoTestReport) {
    dependsOn(listOf(testDebugUnitTest))
    group = "Reporting"
    description = "Execute unit tests, generate and combine Jacoco coverage report"
    reports {
        html.required.set(true)
    }
    sourceDirectories.setFrom(layout.projectDirectory.dir("src/main"))
    classDirectories.setFrom(files(
        fileTree(layout.buildDirectory.dir("intermediates/javac/")) {
            exclude(exclusions)
        },
        fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/")) {
            exclude(exclusions)
        }
    ))
    executionData.setFrom(files(
        fileTree(layout.buildDirectory) { include(listOf("**/*.exec", "**/*.ec")) }
    ))
}

tasks.register<JacocoCoverageVerification>(jacocoTestCoverageVerification) {
    dependsOn(jacocoTestReport)
    group = "Verification"
    description = "Check if the code coverage meets the minimum threshold"
    violationRules {
        rule {
            element = "BUNDLE"
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.3".toBigDecimal()
            }
        }

        rule {
            element = "BUNDLE"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.3".toBigDecimal()
            }
        }
    }
    sourceDirectories.setFrom(layout.projectDirectory.dir("src/main"))
    classDirectories.setFrom(files(
        fileTree(layout.buildDirectory.dir("intermediates/javac/")) {
            exclude(exclusions)
        },
        fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/")) {
            exclude(exclusions)
        }
    ))
    executionData.setFrom(files(
        fileTree(layout.buildDirectory) { include(listOf("**/*.exec", "**/*.ec")) }
    ))
}