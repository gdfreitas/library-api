package com.library.app.user.resource;

import com.library.app.common.resource.AbstractFilterExtractorFromUrl;
import com.library.app.user.model.User.UserType;
import com.library.app.user.model.filter.UserFilter;

import javax.ws.rs.core.UriInfo;

/**
 * @author gabriel.freitas
 */
public class UserFilterExtractorFromUrl extends AbstractFilterExtractorFromUrl {

    public UserFilterExtractorFromUrl(final UriInfo uriInfo) {
        super(uriInfo);
    }

    public UserFilter getFilter() {
        final UserFilter userFilter = new UserFilter();
        userFilter.setPaginationData(extractPaginationData());
        userFilter.setName(getUriInfo().getQueryParameters().getFirst("name"));
        final String userType = getUriInfo().getQueryParameters().getFirst("type");
        if (userType != null) {
            userFilter.setUserType(UserType.valueOf(userType));
        }

        return userFilter;
    }

    @Override
    protected String getDefaultSortField() {
        return "name";
    }

}
