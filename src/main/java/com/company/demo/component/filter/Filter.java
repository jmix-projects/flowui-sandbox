package com.company.demo.component.filter;

import com.company.demo.action.filter.FilterAction;
import com.company.demo.action.filter.FilterAddConditionAction;
import com.company.demo.component.filter.configuration.DesignTimeConfiguration;
import com.company.demo.component.filter.configuration.RunTimeConfiguration;
import com.company.demo.component.groupfilter.GroupFilter;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
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
import io.jmix.flowui.component.SupportsLabelPosition;
import io.jmix.flowui.component.details.JmixDetails;
import io.jmix.flowui.component.filer.FilterComponent;
import io.jmix.flowui.component.filer.SingleFilterComponent;
import io.jmix.flowui.component.filer.SingleFilterComponentBase;
import io.jmix.flowui.component.propertyfilter.PropertyFilter;
import io.jmix.flowui.kit.action.Action;
import io.jmix.flowui.kit.action.BaseAction;
import io.jmix.flowui.kit.component.HasActions;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.kit.component.combobutton.ComboButton;
import io.jmix.flowui.kit.component.combobutton.ComboButtonVariant;
import io.jmix.flowui.kit.component.dropdownbutton.ActionItem;
import io.jmix.flowui.kit.component.dropdownbutton.DropdownButton;
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

