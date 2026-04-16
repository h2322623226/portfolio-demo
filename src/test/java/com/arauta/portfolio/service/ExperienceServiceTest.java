package com.arauta.portfolio.service;

import com.arauta.portfolio.model.Experience;
import com.arauta.portfolio.repo.ExperienceRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class ExperienceServiceTest {

    @Mock
    private ExperienceRepo repo;

    @InjectMocks
    private ExperienceService service;

    // ──────────────────────────────────────────
    // nextSortOrder()
    // ──────────────────────────────────────────

    @Test
    void nextSortOrder_noExistingEntries_returnsZero() {
        when(repo.findTopByOrderBySortOrderDesc()).thenReturn(Optional.empty());
        assertThat(service.nextSortOrder()).isEqualTo(0);
    }

    @Test
    void nextSortOrder_existingEntries_returnsLastPlusOne() {
        Experience last = new Experience("2023", "Last", "", 4);
        when(repo.findTopByOrderBySortOrderDesc()).thenReturn(Optional.of(last));
        assertThat(service.nextSortOrder()).isEqualTo(5);
    }

    // ──────────────────────────────────────────
    // getAll()
    // ──────────────────────────────────────────

    @Test
    void getAll_delegatesToRepositoryOrderedQuery() {
        List<Experience> list = List.of(
                new Experience("2020", "First", "", 0),
                new Experience("2023", "Second", "", 1));
        when(repo.findAllByOrderBySortOrderAsc()).thenReturn(list);

        List<Experience> result = service.getAll();

        assertThat(result).isSameAs(list);
        verify(repo).findAllByOrderBySortOrderAsc();
    }

    // ──────────────────────────────────────────
    // getById()
    // ──────────────────────────────────────────

    @Test
    void getById_existingId_returnsExperience() {
        Experience exp = new Experience("2022", "Title", "body", 0);
        when(repo.findById(1L)).thenReturn(Optional.of(exp));

        Experience result = service.getById(1L);

        assertThat(result).isSameAs(exp);
    }

    @Test
    void getById_nonExistentId_throwsIllegalArgument() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    // ──────────────────────────────────────────
    // save()
    // ──────────────────────────────────────────

    @Test
    void save_delegatesToRepository() {
        Experience exp = new Experience("2024", "New", "body", 0);
        when(repo.save(exp)).thenReturn(exp);

        Experience result = service.save(exp);

        assertThat(result).isSameAs(exp);
        verify(repo).save(exp);
    }

    // ──────────────────────────────────────────
    // deleteById()
    // ──────────────────────────────────────────

    @Test
    void deleteById_callsRepositoryDelete() {
        service.deleteById(3L);
        verify(repo).deleteById(3L);
    }
}
