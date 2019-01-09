package com.zxy.idea.plugin.gradle.plugin.support;

import com.android.tools.adtui.LabelWithEditButton;
import com.android.tools.adtui.util.FormScalingUtil;
import com.android.tools.adtui.validation.Validator;
import com.android.tools.adtui.validation.ValidatorPanel;
import com.android.tools.idea.npw.model.NewProjectModel;
import com.android.tools.idea.npw.project.DomainToPackageExpression;
import com.android.tools.idea.npw.validator.ClassNameValidator;
import com.android.tools.idea.npw.validator.ModuleValidator;
import com.android.tools.idea.observable.BindingsManager;
import com.android.tools.idea.observable.ListenerManager;
import com.android.tools.idea.observable.core.BoolProperty;
import com.android.tools.idea.observable.core.BoolValueProperty;
import com.android.tools.idea.observable.core.ObservableBool;
import com.android.tools.idea.observable.core.StringValueProperty;
import com.android.tools.idea.observable.expressions.Expression;
import com.android.tools.idea.observable.ui.SelectedProperty;
import com.android.tools.idea.observable.ui.TextProperty;
import com.android.tools.idea.ui.wizard.StudioWizardStepPanel;
import com.android.tools.idea.ui.wizard.WizardUtils;
import com.android.tools.idea.wizard.model.ModelWizardStep;
import com.android.tools.idea.wizard.model.SkippableWizardStep;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;

/**
 * Created by zhengxiaoyong on 2019/01/03.
 */
public class ConfigureGradlePluginModuleStep extends SkippableWizardStep<GradlePluginModuleModel> {

    private final StudioWizardStepPanel mRootPanel;
    private final ValidatorPanel mValidatorPanel;
    private final BindingsManager mBindings = new BindingsManager();
    private final ListenerManager mListeners = new ListenerManager();

    private JPanel mPanel;
    private JTextField mPluginName;
    private LabelWithEditButton mPackageName;
    private JTextField mClassName;
    private JCheckBox mCreateIgnoreFile;

    public ConfigureGradlePluginModuleStep(@NotNull GradlePluginModuleModel model, @NotNull String title, ModuleType moduleType) {
        super(model, title);

        mValidatorPanel = new ValidatorPanel(this, mPanel);

        ModuleValidator moduleValidator = new ModuleValidator(model.getProject());
        if (moduleType == ModuleType.BUILD_SRC) {
            mPluginName.setEditable(false);
            mPluginName.setEnabled(false);
            mPluginName.setText("buildSrc");
        } else {
            mPluginName.setEditable(true);
            mPluginName.setEnabled(true);
            mPluginName.setText(WizardUtils.getUniqueName(model.pluginNameName().get(), moduleValidator));
        }

        TextProperty pluginNameText = new TextProperty(mPluginName);
        mBindings.bind(model.pluginNameName(), pluginNameText, mValidatorPanel.hasErrors().not());
        mValidatorPanel.registerValidator(pluginNameText, moduleValidator);

        Expression<String> computedPackageName =
                new DomainToPackageExpression(new StringValueProperty(NewProjectModel.getInitialDomain(false)), model.pluginNameName());
        BoolProperty isPackageNameSynced = new BoolValueProperty(true);

        TextProperty packageNameText = new TextProperty(mPackageName);
        mBindings.bind(packageNameText, computedPackageName, isPackageNameSynced);
        mBindings.bind(model.packageName(), packageNameText);
        mListeners.receive(packageNameText, value -> isPackageNameSynced.set(value.equals(computedPackageName.get())));

        mBindings.bindTwoWay(new TextProperty(mClassName), model.className());

        mBindings.bindTwoWay(new SelectedProperty(mCreateIgnoreFile), model.createGitIgnore());

        mValidatorPanel.registerValidator(model.packageName(),
                value -> Validator.Result.fromNullableMessage(WizardUtils.validatePackageName(value)));
        mValidatorPanel.registerValidator(model.className(), new ClassNameValidator());

        mRootPanel = new StudioWizardStepPanel(mValidatorPanel);
        FormScalingUtil.scaleComponentTree(this.getClass(), mRootPanel);
    }

    @NotNull
    @Override
    protected Collection<? extends ModelWizardStep> createDependentSteps() {
        return Lists.newArrayList();
    }

    @NotNull
    @Override
    protected ObservableBool canGoForward() {
        return mValidatorPanel.hasErrors().not();
    }

    @NotNull
    @Override
    protected JComponent getComponent() {
        return mRootPanel;
    }

    @NotNull
    @Override
    protected JComponent getPreferredFocusComponent() {
        return mPluginName;
    }

    @Override
    public void dispose() {
        super.dispose();
        mBindings.releaseAll();
        mListeners.releaseAll();
    }

}
