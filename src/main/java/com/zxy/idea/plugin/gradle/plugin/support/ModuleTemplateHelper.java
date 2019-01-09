package com.zxy.idea.plugin.gradle.plugin.support;

import com.android.utils.FileUtils;
import com.android.utils.Pair;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by zhengxiaoyong on 2019/01/08.
 */
public class ModuleTemplateHelper {

    private static File checkoutTemplate() {
        IdeaPluginDescriptor descriptor = PluginManager.getPlugin(PluginId.getId(Constants.PLUGIN_ID));
        if (descriptor == null)
            return null;

        File pluginPath = descriptor.getPath();
        if (!pluginPath.exists())
            return null;

        try {
            File pluginFile;
            if (pluginPath.isFile() && pluginPath.getName().endsWith(Constants.DOT_JAR)) {
                pluginFile = pluginPath;
            } else {
                pluginFile = new File(pluginPath, Constants.PLUGIN_BASE_NAME + "-" + descriptor.getVersion() + Constants.DOT_JAR);
                if (!pluginFile.exists())
                    pluginFile = new File(descriptor.getPath(), "lib" + File.separator + Constants.PLUGIN_BASE_NAME + "-" + descriptor.getVersion() + Constants.DOT_JAR);
            }

            File destDir = new File(pluginFile.getParent() + File.separator + pluginFile.getName().replace(Constants.DOT_JAR, ""));
            if (destDir.exists() && destDir.isDirectory())
                FileUtils.deletePath(destDir);

            destDir.mkdirs();

            JarFile jar = new JarFile(pluginFile);
            Enumeration<JarEntry> jarEntries = jar.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();

                if (jarEntry.isDirectory()) {
                    File outDirectory = new File(destDir, jarEntry.getName());
                    outDirectory.mkdirs();
                } else {
                    File outFile = new File(destDir, jarEntry.getName());
                    outFile.getParentFile().mkdirs();
                    InputStream is = jar.getInputStream(jarEntry);
                    FileOutputStream fos = new FileOutputStream(outFile);

                    while (is.available() > 0) {
                        fos.write(is.read());
                    }

                    fos.close();
                    is.close();
                }
            }

            jar.close();
            return destDir;
        } catch (Exception e) {
            //ignore.
        }
        return null;
    }

    public static Pair<File, File> getGradlePluginTemplate() {
        File dir = checkoutTemplate();
        if (dir == null)
            return null;
        return Pair.of(
                new File(dir, "templates" + File.separator + Constants.TEMPLATE_GRADLE_PLUGIN)
                , dir);
    }

    public static Pair<File, File> getBuildSrcTemplate() {
        File dir = checkoutTemplate();
        if (dir == null)
            return null;
        return Pair.of(new File(dir, "templates" + File.separator + Constants.TEMPLATE_BUILDSRC), dir);
    }

}
