package com.company.demo.app.filter.conditiion;

import com.company.demo.component.filter.Configuration;
import com.company.demo.component.filter.builder.PropertyConditionBuilder;
import com.company.demo.entity.filter.FilterCondition;
import com.company.demo.entity.filter.HeaderFilterCondition;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.LoadContext;
import io.jmix.core.Messages;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.flowui.component.SupportsTypedValue;
import io.jmix.flowui.component.SupportsTypedValue.TypedValueChangeEvent;
import io.jmix.flowui.component.grid.TreeDataGrid;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Route(value = "add-condition", layout = MainView.class)
@ViewController("flowui_AddConditionView")
@ViewDescriptor("add-condition-view.xml")
@LookupComponent("filterConditionsTreeDataGrid")
@DialogMode(width = "25em", height = "37.5em")
public class AddConditionView extends StandardListView<FilterCondition> {

//    @ViewComponent
//    protected TypedTextField<String> conditionFilterField;
    @ViewComponent
    protected TreeDataGrid<FilterCondition> filterConditionsTreeDataGrid;

    @ViewComponent
    protected CollectionLoader<FilterCondition> filterConditionsDl;

    @Autowired
    protected Messages messages;

    protected List<FilterCondition> conditions = new ArrayList<>();
    protected List<FilterCondition> rootConditions = new ArrayList<>();
    protected List<FilterCondition> foundConditions = new ArrayList<>();
    protected MetaClass filterMetaClass;
    protected HeaderFilterCondition propertiesHeaderCondition;

    protected Configuration currentFilterConfiguration;

    public List<FilterCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<FilterCondition> conditions) {
        this.conditions = conditions;
        this.foundConditions = new ArrayList<>(conditions);
        this.rootConditions = searchRootConditions(conditions);
    }

    @Nullable
    protected List<FilterCondition> searchRootConditions(List<FilterCondition> conditions) {
        return conditions.stream()
                .filter(condition -> condition.getParent() == null)
                .collect(Collectors.toList());
    }

    public Configuration getCurrentFilterConfiguration() {
        return currentFilterConfiguration;
    }

    public void setCurrentFilterConfiguration(Configuration currentFilterConfiguration) {
        this.currentFilterConfiguration = currentFilterConfiguration;
        this.filterMetaClass = currentFilterConfiguration.getOwner()
                .getDataLoader()
                .getContainer()
                .getEntityMetaClass();
    }

    @Install(to = "filterConditionsDl", target = Target.DATA_LOADER)
    protected List<FilterCondition> filterConditionsDlLoadDelegate(LoadContext<FilterCondition> loadContext) {
        return foundConditions;
    }

    @Subscribe
    protected void onReady(ReadyEvent event) {
        initFilterConditionsTreeDataGrid();
    }

    protected void initFilterConditionsTreeDataGrid() {
//        filterConditionsTreeDataGrid.collapseAll(); // TODO: gg, implement
        String propertiesCaption =
                messages.getMessage(PropertyConditionBuilder.class, "propertyConditionBuilder.headerCaption");

        propertiesHeaderCondition = getHeaderFilterConditionByCaption(propertiesCaption);
        if (propertiesHeaderCondition != null) {
            filterConditionsTreeDataGrid.expand(propertiesHeaderCondition);
        }
    }

    @Nullable
    protected HeaderFilterCondition getHeaderFilterConditionByCaption(String caption) {
        return conditions.stream()
                .filter(condition -> condition instanceof HeaderFilterCondition
                        && Objects.equals(condition.getLocalizedLabel(), caption))
                .map(condition -> (HeaderFilterCondition) condition)
                .findFirst()
                .orElse(null);
    }

    @Subscribe("conditionFilterField")
    protected void onConditionFilterFieldValueChange(TypedValueChangeEvent<TypedTextField<String>, String> event) {
        search(event.getValue());
    }

    protected void search(@Nullable String searchValue) {
        foundConditions.clear();

        boolean loadAllConditions = StringUtils.isEmpty(searchValue) || rootConditions == null;
        if (!loadAllConditions) {
            findConditionsRecursively(rootConditions, searchValue, false);

            List<FilterCondition> exactlyFoundConditions = new ArrayList<>(foundConditions);
            for (FilterCondition condition : exactlyFoundConditions) {
                addParentToExpand(condition);
            }

//            filterConditionsTreeDataGrid.expandTree(); // TODO: gg, replace
        } else {
            foundConditions = new ArrayList<>(conditions);
        }

        filterConditionsDl.load();

        if (loadAllConditions) {
            initFilterConditionsTreeDataGrid();
        }
    }

    protected void findConditionsRecursively(List<FilterCondition> conditions,
                                             String searchValue,
                                             boolean addChildrenAutomatically) {
        for (FilterCondition condition : conditions) {
            boolean conditionFound = addChildrenAutomatically ||
                    StringUtils.containsIgnoreCase(condition.getLocalizedLabel(), searchValue);
            if (conditionFound) {
                foundConditions.add(condition);
            }

            List<FilterCondition> children = searchChildren(condition);
            if (!children.isEmpty()) {
                findConditionsRecursively(children, searchValue, conditionFound);
            }
        }
    }

    protected List<FilterCondition> searchChildren(FilterCondition condition) {
        return conditions.stream()
                .filter(child -> Objects.equals(child.getParent(), condition))
                .collect(Collectors.toList());
    }

    protected void addParentToExpand(FilterCondition child) {
        FilterCondition parent = child.getParent();
        if (parent != null) {
            if (!foundConditions.contains(parent)) {
                foundConditions.add(parent);
            }
            addParentToExpand(parent);
        }
    }
}