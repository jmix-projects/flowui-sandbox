package com.company.demo.action.filter;

import com.company.demo.accesscontext.FlowuiFilterModifyConfigurationContext;
import com.company.demo.app.filter.conditiion.AddConditionView;
import com.company.demo.component.filter.Configuration;
import com.company.demo.component.filter.LogicalFilterComponent;
import com.company.demo.component.filter.builder.FilterConditionsBuilder;
import com.company.demo.component.filter.configuration.DesignTimeConfiguration;
import com.company.demo.component.filter.converter.FilterConverter;
import com.company.demo.component.filter.registration.FilterComponents;
import com.company.demo.entity.filter.FilterCondition;
import com.company.demo.entity.filter.HeaderFilterCondition;
import com.vaadin.flow.component.icon.VaadinIcon;
import io.jmix.core.AccessManager;
import io.jmix.core.Messages;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.action.ActionType;
import io.jmix.flowui.action.AdjustWhenViewReadOnly;
import io.jmix.flowui.component.UiComponentUtils;
import io.jmix.flowui.component.filer.FilterComponent;
import io.jmix.flowui.component.filer.SingleFilterComponentBase;
import io.jmix.flowui.kit.component.FlowuiComponentUtils;
import io.jmix.flowui.view.DialogWindow;
import io.jmix.flowui.view.LookupView.ValidationContext;
import io.jmix.flowui.view.View;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("JmixInternalElementUsage")
@ActionType(FilterAddConditionAction.ID)
public class FilterAddConditionAction extends FilterAction<FilterAddConditionAction> implements AdjustWhenViewReadOnly {

    public static final String ID = "filter_addCondition";

    protected Messages messages;
    protected DialogWindows dialogWindows;
    protected Notifications notifications;
    protected FilterConditionsBuilder builder;
    protected FilterComponents filterComponents;

    protected Predicate<ValidationContext<FilterCondition>> selectValidator;
    protected Consumer<Collection<FilterCondition>> selectHandler;

    public FilterAddConditionAction() {
        super(ID);
    }

    public FilterAddConditionAction(String id) {
        super(id);
    }

    @Override
    protected void initAction() {
        super.initAction();

        this.icon = FlowuiComponentUtils.convertToIcon(VaadinIcon.PLUS);

        initDefaultSelectValidator();
        initDefaultSelectHandler();
    }

    @Autowired
    protected void setMessages(Messages messages) {
        this.messages = messages;
        this.text = messages.getMessage("actions.Filter.AddCondition");
    }

    @Autowired
    public void setDialogWindows(DialogWindows dialogWindows) {
        this.dialogWindows = dialogWindows;
    }

    @Autowired
    public void setNotifications(Notifications notifications) {
        this.notifications = notifications;
    }

    @Autowired
    public void setBuilder(FilterConditionsBuilder builder) {
        this.builder = builder;
    }

    @Autowired
    public void setFilterComponents(FilterComponents filterComponents) {
        this.filterComponents = filterComponents;
    }

    @Autowired
    protected void setAccessManager(AccessManager accessManager) {
        FlowuiFilterModifyConfigurationContext context = new FlowuiFilterModifyConfigurationContext();
        accessManager.applyRegisteredConstraints(context);
        visibleBySpecificUiPermission = context.isPermitted();
    }

    public void setSelectValidator(@Nullable Predicate<ValidationContext<FilterCondition>> selectValidator) {
        this.selectValidator = selectValidator;
    }

    public void setSelectHandler(@Nullable Consumer<Collection<FilterCondition>> selectHandler) {
        this.selectHandler = selectHandler;
    }

    @Override
    protected boolean isApplicable() {
        return super.isApplicable()
                && !(target.getCurrentConfiguration() instanceof DesignTimeConfiguration);
    }

    @Override
    public void execute() {
        checkTarget();
        List<FilterCondition> allConditions = builder.buildConditions(target);
        openAddConditionScreen(allConditions);
    }

