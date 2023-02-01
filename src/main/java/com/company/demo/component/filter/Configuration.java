package com.company.demo.component.filter;

import com.company.demo.component.filter.configuration.RunTimeConfiguration;
import io.jmix.core.querycondition.LogicalCondition;
import io.jmix.flowui.component.filer.FilterComponent;

import javax.annotation.Nullable;
import java.util.EventObject;

/**
 * A configuration is a set of filter components.
 */
public interface Configuration extends Comparable<Configuration> {

    /**
     * @return a {@link Filter} owning the configuration
     */
    Filter getOwner();

    /**
     * @return a configuration id
     */
    String getId();

    /**
     * @return a configuration name
     */
    @Nullable
    String getName();

    /**
     * Sets the name of configuration. This method is only available for
     * the {@link RunTimeConfiguration}.
     *
     * @param name a configuration name
     * @see RunTimeConfiguration
     */
    void setName(@Nullable String name);

    /**
     * @return a root element of configuration
     * @see LogicalFilterComponent
     */
    LogicalFilterComponent getRootLogicalFilterComponent();

    /**
     * Sets the root element of configuration. This method is only available for
     * the {@link RunTimeConfiguration}.
     *
     * @param rootLogicalFilterComponent a root element of configuration
     * @see LogicalFilterComponent
     * @see RunTimeConfiguration
     */
    void setRootLogicalFilterComponent(LogicalFilterComponent rootLogicalFilterComponent);

    /**
     * @return a {@link LogicalCondition} related to the configuration
     */
    LogicalCondition getQueryCondition();

    /**
     * @return true if the configuration is modified
     */
    boolean isModified();

    /**
     * Sets whether configuration is modified. If a filter component is modified,
     * then a remove button appears next to it.
     *
     * @param modified whether configuration is modified.
     */
    void setModified(boolean modified);

    /**
     * Returns whether the {@link FilterComponent} of configuration is modified.
     * If a filter component is modified, then a remove button appears next to it.
     *
     * @param filterComponent the filter component to check
     * @return whether the filter component of configuration is modified
     */
    boolean isFilterComponentModified(FilterComponent filterComponent);

    /**
     * Sets whether the {@link FilterComponent} of configuration is modified.
     * If a filter component is modified, then a remove button appears next to it.
     *
     * @param filterComponent a filter component
     * @param modified        whether the filter component of configuration is modified
     */
    void setFilterComponentModified(FilterComponent filterComponent, boolean modified);

    /**
     * Sets a default value of {@link FilterComponent} for the configuration by the parameter name.
     * This allows the default values to be saved and displayed in the configuration editor.
     *
     * @param parameterName a parameter name of filter component
     * @param defaultValue  a default value
     */
    void setFilterComponentDefaultValue(String parameterName, @Nullable Object defaultValue);

    /**
     * Resets a default value of {@link FilterComponent}. The default value for the filter
     * component becomes null.
     *
     * @param parameterName a parameter name of filter component
     */
    void resetFilterComponentDefaultValue(String parameterName);

    /**
     * Returns a default value of {@link FilterComponent} by parameter name.
     *
     * @param parameterName a parameter name of filter component
     * @return a default value of filter component by parameter name
     */
    @Nullable
    Object getFilterComponentDefaultValue(String parameterName);

    /**
     * Sets null as the default value for all configuration filter components.
     */
    void resetAllDefaultValues();
}
