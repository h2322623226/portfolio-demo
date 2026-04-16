package com.arauta.portfolio.service;

import com.arauta.portfolio.model.Project;
import com.arauta.portfolio.repo.ProjectRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepo projectRepo;

    @InjectMocks
    private ProjectService projectService;

    // ──────────────────────────────────────────
    // createWithTags()
    // ──────────────────────────────────────────

    @Test
    void createWithTags_validInput_projectIsSaved() {
        when(projectRepo.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        projectService.createWithTags("MyProject", null, null, "desc", List.of("java"));

        verify(projectRepo).save(any(Project.class));
    }

    @Test
    void createWithTags_fiveTags_allFiveTagsSaved() {
        when(projectRepo.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));
        List<String> fiveTags = List.of("a", "b", "c", "d", "e");

        projectService.createWithTags("P", null, null, "desc", fiveTags);

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepo).save(captor.capture());
        assertThat(captor.getValue().getTags()).hasSize(5);
    }

    @Test
    void createWithTags_sixTags_onlyFirstFiveSaved() {
        // 行為是靜默截斷，不拋例外
        when(projectRepo.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));
        List<String> sixTags = List.of("a", "b", "c", "d", "e", "f");

        projectService.createWithTags("P", null, null, "desc", sixTags);

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepo).save(captor.capture());
        assertThat(captor.getValue().getTags()).hasSize(5);
    }

    @Test
    void createWithTags_zeroTags_savedWithNoTags() {
        when(projectRepo.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        projectService.createWithTags("P", null, null, "desc", Collections.emptyList());

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepo).save(captor.capture());
        assertThat(captor.getValue().getTags()).isEmpty();
    }

    @Test
    void createWithTags_nullContent_savedAsEmptyString() {
        when(projectRepo.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        projectService.createWithTags("P", null, null, null, null);

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepo).save(captor.capture());
        assertThat(captor.getValue().getContent()).isEqualTo("");
    }

    @Test
    void createWithTags_blankOrNullTagValues_areSkipped() {
        when(projectRepo.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));
        // Arrays.asList() 允許 null 元素，List.of() 不行
        List<String> tagsWithBlanks = Arrays.asList("  ", "", "valid", null, "   ");

        projectService.createWithTags("P", null, null, "desc", tagsWithBlanks);

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepo).save(captor.capture());
        assertThat(captor.getValue().getTags()).hasSize(1);
        assertThat(captor.getValue().getTags().get(0).getTagValue()).isEqualTo("valid");
    }

    // ──────────────────────────────────────────
    // saveWithTags()
    // ──────────────────────────────────────────

    @Test
    void saveWithTags_replacesExistingTags() {
        Project project = new Project("Old", "content", null);
        // 模擬已有 tags（直接操作 list）
        project.getTags().add(new com.arauta.portfolio.model.ProjectTag(project, "old-tag", 0));
        when(projectRepo.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        projectService.saveWithTags(project, List.of("new-tag"));

        assertThat(project.getTags()).hasSize(1);
        assertThat(project.getTags().get(0).getTagValue()).isEqualTo("new-tag");
    }

    @Test
    void saveWithTags_tagValuesAreTrimmed() {
        Project project = new Project("P", "content", null);
        when(projectRepo.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        projectService.saveWithTags(project, List.of("  java  "));

        assertThat(project.getTags().get(0).getTagValue()).isEqualTo("java");
    }

    // ──────────────────────────────────────────
    // getById()
    // ──────────────────────────────────────────

    @Test
    void getById_existingProject_returnsProject() {
        Project project = new Project("Test", "desc", null);
        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));

        Project result = projectService.getById(1L);

        assertThat(result).isSameAs(project);
    }

    @Test
    void getById_nonExistentId_throwsIllegalArgumentException() {
        when(projectRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    // ──────────────────────────────────────────
    // getAll() / deleteById()
    // ──────────────────────────────────────────

    @Test
    void getAll_delegatesToRepository() {
        List<Project> projects = List.of(new Project("P1", "d", null));
        when(projectRepo.findAllByOrderByIdAsc()).thenReturn(projects);

        List<Project> result = projectService.getAll();

        assertThat(result).isSameAs(projects);
    }

    @Test
    void deleteById_callsRepositoryDeleteById() {
        projectService.deleteById(5L);
        verify(projectRepo).deleteById(5L);
    }
}
