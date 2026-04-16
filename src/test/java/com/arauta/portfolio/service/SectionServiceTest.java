package com.arauta.portfolio.service;

import com.arauta.portfolio.model.Section;
import com.arauta.portfolio.repo.SectionRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @Mock
    private SectionRepo repo;

    @InjectMocks
    private SectionService service;

    // ──────────────────────────────────────────
    // getGroupedSections()
    // ──────────────────────────────────────────

    @Test
    void getGroupedSections_returnsSectionsGroupedByKey() {
        Section s1 = new Section("homepage", "about", Section.GroupType.SINGLE, 0);
        Section s2 = new Section("homepage", "about", Section.GroupType.SINGLE, 1);
        Section s3 = new Section("homepage", "projects", Section.GroupType.TWO_COL, 2);
        when(repo.findByPageNameOrderBySortOrderAsc("homepage")).thenReturn(List.of(s1, s2, s3));

        LinkedHashMap<String, List<Section>> result = service.getGroupedSections("homepage");

        assertThat(result).containsKey("about").containsKey("projects");
        assertThat(result.get("about")).hasSize(2);
        assertThat(result.get("projects")).hasSize(1);
    }

    @Test
    void getGroupedSections_emptyPage_returnsEmptyMap() {
        when(repo.findByPageNameOrderBySortOrderAsc("skills")).thenReturn(List.of());

        LinkedHashMap<String, List<Section>> result = service.getGroupedSections("skills");

        assertThat(result).isEmpty();
    }

    @Test
    void getGroupedSections_preservesInsertionOrder() {
        Section s1 = new Section("homepage", "first", Section.GroupType.SINGLE, 0);
        Section s2 = new Section("homepage", "second", Section.GroupType.SINGLE, 1);
        when(repo.findByPageNameOrderBySortOrderAsc("homepage")).thenReturn(List.of(s1, s2));

        LinkedHashMap<String, List<Section>> result = service.getGroupedSections("homepage");

        assertThat(result.keySet()).containsExactly("first", "second");
    }

    // ──────────────────────────────────────────
    // getById()
    // ──────────────────────────────────────────

    @Test
    void getById_existingId_returnsSection() {
        Section section = new Section("homepage", "about", Section.GroupType.SINGLE, 0);
        when(repo.findById(1L)).thenReturn(Optional.of(section));

        Section result = service.getById(1L);

        assertThat(result).isSameAs(section);
    }

    @Test
    void getById_nonExistentId_throwsIllegalArgument() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    // ──────────────────────────────────────────
    // addCard()
    // ──────────────────────────────────────────

    @Test
    void addCard_noExisting_sortOrderIsZero() {
        when(repo.findFirstByPageNameOrderBySortOrderDesc("homepage")).thenReturn(null);
        when(repo.save(any(Section.class))).thenAnswer(i -> i.getArgument(0));

        service.addCard("homepage", "new-group", Section.GroupType.SINGLE);

        ArgumentCaptor<Section> captor = ArgumentCaptor.forClass(Section.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getSortOrder()).isEqualTo(0);
    }

    @Test
    void addCard_existingEntries_sortOrderIsLastPlusOne() {
        Section last = new Section("homepage", "about", Section.GroupType.SINGLE, 3);
        when(repo.findFirstByPageNameOrderBySortOrderDesc("homepage")).thenReturn(last);
        when(repo.save(any(Section.class))).thenAnswer(i -> i.getArgument(0));

        service.addCard("homepage", "new", Section.GroupType.TWO_COL);

        ArgumentCaptor<Section> captor = ArgumentCaptor.forClass(Section.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getSortOrder()).isEqualTo(4);
    }

    // ──────────────────────────────────────────
    // saveWithTags()
    // ──────────────────────────────────────────

    @Test
    void saveWithTags_fiveTags_allSaved() {
        Section section = new Section("homepage", "about", Section.GroupType.SINGLE, 0);
        when(repo.save(any(Section.class))).thenAnswer(i -> i.getArgument(0));

        service.saveWithTags(section, List.of("a", "b", "c", "d", "e"));

        assertThat(section.getTags()).hasSize(5);
    }

    @Test
    void saveWithTags_sixTags_onlyFiveSaved() {
        Section section = new Section("homepage", "about", Section.GroupType.SINGLE, 0);
        when(repo.save(any(Section.class))).thenAnswer(i -> i.getArgument(0));

        service.saveWithTags(section, List.of("a", "b", "c", "d", "e", "f"));

        assertThat(section.getTags()).hasSize(5);
    }

    @Test
    void saveWithTags_replacesExistingTags() {
        Section section = new Section("homepage", "about", Section.GroupType.SINGLE, 0);
        section.getTags().add(new com.arauta.portfolio.model.SectionTag(section, "old", 0));
        when(repo.save(any(Section.class))).thenAnswer(i -> i.getArgument(0));

        service.saveWithTags(section, List.of("new"));

        assertThat(section.getTags()).hasSize(1);
        assertThat(section.getTags().get(0).getTagValue()).isEqualTo("new");
    }

    // ──────────────────────────────────────────
    // deleteById()
    // ──────────────────────────────────────────

    @Test
    void deleteById_callsRepository() {
        service.deleteById(5L);
        verify(repo).deleteById(5L);
    }
}
