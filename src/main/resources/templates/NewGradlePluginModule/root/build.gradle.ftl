apply plugin: 'groovy'

dependencies {
    ${getConfigurationName("compile")} fileTree(dir: 'libs', include: ['*.jar'])
    ${getConfigurationName("compile")} gradleApi()
    ${getConfigurationName("compile")} localGroovy()
}
