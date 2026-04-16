package com.arauta.portfolio.repo;

import com.arauta.portfolio.model.Section;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SectionRepoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private SectionRepo repo;

    @Test
    void findByPageNameOrderBySortOrderAsc_returnsOnlyTargetPage() {
        em.persist(new Section("homepage", "about", Section.GroupType.SINGLE, 0));
        em.persist(new Section("homepage", "projects", Section.GroupType.TWO_COL, 1));
        em.persist(new Section("skills", "lang", Section.GroupType.SKILL_ROW, 0));
        em.flush();

        List<Section> result = repo.findByPageNameOrderBySortOrderAsc("homepage");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Section::getPageName)
                .containsOnly("homepage");
    }

    @Test
    void findByPageNameOrderBySortOrderAsc_returnsInAscendingOrder() {
        em.persist(new Section("homepage", "c", Section.GroupType.SINGLE, 2));
        em.persist(new Section("homepage", "a", Section.GroupType.SINGLE, 0));
        em.persist(new Section("homepage", "b", Section.GroupType.SINGLE, 1));
        em.flush();

        List<Section> result = repo.findByPageNameOrderBySortOrderAsc("homepage");

        assertThat(result).extracting(Section::getSortOrder)
                .containsExactly(0, 1, 2);
    }

    @Test
    void findFirstByPageNameOrderBySortOrderDesc_returnsHighest() {
        em.persist(new Section("homepage", "a", Section.GroupType.SINGLE, 0));
        em.persist(new Section("homepage", "b", Section.GroupType.SINGLE, 3));
        em.persist(new Section("homepage", "c", Section.GroupType.SINGLE, 1));
        em.flush();

        Section result = repo.findFirstByPageNameOrderBySortOrderDesc("homepage");

        assertThat(result).isNotNull();
        assertThat(result.getSortOrder()).isEqualTo(3);
    }
}
