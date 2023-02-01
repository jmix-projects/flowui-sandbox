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

package com.company.demo.entity.filter;

import io.jmix.core.entity.annotation.SystemLevel;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import io.jmix.flowui.component.SupportsLabelPosition.LabelPosition;

import javax.annotation.Nullable;
import java.util.Objects;

@JmixEntity(name = "flowui_AbstractSingleFilterCondition")
@SystemLevel
public abstract class AbstractSingleFilterCondition extends FilterCondition {

    private static final long serialVersionUID = 3911163960016094584L;

    @JmixProperty
    protected String labelPosition = LabelPosition.ASIDE.name();

    @JmixProperty
    protected FilterValueComponent valueComponent;

    @JmixProperty
    protected Boolean required = false;

    public LabelPosition getLabelPosition() {
        return fromId(labelPosition);
    }

    public void setLabelPosition(LabelPosition labelPosition) {
        this.labelPosition = labelPosition != null ? labelPosition.name() : null;
    }

    public FilterValueComponent getValueComponent() {
        return valueComponent;
    }

    public void setValueComponent(FilterValueComponent valueComponent) {
        this.valueComponent = valueComponent;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Nullable
    protected static LabelPosition fromId(String id) {
        for (LabelPosition labelPosition : LabelPosition.values()) {
            if (Objects.equals(id, labelPosition.name())) {
                return labelPosition;
            }
        }
        return null;
    }
}
