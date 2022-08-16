package io.jmix.securityflowui.view.resourcepolicy;

public enum EntityPolicyAction {

    CREATE("create"),
    READ("read"),
    UPDATE("update"),
    DELETE("delete");

    private String id;

    EntityPolicyAction(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
