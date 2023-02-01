package com.company.demo.view.sandbox;

import com.company.demo.view.main.MainView;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "sandbox", layout = MainView.class)
@ViewController("sandbox")
@ViewDescriptor("sandbox-view.xml")
//@AnonymousAllowed
public class SandboxView extends StandardView {

    @Autowired
    private UiComponents uiComponents;

    @Subscribe
    public void onInit(InitEvent event) {
        TextField textField = new TextField();
        textField.setLabel("Vaadin original");
        textField.setInvalid(true);

        TypedTextField<String> typedTextField = uiComponents.create(TypedTextField.class);
        typedTextField.setLabel("Jmix improved");
        typedTextField.setInvalid(true);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems("One", "Two", "Three");
        comboBox.setLabel("Vaadin original");
        comboBox.setInvalid(true);

        JmixComboBox<String> jmixComboBox = uiComponents.create(JmixComboBox.class);
        jmixComboBox.setItems("One", "Two", "Three");
        jmixComboBox.setLabel("Jmix improved");
        jmixComboBox.setInvalid(true);

        getContent().add(textField, typedTextField, comboBox, jmixComboBox);
    }
}
