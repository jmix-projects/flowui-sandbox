package com.company.demo.accesscontext;

import io.jmix.core.accesscontext.SpecificOperationAccessContext;

public class FlowuiFilterModifyConfigurationContext extends SpecificOperationAccessContext {

    public static final String NAME = "flowui.filter.modifyConfiguration";

    public FlowuiFilterModifyConfigurationContext() {
        super(NAME);
    }
}
