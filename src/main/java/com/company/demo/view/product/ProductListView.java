package com.company.demo.view.product;

import com.company.demo.entity.Product;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.SaveContext;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.facet.QueryParametersFacet;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Route(value = "products", layout = MainView.class)
@ViewController("Product.list")
@ViewDescriptor("product-list-view.xml")
@LookupComponent("productsTable")
@DialogMode(width = "50em", height = "37.5em")
public class ProductListView extends StandardListView<Product> {

    @Autowired
    private Notifications notifications;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private DialogWindows dialogWindows;

    @Subscribe("queryParameters.paginationParams")
    public void onPaginationQueryParametersChanged(QueryParametersFacet.QueryParametersChangeEvent event) {
        notifications.create("QueryParametersChangeEvent")
                .show();
    }

    @Subscribe("dialogBtn")
    public void onDialogBtnClick(ClickEvent<Button> event) {
        dialogWindows.view(this, ProductListView.class)
                .open();
    }

    @Subscribe("generateDataBtn")
    public void onGenerateDataBtnClick(ClickEvent<Button> event) {
        SaveContext saveContext = new SaveContext();

        for (int i = 0; i < 300; i++) {
            Product product = dataManager.create(Product.class);
            product.setName("Product " + i);
            product.setPrice(BigDecimal.valueOf(i));

            saveContext.saving(product);
        }

        dataManager.save(saveContext);
        getViewData().loadAll();
        notifications.create("Created")
                .withType(Notifications.Type.SUCCESS)
                .show();
    }
}