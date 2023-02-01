package com.company.demo.component.groupfilter;

import com.company.demo.component.filter.LogicalFilterComponent;
import com.google.common.base.Strings;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import io.jmix.core.annotation.Internal;
import io.jmix.core.querycondition.Condition;
import io.jmix.core.querycondition.LogicalCondition;
import io.jmix.flowui.FlowuiComponentProperties;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.component.SupportsLabelPosition;
import io.jmix.flowui.component.filer.FilterComponent;
import io.jmix.flowui.component.filer.SingleFilterComponent;
import io.jmix.flowui.component.propertyfilter.PropertyFilter;
import io.jmix.flowui.kit.component.FlowuiComponentUtils;
import io.jmix.flowui.model.DataLoader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class GroupFilter extends Composite<VerticalLayout>
        implements LogicalFilterComponent, SupportsLabelPosition,
        ApplicationContextAware, InitializingBean {

    protected static final String GROUP_FILTER_CLASS_NAME = "jmix-group-filter";

    protected ApplicationContext applicationContext;
    protected UiComponents uiComponents;
    protected LogicalFilterSupport logicalFilterSupport;

    protected DataLoader dataLoader;
    protected Condition initialDataLoaderCondition;
    protected boolean autoApply;

    @Internal
    protected boolean conditionModificationDelegated = false;

    //    protected int columnsCount;
    protected LabelPosition labelPosition = LabelPosition.ASIDE;
    protected Label summaryComponent;
    protected String summaryText;
    protected boolean operationTextVisible = true;

    protected Operation operation = Operation.AND;
    protected LogicalCondition queryCondition = LogicalCondition.and();

    protected List<FilterComponent> ownFilterComponentsOrder = new ArrayList<>();

    protected FormLayout conditionsLayout;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        autowireDependencies();
        initComponent();
    }

    protected void autowireDependencies() {
        uiComponents = applicationContext.getBean(UiComponents.class);
        logicalFilterSupport = applicationContext.getBean(LogicalFilterSupport.class);
    }

    @Override
    protected VerticalLayout initContent() {
        VerticalLayout root = super.initContent();

        root.setClassName(GROUP_FILTER_CLASS_NAME);
        root.setWidthFull();
        root.setPadding(false);

        return root;
    }

    protected void initComponent() {
        this.autoApply = applicationContext.getBean(FlowuiComponentProperties.class).isFilterAutoApply();

        initLayout();
    }

    protected void initLayout() {
        // TODO: gg, summary
        summaryComponent = createSummaryComponent();
        initSummaryComponent(summaryComponent);
    }

    protected Label createSummaryComponent() {
        return uiComponents.create(Label.class);
    }

    protected void initSummaryComponent(Label summaryComponent) {
        // TODO: gg, implement
    }

    protected FormLayout createConditionsLayout() {
        return uiComponents.create(FormLayout.class);
    }

    protected void initConditionsLayout(FormLayout layout) {
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("50em", 3)
        );

        // TODO: gg, something else?
    }

    protected void updateConditionsLayout() {
        if (conditionsLayout != null) {
            conditionsLayout.removeAll();
            getContent().remove(conditionsLayout);
            conditionsLayout = null;
        }

        boolean isAnyFilterComponentVisible = getOwnFilterComponents().stream()
                .anyMatch(FlowuiComponentUtils::isVisible);

        if (isAnyFilterComponentVisible) {
            conditionsLayout = createConditionsLayout();
            initConditionsLayout(conditionsLayout);
            getContent().add(conditionsLayout);

            getOwnFilterComponents().stream()
                    .filter(FlowuiComponentUtils::isVisible)
                    .forEach(ownFilterComponent -> {
                        // TODO: gg, implement
                        /*if (ownFilterComponent instanceof LogicalFilterComponent) {
                            addLogicalFilterComponentToConditionsLayoutRow(
                                    (LogicalFilterComponent) ownFilterComponent, row);
                        } else {*/
                            addFilterComponentToConditionsLayout(conditionsLayout, ownFilterComponent);
//                        }
                    });
        }
    }

    protected void addFilterComponentToConditionsLayout(FormLayout conditionsLayout,
                                                        FilterComponent filterComponent) {
        // TODO: gg, add considering label position
        if (filterComponent instanceof SupportsLabelPosition) {
            ((SupportsLabelPosition) filterComponent).setLabelPosition(getLabelPosition());
        }
        conditionsLayout.add(((Component) filterComponent));
    }


    @Override
    public DataLoader getDataLoader() {
        return dataLoader;
    }

    @Override
    public void setDataLoader(DataLoader dataLoader) {
        checkState(this.dataLoader == null, "DataLoader has already been initialized");
        checkNotNull(dataLoader);

        this.dataLoader = dataLoader;
        this.initialDataLoaderCondition = dataLoader.getCondition();

        if (!isConditionModificationDelegated()) {
            updateDataLoaderCondition();
        }

        updateSummaryText();
    }

    protected void updateDataLoaderCondition() {
        if (dataLoader == null) {
            return;
        }

        LogicalCondition resultCondition;
        if (initialDataLoaderCondition instanceof LogicalCondition) {
            resultCondition = (LogicalCondition) initialDataLoaderCondition.copy();
            resultCondition.add(getQueryCondition());
        } else if (initialDataLoaderCondition != null) {
            resultCondition = LogicalCondition.and()
                    .add(initialDataLoaderCondition)
                    .add(getQueryCondition());
        } else {
            resultCondition = getQueryCondition();
        }

        dataLoader.setCondition(resultCondition);
    }

    @Override
    public boolean isAutoApply() {
        return autoApply;
    }

    @Override
    public void setAutoApply(boolean autoApply) {
        if (this.autoApply != autoApply) {
            this.autoApply = autoApply;

            getOwnFilterComponents().forEach(filterComponent ->
                    filterComponent.setAutoApply(autoApply));
        }
    }

    @Override
    public void apply() {
        if (dataLoader != null && autoApply) {
            dataLoader.load();
        }
    }

    @Override
    public LogicalCondition getQueryCondition() {
        updateQueryCondition();
        return queryCondition;
    }

    protected void updateQueryCondition() {
        queryCondition = new LogicalCondition(toLogicalConditionType(operation));

        for (FilterComponent ownComponent : ownFilterComponentsOrder) {
            queryCondition.add(ownComponent.getQueryCondition());
        }
    }

    // TODO: gg, create helper
    public static LogicalCondition.Type toLogicalConditionType(LogicalFilterComponent.Operation operation) {
        switch (operation) {
            case AND:
                return LogicalCondition.Type.AND;
            case OR:
                return LogicalCondition.Type.OR;
            default:
                throw new IllegalArgumentException("Unknown operation " + operation);
        }
    }

    @Override
    public void add(FilterComponent filterComponent) {
        if (dataLoader != filterComponent.getDataLoader()) {
            throw new IllegalArgumentException("The data loader of child component must be the same as the owner " +
                    "GroupFilter component");
        }

        filterComponent.setConditionModificationDelegated(true);
        filterComponent.setAutoApply(isAutoApply());
        getQueryCondition().add(filterComponent.getQueryCondition());
        ownFilterComponentsOrder.add(filterComponent);
        updateConditionsLayout();

        if (filterComponent instanceof PropertyFilter) {
            ((PropertyFilter<?>) filterComponent).addOperationChangeListener(operationChangeEvent -> apply());
        }

        if (!isConditionModificationDelegated()) {
            updateDataLoaderCondition();
        }

        /*if (frame != null) {
            if (filterComponent instanceof BelongToFrame
                    && ((BelongToFrame) filterComponent).getFrame() == null) {
                ((BelongToFrame) filterComponent).setFrame(frame);
            } else {
                attachToFrame(filterComponent);
            }
        }

        filterComponent.setParent(this);*/
    }

    @Override
    public void remove(FilterComponent filterComponent) {
        if (ownFilterComponentsOrder.contains(filterComponent)) {
            ownFilterComponentsOrder.remove(filterComponent);

            if (filterComponent instanceof SingleFilterComponent) {
                getDataLoader().removeParameter(((SingleFilterComponent<?>) filterComponent).getParameterName());
            }

            updateConditionsLayout();

            if (!isConditionModificationDelegated()) {
                updateDataLoaderCondition();
            }
        } else {
            ownFilterComponentsOrder.stream()
                    .filter(ownComponent -> ownComponent instanceof LogicalFilterComponent)
                    .map(ownComponent -> (LogicalFilterComponent) ownComponent)
                    .forEach(childLogicalFilterComponent -> childLogicalFilterComponent.remove(filterComponent));
        }

//        filterComponent.setParent(null);
    }

    @Override
    public void removeAll() {
        // TODO: gg, replace
//        getComposition().removeAll();
        ownFilterComponentsOrder = new ArrayList<>();

        updateConditionsLayout();

        if (!isConditionModificationDelegated()) {
            updateDataLoaderCondition();
        }
    }

    @Nullable
    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(@Nullable String summaryText) {
        if (!Objects.equals(this.summaryText, summaryText)) {
            this.summaryText = summaryText;
            updateSummaryText();
        }
    }

    protected void updateSummaryText() {
        String summaryText = Strings.isNullOrEmpty(this.summaryText)
                ? logicalFilterSupport.getOperationText(operation, operationTextVisible)
                : this.summaryText;
        summaryComponent.setText(summaryText);
    }

    @Override
    public Operation getOperation() {
        return operation;
    }

    @Override
    public void setOperation(Operation operation) {
        if (this.operation != operation) {
            this.operation = operation;

            updateSummaryText();

            if (!isConditionModificationDelegated()) {
                updateDataLoaderCondition();
            }
        }
    }

    @Override
    public boolean isOperationTextVisible() {
        return operationTextVisible;
    }

    @Override
    public void setOperationTextVisible(boolean operationTextVisible) {
        if (this.operationTextVisible != operationTextVisible) {
            this.operationTextVisible = operationTextVisible;

            updateSummaryText();
        }
    }

    @Override
    public LabelPosition getLabelPosition() {
        return labelPosition;
    }

    @Override
    public void setLabelPosition(LabelPosition labelPosition) {
        if (this.labelPosition != labelPosition) {
            this.labelPosition = labelPosition;

            updateConditionsLayout();
        }
    }

    @Override
    public List<FilterComponent> getOwnFilterComponents() {
        return ownFilterComponentsOrder;
    }

    @Override
    public List<FilterComponent> getFilterComponents() {
        List<FilterComponent> components = new ArrayList<>();
        for (FilterComponent ownComponent : ownFilterComponentsOrder) {
            components.add(ownComponent);
            if (ownComponent instanceof LogicalFilterComponent) {
                components.addAll(((LogicalFilterComponent) ownComponent).getFilterComponents());
            }
        }

        return components;
    }


    @Internal
    @Override
    public boolean isConditionModificationDelegated() {
        return conditionModificationDelegated;
    }

    @Internal
    @Override
    public void setConditionModificationDelegated(boolean conditionModificationDelegated) {
        this.conditionModificationDelegated = conditionModificationDelegated;
    }
}
