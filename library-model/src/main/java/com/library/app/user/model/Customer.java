package com.library.app.user.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Arrays;
import java.util.List;

/**
 * @author gabriel.freitas
 */
@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {

    public Customer() {
        setUserType(UserType.CUSTOMER);
    }

    @Override
    protected List<Roles> getDefaultRoles() {
        return Arrays.asList(Roles.CUSTOMER);
    }

}
