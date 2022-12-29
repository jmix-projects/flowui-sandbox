package com.company.demo.view.user;

import com.company.demo.entity.User;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.editor.EditorEvent;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.component.checkbox.JmixCheckbox;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.grid.editor.DataGridEditor;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Consumer;

@Route(value = "users", layout = MainView.class)
@ViewController("User.list")
@ViewDescriptor("user-list-view.xml")
@LookupComponent("usersTable")
@DialogMode(width = "50em", height = "37.5em")
public class UserListView extends StandardListView<User> {

    @ViewComponent
    private DataGrid<User> usersTable;
    @ViewComponent
    private JmixCheckbox bufferedCheck;

    @ViewComponent
    private CollectionContainer<User> usersDc;

    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private Notifications notifications;

    @Subscribe
    public void onReady(ReadyEvent event) {
        /*usersTable.addColumn(LitRenderer.<User>of("<b>${item.firstName}</b>")
                .withProperty("firstName", User::getFirstName)
        ).setHeader("Lit First Name");*/


        DataGridEditor<User> editor = usersTable.getEditor();
        bufferedCheck.setValue(editor.isBuffered());
        bufferedCheck.addValueChangeListener(__ -> editor.setBuffered(bufferedCheck.getValue()));

        editor.setColumnEditorComponent("timeZoneId", generationContext -> {
            //noinspection unchecked
            JmixComboBox<String> timeZoneField = uiComponents.create(JmixComboBox.class);
            timeZoneField.setItems(List.of(TimeZone.getAvailableIDs()));
            timeZoneField.setValueSource(generationContext.getValueSourceProvider().getValueSource("timeZoneId"));
            timeZoneField.setWidthFull();
            timeZoneField.setClearButtonVisible(true);
            timeZoneField.setRequired(true);
            //noinspection unchecked,rawtypes
            timeZoneField.setStatusChangeHandler(((Consumer) generationContext.getStatusHandler()));

            return timeZoneField;
        });


        editor.setValidationErrorsHandler(validationErrors ->
                notifications.create("Validation",
                                ViewValidation.getValidationErrorsMessage(validationErrors))
                        .withPosition(Notification.Position.BOTTOM_END)
                        .withThemeVariant(NotificationVariant.LUMO_ERROR)
                        .show());





        editor.addOpenListener(this::onEditorOpen);
        editor.addCloseListener(this::onEditorClose);
        editor.addSaveListener(this::onEditorSave);
        editor.addCancelListener(this::onEditorCancel);
    }

    private void showEditorEventNotification(EditorEvent<User> event) {
        notifications.create(event.getClass().getSimpleName())
                .withPosition(Notification.Position.BOTTOM_END)
                .show();
    }

    private void onEditorOpen(EditorEvent<User> event) {
        showEditorEventNotification(event);
    }

    private void onEditorClose(EditorEvent<User> event) {
        showEditorEventNotification(event);
    }

    private void onEditorSave(EditorEvent<User> event) {
        showEditorEventNotification(event);
    }

    private void onEditorCancel(EditorEvent<User> event) {
        showEditorEventNotification(event);
    }

    @Subscribe(id = "usersDc", target = Target.DATA_CONTAINER)
    public void onUsersDcCollectionChange(CollectionContainer.CollectionChangeEvent<User> event) {
        notifications.create("CollectionChangeEvent", "Type: " + event.getChangeType())
                .withPosition(Notification.Position.TOP_END)
                .show();
    }

    @Subscribe(id = "usersDc", target = Target.DATA_CONTAINER)
    public void onUsersDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<User> event) {
        notifications.create("ItemPropertyChangeEvent",
                        "Property: " + event.getProperty() +
                                "\nValue: " + event.getValue()
                )
                .withPosition(Notification.Position.TOP_END)
                .withType(Notifications.Type.SUCCESS)
                .show();
    }

    @Subscribe("testBtn")
    public void onTestBtnClick(ClickEvent<Button> event) {
        User item = usersDc.getItem(UUID.fromString("2dd60efa-f3a5-6ee6-30d9-78e09db658a3"));
        item.setFirstName("GLEB " + RandomStringUtils.randomAlphabetic(2));
        item.setLastName("GORELOV " + RandomStringUtils.randomAlphabetic(2));
    }


    /*timeZoneField.setStatusHandler(context -> {
        if (Strings.isNullOrEmpty(context.getDescription())) {
            return;
        }

        notifications.create("timeZoneField", Strings.nullToEmpty(context.getDescription()))
                .withThemeVariant(NotificationVariant.LUMO_ERROR)
                .show();
    });*/
}