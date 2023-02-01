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

import com.company.demo.entity.filter.HeaderFilterCondition;
import io.jmix.core.Metadata;

public abstract class AbstractConditionBuilder implements ConditionBuilder {

    protected final Metadata metadata;

    public AbstractConditionBuilder(Metadata metadata) {
        this.metadata = metadata;
    }

    protected HeaderFilterCondition createHeaderFilterCondition(String label) {
        HeaderFilterCondition headerFilterCondition = metadata.create(HeaderFilterCondition.class);
        headerFilterCondition.setLocalizedLabel(label);
        return headerFilterCondition;
    }
}
