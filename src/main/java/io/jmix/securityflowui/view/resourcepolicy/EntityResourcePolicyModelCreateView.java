package io.jmix.securityflowui.view.resourcepolicy;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import io.jmix.core.Metadata;
import io.jmix.flowui.component.checkbox.JmixCheckbox;
import io.jmix.flowui.component.checkboxgroup.JmixCheckboxGroup;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.component.validation.ValidationErrors;
import io.jmix.flowui.data.items.EnumDataProvider;
import io.jmix.flowui.view.*;
import io.jmix.security.model.ResourcePolicyEffect;
import io.jmix.security.model.ResourcePolicyType;
import io.jmix.securityflowui.model.DefaultResourcePolicyGroupResolver;
import io.jmix.securityflowui.model.ResourcePolicyModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@UiController("sec_EntityResourcePolicyModel.create")
@UiDescriptor("entity-resource-policy-model-create-view.xml")
@DialogMode(width = "32em")
public class EntityResourcePolicyModelCreateView extends MultipleResourcePolicyModelCreateView {

    private static final Set<Actions> ALL_ACTION = Sets.newHashSet(Actions.class.getEnumConstants());

    @ComponentId
    private JmixComboBox<String> entityField;
    @ComponentId
    private TypedTextField<String> policyGroupField;
    @ComponentId
    private JmixCheckbox allActions;
    @ComponentId
    private JmixCheckboxGroup<Actions> actionsGroup;

    @Autowired
    private ResourcePolicyViewUtils resourcePolicyEditorUtils;
    @Autowired
    private DefaultResourcePolicyGroupResolver resourcePolicyGroupResolver;

    @Autowired
    private Metadata metadata;
    @Autowired
    private MessageBundle messageBundle;

    @Subscribe
    public void onInit(InitEvent event) {
        Map<String, String> optionsMap = resourcePolicyEditorUtils.getEntityOptionsMap();

        entityField.setItems(optionsMap.keySet());
        entityField.setItemLabelGenerator(optionsMap::get);
        entityField.addValueChangeListener(this::onEntityFieldValueChange);

        actionsGroup.setItems(new EnumDataProvider<>(Actions.class));
        actionsGroup.addValueChangeListener(this::onActionGroupValueChange);

        allActions.addValueChangeListener(this::onAllActionValueChange);
    }

    private void onEntityFieldValueChange(AbstractField.ComponentValueChangeEvent<ComboBox<String>, String> event) {
        String entityName = event.getValue();
        String policyGroup = resourcePolicyGroupResolver.resolvePolicyGroup(ResourcePolicyType.ENTITY, entityName);
        if (policyGroup != null) {
            policyGroupField.setValue(policyGroup);
        } else {
            policyGroupField.clear();
        }
    }

    private void onActionGroupValueChange(AbstractField.ComponentValueChangeEvent<CheckboxGroup<Actions>, Set<Actions>> event) {
        long size = actionsGroup.getListDataView().getItems().count();

        if (event.getValue().size() == size) {
            allActions.setValue(true);
            allActions.setIndeterminate(false);
        } else if (event.getValue().isEmpty()) {
            allActions.setValue(false);
            allActions.setIndeterminate(false);
        } else {
            allActions.setIndeterminate(true);
        }
    }

    private void onAllActionValueChange(AbstractField.ComponentValueChangeEvent<Checkbox, Boolean> event) {
        if (Boolean.TRUE.equals(event.getValue())) {
            actionsGroup.setValue(ALL_ACTION);
        } else {
            actionsGroup.deselectAll();
        }
    }

    @Override
    protected ValidationErrors validateView() {
        ValidationErrors validationErrors = new ValidationErrors();

        if (Strings.isNullOrEmpty(entityField.getValue())) {
            validationErrors.add(entityField,
                    messageBundle.getMessage("entityResourcePolicyModelCreateView.error.selectEntity"));
        }

        if (getPolicyActions().isEmpty()) {
            validationErrors.add(entityField,
                    messageBundle.getMessage("entityResourcePolicyModelCreateView.error.selectActions"));
        }

        return validationErrors;
    }

    @Override
    public List<ResourcePolicyModel> getResourcePolicies() {
        List<ResourcePolicyModel> policies = new ArrayList<>();
        String entityName = entityField.getValue();
        for (String action : getPolicyActions()) {
            ResourcePolicyModel policy = metadata.create(ResourcePolicyModel.class);
            policy.setType(ResourcePolicyType.ENTITY);
            policy.setResource(entityName);
            policy.setPolicyGroup(policyGroupField.getValue());
            policy.setAction(action);
            policy.setEffect(ResourcePolicyEffect.ALLOW);
            policies.add(policy);
        }

        return policies;
    }

    private Set<String> getPolicyActions() {
        return actionsGroup.getValue().stream()
                .map(action ->
                        action.name().toLowerCase())
                .collect(Collectors.toSet());
    }

    enum Actions {
        CREATE,
        READ,
        UPDATE,
        DELETE
    }
}
