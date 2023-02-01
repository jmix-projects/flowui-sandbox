package com.company.demo.component.filter.converter;

import com.company.demo.entity.filter.FilterCondition;
import io.jmix.flowui.component.filer.FilterComponent;

public interface FilterConverter<C extends FilterComponent, M extends FilterCondition> {

    /**
     * Returns an instance of the UI filter component whose state was retrieved from the model.
     *
     * @param model a model instance
     * @return an instance of the UI filter component
     */
    C convertToComponent(M model);

    /**
     * Returns an instance of model whose state was retrieved from the UI filter component.
     *
     * @param component a filter component instance
     * @return a model instance
     */
    M convertToModel(C component);
}