    public FilterAddConditionAction withSelectValidator(
            @Nullable Predicate<ValidationContext<FilterCondition>> validator) {
        setSelectValidator(validator);
        return this;
    }

    public FilterAddConditionAction withSelectHandler(@Nullable Consumer<Collection<FilterCondition>> handler) {
        setSelectHandler(handler);
        return this;
    }

    protected void initDefaultSelectValidator() {
        selectValidator = context -> {
            for (FilterCondition selectedCondition : context.getSelectedItems()) {
                if (selectedCondition instanceof HeaderFilterCondition) {
                    String text = messages.formatMessage("",
                            "actions.Filter.AddCondition.invalidCondition",
                            selectedCondition.getLocalizedLabel());

                    notifications.create(text)
                            .withType(Notifications.Type.WARNING)
                            .show();
                    return false;
                }
            }

            return true;
        };
    }

    protected void initDefaultSelectHandler() {
        selectHandler = selectedConditions -> {
            if (!selectedConditions.isEmpty()) {
                Configuration currentConfiguration = target.getCurrentConfiguration();

                boolean dataLoadNeeded = false;
                for (FilterCondition selectedCondition : selectedConditions) {
                    FilterConverter converter = filterComponents.getConverterByModelClass(
                            selectedCondition.getClass(), target);

                    FilterCondition parent = selectedCondition.getParent();
                    if (parent instanceof HeaderFilterCondition) {
                        selectedCondition.setParent(null);
                    }

                    FilterComponent filterComponent = converter.convertToComponent(selectedCondition);
                    currentConfiguration.getRootLogicalFilterComponent().add(filterComponent);
                    currentConfiguration.setFilterComponentModified(filterComponent, true);

                    boolean nonNullDefaultValue = setFilterComponentDefaultValue(filterComponent, currentConfiguration);
                    if (nonNullDefaultValue) {
                        dataLoadNeeded = true;
                    }
                }

                target.setCurrentConfiguration(currentConfiguration);

                if (dataLoadNeeded) {
                    target.apply();
                }
            }
        };
    }

    protected boolean setFilterComponentDefaultValue(FilterComponent filterComponent,
                                                     Configuration currentConfiguration) {
        boolean dataLoadNeeded = false;
        if (filterComponent instanceof LogicalFilterComponent) {
            for (FilterComponent child : ((LogicalFilterComponent<?>) filterComponent).getOwnFilterComponents()) {
                boolean nonNullDefaultValue = setFilterComponentDefaultValue(child, currentConfiguration);
                if (nonNullDefaultValue) {
                    dataLoadNeeded = true;
                }
            }
        } else if (filterComponent instanceof SingleFilterComponentBase) {
            currentConfiguration.setFilterComponentDefaultValue(
                    ((SingleFilterComponentBase<?>) filterComponent).getParameterName(),
                    ((SingleFilterComponentBase<?>) filterComponent).getValue());

            if (((SingleFilterComponentBase<?>) filterComponent).getValue() != null) {
                dataLoadNeeded = true;
            }
        }

        return dataLoadNeeded;
    }

    protected void openAddConditionScreen(List<FilterCondition> filterConditions) {
        View<?> origin = UiComponentUtils.findView(target);
        if (origin == null) {
            throw new IllegalStateException(String.format("A component '%s' is not attached to a view",
                    target.getClass().getSimpleName()));
        }

        DialogWindow<AddConditionView> dialog = dialogWindows.lookup(origin, FilterCondition.class)
                .withViewClass(AddConditionView.class)
                .withSelectValidator(selectValidator)
                .withSelectHandler(selectHandler)
                .build();

        AddConditionView addConditionView = dialog.getView();
        addConditionView.setConditions(filterConditions);
        addConditionView.setCurrentFilterConfiguration(target.getCurrentConfiguration());

        dialog.open();
    }
}
