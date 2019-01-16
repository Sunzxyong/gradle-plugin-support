package com.zxy.idea.plugin.gradle.plugin.support.action;

import com.android.tools.idea.gradle.project.GradleProjectInfo;
import com.android.tools.idea.npw.template.TemplateHandle;
import com.android.tools.idea.projectsystem.ProjectSystemUtil;
import com.android.tools.idea.sdk.wizard.SdkQuickfixUtils;
import com.android.tools.idea.ui.wizard.StudioWizardDialogBuilder;
import com.android.tools.idea.wizard.model.ModelWizard;
import com.android.utils.Pair;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.util.ReflectionUtil;
import com.zxy.idea.plugin.gradle.plugin.support.ModuleTemplateHelper;
import com.zxy.idea.plugin.gradle.plugin.support.ConfigureGradlePluginModuleStep;
import com.zxy.idea.plugin.gradle.plugin.support.GradlePluginModuleModel;
import com.zxy.idea.plugin.gradle.plugin.support.ModuleType;
import org.jetbrains.android.sdk.AndroidSdkUtils;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by zhengxiaoyong on 2019/01/04.
 */
public class BuildSrcNewModuleAction extends AnAction implements DumbAware {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        boolean isAvailable = project != null && ProjectSystemUtil.getProjectSystem(project).allowsFileCreation() && GradleProjectInfo.getInstance(project).isBuildWithGradle();
        if (!isAvailable)
            return;

        if (!AndroidSdkUtils.isAndroidSdkAvailable()) {
            SdkQuickfixUtils.showSdkMissingDialog();
            return;
        }

        Pair<File, File> pair = ModuleTemplateHelper.getBuildSrcTemplate();
        if (pair == null)
            return;

        File template = pair.getFirst();

        ModelWizard wizard = new ModelWizard.Builder()
                .addStep(new ConfigureGradlePluginModuleStep(new GradlePluginModuleModel(project, new TemplateHandle(template), pair.getSecond()), "BuildSrc", ModuleType.BUILD_SRC))
                .build();

        StudioWizardDialogBuilder builder = new StudioWizardDialogBuilder(wizard, "Create New Module");
        try {
            Method method = ReflectionUtil.getMethod(builder.getClass(), "setUseNewUx", Boolean.class);
            method.invoke(builder, Boolean.TRUE);
        } catch (Exception e) {
            builder.setUxStyle(StudioWizardDialogBuilder.UxStyle.INSTANT_APP);
        }

        builder.build().show();
    }

    @Override
    public void update(AnActionEvent event) {
        Project project = event.getProject();
        boolean isAvailable = project != null && ProjectSystemUtil.getProjectSystem(project).allowsFileCreation() && GradleProjectInfo.getInstance(project).isBuildWithGradle();
        event.getPresentation().setVisible(isAvailable);
        event.getPresentation().setEnabled(isAvailable && getEventProject(event) != null);
    }
}
