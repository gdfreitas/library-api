package com.library.app.commontests.category;

import com.library.app.category.model.Category;
import jdk.nashorn.internal.ir.annotations.Ignore;

/**
 * @author gabriel.freitas
 */
@Ignore
public class CategoryForTestsRepository {

    public static Category java() {
        return new Category("Java");
    }

}
