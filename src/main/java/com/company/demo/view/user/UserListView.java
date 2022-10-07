package com.company.demo.view.user;

import com.company.demo.entity.User;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "users", layout = MainView.class)
@ViewController("User.list")
@ViewDescriptor("user-list-view.xml")
@LookupComponent("usersTable")
@DialogMode(width = "50em", height = "37.5em")
public class UserListView extends StandardListView<User> {

    @ViewComponent
    private DataGrid<User> usersTable;

    @Autowired
    private Notifications notifications;

    @Subscribe
    public void onInit(InitEvent event) {
        usersTable.addSelectionListener(this::onUserTableSelectionChange);
    }

    //    @Subscribe("usersTable")
    public void onUserTableSelectionChange(SelectionEvent<Grid<User>, User> event) {
        notifications.create("SelectionEvent", event.getFirstSelectedItem().map(User::getUsername).orElse("Empty"))
                .withPosition(Position.BOTTOM_END)
                .show();

        notifications.create("Single Select", usersTable.getSingleSelectedItem() != null
                        ? usersTable.getSingleSelectedItem().getUsername()
                        : "Empty")
                .withPosition(Position.BOTTOM_START)
                .show();
    }

    @Subscribe(id = "usersDc", target = Target.DATA_CONTAINER)
    public void onUsersDcItemChange(InstanceContainer.ItemChangeEvent<User> event) {
        notifications.create("ItemChangeEvent", event.getItem() != null ? event.getItem().getUsername() : "Empty")
                .withPosition(Position.BOTTOM_CENTER)
                .show();
    }
}