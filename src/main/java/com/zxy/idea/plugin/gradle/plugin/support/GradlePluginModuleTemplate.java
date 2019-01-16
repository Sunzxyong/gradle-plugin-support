package com.zxy.idea.plugin.gradle.plugin.support;

import com.android.tools.idea.projectsystem.AndroidModuleTemplate;
import com.android.tools.idea.projectsystem.NamedModuleTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhengxiaoyong on 2019/01/07.
 */
public class GradlePluginModuleTemplate implements AndroidModuleTemplate {

    private File mModuleRoot;

    private File mSrcRoot;

    private File mResDirectory;

    public static NamedModuleTemplate createDefaultTemplateAt(@NotNull File moduleRoot) {
        File baseSrcDir = new File(moduleRoot, "src");
        File baseFlavourDir = new File(baseSrcDir, "main");
        GradlePluginModuleTemplate paths = new GradlePluginModuleTemplate();
        paths.mModuleRoot = moduleRoot;
        paths.mSrcRoot = new File(baseFlavourDir, "groovy");
        paths.mResDirectory = new File(baseFlavourDir, "resources");
        return new NamedModuleTemplate("main", paths);
    }

    private static File appendPackageToRoot(@Nullable File root, @Nullable String packageName) {
        if (root != null && packageName != null) {
            String packagePath = packageName.replace('.', File.separatorChar);
            return new File(root, packagePath);
        } else {
            return root;
        }
    }

    @Nullable
    @Override
    public File getModuleRoot() {
        return mModuleRoot;
    }

    @Nullable
    @Override
    public File getSrcDirectory(String packageName) {
        return appendPackageToRoot(mSrcRoot, packageName);
    }

    @Nullable
    @Override
    public File getTestDirectory(String s) {
        return null;
    }

    @Nullable
    public File getResDirectory() {
        return mResDirectory;
    }

    @Nullable
    @Override
    public File getAidlDirectory(String s) {
        return null;
    }

    @Nullable
    @Override
    public File getManifestDirectory() {
        return null;
    }

    @NotNull
    public List<File> getResDirectories() {
        try {
            return Arrays.asList(mResDirectory.listFiles());
        } catch (Exception e) {
        }
        return Collections.singletonList(mResDirectory);
    }
}
