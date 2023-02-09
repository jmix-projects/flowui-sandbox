package com.company.demo.component.filter;

import com.company.demo.accesscontext.FlowuiFilterModifyConfigurationContext;
import com.company.demo.action.filter.FilterAction;
import com.company.demo.action.filter.FilterAddConditionAction;
import com.company.demo.action.filter.FilterResetAction;
import com.company.demo.component.SupportsResponsiveSteps;
import com.company.demo.component.filter.configuration.DesignTimeConfiguration;
import com.company.demo.component.filter.configuration.RunTimeConfiguration;
import com.company.demo.component.groupfilter.GroupFilter;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import io.jmix.core.AccessManager;
import io.jmix.core.Messages;
import io.jmix.core.Metadata;
import io.jmix.core.metamodel.model.MetaPropertyPath;
import io.jmix.core.querycondition.Condition;
import io.jmix.core.querycondition.LogicalCondition;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.Actions;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.FlowuiComponentProperties;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.component.UiComponentUtils;
import io.jmix.flowui.component.details.JmixDetails;
import io.jmix.flowui.component.filer.FilterComponent;
import io.jmix.flowui.component.filer.SingleFilterComponent;
import io.jmix.flowui.component.filer.SingleFilterComponentBase;
import io.jmix.flowui.component.propertyfilter.PropertyFilter;
import io.jmix.flowui.kit.action.Action;
import io.jmix.flowui.kit.action.BaseAction;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.kit.component.combobutton.ComboButton;
import io.jmix.flowui.kit.component.combobutton.ComboButtonVariant;
import io.jmix.flowui.kit.component.dropdownbutton.DropdownButton;
import io.jmix.flowui.kit.component.dropdownbutton.DropdownButtonVariant;
import io.jmix.flowui.model.BaseCollectionLoader;
import io.jmix.flowui.model.DataLoader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@SuppressWarnings({"JmixInternalElementUsage", "unused"})
public class Filter extends Composite<JmixDetails> implements SupportsResponsiveSteps,
        ApplicationContextAware, InitializingBean {

    protected static final String CONDITION_REMOVE_BUTTON_ID_SUFFIX = "conditionRemoveButton";

    protected static final String FILTER_CLASS_NAME = "jmix-filter";
    protected static final String FILTER_CONTENT_WRAPPER_CLASS_NAME = "jmix-filter-content-wrapper";
    protected static final String FILTER_CONTROLS_LAYOUT_CLASS_NAME = "jmix-filter-controls-layout";

    protected ApplicationContext applicationContext;
    protected CurrentAuthentication currentAuthentication;
    protected UiComponents uiComponents;
    protected Actions actions;
    protected Messages messages;
    protected Metadata metadata;
    protected DialogWindows dialogWindows;
    protected FilterSupport filterSupport;

    protected boolean autoApply;
    protected DataLoader dataLoader;
    protected Condition initialDataLoaderCondition;
    protected Predicate<MetaPropertyPath> propertyFiltersPredicate;

    protected VerticalLayout contentWrapper;
    protected HorizontalLayout controlsLayout;
    protected ComboButton applyButton;
    protected JmixButton addConditionButton;
    protected DropdownButton settingsButton;
    protected List<ResponsiveStep> responsiveSteps;
    protected Registration openedChangeRegistration;

    protected LogicalFilterComponent<?> rootLogicalFilterComponent;
    protected Configuration emptyConfiguration;
    protected Configuration currentConfiguration;
    protected List<Configuration> configurations = new ArrayList<>();

    protected List<FilterComponent> conditions;

    protected boolean configurationModifyPermitted;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        autowireDependencies();
        initComponent();
    }

    protected void autowireDependencies() {
        uiComponents = applicationContext.getBean(UiComponents.class);
        currentAuthentication = applicationContext.getBean(CurrentAuthentication.class);
        actions = applicationContext.getBean(Actions.class);
        messages = applicationContext.getBean(Messages.class);
        metadata = applicationContext.getBean(Metadata.class);
        dialogWindows = applicationContext.getBean(DialogWindows.class);
        filterSupport = applicationContext.getBean(FilterSupport.class);
    }

    protected void initComponent() {
        this.autoApply = applicationContext.getBean(FlowuiComponentProperties.class).isFilterAutoApply();

        initDefaultResponsiveSteps();
        initEmptyConfiguration();
        initLayout();
    }

    protected void initDefaultResponsiveSteps() {
        responsiveSteps = List.of(
                new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("40em", 2),
                new ResponsiveStep("75em", 3)
        );
    }

    protected void initEmptyConfiguration() {
        LogicalFilterComponent<?> configurationLogicalComponent =
                createConfigurationRootLogicalFilterComponent(LogicalFilterComponent.Operation.AND);
        emptyConfiguration =
                new RunTimeConfiguration("empty_configuration", configurationLogicalComponent, this);

        String emptyConfigurationName = StringUtils.isNotEmpty(getSummaryText())
                ? getSummaryText()
                : messages.getMessage("filter.emptyConfiguration.name");
        emptyConfiguration.setName(emptyConfigurationName);

        setCurrentConfigurationInternal(emptyConfiguration, false);
    }

    protected LogicalFilterComponent<?> createConfigurationRootLogicalFilterComponent(
            LogicalFilterComponent.Operation rootOperation) {
        GroupFilter rootGroupFilter = uiComponents.create(GroupFilter.class);
        rootGroupFilter.setConditionModificationDelegated(true);
        rootGroupFilter.setOperation(rootOperation);
        rootGroupFilter.setOperationTextVisible(false);

        if (dataLoader != null) {
            rootGroupFilter.setDataLoader(dataLoader);
            rootGroupFilter.setAutoApply(autoApply);
        }

        return rootGroupFilter;
    }

    @Override
    protected JmixDetails initContent() {
        JmixDetails root = super.initContent();

        root.setClassName(FILTER_CLASS_NAME);
        root.setOpened(true);
        root.setWidthFull();

        return root;
    }

    protected void initLayout() {
        contentWrapper = createContentWrapper();
        initContentWrapper(contentWrapper);
        getContent().setContent(contentWrapper);

        controlsLayout = createControlsLayout();
        initControlsLayout(controlsLayout);
        contentWrapper.add(controlsLayout);
    }

    protected VerticalLayout createContentWrapper() {
        return uiComponents.create(VerticalLayout.class);
    }

    protected void initContentWrapper(VerticalLayout contentWrapper) {
        contentWrapper.setPadding(false);
        contentWrapper.setClassName(FILTER_CONTENT_WRAPPER_CLASS_NAME);
    }

    protected HorizontalLayout createControlsLayout() {
        return uiComponents.create(HorizontalLayout.class);
    }

    protected void initControlsLayout(HorizontalLayout controlsLayout) {
        controlsLayout.setWidthFull();
        controlsLayout.setClassName(FILTER_CONTROLS_LAYOUT_CLASS_NAME);

        applyButton = createApplyButton();
        initApplyButton(applyButton);
        controlsLayout.add(applyButton);

        addConditionButton = createAddConditionButton();
        initAddConditionButton(addConditionButton);
        controlsLayout.add(addConditionButton);

        settingsButton = createSettingsButton();
        initSettingsButton(settingsButton);
        controlsLayout.add(settingsButton);
    }

    protected JmixButton createAddConditionButton() {
        return uiComponents.create(JmixButton.class);
    }

    protected void initAddConditionButton(JmixButton addConditionButton) {
        addConditionButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        FilterAddConditionAction addConditionAction = actions.create(FilterAddConditionAction.class);
        addConditionAction.setTarget(this);
        addConditionAction.setText(messages.getMessage("filter.addConditionButton.text"));
        addConditionAction.setIcon(null);
        addConditionButton.setAction(addConditionAction, false);
    }

    protected ComboButton createApplyButton() {
        return uiComponents.create(ComboButton.class);
    }

    protected void initApplyButton(ComboButton applyButton) {
        applyButton.addClickListener(this::onApplyButtonClick);
        applyButton.addThemeVariants(ComboButtonVariant.LUMO_SUCCESS, ComboButtonVariant.LUMO_PRIMARY);
        updateApplyButtonText(isAutoApply());

        initSelectConfigurationDropdown();
    }

    protected void initSelectConfigurationDropdown() {
        Action resetFilterAction = createResetFilterAction();
        applyButton.addItem(resetFilterAction.getId(), resetFilterAction);
    }

    protected void updateApplyButtonText(boolean autoApply) {
        String text = autoApply
                ? messages.getMessage("filter.applyButton.autoApply")
                : messages.getMessage("filter.applyButton");
        applyButton.setText(text);
    }

    protected void onApplyButtonClick(ClickEvent<MenuItem> clickEvent) {
        getDataLoader().load();
    }

    protected Action createResetFilterAction() {
        FilterResetAction filterResetAction = actions.create(FilterResetAction.class);
        filterResetAction.setTarget(this);
        return filterResetAction;
    }

    protected DropdownButton createSettingsButton() {
        return uiComponents.create(DropdownButton.class);
    }

    protected void initSettingsButton(DropdownButton settingsButton) {
        settingsButton.addThemeVariants(DropdownButtonVariant.LUMO_ICON);
        settingsButton.setDropdownIndicatorVisible(false);
        settingsButton.setIcon(VaadinIcon.COG.create());
        settingsButton.getElement().getStyle().set("margin-inline-start", "auto");

        List<FilterAction<?>> defaultFilterActions = filterSupport.getDefaultFilterActions(this);
        for (FilterAction<?> filterAction : defaultFilterActions) {
            settingsButton.addItem(filterAction.getId(), filterAction);
        }

        FlowuiFilterModifyConfigurationContext context = new FlowuiFilterModifyConfigurationContext();
        applicationContext.getBean(AccessManager.class).applyRegisteredConstraints(context);
        configurationModifyPermitted = context.isPermitted();
        settingsButton.setVisible(configurationModifyPermitted);
    }

    public HasComponents getControlsLayout() {
        return controlsLayout;
    }

    public Condition getQueryCondition() {
        return getCurrentConfiguration().getQueryCondition();
    }

    public DataLoader getDataLoader() {
        return dataLoader;
    }

    public void setDataLoader(DataLoader dataLoader) {
        checkState(this.dataLoader == null, "DataLoader has already been initialized");
        checkNotNull(dataLoader);

        this.dataLoader = dataLoader;
        this.initialDataLoaderCondition = dataLoader.getCondition();

        LogicalFilterComponent<?> rootLogicalFilterComponent = emptyConfiguration.getRootLogicalFilterComponent();
        rootLogicalFilterComponent.setDataLoader(dataLoader);
        rootLogicalFilterComponent.setAutoApply(autoApply);
    }

    public boolean isAutoApply() {
        return autoApply;
    }

    public void setAutoApply(boolean autoApply) {
        if (this.autoApply != autoApply) {
            this.autoApply = autoApply;

            updateApplyButtonText(autoApply);
            updateCurrentConfigurationAutoApply(autoApply);
        }
    }

    protected void updateCurrentConfigurationAutoApply(boolean autoApply) {
        getCurrentConfiguration()
                .getRootLogicalFilterComponent()
                .getOwnFilterComponents()
                .forEach(filterComponent -> filterComponent.setAutoApply(autoApply));
    }

    public void apply() {
        if (dataLoader != null) {
            setupLoaderFirstResult();
            if (isAutoApply()) dataLoader.load();
        }
    }

    protected void setupLoaderFirstResult() {
        if (dataLoader instanceof BaseCollectionLoader) {
            ((BaseCollectionLoader) dataLoader).setFirstResult(0);
        }
    }

    @Override
    public List<ResponsiveStep> getResponsiveSteps() {
        return Collections.unmodifiableList(responsiveSteps);
    }

    @Override
    public void setResponsiveSteps(List<ResponsiveStep> steps) {
        this.responsiveSteps = steps;

        LogicalFilterComponent<?> rootComponent = getCurrentConfiguration().getRootLogicalFilterComponent();
        if (rootComponent instanceof SupportsResponsiveSteps) {
            ((SupportsResponsiveSteps) rootComponent).setResponsiveSteps(steps);
        }
    }

    public String getSummaryText() {
        return getContent().getSummaryText();
    }

    public void setSummaryText(String summary) {
        getContent().setSummaryText(summary);

        if (emptyConfiguration != null) {
            emptyConfiguration.setName(summary);
        }
    }

    public boolean isOpened() {
        return getContent().isOpened();
    }

    public void setOpened(boolean opened) {
        getContent().setOpened(opened);
    }

    public Registration addOpenedChangeListener(ComponentEventListener<OpenedChangeEvent> listener) {
        if (openedChangeRegistration == null) {
            openedChangeRegistration = getContent().addOpenedChangeListener(this::onOpenedChanged);
        }

        Registration registration = getEventBus().addListener(OpenedChangeEvent.class, listener);
        return Registration.once(() -> removeOpenedChangeListener(registration));
    }

    protected void removeOpenedChangeListener(Registration registration) {
        registration.remove();
        if (!getEventBus().hasListener(OpenedChangeEvent.class)) {
            openedChangeRegistration.remove();
            openedChangeRegistration = null;
        }
    }

    protected void onOpenedChanged(Details.OpenedChangeEvent openedChangeEvent) {
        OpenedChangeEvent event = new OpenedChangeEvent(this, openedChangeEvent.isFromClient());
        getEventBus().fireEvent(event);
    }

    @Nullable
    public Predicate<MetaPropertyPath> getPropertyFiltersPredicate() {
        return propertyFiltersPredicate;
    }

    public void setPropertyFiltersPredicate(@Nullable Predicate<MetaPropertyPath> propertyFiltersPredicate) {
        this.propertyFiltersPredicate = propertyFiltersPredicate;
    }

    public void addPropertyFiltersPredicate(Predicate<MetaPropertyPath> propertyFiltersPredicate) {
        if (this.propertyFiltersPredicate == null) {
            setPropertyFiltersPredicate(propertyFiltersPredicate);
        } else {
            this.propertyFiltersPredicate = this.propertyFiltersPredicate.and(propertyFiltersPredicate);
        }
    }

    public Configuration getCurrentConfiguration() {
        return currentConfiguration != null
                ? currentConfiguration
                : emptyConfiguration;
    }

    public void setCurrentConfiguration(Configuration currentConfiguration) {
        setCurrentConfigurationInternal(currentConfiguration, false);
    }

    protected void setCurrentConfigurationInternal(Configuration currentConfiguration, boolean fromClient) {
        if (configurations.contains(currentConfiguration)
                || getEmptyConfiguration().equals(currentConfiguration)) {
            if (this.currentConfiguration != currentConfiguration) {
                clearValues();
            }

            Configuration previousConfiguration = this.currentConfiguration;
            this.currentConfiguration = currentConfiguration;

            refreshCurrentConfigurationLayout();
            updateSelectConfigurationDropdown();

            if (currentConfiguration != previousConfiguration) {
                ConfigurationChangeEvent configurationChangeEvent =
                        new ConfigurationChangeEvent(this, currentConfiguration, previousConfiguration, fromClient);
                getEventBus().fireEvent(configurationChangeEvent);
            }
        }
    }

    protected void refreshCurrentConfigurationLayout() {
        if (rootLogicalFilterComponent != null) {
            contentWrapper.remove(((Component) rootLogicalFilterComponent));
        }

        LogicalFilterComponent<?> rootComponent = getCurrentConfiguration().getRootLogicalFilterComponent();
        boolean isAnyFilterComponentVisible = rootComponent.getFilterComponents().stream()
                .anyMatch(filterComponent -> ((Component) filterComponent).isVisible());
        if (isAnyFilterComponentVisible) {
            updateRootLogicalFilterComponent(getCurrentConfiguration().getRootLogicalFilterComponent());
        } else {
            rootLogicalFilterComponent = rootComponent;
        }

        updateDataLoaderCondition();
        updateRootLayoutSummaryText();
    }

    protected void updateRootLogicalFilterComponent(LogicalFilterComponent<?> logicalFilterComponent) {
        rootLogicalFilterComponent = logicalFilterComponent;
        contentWrapper.addComponentAsFirst(((Component) rootLogicalFilterComponent));

        if (rootLogicalFilterComponent instanceof SupportsResponsiveSteps) {
            ((SupportsResponsiveSteps) rootLogicalFilterComponent).setResponsiveSteps(getResponsiveSteps());
        }

        rootLogicalFilterComponent.setAutoApply(isAutoApply());

        if (!(getCurrentConfiguration() instanceof DesignTimeConfiguration)) {
            for (FilterComponent filterComponent : rootLogicalFilterComponent.getFilterComponents()) {
                if (filterComponent instanceof SingleFilterComponentBase) {
                    updateConditionRemoveButton((SingleFilterComponentBase<?>) filterComponent);
                }

                if (filterComponent instanceof PropertyFilter) {
                    PropertyFilter<?> propertyFilter = (PropertyFilter<?>) filterComponent;
                    propertyFilter.addOperationChangeListener(operationChangeEvent -> {
                        updateConditionRemoveButton(propertyFilter);
                        resetFilterComponentDefaultValue(propertyFilter);
                    });
                }
            }
        }
    }

    protected void resetFilterComponentDefaultValue(PropertyFilter<?> propertyFilter) {
        getCurrentConfiguration().resetFilterComponentDefaultValue(propertyFilter.getParameterName());
    }

    protected void updateConditionRemoveButton(SingleFilterComponentBase<?> singleFilter) {
        String removeButtonPrefix = singleFilter.getInnerComponentPrefix();
        String removeButtonId = removeButtonPrefix + CONDITION_REMOVE_BUTTON_ID_SUFFIX;

        HorizontalLayout singleFilterLayout = singleFilter.getRoot();
        Optional<Component> existingRemoveButton = UiComponentUtils.findComponent(singleFilterLayout, removeButtonId);

        if (getCurrentConfiguration().isFilterComponentModified(singleFilter)) {
            // If the removeButton is added to the singleFilterLayout
            // but is not located at the end, then we delete it and
            // re-add it to the end. This situation is possible when
            // changing the type of operation.
            if (existingRemoveButton.isPresent()
                    && singleFilterLayout.indexOf(existingRemoveButton.get()) != singleFilterLayout.getComponentCount() - 1) {
                singleFilterLayout.remove(existingRemoveButton.get());
                existingRemoveButton = Optional.empty();
            }

            if (existingRemoveButton.isEmpty()) {
                Component newRemoveButton = createConditionRemoveButton(singleFilter, removeButtonId);
                singleFilterLayout.add(newRemoveButton);
            }
        } else {
            existingRemoveButton.ifPresent(singleFilterLayout::remove);
        }
    }

    protected Component createConditionRemoveButton(SingleFilterComponent<?> singleFilter, String removeButtonId) {
        JmixButton conditionRemoveButton = uiComponents.create(JmixButton.class);
        conditionRemoveButton.setId(removeButtonId);
        conditionRemoveButton.setIcon(VaadinIcon.TRASH.create());
        conditionRemoveButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);

        conditionRemoveButton.addClickListener(clickEvent -> {
            removeFilterComponent(singleFilter);
            refreshCurrentConfigurationLayout();
            apply();
        });

        return conditionRemoveButton;
    }

    protected void removeFilterComponent(FilterComponent filterComponent) {
        LogicalFilterComponent<?> rootLogicalComponent = getCurrentConfiguration().getRootLogicalFilterComponent();
        if (filterComponent instanceof SingleFilterComponent) {
            getCurrentConfiguration()
                    .resetFilterComponentDefaultValue(((SingleFilterComponent<?>) filterComponent).getParameterName());
        }
        rootLogicalComponent.remove(filterComponent);
        getCurrentConfiguration().setFilterComponentModified(filterComponent, false);
    }

    protected void updateRootLayoutSummaryText() {
        StringBuilder stringBuilder = new StringBuilder(getConfigurationName(getEmptyConfiguration()));
        if (!getEmptyConfiguration().equals(getCurrentConfiguration())) {
            stringBuilder.append(" : ")
                    .append(getConfigurationName(getCurrentConfiguration()));
        }

        setSummaryText(stringBuilder.toString());
    }

    protected String getConfigurationName(Configuration configuration) {
        String caption = configuration.getName();
        if (caption == null) {
            caption = messages.findMessage(configuration.getId(), null);
            if (caption == null) {
                caption = configuration.getId();
            }
        }

        return caption;
    }

    protected void updateDataLoaderCondition() {
        if (dataLoader != null) {
            LogicalFilterComponent<?> logicalFilterComponent = getCurrentConfiguration().getRootLogicalFilterComponent();
            LogicalCondition filterCondition = logicalFilterComponent.getQueryCondition();

            LogicalCondition resultCondition;
            if (initialDataLoaderCondition instanceof LogicalCondition) {
                resultCondition = (LogicalCondition) initialDataLoaderCondition.copy();
                resultCondition.add(filterCondition);
            } else if (initialDataLoaderCondition != null) {
                resultCondition = LogicalCondition.and()
                        .add(initialDataLoaderCondition)
                        .add(filterCondition);
            } else {
                resultCondition = filterCondition;
            }

            dataLoader.setCondition(resultCondition);
        }
    }

    public Configuration getEmptyConfiguration() {
        return emptyConfiguration;
    }

    @Nullable
    public Configuration getConfiguration(String id) {
        return configurations.stream()
                .filter(configuration -> id.equals(configuration.getId()))
                .findFirst()
                .orElse(null);
    }

    public List<Configuration> getConfigurations() {
        return Collections.unmodifiableList(configurations);
    }

    public DesignTimeConfiguration addConfiguration(String id, @Nullable String name) {
        return addConfiguration(id, name, LogicalFilterComponent.Operation.AND);
    }

    public DesignTimeConfiguration addConfiguration(String id, @Nullable String name,
                                                    LogicalFilterComponent.Operation rootOperation) {
        LogicalFilterComponent<?> rootComponent = createConfigurationRootLogicalFilterComponent(rootOperation);
        DesignTimeConfiguration newConfiguration = new DesignTimeConfiguration(id, name, rootComponent, this);

        addConfiguration(newConfiguration);

        return newConfiguration;
    }

    public void addConfiguration(Configuration configuration) {
        configurations.add(configuration);
        addSelectConfigurationAction(configuration);
    }

    public void removeConfiguration(Configuration configuration) {
        if (configuration != getEmptyConfiguration()
                && !(configuration instanceof DesignTimeConfiguration)) {
            configurations.remove(configuration);
            configuration.getRootLogicalFilterComponent().getElement().removeFromParent();

            if (configuration == getCurrentConfiguration()) {
                setCurrentConfigurationInternal(getEmptyConfiguration(), false);
                apply();
            } else {
                updateSelectConfigurationDropdown();
            }
        }
    }

    protected void updateSelectConfigurationDropdown() {
        if (applyButton == null) {
            return;
        }

        applyButton.removeAll();
        initSelectConfigurationDropdown();
        for (Configuration configuration : getConfigurations()) {
            addSelectConfigurationAction(configuration);
        }
    }

    protected void addSelectConfigurationAction(Configuration configuration) {
        Action configurationAction = createConfigurationAction(configuration);
        applyButton.addItem(configurationAction.getId(), configurationAction);
    }

    protected Action createConfigurationAction(Configuration configuration) {
        return new BaseAction("filter_select_" + configuration.getId())
                .withText(getConfigurationName(configuration))
                .withHandler(actionPerformedEvent -> {
                    setCurrentConfigurationInternal(configuration, true);
                    apply();
                });
    }

    public void addCondition(FilterComponent filterComponent) {
        if (conditions == null) {
            conditions = new ArrayList<>();
        }

        conditions.add(filterComponent);
    }

    public List<FilterComponent> getConditions() {
        return conditions != null
                ? Collections.unmodifiableList(conditions)
                : Collections.emptyList();
    }

    public void removeCondition(FilterComponent filterComponent) {
        if (conditions == null) {
            return;
        }

        conditions.remove(filterComponent);

        if (conditions.isEmpty()) {
            conditions = null;
        }
    }

    public Registration addConfigurationChangeListener(ComponentEventListener<ConfigurationChangeEvent> listener) {
        return getEventBus().addListener(ConfigurationChangeEvent.class, listener);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void clearValues() {
        if (rootLogicalFilterComponent == null
                || rootLogicalFilterComponent.getFilterComponents().isEmpty()) {
            return;
        }

        for (FilterComponent component : rootLogicalFilterComponent.getFilterComponents()) {
            if (component instanceof SingleFilterComponentBase) {
                SingleFilterComponentBase singleFilterComponent = (SingleFilterComponentBase) component;
                singleFilterComponent.setValue(getCurrentConfiguration()
                        .getFilterComponentDefaultValue(singleFilterComponent.getParameterName()));
                getDataLoader().removeParameter(singleFilterComponent.getParameterName());
            }
        }
    }

    /**
     * Event sent when the {@link Configuration} is changed.
     */
    public static class ConfigurationChangeEvent extends ComponentEvent<Filter> {

        protected final Configuration newConfiguration;
        protected final Configuration previousConfiguration;

        public ConfigurationChangeEvent(Filter source,
                                        Configuration newConfiguration,
                                        @Nullable Configuration previousConfiguration,
                                        boolean fromClient) {
            super(source, fromClient);
            this.newConfiguration = newConfiguration;
            this.previousConfiguration = previousConfiguration;
        }

        /**
         * @return new configuration value
         */
        public Configuration getNewConfiguration() {
            return newConfiguration;
        }

        /**
         * @return previous configuration value
         */
        @Nullable
        public Configuration getPreviousConfiguration() {
            return previousConfiguration;
        }
    }

    public static class OpenedChangeEvent extends ComponentEvent<Filter> {
        private final boolean opened;

        public OpenedChangeEvent(Filter source, boolean fromClient) {
            super(source, fromClient);

            this.opened = source.isOpened();
        }

        public boolean isOpened() {
            return opened;
        }
    }
}
