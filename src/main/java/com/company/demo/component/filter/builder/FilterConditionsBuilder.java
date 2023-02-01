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

package com.company.demo.component.filter.builder;

import com.company.demo.component.filter.Filter;
import com.company.demo.entity.filter.FilterCondition;
import io.jmix.core.annotation.Internal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Internal
@Component("flowui_FilterConditionsBuilder")
public class FilterConditionsBuilder {

    protected List<ConditionBuilder> conditionBuilders;

    @Autowired(required = false)
    public void setConditionBuilders(List<ConditionBuilder> conditionBuilders) {
        this.conditionBuilders = conditionBuilders;
    }

    public List<FilterCondition> buildConditions(Filter filter) {
        List<FilterCondition> conditions = new ArrayList<>();

        if (conditionBuilders != null) {
            for (ConditionBuilder conditionBuilder : conditionBuilders) {
                conditions.addAll(conditionBuilder.build(filter));
            }
        }

        return conditions;
    }
}
