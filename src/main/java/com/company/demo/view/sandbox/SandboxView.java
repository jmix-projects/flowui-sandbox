package com.company.demo.view.sandbox;

import com.company.demo.entity.Order;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.data.ValueSource;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Route(value = "sandbox", layout = MainView.class)
@ViewController("sandbox")
@ViewDescriptor("sandbox-view.xml")
//@AnonymousAllowed
public class SandboxView extends StandardView {
    @ViewComponent
    private TypedTextField<BigDecimal> amountField;

    @ViewComponent
    private InstanceContainer<Order> orderDc;

    @Autowired
    private Notifications notifications;
    @Autowired
    private DialogWindows dialogWindows;

//    private BufferedContainerValueSource<Order, BigDecimal> valueSource;

    @Subscribe
    public void onInit(InitEvent event) {
        Order order = getViewData().getDataContext().create(Order.class);
        orderDc.setItem(order);

//        valueSource = new BufferedContainerValueSource<>(orderDc, "amount");
//        valueSource.addValueChangeListener(this::valueSourceValueChanged);

//        amountField.setValueSource(valueSource);
    }

    private void valueSourceValueChanged(ValueSource.ValueChangeEvent<BigDecimal> event) {
        /*notifications.create("ValueSource.ValueChangeEvent", "Event value: " + event.getValue() +
                        "\nSource value: " + valueSource.getValue())
                .withPosition(Position.TOP_END)
                .withThemeVariant(NotificationVariant.LUMO_PRIMARY)
                .show();*/
    }

    @Subscribe(id = "orderDc", target = Target.DATA_CONTAINER)
    public void onOrderDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<Order> event) {
        notifications.create("ItemPropertyChangeEvent", event.getValue() + "")
                .withPosition(Position.TOP_END)
                .show();
    }

    @Subscribe("amountField")
    public void onAmountFieldComponentValueChange(ComponentValueChangeEvent<TextField, BigDecimal> event) {
        notifications.create("ComponentValueChangeEvent", event.getValue() + "")
                .withPosition(Position.TOP_END)
                .withThemeVariant(NotificationVariant.LUMO_CONTRAST)
                .show();
    }

    @Subscribe("setEntityValueBtn")
    public void onSetEntityValueBtnClick(ClickEvent<Button> event) {
        notifications.create("Set new value to entity")
                .withPosition(Position.BOTTOM_END)
                .show();

        orderDc.getItem().setAmount(BigDecimal.valueOf(RandomUtils.nextInt(0, 100)));
    }

    @Subscribe("setSourceValueBtn")
    public void onSetSourceValueBtnClick(ClickEvent<Button> event) {
        notifications.create("Set new value to value source")
                .withPosition(Position.BOTTOM_END)
                .show();

//        valueSource.setValue(BigDecimal.valueOf(RandomUtils.nextInt(0, 100)));
    }

    @Subscribe("writeBtn")
    public void onWriteBtnClick(ClickEvent<Button> event) {
        notifications.create("Write")
                .withPosition(Position.BOTTOM_END)
                .show();

//        valueSource.write();
    }

    @Subscribe("discardBtn")
    public void onDiscardBtnClick(ClickEvent<Button> event) {
        notifications.create("Discard")
                .withPosition(Position.BOTTOM_END)
                .show();

//        valueSource.discard();
    }

    @Subscribe("modifiedBtn")
    public void onModifiedBtnClick(ClickEvent<Button> event) {
//        notifications.create("Modified: " + valueSource.isModified()).show();
    }
}
