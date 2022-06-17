package com.company.demo.view.user;

import com.company.demo.entity.User;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.screen.*;

@Route(value = UserView.ROUTE, layout = MainView.class)
@UiController("User.browse")
@UiDescriptor("user-view.xml")
@LookupComponent("usersTable")
@DialogMode(width = "800px", height = "600px")
public class UserView extends StandardLookup<User> {

    public static final String ROUTE = "users";
}