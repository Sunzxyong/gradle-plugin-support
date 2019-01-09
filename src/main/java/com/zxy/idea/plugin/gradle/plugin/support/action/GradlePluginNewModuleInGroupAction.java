package com.zxy.idea.plugin.gradle.plugin.support.action;

import com.intellij.ide.projectView.impl.ModuleGroup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;

/**
 * Created by zhengxiaoyong on 2019/01/04.
 */
public class GradlePluginNewModuleInGroupAction extends GradlePluginNewModuleAction {

    @Override
    public void update(AnActionEvent event) {
        super.update(event);
        if (!event.getPresentation().isVisible()) {
            return; // Nothing to do, if above call to parent update() has disable the action
        }

        ModuleGroup[] moduleGroups = event.getData(ModuleGroup.ARRAY_DATA_KEY);
        Module[] modules = event.getData(LangDataKeys.MODULE_CONTEXT_ARRAY);
        event.getPresentation().setVisible(isNotEmpty(moduleGroups) || isNotEmpty(modules));
    }

    private static boolean isNotEmpty(Object[] array) {
        return array != null && array.length > 0;
    }
}