public class Filter extends Composite<JmixDetails> implements SupportsLabelPosition, HasActions,
        ApplicationContextAware, InitializingBean {

    protected static final String FILTER_CLASS_NAME = "jmix-filter";

    protected ApplicationContext applicationContext;
    protected CurrentAuthentication currentAuthentication;
    protected UiComponents uiComponents;
    protected Actions actions;
    protected Messages messages;
    protected Metadata metadata;
    protected DialogWindows dialogWindows;
    protected FilterSupport filterSupport;

    protected DataLoader dataLoader;
    protected Condition initialDataLoaderCondition;
    protected boolean autoApply;
    protected LabelPosition labelPosition = LabelPosition.ASIDE;
    protected Predicate<MetaPropertyPath> propertiesFilterPredicate; // TODO: gg, rename to propertyFiltersPredicate?

    protected LogicalFilterComponent rootLogicalFilterComponent;



    // TODO: gg, don't need it?
    protected FormLayout conditionsLayout; // TODO: gg, rename
    protected VerticalLayout contentWrapper;
    protected HorizontalLayout controlsLayout;
    protected ComboButton searchButton;
    protected JmixButton addConditionButton;
    protected DropdownButton settingsButton;
    protected Registration openedChangeRegistration;

    protected Configuration emptyConfiguration;
    protected Configuration currentConfiguration;
    protected List<Configuration> configurations = new ArrayList<>();

    protected List<FilterComponent> conditions = new ArrayList<>(); // TODO: gg, lazy

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
        // TODO: gg, implement
//        this.columnsCount = componentProperties.getFilterColumnsCount();

        initLayout();
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

        // TODO: gg, extract
        controlsLayout = createControlsLayout();
        initControlsLayout(controlsLayout);
        contentWrapper.add(controlsLayout);
    }

    protected VerticalLayout createContentWrapper() {
        return uiComponents.create(VerticalLayout.class);
    }

    protected void initContentWrapper(VerticalLayout contentWrapper) {
        contentWrapper.setPadding(false);
        // TODO: gg, something else?
    }



    protected HorizontalLayout createControlsLayout() {
        return uiComponents.create(HorizontalLayout.class);
    }

    protected void initControlsLayout(HorizontalLayout controlsLayout) {
        // TODO: gg, implement

        searchButton = createSearchButton();
        initSearchButton(searchButton);
        controlsLayout.add(searchButton);

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
        addConditionAction.setTarget(this); // TODO: gg, set automatically
        addConditionAction.setText(messages.getMessage("filter.addConditionButton.text"));
        addConditionAction.setIcon(null);
        addConditionButton.setAction(addConditionAction, false);
    }

    protected ComboButton createSearchButton() {
        return uiComponents.create(ComboButton.class);
    }

    protected void initSearchButton(ComboButton searchButton) {
        searchButton.addClickListener(this::onSearchButtonClick);
        searchButton.addThemeVariants(ComboButtonVariant.LUMO_SUCCESS, ComboButtonVariant.LUMO_PRIMARY);
        updateSearchButtonText(isAutoApply());

        initSelectConfigurationDropdown();
    }

    protected void initSelectConfigurationDropdown() {
        Action resetFilterAction = createResetFilterAction();
        searchButton.addItem(resetFilterAction.getId(), resetFilterAction);
    }

    protected void updateSearchButtonText(boolean autoApply) {
        String text = autoApply
                ? messages.getMessage("filter.searchButton.autoApply")
                : messages.getMessage("filter.searchButton");
        searchButton.setText(text);
    }

    protected void onSearchButtonClick(ClickEvent<MenuItem> clickEvent) {
        getDataLoader().load();
    }

    // TODO: gg, extract
    protected Action createResetFilterAction() {
        return new BaseAction("filter_reset")
                .withText(messages.getMessage("actions.Filter.Reset"))
                .withHandler(actionPerformedEvent -> {
                    Configuration configuration = getEmptyConfiguration();
                    configuration.getRootLogicalFilterComponent().removeAll();
                    configuration.setModified(false);
                    setCurrentConfiguration(configuration);
                    apply();
                });
    }

    protected DropdownButton createSettingsButton() {
        return uiComponents.create(DropdownButton.class);
    }

    protected void initSettingsButton(DropdownButton settingsButton) {
        // TODO: gg, extract
        if (getActions().isEmpty()) {
            List<FilterAction> defaultFilterActions = filterSupport.getDefaultFilterActions(this);
            for (FilterAction filterAction : defaultFilterActions) {
                addAction(filterAction);
            }
        }

        // TODO: gg, extract?
        settingsButton.setVisible(configurationModifyPermitted);
    }

    // TODO: gg,
    public HorizontalLayout getControlsLayout() {
        return controlsLayout;
    }

    public Condition getQueryCondition() {
        return currentConfiguration.getQueryCondition();
    }

    public DataLoader getDataLoader() {
        return dataLoader;
    }

    public void setDataLoader(DataLoader dataLoader) {
        checkState(this.dataLoader == null, "DataLoader has already been initialized");
        checkNotNull(dataLoader);

        this.dataLoader = dataLoader;
        this.initialDataLoaderCondition = dataLoader.getCondition();

        initEmptyConfiguration();
    }

    protected void initEmptyConfiguration() {
        LogicalFilterComponent configurationLogicalComponent =
                createConfigurationRootLogicalFilterComponent(LogicalFilterComponent.Operation.AND);
        emptyConfiguration =
                new RunTimeConfiguration("empty_configuration", configurationLogicalComponent, this);

        String emptyConfigurationName = StringUtils.isNotEmpty(getSummaryText())
                ? getSummaryText()
                : messages.getMessage("filter.emptyConfiguration.name");
        emptyConfiguration.setName(emptyConfigurationName);

        setCurrentConfiguration(emptyConfiguration);
    }

    protected LogicalFilterComponent createConfigurationRootLogicalFilterComponent(
            LogicalFilterComponent.Operation rootOperation) {
        GroupFilter rootGroupFilter = uiComponents.create(GroupFilter.class);
        rootGroupFilter.setConditionModificationDelegated(true);
        rootGroupFilter.setOperation(rootOperation);
        rootGroupFilter.setOperationTextVisible(false);

        if (dataLoader != null) {
            rootGroupFilter.setDataLoader(dataLoader);
            rootGroupFilter.setAutoApply(autoApply);
        }

//        rootGroupFilter.setFrame(getFrame());
        // TODO: gg, just add?
//        rootGroupFilter.setParent(this);
        contentWrapper.addComponentAsFirst(rootGroupFilter);

        return rootGroupFilter;
    }

    public boolean isAutoApply() {
        return autoApply;
    }

    public void setAutoApply(boolean autoApply) {
        if (this.autoApply != autoApply) {
            this.autoApply = autoApply;

            updateSearchButtonText(autoApply);

            getCurrentConfiguration()
                    .getRootLogicalFilterComponent()
                    .getOwnFilterComponents()
                    .forEach(filterComponent -> filterComponent.setAutoApply(autoApply));
        }
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
    public LabelPosition getLabelPosition() {
        return labelPosition;
    }

    @Override
    public void setLabelPosition(LabelPosition labelPosition) {
        if (this.labelPosition != labelPosition) {
            this.labelPosition = labelPosition;

            LogicalFilterComponent rootComponent = getCurrentConfiguration().getRootLogicalFilterComponent();
            if (rootComponent instanceof SupportsLabelPosition) {
                ((SupportsLabelPosition) rootComponent).setLabelPosition(labelPosition);
            }
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

        // TODO: gg, is it ok?
        Registration registration = getEventBus().addListener(OpenedChangeEvent.class, listener);
        return Registration.once(() -> {
            registration.remove();
            if (!getEventBus().hasListener(OpenedChangeEvent.class)) {
                openedChangeRegistration.remove();
                openedChangeRegistration = null;
            }
        });
    }

    protected void onOpenedChanged(Details.OpenedChangeEvent openedChangeEvent) {
        OpenedChangeEvent event = new OpenedChangeEvent(this, openedChangeEvent.isFromClient());
        getEventBus().fireEvent(event);
    }

    // TODO: gg, remove if possible
    @Override
    public void addAction(Action action, int index) {
        // TODO: gg, implement using settingsButton
    }

    @Override
    public void removeAction(Action action) {
        // TODO: gg, implement using settingsButton
    }

    @Override
    public Collection<Action> getActions() {
        // TODO: gg, implement using settingsButton
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public Action getAction(String id) {
        // TODO: gg, implement using settingsButton
        return null;
    }

    @Override
    public void addAction(Action action) {
        // TODO: gg, implement using settingsButton
        HasActions.super.addAction(action);
    }

    @Override
    public void removeAction(String id) {
        // TODO: gg, implement using settingsButton
        HasActions.super.removeAction(id);
    }

    @Override
    public void removeAllActions() {
        // TODO: gg, implement using settingsButton
        HasActions.super.removeAllActions();
    }

    @Nullable
    public Predicate<MetaPropertyPath> getPropertiesFilterPredicate() {
        return propertiesFilterPredicate;
    }

    public void setPropertiesFilterPredicate(@Nullable Predicate<MetaPropertyPath> propertiesFilterPredicate) {
        this.propertiesFilterPredicate = propertiesFilterPredicate;
    }

    public void addPropertiesFilterPredicate(Predicate<MetaPropertyPath> propertiesFilterPredicate) {
        if (this.propertiesFilterPredicate == null) {
            setPropertiesFilterPredicate(propertiesFilterPredicate);
        } else {
            this.propertiesFilterPredicate = this.propertiesFilterPredicate.and(propertiesFilterPredicate);
        }
    }

    public Configuration getCurrentConfiguration() {
        return currentConfiguration;
    }

    public void setCurrentConfiguration(Configuration currentConfiguration) {
        if (configurations.contains(currentConfiguration) || getEmptyConfiguration().equals(currentConfiguration)) {
            if (this.currentConfiguration != currentConfiguration) {
                clearValues();
            }

            Configuration previousConfiguration = this.currentConfiguration;
            this.currentConfiguration = currentConfiguration;

            refreshCurrentConfigurationLayout();
            updateSelectConfigurationDropdown();

            if (currentConfiguration != previousConfiguration) {
                ConfigurationChangeEvent configurationChangeEvent =
                        new ConfigurationChangeEvent(this, currentConfiguration, previousConfiguration, true); // TODO: gg, true?
                getEventBus().fireEvent(configurationChangeEvent);
            }
        }
    }

    // TODO: gg, public? try to remove
    public void loadConfigurationsAndApplyDefault() {
        Map<Configuration, Boolean> configurationsMap = filterSupport.getConfigurationsMap(this);
        boolean defaultForAllConfigurationApplied = false;
        for (Map.Entry<Configuration, Boolean> entry : configurationsMap.entrySet()) {
            addConfiguration(entry.getKey());
            if (!defaultForAllConfigurationApplied && entry.getValue()) {
                setCurrentConfiguration(entry.getKey());
                defaultForAllConfigurationApplied = true;
            }
        }
    }

    // TODO: gg, public?
    public void refreshCurrentConfigurationLayout() {
        if (rootLogicalFilterComponent != null) {
            contentWrapper.remove(((Component) rootLogicalFilterComponent));
        }

        LogicalFilterComponent rootComponent = getCurrentConfiguration().getRootLogicalFilterComponent();
        boolean isAnyFilterComponentVisible = rootComponent.getFilterComponents().stream()
                .anyMatch(filterComponent -> ((Component) filterComponent).isVisible());
        if (isAnyFilterComponentVisible) {
            updateRootLogicalFilterComponent(getCurrentConfiguration().getRootLogicalFilterComponent());
        } else {
            rootLogicalFilterComponent = rootComponent;
        }

        // TODO: gg, send event so actions update themself

        updateDataLoaderCondition();
        updateRootLayoutCaption();
        updateActionsState();
    }

    protected void updateRootLogicalFilterComponent(LogicalFilterComponent logicalFilterComponent) {
        // TODO: gg, implement
        /*logicalFilterComponent.setParent(null);
        ComponentsHelper.getComposition(logicalFilterComponent).setParent(null);
        logicalFilterComponent.addStyleName(FILTER_ROOT_COMPONENT_STYLENAME);*/

        rootLogicalFilterComponent = logicalFilterComponent;
        contentWrapper.addComponentAsFirst(((Component) rootLogicalFilterComponent));

        // TODO: gg, implement
        /*if (rootLogicalFilterComponent instanceof SupportsColumnsCount) {
            ((SupportsColumnsCount) rootLogicalFilterComponent).setColumnsCount(getColumnsCount());
        }*/

        if (rootLogicalFilterComponent instanceof SupportsLabelPosition) {
            ((SupportsLabelPosition) rootLogicalFilterComponent).setLabelPosition(getLabelPosition());
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

                /*if (filterComponent instanceof BelongToFrame) {
                    ((BelongToFrame) filterComponent).setFrame(getFrame());
                }*/
            }
        }
    }

    protected void resetFilterComponentDefaultValue(PropertyFilter<?> propertyFilter) {
        getCurrentConfiguration().resetFilterComponentDefaultValue(propertyFilter.getParameterName());
    }

    protected void updateConditionRemoveButton(SingleFilterComponentBase<?> singleFilter) {
        // TODO: gg, implement
        /*String removeButtonPrefix = ((SingleFilterComponentBase<?>) singleFilter).getInnerComponentPrefix();
        String removeButtonId = removeButtonPrefix + "conditionRemoveButton";

        HBoxLayout singleFilterLayout = ((AbstractSingleFilterComponent<?>) singleFilter).getComposition();
        LinkButton removeButton = (LinkButton) singleFilterLayout.getComponent(removeButtonId);

        if (getCurrentConfiguration().isFilterComponentModified(singleFilter)) {
            // If the removeButton is added to the singleFilterLayout
            // but is not located at the end, then we delete it and
            // re-add it to the end.
            // This situation is possible when changing the type of operation.
            if (removeButton != null
                    && singleFilterLayout.indexOf(removeButton) != singleFilterLayout.getComponents().size() - 1) {
                singleFilterLayout.remove(removeButton);
                removeButton = null;
            }

            if (removeButton == null) {
                removeButton = createConditionRemoveButton(singleFilter, removeButtonId);
                singleFilterLayout.add(removeButton);
            }
        } else {
            if (removeButton != null) {
                singleFilterLayout.remove(removeButton);
            }
        }*/
    }

    protected Component createConditionRemoveButton(SingleFilterComponent<?> singleFilter, String removeButtonId) {
        // TODO: gg, implement
        /*LinkButton conditionRemoveButton = uiComponents.create(LinkButton.NAME);
        conditionRemoveButton.setId(removeButtonId);
        conditionRemoveButton.setIconFromSet(JmixIcon.TIMES);
        conditionRemoveButton.addStyleName(ThemeClassNames.BUTTON_ICON_ONLY);
        conditionRemoveButton.setAlignment(Alignment.MIDDLE_CENTER);

        conditionRemoveButton.addClickListener(clickEvent -> {
            removeFilterComponent(singleFilter);
            refreshCurrentConfigurationLayout();
            apply();
        });

        return conditionRemoveButton;*/
        return null;
    }

    protected void removeFilterComponent(FilterComponent filterComponent) {
        LogicalFilterComponent rootLogicalComponent = getCurrentConfiguration().getRootLogicalFilterComponent();
        if (filterComponent instanceof SingleFilterComponent) {
            getCurrentConfiguration().resetFilterComponentDefaultValue(((SingleFilterComponent<?>) filterComponent).getParameterName());
        }
        rootLogicalComponent.remove(filterComponent);
        getCurrentConfiguration().setFilterComponentModified(filterComponent, false);
    }

    protected void updateRootLayoutCaption() {
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
            LogicalFilterComponent logicalFilterComponent = getCurrentConfiguration().getRootLogicalFilterComponent();
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

    // TODO: gg, actions must do it themself
    protected void updateActionsState() {
//        settingsButton.getActions().forEach(Action::refreshState);
        searchButton.getItems().stream()
                .filter(item -> item instanceof ActionItem)
                .map(item -> ((ActionItem) item).getAction())
                .forEach(Action::refreshState);

        if (addConditionButton.getAction() != null) {
            addConditionButton.getAction().refreshState();
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
        return configurations;
    }

    public DesignTimeConfiguration addConfiguration(String id, @Nullable String name) {
        return addConfiguration(id, name, LogicalFilterComponent.Operation.AND);
    }

    public DesignTimeConfiguration addConfiguration(String id, @Nullable String name,
                                                    LogicalFilterComponent.Operation rootOperation) {
        LogicalFilterComponent rootComponent = createConfigurationRootLogicalFilterComponent(rootOperation);

        DesignTimeConfiguration newConfiguration =
                new DesignTimeConfiguration(id, name, rootComponent, this);

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
            // TODO: gg, how to replace?
//            configuration.getRootLogicalFilterComponent().setParent(null);
            configuration.getRootLogicalFilterComponent().getElement().removeFromParent();

            if (configuration == getCurrentConfiguration()) {
                setCurrentConfiguration(getEmptyConfiguration());
                apply();
            } else {
                updateSelectConfigurationDropdown();
            }
        }
    }

    protected void updateSelectConfigurationDropdown() {
        searchButton.removeAll();
        initSelectConfigurationDropdown();
        for (Configuration configuration : getConfigurations()) {
            addSelectConfigurationAction(configuration);
        }
    }

    protected void addSelectConfigurationAction(Configuration configuration) {
        Action configurationAction = new BaseAction("filter_select_" + configuration.getId())
                .withText(getConfigurationName(configuration))
                .withHandler(actionPerformedEvent -> {
                    setCurrentConfiguration(configuration);
                    apply();
                });
        searchButton.addItem(configurationAction.getId(), configurationAction);
    }

    public void addCondition(FilterComponent filterComponent) {
        conditions.add(filterComponent);
    }

    public List<FilterComponent> getConditions() {
        return conditions;
    }

    public void removeCondition(FilterComponent filterComponent) {
        conditions.remove(filterComponent);
    }

    public Registration addConfigurationChangeListener(ComponentEventListener<ConfigurationChangeEvent> listener) {
        return getEventBus().addListener(ConfigurationChangeEvent.class, listener);
    }

    protected void clearValues() {
        if (rootLogicalFilterComponent != null && !rootLogicalFilterComponent.getFilterComponents().isEmpty()) {
            for (FilterComponent component : rootLogicalFilterComponent.getFilterComponents()) {
                if (component instanceof SingleFilterComponentBase) {
                    SingleFilterComponentBase singleFilterComponent = (SingleFilterComponentBase) component;
                    singleFilterComponent.setValue(
                            currentConfiguration.getFilterComponentDefaultValue(singleFilterComponent.getParameterName()));
                    getDataLoader().removeParameter(singleFilterComponent.getParameterName());
                }
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
