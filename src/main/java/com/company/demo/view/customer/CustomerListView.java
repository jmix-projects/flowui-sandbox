package com.company.demo.view.customer;

import com.company.demo.entity.Customer;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "customers", layout = MainView.class)
@ViewController("Customer.list")
@ViewDescriptor("customer-list-view.xml")
@LookupComponent("customersTable")
@DialogMode(width = "50em", height = "37.5em")
public class CustomerListView extends StandardListView<Customer> {
}