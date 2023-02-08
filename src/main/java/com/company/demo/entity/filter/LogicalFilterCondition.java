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

import com.company.demo.component.filter.LogicalFilterComponent.Operation;
import io.jmix.core.entity.annotation.SystemLevel;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JmixEntity(name = "flowui_LogicalFilterCondition")
@SystemLevel
public abstract class LogicalFilterCondition extends FilterCondition {

    private static final long serialVersionUID = 5915361115976376590L;

    @JmixProperty
    @InstanceName
    protected String operation;

    @JmixProperty
    protected Boolean operationTextVisible = true;

    @JmixProperty
    protected List<FilterCondition> ownFilterConditions = new ArrayList<>();

    public Operation getOperation() {
        return fromId(operation);
    }

    public void setOperation(Operation operation) {
        this.operation = operation != null ? operation.name() : null;
    }

    public Boolean getOperationTextVisible() {
        return operationTextVisible;
    }

    public void setOperationTextVisible(Boolean operationTextVisible) {
        this.operationTextVisible = operationTextVisible;
    }

    public List<FilterCondition> getOwnFilterConditions() {
        return ownFilterConditions;
    }

    public void setOwnFilterConditions(List<FilterCondition> ownFilterConditions) {
        this.ownFilterConditions = ownFilterConditions;
    }

    @Nullable
    public static Operation fromId(String id) {
        for (Operation operation : Operation.values()) {
            if (Objects.equals(id, operation.name())) {
                return operation;
            }
        }
        return null;
    }
}
