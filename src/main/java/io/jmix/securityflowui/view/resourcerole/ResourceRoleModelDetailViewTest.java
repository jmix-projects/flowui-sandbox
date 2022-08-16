/*
 * Copyright 2022 Haulmont.
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

package io.jmix.securityflowui.view.resourcerole;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.LoadContext;
import io.jmix.core.Messages;
import io.jmix.core.Metadata;
import io.jmix.flowui.DialogWindowBuilders;
import io.jmix.flowui.component.checkboxgroup.JmixCheckboxGroup;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.exception.ValidationException;
import io.jmix.flowui.kit.action.Action;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionPropertyContainer;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import io.jmix.flowui.view.navigation.UrlIdSerializer;
import io.jmix.security.model.ResourceRole;
import io.jmix.security.model.RoleSource;
import io.jmix.security.model.SecurityScope;
import io.jmix.security.role.ResourceRoleRepository;
import io.jmix.securitydata.entity.ResourcePolicyEntity;
import io.jmix.securitydata.entity.ResourceRoleEntity;
import io.jmix.securityflowui.model.BaseRoleModel;
import io.jmix.securityflowui.model.ResourcePolicyModel;
import io.jmix.securityflowui.model.ResourceRoleModel;
import io.jmix.securityflowui.model.RoleModelConverter;
import io.jmix.securityflowui.view.resourcepolicy.EntityResourcePolicyModelCreateView;
import io.jmix.securityflowui.view.resourcepolicy.MenuResourcePolicyModelCreateView;
import io.jmix.securityflowui.view.resourcepolicy.MultipleResourcePolicyModelCreateView;
import io.jmix.securityflowui.view.resourcepolicy.ViewResourcePolicyModelCreateView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route(value = "resourcerolemodels-test/:code", layout = DefaultMainViewParent.class)
@UiController("sec_ResourceRoleModel.detail1")
@UiDescriptor("resource-role-model-detail-view-test.xml")
@EditedEntityContainer("roleModelDc")
@PrimaryDetailView(ResourceRoleModel.class)
public class ResourceRoleModelDetailViewTest extends StandardDetailView<ResourceRoleModel> {

    public static final String ROUTE_PARAM_NAME = "code";

    private static final Logger log = LoggerFactory.getLogger(ResourceRoleModelDetailView.class);

    @ComponentId
    private DataGrid<ResourcePolicyModel> resourcePoliciesTable;
    @ComponentId
    private DataGrid<ResourceRoleModel> childRolesTable;
    @ComponentId
    private TypedTextField<String> codeField;
    @ComponentId
    private JmixCheckboxGroup<String> scopesField;
    @ComponentId
    private TypedTextField<String> sourceField;
    @ComponentId
    protected HorizontalLayout resourcePoliciesButtonsPanel;

    @ComponentId
    private InstanceContainer<ResourceRoleModel> roleModelDc;
    @ComponentId
    private CollectionContainer<ResourceRoleModel> childRolesDc;
    @ComponentId
    private CollectionPropertyContainer<ResourcePolicyModel> resourcePoliciesDc;

    @Autowired
    private ResourceRoleRepository roleRepository;
    @Autowired
    private RoleModelConverter roleModelConverter;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private Metadata metadata;
    @Autowired
    private Messages messages;
    @Autowired
    private DialogWindowBuilders dialogBuilders;

    private boolean openedByCreateAction = false;
    private Set<UUID> forRemove = new HashSet<>();

    @Subscribe
    public void onInit(InitEvent event) {
        // we need to set items (i.e. options) before the value is set,
        // otherwise it will be cleared
        initScopesField();
        initCodeField();
    }

    @Subscribe
    public void onInitNewEntity(InitEntityEvent<ResourceRoleModel> event) {
        openedByCreateAction = true;

        codeField.setReadOnly(false);

        ResourceRoleModel entity = event.getEntity();
        entity.setSource(RoleSource.DATABASE);
        entity.setScopes(Sets.newHashSet(SecurityScope.UI));
    }

    @Override
    protected String getRouteParamName() {
        return ROUTE_PARAM_NAME;
    }

    @Override
    protected void initExistingEntity(String serializedEntityCode) {
        String code = UrlIdSerializer.deserializeId(String.class, serializedEntityCode);
        ResourceRole roleByCode = roleRepository.findRoleByCode(code);

        ResourceRoleModel resourceRoleModel = roleModelConverter.createResourceRoleModel(roleByCode);
        roleModelDc.setItem(resourceRoleModel);
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        //non-persistent entities are automatically marked as modified. If isNew is not set, we must remove
        //all entities from dataContext.modifiedInstances collection
        if (!openedByCreateAction) {
            Set<Object> modified = new HashSet<>(getDataContext().getModified());
            for (Object entity : modified) {
                getDataContext().setModified(entity, false);
            }
        }

        boolean isDatabaseSource = isDatabaseSource();
        setupRoleViewMode(isDatabaseSource);

        Action createGraphQLPolicyAction = resourcePoliciesTable.getAction("createGraphQLPolicy");
        if (createGraphQLPolicyAction != null) {
            createGraphQLPolicyAction.setVisible(isGraphQLEnabled());
        }

        initPoliciesMenuBar(isDatabaseSource);
    }

    private void setupRoleViewMode(boolean isDatabaseSource) {
        setReadOnly(!isDatabaseSource);

        Collection<Action> resourcePoliciesActions = resourcePoliciesTable.getActions();
        for (Action action : resourcePoliciesActions) {
            action.setEnabled(isDatabaseSource);
        }

        Collection<Action> childRolesActions = childRolesTable.getActions();
        for (Action action : childRolesActions) {
            action.setEnabled(isDatabaseSource);
        }
    }

    private void initPoliciesMenuBar(boolean enabled) {
        MenuBar policiesMenuBar = new MenuBar();
        policiesMenuBar.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        policiesMenuBar.setEnabled(enabled);

        resourcePoliciesButtonsPanel.addComponentAsFirst(policiesMenuBar);

        MenuItem item = policiesMenuBar.addItem(VaadinIcon.PLUS.create());
        item.add(messages.getMessage("actions.Create"));

        if (!enabled) {
            return;
        }

        SubMenu subMenu = item.getSubMenu();
        getCreatePolicyActions()
                .filter(Action::isVisible)
                .forEach(action ->
                        subMenu.addItem(action.getText(), event ->
                                action.actionPerform(null)));
    }

    @Install(to = "childRolesDl", target = Target.DATA_LOADER)
    private List<ResourceRoleModel> childRolesDlLoadDelegate(LoadContext<ResourceRoleModel> loadContext) {
        ResourceRoleModel editedRoleModel = getEditedEntity();
        if (editedRoleModel.getChildRoles() == null || editedRoleModel.getChildRoles().isEmpty()) {
            return Collections.emptyList();
        }
        List<ResourceRoleModel> childRoleModels = new ArrayList<>();
        for (String code : editedRoleModel.getChildRoles()) {
            ResourceRole child = roleRepository.findRoleByCode(code);
            if (child != null) {
                childRoleModels.add(roleModelConverter.createResourceRoleModel(child));
            } else {
                log.warn("Role {} was not found while collecting child roles for aggregated role {}",
                        editedRoleModel.getCode(), code);
            }
        }
        return childRoleModels;
    }

    protected void initScopesField() {
        scopesField.setItems(Arrays.asList(SecurityScope.UI, SecurityScope.API));
    }

    private void initCodeField() {
        codeField.addValidator(s -> {
            ResourceRoleModel editedEntity = getEditedEntity();
            boolean exist = roleRepository.getAllRoles().stream()
                    .filter(resourceRole -> {
                        if (resourceRole.getCustomProperties().isEmpty()) {
                            return true;
                        }
                        return !resourceRole.getCustomProperties().get("databaseId").equals(editedEntity.getCustomProperties().get("databaseId"));
                    })
                    .anyMatch(resourceRole -> resourceRole.getCode().equals(s));
            if (exist) {
                throw new ValidationException(messages.getMessage("io.jmix.securityflowui.view.resourcerole/uniqueCode"));
            }
        });
    }

    @Subscribe("resourcePoliciesTable.createMenuPolicy")
    public void onResourcePoliciesTableCreateMenuPolicy(ActionPerformedEvent event) {
        dialogBuilders.view(this, MenuResourcePolicyModelCreateView.class)
                .withAfterCloseListener(this::addPoliciesFromMultiplePoliciesView)
                .open();
    }

    @Subscribe("resourcePoliciesTable.createViewPolicy")
    public void onResourcePoliciesTableCreateViewPolicy(ActionPerformedEvent event) {
        dialogBuilders.view(this, ViewResourcePolicyModelCreateView.class)
                .withAfterCloseListener(this::addPoliciesFromMultiplePoliciesView)
                .open();
    }

    @Subscribe("resourcePoliciesTable.createEntityPolicy")
    public void onResourcePoliciesTableCreateEntityPolicy(ActionPerformedEvent event) {
        dialogBuilders.view(this, EntityResourcePolicyModelCreateView.class)
                .withAfterCloseListener(this::addPoliciesFromMultiplePoliciesView)
                .open();
    }

    @Subscribe("resourcePoliciesTable.createEntityAttributePolicy")
    public void onResourcePoliciesTableCreateEntityAttributePolicy(ActionPerformedEvent event) {
        /*dialogBuilders.view(this, EntityAttributeResourcePolicyModelCreateView.class)
                .withAfterCloseListener(this::addPoliciesFromMultiplePoliciesView)
                .open();*/
    }

    private void addPoliciesFromMultiplePoliciesView(
            DialogWindow.AfterCloseEvent<? extends MultipleResourcePolicyModelCreateView> closeEvent) {
        if (closeEvent.closedWith(StandardOutcome.COMMIT)) {
            MultipleResourcePolicyModelCreateView view = closeEvent.getSource().getView();
            for (ResourcePolicyModel resourcePolicyModel : view.getResourcePolicies()) {
                boolean policyExists = resourcePoliciesDc.getItems().stream()
                        .anyMatch(rpm ->
                                resourcePolicyModel.getType().equals(rpm.getType())
                                        && resourcePolicyModel.getAction().equals(rpm.getAction())
                                        && resourcePolicyModel.getResource().equals(rpm.getResource())
                        );

                if (!policyExists) {
                    ResourcePolicyModel mergedResourcePolicyModel = getDataContext().merge(resourcePolicyModel);
                    resourcePoliciesDc.getMutableItems().add(mergedResourcePolicyModel);
                }
            }
        }
    }

    @Subscribe("resourcePoliciesTable.createGraphQLPolicy")
    public void onGraphQLPoliciesTableCreateGraphQLPolicy(ActionPerformedEvent event) {
        Notification.show("createGraphQLPolicy");
        /*screenBuilders.editor(resourcePoliciesTable)
                .withScreenClass(GraphQLResourcePolicyModelEdit.class)
                .newEntity()
                .withInitializer(resourcePolicyModel -> {
                    resourcePolicyModel.setType(ResourcePolicyType.GRAPHQL);
                    resourcePolicyModel.setAction(ResourcePolicy.DEFAULT_ACTION);
                    resourcePolicyModel.setEffect(ResourcePolicyEffect.ALLOW);
                })
                .build()
                .show();*/
    }

    @Subscribe("resourcePoliciesTable.createSpecificPolicy")
    public void onResourcePoliciesTableCreateSpecificPolicy(ActionPerformedEvent event) {
        Notification.show("createSpecificPolicy");
        /*screenBuilders.editor(resourcePoliciesTable)
                .withScreenClass(SpecificResourcePolicyModelEdit.class)
                .newEntity()
                .withInitializer(resourcePolicyModel -> {
                    resourcePolicyModel.setType(ResourcePolicyType.SPECIFIC);
                    resourcePolicyModel.setAction(ResourcePolicy.DEFAULT_ACTION);
                    resourcePolicyModel.setEffect(ResourcePolicyEffect.ALLOW);
                })
                .build()
                .show();*/
    }

    @Subscribe(target = Target.DATA_CONTEXT)
    public void onPreCommit(DataContext.PreCommitEvent event) {
        if (isDatabaseSource()) {
            saveRoleEntityToDatabase(event.getModifiedInstances());
        }
    }

    private void saveRoleEntityToDatabase(Collection<?> modifiedInstances) {
        ResourceRoleModel roleModel = getEditedEntity();
        String roleDatabaseId = roleModel.getCustomProperties().get("databaseId");
        ResourceRoleEntity roleEntity;
        if (!Strings.isNullOrEmpty(roleDatabaseId)) {
            UUID roleEntityId = UUID.fromString(roleDatabaseId);
            roleEntity = dataManager.load(ResourceRoleEntity.class).id(roleEntityId).one();
        } else {
            roleEntity = metadata.create(ResourceRoleEntity.class);
        }
        roleEntity = getDataContext().merge(roleEntity);
        roleEntity.setName(roleModel.getName());
        roleEntity.setCode(roleModel.getCode());
        roleEntity.setDescription(roleModel.getDescription());
        roleEntity.setScopes(roleModel.getScopes());

        boolean roleModelModified = modifiedInstances.stream()
                .anyMatch(entity -> entity instanceof ResourceRoleModel);

        if (roleModelModified) {
            roleEntity = getDataContext().merge(roleEntity);
        }

        List<ResourcePolicyModel> resourcePolicyModels = modifiedInstances.stream()
                .filter(entity -> entity instanceof ResourcePolicyModel)
                .map(entity -> (ResourcePolicyModel) entity)
                //modifiedInstances may contain resource policies from just added child role. We should not analyze them here
                .filter(entity -> resourcePoliciesDc.containsItem(entity.getId()))
                .collect(Collectors.toList());

        for (ResourcePolicyModel policyModel : resourcePolicyModels) {
            String databaseId = policyModel.getCustomProperties().get("databaseId");
            ResourcePolicyEntity policyEntity;
            if (!Strings.isNullOrEmpty(databaseId)) {
                UUID entityId = UUID.fromString(databaseId);
                policyEntity = dataManager.load(ResourcePolicyEntity.class)
                        .id(entityId)
                        .one();
            } else {
                policyEntity = metadata.create(ResourcePolicyEntity.class);
                policyEntity.setRole(roleEntity);
            }
            policyEntity = getDataContext().merge(policyEntity);
            policyEntity.setType(policyModel.getType());
            policyEntity.setResource(policyModel.getResource());
            policyEntity.setAction(policyModel.getAction());
            policyEntity.setEffect(policyModel.getEffect());
            policyEntity.setPolicyGroup(policyModel.getPolicyGroup());
        }

        // TODO: 10.08.2022
        /*for (UUID databaseId : forRemove) {
            dataManager.remove(Id.of(databaseId, ResourcePolicyEntity.class));
        }*/

        Set<String> childRoles = childRolesDc.getItems().stream()
                .map(BaseRoleModel::getCode)
                .collect(Collectors.toSet());

        roleModel.setChildRoles(childRoles);
        roleEntity.setChildRoles(childRoles);
    }

    private Stream<Action> getCreatePolicyActions() {
        return resourcePoliciesTable.getActions()
                .stream()
                .filter(action -> action.getId().contains("Policy"));
    }

    private boolean isDatabaseSource() {
        return RoleSource.DATABASE.equals(getEditedEntity().getSource());
    }

    private boolean isGraphQLEnabled() {
        try {
            // TODO: 11.08.2022 is it applicable?
            Class.forName("io.jmix.graphql.security.GraphQLAuthorizedUrlsProvider");
            return true;
        } catch (ClassNotFoundException ignored) {
        }
        return false;
    }

    private DataContext getDataContext() {
        return getViewData().getDataContext();
    }
}
