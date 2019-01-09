package com.zxy.idea.plugin.gradle.plugin.support.action;

import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.actions.CreateTemplateInPackageAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import icons.JetgroovyIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.GroovyBundle;
import org.jetbrains.plugins.groovy.actions.GroovyTemplatesFactory;
import org.jetbrains.plugins.groovy.config.GroovyConfigUtils;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.GrTypeDefinition;
import org.jetbrains.plugins.groovy.projectRoots.RootTypesKt;

/**
 * Created by zhengxiaoyong on 2019/01/02.
 */
public class NewGroovyClassAction extends CreateTemplateInPackageAction<GrTypeDefinition> implements DumbAware {

    public NewGroovyClassAction() {
        super("Groovy Class", "Create a new Groovy class", JetgroovyIcons.Groovy.Class, RootTypesKt.ROOT_TYPES);
    }

    @Override
    protected void buildDialog(final Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle("New Groovy Class")
                .addKind("Class", JetgroovyIcons.Groovy.Class, "Groovy Class.groovy")
                .addKind("Interface", JetgroovyIcons.Groovy.Interface, "Groovy Interface.groovy");
        if (GroovyConfigUtils.getInstance().isVersionAtLeast(directory, "2.3", true)) {
            builder.addKind("Trait", JetgroovyIcons.Groovy.Trait, "Groovy Trait.groovy");
        }

        builder.addKind("Enum", JetgroovyIcons.Groovy.Enum, "Groovy Enum.groovy")
                .addKind("Annotation", JetgroovyIcons.Groovy.AnnotationType, "Groovy Annotation.groovy");

//        FileTemplate[] fileTemplates = FileTemplateManager.getInstance(project).getAllTemplates();

        builder.setValidator(new InputValidatorEx() {
            public String getErrorText(String inputString) {
                return "This is not a valid Groovy qualified name";
            }

            public boolean checkInput(String inputString) {
                return true;
            }

            public boolean canClose(String inputString) {
                return !StringUtil.isEmptyOrSpaces(inputString) && PsiNameHelper.getInstance(project).isQualifiedName(inputString);
            }
        });
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
        return "Groovy Class";
    }

    @Override
    protected PsiElement getNavigationElement(@NotNull GrTypeDefinition createdElement) {
        return createdElement.getLBrace();
    }

    @Override
    protected final GrTypeDefinition doCreate(PsiDirectory dir, String className, String templateName) throws IncorrectOperationException {
        String fileName = className + ".groovy";
        PsiFile fromTemplate = GroovyTemplatesFactory.createFromTemplate(dir, className, fileName, templateName, true);
        if (fromTemplate instanceof GroovyFile) {
            CodeStyleManager.getInstance(fromTemplate.getManager()).reformat(fromTemplate);
            return ((GroovyFile) fromTemplate).getTypeDefinitions()[0];
        } else {
            String description = fromTemplate.getFileType().getDescription();
            throw new IncorrectOperationException(GroovyBundle.message("groovy.file.extension.is.not.mapped.to.groovy.file.type", description));
        }
    }

    @Override
    protected boolean checkPackageExists(PsiDirectory directory) {
        PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(directory);
        if (psiPackage == null) {
            return false;
        }

        String name = psiPackage.getQualifiedName();
        return StringUtil.isEmpty(name) || PsiNameHelper.getInstance(directory.getProject()).isQualifiedName(name);
    }

}
