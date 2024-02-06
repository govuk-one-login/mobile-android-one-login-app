rootProject.tasks.register<Exec>("vale") {
    description = "Lint the project's markdown and text files with Vale."
    group = "verification"
    executable = "vale"
    args = listOf(
        "--no-wrap",
        rootProject.projectDir.toString(),
        "--config ./.vale.ini ."
    )
}

rootProject.tasks.named("check") {
    dependsOn("vale")
}
