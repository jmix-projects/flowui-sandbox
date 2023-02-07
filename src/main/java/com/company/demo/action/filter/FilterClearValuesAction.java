package com.company.demo.action.filter;

import com.company.demo.component.filter.Filter;
import com.company.demo.component.filter.LogicalFilterComponent;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.shared.Registration;
import io.jmix.core.Messages;
import io.jmix.flowui.action.ActionType;
import io.jmix.flowui.component.filer.FilterComponent;
import io.jmix.flowui.kit.component.FlowuiComponentUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import java.util.List;

@ActionType(FilterClearValuesAction.ID)
public class FilterClearValuesAction extends FilterAction<FilterClearValuesAction> {

    public static final String ID = "filter_clearValues";

    protected Registration configurationChangeRegistration;
    protected Registration configurationUpdateRegistration;

    public FilterClearValuesAction() {
        this(ID);
    }

    public FilterClearValuesAction(String id) {
        super(id);
    }

    @Override
    protected void initAction() {
        super.initAction();

        this.icon = FlowuiComponentUtils.convertToIcon(VaadinIcon.ERASER);
    }

    @Autowired
    protected void setMessages(Messages messages) {
        this.text = messages.getMessage("actions.Filter.ClearValues");
    }

    @Override
    protected void setTargetInternal(@Nullable Filter target) {
        super.setTargetInternal(target);

        if (target == null) {
            return;
        }

        if (configurationChangeRegistration != null) {
            configurationChangeRegistration.remove();
            configurationChangeRegistration = null;
        }

        if (configurationUpdateRegistration != null) {
            configurationUpdateRegistration.remove();
            configurationUpdateRegistration = null;
        }

        configurationChangeRegistration = target.addConfigurationChangeListener(this::onConfigurationChanged);

        LogicalFilterComponent<?> rootLogicalFilterComponent = target.getCurrentConfiguration()
                .getRootLogicalFilterComponent();
        configurationUpdateRegistration = rootLogicalFilterComponent
                .addFilterComponentsChangeListener(this::onFilterComponentsChanged);
    }

    protected void onConfigurationChanged(Filter.ConfigurationChangeEvent event) {
        refreshState();
    }

    protected void onFilterComponentsChanged(LogicalFilterComponent.FilterComponentsChangeEvent<?> event) {
        refreshState();
    }

    @Override
    protected boolean isApplicable() {
        return super.isApplicable() &&
                !target.getCurrentConfiguration().getRootLogicalFilterComponent()
                        .getFilterComponents().isEmpty();
    }

    @Override
    public void execute() {
        checkTarget();

        List<FilterComponent> ownFilterComponents = target.getCurrentConfiguration()
                .getRootLogicalFilterComponent()
                .getOwnFilterComponents();

        if (ownFilterComponents.isEmpty()) {
            return;
        }

        ownFilterComponents.stream()
                .filter(filterComponent -> filterComponent instanceof HasValue)
                .forEach(filterComponent -> {
                    filterComponent.setAutoApply(false);
                    ((HasValue<?, ?>) filterComponent).clear();
                    filterComponent.setAutoApply(true);
                });

        target.apply();
    }
}
