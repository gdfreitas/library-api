package com.library.app.category.services.impl;

import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.exception.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

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

    @Test
    public void updateCategoryWithNullName() {
        updateCategoryWithInvalidName(null);
    }

    @Test
    public void updateCategoryWithShortName() {
        updateCategoryWithInvalidName("A");
    }

    @Test
    public void updateCategoryWithLongName() {
        updateCategoryWithInvalidName("This is a long name that will cause an exception to be thrown");
    }

    @Test(expected = CategoryExistentException.class)
    public void updateCategoryWithExistentName() {
        when(categoryRepository.alreadyExists(categoryWithId(java(), 1L))).thenReturn(true);

        categoryServices.update(categoryWithId(java(), 1L));
    }

    @Test(expected = CategoryNotFoundException.class)
    public void updateCategoryNotFound() {
        when(categoryRepository.alreadyExists(categoryWithId(java(), 1L))).thenReturn(false);
        when(categoryRepository.existsById( 1L)).thenReturn(false);

        categoryServices.update(categoryWithId(java(), 1L));
    }

    @Test
    public void updateValidCategory() {
        when(categoryRepository.alreadyExists(categoryWithId(java(), 1L))).thenReturn(false);
        when(categoryRepository.existsById( 1L)).thenReturn(true);

        categoryServices.update(categoryWithId(java(), 1L));

        verify(categoryRepository).update(categoryWithId(java(), 1L));
    }

    @Test
    public void findCategoryById() {
        when(categoryRepository.findById(1L)).thenReturn(categoryWithId(java(), 1L));

        Category category = categoryServices.findById(1L);
        assertThat(category, is(notNullValue()));
        assertThat(category.getId(), is(equalTo(1L)));
        assertThat(category.getName(), is(equalTo(java().getName())));
    }

    @Test(expected = CategoryNotFoundException.class)
    public void findCategoryByIdNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(null);

        categoryServices.findById(1L);
    }

    @Test
    public void findAllCategoriesWhenEmpty() {
        when(categoryRepository.findAll("name")).thenReturn(new ArrayList<>());

        List<Category> categories = categoryServices.findAll();
        assertThat(categories.isEmpty(), is(equalTo(true)));
    }

    @Test
    public void findAllNoCategories() {
        when(categoryRepository.findAll("name"))
                .thenReturn(Arrays.asList(categoryWithId(java(), 1L), categoryWithId(networks(), 2L)));

        List<Category> categories = categoryServices.findAll();
        assertThat(categories.size(), is(equalTo(2)));
        assertThat(categories.get(0).getName(), is(equalTo(java().getName())));
        assertThat(categories.get(1).getName(), is(equalTo(networks().getName())));
    }

    private void addCategoryWithInvalidName(final String name) {
        try {
            categoryServices.add(new Category(name));
            fail("should have thrown an error");
        } catch (final FieldNotValidException e) {
            assertThat(e.getFieldName(), is(equalTo("name")));
        }
    }

    private void updateCategoryWithInvalidName(final String name) {
        try {
            categoryServices.update(new Category(name));
            fail("should have thrown an error");
        } catch (final FieldNotValidException e) {
            assertThat(e.getFieldName(), is(equalTo("name")));
        }
    }

}
