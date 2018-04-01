package com.library.app.category.services.impl;

import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import static com.library.app.commontests.category.CategoryForTestsRepository.categoryWithId;
import static com.library.app.commontests.category.CategoryForTestsRepository.java;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author gabriel.freitas
 */
public class CategoryServicesUTest {

    private CategoryServices categoryServices;
    private CategoryRepository categoryRepository;
    private Validator validator;

    @Before
    public void initTestCase() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        categoryRepository = mock(CategoryRepository.class);

        categoryServices = new CategoryServicesImpl();

        ((CategoryServicesImpl) categoryServices).validator = validator;
        ((CategoryServicesImpl) categoryServices).repository = categoryRepository;
    }

    @Test
    public void addCategoryWithNullName() {
        addCategoryWithInvalidName(null);
    }

    @Test
    public void addCategoryWithShortName() {
        addCategoryWithInvalidName("A");
    }

    @Test
    public void addCategoryWithLongName() {
        addCategoryWithInvalidName("This is a long name that will cause an exception to be thrown");
    }

    @Test(expected = CategoryExistentException.class)
    public void addCategoryWithExistentName() {
        when(categoryRepository.alreadyExists(java())).thenReturn(true);
        categoryServices.add(java());
    }

    @Test
    public void addValidCategory() {
        when(categoryRepository.alreadyExists(java())).thenReturn(false);
        when(categoryRepository.add(java())).thenReturn(categoryWithId(java(), 1L));

        final Category categoryAdded = categoryServices.add(java());
        assertThat(categoryAdded.getId(), is(equalTo(1L)));
    }

    public void addCategoryWithInvalidName(final String name) {
        try {
            categoryServices.add(new Category());
        } catch (final FieldNotValidException e) {
            assertThat(e.getFieldName(), is(equalTo("name")));
        }
    }

}
