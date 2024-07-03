package com.twelve.challengeapp.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CommentResponseDtoTest {

    @Test
    void testCommentResponseDto() {
        // given
        Long id = 1L;
        String content = "Test comment";
        String username = "testuser";
        Long count = 0L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // when
        CommentResponseDto dto = new CommentResponseDto(id, content, username, count, createdAt, updatedAt);

        // then
        assertEquals(id, dto.getId());
        assertEquals(content, dto.getContent());
        assertEquals(username, dto.getUsername());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(updatedAt, dto.getUpdatedAt());
    }
}
