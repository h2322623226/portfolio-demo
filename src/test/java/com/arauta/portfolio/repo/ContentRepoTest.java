package com.arauta.portfolio.repo;

import com.arauta.portfolio.model.ContentBlock;
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
class ContentRepoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ContentRepo repo;

    @Test
    void findByPageNameAndKey_existingKey_returnsBlock() {
        em.persist(new ContentBlock("homepage", "rail.title", "Hello"));
        em.flush();

        Optional<ContentBlock> result = repo.findByPageNameAndKey("homepage", "rail.title");

        assertThat(result).isPresent();
        assertThat(result.get().getContent()).isEqualTo("Hello");
    }

    @Test
    void findByPageNameOrderByKeyAsc_returnsOnlyTargetPage() {
        em.persist(new ContentBlock("homepage", "rail.title", "HP Title"));
        em.persist(new ContentBlock("homepage", "rail.subtitle", "HP Sub"));
        em.persist(new ContentBlock("skills", "rail.title", "Skills Title"));
        em.flush();

        List<ContentBlock> result = repo.findByPageNameOrderByKeyAsc("homepage");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ContentBlock::getPageName)
                .containsOnly("homepage");
    }

    @Test
    void findByPageNameAndKeyStartingWith_returnsOnlyMatchingPrefix() {
        em.persist(new ContentBlock("homepage", "rail.title", "Title"));
        em.persist(new ContentBlock("homepage", "rail.subtitle", "Subtitle"));
        em.persist(new ContentBlock("homepage", "section.about", "About"));
        em.flush();

        List<ContentBlock> result = repo.findByPageNameAndKeyStartingWith("homepage", "rail.");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ContentBlock::getKey)
                .allMatch(key -> key.startsWith("rail."));
    }
}
