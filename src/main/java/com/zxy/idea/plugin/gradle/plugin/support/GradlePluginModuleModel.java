package com.zxy.idea.plugin.gradle.plugin.support;

import com.android.tools.idea.npw.template.TemplateHandle;
import com.android.tools.idea.npw.template.TemplateValueInjector;
import com.android.tools.idea.observable.core.BoolProperty;
import com.android.tools.idea.observable.core.BoolValueProperty;
import com.android.tools.idea.observable.core.StringProperty;
import com.android.tools.idea.observable.core.StringValueProperty;
import com.android.tools.idea.templates.Template;
import com.android.tools.idea.templates.TemplateMetadata;
import com.android.tools.idea.templates.TemplateUtils;
import com.android.tools.idea.templates.recipe.RenderingContext;
import com.android.tools.idea.wizard.model.WizardModel;
import com.android.utils.FileUtils;
import com.google.common.collect.Maps;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhengxiaoyong on 2019/01/03.
 */
public class GradlePluginModuleModel extends WizardModel {

    private final Project mProject;

    private final TemplateHandle mTemplateHandle;

    private final StringProperty mPluginName = new StringValueProperty("plugin");

    private final StringProperty mPackageName = new StringValueProperty();

    private final StringProperty mClassName = new StringValueProperty("MyPlugin");

    private final BoolProperty mCreateGitIgnore = new BoolValueProperty(true);

    private File mTemplateDirectory;

    public GradlePluginModuleModel(@NotNull Project project, @NotNull TemplateHandle templateHandle, File templateDirectory) {
        mProject = project;
        mTemplateHandle = templateHandle;
        mTemplateDirectory = templateDirectory;
    }

    @NotNull
    public Project getProject() {
        return mProject;
    }

    @NotNull
    public StringProperty pluginNameName() {
        return mPluginName;
    }

    @NotNull
    public StringProperty packageName() {
        return mPackageName;
    }

    @NotNull
    public StringProperty className() {
        return mClassName;
    }

    @NotNull
    public BoolProperty createGitIgnore() {
        return mCreateGitIgnore;
    }

    @Override
    protected void handleFinished() {
        createModule();
        try {
            if (mTemplateDirectory != null)
                FileUtils.deletePath(mTemplateDirectory);
        } catch (Exception e) {
        }
    }

    public void createModule() {
        File moduleRoot = new File(mProject.getBasePath(), pluginNameName().get());

        Map<String, Object> templateValues = Maps.newHashMap();

        new TemplateValueInjector(templateValues)
                .setModuleRoots(GradlePluginModuleTemplate.createDefaultTemplateAt(moduleRoot).getPaths(), packageName().get())
                .setProjectDefaults(mProject, "", false)
                .setJavaVersion(mProject)
                .addGradleVersions(mProject);

        templateValues.put(TemplateMetadata.ATTR_CLASS_NAME, className().get());
        templateValues.put(TemplateMetadata.ATTR_MAKE_IGNORE, createGitIgnore().get());
        templateValues.put(TemplateMetadata.ATTR_IS_NEW_PROJECT, true);
        templateValues.put(TemplateMetadata.ATTR_IS_LIBRARY_MODULE, true);

        if (doDryRun(moduleRoot, templateValues)) {
            render(moduleRoot, templateValues);
        }
    }

    private boolean doDryRun(@NotNull File moduleRoot, @NotNull Map<String, Object> templateValues) {
        return renderTemplate(true, mProject, moduleRoot, templateValues, null);
    }

    private void render(@NotNull File moduleRoot, @NotNull Map<String, Object> templateValues) {
        List<File> filesToOpen = new ArrayList<>();
        boolean success = renderTemplate(false, mProject, moduleRoot, templateValues, filesToOpen);
        if (!success)
            return;
        // calling smartInvokeLater will make sure that files are open only when the project is ready
        DumbService.getInstance(mProject).smartInvokeLater(new Runnable() {
            @Override
            public void run() {
                TemplateUtils.openEditors(mProject, filesToOpen, true);
            }
        });
    }

    private boolean renderTemplate(boolean dryRun,
                                   @NotNull Project project,
                                   @NotNull File moduleRoot,
                                   @NotNull Map<String, Object> templateValues,
                                   @Nullable List<File> filesToOpen) {
        Template template = mTemplateHandle.getTemplate();

        // @formatter:off
        final RenderingContext context = RenderingContext.Builder.newContext(template, project)
                .withCommandName("Adds a new module to the project")
                .withDryRun(dryRun)
                .withShowErrors(true)
                .withModuleRoot(moduleRoot)
                .withParams(templateValues)
                .intoOpenFiles(filesToOpen)
                .build();
        // @formatter:on

        return template.render(context, dryRun);
    }

}
