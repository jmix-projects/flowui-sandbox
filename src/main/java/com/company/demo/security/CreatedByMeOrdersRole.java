package com.company.demo.security;

import com.company.demo.entity.Order;
import io.jmix.security.model.RowLevelBiPredicate;
import io.jmix.security.model.RowLevelPolicyAction;
import io.jmix.security.role.annotation.JpqlRowLevelPolicy;
import io.jmix.security.role.annotation.PredicateRowLevelPolicy;
import io.jmix.security.role.annotation.RowLevelRole;
import org.springframework.context.ApplicationContext;

@RowLevelRole(
        name = "Can see only Orders created by themselves",
        code = "orders-created-by-themselves")
public interface CreatedByMeOrdersRole {

    @JpqlRowLevelPolicy(
            entityClass = Order.class,
            where = "{E}.createdBy = :current_user_username")
    void order();

    @PredicateRowLevelPolicy(
            entityClass = Order.class,
            actions = {RowLevelPolicyAction.UPDATE})
    default RowLevelBiPredicate<Order, ApplicationContext> customerDetailNotConfidential() {
        return (order, context) -> true;
    }
}
