<?xml version="1.0"?>
<recipe>
    <mkdir at="${escapeXmlAttribute(resOut)}/META-INF" />
    <mkdir at="${escapeXmlAttribute(resOut)}/META-INF/gradle-plugins" />

    <merge from="root/settings.gradle.ftl"
             to="${escapeXmlAttribute(topOut)}/settings.gradle" />
    <instantiate from="root/build.gradle.ftl"
                   to="${escapeXmlAttribute(projectOut)}/build.gradle" />
    <instantiate from="root/src/plugin_package/Placeholder.groovy.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${className}.groovy" />

    <instantiate from="root/resources/META-INF/gradle-plugins/placeholder.properties.ftl"
                   to="${escapeXmlAttribute(resOut)}/META-INF/gradle-plugins/${className?lower_case}.properties" />
<#if makeIgnore>
    <copy from="root/gitignore"
            to="${escapeXmlAttribute(projectOut)}/.gitignore" />
</#if>

	<mkdir at="${escapeXmlAttribute(projectOut)}/libs" />
</recipe>
