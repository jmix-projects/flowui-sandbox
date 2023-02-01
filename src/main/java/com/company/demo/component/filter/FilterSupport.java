/*
 * Copyright 2020 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.company.demo.component.filter;

import com.company.demo.action.filter.FilterAction;
import com.company.demo.component.filter.configuration.RunTimeConfiguration;
import com.google.common.collect.ImmutableSet;
import io.jmix.core.annotation.Internal;
import io.jmix.flowui.Actions;
import io.jmix.flowui.component.filer.FilterComponent;
import io.jmix.flowui.component.filer.SingleFilterComponent;
import io.jmix.flowui.component.filer.SingleFilterComponentBase;
import org.springframework.stereotype.Component;

import java.util.*;

@Internal
@Component("flowui_FilterSupport")
public class FilterSupport {

    protected final Actions actions;

    public FilterSupport(Actions actions) {
        this.actions = actions;
    }

    public List<FilterAction> getDefaultFilterActions(Filter filter) {
        List<FilterAction> filterActions = new ArrayList<>();
        for (Class<? extends FilterAction> actionClass : getDefaultFilterActionClasses()) {
            filterActions.add(createFilterAction(actionClass, filter));
        }
        return filterActions;
    }

    public Map<Configuration, Boolean> getConfigurationsMap(Filter filter) {
        return Collections.emptyMap();
    }

    /*public Configuration saveCurrentFilterConfiguration(Configuration configuration,
                                                        boolean isNewConfiguration,
                                                        LogicalFilterComponent rootFilterComponent,
                                                        ScreenFragment configurationFragment) {
        String id = "";
        String name = "";
        if (configurationFragment instanceof FilterConfigurationModelFragment) {
            id = ((FilterConfigurationModelFragment) configurationFragment).getConfigurationId();
            name = ((FilterConfigurationModelFragment) configurationFragment).getConfigurationName();
        }

        return initFilterConfiguration(id, name, configuration, isNewConfiguration, rootFilterComponent);
    }*/

    public void removeCurrentFilterConfiguration(Filter filter) {
        filter.removeConfiguration(filter.getCurrentConfiguration());
    }

    /*public ScreenFragment createFilterConfigurationFragment(FrameOwner owner,
                                                            boolean isNewConfiguration,
                                                            Filter.Configuration currentConfiguration) {
        Fragments fragments = UiControllerUtils.getScreenContext(owner).getFragments();
        FilterConfigurationModelFragment fragment = fragments.create(owner, FilterConfigurationModelFragment.class);
        initFilterConfigurationFragment(fragment, isNewConfiguration, currentConfiguration);
        return fragment;
    }*/

    public Map<String, Object> initConfigurationValuesMap(Configuration configuration) {
        Map<String, Object> valuesMap = new HashMap<>();
        LogicalFilterComponent rootLogicalComponent = configuration.getRootLogicalFilterComponent();
        for (FilterComponent filterComponent : rootLogicalComponent.getFilterComponents()) {
            if (filterComponent instanceof SingleFilterComponentBase) {
                String parameterName = ((SingleFilterComponentBase<?>) filterComponent).getParameterName();
                valuesMap.put(parameterName, ((SingleFilterComponentBase<?>) filterComponent).getValue());
                ((SingleFilterComponentBase) filterComponent).setValue(configuration.getFilterComponentDefaultValue(parameterName));
            }
        }

        return valuesMap;
    }

    public void resetConfigurationValuesMap(Configuration configuration, Map<String, Object> valuesMap) {
        LogicalFilterComponent rootLogicalComponent = configuration.getRootLogicalFilterComponent();
        for (FilterComponent filterComponent : rootLogicalComponent.getFilterComponents()) {
            if (filterComponent instanceof SingleFilterComponent) {
                ((SingleFilterComponentBase) filterComponent).setValue(
                        valuesMap.get(((SingleFilterComponent<?>) filterComponent).getParameterName()));
            }
        }
    }

    public void refreshConfigurationValuesMap(Configuration configuration, Map<String, Object> valuesMap) {
        LogicalFilterComponent rootLogicalComponent = configuration.getRootLogicalFilterComponent();
        for (FilterComponent filterComponent : rootLogicalComponent.getFilterComponents()) {
            if (filterComponent instanceof SingleFilterComponentBase) {
                String parameterName = ((SingleFilterComponentBase<?>) filterComponent).getParameterName();
                Object value = valuesMap.get(parameterName);
                Object defaultValue = configuration.getFilterComponentDefaultValue(parameterName);

                if (value == null && defaultValue != null) {
                    ((SingleFilterComponentBase) filterComponent).setValue(defaultValue);
                } else {
                    try {
                        ((SingleFilterComponentBase) filterComponent).setValue(value);
                    } catch (ClassCastException e) {
                        ((SingleFilterComponentBase) filterComponent).setValue(defaultValue);
                    }
                }
            }
        }
    }

    public void refreshConfigurationDefaultValues(Configuration configuration) {
        configuration.resetAllDefaultValues();
        LogicalFilterComponent rootLogicalComponent = configuration.getRootLogicalFilterComponent();
        for (FilterComponent filterComponent : rootLogicalComponent.getFilterComponents()) {
            if (filterComponent instanceof SingleFilterComponentBase) {
                configuration.setFilterComponentDefaultValue(
                        ((SingleFilterComponentBase<?>) filterComponent).getParameterName(),
                        ((SingleFilterComponentBase<?>) filterComponent).getValue());
            }
        }
    }

    protected Configuration initFilterConfiguration(String id,
                                                    String name,
                                                    Configuration existedConfiguration,
                                                    boolean isNewConfiguration,
                                                    LogicalFilterComponent rootFilterComponent) {
        Configuration resultConfiguration;
        if (isNewConfiguration) {
            resultConfiguration = new RunTimeConfiguration(id, rootFilterComponent, existedConfiguration.getOwner());
        } else if (!existedConfiguration.getId().equals(id)) {
            Filter owner = existedConfiguration.getOwner();
            resultConfiguration = new RunTimeConfiguration(id, rootFilterComponent, owner);
            owner.removeConfiguration(existedConfiguration);
        } else {
            resultConfiguration = existedConfiguration;
            resultConfiguration.setRootLogicalFilterComponent(rootFilterComponent);
        }
        resultConfiguration.setName(name);

        return resultConfiguration;
    }

    /*protected void initFilterConfigurationFragment(ScreenFragment fragment,
                                                   boolean isNewConfiguration,
                                                   Filter.Configuration currentConfiguration) {
        if (fragment instanceof FilterConfigurationModelFragment) {
            if (!isNewConfiguration) {
                ((FilterConfigurationModelFragment) fragment).setConfigurationId(currentConfiguration.getId());
            }

            ((FilterConfigurationModelFragment) fragment).setConfigurationName(currentConfiguration.getName());
        }
    }*/

    protected Set<Class<? extends FilterAction>> getDefaultFilterActionClasses() {
        return ImmutableSet.of(
//                FilterEditAction.class,
//                FilterCopyAction.class,
//                FilterClearValuesAction.class
        );
    }

    protected FilterAction createFilterAction(Class<? extends FilterAction> filterActionClass,
                                              Filter filter) {
        FilterAction filterAction = actions.create(filterActionClass);
        filterAction.setTarget(filter);
        return filterAction;
    }
}
