package uk.gov

val valeSync = rootProject.tasks.register(
    "valeSync",
    Exec::class.java
) {
    description = "Lint the project's markdown and text files with Vale."
    group = "verification"
    executable = "vale"
    setArgs(
        listOf("sync"),
    )
    enabled = !project.gradle.startParameter.isOffline
}

val vale = rootProject.tasks.maybeCreate(
    "vale",
    Exec::class.java,
).apply {
    description = "Lint the project's markdown and text files with Vale."
    group = "verification"
    executable = "vale"
    dependsOn(valeSync)
    setArgs(
        listOf(
            "--no-wrap",
            "--config=${rootProject.projectDir}/.vale.ini",
            rootProject.projectDir.toString(),
        ),
    )
}
