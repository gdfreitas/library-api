package com.library.app.category.repository;

import com.library.app.category.model.Category;
import com.library.app.commontests.utils.TestBaseRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @author gabriel.freitas
 */
public class CategoryRepositoryUTest extends TestBaseRepository {

    private CategoryRepository categoryRepository;

    @Before
    public void createEntityManager() {
        initializeTestDB();
        categoryRepository = new CategoryRepository();
        categoryRepository.em = em;
    }

    @After
    public void setDownTestCase() {
        closeEntityManager();
    }

    @Test
    public void addCategoryAndFindIt() {
        final Long categoryAddedId = dbCommandExecutor.execute(() -> categoryRepository.add(java()).getId());

        assertThat(categoryAddedId, is(notNullValue()));

        final Category category = categoryRepository.findById(categoryAddedId);
        assertThat(category, is(notNullValue()));
        assertThat(category.getName(), is(equalTo(java().getName())));
    }

    @Test
    public void findCategoryByIdNotFound() {
        final Category category = categoryRepository.findById(999L);
        assertThat(category, is(nullValue()));
    }

    @Test
    public void findCategoryByIdWithNullId() {
        final Category category = categoryRepository.findById(null);
        assertThat(category, is(nullValue()));
    }

    @Test
    public void updateCategory() {
        final Long categoryAddedId = dbCommandExecutor.execute(() -> categoryRepository.add(java()).getId());

        final Category categoryAfterAdd = categoryRepository.findById(categoryAddedId);
        assertThat(categoryAfterAdd.getName(), is(equalTo(java().getName())));

        categoryAfterAdd.setName(cleanCode().getName());
        dbCommandExecutor.execute(() -> {
            categoryRepository.update(categoryAfterAdd);
            return null;
        });

        final Category categoryAfterUpdate = categoryRepository.findById(categoryAddedId);
        assertThat(categoryAfterUpdate.getName(), is(equalTo(cleanCode().getName())));
    }

    @Test
    public void findAllCategories() {
        dbCommandExecutor.execute(() -> {
            allCategories().forEach(categoryRepository::add);
            return null;
        });

        List<Category> categories = categoryRepository.findAll("name");
        assertThat(categories.size(), is(equalTo(4)));
        assertThat(categories.get(0).getName(), is(equalTo(architecture().getName())));
        assertThat(categories.get(1).getName(), is(equalTo(cleanCode().getName())));
        assertThat(categories.get(2).getName(), is(equalTo(java().getName())));
        assertThat(categories.get(3).getName(), is(equalTo(networks().getName())));
    }

    @Test
    public void alreadyExistsForAdd() {
        dbCommandExecutor.execute(() -> categoryRepository.add(java()).getId());

        assertThat(categoryRepository.alreadyExists(java()), is(equalTo(true)));
        assertThat(categoryRepository.alreadyExists(cleanCode()), is(equalTo(false)));
    }

    @Test
    public void alreadyExistsCategoryWithId() {
        final Category java = dbCommandExecutor.execute(() -> {
            categoryRepository.add(cleanCode());
            return categoryRepository.add(java());
        });

        assertThat(categoryRepository.alreadyExists(java), is(equalTo(false)));

        java.setName(cleanCode().getName());
        assertThat(categoryRepository.alreadyExists(java), is(equalTo(true)));

        java.setName(networks().getName());
        assertThat(categoryRepository.alreadyExists(java), is(equalTo(false)));
    }

    @Test
    public void existsById() {
        final Long categoryAddedId = dbCommandExecutor.execute(() -> categoryRepository.add(java()).getId());

        assertThat(categoryRepository.existsById(categoryAddedId), is(equalTo(true)));
        assertThat(categoryRepository.existsById(999L), is(equalTo(false)));
    }

}
