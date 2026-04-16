package com.arauta.portfolio.repo;

import com.arauta.portfolio.model.Experience;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExperienceRepoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ExperienceRepo repo;

    @Test
    void findAllByOrderBySortOrderAsc_returnsInAscendingOrder() {
        em.persist(new Experience("2022", "Third", null, 2));
        em.persist(new Experience("2020", "First", null, 0));
        em.persist(new Experience("2021", "Second", null, 1));
        em.flush();

        List<Experience> result = repo.findAllByOrderBySortOrderAsc();

        assertThat(result).extracting(Experience::getSortOrder)
                .containsExactly(0, 1, 2);
    }

    @Test
    void findTopByOrderBySortOrderDesc_returnsHighestSortOrder() {
        em.persist(new Experience("2020", "First", null, 0));
        em.persist(new Experience("2021", "Second", null, 1));
        em.persist(new Experience("2022", "Third", null, 5));
        em.flush();

        Optional<Experience> result = repo.findTopByOrderBySortOrderDesc();

        assertThat(result).isPresent();
        assertThat(result.get().getSortOrder()).isEqualTo(5);
    }

    @Test
    void findTopByOrderBySortOrderDesc_emptyTable_returnsEmpty() {
        Optional<Experience> result = repo.findTopByOrderBySortOrderDesc();

        assertThat(result).isEmpty();
    }
}
