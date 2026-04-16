package com.arauta.portfolio.service;

import com.arauta.portfolio.model.LabEntry;
import com.arauta.portfolio.repo.LabRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LabServiceTest {

    @Mock
    private LabRepo repo;

    @InjectMocks
    private LabService service;

    // ──────────────────────────────────────────
    // prepareNew()
    // ──────────────────────────────────────────

    @Test
    void prepareNew_noExistingEntries_sortOrderIsZero() {
        when(repo.findTopByOrderBySortOrderDesc()).thenReturn(Optional.empty());

        LabEntry entry = service.prepareNew("Tool A", "desc", null, null);

        assertThat(entry.getSortOrder()).isEqualTo(0);
        assertThat(entry.getName()).isEqualTo("Tool A");
        assertThat(entry.getDescription()).isEqualTo("desc");
    }

    @Test
    void prepareNew_existingEntries_sortOrderIsLastPlusOne() {
        LabEntry last = new LabEntry("Existing");
        last.setSortOrder(2);
        when(repo.findTopByOrderBySortOrderDesc()).thenReturn(Optional.of(last));

        LabEntry entry = service.prepareNew("New Tool", null, null, null);

        assertThat(entry.getSortOrder()).isEqualTo(3);
    }

    @Test
    void prepareNew_fieldsAreSetCorrectly() {
        when(repo.findTopByOrderBySortOrderDesc()).thenReturn(Optional.empty());

        LabEntry entry = service.prepareNew("ToolX", "some desc", "http://link", "http://img");

        assertThat(entry.getName()).isEqualTo("ToolX");
        assertThat(entry.getDescription()).isEqualTo("some desc");
        assertThat(entry.getLinkUrl()).isEqualTo("http://link");
        assertThat(entry.getImageUrl()).isEqualTo("http://img");
    }

    // ──────────────────────────────────────────
    // saveWithTags()
    // ──────────────────────────────────────────

    @Test
    void saveWithTags_fiveTags_allSaved() {
        LabEntry entry = new LabEntry("Tool");
        when(repo.save(any(LabEntry.class))).thenAnswer(i -> i.getArgument(0));

        service.saveWithTags(entry, List.of("a", "b", "c", "d", "e"));

        assertThat(entry.getTags()).hasSize(5);
    }

    @Test
    void saveWithTags_sixTags_onlyFiveSaved() {
        LabEntry entry = new LabEntry("Tool");
        when(repo.save(any(LabEntry.class))).thenAnswer(i -> i.getArgument(0));

        service.saveWithTags(entry, List.of("a", "b", "c", "d", "e", "f"));

        assertThat(entry.getTags()).hasSize(5);
    }

    @Test
    void saveWithTags_nullList_savedWithNoTags() {
        LabEntry entry = new LabEntry("Tool");
        when(repo.save(any(LabEntry.class))).thenAnswer(i -> i.getArgument(0));

        service.saveWithTags(entry, null);

        assertThat(entry.getTags()).isEmpty();
        verify(repo).save(entry);
    }

    @Test
    void saveWithTags_replacesExistingTags() {
        LabEntry entry = new LabEntry("Tool");
        entry.getTags().add(new com.arauta.portfolio.model.LabTag(entry, "old", 0));
        when(repo.save(any(LabEntry.class))).thenAnswer(i -> i.getArgument(0));

        service.saveWithTags(entry, List.of("new"));

        assertThat(entry.getTags()).hasSize(1);
        assertThat(entry.getTags().get(0).getTagValue()).isEqualTo("new");
    }

    // ──────────────────────────────────────────
    // getById()
    // ──────────────────────────────────────────

    @Test
    void getById_existingId_returnsEntry() {
        LabEntry entry = new LabEntry("Tool");
        when(repo.findById(1L)).thenReturn(Optional.of(entry));

        LabEntry result = service.getById(1L);

        assertThat(result).isSameAs(entry);
    }

    @Test
    void getById_nonExistentId_throwsIllegalArgument() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    // ──────────────────────────────────────────
    // getAll() / deleteById()
    // ──────────────────────────────────────────

    @Test
    void getAll_delegatesToRepository() {
        List<LabEntry> entries = List.of(new LabEntry("Tool"));
        when(repo.findAllByOrderBySortOrderAsc()).thenReturn(entries);

        assertThat(service.getAll()).isSameAs(entries);
    }

    @Test
    void deleteById_callsRepository() {
        service.deleteById(7L);
        verify(repo).deleteById(7L);
    }
}
