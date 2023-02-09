package com.company.demo.action.filter;

import com.company.demo.component.filter.Configuration;
import com.company.demo.component.filter.FilterUtils;
import io.jmix.core.Messages;
import io.jmix.flowui.action.ActionType;
import org.springframework.beans.factory.annotation.Autowired;

@ActionType(FilterResetAction.ID)
public class FilterResetAction extends FilterAction<FilterResetAction> {

    public static final String ID = "filter_reset";

    public FilterResetAction() {
        this(ID);
    }

    public FilterResetAction(String id) {
        super(id);
    }

    @Autowired
    protected void setMessages(Messages messages) {
        this.text = messages.getMessage("actions.Filter.Reset");
    }

    @Override
    public void execute() {
        checkTarget();

        Configuration configuration = target.getEmptyConfiguration();
        configuration.getRootLogicalFilterComponent().removeAll();
        configuration.setModified(false);

        FilterUtils.setCurrentConfiguration(target, configuration, true);

        target.apply();
    }
}
