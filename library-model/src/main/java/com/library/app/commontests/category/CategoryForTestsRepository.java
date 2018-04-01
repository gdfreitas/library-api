package com.library.app.commontests.category;

import com.library.app.category.model.Category;
import jdk.nashorn.internal.ir.annotations.Ignore;

import java.util.Arrays;
import java.util.List;

/**
 * @author gabriel.freitas
 */
@Ignore
public class CategoryForTestsRepository {

    public static Category java() {
        return new Category("Java");
    }

    public static Category cleanCode() {
        return new Category("Clean Code");
    }

    public static Category architecture() {
        return new Category("Architecture");
    }

    public static Category networks() {
        return new Category("Networks");
    }

    public static Category categoryWithId(Category category, Long id) {
        category.setId(id);
        return category;
    }

    public static List<Category> allCategories() {
        return Arrays.asList(java(), cleanCode(), architecture(), networks());
    }

}
