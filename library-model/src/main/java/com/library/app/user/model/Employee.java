package com.library.app.user.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Arrays;
import java.util.List;

/**
 * @author gabriel.freitas
 */
@Entity
@DiscriminatorValue("EMPLOYEE")
public class Employee extends User {

    public Employee() {
        setUserType(UserType.EMPLOYEE);
    }

    @Override
    protected List<Roles> getDefaultRoles() {
        return Arrays.asList(Roles.EMPLOYEE);
    }

}
