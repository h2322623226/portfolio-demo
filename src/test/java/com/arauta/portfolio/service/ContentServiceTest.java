package com.arauta.portfolio.service;

import com.arauta.portfolio.model.ContentBlock;
import com.arauta.portfolio.repo.ContentRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @Mock
    private ContentRepo repo;

    @InjectMocks
    private ContentService service;

    // ──────────────────────────────────────────
    // upsert()
    // ──────────────────────────────────────────

    @Test
    void upsert_newBlock_createsAndSaves() {
        when(repo.findByPageNameAndKey("homepage", "hero.title")).thenReturn(Optional.empty());
        when(repo.save(any(ContentBlock.class))).thenAnswer(i -> i.getArgument(0));

        service.upsert("homepage", "hero.title", "Hello World");

        ArgumentCaptor<ContentBlock> captor = ArgumentCaptor.forClass(ContentBlock.class);
        verify(repo).save(captor.capture());
        ContentBlock saved = captor.getValue();
        assertThat(saved.getPageName()).isEqualTo("homepage");
        assertThat(saved.getKey()).isEqualTo("hero.title");
        assertThat(saved.getContent()).isEqualTo("Hello World");
    }

    @Test
    void upsert_existingBlock_updatesContentWithoutDuplicate() {
        ContentBlock existing = new ContentBlock("homepage", "hero.title", "Old Content");
        when(repo.findByPageNameAndKey("homepage", "hero.title")).thenReturn(Optional.of(existing));
        when(repo.save(any(ContentBlock.class))).thenAnswer(i -> i.getArgument(0));

        service.upsert("homepage", "hero.title", "New Content");

        verify(repo, times(1)).save(existing);
        assertThat(existing.getContent()).isEqualTo("New Content");
    }

    // ──────────────────────────────────────────
    // getPageContent()
    // ──────────────────────────────────────────

    @Test
    void getPageContent_returnsMapKeyedByBlockKey() {
        ContentBlock b1 = new ContentBlock("homepage", "hero.title", "Hello");
        ContentBlock b2 = new ContentBlock("homepage", "hero.body", "World");
        when(repo.findByPageNameOrderByKeyAsc("homepage")).thenReturn(List.of(b1, b2));

        Map<String, String> result = service.getPageContent("homepage");

        assertThat(result).containsEntry("hero.title", "Hello")
                          .containsEntry("hero.body", "World");
    }

    @Test
    void getPageContent_emptyPage_returnsEmptyMap() {
        when(repo.findByPageNameOrderByKeyAsc("empty")).thenReturn(List.of());

        assertThat(service.getPageContent("empty")).isEmpty();
    }

    // ──────────────────────────────────────────
    // getDrawerContent()
    // ──────────────────────────────────────────

    @Test
    void getDrawerContent_returnsOnlyRailPrefixedBlocks() {
        ContentBlock rail = new ContentBlock("homepage", "rail.title", "Sidebar");
        ContentBlock other = new ContentBlock("homepage", "hero.title", "Hero");
        // ContentRepo 只返回 rail.* 開頭的（由 service 呼叫 findByPageNameAndKeyStartingWith）
        when(repo.findByPageNameAndKeyStartingWith("homepage", "rail.")).thenReturn(List.of(rail));

        Map<String, String> result = service.getDrawerContent();

        assertThat(result).containsKey("rail.title").doesNotContainKey("hero.title");
    }

    // ──────────────────────────────────────────
    // updatePageContent()
    // ──────────────────────────────────────────

    @Test
    void updatePageContent_callsUpsertForEachEntry() {
        when(repo.findByPageNameAndKey(anyString(), anyString())).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        service.updatePageContent("homepage", Map.of("key1", "val1", "key2", "val2"));

        verify(repo, times(2)).save(any(ContentBlock.class));
    }
}
