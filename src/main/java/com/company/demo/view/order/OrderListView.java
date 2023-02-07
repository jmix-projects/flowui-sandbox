package com.company.demo.view.order;

import com.company.demo.component.filter.Filter;
import com.company.demo.component.filter.LogicalFilterComponent;
import com.company.demo.component.groupfilter.GroupFilter;
import com.company.demo.entity.Order;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.component.SupportsLabelPosition;
import io.jmix.flowui.component.propertyfilter.SingleFilterSupport;
import io.jmix.flowui.kit.component.dropdownbutton.DropdownButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "orders", layout = MainView.class)
@ViewController("Order_.list")
@ViewDescriptor("order-list-view.xml")
@LookupComponent("ordersTable")
@DialogMode(width = "50em", height = "37.5em")
public class OrderListView extends StandardListView<Order> {
//    @ViewComponent
//    private HorizontalLayout filterBox;

    @ViewComponent
    private CollectionContainer<Order> ordersDc;
    @ViewComponent
    private CollectionLoader<Order> ordersDl;

    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private SingleFilterSupport singleFilterSupport;

    @ViewComponent
    private DropdownButton db;
    @Autowired
    private Notifications notifications;
    private GroupFilter groupFilter;

    @Subscribe
    public void onInit(InitEvent event) {
        /*JpqlFilter<?> jpqlFilter = uiComponents.create(JpqlFilter.class);
        jpqlFilter.setLabel("Number contains");
        jpqlFilter.setParameterName("number");
        jpqlFilter.setParameterClass(String.class);
        jpqlFilter.setCondition("{E}.number like :number", null);
        jpqlFilter.setDataLoader(ordersDl);
        jpqlFilter.setValueComponent(generateValueComponent(jpqlFilter.getParameterClass()));

        filterBox.add(jpqlFilter);*/


        Filter filter = uiComponents.create(Filter.class);
        filter.setId("filter");
        filter.setDataLoader(ordersDl);
        filter.loadConfigurationsAndApplyDefault();

        getContent().addComponentAsFirst(filter);


        ordersDc.addCollectionChangeListener(changeEvent ->
                notifications.create("CollectionChangeListener: " + changeEvent.getChangeType())
                        .withPosition(Notification.Position.BOTTOM_END)
                        .show());

        groupFilter = ((GroupFilter) filter.getCurrentConfiguration().getRootLogicalFilterComponent());

//        filter.setLabelPosition(SupportsLabelPosition.LabelPosition.TOP);
    }

    protected HasValueAndElement generateValueComponent(Class<?> paramType) {
        MetaClass metaClass = ordersDl.getContainer().getEntityMetaClass();
        return singleFilterSupport.generateValueComponent(metaClass, false, paramType);
    }

    @Subscribe("btn")
    public void onBtnClick(ClickEvent<Button> event) {
        /*Element element = db.getElement();
        element.executeJs("setTimeout(function(){$0.focus()},0)", element);*/
        groupFilter.setSummaryText("TEST SUMMARY " + RandomStringUtils.randomAlphanumeric(2));
    }


}
