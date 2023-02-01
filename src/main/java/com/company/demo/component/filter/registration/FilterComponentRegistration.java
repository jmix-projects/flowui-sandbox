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

package com.company.demo.component.filter.registration;

import com.company.demo.component.filter.converter.FilterConverter;
import com.company.demo.entity.filter.FilterCondition;
import io.jmix.flowui.component.filer.FilterComponent;

import javax.annotation.Nullable;

/**
 * Registers a UI filter component in the framework. Registered components can be
 * used inside a {@link Filter.Configuration}.
 * <p>
 * For instance:
 * <pre>
 * &#64;Configuration
 * public class FilterComponentConfiguration {
 *
 *      &#64;Bean
 *      public FilterComponentRegistration registerPropertyFilterComponent() {
 *          return FilterComponentRegistrationBuilder.create(PropertyFilter.class,
 *          PropertyFilterCondition.class,
 *          PropertyFilterConverter.class)
 *          .build();
 *      }
 * }
 * </pre>
 *
 * @see FilterComponentRegistrationBuilder
 * @see FilterComponentRegistrationImpl
 * @see FilterComponents
 */
public interface FilterComponentRegistration {

    /**
     * Returns a class of the UI filter component.
     *
     * @return a class of the UI filter component
     */
    Class<? extends FilterComponent> getComponentClass();

    /**
     * Returns a class of non-persistent entity that stores the state of the UI filter component.
     * The model class is used to save filter component state in DB and is used to display and
     * change the state of the filter component at runtime.
     *
     * @return a model class
     */
    Class<? extends FilterCondition> getModelClass();

    /**
     * Returns a converter class. The converter is used to convert between a UI filter component
     * and model classes.
     *
     * @return a converter class
     */
    Class<? extends FilterConverter<? extends FilterComponent, ? extends FilterCondition>> getConverterClass();

    /**
     * Returns an id of the model edit screen.
     *
     * @return an id of the model edit screen.
     */
    @Nullable
    String getDetailViewId();
}
